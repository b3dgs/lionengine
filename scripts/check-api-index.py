#!/usr/bin/env python3
"""API index check (REQ-DOC-002, REQ-DOC-005, ADR-0023, ADR-0032).

Usage (from the repository root, after the Maven build has compiled the sources):
    python3 -I scripts/check-api-index.py [repository-root]

Standard library only. Exit code 0 when every name holds, 1 otherwise.

Reads the "Index by task" table of docs/api-index.md. Placeholder cells written in italics, such as
"*(to be filled)*", are skipped, so the check passes on an index that names no type yet. Otherwise:
  1. every type in backticks of the "Entry-point types" column is a public type of the compiled main
     classes of an engine module (lionengine-*/target/classes); generic parameters are ignored and
     `Outer.Inner` names a nested type;
  2. every class in backticks of the "Example to copy" column is a compiled class of lionengine-examples.
"""
import os
import re
import struct
import sys

TOKEN = re.compile(r"`([^`]+)`")
TYPE_NAME = re.compile(r"^[A-Z][A-Za-z0-9]*(\.[A-Z][A-Za-z0-9]*)*$")
ACC_PUBLIC = 0x0001
CONSTANT_SIZES = {3: 4, 4: 4, 5: 8, 6: 8, 7: 2, 8: 2, 9: 4, 10: 4, 11: 4, 12: 4, 15: 3, 16: 2, 17: 4, 18: 4,
                  19: 2, 20: 2}


def access_flags(path):
    """Return the access flags of a class file."""
    with open(path, "rb") as handle:
        data = handle.read()
    count = struct.unpack_from(">H", data, 8)[0]
    offset, index = 10, 1
    while index < count:
        tag = data[offset]
        if tag == 1:
            offset += 3 + struct.unpack_from(">H", data, offset + 1)[0]
        else:
            offset += 1 + CONSTANT_SIZES[tag]
        index += 2 if tag in (5, 6) else 1
    return struct.unpack_from(">H", data, offset)[0]


def compiled_classes(root, only_module=None):
    """Return {simple binary name (Outer$Inner): [class file paths]} of the compiled main classes."""
    found = {}
    for name in sorted(os.listdir(root)):
        if not name.startswith("lionengine-") or (only_module and name != only_module):
            continue
        classes = os.path.join(root, name, "target", "classes")
        for directory, _, files in os.walk(classes):
            for file in files:
                if file.endswith(".class"):
                    found.setdefault(file[:-len(".class")], []).append(os.path.join(directory, file))
    return found


def rows(path):
    """Yield (types cell, example cell) of the index table."""
    header = None
    with open(path, encoding="utf-8") as handle:
        for line in handle:
            cells = [c.strip() for c in line.strip().strip("|").split("|")]
            if line.startswith("| Task |"):
                header = cells
            elif header and line.startswith("| ") and not line.startswith("|---") and len(cells) == len(header):
                yield cells[header.index("Entry-point types")], cells[header.index("Example to copy")]
            elif header and not line.startswith("|"):
                header = None


def names(cell):
    if cell.startswith("*("):
        return []
    result = []
    for token in TOKEN.findall(cell):
        token = re.sub(r"<.*>", "", token).strip()
        if TYPE_NAME.match(token):
            result.append(token)
    return result


def main(root):
    root = os.path.abspath(root)
    errors = []
    engine = compiled_classes(root)
    examples = compiled_classes(root, "lionengine-examples")
    checked = 0
    for types, example in rows(os.path.join(root, "docs", "api-index.md")):
        for name in names(types):
            checked += 1
            paths = engine.get(name.replace(".", "$"), [])
            if not any(access_flags(p) & ACC_PUBLIC for p in paths):
                errors.append(f"api-index.md names `{name}`, which is not a public compiled type")
        for name in names(example):
            checked += 1
            if name.replace(".", "$") not in examples:
                errors.append(f"api-index.md names example `{name}`, which is not a compiled class of examples")
    if errors:
        print(f"{len(errors)} problem(s) found:")
        for message in errors:
            print(f"  - {message}")
        return 1
    print(f"OK: {checked} name(s) checked against {len(engine)} compiled class(es).")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1] if len(sys.argv) > 1 else "."))
