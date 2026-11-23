# ADR-0049 — Interface catalog and a contract-skeleton batch before the feature batches

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, at the project owner's request of 2026-11-23 to review the architecture with the dedicated skills and apply the changes the review suggests, so that the base skeleton of each module and its interfaces can be defined; reversible by a superseding ADR
- Related requirements: `REQ-CORE-003`, `REQ-CORE-004`, `REQ-CORE-005`, `REQ-TECH-006`, `REQ-DOC-001`, `REQ-DOC-002`
- Open questions resolved: none
- Supersedes / superseded by: none; completes [ADR-0037](0037-package-tree-per-module-callbacks-and-canonical-names.md) (section 4: names not listed are chosen by the batch, here the contract skeleton for the seams it declares) and [ADR-0042](0042-build-skeleton-closure-and-maven-central.md) (both unchanged)

## Context

The owner's goal after this review is to define the base skeleton of each module and its interfaces. Today:

- the build skeleton ([`features/build-skeleton.md`](../features/build-skeleton.md)) declares empty modules, and each feature batch writes the signatures of its own module;
- the contracts that cross a module boundary (seams) are defined in one module, implemented in a second and used in a third, often in different phases. For example: `WorkerPool` is defined in `core`, provided by the platforms and used by `graphics`, `audio-api`, `tilemap` and `network`. The texture contract is defined in `render-api`, implemented by the backends and used by `graphics`. Writing a seam inside the first batch that touches it lets that batch decide for every later consumer. Agents working in parallel ([`agent-workflow.md`](../agent-workflow.md)) can then disagree;
- the seam names and responsibilities are spread over ADR-0037, ADR-0038, ADR-0040 and the feature pages. No document lists them per module, and the four dependency defects fixed by ADR-0045 to ADR-0048 went unnoticed for that reason.

## Decision

1. **Interface catalog.** [`../architecture/interfaces.md`](../architecture/interfaces.md) lists, per module, its root package and its public types. For every seam it gives the defining module, its kind, who implements or provides it, who uses it, and which batch declares it. It summarizes: names stay owned by ADR-0037, ADR-0038, ADR-0040 and the feature pages, and in a conflict the catalog ranks with [`contracts.md`](../architecture/contracts.md) ([`documentation.md`](../documentation.md)). Every batch that adds or changes a seam updates it.
2. **Contract-skeleton batch.** After the build skeleton and before the Phase 1 feature batches, one Phase 0 batch ([`features/contract-skeleton.md`](../features/contract-skeleton.md)) declares the seams marked "contract skeleton" in the catalog, together with the enums, value types and exceptions their signatures name. Every declared type has complete Javadoc restating its validated contract: thread, units, ownership, errors and absence. Behavior is limited to the validation and accessors of value types. The batch declares no concrete engine class, such as `World`, `GameLoop`, `CommandBuffer` or `Resources`. A seam whose signature needs a concrete class is declared, or completed, by the feature batch that writes that class; the catalog names that batch.
3. **Signatures are reviewed once.** The contract-skeleton pull request proposes the signatures and is where they are reviewed (`AGENTS.md`, section 3). Names not fixed by ADR-0037, ADR-0038 or ADR-0040 are chosen there under [`../conventions.md`](../conventions.md).
4. **Later batches implement the declared contracts.** A feature batch that must change a declared signature changes it in its own pull request, together with its users and the catalog. No compatibility is kept before the first release.
5. **No feature, so no example.** A declared contract becomes usable when its feature batch implements it; that batch adds the example and the API-index entry (`REQ-DOC-001`, `REQ-DOC-002`). The contract skeleton adds neither, and the API index lists only types a game can use.

## Options considered

- A separate contract-skeleton batch (chosen): the seams are fixed once, compiled, checked by doclint and the package rule, and dexed by the Android job before any behavior exists. It stays a reviewable change set.
- Declare the interfaces inside the build-skeleton batch (rejected): one pull request mixing the reactor, CI, the Android job and some thirty signatures, which is not one reviewable change set (`AGENTS.md`, section 6).
- Keep per-batch signatures (rejected): the first batch decides each seam for every later consumer, and parallel batches drift.
- Declare every public type of every module, concrete classes included (rejected): stub classes without behavior are not contracts, and they would be rewritten by their batches.
- Also declare now the contracts of `audio-api`, `input-api`, `ecs`, `physics`, `camera`, `tilemap`, `network` and `multiplayer` (rejected). Their signatures need a class of their own batch (`World`, `PixelBuffer`, `CommandBuffer`), or a hot-path shape that the batch designs (`ShapeQuery`, `FollowLookup`: no allocation per call). The first batch of each of these modules declares them before any implementer exists, so no later consumer is decided for.

## Consequences

- Positive consequences: one place to check that every seam is allowed by the dependency rows; the Phase 1 batches start from compiled contracts; the Android job checks the seams early.
- Costs / constraints: one more Phase 0 batch. Javadoc written before the implementation may be refined by the feature batch. Declared interfaces have no implementation until their batches.
- Impact on modules and APIs: the modules of the build skeleton receive their seam types; no new module.
- Impact on tests and documentation (including Android/DEX compatibility): new `architecture/interfaces.md` and `features/contract-skeleton.md`; `roadmap.md`, `features/README.md`, `engine-features.md`, `documentation.md`, `README.md` updated. Value types get unit tests; the declared types go through the Android job (`REQ-TECH-006`).

## Validation

Decided by the agent on 2026-11-23 at the owner's request to apply the review's changes. After reading a summary of this ADR, the owner left the remaining choice, the width of the contract skeleton, to the agent the same day ("I let you choose the best option"). The agent kept the scope above: the cross-module contracts of Phases 1 and 2 whose signatures are fully specified now. Reversible by a superseding ADR.
