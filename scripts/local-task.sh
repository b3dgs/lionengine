#!/usr/bin/env bash
# Run one local worker on a task file, in its own git worktree (docs/local-agents.md, REQ-AGENT-001).
#
# Usage (from the repository root):
#   scripts/local-task.sh [--dry-run] <task-file>
#
# The task file follows docs/templates/LOCAL-TASK-TEMPLATE.md; this script reads its "Slug" field.
# The model is reached through a local Anthropic-compatible gateway configured by the environment of
# the machine (ANTHROPIC_BASE_URL and the model variables), never by this script. The script holds no
# secret, uses no permission-skip flag, never works on rewrite/v11, and never pushes.
set -euo pipefail

dry_run=0
if [ "${1:-}" = "--dry-run" ]; then
  dry_run=1
  shift
fi
if [ "$#" -ne 1 ]; then
  echo "usage: scripts/local-task.sh [--dry-run] <task-file>" >&2
  exit 2
fi

task_file=$1
repo_root=$(git rev-parse --show-toplevel)
base_branch=rewrite/v11

[ -f "$task_file" ] || { echo "error: task file not found: $task_file" >&2; exit 2; }
task_file=$(cd "$(dirname "$task_file")" && pwd)/$(basename "$task_file")

slug=$(sed -n 's/^- Slug: *//p' "$task_file" | head -n 1 | tr -d '[:space:]')
if ! printf '%s' "$slug" | grep -Eq '^[a-z0-9][a-z0-9-]{0,40}$'; then
  echo "error: the task file needs a '- Slug:' line with lowercase letters, digits and hyphens" >&2
  exit 2
fi

branch="local/$slug"
worktree="$(dirname "$repo_root")/$(basename "$repo_root")-local/$slug"
entry="$repo_root/docs/local-agent-entry.md"
[ -f "$entry" ] || { echo "error: missing $entry" >&2; exit 2; }

allowed_tools='Read,Edit,Write,Glob,Grep,Bash(mvn:*),Bash(python3 -I scripts/check-*.py:*),Bash(git status:*),Bash(git diff:*),Bash(git add:*),Bash(git commit:*)'
prompt="Follow docs/local-agent-entry.md, then perform the task file $task_file. Work only in the current directory."

if [ "$dry_run" -eq 1 ]; then
  echo "dry run: nothing is changed"
  echo "worktree: $worktree"
  echo "branch:   $branch (from $base_branch)"
  echo "would run: git worktree add -b $branch $worktree $base_branch"
  echo "would run in the worktree: claude -p <prompt> --allowedTools '$allowed_tools'"
  echo "would run in the worktree: the four scripts/check-*.py"
  exit 0
fi

command -v claude >/dev/null 2>&1 || { echo "error: the claude CLI is not installed" >&2; exit 2; }
if [ -e "$worktree" ] || git -C "$repo_root" show-ref --verify --quiet "refs/heads/$branch"; then
  echo "error: worktree or branch already exists: $worktree / $branch" >&2
  exit 2
fi

git -C "$repo_root" worktree add -b "$branch" "$worktree" "$base_branch"
cd "$worktree"

if [ "$(git rev-parse --abbrev-ref HEAD)" != "$branch" ]; then
  echo "error: the worktree is not on $branch" >&2
  exit 2
fi

worker_status=0
claude -p "$prompt" --append-system-prompt-file "$entry" --allowedTools "$allowed_tools" || worker_status=$?
echo "worker exit status: $worker_status"

gate_status=0
for check in check-docs check-modules check-api-index check-provenance; do
  if python3 -I "scripts/$check.py"; then
    echo "gate: $check passed"
  else
    echo "gate: $check FAILED"
    gate_status=1
  fi
done

echo "branch $branch is ready for review in $worktree (not pushed)"
[ "$worker_status" -eq 0 ] && [ "$gate_status" -eq 0 ]
