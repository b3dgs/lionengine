# Local task template

**Status: VALIDATED (owner, 2026-11-25) — template of the task file given to a local worker (`REQ-AGENT-002`, [`local-agents.md`](../local-agents.md), [ADR-0053](../decisions/0053-local-agent-escalation-branch-hand-off-and-merge-gate.md)).**

Copy the block below into a file outside the committed tree (or where the owner asks), fill every field and pass it to `scripts/local-task.sh`; check it first with `scripts/local-task.sh --dry-run <task-file>`. A field that cannot be filled means the task is not ready to be delegated.

The script reads the fields `Slug`, `Model profile` and `Remove before start` and the sections `Files in scope` and `Acceptance commands` (one item per line, starting with `- `). Files in scope are paths relative to the repository root; a path ending with `/` covers a directory. Acceptance commands are limited to `mvn -B ...` and `python3 -I scripts/check-*.py`; a compilation of the whole reactor and the four checks are always added to the gate. The escalation threshold (3 gate runs) is fixed by ADR-0053, not by the task file.

```markdown
# Task: <short title>

- Slug: <lowercase letters, digits and hyphens; the branch is local/<slug>>
- Model profile: <PC1 | PC2 | PC3, see docs/local-agents.md>
- Requirements: <REQ-* identifiers>
- Decisions: <ADR-NNNN identifiers>
- Remove before start: <benchmarks only: comma-separated paths removed in the worktree before the worker starts; delete this line otherwise>

## Batch file
<link to the page in docs/features/ or to the contract the task implements>

## Files in scope
- <path that may be created or edited>
Anything else is out of scope.

## Steps
1. <small, ordered step>

## Acceptance commands
- <exact command, for example: mvn -B -pl lionengine-core -am verify>
- python3 -I scripts/check-docs.py

## Stop conditions
- a TO BE DEFINED point, a missing default value or a missing contract;
- a file outside the scope would have to change;
- 3 failed gate runs (ADR-0053; counted by the script);
- <task-specific condition>
```
