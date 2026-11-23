#!/usr/bin/env python3
"""Consistency checks for the documentation of the repository.

Usage (from the repository root):
    python3 -I scripts/check-docs.py [repository-root]

Standard library only. Exit code 0 when everything is consistent, 1 otherwise.

Checks:
  1. relative Markdown links point to existing files;
  2. every Markdown file is reachable from README.md or CLAUDE.md (no orphan);
  3. every REQ-*, OQ-* and ADR-* reference is defined;
  4. no duplicate REQ-* or OQ-* definition;
  5. requirements.md and traceability.md list exactly the same requirements,
     with the same priority and status;
  6. every document under docs/ declares a Status line near its top;
  7. every feature page of docs/features/ is linked from docs/engine-features.md;
  8. no French accented character in the checked Markdown files (AGENTS.md section 0:
     repository content is English whatever the language of the request).
"""
import os
import re
import sys

ROOTS = ("README.md", "CLAUDE.md")
# Only these root files and everything under docs/ are checked; other Markdown files
# of the repository (CONTRIBUTING.md, CODE_OF_CONDUCT.md, issue templates...) are ignored.
SCOPE_ROOT_FILES = ("README.md", "AGENTS.md", "CLAUDE.md")
LINK = re.compile(r"\]\(([^)#\s]+)(?:#[^)]*)?\)")
REQ_ROW = re.compile(
    r"^\| (REQ-[A-Z]+-\d+) \| .*? \| (Must|Should|Could) \| ([^|]+?) \| .* \|$"
)
REQ_ANY_ROW = re.compile(r"^\| (REQ-[A-Z]+-\d+) \|", re.M)
OQ_ROW = re.compile(r"^\| (OQ-\d+) \|", re.M)
TRACE_ROW = re.compile(
    r"^\| (REQ-[A-Z]+-\d+) \| (Must|Should|Could) \| ([^|]+?) \|"
)
REQ_REF = re.compile(r"REQ-[A-Z]+-\d+")
OQ_REF = re.compile(r"OQ-\d+")
ADR_REF = re.compile(r"ADR-(\d{4})")
STATUS = re.compile(r"^(\*\*)?(- )?Status:", re.M)
# Accented letters that French uses and English documentation does not (AGENTS.md section 0).
FRENCH_ACCENT = re.compile("[àâäçéèêëîïôöùûüÀÂÄÇÉÈÊËÎÏÔÖÙÛÜ]")


def read(path):
    with open(path, encoding="utf-8") as handle:
        return handle.read()


def main(root):
    root = os.path.abspath(root)
    errors = []
    md_files = []
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if not d.startswith(".")]
        for name in filenames:
            if name.endswith(".md"):
                md_files.append(os.path.join(dirpath, name))
    rel = lambda p: os.path.relpath(p, root)
    in_scope = lambda p: (
        rel(p).replace(os.sep, "/") in SCOPE_ROOT_FILES
        or rel(p).replace(os.sep, "/").startswith("docs/")
    )
    md_files = [p for p in md_files if in_scope(p)]
    texts = {p: read(p) for p in md_files}

    # 1 and 2: links and orphans
    linked = set()
    for path, text in texts.items():
        for target in LINK.findall(text):
            if re.match(r"^[a-z]+:", target):
                continue
            if target.startswith("/"):  # GitHub style: relative to the repository root
                resolved = os.path.normpath(os.path.join(root, target.lstrip("/")))
            else:
                resolved = os.path.normpath(os.path.join(os.path.dirname(path), target))
            if os.path.exists(resolved):
                linked.add(resolved)
            else:
                errors.append(f"broken link in {rel(path)}: {target}")
    roots = {os.path.join(root, r) for r in ROOTS}
    for path in sorted(set(md_files) - linked - roots):
        errors.append(f"orphan file (not linked from anywhere): {rel(path)}")

    # 3 and 4: identifiers
    requirements = os.path.join(root, "docs", "requirements.md")
    open_questions = os.path.join(root, "docs", "open-questions.md")
    traceability = os.path.join(root, "docs", "traceability.md")
    decisions = os.path.join(root, "docs", "decisions")
    req_ids = REQ_ANY_ROW.findall(texts.get(requirements, ""))
    oq_ids = OQ_ROW.findall(texts.get(open_questions, ""))
    for kind, ids in (("REQ", req_ids), ("OQ", oq_ids)):
        for ident in sorted({i for i in ids if ids.count(i) > 1}):
            errors.append(f"duplicate {kind} definition: {ident}")
    adr_ids = set()
    if os.path.isdir(decisions):
        for name in os.listdir(decisions):
            match = re.match(r"^(\d{4})-.*\.md$", name)
            if match:
                adr_ids.add(match.group(1))
    for path, text in texts.items():
        for ident in sorted(set(REQ_REF.findall(text)) - set(req_ids)):
            errors.append(f"undefined requirement {ident} in {rel(path)}")
        for ident in sorted(set(OQ_REF.findall(text)) - set(oq_ids)):
            errors.append(f"undefined open question {ident} in {rel(path)}")
        for number in sorted(set(ADR_REF.findall(text)) - adr_ids):
            errors.append(f"undefined decision ADR-{number} in {rel(path)}")

    # 5: requirements <-> traceability
    if requirements in texts and traceability in texts:
        defined = {}
        for line in texts[requirements].splitlines():
            match = REQ_ROW.match(line)
            if match:
                defined[match.group(1)] = (match.group(2), match.group(3).strip())
        traced = {}
        for line in texts[traceability].splitlines():
            match = TRACE_ROW.match(line)
            if match:
                traced[match.group(1)] = (match.group(2), match.group(3).strip())
        for ident in sorted(set(defined) - set(traced)):
            errors.append(f"{ident} missing from traceability.md")
        for ident in sorted(set(traced) - set(defined)):
            errors.append(f"{ident} in traceability.md but not an active requirement")
        for ident in sorted(set(defined) & set(traced)):
            if defined[ident] != traced[ident]:
                errors.append(
                    f"{ident}: requirements.md says {defined[ident]}, "
                    f"traceability.md says {traced[ident]}"
                )

    # 6: status headers
    docs_dir = os.path.join(root, "docs")
    for path, text in texts.items():
        if path.startswith(docs_dir + os.sep):
            head = "\n".join(text.splitlines()[:8])
            if not STATUS.search(head):
                errors.append(f"no Status line near the top of {rel(path)}")

    # 7: every feature page is summarized in engine-features.md
    summary = os.path.join(root, "docs", "engine-features.md")
    features_dir = os.path.join(root, "docs", "features")
    if summary in texts and os.path.isdir(features_dir):
        summary_links = {
            os.path.normpath(os.path.join(os.path.dirname(summary), target))
            for target in LINK.findall(texts[summary])
        }
        for name in sorted(os.listdir(features_dir)):
            page = os.path.join(features_dir, name)
            if name.endswith(".md") and name != "README.md" and page not in summary_links:
                errors.append(f"feature page not linked from docs/engine-features.md: {rel(page)}")

    # 8: repository content is English (AGENTS.md section 0)
    for path, text in texts.items():
        for number, line in enumerate(text.splitlines(), start=1):
            found = sorted(set(FRENCH_ACCENT.findall(line)))
            if found:
                errors.append(
                    f"French accented character(s) {''.join(found)} in {rel(path)}:{number} "
                    "(repository content is English, AGENTS.md section 0)"
                )

    if errors:
        print(f"{len(errors)} problem(s) found:")
        for message in errors:
            print(f"  - {message}")
        return 1
    print(f"OK: {len(md_files)} Markdown files, {len(req_ids)} requirement rows, "
          f"{len(oq_ids)} open questions, {len(adr_ids)} ADR(s).")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1] if len(sys.argv) > 1 else "."))
