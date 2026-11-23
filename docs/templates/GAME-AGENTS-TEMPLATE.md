# Template: `AGENTS.md` for a game repository built on LionEngine

**Status: PROPOSED — copy this into the root `AGENTS.md` of the game repository, then fill the placeholders. The game repository consumes the engine at a pinned Maven version (ADR-0023).**

---

# Instructions for AI agents — `<game name>`

This repository is a game built on LionEngine `<engine version>`. The engine lives in `<engine repository>`; its architecture rules are in that repository's `AGENTS.md` and `docs/`. This repository follows the same conventions (English, status vocabulary, `REQ-*`/`ADR-*` traceability), with its own requirements and decisions.

## 1. Source of truth

1. This file.
2. The game design document: `<path>` (scope, rules, content).
3. `docs/decisions/` of this repository.
4. For anything about the engine: the engine's `docs/game-development-guide.md`, `docs/api-index.md`, the Javadoc and the `examples` module, at the pinned engine version.

## 2. Using the engine

- Use **only** classes and methods you found in the engine's code, sources/Javadoc artifacts or API index. Never rely on your knowledge of other engines or of earlier LionEngine versions; their APIs differ.
- Start from the closest engine example and copy its structure.
- After each change: compile, run the headless tests, and run the game scenario if one exists. A method that does not compile does not exist: search for the right one, do not invent a replacement.
- Respect engine conventions: world in meters, time in seconds, speeds in m/s; pixels only at presentation.
- If the engine lacks a needed capability, **do not patch around it in the game and do not edit the engine from a game task**. Write an *engine change request* (`docs/engine-requests.md`: need, current workaround, proposed contract) and continue with the smallest reversible workaround, clearly marked.

## 3. Game rules

- One batch per change set; reference the requirement or design item in commits.
- Game logic is testable headless (no window, no audio device) whenever possible.
- Assets: lowercase names with hyphens; a provenance metadata file per asset or folder (source, tool and model if generated, license, author); format fixed per asset kind when the loader exists (ADR-0023).
- Do not decide game design questions silently; record them in the design document as `TO BE DEFINED`.

## 4. End of task

Same report as the engine `AGENTS.md`: summary, requirements/design items covered, files changed, tests run and results, limits, engine change requests raised, questions remaining.
