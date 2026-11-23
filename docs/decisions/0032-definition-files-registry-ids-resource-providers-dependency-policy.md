# ADR-0032 — Definition files, registry ids, resource providers, dependency policy and documentation checks

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, under the project owner's explicit delegation of 2026-11-23 ("choose the best choice for all requirements and all sub pages related to them; mark them as VALIDATED with your choice"); reversible by a superseding ADR
- Related requirements: `REQ-DEF-001` to `REQ-DEF-003`, `REQ-RT-007`, `REQ-RES-001` to `REQ-RES-006`, `REQ-MAP-004`, `REQ-CORE-003`, `REQ-DOC-005` to `REQ-DOC-007`
- Open questions resolved: `OQ-148`, `OQ-154`, `OQ-157`, `OQ-162`; narrows `OQ-149`
- Supersedes / superseded by: none; completes [ADR-0007](0007-resource-convention-keys-release-and-errors.md), [ADR-0012](0012-platform-and-build-baseline.md), [ADR-0020](0020-no-reflection-factory-registry-and-capability-lookup.md), [ADR-0023](0023-game-repository-change-requests-assets-and-api-index-check.md) and [ADR-0028](0028-tile-map-collision-json-definitions-and-storage.md) (all unchanged)

## Context

ADR-0020 and ADR-0028 chose an explicit registry and JSON definition files, ADR-0023 chose provenance files and an API index check, and ADR-0012 chose GPL-3.0. Id rules, the typed configuration, the reader limits, the definition schema convention, the Tiled subset, the resource provider rules, the third-party policy and the check implementations were left open.

## Decision

### 1. Registry ids and typed configuration (`OQ-154`)

- A registry id has the form `namespace:name`. Each part matches `[a-z][a-z0-9_-]*`; ids are case-sensitive and lower-case only. The engine uses the namespace `lionengine` (for example `lionengine:movable`); a game uses its own namespace.
- The registry and the typed configuration live in `core`, next to the JSON reader: `graphics`, `tilemap`, `ecs` and `traits` all read definitions.
- The typed configuration is a read-only view of one JSON object: getters by name and type (`int`, `long`, `double`, `boolean`, string, object, array), each with and without a default. A missing required value, a wrong type or an out-of-range value throws the definition exception naming the id, the parameter and the file position.
- **Strict fields:** a field that the factory did not read is an error, except `"comment"`. Typos fail at load time instead of being ignored.
- Build-time generated registration (annotation processing) is not adopted.

### 2. JSON reader and definition files (`OQ-162`)

- The reader is in `core`, pure Java, standard JSON (RFC 8259) only, UTF-8 (a leading byte-order mark is ignored). Duplicate keys, `NaN`, `Infinity`, comments and trailing commas are errors.
- Numbers: an integral literal without fraction or exponent that fits a `long` is read as `long`, any other as `double`; an `int` getter fails on a non-integral or out-of-range value.
- Errors: one unchecked definition exception carrying the resource key, line, column and JSON path (for example `$.animations.walk.frames[3]`).
- Default limits, configurable: document size 16 MiB, nesting depth 64, string length 1 MiB.
- Every engine definition file has two top-level fields: `"format"` (the kind, for example `"lionengine.sheet"`, `"lionengine.tileset"`, `"lionengine.object"`) and `"version"` (an `int`, starting at `1`). A loader rejects an unknown kind or a version higher than it supports. There is no schema language: each loader documents its fields in its Javadoc and in its feature document. Field names are lower camel case. Colors are strings `"#RRGGBB"` or `"#AARRGGBB"`. Durations are seconds, angles degrees, image positions pixels.
- Tiled files keep their own format: the Tiled loader accepts orthogonal, finite maps with one tile layer (ADR-0009), layer data as a JSON array or base64 (uncompressed, zlib or gzip through `java.util.zip`; zstd rejected), one tileset per map, embedded or external (`.tsj`). The tile collision property is the Tiled tile property `collision` with value `"empty"` or `"solid"`. Flip and rotation flags, infinite maps, isometric or hexagonal orientations and several tile layers are rejected with a definition exception; object, image and group layers are ignored in the first batch and documented as such.
- Base64 data is decoded by project code: `java.util.Base64` needs Android API 26 and is not used in shared code. Tile rendering draws the visible tiles plus a margin of one tile by default, configurable.
- No XML converter is shipped: old XML files are re-exported from Tiled or rewritten. No packaged binary form is planned; it would need a measured load-time problem and a new ADR.

### 3. Resource providers (`contracts.md`, Resources)

- The application configures an **ordered list of providers**; a key is looked up in each provider in order and the first one that has it wins. There is no merging of content.
- Providers are read-only by default. Writing is allowed only through a provider declared writable (temporary or memory storage, application storage) and an explicit open-for-write call.
- A key never contains a physical path; physical locations appear only in provider configuration.
- An opened stream belongs to the caller, who closes it.
- Providers are thread-safe for reading, because loading workers read through them (ADR-0019); writing happens on the runtime thread only.
- Missing or inaccessible content raises the ADR-0007 exception, with the key and a reason (`MISSING` or `INACCESSIBLE`); invalid format and corruption are decoding errors of the consuming module.

### 4. Third-party dependency policy (`OQ-148`)

- A dependency shipped with the engine must have a GPL-3.0-compatible license: MIT, BSD-2-Clause, BSD-3-Clause, ISC, zlib, Apache-2.0, MPL-2.0, LGPL-2.1-or-later, LGPL-3.0, GPL-3.0-or-later, or public domain (CC0, Unlicense). GPL-2.0-only, non-commercial, proprietary and unknown licenses are refused. Build and test tools that are not shipped may use any OSI-approved license.
- Every dependency, shipped or not, is recorded in [`../dependencies.md`](../dependencies.md) before it is added: name, version, module, purpose, license (SPDX id), Android status (`android-compatibility.md` vocabulary), date checked. The pull request that adds it updates the register.
- Codecs: baseline formats are decoded by project code (PNG, WAV, JSON); Android uses platform codecs; a third-party codec lives in its own decoder module (ADR-0022) after its register entry.
- An algorithm written by the project from a published description is not a dependency. Code ported from a reference implementation is, and is recorded with its license in the register (this is how the `HQ2X` check of ADR-0025 is recorded).

### 5. Asset provenance and API index check (`OQ-157`)

- Provenance metadata is a JSON file: `provenance.json` in a folder applies to every file of that folder; `<asset file name>.provenance.json` next to an asset overrides it. Fields: `"format": "lionengine.provenance"`, `"version": 1`, `"source"` (`"hand-made"`, `"generated"` or `"third-party"`), `"author"`, `"license"` (SPDX id), `"tool"` and `"model"` (required for `"generated"`), `"url"` (required for `"third-party"`), `"date"` (ISO 8601), optional `"comment"`.
- A CI check fails when an asset file (images, sounds, maps, tilesets, definition files under asset folders) has no applicable provenance entry or an entry without a license. It is written at the build skeleton for the engine test assets and copied by game repositories.
- The API index check, written at the build skeleton, reads the type names in [`../api-index.md`](../api-index.md) and fails if one is not a public type of the compiled sources, or if a named example is not a compiled class of `examples`.
- Where artifacts are published stays with the release step (`OQ-149`).

### 6. Javadoc check (`REQ-CORE-003`)

- Javadoc is generated by the build with doclint `all` on public and protected members; any doclint warning fails the build. Missing `package-info.java` fails it too.

### 7. Versioning details (part of `OQ-149`)

- A deprecated API stays for at least one minor release and is removed only in a major release; its Javadoc names the replacement (ADR-0012).

## Options considered

- Id form: `namespace:name` (chosen; collision-free between engine, game and mods) vs bare names (collisions) vs Java class names (forbidden by ADR-0020).
- Unknown fields: error (chosen; typos fail fast, agents get immediate feedback) vs ignored (silent misconfiguration).
- Schema: `format` and `version` fields plus Javadoc (chosen) vs JSON Schema files (a validator dependency or a second reader).
- Provider order: first match wins (chosen; predictable override such as a mod folder before the base data) vs merging (ambiguous).
- License policy: GPL-3.0-compatible allow-list and a register (chosen) vs case-by-case review only (no record, decisions lost).

## Consequences

- Positive consequences: one id form, one reader, one error format and one schema rule for every data file; predictable overrides; a single place where licenses are recorded.
- Costs / constraints: strict fields make data files fail on unknown fields, so a newer file needs a newer engine (by design, with `version`); a register to maintain; two CI checks to write at the skeleton.
- Impact on modules and APIs: `core` (registry, typed configuration, JSON reader, definition exception, logging), `resources` and `resources-*` (provider order, writable providers), `tilemap` (Tiled subset).
- Impact on tests and documentation: `REQ-DEF-002`, `REQ-DEF-003`, `REQ-RES-006`, `REQ-DOC-007` added; `REQ-CORE-003` made precise; new [`../dependencies.md`](../dependencies.md); `contracts.md`, `android-compatibility.md`, `features/tile-map.md`, `features/sprite-sheet.md` updated.

## Validation

Decided by the agent on 2026-11-23 under the owner's explicit delegation; reversible by a superseding ADR.
