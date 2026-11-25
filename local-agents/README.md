# Local agents: owner's guide

This folder is for **you, the owner**. It is not read by agents: a local worker follows [`docs/local-agent-entry.md`](../docs/local-agent-entry.md), and the rules, decisions and design are in [`docs/local-agents.md`](../docs/local-agents.md) and [ADR-0053](../docs/decisions/0053-local-agent-escalation-branch-hand-off-and-merge-gate.md). Here you only find what to do, in order, on your machines.

| File | What it is |
|---|---|
| `README.md` | This procedure. |
| `run-benchmark.sh` | Runs one benchmark run on one machine, or cleans it up. |
| `benchmark-task.template.md` | The benchmark task; the script fills in the slug and the machine profile. |

## What you are doing

The benchmark (`OQ-170`) answers one question per machine: **does the chosen local model do a small, real task correctly?** The task is to recreate `RandomExample` and its test from the API index and the Javadoc, after the script has deleted them in a private worktree. A run passes when the build of `lionengine-examples` and the four check scripts succeed (the gate), in at most 3 gate runs.

Nothing is pushed and nothing reaches GitHub: each run happens in its own git worktree, on a `local/bench-random-example-<machine>-<run>` branch that you delete afterwards.

Your three machines run **Windows 11**. The model server runs in a normal terminal (PowerShell), and everything else in this guide runs in **Git Bash** (installed with Git for Windows): open it from the Start menu, then `cd` to the repository.

Status: the scripts have been run end to end only on Linux, against a stub gateway that answers with text and never calls a tool, with a Windows-like setup simulated (a `python3` that fails like the Microsoft Store stub, task files with Windows line endings). **No real local model has run yet and no script has run on a real Windows machine yet**, so expect to adjust the gateway settings, and possibly the scripts, on the first run. If a script fails because of Windows, that is a defect to report, not something to work around by hand.

## Per machine, once

1. **Software.** Git for Windows (it provides Git Bash), JDK 25 and Maven 3.9 (both on the PATH of Git Bash, with `JAVA_HOME` set), Python 3 (`python3` itself may be missing or be the Microsoft Store stub: the script copes with that, but Python 3 must be installed), the Claude Code CLI (`claude --version` in Git Bash), and the Windows build of llama.cpp for NVIDIA GPUs (its server speaks the Anthropic Messages API). Once: `git config --global core.longpaths true`, and keep the repository in a short path such as `C:\dev\lionengine`.
2. **Clone and update the repository:** `git clone` it, or `git pull` on `rewrite/v11`. The script fetches `origin/rewrite/v11` itself before each run.
3. **Start the model server.** The exact command per machine (model, context size, how many MoE layers stay in RAM) is in [`docs/local-agents.md`](../docs/local-agents.md), section 5 and the table of section 1. Keep it running in its own PowerShell window (`llama-server.exe` takes the same arguments). Use `--jinja`: without it, tool calls fail.
4. **Point the shell at it**, in the Git Bash window where you will run the benchmark (not PowerShell: `export` is the Git Bash syntax):

   ```bash
   export ANTHROPIC_BASE_URL=http://127.0.0.1:8080
   export LOCAL_AGENT_MODEL=<the model name your server reports>   # only if it serves several
   ```

   The script refuses to start without `ANTHROPIC_BASE_URL`, and refuses any `anthropic.com` address: a benchmark can never spend cloud tokens. It sends a placeholder key, never a real one.

## Per run (3 runs per machine)

Run from the repository root, in Git Bash. Replace `pc1` with `pc2` or `pc3`.

```bash
bash local-agents/run-benchmark.sh --dry-run pc1 1   # check, changes nothing
bash local-agents/run-benchmark.sh pc1 1              # real run
bash local-agents/run-benchmark.sh pc1 2
bash local-agents/run-benchmark.sh pc1 3
```

Each real run builds Maven modules, so give it a few minutes. When it ends, it prints the report and its path: `../lionengine-local/bench-random-example-<machine>-<run>.report.md`, with logs next to it in a `.logs` folder.

### Reading the verdict

| Verdict | Meaning | What to do |
|---|---|---|
| `PASSED` | The gate succeeded within 3 gate runs and no file outside the scope changed. | Count it as a pass. |
| `ESCALATE` | The gate still failed after 3 gate runs, or a file outside the scope was changed. | Count it as a failure. Look at the report to see which command failed; the gate output is in `<slug>.logs/gate-<n>.log`. |
| `ANSWER READY` | PC3 only: a single-shot answer was saved, nothing has been checked yet. | See "PC3" below. |

The report also gives, per worker run, the number of turns, the duration and the failed tool calls. A high number of failed tool calls with a failing gate usually means a gateway problem (see "If something goes wrong") rather than a weak model.

### PC3 (single shot)

PC3 has no agent loop: the model gets the task and the current files once and answers once. The script saves the answer to `../lionengine-local/bench-random-example-pc3-<run>.answer.md`. Then ask Claude Code, with a cloud model, in a session that can reach that folder and the worktree `../lionengine-local/bench-random-example-pc3-<run>`:

> Apply `bench-random-example-pc3-<run>.answer.md` unchanged in the worktree `bench-random-example-pc3-<run>`, then run the gate of `local/bench-random-example-pc3-<run>` and tell me whether it passes.

"Unchanged" matters: if you or Claude Code fix the answer, the run no longer measures the model.

## Deciding

A model is **confirmed** for a machine when at least **2 of its 3 runs pass**, each in **under 30 minutes**. If not, run the same 3 runs again with the fallback model of that machine (table of section 1 of `docs/local-agents.md`; none for PC3). PC3's criterion is the same, counted on the answers applied unchanged.

## Recording the result

Fill the table of [`docs/local-agents.md`](../docs/local-agents.md), section 8, from the reports (model and quantization, gateway used, runs passed, gate runs per run, duration, failed tool calls). When all three machines are confirmed, `OQ-170` is resolved.

Easiest: tell Claude Code the numbers (or paste the three reports) and ask it to record the results and resolve `OQ-170`. Resolving a question needs a recorded decision (an ADR or a requirement) and a pull request, so do not edit `open-questions.md` by hand.

## Cleaning up

```bash
bash local-agents/run-benchmark.sh --clean pc1 1
```

This removes the worktree and the local branch of that run and keeps its report and logs. A benchmark branch is never pushed.

## If something goes wrong

| Symptom | Likely cause and fix |
|---|---|
| `ANTHROPIC_BASE_URL is not set` | The export of step 4 is missing in this Git Bash window. |
| `$'\r': command not found` | A script was checked out with Windows line endings (an old checkout, before `.gitattributes`). Clone the repository again after pulling the commit that adds `.gitattributes`. |
| `Python 3 not found` | None of `python3`, `python` and `py -3` runs Python 3. Install Python 3 and open a new Git Bash window. |
| `mvn: command not found`, or Maven cannot find Java | `JAVA_HOME` and the Maven `bin` folder are missing from the PATH of Git Bash. |
| `Filename too long`, or Maven fails on a very long path | Run `git config --global core.longpaths true` and move the repository to a shorter path. |
| `ANTHROPIC_BASE_URL must point to the local gateway` | The URL contains `anthropic.com`: it must be your local server. |
| `worktree or branch already exists` | A previous run with the same machine and run number was not cleaned: `--clean` it, or use another run number. |
| Many failed tool calls, the worker answers in plain text and never edits | The server was started without `--jinja`, or the gateway does not support tool calls. Restart it with `--jinja`, or try another Anthropic-compatible gateway. |
| Very slow turns after the first one | The prompt cache is not reused. The script already disables the Claude Code header that breaks it; also check that the server was not restarted between turns, and that the context size (`-c`) is not too small. |
| The first turn takes minutes | Can be normal on a large model with MoE layers in RAM: the first request must read the whole prompt. |
| Out-of-memory, or the model does not load | Lower the context size, or keep more MoE layers in RAM (`--n-cpu-moe`, section 5 of `docs/local-agents.md`). |
| `ESCALATE` with `Out of scope` listing files | The model edited files it was not allowed to. Count the run as failed. |
| Maven cannot resolve snapshots | Network access to the snapshot repository (ADR-0051); try again with the network up. |

## After the benchmark

Delegating a real task is a conversation with Claude Code, not a command you run yourself: Claude Code writes the task file, you run `scripts/local-task.sh` on the machine of the profile, and Claude Code reviews the branch, opens the pull request and adds the label `cloud-reviewed` once it approves it. The checklist is [`docs/local-agents.md`](../docs/local-agents.md), section 9; the merge gate is section 7.
