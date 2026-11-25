#!/usr/bin/env bash
# Runs one run of the local-model benchmark (OQ-170) with the right slug and profile, or cleans one up.
# For the owner, not for agents. Procedure: local-agents/README.md.
#
# Usage (from anywhere inside the repository):
#   local-agents/run-benchmark.sh [--dry-run] <pc1|pc2|pc3> <run: 1, 2 or 3>
#   local-agents/run-benchmark.sh --clean <pc1|pc2|pc3> <run: 1, 2 or 3>
#
# It writes the task file from local-agents/benchmark-task.template.md to a temporary folder, then runs
# scripts/local-task.sh on it. The model gateway is configured by the environment of the machine
# (ANTHROPIC_BASE_URL, LOCAL_AGENT_MODEL), never by this script. No secret, no skip flag, no push.
# --clean removes the worktree and the branch of one run and keeps its report and logs.
set -euo pipefail

mode=run
args=()
for arg in "$@"; do
  case "$arg" in
    --dry-run) mode=dry ;;
    --clean) mode=clean ;;
    *) args+=("$arg") ;;
  esac
done
if [ "${#args[@]}" -ne 2 ]; then
  echo "usage: local-agents/run-benchmark.sh [--dry-run|--clean] <pc1|pc2|pc3> <1|2|3>" >&2
  exit 2
fi

pc=$(printf '%s' "${args[0]}" | tr '[:upper:]' '[:lower:]')
run=${args[1]}
case "$pc" in pc1|pc2|pc3) ;; *) echo "error: the machine must be pc1, pc2 or pc3" >&2; exit 2 ;; esac
case "$run" in 1|2|3) ;; *) echo "error: the run must be 1, 2 or 3" >&2; exit 2 ;; esac

repo_root=$(git rev-parse --show-toplevel)
template="$repo_root/local-agents/benchmark-task.template.md"
[ -f "$template" ] || { echo "error: missing $template" >&2; exit 2; }

slug="bench-random-example-$pc-$run"
work_root="$(dirname "$repo_root")/$(basename "$repo_root")-local"

if [ "$mode" = clean ]; then
  git -C "$repo_root" worktree remove --force "$work_root/$slug" 2>/dev/null || echo "no worktree $work_root/$slug"
  git -C "$repo_root" branch -D "local/$slug" 2>/dev/null || echo "no branch local/$slug"
  echo "cleaned $slug (report and logs kept in $work_root)"
  exit 0
fi

task_dir=$(mktemp -d)
trap 'rm -rf "$task_dir"' EXIT
task_file="$task_dir/$slug.md"
sed -e "s/__SLUG__/$slug/" -e "s/__PROFILE__/$(printf '%s' "$pc" | tr '[:lower:]' '[:upper:]')/" "$template" > "$task_file"

status=0
if [ "$mode" = dry ]; then
  "$repo_root/scripts/local-task.sh" --dry-run "$task_file" || status=$?
else
  "$repo_root/scripts/local-task.sh" "$task_file" || status=$?
  echo
  echo "report: $work_root/$slug.report.md"
  [ "$pc" = pc3 ] && echo "answer: $work_root/$slug.answer.md (to be applied by Claude Code, see local-agents/README.md)"
  echo "clean up when done: local-agents/run-benchmark.sh --clean $pc $run"
fi
exit "$status"
