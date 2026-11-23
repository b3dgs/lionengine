# ADR-0015 — Single artifact version and package dependency rule

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner
- Related requirements: `REQ-CORE-005`, `REQ-TECH-002`
- Open questions resolved: none; narrows `OQ-005` and `OQ-149`
- Supersedes / superseded by: none; completes [ADR-0012](0012-platform-and-build-baseline.md) (which stays unchanged)

Source: owner answers on issue #875.

## Context

[ADR-0012](0012-platform-and-build-baseline.md) chose multiple Maven modules and `radial-encapsulation-maven-plugin`, and left the versioning of artifacts and the exact rule enforced by the plugin to be verified.

## Decision

- **One version number is shared by all modules** (all `lionengine-XXX` artifacts and the aggregator).
- `radial-encapsulation-maven-plugin` forbids any dependency from a package to one of its **descendant** packages. A package may depend only on itself, its own package or a **parent** package.

## Consequences

- Positive consequences: one release number to reason about; the rule is automatic and the build fails on a violation (`maxViolations` 0).
- Costs / constraints: every module release moves together; the rule is hierarchical, so the dependency direction between modules must be reflected in the package tree (a module that others depend on lives in a parent package of theirs). A rule such as "core independent of every frontend" holds if frontends are not parents of core; the package naming per module (`OQ-149`) must be designed with this in mind and verified on the build skeleton.
- Impact on modules and APIs: package naming per module.
- Impact on tests and documentation: `OQ-005` and `OQ-149` updated; `REQ-CORE-005` coverage still verified on the skeleton, now with the exact rule.

## Validation

Decisions explicitly provided by the project owner on 2026-11-22 (issue #875).
