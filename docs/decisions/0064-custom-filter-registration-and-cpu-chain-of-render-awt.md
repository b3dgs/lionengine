# ADR-0064 — Custom output filters: registration on the render backend, the implementation each backend needs, the CPU chain of render-awt

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-FILTER-003`, `REQ-FILTER-002`
- Open questions resolved: none (details left open by ADR-0033 and the contract skeleton, needed by batch 3 of [`../features/output-filtering.md`](../features/output-filtering.md))
- Supersedes / superseded by: none; completes [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md), [ADR-0062](0062-filter-chain-shape-and-per-filter-fallback.md) and [ADR-0063](0063-render-awt-cpu-filters-scale2x-stand-in.md) (all unchanged)

## Context

ADR-0033 defines a custom filter:
- it has a registry id (`namespace:name`) and is registered explicitly on the render backend at startup;
- it declares whether it scales or post-processes;
- it provides a fragment shader resource (GPU backends), a CPU implementation over `int[]` ARGB buffers (CPU backends), or both;
- a backend lacking the needed implementation skips it and reports the skip.

The contract skeleton declares `CustomFilter` (its `OutputFilter`, shader `ResourceKey` and `CpuFilter`) and `CpuFilter` (`apply(source, width, height, target, width, height)`, allocating nothing). `RenderBackend` has no registration method. ADR-0062 resolves the chain against the backend, a custom scaling filter falling back to `NONE`. ADR-0063 gives `render-awt` its standard scaling filters and no post filter.

No document says:
- the signature and rules of the registration;
- which implementation each backend needs;
- how `render-awt` runs a chain of CPU filters, at which sizes;
- the pixel format the filters see;
- how the recording backend of the tests behaves.

## Decision

1. **Registration.** `RenderBackend.register(CustomFilter filter)` adds a custom filter, on the runtime thread.
   - It is refused with an `IllegalArgumentException` when the filter or its identifier is `null`, the identifier is a standard one (`NONE`, `BILINEAR`, `HQ2X`, `CRT`), or a filter with the same id is already registered.
   - A registered filter stays registered while the backend lives.
   - The platform reads the capabilities each time it resolves the chain, so a filter is applied from the next request or resize after its registration. Registering at startup, before the platform starts, makes it part of the first resolution.
2. **The implementation a backend needs.** `isSupported` answers `true` for a custom filter only when it is registered, the chain names it with the kind it was registered with, and it has the implementation that backend runs: the `CpuFilter` for a CPU backend (`render-awt`), the fragment shader for a GPU backend (`render-opengl`). A filter registered without it is skipped or, for a scaling filter, falls back to `NONE` (ADR-0062), reported in the effective result. The recording backend of `test-support` acts as a GPU backend: it needs the shader and runs nothing.
3. **The CPU chain of `render-awt`.** When the effective chain holds a custom filter:
   - the frame is first scaled to the size of the presentation geometry into an image kept by the backend: by the custom scaling filter (source: the logical frame; target: the geometry size), or else by the standard scaling filter of ADR-0063;
   - each post filter then runs on that buffer, the output of one being the input of the next (source and target: the geometry size, two buffers in turn);
   - the result is drawn at the geometry position, without scaling.

   The arrays and images are created at the first frame of a size and reused while the size stays the same, and each `CpuFilter` is read once at registration, so a frame allocates nothing. Without a custom filter, the frame is presented as in ADR-0063. An exception thrown by a filter propagates, as any failure of the render callback (ADR-0017).
4. **Pixel format.** The filters see packed `0xAARRGGBB` pixels, row-major, stride equal to the width. The source is opaque (alpha `0xFF`), and the alpha the filter writes is ignored, since the presented frame is opaque.

## Options considered

- Registration:
  - A method on `RenderBackend` (chosen): explicit, the backend owns the implementations it runs, and ADR-0033 says "registered on the render backend".
  - A registry in `render-api` given to each backend (rejected: one more object to wire; each backend still decides what it can run).
- A duplicate id:
  - Refused (chosen): two filters with one id would make the chain ambiguous.
  - The last one wins (rejected: it hides a mistake).
- A filter lacking the backend's implementation:
  - Accepted at registration, reported as unsupported (chosen): the same game code runs on CPU and GPU backends, and the skip is reported where it applies.
  - Refused at registration (rejected: a game would need to know the backend kind to register its filters).
- Size of the post filters on the CPU:
  - The geometry size (chosen): post filters "run on the scaled output" (ADR-0006), and the black bars stay outside.
  - The whole surface (rejected: the bars would be filtered too, for nothing).

## Consequences

- Positive consequences:
  - A game adds its own effects (scanlines, color grading, its own scaler) on the desktop with a CPU implementation, and on GPU backends with a shader, from the same registration.
  - Missing implementations are reported, never fatal.
- Costs / constraints:
  - Every `RenderBackend` implements one more method.
  - A CPU chain costs one copy of the scaled frame and one pass per post filter, at the geometry size: on a large window this is slow, so it fits light effects.
  - A change of window size recreates the buffers once.
- Impact on modules and APIs:
  - `render-api`: `RenderBackend.register`. The contract suite checks the registration rules and the implementation each backend needs.
  - `render-awt`: the CPU chain.
  - `test-support`: `RecordingBackend.register`, acting as a GPU backend.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: contract tests of the registration on the recording backend (GPU) and on `render-awt` (CPU); headless golden images of a custom scaling filter and of a chain of post filters; an allocation test; an example registering a filter.
  - Documentation: `features/output-filtering.md`, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md` and the traceability.
  - Android/DEX: one interface method in `render-api`, run by the device test of the render module.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the batches; reviewed in the pull request of output-filtering batch 3.
