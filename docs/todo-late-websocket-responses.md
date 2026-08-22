# TODO: robust handling of late WebSocket responses

## Context

Deribit JSON-RPC responses contain only the request ID and response payload; they do not repeat the request method. `WebSocketMessageParser` therefore keeps an `id -> response type` map so each response can be deserialized into the correct Java class.

That map is bounded. When enough requests are simultaneously unresolved, its oldest entry is evicted. If Deribit later returns that response, the parser no longer knows its Java type. This can happen during a temporary exchange-side backlog even when the request succeeds.

The minimal mitigation currently implemented does the following:

- Every unknown response ID and its full JSON response are logged at WARN level.
- The first 19 unknown response IDs are converted to one shared sentinel message. Typed channels ignore the sentinel and the WebSocket remains connected.
- The 20th unknown response ID is logged, then throws and forces the existing WebSocket reconnect path. The counter is then reset.
- The order-edit channel advances its bootstrap state only for the expected `WebSocketTestResponse`, so the sentinel cannot alter channel state.
- The normal correlated-response path does not allocate additional objects or perform additional synchronization.

This prevents an isolated late response from disconnecting every consumer, but it deliberately does not recover the response or its result.

## Recommended complete design

### 1. Never evict an active request merely because the correlation map is full

Keep active request metadata until one of these explicit terminal events occurs:

- its response arrives;
- its correlation deadline expires;
- the connection carrying the request is closed;
- the send fails before the request reaches the socket.

If the active map reaches its configured limit, apply backpressure or reject/coalesce new requests before sending them. Discarding metadata after a request is already on the wire guarantees that a valid late response cannot be interpreted.

For order editing, prefer at most one in-flight edit per order. Retain only the newest desired replacement while an edit is outstanding, then submit that replacement after receiving or timing out the current response. This bounds outstanding work and avoids applying stale intermediate prices after an exchange backlog clears.

### 2. Store compact request metadata

Replace the bare response class with a compact request descriptor containing at least:

- response class;
- request method or operation kind;
- request ID;
- send timestamp;
- connection generation;
- the minimum reconciliation key needed by mutation requests, such as order ID or client order ID.

Use primitive-keyed, pre-sized storage. Avoid callbacks, futures, lambdas, boxed request IDs, and per-request diagnostic strings on latency-sensitive paths.

### 3. Move timed-out requests to a bounded tombstone table

When an active request reaches its correlation deadline, move its compact descriptor to a separate recently-timed-out table instead of immediately forgetting its response type. Retain tombstones for a configured grace period longer than the largest exchange delay the application intends to tolerate.

On response:

1. Remove the descriptor from the active table.
2. If absent, remove it from the tombstone table.
3. If found in either table, deserialize using the stored response class.
4. Mark tombstone matches as late and publish latency/error metrics without completing an already-timed-out caller a second time.

The tombstone table may be a fixed-size primitive ring or map. Expiration should be incremental and bounded so a response cannot trigger an unbounded scan.

### 4. Represent truly orphaned responses explicitly

If an ID exists in neither table, deserialize only the common JSON-RPC envelope into an `OrphanWebSocketResponse`. Do not guess the original response class from payload fields such as `result.order`.

An API-level orphan-response observer should receive:

- request ID;
- `error` code/message when present;
- `usIn`, `usOut`, and `usDiff`;
- a bounded or lazily rendered raw payload for diagnostics.

Typed channels should ignore the orphan envelope. A single orphan must not close the WebSocket.

### 5. Preserve operational handling for late order errors and successes

Late responses can be semantically important even after the original caller stopped waiting:

- Rate-limit errors must still activate backoff.
- Authentication/session errors may require immediate reconnect rather than threshold counting.
- A late successful place/edit/cancel means remote state may differ from the caller's assumed state.
- A late error may mean a retry already changed the order through another request.

Order mutations that resolve through a tombstone or arrive as true orphans should schedule bounded order reconciliation. Reconciliation should compare the order stream and/or an open-orders snapshot with local state; it should not blindly replay the late operation.

### 6. Make connection generation part of correlation

Associate every request with the WebSocket connection generation on which it was sent. On disconnect:

- move or clear active descriptors for that generation;
- fail any waiting callers exactly once;
- mark affected order mutations for reconciliation;
- prevent responses or bookkeeping from an older generation from completing work on the new connection.

Reset unknown-response threshold counters after a successful reconnect. Consider using a time window or consecutive-error policy rather than a lifetime cumulative count.

### 7. Add bounded observability

Record allocation-conscious counters and histograms for:

- active request count and high-water mark;
- correlation timeouts;
- late responses recovered through tombstones;
- truly orphaned responses;
- forced reconnects caused by orphan thresholds;
- Deribit `usDiff` for normal and late responses;
- coalesced or rejected outbound edits;
- reconciliation requests and mismatches.

Logs should be rate-limited. Avoid rendering full JSON payloads unless the relevant log level is enabled and the event is sampled or operationally significant.

## Suggested implementation sequence

- [ ] Introduce a compact primitive-keyed `RequestMetadata` store without changing channel behavior.
- [ ] Add explicit response deadlines and a bounded tombstone table.
- [ ] Deserialize tombstone matches with their original response class and expose late-response metrics.
- [ ] Add an explicit generic orphan envelope and API-level observer.
- [ ] Preserve rate-limit, authentication, and session-error handling for late/orphan responses.
- [ ] Coalesce order edits so each order has at most one request in flight.
- [ ] Add connection-generation tracking and reconcile mutations affected by disconnects.
- [ ] Replace the fixed count mitigation after production evidence validates the complete path.

## Acceptance criteria

- An isolated response delayed beyond the active correlation deadline does not disconnect the WebSocket.
- A late response with retained metadata is deserialized into its original type.
- A late successful order mutation schedules reconciliation and is never blindly retried from the response handler.
- Rate-limit and authentication errors retain their existing operational effects when late.
- Outstanding request metadata remains bounded without evicting already-sent active requests.
- Normal request registration and response correlation add no avoidable allocation, logging, blocking I/O, or unbounded work.
- Sustained orphan responses still trigger a controlled reconnect and an observable alert.
