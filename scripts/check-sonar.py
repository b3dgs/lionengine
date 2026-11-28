#!/usr/bin/env python3
"""Sonar check before the first push (docs/implementation-guide.md, section 5).

Usage (from the repository root):
    python3 -I scripts/check-sonar.py [--keep]

Standard library only. Runs a SonarCloud branch analysis of the working tree on the analysis branch
"precheck/<current git branch>", waits for SonarCloud to process it, then lists its open issues that rewrite/v11
does not have (same rule, file and message; the line is ignored, since it moves with unrelated edits). Prints each
as "rule file:line message". Exit code 0 when none remains, 1 when some remain or a step fails.

SonarCloud computes "new code only" for real pull requests, so a check before any push is a branch analysis
compared with the open issues of rewrite/v11. It does not replace the quality gate of the CI "sonar" job, which also
checks the coverage of the new code.

The token is SONAR_TOKEN from the environment when set; otherwise a placeholder is sent and the agent proxy of a
cloud session replaces it with the token saved for sonarcloud.io and api.sonarcloud.io. The analysis needs the hosts
api.sonarcloud.io (scanner bootstrap) and scanner.sonarcloud.io (engine download). The JDK of JAVA_HOME runs the
scanner (no JRE provisioning).

After the comparison, the analysis branch is deleted on SonarCloud (POST api/project_branches/delete) unless --keep
is given; when the token may not delete it, a warning is printed and the branch is left (SonarCloud purges inactive
branches).
"""
import json
import os
import subprocess
import sys
import time
import urllib.error
import urllib.parse
import urllib.request

HOST = "https://sonarcloud.io"
ORGANIZATION = "b3dgs"
PROJECT = "b3dgs_lionengine"
REFERENCE_BRANCH = "rewrite/v11"
BRANCH_PREFIX = "precheck/"
PLACEHOLDER_TOKEN = "proxy-injected"
REPORT_TASK = os.path.join("target", "sonar", "report-task.txt")
PAGE_SIZE = 500
POLL_SECONDS = 5
TIMEOUT_SECONDS = 900


def token():
    """Return the token sent to SonarCloud: SONAR_TOKEN, or the placeholder the proxy replaces."""
    return os.environ.get("SONAR_TOKEN") or PLACEHOLDER_TOKEN


def request(path, params, method="GET"):
    """Send an authenticated request to the SonarCloud web API and return the decoded JSON (or None)."""
    query = urllib.parse.urlencode(params)
    url = f"{HOST}/{path}"
    data = None
    if method == "GET":
        url = f"{url}?{query}"
    else:
        data = query.encode("ascii")
    req = urllib.request.Request(url, data=data, method=method)
    req.add_header("Authorization", f"Bearer {token()}")
    with urllib.request.urlopen(req, timeout=60) as response:
        body = response.read()
    return json.loads(body) if body else None


def current_branch():
    """Return the current git branch name."""
    result = subprocess.run(["git", "rev-parse", "--abbrev-ref", "HEAD"], capture_output=True, text=True, check=True)
    branch = result.stdout.strip()
    if branch == "HEAD":
        raise RuntimeError("detached HEAD: check out a branch first")
    return branch


def analyze(branch):
    """Run the build and the Sonar analysis of the working tree on the analysis branch."""
    command = [
        "mvn", "-B", "-ntp", "verify", "-Psonar",
        f"-Dsonar.host.url={HOST}",
        f"-Dsonar.organization={ORGANIZATION}",
        f"-Dsonar.projectKey={PROJECT}",
        f"-Dsonar.branch.name={branch}",
        "-Dsonar.scanner.skipJreProvisioning=true",
        "-Dsonar.qualitygate.wait=false",
        f"-Dsonar.token={token()}",
    ]
    printable = " ".join(part if not part.startswith("-Dsonar.token=") else "-Dsonar.token=***" for part in command)
    print(f"Running: {printable}", flush=True)
    subprocess.run(command, check=True)


def task_id():
    """Return the compute-engine task id written by the scanner."""
    with open(REPORT_TASK, encoding="utf-8") as handle:
        for line in handle:
            key, _, value = line.strip().partition("=")
            if key == "ceTaskId":
                return value
    raise RuntimeError(f"no ceTaskId in {REPORT_TASK}")


def wait_task(identifier):
    """Poll the compute-engine task until SUCCESS; fail on FAILED, CANCELED or after the timeout."""
    deadline = time.monotonic() + TIMEOUT_SECONDS
    while True:
        status = request("api/ce/task", {"id": identifier})["task"]["status"]
        if status == "SUCCESS":
            print(f"Analysis task {identifier} processed.", flush=True)
            return
        if status in ("FAILED", "CANCELED"):
            raise RuntimeError(f"analysis task {identifier} ended with {status}")
        if time.monotonic() >= deadline:
            raise RuntimeError(f"analysis task {identifier} not processed after {TIMEOUT_SECONDS} s ({status})")
        time.sleep(POLL_SECONDS)


def issues(branch):
    """Return the open issues of a branch, every page."""
    found = []
    page = 1
    while True:
        result = request("api/issues/search", {
            "componentKeys": PROJECT,
            "branch": branch,
            "resolved": "false",
            "ps": PAGE_SIZE,
            "p": page,
        })
        found.extend(result["issues"])
        if page * PAGE_SIZE >= result["paging"]["total"]:
            return found
        page += 1


def path_of(issue):
    """Return the file of an issue, relative to the repository root."""
    return issue["component"].partition(":")[2]


def key_of(issue):
    """Return the comparison key of an issue: rule, file and message, the line ignored."""
    return issue["rule"], path_of(issue), issue["message"]


def new_issues(branch_issues, reference_issues):
    """Return the issues of the branch absent from the reference, each reference issue matching once."""
    remaining = {}
    for issue in reference_issues:
        key = key_of(issue)
        remaining[key] = remaining.get(key, 0) + 1
    result = []
    for issue in branch_issues:
        key = key_of(issue)
        if remaining.get(key, 0) > 0:
            remaining[key] -= 1
        else:
            result.append(issue)
    return sorted(result, key=lambda issue: (path_of(issue), issue.get("line", 0), issue["rule"]))


def delete_branch(branch):
    """Delete the analysis branch on SonarCloud; warn when the token may not."""
    try:
        request("api/project_branches/delete", {"project": PROJECT, "branch": branch}, method="POST")
        print(f"Analysis branch {branch} deleted on SonarCloud.")
    except urllib.error.HTTPError as error:
        print(f"warning: analysis branch {branch} not deleted (HTTP {error.code}); SonarCloud purges inactive "
              "branches.", file=sys.stderr)


def main(arguments):
    keep = False
    for argument in arguments:
        if argument == "--keep":
            keep = True
        else:
            print(__doc__, file=sys.stderr)
            return 1
    try:
        branch = BRANCH_PREFIX + current_branch()
    except (OSError, subprocess.CalledProcessError, RuntimeError) as error:
        print(f"check-sonar: {error}", file=sys.stderr)
        return 1
    try:
        analyze(branch)
        wait_task(task_id())
        remaining = new_issues(issues(branch), issues(REFERENCE_BRANCH))
    except (OSError, RuntimeError, KeyError, ValueError, subprocess.CalledProcessError) as error:
        print(f"check-sonar: {error}", file=sys.stderr)
        return 1
    finally:
        if not keep:
            try:
                delete_branch(branch)
            except OSError as error:
                print(f"warning: analysis branch {branch} not deleted ({error})", file=sys.stderr)
    for issue in remaining:
        print(f"{issue['rule']} {path_of(issue)}:{issue.get('line', 0)} {issue['message']}")
    if remaining:
        print(f"{len(remaining)} issue(s) not on {REFERENCE_BRANCH}.", file=sys.stderr)
        return 1
    print(f"No issue beyond those of {REFERENCE_BRANCH}.")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
