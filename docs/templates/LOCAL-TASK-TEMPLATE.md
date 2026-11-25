# Local task template

**Status: PROPOSED — template of the task file given to a local worker (`REQ-AGENT-002`, [`local-agents.md`](../local-agents.md)).**

Copy the block below into a file outside the committed tree (or where the owner asks), fill every field and pass it to `scripts/local-task.sh`. A field that cannot be filled means the task is not ready to be delegated.

```markdown
# Task: <short title>

- Slug: <lowercase letters, digits and hyphens; the branch is local/<slug>>
- Model profile: <PC1 | PC2 | PC3, see docs/local-agents.md>
- Requirements: <REQ-* identifiers>
- Decisions: <ADR-NNNN identifiers>

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
- <number> consecutive failed runs of the acceptance commands (OQ-171);
- <task-specific condition>
```
