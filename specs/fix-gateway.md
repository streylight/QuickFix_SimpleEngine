# FIX gateway: DynamoDB persistence and one-order simulation

Status: agent-assisted draft for owner review. Confirmed requirements below
come from the owner's discussion; proposed defaults are not yet decisions.
No business implementation or owner-owned invariant tests are authorized by
this draft. Implementation ownership is agreed separately with the maintainer.

## Confirmed outcome

Keep Java 21, Maven, QuickFIX/J, and standard FIX 4.4. Replace experimental
MemoryDB storage with DynamoDB. Demonstrate one market order for one account,
with a New acknowledgment followed by a simulated full fill.

DynamoDB is in the workflow: session messages and sequence state are persisted,
and the simulator reads the received order from a durable inbox before producing
responses. Application inbox/outbox records survive processing failures and
are retained until processing completes durably. This is a gateway learning
project, not a matching engine or production exchange.

## Proposed first fixture — owner to confirm

- Confirmed account: DEMO.
- Proposed values: instrument TEST, buy quantity 100, synthetic fill price 10.00.
- Client acts as a minimal OMS test driver; engine acts as the exchange gateway.
- One active process per endpoint/session; both endpoints use DynamoDB session
  stores and application inbox/outbox records. Multi-instance failover is later.
- DynamoDB Local for repeatable development tests; a separate opt-in AWS run
  demonstrates real DynamoDB use. Local tests do not demonstrate multi-AZ behavior.
- No automatic deletion/TTL during the first milestone. No cloud provisioning
  until region, credentials/profile, table ownership, and provisioning approach
  are specified. No real customer data or endpoints.

## Required application flow

1. Client records one uniquely identified order in its DynamoDB outbox.
2. Sender reads pending work and sends it through QuickFIX/J. The FIX session
   store persists protocol messages and sequence state for recovery.
3. Acceptor durably records the received application order in its inbox before
   allowing successful application receipt handling to finish.
4. Simulator reads that inbox record. It durably creates an ordered pair of
   response intents (New, then Trade/Filled) and records input completion.
   Retrying this processing must not create another simulated execution.
5. Server sender reads response intents and transmits them in that order.
6. Client durably records received reports and verifies the expected sequence,
   order correlation, quantities, and price by reading persisted records.

The response fixture is deliberately scripted. Business validation, a general
order state machine, and matching remain HAND responsibilities. Agree on fixture
ownership before implementation; gateway plumbing must not become a hidden OMS.

## FIX response contract

For order quantity Q and configured execution price P:

| Message | ExecType | OrdStatus | CumQty | LeavesQty | Other values |
| --- | --- | --- | --- | --- | --- |
| New acknowledgment | 0 (New) | 0 (New) | 0 | Q | AvgPx=0 |
| Full fill | F (Trade) | 2 (Filled) | Q | 0 | LastQty=Q, LastPx=P, AvgPx=P |

Preserve ClOrdID correlation; use one stable exchange OrderID and distinct,
retry-stable ExecIDs. Do not use FIX MsgSeqNum as the business execution ID.
The NewOrderSingle uses OrdType=1 (Market); the synthetic fill price belongs to
the simulator, not a limit-price instruction on the submitted order.

## Persistence and recovery contract

- Separate session-recovery records from application work records. Scope
  protocol keys by endpoint/session, reset generation, and sequence number.
- Preserve numeric sequence ordering for recovery reads. Application ordering
  needs its own explicit per-session ordinal/cursor; administrative FIX messages
  mean application inbox sequences need not be contiguous.
- Persist both next-sender and next-target counters, creation time, and messages;
  implement MessageStore range retrieval, refresh, and intentional reset.
- Use consistency appropriate to reading newly committed state. Never use a
  timestamp or unordered scan as the authoritative processing order.
- Claim work without deleting it. Record progress/completion durably; retries
  reuse the same logical identities. A claim must be recoverable after a crash.
- Atomically commit input completion with its generated response intents where
  possible. Specify every boundary that spans multiple QuickFIX/J callbacks.
- DynamoDB commits and TCP delivery cannot share a transaction. Define recovery
  for persisted-but-unsent, sent-but-not-marked, and ambiguous-send outcomes.
  Do not equate sendToTarget success with counterparty business acceptance.
- Resolve the outbox-to-FIX-sequence association durably. Never blindly resend
  an uncertain outbox item as a new application order with a new identity.
- Duplicate FIX delivery must not create a duplicate simulated execution.
  Session replay and repeated business requests need separate identity rules.
- A storage failure must not be swallowed or reported as successful processing.
  Verify QuickFIX/J 2.3.0 callback/error behavior before selecting failure handling;
  throwing an arbitrary exception is not proof that receipt progress is withheld.
- Initial process ownership is single-active. Claims of multi-AZ process failover,
  zero loss, or exactly-once execution require separately specified mechanisms
  and failure tests; DynamoDB replication alone does not establish them.

## Acceptance scenarios to turn into technical tests

These are proposed AI-owned gateway tests, not the owner's lifecycle invariants.

- One order produces exactly one New and one full-fill logical response, both
  correlated correctly and observable in DynamoDB.
- Prove the simulator consumes stored input: pause processing after receipt,
  restart it, and complete the order from DynamoDB without resubmitting it.
- Restart each endpoint and recover the same session state and stored messages.
- Request a stored message range and verify ordered resend behavior.
- Crash after claiming input and before completion; work remains recoverable.
- Crash after committing responses but before sending; responses remain pending.
- Exercise the send/mark crash window; no second logical order or fill results.
- Replay the same inbound order/report; no duplicate business effect results.
- Fail writes and exercise ambiguous retry outcomes; no silent loss or memory fallback.
- Verify distinct sessions and reset generations cannot read each other's data.

## Decisions before implementation

1. Owner confirms/edits the remaining fixture defaults. Recommendation: AI
   implements the narrowly scripted responder as test scaffolding while the
   owner defines expected behavior. This ownership recommendation remains open;
   general lifecycle and matching implementations remain HAND-owned.
2. Owner reviews this draft as the learning project's spec-first step.
3. Technical design resolves conditional claims, ordering cursors, atomic
   processing writes, QuickFIX/J callback boundaries, and outbox/send recovery
   before the storage implementation is described as reliable.

## Exclusions

No order book, real liquidity, partial-fill scenarios, cancel/replace, positions,
P&L, clearing/settlement, full raw-message audit archive, or EKS deployment in
this milestone. Future integration with other lifecycle projects remains TBD.
