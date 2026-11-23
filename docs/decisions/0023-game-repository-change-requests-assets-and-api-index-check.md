# ADR-0023 — Game repository, engine change requests, asset provenance and API index check

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner ("Confirmed as proposed, write the ADR")
- Related requirements: `REQ-DOC-001`, `REQ-DOC-002`, `REQ-DOC-004`, `REQ-DOC-005`, `REQ-DOC-006`
- Open questions resolved: `OQ-080`, `OQ-081`, `OQ-082`, `OQ-083`
- Supersedes / superseded by: none

## Context

A game is built on the engine by local and cloud agents (`docs/agent-workflow.md`). The layout of the game repository, the path for missing engine capabilities, the provenance of assets (including locally generated sprites) and the synchronization of `api-index.md` with the code were open.

## Decision

1. **Game repository (`OQ-080`).** A separate repository depends on the engine Maven artifacts (group `com.b3dgs.lionengine`, ADR-0012) at an exact pinned version; one version for all modules (ADR-0015). A snapshot is used only while the engine is under active development. The engine publishes a sources JAR and a Javadoc JAR (`REQ-DOC-004`), which the game's agents read at the pinned version. The game's `AGENTS.md` is copied from `templates/GAME-AGENTS-TEMPLATE.md`. Where artifacts are hosted (GitHub Packages or Maven Central) is decided at the release step (`OQ-149`).
2. **Engine change requests (`OQ-081`).** A GitHub issue in the engine repository using the template `.github/ISSUE_TEMPLATE/engine_change_request.md` (game need, triggering code, workaround if any). The owner triages: a small request becomes a batch; an architecture or public-API change becomes a proposal then an ADR (`AGENTS.md` section 9). A game agent never works around a missing capability inside the game; it writes the request.
3. **Asset provenance (`OQ-082`).** Each asset or asset folder has a small metadata file recording: source (hand-made, generated, third-party), tool and model for generated assets, license, author. Assets live in the game repository; the engine keeps only tiny test assets for examples. Names are lowercase with hyphens; formats per asset kind are fixed when the first loader exists. A generated asset is accepted only with a recorded tool and license, so redistribution stays GPL-compatible (`OQ-148`). The metadata file format and a CI check are follow-ups (`OQ-157`).
4. **API index (`OQ-083`).** `api-index.md` stays hand-written, organized by task. A CI check, added once the build skeleton exists, verifies that every type it names exists in the public API and that every example it names is compiled (`REQ-DOC-001`). Until then `scripts/check-docs.py` covers links only.

## Options considered

- Game consumption: pinned Maven version (chosen) vs a source dependency or git submodule (couples builds, no version pin for agents) vs always snapshot (unpinned).
- Change requests: GitHub issue with template (chosen) vs free-form messages (untracked) vs a file in the repository (needs a commit for each request).
- Provenance: metadata file per asset (chosen) vs one global ledger (merge conflicts, drifts) vs none (license risk).
- API index: hand-written plus CI check (chosen) vs generated (flat list, not by task) vs manual review only (drifts).

## Consequences

- Positive consequences: reproducible game builds; agents read the exact engine version; one tracked path for engine needs; license-safe assets; an index that cannot silently drift.
- Costs / constraints: an engine fix needs a new version before the game sees it (a snapshot covers development); a metadata file to maintain; a check script to write at the skeleton.
- Impact on modules and APIs: none.
- Impact on tests and documentation: `REQ-DOC-005`, `REQ-DOC-006` added; issue template added; `api-index.md`, `game-development-guide.md`, `agent-workflow.md`, `GAME-AGENTS-TEMPLATE.md`, `roadmap.md` updated.

## Validation

Decisions confirmed by the project owner on 2026-11-23. Follow-ups: `OQ-157`.
