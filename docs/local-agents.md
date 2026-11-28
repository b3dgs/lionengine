# Local agents: delegation, models, task files and merge gate

**Status: VALIDATED — decisions 1A, 2C, 3B, 4A and 5A of the packet of issue #910 (owner comment of 2026-11-25) and the proposal of this page, validated by the owner on 2026-11-25; escalation, hand-off and merge gate in [ADR-0053](decisions/0053-local-agent-escalation-branch-hand-off-and-merge-gate.md) (ACCEPTED). Requirements `REQ-AGENT-001` to `REQ-AGENT-004` are VALIDATED. `OQ-170` (benchmark results of the models) is open; the Windows 11 scripts are verified by simulation only, not yet on a real Windows machine.**

This page extends [`agent-workflow.md`](agent-workflow.md): Claude Code (cloud model) delegates token-heavy, well-specified work to local models on the owner's machines, reviews the result, and keeps high-reasoning work in the cloud. Requirements: `REQ-AGENT-001` to `REQ-AGENT-004` in [`requirements.md`](requirements.md).

## 1. Machines and model profiles

All three machines run **Windows 11** (owner, 2026-11-25); the scripts run in Git Bash (section 5). Decision 5A. The models are the owner's delegated choice; they are confirmed (or replaced) by the benchmark of section 8 (`OQ-170`). Until then they are a starting profile, not a guarantee.

| Machine | GPU (VRAM) | RAM | Model | Fallback | Mode |
|---|---|---|---|---|---|
| PC1 | RTX 5070 Ti (16 GB) | 64 GB | Qwen3.6-35B-A3B, 4-bit (Q4_K_M), the MoE experts that do not fit in VRAM held in RAM, 64K context | gpt-oss-20b | Agent loop |
| PC2 | RTX 4070 (12 GB) | 32 GB | Qwen3.6-35B-A3B, 4-bit, more MoE experts in RAM than on PC1, 64K context | Qwen3-Coder-30B-A3B, 4-bit | Agent loop |
| PC3 | RTX 2060 (8 GB) | 16 GB | Qwen3.5-9B, 4-bit (Q4_K_M), 16K context | none | Single-shot jobs only (Javadoc, documentation); no agent loop |

Hardware confirmed by the owner on 2026-11-25: the RTX 4070 has 12 GB of VRAM (32 GB of RAM) and the RTX 2060 has 8 GB of VRAM (16 GB of RAM). The PC3 profile stays single-shot: a task given to PC3 is one prompt and one answer applied by Claude Code, never a multi-step tool loop. The packet named "Qwen3-Coder 8B" for PC3, which is not a published model; Qwen3.5-9B replaces it (ADR-0053).

## 2. What is delegated (decision 2C)

1. **First**: examples, API-index rows and Javadoc for an API that already landed.
2. **Then**, once the first stage has passed the benchmark of `OQ-170`: bounded batches, starting with Resources batch 4 ([`features/resources.md`](features/resources.md)); Resources batch 3 and Game loop batch 4 were done by a cloud agent on 2026-11-25 and 2026-11-26.

Delegation criteria (all must hold):

- the work is described by a batch page or by an existing contract, with every default value stated (an agent never chooses a value a document leaves unstated);
- the scope is one module, a few files, and fits the context of the profile;
- the acceptance is a list of commands that a machine can run (section 4);
- no `TO BE DEFINED` point, no architecture decision, no public API that no `VALIDATED` contract describes.

Never delegated: ADRs, answers to open questions, cross-module consistency reviews, anything touching the game-loop invariant without a batch page, `.github/workflows/`, and anything on `rewrite/v11` directly.

## 3. Entry file for small contexts

[`local-agent-entry.md`](local-agent-entry.md) is the compact entry file of a local worker (`REQ-AGENT-003`). It lists the rules in a few lines and links to the sections of [`AGENTS.md`](../AGENTS.md) instead of copying them, so a rule has a single source. The script gives it to the worker as its system prompt addition and runs Claude Code in minimal mode (`--bare`), so `CLAUDE.md` is not loaded automatically: the worker reads `AGENTS.md` only through the links it needs.

## 4. Task file

A task is a Markdown file written by Claude Code from [`templates/LOCAL-TASK-TEMPLATE.md`](templates/LOCAL-TASK-TEMPLATE.md) (`REQ-AGENT-002`): the batch page, the files in scope, the acceptance commands and the stop conditions. The task file is the hand-off: the worker relies on no conversation history. Task files are working material for the delegation and are not committed unless the owner asks; the benchmark task of section 8 is the exception.

The script checks the task file before it starts: a slug, a profile `PC1`, `PC2` or `PC3`, at least one file in scope (no absolute path, no `..`, nothing under `.github/` or `docs/decisions/`), and acceptance commands limited to `mvn -B ...` and `python3 -I scripts/check-*.py`, with no shell character and no skip flag. The commands are run without a shell.

## 5. Setting up a machine

Done once per machine by the owner. Nothing here is committed: no key, no address.

1. **Gateway.** The install from scratch (tools, a prebuilt or compiled llama.cpp, the model files with their download links, the `llama-server` command of each machine) is in one place, [`../local-agents/INSTALL.md`](../local-agents/INSTALL.md); this page does not repeat the commands. llama.cpp's server speaks the Anthropic Messages API (since January 2026); `--jinja` is needed for tool calls. Another gateway with an Anthropic-compatible endpoint (Ollama, LM Studio, vLLM) is acceptable if it handles tool calls; record which one in the benchmark results.
2. **Claude Code CLI** installed (`claude --version`), and Git, JDK 25, Maven 3.9 and Python 3 as for any build ([`implementation-guide.md`](implementation-guide.md)); all of it is covered by `INSTALL.md` on Windows.
3. **Environment of the shell that runs the script**: `ANTHROPIC_BASE_URL=http://127.0.0.1:8080` and, if the gateway serves several models, `LOCAL_AGENT_MODEL=<model name>`. The script refuses to start without `ANTHROPIC_BASE_URL` or with an `anthropic.com` address, gives the worker a placeholder key (`local-gateway`) and unsets the other provider variables, so a local run can never spend cloud tokens or send a real key to the gateway.
4. **Prompt cache.** The script disables the attribution block of Claude Code (`CLAUDE_CODE_ATTRIBUTION_HEADER=0`, through `--settings`), which changes at every request and otherwise forces the local server to recompute the whole prompt each turn.

**Windows 11 (the three machines).** The scripts are Bash scripts and run in **Git Bash**, which comes with Git for Windows (Claude Code uses it for its Bash tool, and falls back to a PowerShell tool without it, which the worker's command allow-list does not cover). Differences from Linux:

- **Two terminals.** The model server runs in PowerShell (or any terminal) with the Windows build of llama.cpp (the CUDA 13 build for the NVIDIA GPUs, `llama-server.exe`). The scripts run in a Git Bash terminal, where the variables are set with `export`, not with `$env:`.
- **Line endings.** `.gitattributes` forces Unix line endings on `*.sh`, so the scripts work whatever `core.autocrlf` is. A task file with Windows line endings is accepted. If a script of an older checkout fails with `\r: command not found`, check it out again.
- **Python.** The gate and the worker call `python3 -I scripts/check-*.py`. On Windows `python3` is often missing or the Microsoft Store stub: when it does not run Python 3, `scripts/local-task.sh` finds `python` or `py -3` and puts a `python3` wrapper first in the PATH of the script and of the worker, so nothing else has to be installed or aliased. Python 3 itself must be installed.
- **Long paths.** Maven build folders inside `../lionengine-local/<slug>` can pass the 260-character path limit of Windows: run `git config --global core.longpaths true` once, and keep the repository in a short path (for example `C:\dev\lionengine`).
- **Java and Maven.** JDK 25 and Maven 3.9 on the PATH of Git Bash, with `JAVA_HOME` set; `mvn` is then the `mvn` shell script of the Maven distribution.
- **No display tool.** The gate of the benchmark does not run the platform tests, so no virtual display is needed.

Status of this support: simulated on Linux (a `python3` that fails like the Store stub, a task file with Windows line endings, a full run against a stub gateway); **not yet run on a real Windows 11 machine**. The first benchmark run on PC1 doubles as that check: a Windows-specific failure is reported as a defect of the script, not worked around by hand.

## 6. Running a worker

Decision 4A. `scripts/local-task.sh <task-file>` fetches `rewrite/v11` from `origin`, creates a git worktree of its own (`../lionengine-local/<slug>`) on a branch `local/<slug>` cut from it, and runs the worker there:

- **Agent loop (PC1, PC2).** A headless `claude -p --bare` with the tools `Read`, `Edit`, `Write`, `Glob`, `Grep` and `Bash`, an explicit allow-list (Maven in batch mode, the four checks, `git status`, `diff`, `add`, `commit`, `log`), a deny-list (`git push`, `checkout`, `switch`, `reset`, `rebase`, `worktree`, web tools), the permission mode `dontAsk` (anything not allowed is refused, never asked), no MCP server and at most 60 turns. Then the gate runs: the Maven acceptance commands of the task file, a compilation of the whole reactor (`mvn -B -ntp -q test-compile`, because `check-api-index.py` reads the compiled classes of every module), then the check commands of the task file and the four structural checks. A failed gate gives the worker its log and another run, at most 3 gate runs in all (section 7).
- **Single shot (PC3).** One prompt: the task file and the current content of the files in scope; no tool; the entry file as the whole system prompt. The answer is saved to `../lionengine-local/<slug>.answer.md`; Claude Code applies it in the worktree and runs the gate itself.

The script writes a report, `../lionengine-local/<slug>.report.md`: worker runs with turns, duration and failed tool calls, each gate command and its result, files changed, files out of scope, uncommitted entries, and a verdict: `PASSED`, `ESCALATE` or, for a single shot, `ANSWER READY`. Logs (the worker's stream and the gate output) are in `../lionengine-local/<slug>.logs/`. `scripts/local-task.sh --dry-run <task-file>` validates the task file, prints what it would run and changes nothing.

Rules of the script: it contains no secret and no permission-skip flag; the worker works only in its worktree and never on `rewrite/v11`; the script refuses to reuse an existing worktree or branch; it does not push and does not open a pull request. Clean up after the review with `git worktree remove ../lionengine-local/<slug>` and `git branch -D local/<slug>`.

## 7. Escalation, hand-off and merge gate (ADR-0053)

**Escalation.** A task returns to a cloud agent, with its branch and report, when:

- a stop condition of the task file is met (for example a `TO BE DEFINED` point, a needed public API without a validated contract, an Android/DEX doubt);
- the gate still fails after 3 gate runs;
- the worker changed a file outside the scope of the task file.

The worker records a gap in its report and stops that part of the work; Claude Code records it in [`open-questions.md`](open-questions.md) if it is one; nobody decides it locally (`agent-workflow.md`, "Rules that never depend on the agent"). An escalated task is not retried locally.

**Hand-off.** The worker holds no GitHub credential and no signing key. Claude Code, on the owner's machine with a cloud model, reads the report and the diff (`git -C ../lionengine-local/<slug> diff origin/rewrite/v11...`), fixes nothing silently, then pushes the same branch `local/<slug>` and opens the pull request into `rewrite/v11`. The commits of the branch need no signature: only the squash commit reaches `rewrite/v11`, and GitHub signs it.

**Merge gate** (decision 3B, `REQ-AGENT-004`). A `local/` pull request is not auto-merged until a cloud review has approved it:

1. Claude Code with a cloud model (or the owner), not the worker, reviews the diff against the task file, the batch page and [`local-agent-entry.md`](local-agent-entry.md).
2. It posts its review as a comment and, if it approves, adds the label `cloud-reviewed` from an allow-listed account (`claude[bot]`, `DjThunder`, `b3dgs`).
3. The label starts `.github/workflows/auto-merge-agent-prs.yml`, which runs the same checks as for the other agent pull requests (documentation, build and tests on the merge result, the `linux` check, with its Sonar quality gate, and the `android` check of the head commit) and squash-merges.
4. Any push to the pull request removes the label: a new review is needed. Local work is never re-pushed under `claude/` or `packet/`, which would bypass the review.

## 8. Benchmark (`OQ-170`)

The benchmark task is [`local-tasks/benchmark-random-example.md`](local-tasks/benchmark-random-example.md): the script removes `RandomExample` and its test from the worktree (field `Remove before start`, used only by benchmarks) and the worker recreates them from the API index and the Javadoc; the gate is the build of `lionengine-examples` and the API-index check.

The owner's step-by-step guide is [`../local-agents/README.md`](../local-agents/README.md); `../local-agents/run-benchmark.sh <pc1|pc2|pc3> <run>` fills the slug and the profile of the task for each run. On each machine, run it 3 times, then delete the branches: a benchmark branch is never pushed. A model is confirmed when at least 2 runs end `PASSED` in under 30 minutes each; otherwise benchmark the fallback. For PC3, copy the task with `Model profile: PC3`; Claude Code applies each answer unchanged and runs the gate; 2 of 3 passing answers confirm the model.

Results (fill from the reports; `OQ-170` is resolved when every machine has a confirmed model):

| Machine | Model and quantization | Gateway | Runs passed | Gate runs per run | Duration per run | Failed tool calls | Confirmed |
|---|---|---|---|---|---|---|---|
| PC1 | | | | | | | |
| PC2 | | | | | | | |
| PC3 | | | | | | | |

## 9. Checklist for Claude Code delegating a task

1. The task meets the criteria of section 2, and the benchmark of the profile has passed (until then, only the benchmark is run).
2. Write the task file from the template; run `scripts/local-task.sh --dry-run <task-file>`.
3. Ask the owner to run `scripts/local-task.sh <task-file>` on the machine of the profile (or run it there when Claude Code runs on that machine).
4. Read the report. `ESCALATE`: take the task back. `PASSED` or an applied `ANSWER READY`: review, push `local/<slug>`, open the pull request, review it as in section 7.
