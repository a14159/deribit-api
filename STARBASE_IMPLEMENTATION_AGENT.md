# Starbase implementation agent brief

## Mission

Create a new sibling Java/Maven project named `deribit-starbase-api` that implements the
Deribit Starbase protocols needed by `ftxarb`. Integrate it into `ftxarb` with the smallest
possible change outside the existing Deribit implementation packages.

The existing `deribit-api` remains the client for the standard Deribit REST and WebSocket
APIs. `ftxarb` must depend on both artifacts; the Starbase artifact is not a replacement for
`deribit-api`.

Preserve the high-level shape of the current API where it fits:

```text
factory -> API object -> cached channel/stream object -> registered consumers
                    \-> connection/session -> decoder -> channel dispatch
```

Starbase uses TCP sessions for order entry and UDP for market data, so retain this shape
without inheriting WebSocket-specific lifecycle or message-object behavior.

## Execution control

Use [`STARBASE_IMPLEMENTATION_TRACKER.md`](STARBASE_IMPLEMENTATION_TRACKER.md) as the
mutable execution ledger for this specification. At the start of every implementation
turn, read this brief and the tracker, then resume the single recorded active task or the
first dependency-ready task.

Keep this brief stable. Record task status, test-first evidence, blockers, durable
implementation decisions, and the exact next action in the tracker so work can continue
correctly after context compaction or a new Codex session.

## Fixed decisions

Treat the following as requirements, not suggestions:

1. Create a separate repository/project and Maven artifact:
   - repository/directory: `../deribit-starbase-api`
   - group ID: `io.contek.invoker`
   - artifact ID: `invoker-deribit-starbase-api`
   - package root: `io.contek.invoker.deribit.starbase`
2. Do not move, duplicate, or replace standard Deribit requests. In particular,
   [`GetUserTradesByCurrencyAndTime`](src/main/java/io/contek/invoker/deribit/api/rest/user/GetUserTradesByCurrencyAndTime.java)
   remains in `deribit-api`.
3. Do not implement FIX or add a FIX dependency. FIX Drop Copy is completely out of scope.
4. Do not generate Java codecs from the SBE XML.
5. Implement hardcoded, bounds-checked encoders and decoders using fixed offsets and
   absolute `ByteBuffer` access.
6. Do not allocate a message/decoder/field object for each TCP message, UDP packet, or
   market-data event.
7. Keep the decoding and book-maintenance hot paths free of boxing, streams, lambdas that
   capture, reflection, JSON, `BigDecimal`, temporary collections, and per-event strings.
8. Preserve ftxarb's public exchange-neutral interfaces. Strategies must not know whether
   Deribit is using the standard API, legacy multicast, or Starbase.
9. Market-data and execution backends must be independently selectable.
10. Never silently approximate unsupported order semantics. Reject explicitly or use the
    configured standard backend.

## Sources of truth

Before coding, review the current production documentation and pin the exact schema
versions being implemented:

- [Starbase overview](https://docs.deribit.com/starbase/overview)
- [Connectivity and compatibility guide](https://docs.deribit.com/starbase/connectivity-best-practices)
- [Starbase changelog](https://docs.deribit.com/changelogs/starbase)
- [Binary API reference](https://docs.deribit.com/starbase/binary-api-reference)
- [Official SBE XML bundle](https://statics.deribit.com/files/deribit-sbe-xmls.zip)
- [Gateway connectivity](https://docs.deribit.com/starbase/gateway-connectivity)
- [Multicast channels](https://docs.deribit.com/starbase/multicast-channels)
- [Multicast subscription guide](https://docs.deribit.com/starbase/multicast-subscription-guide)
- [Order-book maintenance](https://docs.deribit.com/starbase/order-book-maintenance)
- [Trades](https://docs.deribit.com/starbase/trades)
- [Retransmit gateway](https://docs.deribit.com/starbase/retransmit-gateway)
- [Reference data](https://docs.deribit.com/starbase/reference-data)
- [Placing an order](https://docs.deribit.com/starbase/placing-new-order)
- [Amending an order](https://docs.deribit.com/starbase/amending-order)
- [Cancelling an order](https://docs.deribit.com/starbase/cancelling-order)
- [Mass cancel](https://docs.deribit.com/starbase/mass-cancel)
- [Starbase REST OpenAPI specification](https://docs.deribit.com/specifications/starbase_rest_openapi.json)

At the time this brief was written, the order-entry schema was version 11, semantic
version 1.3, and the market-data schema was version 1. Recheck the changelog and downloaded
XML before implementing any offsets.

Use the XML as a human-reviewed canonical description of field offsets, primitive types,
null values, enum values, block lengths, and template IDs. It may also be stored as a
versioned test/reference resource. It must not be used to generate code or parsed at
runtime.

Record in the new repository:

- schema ID, schema version, and semantic version;
- download date and source URL;
- SHA-256 of each reviewed XML file;
- a table mapping every implemented template ID to its decoder/encoder and test.

If the documentation and XML disagree, prefer the current production XML for the wire
layout, document the discrepancy, and do not guess about behavior.

## Repository boundaries

### `deribit-api`

Do not add Starbase transports or models to this artifact. It continues to provide:

- standard market and user REST;
- standard market and user WebSocket;
- fills/trade history;
- positions, balances, and account summaries;
- derived ticker/statistical data;
- standard execution when configured.

The architecture to imitate, without coupling to WebSocket classes, is represented by:

- [`ApiFactory`](src/main/java/io/contek/invoker/deribit/api/ApiFactory.java)
- [`WebSocketApi`](src/main/java/io/contek/invoker/deribit/api/websocket/WebSocketApi.java)
- [`MarketWebSocketApi`](src/main/java/io/contek/invoker/deribit/api/websocket/market/MarketWebSocketApi.java)
- [`UserWebSocketApi`](src/main/java/io/contek/invoker/deribit/api/websocket/user/UserWebSocketApi.java)
- [`WebSocketChannel`](src/main/java/io/contek/invoker/deribit/api/websocket/WebSocketChannel.java)

Reuse the concepts of a factory, context, API, stable cached channels, a connection-owned
dispatcher, listener registration, authentication, liveness, reconnect, and explicit
close. Do not extend the WebSocket implementations or expose WebSocket types from the
Starbase API.

### `deribit-starbase-api`

This is the reusable low-level and stateful Starbase client. It must not depend on
`ftxarb` or its DTOs. Prefer Java 23 bytecode so it remains compatible with
`deribit-api` and the Java 25 ftxarb application.

It may depend on existing neutral Contek security/utility artifacts when useful, but do
not distort HTTP/WebSocket abstractions to carry raw TCP or UDP traffic.

### `ftxarb`

All protocol selection and DTO adaptation belongs under the existing Deribit
implementation packages. Keep changes to strategy and exchange-neutral packages at zero
unless an existing interface makes correct Starbase semantics impossible.

Relevant integration points:

- [`DeribitStreamsFactory`](../ftxarb/src/main/java/is/fm/crypto/connect/ceximpl/deribit/DeribitStreamsFactory.java)
- [`DeribitAPIAccess`](../ftxarb/src/main/java/is/fm/crypto/connect/ceximpl/deribit/DeribitAPIAccess.java)
- [`DeribitCEXTradeRaw`](../ftxarb/src/main/java/is/fm/crypto/connect/ceximpl/deribit/rest/DeribitCEXTradeRaw.java)
- [legacy multicast implementation](../ftxarb/src/main/java/is/fm/crypto/connect/ceximpl/deribit/streams/multicast/DeribitMulticastStreams.java)

## Target public architecture

Use names close to the following. Small naming changes are acceptable if the resulting
responsibilities remain clear.

```text
StarbaseApiFactory
├── marketData(StarbaseMarketDataContext) -> StarbaseMarketDataApi
├── orderEntry(StarbaseOrderEntryContext, StarbaseCredentials)
│                                      -> StarbaseOrderEntryApi
└── rest(StarbaseRestContext, StarbaseCredentials) -> StarbaseRestApi
```

`StarbaseApiFactory` must use supplied contexts. Do not assume public-internet
connectivity or bake production endpoint addresses into application logic. Contexts must
support test and production endpoint configuration, product/gateway routing, network
interface selection, timeouts, receive/send buffer sizes, and spin/blocking policy.

Prefer the following conceptual correspondence with the existing implementation:

| Existing architecture | Starbase counterpart |
| --- | --- |
| `ApiFactory` | `StarbaseApiFactory` |
| `WebSocketApi` | `StarbaseTcpApi` for order entry |
| `WebSocketSession` | `StarbaseTcpSession` |
| `WebSocketMessageParser` | hardcoded frame decoder and template dispatcher |
| `WebSocketLiveKeeper` | Starbase heartbeat/liveness state machine |
| `MarketWebSocketApi` | `StarbaseMarketDataApi` |
| `UserWebSocketApi` | `StarbaseOrderEntryApi` |
| `WebSocketChannel` | stable local `StarbaseChannel` with registered listeners |

This is a responsibility/API correspondence, not an inheritance requirement.
`StarbaseMarketDataApi` is UDP-based and must not be forced through the TCP base class.

### Channel/stream behavior

API objects cache and return stable channel objects just as `MarketWebSocketApi` and
`UserWebSocketApi` do. Creating a second consumer for the same logical instrument or event
must not create a second network connection or decoder.

Suggested channel surface:

```text
StarbaseMarketDataApi
├── getOrderBookChannel(instrument)
├── getTradesChannel(instrument)
└── getReferenceDataChannel()

StarbaseOrderEntryApi
├── orders()                 // outbound command facade
├── getOrderEventsChannel()
├── getFillsChannel()
└── getSessionEventsChannel()
```

Starbase channels are local dispatch channels, not server subscriptions. Order-entry
events arrive for the authenticated TCP session; no fake subscribe/unsubscribe request
should be sent.

Listener/channel objects may be allocated during configuration. The receive path must
dispatch through stable objects or primitive callbacks. If a reusable flyweight view is
exposed, document clearly that it is valid only for the duration of the callback and must
not be retained.

### Explicit connection lifetime

Do not copy the current WebSocket rule that closes a connection when there are no active
consumers. Starbase cancel-on-disconnect can cancel live orders.

Order-entry connections require explicit lifecycle:

```java
api.start();
api.isAuthenticated();
api.close();
```

An unexpected disconnect must:

1. atomically mark the session unavailable;
2. notify session-state listeners;
3. stop accepting outbound orders;
4. apply the documented reconnect/backoff policy;
5. assume session-scoped orders may have been cancelled;
6. reconcile through the new SBE session and Starbase REST snapshot before reporting the
   connection ready again.

Never reconnect and resume order submission while local order state is silently stale.

## Suggested package structure

```text
io.contek.invoker.deribit.starbase
├── StarbaseApiFactory
├── codec
│   ├── common
│   ├── marketdata
│   └── orderentry
├── common
│   ├── StarbaseCredentials
│   ├── ProductGroup
│   ├── Side
│   ├── Price9
│   └── quantity helpers
├── marketdata
│   ├── StarbaseMarketDataApi
│   ├── StarbaseMarketDataContext
│   ├── feed
│   ├── book
│   └── channel
├── orderentry
│   ├── StarbaseOrderEntryApi
│   ├── StarbaseOrderEntryContext
│   ├── connection
│   ├── command
│   ├── state
│   └── channel
├── reference
│   └── InstrumentRegistry
└── rest
    ├── StarbaseRestApi
    ├── StarbaseRestContext
    └── request/response models
```

Do not create a `fix` or `dropcopy` package.

## Hardcoded codec requirements

### General rules

Implement one final stateless decoder class per message layout. A decoder method accepts a
buffer and the absolute start offset:

```java
public final class BidPutDecoder {
  public static final int TEMPLATE_ID = 20;
  public static final int BLOCK_LENGTH = /* pinned XML value */;
  private static final int INSTRUMENT_ID_OFFSET = /* pinned XML value */;

  public static long instrumentId(ByteBuffer buffer, int messageOffset) {
    return buffer.getLong(messageOffset + INSTRUMENT_ID_OFFSET);
  }
}
```

Requirements:

- all buffers are explicitly `LITTLE_ENDIAN`;
- use absolute `get*`/`put*` operations on a reused direct buffer;
- keep header and body offsets distinct and unambiguous;
- define constants for every offset, encoded length, template ID, enum, flag, and null
  value;
- validate frame/message length and remaining bytes before reading;
- validate schema/message version before dispatch;
- for TCP, advance to `offset + align8(messageLength)` and validate the padding;
- for UDP, advance by the market-data message length defined by the pinned schema;
- do not create decoder instances, slices, duplicate buffers, arrays, strings, enum
  objects, collections, or DTOs per message;
- do not call an allocating numeric conversion on the hot path;
- decode fixed character fields only on reference/control paths;
- expose Decimal72 as mantissa and exponent primitives;
- retain Price9 in its exact integer representation until an API boundary requires a
  conversion;
- write outbound messages into a per-connection reusable direct send buffer;
- serialize concurrent writers before they touch that buffer;
- retain unsent bytes and correctly resume partial TCP writes.

Unknown templates must be counted and logged with rate limiting. A new or unsupported
state-changing book/order template must make the affected session/feed unhealthy; do not
skip it and continue with apparently valid state.

### Required common framing

Implement and test:

- 32-byte TCP header;
- 24-byte UDP packet header;
- 16-byte market-data message header;
- TCP 8-byte frame padding;
- TCP inbound/outbound sequence numbers and `lastProcessedSeqNum`;
- UDP `sequenceNum`, `channelId`, packet type, and `messageCount`;
- transaction start/end flags;
- nanosecond timestamps.

TCP reads are a byte stream. Correctly handle:

- a header split across reads;
- a body split across reads;
- multiple complete messages in one read;
- a complete message followed by a partial message;
- clean EOF and truncated/corrupt frames.

Never assume that one socket read equals one SBE message.

### Required market-data templates

At minimum implement the current schema messages necessary for:

- instrument/reference definition and status;
- bid and ask put;
- bid and ask quantity reduction;
- bid and ask delete;
- trade summary and individual trade;
- snapshot header and trailer;
- end of cycle;
- retransmit request and retransmit reject.

Use the template IDs from the pinned XML. At the time of writing, the important
market-data IDs include:

```text
10   InstrumentDefinition
14   InstrumentInfo
15   InstrumentRef
16   InstrumentStatusUpdate
20   BidPut
21   AskPut
22   BidQtyReduced
23   AskQtyReduced
24   BidDelete
25   AskDelete
30   TradeSummary
31   Trade
100  SnapshotHeader
101  SnapshotTrailer
119  EndOfCycle
200  RetransmitRequest
202  RetransmitReject
```

Confirm this list against the pinned production XML. Add additional reference/status
messages when they are needed to interpret a traded product correctly.

### Required order-entry templates

Implement the session and trading messages required by ftxarb:

- logon/authentication, logon response/reject;
- logout;
- client/server heartbeat and inactivity detection;
- sequence recovery/resend messages required by the current protocol;
- new limit and market order;
- amend order;
- cancel by numeric client order ID;
- cancel by exchange order ID if present in the pinned schema;
- mass cancel scoped to an instrument/product as supported by Starbase;
- every corresponding response and reject;
- immediate fills carried in command responses;
- subsequent fill events;
- order placed/queued/cancelled and other unsolicited lifecycle events needed to maintain
  correct local order state.

Mass quoting is not required for the initial ftxarb integration unless a current ftxarb
caller is identified. Keep the codec/session design extensible to it without adding FIX or
premature abstractions.

Outbound command methods should return a primitive correlation ID or write into a
caller-owned response slot. Do not allocate a `CompletableFuture`, request object, or
callback closure for every command. If a synchronous compatibility facade is needed in
ftxarb, back it with preallocated correlation slots and document its timeout behavior.

## TCP order-entry implementation

Create a connection/session layer analogous in responsibility to the current
`BaseWebSocketApi`, but purpose-built for binary TCP:

```text
StarbaseOrderEntryApi
└── OrderEntryConnection
    ├── reusable receive/send buffers
    ├── frame assembler
    ├── authentication state machine
    ├── heartbeat/liveness state machine
    ├── inbound/outbound sequence state
    ├── correlation table
    ├── message dispatcher
    └── stable channels/listeners
```

One connection represents one API key on one gateway host. Respect Deribit's one
connection-per-key-per-gateway rule. Product groups and A/B gateway sides must be explicit
configuration dimensions.

Provide a router above individual connections:

- resolve instrument name to the Starbase 64-bit instrument ID and product group;
- select an eligible A or B connection;
- never duplicate-send an order to A and B;
- record the originating connection for order lifecycle and cancel-on-disconnect
  handling;
- fail closed when no authenticated, reconciled connection is available.

Listener callbacks execute on the I/O thread by default and must not block. State this in
the public API. If another dispatch policy is offered, make it opt-in and keep queues
bounded and preallocated.

## UDP market-data implementation

Do not modify the legacy decoder in place. Implement an independent Starbase feed stack.

For every configured product group:

1. join both A and B incremental feeds;
2. join both A and B snapshot feeds;
3. track sequence and health independently per feed;
4. merge/de-duplicate equivalent A/B messages by sequence;
5. calculate the next expected sequence as
   `packet.sequenceNum + packet.messageCount`;
6. treat zero-message heartbeat packets according to the protocol;
7. request missing messages over the UDP unicast retransmit gateway;
8. page retransmit requests using the actual returned message count;
9. respect the maximum request count and response MTU;
10. fall back to a fresh multicast snapshot when a gap is no longer recoverable.

Do not mix a standard REST L2 snapshot with a Starbase L3 incremental sequence. Starbase
snapshot/retransmit state is authoritative for the Starbase book.

### L3 book

Maintain an L3 book keyed by 64-bit exchange order ID:

- retain instrument ID, side, Price9, quantity mantissa, and `sortOrderId`;
- use `sortOrderId` for priority; do not infer priority from packet order;
- apply quantity reductions and deletes to the exact order;
- aggregate changed price levels incrementally for the ftxarb L2 adapter;
- publish a coherent update only at an end-of-transaction/end-of-cycle boundary;
- discard or quarantine a book when sequence, snapshot, transaction, or invariant checks
  fail;
- never report a stale/incomplete book as healthy.

Use primitive, pre-sized maps/arrays and object pools. Do not use boxed `Long` keys or
allocate an order node for every update. Capacity growth during initial snapshot loading
must be bounded and observable; pre-size from configuration/reference data where possible.

### Trade stream

`TradeSummary` supplies context for the following `Trade` messages. Retain that context in
reusable per-feed state and emit the existing logical trade events without allocating an
intermediate decoded-message object.

Keep ticker/derived-statistics streams on the standard Deribit WebSocket implementation
for the initial integration.

## Reference data and identifiers

Starbase instrument IDs are signed 64-bit values. The shared ftxarb
[`Instrument`](../ftxarb/src/main/java/is/fm/crypto/connect/api/dto/Instrument.java) stores
an `int`; do not truncate the Starbase ID and do not widen this common DTO solely for this
integration.

Maintain a Starbase `InstrumentRegistry` with:

- name to 64-bit instrument ID;
- instrument ID to metadata/channel;
- product group;
- quantity exponent/unit;
- price tick and minimum quantity;
- trading/status flags.

Standard REST reference data may bootstrap names, but Starbase reference messages are
authoritative for Starbase units and IDs.

## Starbase REST

Implement only the documented Starbase REST utility endpoints:

- instruments;
- open-order snapshot;
- portfolio-wide cancel;
- portfolio lock;
- portfolio unlock.

Keep credentials/authentication separate from the standard Deribit API. REST is for
bootstrap, recovery, and administrative operations, not the live order-state or
latency-sensitive execution path.

Rate-limit and cache the open-order snapshot. Never implement ftxarb's
`getOpenOrders(market)` by issuing a Starbase REST call on every invocation.

## ftxarb integration

### Backend selection

Replace the internal boolean selection with independent enums:

```java
enum DeribitMarketDataBackend {
  WEBSOCKET,
  LEGACY_MULTICAST,
  STARBASE
}

enum DeribitExecutionBackend {
  STANDARD,
  STARBASE
}
```

Keep the old `multicast=true` property as a compatibility alias for
`LEGACY_MULTICAST` if existing deployments use it. Add explicit Starbase configuration;
do not reinterpret the old property as Starbase.

### Stream adapters

Add under a new package such as
`is.fm.crypto.connect.ceximpl.deribit.streams.starbase`:

- `StarbaseOrderBookStream implements OrderBookStream`;
- `StarbaseTradesStream implements TradesStream`;
- the smallest necessary wrapper around the standard ticker stream;
- session/health monitoring adapters.

`DeribitStreamsFactory` should select these implementations while its callers continue to
receive the existing interfaces.

The order-book adapter must incrementally copy/translate changed aggregated levels into
the existing ftxarb `OrderBook`. Do not rebuild the whole book after every Starbase
message. Avoid allocation in the listener path.

### Execution adapter

Add a Starbase-backed implementation of `CEXTradeConnector`, preferably by composition
rather than by adding protocol branches throughout `DeribitCEXTradeRaw`.

Preserve the existing high-level methods where semantics can be represented:

- place limit order;
- place market order;
- edit order;
- cancel by client ID;
- cancel by exchange ID;
- cancel all for an instrument;
- get cached open orders.

Use the standard `deribit-api` implementation for:

- historical fills, including
  `get_user_trades_by_currency_and_time`;
- positions, balances, and account summaries;
- ordinary ticker/order-book REST fallbacks that are not used to merge Starbase L3 state.

### Compatibility issues that require explicit handling

1. **Client order IDs:** ftxarb uses string labels; Starbase uses numeric `int64`
   `clientOrderId`. Implement a collision-free mapping that survives as long as any
   mapped order can remain live. It must support reverse lookup for events.
2. **Amend by exchange ID:** current ftxarb edits by exchange order ID, while Starbase
   amend uses numeric client order ID. Maintain exchange-ID to client-ID/instrument
   mapping inside the Starbase order-state store.
3. **Instrument ID:** resolve the market name to a 64-bit Starbase instrument ID without
   changing the shared ftxarb `Instrument`.
4. **Open orders:** standard Deribit open-order methods and private WebSocket order
   subscriptions do not expose live Starbase orders. Serve the ftxarb method from the
   SBE-maintained local state, recovered from the Starbase REST snapshot when necessary.
5. **Immediate fills:** process fills included in an order response and do not wait for a
   duplicate unsolicited fill that will not arrive.
6. **Session scope:** record the originating session and handle cross-session
   amend/cancel responses without assuming all lifecycle events appear on every
   connection.
7. **Reduce-only:** the schema version reviewed for this brief had no equivalent to
   ftxarb's `reduceOnly` argument. Reconfirm against the pinned schema. If still
   unsupported, fail explicitly or route the entire order through the configured standard
   backend. Never silently drop the flag.
8. **Quantity units:** Starbase uses its native Decimal72 quantity units, not the standard
   contract-count assumptions. Convert using pinned reference data and reject an
   inexact/invalid conversion.

## Failure and health model

Expose health separately for:

- each A/B order-entry connection;
- each A/B incremental feed;
- each A/B snapshot feed;
- each retransmit endpoint;
- each reconstructed instrument book;
- reference-data readiness;
- order-state reconciliation.

An API can be connected but not ready. Outbound trading readiness requires:

- authenticated session;
- valid inbound/outbound sequence state;
- current reference data;
- completed order-state reconciliation;
- an eligible product-group route.

Market-data readiness requires:

- a completed valid snapshot or otherwise documented initial synchronization;
- all subsequent incremental sequences applied;
- no open transaction or unresolved gap.

Provide counters for packets/messages, duplicates, gaps, retransmit requests/rejects,
snapshot resets, corrupt frames, unknown templates, reconnects, order rejects, callback
failures, and buffer/capacity exhaustion.

## Performance contract

The new implementation is on an HFT path.

After warm-up, these operations must allocate zero bytes in normal processing:

- UDP header/message decode and dispatch;
- TCP header/body framing and decode;
- outbound order encoding;
- L3 add/reduce/delete;
- A/B de-duplication;
- sequence-gap detection;
- ftxarb order-book update adaptation.

Allocations required by an existing public return type, logging an exceptional failure, a
REST recovery call, initial channel creation, or initial capacity setup are allowed but
must remain outside the normal packet path.

Do not log per packet/message. Guard debug logging so disabled logging does not allocate
argument arrays or format strings on the hot path.

## Implementation sequence

Work in small, independently verifiable stages.

### Stage 1: project and protocol manifest

- create the sibling Maven project;
- match repository conventions and Java compatibility;
- add README and schema manifest/checksums;
- define contexts, credentials, product groups, versions, constants, and exceptions;
- add no ftxarb dependency.

### Stage 2: hardcoded codecs

- implement common TCP/UDP headers;
- implement required market-data decoders;
- implement required order-entry encoders/decoders;
- add a unit test for every field offset, null value, enum, length, and padding rule;
- add version/template rejection tests.

Do not continue to networking until the byte-level tests pass.

### Stage 3: multicast and recovery

- implement A/B incremental and snapshot receivers;
- implement de-duplication and sequence tracking;
- implement retransmit request/response handling;
- implement snapshot state machine;
- implement L3 reconstruction and aggregated level view;
- replay the official market-data PCAP deterministically.

### Stage 4: TCP session and order entry

- implement stream framing and partial I/O handling;
- implement authentication, heartbeat, liveness, sequence handling, and reconnect;
- implement the required order commands and response correlation;
- implement unsolicited event routing and local order state;
- implement explicit lifecycle and readiness gating;
- test A/B routing without duplicate submission.

### Stage 5: REST utilities

- implement and test the five utility endpoints;
- implement rate-limited open-order recovery;
- reconcile snapshot orders into the local order-state store.

### Stage 6: ftxarb adapters

- add both backend enums/configuration;
- add Starbase stream adapters;
- add Starbase execution adapter;
- leave historical fills and account data on `deribit-api`;
- enable Starbase independently for market data and execution;
- retain WebSocket and legacy multicast rollback paths.

### Stage 7: validation

- compile/test `deribit-starbase-api`;
- compile/test `deribit-api` unchanged;
- compile/test `ftxarb` with both dependencies;
- run PCAP replay and allocation benchmarks;
- run test-environment session/order lifecycle tests;
- compare the aggregated Starbase book with an independently observed standard book for
  sanity, without attempting to merge their sequence domains;
- document deployment, endpoint, credential, and rollback configuration.

## Mandatory tests

At minimum provide:

1. byte-layout golden tests for every implemented encoder/decoder;
2. truncated/corrupt/unsupported-version frame tests;
3. split and coalesced TCP read tests;
4. partial TCP write tests;
5. UDP heartbeat and `messageCount` sequence tests;
6. A/B duplicate, reordering, single-feed-loss, and failover tests;
7. retransmit paging, timeout, reject, and snapshot-fallback tests;
8. snapshot/incremental overlap and transaction-boundary tests;
9. L3 add/reduce/delete and aggregate-L2 invariant tests;
10. immediate-fill versus later-fill order lifecycle tests;
11. disconnect/cancel-on-disconnect/reconciliation tests;
12. client-ID and exchange-ID reverse-mapping tests;
13. Decimal72/Price9 exact conversion and rejection tests;
14. zero-allocation benchmarks after warm-up for decoder, dispatcher, and book update
    paths.

Use official PCAP data where available. Add small hand-built byte fixtures for cases not
present in the PCAP; construct them in test setup, not in production code.

## Acceptance criteria

The work is complete only when all of the following are true:

- `deribit-starbase-api` is a separate buildable artifact;
- no generated codec source or runtime XML parser exists;
- no FIX code or dependency exists;
- every implemented wire template is pinned, documented, bounds-checked, and tested;
- normal TCP/UDP decode and book paths are allocation-free after warm-up;
- Starbase order-entry connection lifetime is explicit and cancel-on-disconnect safe;
- A/B, snapshot, retransmit, and L3 reconstruction are implemented;
- ftxarb strategies and exchange-neutral stream/trading interfaces are unchanged;
- `GetUserTradesByCurrencyAndTime` and other standard account/history operations still use
  `deribit-api`;
- standard WebSocket, legacy multicast, and Starbase backends can be selected for rollback;
- unsupported semantics such as reduce-only are never silently ignored;
- readiness/health prevents trading on stale session, order, reference, or book state;
- both repositories build and their relevant tests pass.

## Out of scope

- FIX and FIX Drop Copy;
- replacing the existing `deribit-api`;
- moving standard history/account endpoints into the Starbase artifact;
- generated SBE codecs;
- public-internet connectivity assumptions;
- changing common ftxarb DTO identifiers from `int` to `long` solely for Starbase;
- silently emulating an unsupported Starbase order feature.
