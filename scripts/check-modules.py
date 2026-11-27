#!/usr/bin/env python3
"""Module dependency check of the build skeleton (REQ-CORE-005, ADR-0037, ADR-0042, ADR-0045).

Usage (from the repository root):
    python3 -I scripts/check-modules.py [repository-root]

Standard library only. Exit code 0 when every rule holds, 1 otherwise.

Rules:
  1. every folder lionengine-<module> with a pom.xml is listed in the aggregator pom.xml, and the reverse;
  2. every module has a row in docs/architecture/modules.md (exact name, or a wildcard row such as
     `resources-*`);
  3. main dependencies (any scope but test) on engine modules are within the module's row; a clause
     "`a` also `b`" in a row allows `b` for module `a` only; `examples` may use every module;
  4. a test-scope dependency on an engine module targets a module the dependent already depends on in
     main scope (directly or transitively), typically its test JAR, or `test-support` when the main
     closure of `test-support` does not contain the dependent (ADR-0045);
  5. no module depends on itself.
Cycles are rejected by Maven itself.
"""
import os
import re
import sys
import xml.etree.ElementTree as ET

POM_NS = {"m": "http://maven.apache.org/POM/4.0.0"}
PREFIX = "lionengine-"
GROUP_IDS = ("${project.groupId}", "com.b3dgs.lionengine")
ROW = re.compile(r"^\| `([^`]+)` \| .* \| (.*) \|$")
TOKEN = re.compile(r"`([^`]+)`")
ALSO = re.compile(r"`([\w-]+)` also `([\w-]+)`")
TEST_SUPPORT = "test-support"
EXAMPLES = "examples"


def text(element, path):
    found = element.find(path, POM_NS)
    return found.text.strip() if found is not None and found.text else None


def read_modules(root):
    """Return {module: [(dependency module, type, scope)]} for every module folder, and the aggregator list."""
    aggregator = ET.parse(os.path.join(root, "pom.xml")).getroot()
    listed = [m.text.strip() for m in aggregator.findall("m:modules/m:module", POM_NS)]
    modules = {}
    for name in sorted(os.listdir(root)):
        pom = os.path.join(root, name, "pom.xml")
        if not name.startswith(PREFIX) or not os.path.isfile(pom):
            continue
        project = ET.parse(pom).getroot()
        deps = []
        for dep in project.findall("m:dependencies/m:dependency", POM_NS):
            artifact = text(dep, "m:artifactId") or ""
            if text(dep, "m:groupId") in GROUP_IDS and artifact.startswith(PREFIX):
                deps.append((artifact[len(PREFIX):], text(dep, "m:type") or "jar", text(dep, "m:scope") or "compile"))
        modules[name[len(PREFIX):]] = deps
    return modules, [m[len(PREFIX):] if m.startswith(PREFIX) else m for m in listed]


def read_rows(root):
    """Return {row name: (allowed modules, {module: extra allowed})} from modules.md."""
    rows = {}
    path = os.path.join(root, "docs", "architecture", "modules.md")
    with open(path, encoding="utf-8") as handle:
        for line in handle:
            match = ROW.match(line.rstrip("\n"))
            if not match:
                continue
            cell = match.group(2)
            extra = {}
            for owner, allowed in ALSO.findall(cell):
                extra.setdefault(owner, set()).add(allowed)
            general = set(TOKEN.findall(ALSO.sub("", cell)))
            rows[match.group(1)] = (general, extra)
    return rows


def row_for(module, rows):
    if module in rows:
        return rows[module]
    for name, row in rows.items():
        if name.endswith("-*") and module.startswith(name[:-1]):
            return row
    return None


def closure(module, modules):
    """Modules reached from module through main (non-test) dependencies, module excluded."""
    seen, todo = set(), [module]
    while todo:
        for dep, _, scope in modules.get(todo.pop(), []):
            if scope != "test" and dep not in seen:
                seen.add(dep)
                todo.append(dep)
    seen.discard(module)
    return seen


def main(root):
    root = os.path.abspath(root)
    errors = []
    modules, listed = read_modules(root)
    rows = read_rows(root)
    for name in sorted(set(modules) - set(listed)):
        errors.append(f"{PREFIX}{name} is not listed in the aggregator pom.xml")
    for name in sorted(set(listed) - set(modules)):
        errors.append(f"aggregator pom.xml lists {name}, which has no pom.xml")
    support = closure(TEST_SUPPORT, modules)
    for module, deps in sorted(modules.items()):
        row = row_for(module, rows)
        if row is None:
            errors.append(f"{module}: no row in docs/architecture/modules.md")
            continue
        allowed = set(modules) if module == EXAMPLES else (row[0] | row[1].get(module, set())) & set(modules)
        main_closure = closure(module, modules)
        for dep, kind, scope in deps:
            label = f"{module} -> {dep} ({kind}, {scope})"
            if dep == module:
                errors.append(f"{label}: a module cannot depend on itself")
            elif scope != "test":
                if dep not in allowed:
                    errors.append(f"{label}: not in the row of {module} in modules.md")
            elif dep == TEST_SUPPORT:
                if module in support:
                    errors.append(f"{label}: test-support depends on {module} (ADR-0045)")
            elif dep not in main_closure:
                errors.append(f"{label}: test scope may only use modules {module} already depends on (ADR-0045)")
    if errors:
        print(f"{len(errors)} problem(s) found:")
        for message in errors:
            print(f"  - {message}")
        return 1
    print(f"OK: {len(modules)} modules, {sum(len(d) for d in modules.values())} engine dependencies.")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1] if len(sys.argv) > 1 else "."))
