# ADR-0045 — Test fixtures with their contracts, `test-support` as the headless application kit

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, at the project owner's request of 2026-11-23 to review the architecture with the dedicated skills and apply the changes the review suggests, so that the base skeleton of each module and its interfaces can be defined; reversible by a superseding ADR
- Related requirements: `REQ-TEST-001`, `REQ-CORE-004`, `REQ-CORE-005`, `REQ-PLATFORM-001`, `REQ-PLATFORM-002`, `REQ-RES-004`, `REQ-AUDIO-009`, `REQ-MAP-004`, `REQ-NET-002`, `REQ-MP-013`
- Open questions resolved: none (defect found by the skeleton-readiness review of 2026-11-23)
- Supersedes / superseded by: supersedes the placement "in `test-support`" of the contract suites and fakes stated by [ADR-0002](0002-display-modes-resolution-and-output-filtering.md), [ADR-0003](0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [ADR-0004](0004-audio-contracts-short-and-long-sounds.md), [ADR-0005](0005-common-convention-for-modules-handling-resources.md), [ADR-0006](0006-display-scaling-effective-result-and-fallback-orders.md), [ADR-0007](0007-resource-convention-keys-release-and-errors.md), [ADR-0008](0008-awt-desktop-implementer-of-platform-api.md), [ADR-0009](0009-tile-map-contracts-in-tilemap.md) and [ADR-0040](0040-thread-hand-off-contracts-in-core-and-loop-wiring.md) (section 2, `test-support` bullet), and the `test-support` row of [`../architecture/modules.md`](../architecture/modules.md); every other point of these ADRs is unchanged

## Context

[`modules.md`](../architecture/modules.md) gives one `test-support` module the controllable clock, the manually driven loop, the headless frontend, the fake backends and every reusable contract suite, with the dependency row "contracts of the modules it tests; used in `test` scope only". The feature pages then ask modules to use those suites and fakes in their own tests:

- `runtime` tests the loop with the controllable clock, which implements the `runtime` `Clock`;
- `network` runs the transport suite on `LoopbackNetwork` and `UdpTransport`, both in `network`;
- `multiplayer` runs the codec suite on its standard codecs;
- `resources` runs the provider suite on its application-stream provider; `ecs` runs the trait and system conventions.

If `test-support` depends on a module and that module's tests depend on `test-support`, the reactor has a cycle. Maven rejects a cycle whatever the scope, so the build skeleton cannot declare the row, and Phase 1 fails as soon as `runtime` needs the controllable clock.

## Decision

### 1. Fixtures live with the contract they exercise

- The fakes, spies and controllable doubles of a module's contracts, and the abstract contract suites that the implementations of those contracts must pass, are written in that module's test sources, under its root package ([ADR-0037](0037-package-tree-per-module-callbacks-and-canonical-names.md)), and published as the module's **test JAR** (Maven `test-jar` goal, classifier `tests`).
- An abstract suite is bound to an implementation by a subclass in the implementing module's tests.

### 2. Test dependencies follow the main graph

- A module may depend, in test scope, on the test JAR of a module it already depends on in main scope (directly or transitively), and on `test-support` when `test-support` does not depend on it (section 3). No other test dependency between engine modules is allowed, so the test scope can never create a cycle.
- The module-dependency check of the build skeleton applies this rule to test-scope dependencies too.

### 3. `test-support` is the headless application kit

- `test-support` keeps what combines several modules into a headless application: the **headless platform** (an implementation of `Platform` that passes the `platform-api` suites, with event injection and a fake capability set), the **recording render backend** (presents into a list of command entries per frame), and the **manually driven loop** (a `GameLoop` advanced one iteration at a time on the controllable clock). It is a normal JAR, so a game repository can use it for its own headless tests (ADR-0023).
- It depends on `core`, `runtime`, `platform-api` and `render-api` (and on what they depend on), and on the test JARs of `core` and `runtime` in compile scope, so that its users also get the fakes it is built on.
- It is used in test scope by the modules it does not depend on: `graphics`, `tilemap`, `camera`, `ecs`, `physics`, `traits`, `input-*`, `audio-*`, `composition`, `network`, `multiplayer`, `platform-awt`, `render-awt`, `render-opengl`, `examples`. The modules it depends on (`core`, `runtime`, `platform-api`, `render-api`, `resources`) test themselves with their own fixtures.

### 4. Placement of the fixtures named by the feature pages

| Fixture | Lives in |
|---|---|
| Fake `WorkerPool` (runs submitted work when the test asks), inbox spy, manual `LoopDriver` | `core` test JAR |
| Controllable clock | `runtime` test JAR |
| `platform-api` contract suites (lifecycle events and their effect on the loop, close veto, capability presence and absence, effective result and reason, no exception on a missing capability, frame-boundary application, resize) | `platform-api` test JAR |
| Render backend and filter suites (command ordering, viewport clipping, fallback reported, custom filter on CPU and GPU fakes) | `render-api` test JAR |
| Provider suite; cache convention suite run by every consuming module | `resources` test JAR |
| Fake audio backend; short-sound and long-sound suites | `audio-api` test JAR |
| Map loader suite with an in-memory test format | `tilemap` test JAR |
| Fake follow lookup | `camera` test JAR |
| Fake devices and the input adapter suite | `input-api` test JAR |
| Trait and system convention suites | `ecs` test JAR |
| Transport suite | `network` test JAR |
| Codec suite | `multiplayer` test JAR |
| Headless platform, recording render backend, manually driven loop | `test-support` |

A fixture not listed here follows section 1.

### 5. Build

The build skeleton configures the test JAR in every module and verifies, with a probe, that its recorded build and test commands resolve a test JAR of another module of the reactor.

## Options considered

- Fixtures with their contract in test JARs, plus a headless kit (chosen): no cycle, each contract and its suite change in one module, implementations get the suite from the module they already depend on.
- One `test-support` depending on every API (current text, rejected): a reactor cycle as soon as a module it depends on uses it in its tests.
- One test-support module per API (rejected): about fifteen more artifacts for small fakes, and the same cycle rule still applies to each.
- Fixtures in the main code of each API module (rejected): ships test doubles and JUnit-based suites in engine artifacts and makes JUnit a main dependency.
- Each module copies the fakes it needs (rejected): copies drift, and contract suites cannot be shared.

## Consequences

- Positive consequences: the test graph is acyclic by construction; a contract, its fakes and its suite stay together; games keep one headless frontend artifact.
- Costs / constraints: one test JAR per module; consumers declare test-JAR dependencies; `test-support` depends on two test JARs in compile scope; the module-dependency check covers test scope.
- Impact on modules and APIs: `test-support` row and responsibility; no main API changes.
- Impact on tests and documentation (including Android/DEX compatibility): `modules.md`, `testing.md`, `change-process.md`, `glossary.md`, `requirements.md` (verification wording), the feature pages that name `test-support`, `features/build-skeleton.md`. Test JARs are not shipped in applications; no Android/DEX impact.

## Validation

Decided by the agent on 2026-11-23 at the owner's request to apply the review's changes; reversible by a superseding ADR.
