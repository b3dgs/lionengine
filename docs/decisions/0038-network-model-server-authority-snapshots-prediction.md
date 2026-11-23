# ADR-0038 — Network model: server authority, state snapshots, client prediction, modules `network` and `multiplayer`

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner (need: "server and clients, the server can be the first client itself; join from IP with a client name; list clients; internal chat; a strong architecture that avoids the classic out-of-sync issue; fewer than 16 players; low latency if possible but simple to use; compatible with RTS, platform and shoot'em up games; UDP for the main events, sync with real values, let each computer decide but the server stays the reference"); design decided by the agent under the owner's explicit delegation of 2026-11-23 ("I let you decide everything in this part"); reversible by a superseding ADR
- Related requirements: `REQ-NET-001` to `REQ-NET-017`, `REQ-MP-002`, `REQ-MP-004` to `REQ-MP-015`, `REQ-STD-011`, `REQ-DET-001`, `REQ-DET-002`, `REQ-IN-003`, `REQ-RT-001`, `REQ-RT-006`, `REQ-WORLD-001`, `REQ-ECS-012`
- Open questions resolved: `OQ-165` (with [ADR-0039](0039-network-protocol-transport-sessions-and-defaults.md))
- Supersedes / superseded by: supersedes decision 1 of [ADR-0022](0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md) as to the content and dependencies of `multiplayer` (it stays optional); replaces the deferral of the network model in decision 1 of [ADR-0024](0024-determinism-performance-budgets-and-desktop-support.md) and in section 5 of [ADR-0031](0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md) (their determinism rules stay); completes [ADR-0030](0030-ecs-details-ids-flush-scheduling-collision-and-traits.md), [ADR-0035](0035-input-actions-devices-and-step-edges.md) and the tables of [ADR-0037](0037-package-tree-per-module-callbacks-and-canonical-names.md). Those ADRs are not edited.

## Context

ADR-0022 made `multiplayer` an optional module and ADR-0024 kept a later network mode possible (deterministic simulation, whole-state read and replace, inputs through actions) while deferring the model itself to `OQ-165`. The owner now asks to decide it, for fewer than 16 players and for very different games: a platformer or a shoot'em up (each player steers one avatar in real time) and an RTS (many units, orders rather than continuous steering).

"Out of sync" is the classic failure of networked games: two machines end up with different game states and nothing brings them back together. Its usual causes are:

1. machines simulate independently and compare nothing, so any difference (floating point, a random draw, an event order) grows forever (deterministic lockstep with an undetected divergence);
2. a one-off event (spawn, death) is lost or applied twice;
3. packets arrive late, duplicated or out of order and are applied as they come;
4. two builds with different content or code join the same game;
5. the client simulates with an input that differs from the one the server used;
6. logic depends on wall-clock time or a machine-local random source.

The `master` branch had a small UDP client and server (`com.b3dgs.lionengine.network`: connect, name, client list, ping, data messages) and a `Networkable` feature, with no reliability layer and no state replication. Version 11 does not port it.

## Decision

### 1. Topology

Client–server (star). One **server** is the authority. A **host** runs the server and one ordinary local client in the same process, so no dedicated machine is needed; a **dedicated server** runs the server alone on the headless platform. Clients never talk to each other. Peer-to-peer and host migration are not provided.

### 2. Authority

The server owns the state of every **replicated object**. It alone creates and removes replicated objects and decides outcomes (hits, damage, pickups, scores, victory). A client sends only **intentions**: per-step input frames and game commands (section 6); the server validates and applies them. A client never sends object state.

### 3. Replication by state snapshots ("sync with real values")

- Every `snapshotInterval` ticks (default 3, that is 20 snapshots per second at 60 Hz) the server sends each client a **snapshot**: the absolute values of the replicated traits of every relevant replicated object at that tick.
- A snapshot is **delta-compressed** against the last snapshot this client has acknowledged (its *baseline*): an object or trait whose encoded bytes did not change is not sent. Without a usable baseline the snapshot is complete.
- The **existence** of objects is part of the snapshot: an object present in the snapshot exists on the client, an absent one is removed. There are no separate spawn or despawn events that could be lost.
- A newer snapshot always supersedes an older one; an older or duplicated snapshot is ignored.

Consequence: whatever was lost, duplicated, reordered or mispredicted, the first snapshot that arrives brings every proxy back to the server's values. A lasting divergence is impossible by construction; this is the answer to causes 1 to 3.

**Deterministic lockstep is rejected** as the model: it needs bit-exact determinism, which is not promised between the JVM and Android (`REQ-DET-002`); one divergence ruins the game silently; every player waits for the slowest; joining a running game still needs a full state transfer. Determinism (`REQ-DET-001`) stays useful because it makes prediction corrections rarer, but nothing relies on it.

### 4. Ticks

Server and clients simulate at the same fixed step (ADR-0017, default 60 Hz). Ticks are counted by the session as an `int` from 0 at session start (about 414 days at 60 Hz); snapshots, input frames and commands carry tick numbers. Everything time-dependent in the simulation counts ticks or step seconds, never wall-clock time (cause 6).

### 5. What a client shows: proxies and predicted objects

- **Proxy** (default for every replicated object on a client): its replicated state comes only from snapshots. It is shown `interpolationDelay` behind the latest server tick received (default two snapshot intervals, 0.1 s at the defaults), interpolated between the two snapshots around that time by its trait codecs; with no newer snapshot the last value is held (no extrapolation). Proxies carry the `NetProxy` trait and every simulation system excludes it, so a client never simulates a proxy (`REQ-STD-011`).
- **Predicted object** (opt-in per object type, only for objects owned by the local player, typically the avatar): the client simulates it immediately with its own systems and its own input frames, ahead of the server. For each tick the client keeps the input frame it sent and the encoded replicated state of its predicted objects (history of 64 ticks). When a snapshot for tick T arrives, the server's encoded state of each predicted object is compared byte for byte with the history at T:
  - equal: the prediction was right, nothing happens (the usual case);
  - different: the client writes the server state, then re-runs its world for ticks T+1 to now with the recorded frames (*replay*), during which the client world reports `isReplaying()` so that sounds and effects are not repeated;
  - T older than the history: the server state is taken without replay (*snap*).
  Corrections and snaps are counted. Prediction gives the local player an immediate response ("let each computer decide") while the server stays the reference.
- RTS-style games need no prediction: orders are commands (section 6) and every unit is a proxy.

### 6. Inputs: frames and commands

- **Input frame**: for one tick and one player, the action states of `input-api` (ADR-0035): for each digital action the bits `down`, `pressed`, `released`; for each analog action a value quantized to 16 bits. The client simulates with exactly the quantized frame it sends (cause 5). Frames go unreliably every tick, each packet repeating every frame not yet acknowledged (up to 32), so a single lost packet loses nothing.
- The server buffers frames per player (64 ticks) and applies the frame of tick T at tick T. A missing frame repeats the held `down` and analog states without edges; when a late frame arrives after its tick, its edges are carried into the next applied frame, so a press is never lost, at most delayed (the guarantee of `REQ-IN-003` extended to the network).
- On the server, the input of each player is an `InputState` (the type a local game already reads) driven by the received frames; on a client, the local player's `InputState` is driven by its own frames, including during replay. Game systems read inputs the same way in local and network play.
- **Commands** are game-defined reliable messages (`network` game messages, ADR-0039), for example RTS orders ("units 3, 5, 8: move to x, y"), build requests or a pause request. The server validates and applies them on the tick they are received.

### 7. Tick alignment

A client runs ahead of the server so that its frame for tick T arrives just before the server simulates T. With each snapshot the server reports the client's **input lead**: how many ticks before its use the last frame arrived. The client aims at a lead of 2 ticks (configurable from 0 to 10): it runs two world ticks in one update when the lead falls below 1, none when it exceeds the target by more than 3, at most one adjustment every 10 ticks; if its tick is more than 60 ticks away from where it should be, it jumps there at once and clears its prediction history. No change to the game loop is needed.

### 8. Schema and content check

Both programs register, in the same order: replicated object types (registry ids `namespace:name`, ADR-0032, with a factory per side), replicated trait types with a **trait codec** (write, read, optional interpolate), the action layout of input frames, and the game message types (ADR-0039). A 32-bit **content hash** of these registrations is checked when a client joins; a mismatch is refused (cause 4). Codecs write `double` values exactly by default; quantizing is an explicit codec choice of the game. Reflection and Java serialization are never used (ADR-0020).

### 9. Identity and ownership

The server gives each replicated object a **network id**, an `int` from 1, never reused in a session; clients map network ids to their local ECS ids (ADR-0030). Each replicated object has an **owner** player id (`0` for the server). When a player leaves, the game decides what happens to its objects (listener of ADR-0039).

### 10. Client world rules

The replicated world of a client mirrors the server: client-only presentation objects (particles, effects) live in another world (several worlds coexist, `REQ-WORLD-001`) or outside the ECS. On a client only `multiplayer` creates and removes replicated objects or removes their replicated traits; such a change requested by game code fails fast with `IllegalStateException` at the flush; adding or removing a client-only trait on a replicated object is allowed. `multiplayer` applies the structural changes of a snapshot with an explicit world flush between two world updates (completes decision 2 of ADR-0030).

### 11. Optional relevance

The server may filter, per player and per object, which objects that player receives (a relevance callback; default: everything). An object that becomes irrelevant disappears from that client and reappears, from the next snapshot, when relevant again.

### 12. Modules

- **`network`** (new, root package `com.b3dgs.lionengine.network`): transport contract and implementations, protocol, connections, reliable messages, sessions, players, chat, game messages, server info and LAN discovery (details: ADR-0039). Depends on `core` only; usable without objects (a card or turn-based game).
- **`multiplayer`** (root `com.b3dgs.lionengine.multiplayer`): replication of an ECS world over `network`: schema, snapshots and deltas, proxies, interpolation, input frames, tick alignment, prediction and replay, standard trait codecs, host helper. Depends on `network`, `ecs`, `traits` (standard codecs), `input-api` and `core`.
- Neither depends on `runtime`, a platform, `render-api` or `camera`; no module depends on either (`vision.md` principle 7). Both are shared code for desktop and Android.

### 13. Driving and threads

Everything runs on the runtime thread (ADR-0018); no thread is started (`REQ-RT-001`): sockets are non-blocking and polled. Once per loop step the scene calls the server or client world's `update(stepSeconds)` instead of `world.update`: it receives, applies snapshots, inputs and commands, runs the world for one tick on the server (zero to two ticks on a client, section 7), captures and sends. Without a world (a lobby scene), the scene calls the session's own `update` instead; exactly one of them is called per step. The session object lives in the scene-flow context (ADR-0036) so that the lobby and the level share it. Snapshots carry a world epoch so that, after a level change, a client never applies a snapshot of the previous world.

### 14. Host

The host's own player is an ordinary client connected to the in-process server through the loopback transport, with its own client world. Every player runs the same code path and the host has no hidden advantage; the cost is a second, mostly proxy, world in the host process.

### 15. Session constraints

A networked session runs at time scale 1.0 (ADR-0017 time scale is not synchronized). Pausing the server's loop stalls the session and clients time out; a game pause in network play is a game state decided by the server (a command), not a loop pause. Android background therefore stalls a hosted session.

### 16. Canonical names (completes ADR-0037)

| Concept | Type name | Module |
|---|---|---|
| Server and client sessions, configurations | `NetworkServer`, `NetworkClient`, `ServerConfig`, `ClientConfig` | `network` |
| Transport, UDP, loopback, simulated conditions, time source | `Transport`, `UdpTransport`, `LoopbackNetwork`, `ConditionedTransport`, `NetworkConditions`, `NetworkTime` | `network` |
| Players and connection events | `PlayerInfo`, `PlayerList`, `PlayerListener`, `ConnectionListener`, `ConnectionState`, `DisconnectReason`, `RejectReason` | `network` |
| Chat, game messages, validation callbacks | `ChatMessage`, `ChatListener`, `ChatFilter`, `MessageType<T>`, `MessageCodec<T>`, `MessageListener<T>`, `Delivery`, `Authenticator`, `PropertyValidator` | `network` |
| Wire encoding, statistics, discovery, protection stage | `NetWriter`, `NetReader`, `ConnectionStats`, `ServerInfo`, `Discovery`, `PacketProtection` | `network` |
| Replication schema, codecs, factories, relevance | `ReplicationSchema`, `TraitCodec<T>`, `ReplicatedTypeFactory`, `RelevanceFilter`, `StandardCodecs` | `multiplayer` |
| Server and client worlds, host | `ServerWorld`, `ClientWorld`, `Host` | `multiplayer` |
| Replication traits, input frame | `Replicated`, `NetProxy`, `InputFrame` | `multiplayer` |

## Options considered

### Model

- A (chosen): server-authoritative state snapshots with delta compression, client prediction with replay for owned objects, interpolation for the rest (the model of most action games since the late 1990s). Divergence cannot last; works for every genre; cost: bandwidth grows with the number of changing objects, remote objects are shown slightly in the past.
- B: deterministic lockstep (classic RTS). Very low bandwidth, but needs bit-exact determinism across JVM and Android, stalls on the slowest player, and a divergence is fatal. Rejected as the base; could come later as a separate mode for a game that proves its determinism.
- C: event replication only ("unit spawned", "unit moved by 3 m"). Simple at first, but a lost or doubled event diverges forever (causes 1 and 2).
- D: each client authoritative for its own avatar, the server relaying. Lowest latency, but conflicts between clients and trivial cheating; the server is no longer the reference.

### Prediction

- A (chosen): opt-in per type, owned objects only, byte comparison with a tick history, replay of the client world.
- B: no prediction. Simplest, but a platformer feels a full round trip of input lag.
- C: predict every object. Many corrections and visual jumps for objects the player does not control.

### Modules

- A (chosen): `network` without ECS plus `multiplayer` over ECS. Non-object games can use sessions and chat; each module has one responsibility.
- B: one `multiplayer` module. Forces `ecs` on every networked game.
- C: put the transport in `platform-api`. Not a platform concern: UDP sockets exist in the JDK on desktop and Android.

## Consequences

- Positive consequences: no lasting desynchronization by construction; one model for platformers, shoot'em ups and RTS; immediate response for the local player; the host plays without a dedicated server; everything is testable headless over the loopback transport with simulated latency and loss; no new dependency.
- Costs / constraints: bandwidth proportional to changing replicated state (fine below 16 players; a large RTS uses relevance filtering); remote objects are shown about 0.1 s in the past; predicted code must be written so that a replay gives the same result as the first run (no effects without checking `isReplaying()`); the host simulates two worlds; a hosted game stalls when the host's loop pauses; no lag compensation, host migration, predicted spawning or reconnection in this version (later candidates in [`../features/multiplayer.md`](../features/multiplayer.md)).
- Impact on modules and APIs: new module `network`; `multiplayer` redefined; `traits` standard systems accept extra excluded trait types (`REQ-STD-011`); `input-api` `InputState` driven by frames (recording of ADR-0035); `ecs` explicit flush between updates used by `multiplayer`. `modules.md`, `overview.md`, `contracts.md`, `glossary.md`, `roadmap.md`, `testing.md`, `android-compatibility.md`, `vision.md`, `engine-features.md`, `how-the-engine-works.md` updated.
- Impact on tests and documentation (including Android/DEX compatibility): `REQ-NET-*`, `REQ-MP-004` to `REQ-MP-015` and `REQ-STD-011` added, `REQ-MP-002` and `REQ-DET-002` updated; feature pages [`../features/network.md`](../features/network.md) and [`../features/multiplayer.md`](../features/multiplayer.md); a convergence test suite under simulated loss, duplication, reordering and latency; the Android checks of the JDK socket APIs are listed in `android-compatibility.md`.

## Validation

Need and constraints stated by the project owner on 2026-11-23; model and defaults decided by the agent under the owner's explicit delegation of the same day; reversible by a superseding ADR.
