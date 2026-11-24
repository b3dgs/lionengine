#!/usr/bin/env python3
"""Asset provenance check (REQ-DOC-006, ADR-0023, ADR-0032 section 5).

Usage (from the repository root):
    python3 -I scripts/check-provenance.py [repository-root]

Standard library only. Exit code 0 when every asset has a valid provenance entry, 1 otherwise.

Asset folders: src/main/resources and src/test/resources of every lionengine-* module, and every folder
named "assets" outside target directories. Asset files: images, sounds, maps, tilesets and definition files
(see ASSET_EXTENSIONS; a batch adding another asset kind extends the list).

A "provenance.json" in a folder applies to every file of that folder (not of its sub-folders); a
"<file name>.provenance.json" next to an asset overrides it. Each entry is strict JSON with exactly these
fields: "format" = "lionengine.provenance", "version" = 1, "source" ("hand-made", "generated" or
"third-party"), "author", "license" (SPDX id or expression), "tool" and "model" (required for "generated"),
"url" (required for "third-party"), "date" (ISO 8601), optional "comment".
"""
import json
import os
import re
import sys

ASSET_EXTENSIONS = {
    # images
    ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp",
    # sounds
    ".wav", ".ogg", ".mp3", ".flac",
    # maps and tilesets (Tiled)
    ".tmj", ".tsj", ".tmx", ".tsx",
    # definition files
    ".json",
}
FOLDER_FILE = "provenance.json"
SUFFIX = ".provenance.json"
SOURCES = ("hand-made", "generated", "third-party")
REQUIRED = ("format", "version", "source", "author", "license", "date")
OPTIONAL = ("tool", "model", "url", "comment")
SPDX = re.compile(r"^[A-Za-z0-9.+-]+(\s+(AND|OR|WITH)\s+[A-Za-z0-9.+-]+)*$")
DATE = re.compile(r"^\d{4}-\d{2}-\d{2}(T\d{2}:\d{2}(:\d{2}(\.\d+)?)?(Z|[+-]\d{2}:\d{2})?)?$")


def no_duplicates(pairs):
    keys = [k for k, _ in pairs]
    duplicates = sorted({k for k in keys if keys.count(k) > 1})
    if duplicates:
        raise ValueError(f"duplicate key(s) {', '.join(duplicates)}")
    return dict(pairs)


def reject_constant(name):
    raise ValueError(f"invalid number {name}")


def validate(path):
    """Return the problems of one provenance file."""
    try:
        with open(path, encoding="utf-8") as handle:
            entry = json.load(handle, object_pairs_hook=no_duplicates, parse_constant=reject_constant)
    except (OSError, ValueError) as error:
        return [f"{path}: unreadable ({error})"]
    if not isinstance(entry, dict):
        return [f"{path}: not a JSON object"]
    problems = []
    for field in REQUIRED:
        if field not in entry:
            problems.append(f"missing \"{field}\"")
    for field in sorted(set(entry) - set(REQUIRED) - set(OPTIONAL)):
        problems.append(f"unknown field \"{field}\"")
    if entry.get("format") != "lionengine.provenance":
        problems.append("\"format\" must be \"lionengine.provenance\"")
    if entry.get("version") != 1 or isinstance(entry.get("version"), bool):
        problems.append("\"version\" must be 1")
    source = entry.get("source")
    if source not in SOURCES:
        problems.append(f"\"source\" must be one of {', '.join(SOURCES)}")
    for field in ("author", "license", "tool", "model", "url", "comment"):
        if field in entry and (not isinstance(entry[field], str) or not entry[field].strip()):
            problems.append(f"\"{field}\" must be a non-empty string")
    if isinstance(entry.get("license"), str) and not SPDX.match(entry["license"].strip()):
        problems.append("\"license\" must be an SPDX id or expression")
    if source == "generated":
        problems += [f"\"{f}\" is required for a generated asset" for f in ("tool", "model") if f not in entry]
    if source == "third-party" and "url" not in entry:
        problems.append("\"url\" is required for a third-party asset")
    if not isinstance(entry.get("date"), str) or not DATE.match(entry["date"]):
        problems.append("\"date\" must be an ISO 8601 date")
    return [f"{path}: {p}" for p in problems]


def asset_folders(root):
    for name in sorted(os.listdir(root)):
        if name.startswith("lionengine-"):
            for kind in ("main", "test"):
                folder = os.path.join(root, name, "src", kind, "resources")
                if os.path.isdir(folder):
                    yield folder
    for directory, dirnames, _ in os.walk(root):
        dirnames[:] = [d for d in dirnames if not d.startswith(".") and d != "target"]
        if os.path.basename(directory) == "assets":
            yield directory


def main(root):
    root = os.path.abspath(root)
    errors = []
    checked_assets = 0
    seen = set()
    for top in asset_folders(root):
        for directory, dirnames, files in os.walk(top):
            if directory in seen:
                continue
            seen.add(directory)
            dirnames[:] = [d for d in dirnames if not d.startswith(".")]
            folder_entry = os.path.join(directory, FOLDER_FILE)
            for file in sorted(files):
                path = os.path.join(directory, file)
                if file == FOLDER_FILE or file.endswith(SUFFIX):
                    errors.extend(validate(path))
                    continue
                if os.path.splitext(file)[1].lower() not in ASSET_EXTENSIONS:
                    continue
                checked_assets += 1
                if not os.path.isfile(path + SUFFIX) and not os.path.isfile(folder_entry):
                    errors.append(f"{os.path.relpath(path, root)}: no provenance entry "
                                  f"({FOLDER_FILE} in its folder or {file}{SUFFIX})")
    if errors:
        print(f"{len(errors)} problem(s) found:")
        for message in errors:
            print(f"  - {message}")
        return 1
    print(f"OK: {checked_assets} asset file(s) covered by a provenance entry.")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1] if len(sys.argv) > 1 else "."))
