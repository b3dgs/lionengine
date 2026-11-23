# Feature: multiplayer replication, prediction and interpolation

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 5, see `roadmap.md` (needs `network`, `ecs`, `traits`, `input-api`)
- Flow: B of [`change-process.md`](../change-process.md) (module `multiplayer` redefined); ADRs written and accepted
- Affected modules: `multiplayer`, `traits` (`REQ-STD-011`), `test-support`, `examples`
- Requirements: `REQ-MP-002`, `REQ-MP-004` to `REQ-MP-015`, `REQ-STD-011`, `REQ-DET-001`, `REQ-DET-002`
- Applicable ADRs: [`ADR-0017`](../decisions/0017-fixed-step-game-loop.md), [`ADR-0020`](../decisions/0020-no-reflection-factory-registry-and-capability-lookup.md), [`ADR-0024`](../decisions/0024-determinism-performance-budgets-and-desktop-support.md), [`ADR-0030`](../decisions/0030-ecs-details-ids-flush-scheduling-collision-and-traits.md), [`ADR-0035`](../decisions/0035-input-actions-devices-and-step-edges.md), [`ADR-0036`](../decisions/0036-scene-flow-ids-triggers-conditions-transitions.md), [`ADR-0038`](../decisions/0038-network-model-server-authority-snapshots-prediction.md), [`ADR-0039`](../decisions/0039-network-protocol-transport-sessions-and-defaults.md)
- Open questions involved: none

## Objective

Play the same game on several machines with the server as the single reference: the server simulates, clients receive the real values of the objects many times per second, the local player's own objects respond immediately, the other objects move smoothly, and no machine can stay out of sync. One model serves platformers, shoot'em ups (real-time input) and RTS (orders on many units).

## Out of scope

Transport, joining, players, chat and game messages (`network`, [`network.md`](network.md)); deterministic lockstep; lag compensation (rewinding hit boxes to what a shooter saw); predicted creation of objects on a client (a predicted bullet); visual smoothing of prediction corrections; host migration; reconnection to the same slot; several local players on one online client; recording of a session for replays. These are later candidates, each with its own ADR when a game needs it.

## Expected behavior

### Principle (`REQ-MP-004`, `REQ-MP-007`)

```mermaid
sequenceDiagram
  participant C as Client (ahead of the server)
  participant S as Server (the reference)
  C->>C: tick 53: sample input, predict own avatar
  C->>S: input frames 50..53 (unacknowledged ones repeated)
  S->>S: tick 51: apply each player's frame 51, run the world
  S-->>C: snapshot of tick 51 (changes since the client's baseline)
  C->>C: proxies: shown at tick 45 (0.1 s behind), interpolated between snapshots 45 and 48
  C->>C: own avatar: compare with its prediction of tick 51; if different, replay 52 and 53
```

- Only the server creates and removes replicated objects and decides outcomes. Clients send **input frames** every tick and **commands** (game messages of `network`, reliable) when the player orders something; the server validates and applies them.
- **Convergence:** whatever the loss, duplication, reordering or delay, as soon as a client receives a snapshot of tick T, the replicated traits of its proxies hold the server's values of tick T exactly (standard codecs), before interpolation is applied for presentation.

### Schema (`REQ-MP-005`)

`ReplicationSchema` is built at startup, identically on both sides, then immutable:

- replicated object types: registry id `namespace:name` and a `ReplicatedTypeFactory` per side, which adds the traits that side needs (for example sprites on the client, AI state on the server); the replicated traits are added automatically if the factory did not;
- replicated trait types: the ECS trait type and a `TraitCodec<T>` with `write(T, NetWriter)`, `read(NetReader, T)` and optional `interpolate(T from, T to, double ratio, T out)`; at most 64 replicated trait types;
- the predicted object types (subset of the object types);
- the input action layout: the ordered digital and analog actions of the bindings (at most 64 digital and 16 analog).

`hash()` gives a 32-bit FNV-1a hash of these registrations (ids, kinds, order), passed as the extra content hash of `ClientConfig` and `ServerConfig`; `network` combines it with its message types into the content hash checked at join. Codecs write `double` values exactly unless the game chooses to quantize.

### Server world (`REQ-MP-004`, `REQ-MP-006`, `REQ-MP-012`)

`ServerWorld` wraps a `World` and a `NetworkServer`. Per loop step, `update(stepSeconds)`:

1. updates the session (receives frames, commands, chat; listeners run);
2. sets each player's `InputState` from its frame of the current tick;
3. runs `world.update(stepSeconds)` (systems, then flush);
4. if `tick % snapshotInterval == 0`, captures and sends one snapshot per client;
5. advances the tick.

- Marking: `replicate(objectId, typeId, ownerPlayerId)` adds the `Replicated` trait (network id, type, owner); network ids are `int` from 1, never reused in the session; owner `0` is the server.
- Snapshot interval: 3 ticks by default (20 Hz at 60 Hz), configurable in `[1, 60]`.
- A snapshot holds: world epoch, tick, last frame tick applied for this client, the client's input lead in ticks, and for each relevant replicated object: network id, type, owner and its replicated traits.
- **Delta:** the server keeps the encoded traits of the last 64 snapshots; for each client, the baseline is its last acknowledged snapshot; an object or trait whose bytes equal the baseline is skipped; an object of the baseline that is absent is sent as removed. A baseline older than the kept history (or none) gives a complete snapshot.
- Size: at most 64 fragments by default (configurable up to 255); a larger snapshot is not sent, the error is reported to a listener once per occurrence, and the game raises the limit or filters relevance.
- `RelevanceFilter` `(playerId, objectId) → boolean`, default every object is relevant.

### Client world (`REQ-MP-008`, `REQ-MP-010`, `REQ-MP-011`)

`ClientWorld` wraps a `World` and a `NetworkClient`. Per loop step, `update(stepSeconds)`:

1. updates the session (receives snapshots, messages, chat);
2. applies the newest complete snapshot not yet applied: creates and removes replicated objects (queued, then an explicit flush, ADR-0030), writes their replicated traits, reconciles predicted objects;
3. runs 1 world tick (0 or 2 when pacing adjusts, see below): samples the local input frame, quantizes it, drives the local `InputState` with it, sends the unacknowledged frames, runs `world.update(stepSeconds)`, stores the frame and the encoded predicted state in the history;
4. sets each proxy's replicated traits to their interpolated value at the presentation tick.

- **Proxies** carry `NetProxy`; the client's simulation systems exclude it (`REQ-STD-011`, and game systems add it to their exclusions).
- **Interpolation delay:** 2 snapshot intervals by default (0.1 s at the defaults), configurable in `[0, 1]` s. Presentation tick = newest server tick received minus the delay; values are interpolated between the two snapshots around it with the codec (without `interpolate`, the older value); with no newer snapshot, the last value is held. Snapshots are kept in a buffer of 32.
- **Client structural rule:** game code on a client must not create or remove replicated objects or remove their replicated traits; doing so fails at the flush with `IllegalStateException`. Client-only traits may be added to and removed from replicated objects. Client-only presentation objects live in another world.
- **Tick alignment:** target input lead 2 ticks, configurable in `[0, 10]`. If the reported lead is below 1, the next update runs 2 ticks; above target + 3, it runs 0 ticks; at most one adjustment every 10 ticks. If the client tick is more than 60 ticks away from server tick + target lead, it jumps there and clears the prediction history.
- **Prediction** (types declared predicted, objects owned by the local player only): history of 64 ticks of frames and encoded predicted state. On a snapshot of tick T: equal bytes → nothing; different → write the server state, re-run `world.update` for ticks T+1 to the current tick with the recorded frames while `isReplaying()` is `true` (game systems skip sounds and effects then); T outside the history → write the server state without replay. Corrections and snaps are counted.

### Input frames and commands (`REQ-MP-009`)

- `InputFrame` of one tick: per digital action 3 bits (`down`, `pressed`, `released`), per analog action a signed 16-bit value `q = round(v * 32767)` (sticks in `[-1, 1]`, triggers in `[0, 1]`), decoded as `q / 32767.0`. The client applies to its own simulation the frame as decoded, never the raw device values.
- Sent unreliably every client tick; each packet repeats every frame not yet acknowledged by a snapshot, up to the 32 newest.
- Server buffer: 64 ticks per player. At tick T the frame of T is applied. Missing: the held `down` and analog values of the previous frame are repeated with no edges. A frame arriving after its tick: its `pressed` and `released` edges are merged into the next applied frame. Frames more than 64 ticks ahead are dropped and counted.
- On the server, `inputOf(playerId)` returns that player's `InputState` (the `input-api` type), driven by frames; on the client, the local player's `InputState` is driven by its own frames, including during replay. A local game and a networked game read input with the same code.
- Commands: game messages of `network` (`RELIABLE`), applied by the server during step 1 of the tick in which they are received.

### Standard codecs (`REQ-MP-013`)

`StandardCodecs` registers codecs for `Transformable` (position and size; position interpolated linearly), `Movable`, `Gravity`, `Jumpable`, `Collidable` and `Lifetime`, writing every `double` exactly.

### Host and dedicated server (`REQ-MP-014`)

- `Host` creates a `NetworkServer`, a `ServerWorld`, a `NetworkClient` on `LoopbackNetwork` and its `ClientWorld`; the host's player is an ordinary client (same interpolation delay, same prediction) and the host process holds two worlds.
- A dedicated server is a `ServerWorld` on the headless platform with no rendering.

### Levels and session constraints (`REQ-MP-015`)

- Each `ServerWorld` attached to a session gets the next world epoch; a client applies only snapshots of the epoch of its own `ClientWorld`. A level change is: server game message → each side closes its world and attaches a new one.
- The session runs at time scale 1.0. Pausing the server's loop stalls the session (clients time out after the timeout of `network`); a game pause in network play is a server state sent to clients.
- Steady state allocates nothing per step in snapshot capture, delta encoding, snapshot application, interpolation and input frames.

### Defaults

| Item | Default | Range |
|---|---|---|
| Snapshot interval | 3 ticks (20 Hz at 60 Hz) | `[1, 60]` |
| Snapshot history (server) | 64 snapshots | — |
| Snapshot buffer (client) | 32 snapshots | — |
| Snapshot size limit | 64 fragments | `[1, 255]` |
| Interpolation delay | 2 snapshot intervals (0.1 s) | `[0, 1]` s |
| Frames repeated per packet | up to 32 | — |
| Server frame buffer | 64 ticks per player | — |
| Target input lead | 2 ticks | `[0, 10]` |
| Pacing | 2 ticks if lead < 1; 0 ticks if lead > target + 3; at most once per 10 ticks; jump beyond 60 ticks | — |
| Prediction history | 64 ticks | — |
| Replicated trait types | at most 64 | — |
| Input actions | at most 64 digital, 16 analog | — |

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Network and multiplayer); canonical names in [ADR-0038](../decisions/0038-network-model-server-authority-snapshots-prediction.md) section 16: `ReplicationSchema`, `TraitCodec<T>`, `ReplicatedTypeFactory`, `RelevanceFilter`, `StandardCodecs`, `ServerWorld`, `ClientWorld`, `Host`, `Replicated`, `NetProxy`, `InputFrame`. Signatures are written by the batches under `conventions.md`.

## Batches (order)

1. Schema and content hash, `Replicated` and `NetProxy`, complete snapshots, client application with explicit flush, client structural rule, exclusion support in the standard systems (`REQ-MP-004`, `REQ-MP-005`, `REQ-MP-008` without interpolation, `REQ-STD-011`).
2. Delta against the acknowledged baseline, snapshot fragmentation and limit, relevance filter, convergence suite (`REQ-MP-006`, `REQ-MP-007`, `REQ-MP-012`).
3. Input frames, server buffer, missing and late rules, per-player `InputState`, commands (`REQ-MP-009`).
4. Interpolation of proxies (`REQ-MP-008`).
5. Tick alignment (`REQ-MP-010`).
6. Prediction, replay and snap (`REQ-MP-011`).
7. Standard codecs, `Host`, world epochs, allocation check; examples: a headless two-player platformer over loopback with simulated latency and loss, and an RTS-style order example (`REQ-MP-013` to `REQ-MP-015`).

## Lifecycle and concurrency

The game builds the schema at startup, creates the session (`network`), then a `ServerWorld` or `ClientWorld` per level, calls its `update` once per step on the runtime thread, and closes it at the end of the level (the session stays open). Not thread-safe.

## Units and Android/DEX compatibility

World units of ADR-0013 (meters, seconds, m/s); ticks are `int`; interpolation ratios dimensionless. Pure Java on top of `network`, `ecs`, `traits` and `input-api`; no new JDK API beyond those of `network`; no `java.util.function`, `Optional` or streams (ADR-0037). Verified with the Android job like every shared module (`OQ-147`).

## Dependencies

`multiplayer` on `network`, `ecs`, `traits`, `input-api`, `core`. Forbidden: `runtime`, any platform, render or `camera` module; any dependency of another module on `multiplayer`.

## Tests

- Unit tests: codec round trips, content hash changes with any registration change, delta encoding and decoding against a baseline, baseline selection and fallback to complete, removal by absence, network id mapping, input frame encoding and quantization, missing and late frame rules, pacing decisions, prediction comparison, replay, snap, epoch filtering, the client structural rule.
- Contract tests (`test-support`): a codec suite (write then read gives equal bytes; interpolation at 0 and 1 gives the endpoints) run on every standard codec.
- Integration tests: **convergence suite** — a server and two clients over `ConditionedTransport` (latency 0.1 s, jitter 0.03 s, loss 0.2, duplication 0.05, reordering 0.1, fixed seeds), 3,000 ticks with objects created, moved and removed: after every applied snapshot, each proxy's traits equal the server's values of that tick and no object exists on one side only; a predicted avatar under the same conditions ends at the server's position; an RTS scenario with 1,000 replicated units and orders as commands.
- Performance tests: allocation check of the per-step paths; snapshot size and encode time for 1,000 objects reported by the trend job.

## Acceptance criteria

- [ ] Requirements `REQ-MP-004` to `REQ-MP-015` and `REQ-STD-011` verified.
- [ ] Automated tests added and passing, convergence suite included; complete Javadoc (units, thread, which side may call what).
- [ ] Headless examples (platformer with prediction, RTS orders) and `api-index.md` entries.
- [ ] No forbidden coupling; Android/DEX compatibility verified.
