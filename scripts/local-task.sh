#!/usr/bin/env bash
# Run one local worker on a task file, in its own git worktree (docs/local-agents.md, REQ-AGENT-002, ADR-0053).
# Linux, macOS and Windows 11 (Git Bash of Git for Windows): see docs/local-agents.md, section 5.
#
# Usage (from the repository root):
#   scripts/local-task.sh [--dry-run] <task-file>
#
# The task file follows docs/templates/LOCAL-TASK-TEMPLATE.md. The script reads its fields "Slug",
# "Model profile" and "Remove before start", and its sections "Files in scope" and "Acceptance commands".
#
# Environment (set by the machine profile, never by this script; see docs/local-agents.md, section 5):
#   ANTHROPIC_BASE_URL  required: the local Anthropic-compatible gateway, for example http://127.0.0.1:8080
#   LOCAL_AGENT_MODEL   optional: the model name passed to the gateway (--model)
#
# Profiles PC1 and PC2 run an agent loop: the worker edits and commits in the worktree, then the script runs
# the gate (the acceptance commands and the four structural checks). A failed gate gives the worker its log and
# another run, up to MAX_GATE_RUNS gate runs in all; then the task is escalated to a cloud agent (ADR-0053).
# Profile PC3 is single-shot: one prompt with the task and the files in scope, no tool; the answer is saved for
# Claude Code to apply.
#
# The script holds no secret (the worker receives a placeholder key that only the local gateway sees), uses no
# permission-skip flag, never works on rewrite/v11, never pushes and never opens a pull request.
set -euo pipefail

readonly MAX_GATE_RUNS=3
readonly MAX_TURNS=60
readonly BASE_BRANCH=rewrite/v11
readonly CHECKS=(check-docs check-modules check-api-index check-provenance)

dry_run=0
if [ "${1:-}" = "--dry-run" ]; then
  dry_run=1
  shift
fi
if [ "$#" -ne 1 ]; then
  echo "usage: scripts/local-task.sh [--dry-run] <task-file>" >&2
  exit 2
fi

fail() {
  echo "error: $*" >&2
  exit 2
}

# mapfile and empty arrays under "set -u" need Bash 4.4: Git Bash and Linux have it, macOS ships 3.2.
if [ "${BASH_VERSINFO[0]}" -lt 4 ] || { [ "${BASH_VERSINFO[0]}" -eq 4 ] && [ "${BASH_VERSINFO[1]}" -lt 4 ]; }; then
  fail "Bash 4.4 or newer is needed, this is $BASH_VERSION (macOS: install a recent bash, for example with Homebrew)"
fi

task_file=$1
repo_root=$(git rev-parse --show-toplevel)
[ -f "$task_file" ] || fail "task file not found: $task_file"
task_file=$(cd "$(dirname "$task_file")" && pwd)/$(basename "$task_file")

# Value of a "- Name: value" field of the task file.
field() {
  sed -n "s/^- $1: *//p" "$task_file" | head -n 1 | sed 's/[[:space:]]*$//'
}

# Items ("- item") of a "## Title" section of the task file, without backquotes.
section_items() {
  # The task file may have Windows line endings: the carriage returns are dropped first.
  tr -d '\r' < "$task_file" | awk -v title="## $1" '
    $0 == title { inside = 1; next }
    /^## / { inside = 0 }
    inside && /^- / { sub(/^- /, ""); gsub(/`/, ""); print }
  ' | sed 's/[[:space:]]*$//'
}

slug=$(field Slug | tr -d '[:space:]')
printf '%s' "$slug" | grep -Eq '^[a-z0-9][a-z0-9-]{0,40}$' \
  || fail "the task file needs a '- Slug:' line with lowercase letters, digits and hyphens"

profile=$(field "Model profile" | tr -d '[:space:]')
case "$profile" in
  PC1|PC2) mode=loop ;;
  PC3) mode=single ;;
  *) fail "the task file needs a '- Model profile:' line with PC1, PC2 or PC3" ;;
esac

mapfile -t scope < <(section_items "Files in scope")
[ "${#scope[@]}" -gt 0 ] || fail "the section '## Files in scope' lists no file"
for path in "${scope[@]}"; do
  case "$path" in
    /*|*..*|.github/*|docs/decisions/*) fail "file out of the delegable scope: $path" ;;
  esac
done

# Acceptance commands: only Maven in batch mode and the structural checks; run without a shell.
mapfile -t acceptance < <(section_items "Acceptance commands")
for cmd in "${acceptance[@]}"; do
  case "$cmd" in
    *[\;\&\|\$\<\>\`\\]*) fail "acceptance command with a shell character: $cmd" ;;
  esac
  printf '%s' "$cmd" | grep -Eq '^(mvn -B( [A-Za-z0-9:_.,/=@-]+)*|python3 -I scripts/check-[a-z-]+\.py)$' \
    || fail "acceptance command not allowed (only 'mvn -B ...' and 'python3 -I scripts/check-*.py'): $cmd"
  case "$cmd" in
    *skip*|*-Dmaven.test.failure.ignore*|*-fae*|*--fail-never*) fail "skip flags are forbidden: $cmd" ;;
  esac
done

# Gate order: the Maven acceptance commands, then a compilation of the whole reactor (check-api-index.py reads the
# compiled classes of every module, test classes included), then the checks, each once.
gate_cmds=()
for cmd in "${acceptance[@]}"; do
  case "$cmd" in mvn*) gate_cmds+=("$cmd") ;; esac
done
gate_cmds+=("mvn -B -ntp -q test-compile")
for cmd in "${acceptance[@]}"; do
  case "$cmd" in python3*) gate_cmds+=("$cmd") ;; esac
done
for check in "${CHECKS[@]}"; do
  case " ${acceptance[*]} " in
    *"scripts/$check.py"*) ;;
    *) gate_cmds+=("python3 -I scripts/$check.py") ;;
  esac
done

remove_first=$(field "Remove before start")
read -r -a remove_paths <<< "${remove_first//,/ }"

branch="local/$slug"
work_root="$(dirname "$repo_root")/$(basename "$repo_root")-local"
worktree="$work_root/$slug"
logs="$work_root/$slug.logs"
report="$work_root/$slug.report.md"
entry="$repo_root/docs/local-agent-entry.md"
[ -f "$entry" ] || fail "missing $entry"

tools='Read,Edit,Write,Glob,Grep,Bash'
allowed_tools='Read,Edit,Write,Glob,Grep,Bash(mvn -B:*),Bash(python3 -I scripts/check-docs.py),Bash(python3 -I scripts/check-modules.py),Bash(python3 -I scripts/check-api-index.py),Bash(python3 -I scripts/check-provenance.py),Bash(git status:*),Bash(git diff:*),Bash(git add:*),Bash(git commit:*),Bash(git log:*)'
disallowed_tools='Bash(git push:*),Bash(git checkout:*),Bash(git switch:*),Bash(git reset:*),Bash(git rebase:*),Bash(git worktree:*),WebFetch,WebSearch'
# The attribution block changes at every request and would defeat the prompt cache of a local server.
settings='{"env":{"CLAUDE_CODE_ATTRIBUTION_HEADER":"0","CLAUDE_CODE_DISABLE_NONESSENTIAL_TRAFFIC":"1"}}'
model_args=()
if [ -n "${LOCAL_AGENT_MODEL:-}" ]; then
  model_args=(--model "$LOCAL_AGENT_MODEL")
  # The background requests of Claude Code use the small model: the gateway serves the same one.
  export ANTHROPIC_DEFAULT_HAIKU_MODEL="$LOCAL_AGENT_MODEL"
fi

if [ "$dry_run" -eq 1 ]; then
  echo "dry run: nothing is changed"
  echo "task:      $task_file"
  echo "profile:   $profile ($mode)"
  echo "worktree:  $worktree"
  echo "branch:    $branch (from origin/$BASE_BRANCH, fetched first)"
  echo "gateway:   ${ANTHROPIC_BASE_URL:-<not set: a real run refuses to start>}"
  echo "model:     ${LOCAL_AGENT_MODEL:-<gateway default>}"
  echo "scope:     ${scope[*]}"
  [ "${#remove_paths[@]}" -gt 0 ] && echo "removed before start (benchmark): ${remove_paths[*]}"
  if [ "$mode" = loop ]; then
    echo "worker:    claude -p --bare --tools '$tools' --allowedTools '$allowed_tools' --disallowedTools '$disallowed_tools' --max-turns $MAX_TURNS"
    echo "gate:      at most $MAX_GATE_RUNS gate runs, then escalation:"
    for cmd in "${gate_cmds[@]}"; do echo "           $cmd"; done
  else
    echo "worker:    claude -p --bare --tools '' (single shot), answer saved to $work_root/$slug.answer.md"
  fi
  echo "report:    $report"
  exit 0
fi

# Windows: "python3" is often missing or the Microsoft Store stub. The gate and the worker call "python3 -I ...";
# when it does not run Python 3, a wrapper named python3 is put first in the PATH of this script and its children.
python_ok() {
  "$@" -c 'import sys; sys.exit(0 if sys.version_info[0] == 3 else 1)' >/dev/null 2>&1
}
if ! python_ok python3; then
  python_cmd=
  for candidate in python "py -3"; do
    # shellcheck disable=SC2086
    if python_ok $candidate; then python_cmd=$candidate; break; fi
  done
  [ -n "$python_cmd" ] || fail "Python 3 not found: install it and make 'python3', 'python' or 'py -3' work"
  shim_dir=$(mktemp -d)
  trap 'rm -rf "$shim_dir"' EXIT
  printf '#!/usr/bin/env bash\nexec %s "$@"\n' "$python_cmd" > "$shim_dir/python3"
  chmod +x "$shim_dir/python3"
  export PATH="$shim_dir:$PATH"
fi

# A real run reaches only a local gateway: never the account of the machine, never a cloud key.
[ -n "${ANTHROPIC_BASE_URL:-}" ] || fail "ANTHROPIC_BASE_URL is not set: start the local gateway of the machine profile first"
case "$ANTHROPIC_BASE_URL" in
  *anthropic.com*) fail "ANTHROPIC_BASE_URL must point to the local gateway, not to $ANTHROPIC_BASE_URL" ;;
esac
command -v claude >/dev/null 2>&1 || fail "the claude CLI is not installed"
if [ -e "$worktree" ] || git -C "$repo_root" show-ref --verify --quiet "refs/heads/$branch"; then
  fail "worktree or branch already exists: $worktree / $branch (remove them or choose another slug)"
fi

git -C "$repo_root" fetch --quiet origin "$BASE_BRANCH"
mkdir -p "$work_root" "$logs"
git -C "$repo_root" worktree add --quiet -b "$branch" "$worktree" FETCH_HEAD
cd "$worktree"
[ "$(git rev-parse --abbrev-ref HEAD)" = "$branch" ] || fail "the worktree is not on $branch"
base_commit=$(git rev-parse HEAD)

if [ "${#remove_paths[@]}" -gt 0 ]; then
  git rm --quiet -- "${remove_paths[@]}"
  git commit --quiet -m "Benchmark setup: remove ${remove_paths[*]}"
  base_commit=$(git rev-parse HEAD)
fi

# The worker never receives a real key: the placeholder is sent only to the local gateway.
export ANTHROPIC_API_KEY=local-gateway
unset ANTHROPIC_AUTH_TOKEN CLAUDE_CODE_USE_BEDROCK CLAUDE_CODE_USE_VERTEX CLAUDE_CODE_USE_FOUNDRY

# Metrics of a stream-json log: turns, duration and failed tool calls.
metrics() {
  python3 -I - "$1" <<'EOF'
import json, sys
turns, duration, tool_errors, error = 0, 0, 0, False
with open(sys.argv[1], encoding="utf-8", errors="replace") as handle:
    for line in handle:
        try:
            event = json.loads(line)
        except ValueError:
            continue
        if event.get("type") == "result":
            turns = event.get("num_turns", 0)
            duration = event.get("duration_ms", 0)
            error = event.get("is_error", False)
        elif event.get("type") == "user":
            content = event.get("message", {}).get("content", [])
            if isinstance(content, list):
                tool_errors += sum(1 for item in content
                                   if isinstance(item, dict) and item.get("type") == "tool_result"
                                   and item.get("is_error"))
print(f"turns {turns}, {duration / 1000:.0f} s, failed tool calls {tool_errors}, error {str(error).lower()}")
EOF
}

started=$(date +%s)
{
  echo "# Local task report: $slug"
  echo
  echo "- Task file: $task_file"
  echo "- Profile: $profile ($mode), model ${LOCAL_AGENT_MODEL:-gateway default}, gateway $ANTHROPIC_BASE_URL"
  echo "- Branch: $branch from $base_commit (not pushed)"
} > "$report"

if [ "$mode" = single ]; then
  prompt_file="$logs/prompt.md"
  {
    echo "Perform the task below. Answer with the complete content of every file you create or change, each in a"
    echo "fenced block preceded by its path, then your report. You cannot run commands."
    echo
    cat "$task_file"
    for path in "${scope[@]}"; do
      if [ -f "$path" ]; then
        echo
        echo "Current content of $path:"
        echo '```'
        cat "$path"
        echo '```'
      fi
    done
  } > "$prompt_file"
  status=0
  claude -p --bare --tools "" --strict-mcp-config --system-prompt-file "$entry" --settings "$settings" \
    --max-turns 1 --output-format stream-json --verbose "${model_args[@]}" < "$prompt_file" \
    > "$logs/worker-1.jsonl" || status=$?
  python3 -I - "$logs/worker-1.jsonl" > "$work_root/$slug.answer.md" <<'EOF'
import json, sys
for line in open(sys.argv[1], encoding="utf-8", errors="replace"):
    try:
        event = json.loads(line)
    except ValueError:
        continue
    if event.get("type") == "result":
        print(event.get("result", ""))
EOF
  {
    echo "- Worker: exit $status, $(metrics "$logs/worker-1.jsonl"), $(( $(date +%s) - started )) s in all"
    echo "- Answer: $work_root/$slug.answer.md, to be applied by Claude Code in $worktree, then the gate is run"
    echo "- Verdict: ANSWER READY"
  } >> "$report"
  cat "$report"
  exit "$status"
fi

prompt="Follow the rules of your system prompt, then perform the task file $task_file. Work only in the current directory and commit your work on the current branch."
gate_runs=0
verdict=ESCALATE
while [ "$gate_runs" -lt "$MAX_GATE_RUNS" ]; do
  run=$(( gate_runs + 1 ))
  status=0
  claude -p --bare --tools "$tools" --allowedTools "$allowed_tools" --disallowedTools "$disallowed_tools" \
    --permission-mode dontAsk --strict-mcp-config --append-system-prompt-file "$entry" --settings "$settings" \
    --max-turns "$MAX_TURNS" --output-format stream-json --verbose "${model_args[@]}" "$prompt" \
    > "$logs/worker-$run.jsonl" || status=$?
  echo "- Worker run $run: exit $status, $(metrics "$logs/worker-$run.jsonl")" >> "$report"

  gate_runs=$run
  gate_log="$logs/gate-$run.log"
  : > "$gate_log"
  gate_ok=1
  for cmd in "${gate_cmds[@]}"; do
    read -r -a words <<< "$cmd"
    if "${words[@]}" >> "$gate_log" 2>&1; then
      echo "  - gate run $run: $cmd passed" >> "$report"
    else
      echo "  - gate run $run: $cmd FAILED" >> "$report"
      gate_ok=0
    fi
  done
  if [ "$gate_ok" -eq 1 ]; then
    verdict=PASSED
    break
  fi
  prompt="The gate failed after your previous run (run $run of $MAX_GATE_RUNS). Its log is $gate_log; read its end, fix the cause within the files in scope and commit. Follow the rules of your system prompt and the task file $task_file. Work only in the current directory."
done

# Files changed outside the scope of the task file, committed or not.
mapfile -t changed < <( { git diff --name-only "$base_commit" HEAD; git status --porcelain | cut -c4-; } | sort -u)
out_of_scope=()
for path in "${changed[@]}"; do
  [ -n "$path" ] || continue
  allowed=0
  for item in "${scope[@]}"; do
    if [ "$path" = "$item" ] || { [ "${item%/}" != "$item" ] && [ "${path#"$item"}" != "$path" ]; }; then
      allowed=1
    fi
  done
  [ "$allowed" -eq 1 ] || out_of_scope+=("$path")
done
uncommitted=$(git status --porcelain | wc -l)
[ "${#out_of_scope[@]}" -eq 0 ] || verdict=ESCALATE

{
  echo "- Gate runs: $gate_runs of at most $MAX_GATE_RUNS"
  echo "- Files changed: ${changed[*]:-none}"
  echo "- Out of scope: ${out_of_scope[*]:-none}"
  echo "- Uncommitted entries: $uncommitted"
  echo "- Duration: $(( $(date +%s) - started )) s"
  echo "- Logs: $logs"
  echo "- Verdict: $verdict"
} >> "$report"
cat "$report"
[ "$verdict" = PASSED ]
