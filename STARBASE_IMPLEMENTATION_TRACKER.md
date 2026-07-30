# Starbase implementation execution tracker

This is the mutable execution ledger for
[`STARBASE_IMPLEMENTATION_AGENT.md`](STARBASE_IMPLEMENTATION_AGENT.md). The implementation
brief is the technical contract; this file records what has been proved, what is currently
being changed, and exactly where the next agent should resume.

The tracker is not optional for the Starbase implementation. It is required as a project
workflow even though it is not required by Maven or Java. The task is too broad and
stateful to rely on conversation context alone.

Codex does not automatically discover this filename. The initial implementation prompt
must reference both Starbase Markdown files explicitly. A concise `AGENTS.md` has been
pre-created in the new `deribit-starbase-api` repository; `FND-02` must preserve and
verify it while scaffolding the project. It tells Codex to read and maintain these files
at their sibling-repository paths. Keep that `AGENTS.md` short; do not paste either large
document into it.

The canonical mutable tracker remains this file in `deribit-api` during implementation.
Do not create two independently updated tracker copies. The finalized brief and completed
tracker may be copied into the new repository as archival project documentation during
`VAL-07`, with relative links corrected at that time.

Use approximately this bootstrap in `deribit-starbase-api/AGENTS.md`:

```md
# Starbase repository guidance

- Before Starbase implementation work, read
  `../deribit-api/STARBASE_IMPLEMENTATION_AGENT.md` and
  `../deribit-api/STARBASE_IMPLEMENTATION_TRACKER.md` completely.
- Treat the first file as the stable specification and the second as the canonical
  mutable execution state.
- Resume the single `IN_PROGRESS` task, or select the first dependency-ready `TODO`.
- For each behavior, observe the relevant failing test before implementation, then run
  focused and regression tests and record concise evidence in the tracker.
- Keep the tracker current before every handoff. Do not implement FIX or generated SBE
  codecs.
```

## Agent operating directive

At the beginning of every implementation turn:

1. Read `STARBASE_IMPLEMENTATION_AGENT.md` completely.
2. Read this tracker completely.
3. Inspect `git status --short` in every repository that may be changed. Preserve
   unrelated/user changes.
4. If `Current checkpoint` names an `IN_PROGRESS` task, resume it.
5. Otherwise select the first `TODO` task in listed order whose dependencies are `DONE`.
6. Change that task to `IN_PROGRESS` and fill in the checkpoint before editing production
   code.
7. Follow the test-first loop below until the task satisfies its completion proof.
8. Mark it `DONE`, record concise evidence, and proceed to the next dependency-ready task.
9. Continue autonomously while an unblocked task remains. Do not stop merely because one
   phase, test class, or compilation succeeds.

Before ending a turn or when a context boundary may be near, update this file with:

- current task and state;
- tests added and their current result;
- files materially changed;
- exact last verification command and result;
- exact next action/command;
- blockers or newly discovered work.

Do not rely on a chat summary as the only handoff.

Suggested initial prompt when starting the implementation:

> Read `STARBASE_IMPLEMENTATION_AGENT.md` and
> `STARBASE_IMPLEMENTATION_TRACKER.md` completely. Implement the project autonomously,
> one dependency-ready tracker task at a time. For every task, write and observe the
> relevant failing test first, implement the smallest correct change, run regression and
> allocation checks appropriate to that task, record evidence in the tracker, and
> continue. Keep the tracker current before every handoff or context compaction. Do not
> implement FIX or generated SBE codecs.

## Status rules

Use only these states:

- `TODO`: not started and dependencies may or may not be ready.
- `IN_PROGRESS`: actively being implemented. There must be at most one.
- `BLOCKED`: cannot progress without user input, credentials, connectivity, or an
  unresolved upstream decision. Record the blocker.
- `DONE`: completion proof is satisfied and recorded.
- `N/A`: confirmed unnecessary without reducing required functionality. Record why.

Rules:

- A compile-only result is not enough to mark a behavioral task `DONE`.
- A test written after the implementation does not satisfy the test-first requirement
  unless the implementation is deliberately disabled/reverted long enough to observe the
  intended failure.
- Do not mark a hot-path task `DONE` without the relevant allocation check.
- Do not leave a task `IN_PROGRESS` when switching to another task. Mark it `BLOCKED` or
  split it first.
- If a task is too large for one focused iteration, split it into suffixed tasks such as
  `MDT-04a` and `MDT-04b`, add explicit dependencies, and update the task table before
  continuing.
- If new work is discovered, add it to `Discovered work`; promote it into the ordered task
  list before implementation.
- Do not change a fixed architectural decision from the implementation brief without
  explicit user approval.

## Test-first task loop

Apply this loop to every functional task:

1. **Define:** restate the task's observable behavior and completion proof.
2. **Red:** add the smallest deterministic test that describes the next missing behavior.
3. **Observe:** run it and confirm that it fails for the expected reason, not because the
   test or build is broken.
4. **Green:** implement the smallest production change that makes the test pass.
5. **Regress:** run the focused test, its package/module tests, and affected prior tests.
6. **Harden:** add boundary, corrupt-input, state-transition, or error-path tests relevant
   to the behavior.
7. **Measure:** for a hot path, run an allocation/performance check after warm-up.
8. **Review:** check bounds, protocol version, sequence/state invariants, lifecycle,
   concurrency, and absence of silent fallbacks.
9. **Record:** update task status, checkpoint, and completion evidence.

For network behavior, build deterministic tests against byte fixtures, fake clocks,
scripted channels, or loopback peers before using a live Starbase environment. A live
environment test supplements deterministic tests; it does not replace them.

## Definition of a task-sized change

A task should normally produce one independently reviewable capability, such as one
header codec, one message family, one state transition, or one adapter behavior. It should
not combine transport, protocol, state store, and ftxarb integration in one change.

Refactoring required to make a test possible belongs to the same task only when it is
small and behavior-preserving. Larger refactoring gets its own prerequisite task and
tests.

Do not make commits unless the user requests them. Record working-tree state and
verification regardless of whether commits are used.

## Current checkpoint

Update this block before and after every material implementation step.

```text
Overall state: NOT_STARTED
Active task: none
Task objective: none
Test being written/run: none
Expected/observed RED result: none
Last completed task: none
Files materially changed: STARBASE_IMPLEMENTATION_AGENT.md,
  STARBASE_IMPLEMENTATION_TRACKER.md, ../deribit-starbase-api/AGENTS.md
Last verification command: git -C ../deribit-starbase-api status --short --branch
Last verification result: empty Git repository on main; AGENTS.md is the only worktree file
Next exact action: Begin FND-01 by revalidating current production docs and schemas.
Known blocker: none
Repository state notes: deribit-starbase-api is initialized locally on main with no
  commits, remote, Maven scaffold, or source files; root AGENTS.md is present.
```

## Ordered task list

Select tasks in listed order unless dependencies require otherwise. Keep the `Evidence`
cell short; detailed proof belongs in `Completed-task evidence`.

### Foundation

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| FND-01 | TODO | Revalidate current Starbase docs, changelog, XMLs, OpenAPI, and rollout scope | — | Sources, versions, discrepancies, and supported feature subset recorded | — |
| FND-02 | TODO | Scaffold sibling Maven repository/artifact with Java 23 and baseline tests; verify the existing root `AGENTS.md` bootstrap | FND-01 | Clean build, simple test pass, and durable instruction bootstrap present | `AGENTS.md` pre-created; Maven/test scaffold pending |
| FND-03 | TODO | Add protocol/schema manifest, source URLs, versions, and SHA-256 values | FND-02 | Manifest test verifies pinned constants/resources | — |
| FND-04 | TODO | Build deterministic byte-fixture and buffer assertion test utilities | FND-03 | Fixture tests prove endian, offset, truncation, and padding helpers | — |
| FND-05 | TODO | Define contexts, credentials, product groups, gateway sides, clocks, and exceptions | FND-02, FND-03 | Construction/validation tests cover valid and invalid configuration | — |
| FND-06 | TODO | Add factory and API/channel lifecycle skeletons without live networking | FND-05 | Cache identity, listener registration, explicit start/close tests pass | — |

### Common wire codecs

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| COD-01 | TODO | Add bounds, unsigned, little-endian, and `align8` primitives | FND-04 | Golden/boundary tests pass with no hot-path allocation | — |
| COD-02 | TODO | Hardcode TCP header encoder/decoder | COD-01 | All fields, lengths, flags, sequences, padding, and truncation tested | — |
| COD-03 | TODO | Hardcode UDP packet-header decoder/encoder portions needed by retransmit | COD-01 | All fields, bit flags, heartbeat values, and truncation tested | — |
| COD-04 | TODO | Hardcode market-data message-header decoder | COD-01 | Length, template, version, transaction flags, and timestamp tested | — |
| COD-05 | TODO | Implement exact Price9 and Decimal72 primitive helpers | COD-01, FND-03 | Exact round-trip, overflow, null, exponent, and rejection tests pass | — |
| COD-06 | TODO | Implement bounds/version/template validation and dispatch tables | COD-02, COD-03, COD-04 | Unknown, corrupt, and unsupported state-changing messages fail safely | — |

### Market-data message codecs

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| MDC-01 | TODO | Hardcode instrument/reference/status decoders | COD-04, COD-05, COD-06 | Every implemented XML field has golden and truncation tests | — |
| MDC-02 | TODO | Hardcode bid/ask put decoders | COD-04, COD-05, COD-06 | Both sides and all identifiers/price/quantity/priority fields tested | — |
| MDC-03 | TODO | Hardcode bid/ask quantity-reduced and delete decoders | MDC-02 | Both sides, invalid lengths, and null/unknown order cases tested | — |
| MDC-04 | TODO | Hardcode trade-summary and trade decoders | COD-04, COD-05, COD-06 | Summary context, individual fills, flags, and counts tested | — |
| MDC-05 | TODO | Hardcode snapshot header/trailer and end-of-cycle decoders | COD-04, COD-06 | Snapshot anchors, cycle boundaries, and invalid ordering tested | — |
| MDC-06 | TODO | Hardcode retransmit request encoder and reject decoder | COD-03, COD-04, COD-06 | Counts, MTU-related response handling fields, and rejects tested | — |
| MDC-07 | TODO | Decode official PCAP packets through the hardcoded dispatcher | MDC-01–MDC-06 | Expected template/field trace matches a checked-in golden summary | — |

### Market-data transport and recovery

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| MDT-01 | TODO | Implement configured UDP receiver, interface selection, joins, and reusable buffers | FND-05, MDC-07 | Scripted/loopback receive and lifecycle tests pass | — |
| MDT-02 | TODO | Implement per-feed sequence and heartbeat tracking | MDT-01 | Normal, duplicate, gap, wrap/invalid, and zero-count heartbeat tests pass | — |
| MDT-03 | TODO | Implement A/B arbitration and de-duplication | MDT-02 | Reordering, duplicates, one-side loss, recovery, and earliest-copy tests pass | — |
| MDT-04 | TODO | Implement retransmit client, paging, timeout, retry, and reject handling | MDT-02, MDC-06 | Scripted retransmit scenarios and unrecoverable-gap result pass | — |
| MDT-05 | TODO | Implement snapshot/incremental synchronization state machine | MDT-03, MDT-04, MDC-05 | Overlap, anchor, restart, gap, and fresh-snapshot fallback tests pass | — |
| MDT-06 | TODO | Add feed health/readiness and bounded counters/diagnostics | MDT-01–MDT-05 | State transitions and counter tests pass without per-packet logging/allocation | — |

### Reference registry and L3 book

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| BOK-01 | TODO | Implement long-ID instrument registry and exact unit metadata | MDC-01 | Name/ID/product/unit/status lookup and update tests pass | — |
| BOK-02 | TODO | Implement pre-sized primitive L3 order storage and bid/ask put | MDC-02, BOK-01 | Insert/update/duplicate/capacity/invariant tests pass | — |
| BOK-03 | TODO | Implement quantity reduction and deletion | BOK-02, MDC-03 | Partial/full reduction, delete, missing-order, and side checks pass | — |
| BOK-04 | TODO | Implement Price9 level aggregation and priority metadata | BOK-02, BOK-03 | Level totals and `sortOrderId` invariants pass across mutations | — |
| BOK-05 | TODO | Implement transaction/end-of-cycle publication boundaries | BOK-04, MDC-05 | Consumers see coherent state only at valid boundaries | — |
| BOK-06 | TODO | Apply snapshots and recovery atomically to books | BOK-05, MDT-05 | Initial sync, replacement, buffered incrementals, and failed sync tested | — |
| BOK-07 | TODO | Add book invariant checks and zero-allocation benchmark | BOK-06 | Replay invariants pass and normal add/reduce/delete allocate zero after warm-up | — |

### Market-data API and channels

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| MDA-01 | TODO | Implement cached reference-data and per-instrument channel routing | FND-06, BOK-01, MDT-06 | Stable channel identity and primitive dispatch tests pass | — |
| MDA-02 | TODO | Implement order-book channel over reconstructed L3/aggregated levels | MDA-01, BOK-07 | Readiness, update, invalidation, and listener-lifetime tests pass | — |
| MDA-03 | TODO | Implement trade-summary context and trades channel | MDA-01, MDC-04 | Multi-trade summary, flags, ordering, and callback tests pass | — |
| MDA-04 | TODO | Complete PCAP-to-channel end-to-end replay | MDA-02, MDA-03, MDC-07 | Deterministic book/trade golden outputs and health state match | — |

### Order-entry message codecs

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| OEC-01 | TODO | Hardcode logon/logout/heartbeat/session-recovery message codecs | COD-02, COD-05, COD-06 | Every field, secret handling boundary, length, padding, and reject tested | — |
| OEC-02 | TODO | Hardcode new limit/market order encoders and responses/rejects | OEC-01, BOK-01 | Golden request/response fixtures, flags, and exact units pass | — |
| OEC-03 | TODO | Hardcode amend-order encoder and responses/rejects | OEC-02 | Client ID, instrument, price, amount, flags, and rejects tested | — |
| OEC-04 | TODO | Hardcode cancel variants and mass-cancel codecs | OEC-02 | Client/exchange ID, instrument/product scope, responses, and rejects tested | — |
| OEC-05 | TODO | Hardcode fills and unsolicited order-lifecycle decoders | OEC-02–OEC-04 | Immediate/later fill and placed/cancelled event fixtures pass | — |
| OEC-06 | TODO | Complete order-entry template dispatch and unsupported-version handling | OEC-01–OEC-05 | All required templates route correctly; unknown state changes fail closed | — |

### TCP session and connection

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| OET-01 | TODO | Implement reusable TCP frame assembler | OEC-06 | Split header/body, coalesced frames, trailing partial, EOF, corrupt length pass | — |
| OET-02 | TODO | Implement serialized reusable-buffer writer with partial-write resumption | OEC-06 | Concurrent caller, backpressure, partial write, and padding tests pass | — |
| OET-03 | TODO | Implement explicit connection lifecycle and event-loop ownership | OET-01, OET-02, FND-06 | Start/close/idempotence/failure tests pass; idle consumers do not auto-close | — |
| OET-04 | TODO | Implement authentication state machine | OET-03, OEC-01 | Success, reject, timeout, duplicate response, and secret-safety tests pass | — |
| OET-05 | TODO | Implement heartbeat and inactivity detection | OET-04 | Fake-clock normal, delayed, missing, and disconnect transitions pass | — |
| OET-06 | TODO | Implement inbound/outbound sequence and resend handling | OET-04, OEC-01 | Normal, gap, duplicate, reset, resend, and invalid acknowledgment pass | — |
| OET-07 | TODO | Implement reconnect/backoff and readiness gating | OET-05, OET-06 | Disconnect becomes unavailable; reconnect remains unready until reconciled | — |

### Order behavior and local state

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| ORD-01 | TODO | Implement bounded/preallocated correlation table | OET-03 | Correlate, timeout, reuse, exhaustion, late response, and allocation tests pass | — |
| ORD-02 | TODO | Implement consolidated local order-state store across SBE sessions | OEC-05, ORD-01 | Legal lifecycle, duplicate, stale, and cross-session transitions pass | — |
| ORD-03 | TODO | Implement allocation-conscious order command facade | OEC-02–OEC-04, OET-07, ORD-01 | New/amend/cancel/mass-cancel encode/send/correlate tests pass | — |
| ORD-04 | TODO | Handle immediate response fills and later unsolicited fills exactly once | ORD-02, ORD-03 | Full/partial/multiple/duplicate fill lifecycle tests pass | — |
| ORD-05 | TODO | Implement reversible string-to-int64 client-order-ID mapping | ORD-02 | Collision, restart/lifetime, reverse lookup, exhaustion, and stale ID tests pass | — |
| ORD-06 | TODO | Implement product-group and A/B order router without duplicate submission | OET-07, ORD-03, ORD-05, BOK-01 | Route/failover/unavailable/origin-session tests pass | — |

### Starbase REST and reconciliation

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| RST-01 | TODO | Implement configured REST transport and current Starbase authentication | FND-05 | Auth headers, redaction, error parsing, timeout, and base URL tests pass | — |
| RST-02 | TODO | Implement instruments endpoint | RST-01 | Golden response and registry bootstrap/update tests pass | — |
| RST-03 | TODO | Implement open-order snapshot endpoint | RST-01 | Golden response, portfolio scope, unknown fields, and errors tested | — |
| RST-04 | TODO | Implement cancel-all, portfolio lock, and unlock endpoints | RST-01 | Request/response/error tests pass for all three | — |
| RST-05 | TODO | Implement rate-limited/cached open-order recovery | RST-03 | Fake-clock cache, single-flight, rate limit, failure retention, and refresh pass | — |
| ORD-07 | TODO | Reconcile all SBE session state with REST snapshot after reconnect | ORD-02, OET-07, RST-05 | Missing/extra/matching orders and readiness transition tests pass | — |

### ftxarb integration

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| FTX-01 | TODO | Add artifact dependency and independent market-data/execution backend enums | MDA-04, ORD-07 | Configuration parsing/default/legacy alias tests and compile pass | — |
| FTX-02 | TODO | Add Starbase access/lifecycle holder without disturbing standard APIs | FTX-01 | Start/close/readiness and coexistence tests pass | — |
| FTX-03 | TODO | Add allocation-conscious `OrderBookStream` adapter | FTX-02, MDA-02 | Incremental changed-level, readiness, invalidation, and allocation tests pass | — |
| FTX-04 | TODO | Add `TradesStream` adapter and keep ticker on standard WebSocket | FTX-02, MDA-03 | Trade mapping tests pass and ticker selection remains standard | — |
| FTX-05 | TODO | Add Starbase `CEXTradeConnector` placement path | FTX-02, ORD-03, ORD-05, ORD-06 | Limit/market/post-only/unit/error mapping tests pass | — |
| FTX-06 | TODO | Add amend/cancel/cancel-all execution paths | FTX-05, ORD-02 | Exchange/client ID mapping and all response/reject paths tested | — |
| FTX-07 | TODO | Serve Starbase open orders locally while retaining standard fills/account APIs | FTX-05, ORD-07 | Open-order filtering and linked standard trade-history calls tested | — |
| FTX-08 | TODO | Add backend health, readiness, fallback, and rollback behavior | FTX-03–FTX-07 | No stale trading/book state; all backend combinations and rollback tested | — |

### Final validation and documentation

| ID | Status | Task | Depends on | Completion proof | Evidence |
| --- | --- | --- | --- | --- | --- |
| VAL-01 | TODO | Run complete new-artifact unit/regression suite | FTX-08 | Clean full Maven test with command/result recorded | — |
| VAL-02 | TODO | Run PCAP/retransmit/snapshot end-to-end validation | VAL-01 | Deterministic golden replay passes repeatedly | — |
| VAL-03 | TODO | Run TCP scripted/loopback lifecycle validation | VAL-01 | Auth/order/fill/gap/reconnect/reconciliation scenario passes | — |
| VAL-04 | TODO | Run allocation/performance verification | VAL-02, VAL-03 | Required hot paths report zero allocation after warm-up | — |
| VAL-05 | TODO | Run accessible Starbase test-environment smoke tests | VAL-01–VAL-04 | Results recorded, or explicit external blocker remains without false claim | — |
| VAL-06 | TODO | Build/test `deribit-api`, `deribit-starbase-api`, and `ftxarb` together | VAL-01–VAL-05 | Clean commands/results recorded; unrelated existing failures identified | — |
| VAL-07 | TODO | Complete README, configuration, operations, recovery, and rollback docs | VAL-06 | Fresh-reader checklist succeeds and acceptance criteria are audited | — |

## Completed-task evidence

Append one concise row whenever a task becomes `DONE`. Do not paste raw build logs.

| Task | RED proof | Passing verification | Key files | Notes |
| --- | --- | --- | --- | --- |
| — | — | — | — | — |

## Blockers

| Task | Blocker | Checks already performed | Required input/change | Since |
| --- | --- | --- | --- | --- |
| — | — | — | — | — |

When one task is blocked, mark it `BLOCKED` and continue with the next dependency-ready
task if doing so is safe. Do not mark the overall implementation complete while a required
task remains blocked.

## Discovered work

Record newly discovered work here before changing scope or production code.

| Proposed ID | Description | Why required | Dependencies | Disposition |
| --- | --- | --- | --- | --- |
| — | — | — | — | — |

Promote accepted items into the ordered task list. Reject or defer items explicitly; do
not leave important work only in prose.

## Durable implementation decisions

Record only decisions not already fixed by the implementation brief. Keep entries concise
and link the affected task. A decision here may clarify the brief but may not override it.

| ID | Task | Decision | Reason | Consequence |
| --- | --- | --- | --- | --- |
| — | — | — | — | — |

## Verification command ledger

Keep the most recent known-good commands here so a new agent can reproduce state without
rediscovering repository layout. Replace placeholders as repositories are created.

```text
deribit-api:
  not yet recorded

deribit-starbase-api focused test:
  not yet recorded

deribit-starbase-api full test:
  not yet recorded

ftxarb focused test:
  not yet recorded

ftxarb full build/test:
  not yet recorded

allocation benchmark:
  not yet recorded
```

## Handoff quality rules

A useful checkpoint says what to do next at command/file/test level. Bad: “continue TCP.”
Good: “In `TcpFrameAssemblerTest`, add the coalesced-frame case, run
`mvn -Dtest=TcpFrameAssemblerTest test`, confirm it fails because only the first frame is
emitted, then implement the decode loop.”

Keep this file compact:

- summarize failures rather than pasting logs;
- record stable commands and paths;
- remove obsolete checkpoint prose when replacing it;
- retain completed evidence, decisions, and meaningful blockers;
- do not duplicate the implementation brief or protocol documentation here.

## Overall completion

Set `Overall state: COMPLETE` only when every required task is `DONE` or explicitly
approved as `N/A`, the acceptance criteria in `STARBASE_IMPLEMENTATION_AGENT.md` have been
audited, and no required blocker remains.

If live private connectivity is unavailable, record `VAL-05` as `BLOCKED` and report that
the implementation is locally verified but not production/test-environment validated.
Never describe it as fully production-ready without that evidence.
