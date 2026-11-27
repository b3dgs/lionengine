# ADR-0033 — Media defaults: module names, filters and shaders, graphics data, audio policies

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, under the project owner's explicit delegation of 2026-11-23 ("choose the best choice for all requirements and all sub pages related to them; mark them as VALIDATED with your choice"); reversible by a superseding ADR
- Related requirements: `REQ-PLATFORM-001`, `REQ-DISPLAY-003`, `REQ-FILTER-001` to `REQ-FILTER-004`, `REQ-SPR-001`, `REQ-SPRITE-001` to `REQ-SPRITE-010`, `REQ-ANIM-001`, `REQ-SHEET-001` to `REQ-SHEET-003`, `REQ-AUDIO-002` to `REQ-AUDIO-013`
- Open questions resolved: `OQ-156`, `OQ-159`, `OQ-160`, `OQ-161`
- Supersedes / superseded by: none; completes [ADR-0022](0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md), [ADR-0025](0025-filter-implementation-shaders-and-display-settings.md), [ADR-0026](0026-graphics-pixels-cache-transforms-animation-and-commands.md) and [ADR-0027](0027-audio-policies-formats-latency-and-first-scope.md) (all unchanged)

## Context

The media decisions left names, data layouts, limits and the details of the extension points to the batches. They are fixed here so that the display, filter, graphics and audio batches have no hidden choice.

## Decision

### 1. Module and type names (`OQ-156`)

- Audio backends: `audio-javasound` (desktop, Java Sound), `audio-android` (Android platform audio), later `audio-openal`. Third-party decoders: `audio-codec-<codec>` (first: `audio-codec-ogg`, desktop only).
- Optional image decoders: `graphics-imageio` (desktop, ImageIO) and `graphics-android` (Android `BitmapFactory`).
- Input adapters: `input-awt` and `input-android` (ADR-0035).
- No `awt-common` module until a third AWT module needs the surface type.
- The display-mode value type offers `isRefreshRateKnown()` and `isBitDepthKnown()`, true when the value is not `0` (ADR-0022).
- The AWT surface capability type is `AwtSurface`, in `platform-awt`.

### 2. Filters and shaders (`OQ-159`)

- Shader sources use the GLSL ES 1.00 feature set and are written to compile both as `#version 100` (OpenGL ES 2.0) and `#version 120` (desktop OpenGL 2.1). Shader files carry no `#version` line and no precision qualifier: the backend prepends them. File extensions: `.vert` and `.frag`; they are resources (ADR-0005).
- Standard uniforms available to every filter shader: source texture, source size and output size in pixels, and elapsed presentation time in seconds.
- `render-awt` implements on the CPU: `NONE` (nearest neighbor), bilinear, and `Scale2x` (standing in for `HQ2X` until the license record exists, ADR-0025). CRT is GPU-only: `render-awt` reports it unsupported, so it is skipped and reported (ADR-0006).
- `HQ2X` is written from the published algorithm description; if code is ported from a reference implementation, its license is recorded in [`../dependencies.md`](../dependencies.md) before merge (ADR-0032).
- **Custom filter extension point (`REQ-FILTER-003`):** a custom filter has a registry id (`namespace:name`, ADR-0032) and is registered explicitly on the render backend at startup. It provides a fragment shader resource key (GPU backends), a CPU implementation over `int[]` ARGB buffers (CPU backends), or both. It declares whether it is a scaling filter or a post filter. A backend lacking the needed implementation skips it and reports the skip in the effective result (ADR-0006).

### 3. Graphics data (`OQ-160`)

- Pixel buffer: packed `int` ARGB (`0xAARRGGBB`), row-major, origin at the top-left, stride equal to the width, **straight (non-premultiplied) alpha**. Backends may premultiply at upload. Blending is source-over.
- Texture sampling for sprites and tiles is nearest-neighbor by default (pixel art); output filtering is separate (ADR-0002).
- PNG decoder subset: non-interlaced images of every color type (grayscale, RGB, palette, grayscale with alpha, RGBA), bit depths 1 to 8, and 16 reduced to 8; `tRNS` transparency is supported; gamma and color profiles are ignored; Adam7 interlacing and the extra frames of APNG are rejected with the decoding exception of `graphics`.
- Render command entry fields: texture handle (`int`), source rectangle (`int` x, y, width, height in texture pixels), destination position (`float` pixels of the logical frame, ADR-0014), scale x and y (`float`), rotation (`float` degrees), pivot (`float` pixels from the frame top-left), tint (`int` ARGB, white means none), alpha (`float` in `[0, 1]`), layer (`int`). The buffer stores entries as parallel primitive arrays. Commands are presented in ascending layer order, then submission order (stable).
- Sprite sheet definition (`"format": "lionengine.sheet"`, `"version": 1`): `image` (resource key), `cellWidth`, `cellHeight`, optional `columns`, `rows` (derived from the image when absent), `margin`, `spacing`, `pivotX`, `pivotY` (default the cell center), `animations` (an object of named animations), `templates` (an object of named color mappings `{"#RRGGBB": "#RRGGBB"}`).
- Animation fields: `frames` (array of cell indices), `frameDuration` (seconds), optional `frameDurations` (seconds, one per frame, overriding `frameDuration`), `loop` (`"once"`, `"loop"` or `"ping-pong"`). Per-frame durations are part of the animated-sprite batch.
- Sheet cells are numbered row by row from the top-left cell, starting at `0`; an out-of-range index throws `IndexOutOfBoundsException`.
- Animation states: stopped, playing, paused, finished (`once` only). `play` starts from the first frame, `pause` keeps the frame, `resume` continues a pause, `stop` returns to the first frame; a call that is invalid in the current state throws `IllegalStateException`. `ping-pong` plays forward then backward and shows each end frame once per turn. A step longer than a frame advances several frames.
- Mask-layer templates stay a later extension, added only when anti-aliased art needs them.

### 4. Audio (`OQ-161`)

- Voices: default **16** simultaneous short voices (ADR-0027); recalibration after measurement is an ordinary change.
- Limits: a short sound is at most **10 s** long; a longer one is refused with a defined error and belongs to the long family. The short-sound cache size limit is opt-in, in bytes, none by default.
- Priority: an `int` from `0` (lowest) to `100` (highest), default `50`.
- Variant weights: a non-negative `double` per variant, default `1.0`; a weight of `0` excludes the variant; the no-immediate-repeat rule applies when at least two variants have a positive weight.
- Playlist failure event: a listener called on the runtime thread with the playlist, the track index and the failure exception.
- Streaming buffer: default **4 chunks of 4,096 frames** (about 0.37 s at 44.1 kHz), configurable.
- Desktop OGG Vorbis decoder: JOrbis (pure Java) is the candidate for `audio-codec-ogg`; it is added only with its entry in [`../dependencies.md`](../dependencies.md) (license and status recorded, ADR-0032). Android decodes OGG with the platform.
- Volume: a `double` gain in `[0, 1]`; effective gain = master × group × sound.
- Mixer groups (second batch): the groups `music`, `effects` and `ui` exist by default; a game may add groups by id. Each sound is in exactly one group (`effects` for short sounds, `music` for long sounds by default).
- Fades (second batch): a fade has a target gain and a duration in seconds; the backend ramps the gain linearly per audio buffer; completion is posted to the inbox.
- Spatial audio stays deferred; when scheduled it will use the camera center as listener, positions in meters, stereo pan and distance attenuation.
- Latency targets (ADR-0027) are measured by platform tests from the request to the first sample, outside CI; Android measurement waits for `OQ-147`.

## Options considered

- Alpha: straight (chosen; matches PNG, AWT `TYPE_INT_ARGB` and Android `Bitmap` conventions; simple palette mapping) vs premultiplied (cheaper blending, lossy for low alpha, complicates color mapping).
- Shader dialect: one GLSL ES 1.00 source with prepended headers (chosen) vs two shader sets (doubled maintenance, rejected by ADR-0025).
- Interlaced PNG: rejected (chosen; rare in game assets, a large share of decoder complexity) vs supported.
- Command buffer layout: parallel primitive arrays (chosen; no allocation, cache friendly) vs an array of mutable entry objects.
- OGG on desktop: JOrbis candidate (chosen; pure Java, no native code) vs a native library binding (platform packaging, DEX irrelevant but native risk).

## Consequences

- Positive consequences: every media batch has its names, layouts and limits; the extension point for filters is defined; the license step for third-party code is explicit.
- Costs / constraints: CRT only on GPU backends; interlaced PNG unsupported; a 10 s limit for short sounds.
- Impact on modules and APIs: `render-api`, `render-awt`, `render-opengl`, `graphics`, `audio-api`, audio backends, decoder modules.
- Impact on tests and documentation: `REQ-SPRITE-009`, `REQ-SPRITE-010`, `REQ-SHEET-003`, `REQ-AUDIO-012`, `REQ-AUDIO-013` added; `REQ-FILTER-003` made precise; feature documents, `modules.md`, `contracts.md`, `android-compatibility.md` updated.

## Validation

Decided by the agent on 2026-11-23 under the owner's explicit delegation; reversible by a superseding ADR.
