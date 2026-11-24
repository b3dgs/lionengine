# Traceability matrix

**Status: VALIDATED — keep in sync with `requirements.md`.**

Maps each requirement to its decisions, the questions that are still open for it, and its tests. The build skeleton and the contract skeleton exist, so a few rows link build checks or value-type tests; a requirement may only leave the state `no test yet` when a test is linked here or it is declared non-automatable with a reason. The "Still open" column lists only open questions; resolved ones are recorded in the ADRs and in `open-questions.md`.

| Requirement | Priority | Status | ADR | Still open | Tests | Verification state |
|---|---|---|---|---|---|---|
| REQ-TECH-001 | Must | VALIDATED | ADR-0001, ADR-0012, ADR-0041, ADR-0044 | — | enforcer rule (JDK 25 or later) in `pom.xml`; desktop modules at `--release 25` | verified by the build |
| REQ-TECH-002 | Must | VALIDATED | ADR-0001, ADR-0012, ADR-0051 | — | Maven 3.9 reactor, `mvn -B verify` | verified by the build |
| REQ-TECH-003 | Must | VALIDATED | ADR-0001, ADR-0013 | — | — | no test yet |
| REQ-TECH-004 | Must | VALIDATED | ADR-0001 | — | — | no test yet |
| REQ-TECH-005 | Must | VALIDATED | ADR-0001 | — | — | no test yet |
| REQ-TECH-006 | Must | VALIDATED | ADR-0001, ADR-0012, ADR-0041, ADR-0044, ADR-0049 | — | — | no test yet |
| REQ-TECH-007 | Must | VALIDATED | ADR-0001, ADR-0012, ADR-0041, ADR-0044 | — | — | no test yet |
| REQ-TECH-008 | Must | VALIDATED | ADR-0001 | — | — | no test yet |
| REQ-CORE-002 | Must | VALIDATED | ADR-0012, ADR-0015 | — | — | no test yet |
| REQ-CORE-003 | Must | VALIDATED | ADR-0012, ADR-0032, ADR-0042, ADR-0049, ADR-0051 | — | javadoc doclint `all`, `failOnWarnings`; probe: an undocumented public class fails the build | verified by the build (probe) |
| REQ-CORE-004 | Must | VALIDATED | ADR-0012, ADR-0045, ADR-0049, ADR-0050 | — | — | no test yet |
| REQ-CORE-005 | Must | VALIDATED | ADR-0012, ADR-0015, ADR-0040, ADR-0042, ADR-0045, ADR-0046, ADR-0047, ADR-0048, ADR-0049 | — | `radial-encapsulation-maven-plugin` per module, `scripts/check-modules.py`, Maven graph; probes: sibling package, undeclared module, row and test-scope violations | verified by the build (probes) |
| REQ-CORE-006 | Should | VALIDATED | ADR-0031 | — | — | no test yet |
| REQ-TEST-001 | Must | VALIDATED | ADR-0017, ADR-0022, ADR-0045, ADR-0050 | — | — | no test yet |
| REQ-DOC-001 | Must | VALIDATED | ADR-0023, ADR-0042 | — | — | no test yet |
| REQ-DOC-002 | Should | VALIDATED | ADR-0023 | — | — | no test yet |
| REQ-DOC-003 | Should | VALIDATED | ADR-0023 | — | — | no test yet |
| REQ-DOC-004 | Should | VALIDATED | ADR-0023, ADR-0042 | — | sources and Javadoc JARs built by `mvn -B verify` | verified by the build |
| REQ-DOC-005 | Should | VALIDATED | ADR-0023, ADR-0032, ADR-0042 | — | `scripts/check-api-index.py`; probe: unknown type and example fail | check in place; the index names no type yet |
| REQ-DOC-006 | Should | VALIDATED | ADR-0023, ADR-0032, ADR-0042 | — | `scripts/check-provenance.py`; probes: missing entry and incomplete generated entry fail | check in place; no asset yet |
| REQ-DOC-007 | Must | VALIDATED | ADR-0012, ADR-0032 | — | — | no test yet |
| REQ-FE-001 | Must | VALIDATED | ADR-0022 | — | — | no test yet |
| REQ-FE-002 | Should | VALIDATED | ADR-0022 | — | — | no test yet |
| REQ-FE-003 | Must | VALIDATED | ADR-0002, ADR-0008 | — | — | no test yet |
| REQ-PLATFORM-001 | Must | VALIDATED | ADR-0008, ADR-0022, ADR-0033, ADR-0045 | — | — | no test yet |
| REQ-PLATFORM-002 | Must | VALIDATED | ADR-0017, ADR-0018, ADR-0031, ADR-0040, ADR-0045, ADR-0046 | — | — | no test yet |
| REQ-DISPLAY-001 | Must | VALIDATED | ADR-0002, ADR-0043 | — | — | no test yet |
| REQ-DISPLAY-002 | Must | VALIDATED | ADR-0002, ADR-0006 | — | — | no test yet |
| REQ-DISPLAY-003 | Must | VALIDATED | ADR-0002, ADR-0022, ADR-0033, ADR-0043 | — | `DisplayModeTest` (value type, unknown values) | partial: enumeration and selection pending |
| REQ-DISPLAY-004 | Must | VALIDATED | ADR-0002, ADR-0006, ADR-0014, ADR-0046 | — | — | no test yet |
| REQ-DISPLAY-005 | Must | VALIDATED | ADR-0002, ADR-0018 | — | — | no test yet |
| REQ-DISPLAY-006 | Must | VALIDATED | ADR-0002 | — | — | no test yet |
| REQ-DISPLAY-007 | Must | VALIDATED | ADR-0006, ADR-0046 | — | — | no test yet |
| REQ-DISPLAY-008 | Must | VALIDATED | ADR-0006, ADR-0018, ADR-0046 | — | `DisplayRequestTest` (request and effective-result values) | partial: delivery at the frame boundary pending |
| REQ-DISPLAY-009 | Should | VALIDATED | ADR-0025, ADR-0043 | — | — | no test yet |
| REQ-DISPLAY-010 | Should | VALIDATED | ADR-0043 | — | — | no test yet |
| REQ-FILTER-001 | Must | VALIDATED | ADR-0002, ADR-0025 | — | `OutputFilterTest` (standard identifiers) | partial: capability query pending |
| REQ-FILTER-002 | Should | VALIDATED | ADR-0002, ADR-0006, ADR-0046 | — | — | no test yet |
| REQ-FILTER-003 | Could | VALIDATED | ADR-0002, ADR-0033, ADR-0047 | — | `OutputFilterTest` (custom ids) | partial: registration on the backend pending |
| REQ-FILTER-004 | Should | VALIDATED | ADR-0025, ADR-0033 | — | — | no test yet |
| REQ-LOOP-001 | Must | VALIDATED | ADR-0017 | — | — | no test yet |
| REQ-LOOP-002 | Must | VALIDATED | ADR-0017, ADR-0018 | — | — | no test yet |
| REQ-LOOP-003 | Should | VALIDATED | ADR-0017 | — | — | no test yet |
| REQ-LOOP-004 | Must | VALIDATED | ADR-0001, ADR-0017 | — | — | no test yet |
| REQ-LOOP-005 | Must | VALIDATED | ADR-0001, ADR-0017 | — | — | no test yet |
| REQ-LOOP-006 | Must | VALIDATED | ADR-0001, ADR-0017 | — | — | no test yet |
| REQ-LOOP-007 | Must | VALIDATED | ADR-0017 | — | — | no test yet |
| REQ-LOOP-008 | Must | VALIDATED | ADR-0017, ADR-0031 | — | — | no test yet |
| REQ-LOOP-009 | Must | VALIDATED | ADR-0017 | — | — | no test yet |
| REQ-LOOP-010 | Must | VALIDATED | ADR-0017, ADR-0018 | — | — | no test yet |
| REQ-LOOP-011 | Must | VALIDATED | ADR-0031 | — | — | no test yet |
| REQ-LOOP-012 | Should | VALIDATED | ADR-0031 | — | — | no test yet |
| REQ-RT-001 | Must | VALIDATED | ADR-0018, ADR-0040 | — | — | no test yet |
| REQ-RT-002 | Must | VALIDATED | ADR-0018, ADR-0040 | — | — | no test yet |
| REQ-RT-003 | Must | VALIDATED | ADR-0018, ADR-0007 | — | — | no test yet |
| REQ-RT-004 | Should | VALIDATED | ADR-0019, ADR-0031, ADR-0040 | — | — | no test yet |
| REQ-RT-005 | Must | VALIDATED | ADR-0019, ADR-0026, ADR-0027 | — | — | no test yet |
| REQ-RT-006 | Must | VALIDATED | ADR-0019 | — | — | no test yet |
| REQ-RT-007 | Must | VALIDATED | ADR-0020, ADR-0032 | — | — | no test yet |
| REQ-RT-008 | Must | VALIDATED | ADR-0020 | — | — | no test yet |
| REQ-RT-009 | Must | VALIDATED | ADR-0031, ADR-0040 | — | — | no test yet |
| REQ-RT-010 | Should | VALIDATED | ADR-0031, ADR-0040 | — | — | no test yet |
| REQ-RES-001 | Must | VALIDATED | ADR-0005, ADR-0032 | — | — | no test yet |
| REQ-RES-002 | Should | VALIDATED | ADR-0005, ADR-0032 | — | — | no test yet |
| REQ-RES-003 | Must | VALIDATED | ADR-0005, ADR-0019 | — | — | no test yet |
| REQ-RES-004 | Must | VALIDATED | ADR-0005, ADR-0007, ADR-0045 | — | — | no test yet |
| REQ-RES-005 | Must | VALIDATED | ADR-0007 | — | `ResourceKeyTest` (key rules, equality) | partial: release convention pending |
| REQ-RES-006 | Must | VALIDATED | ADR-0032 | — | `ResourceExceptionTest` (reasons, key, cause) | partial: providers pending |
| REQ-AUDIO-001 | Should | VALIDATED | ADR-0004, ADR-0022 | — | — | no test yet |
| REQ-AUDIO-002 | Should | VALIDATED | ADR-0027, ADR-0033 | — | — | no test yet |
| REQ-AUDIO-003 | Must | VALIDATED | ADR-0004, ADR-0027, ADR-0041, ADR-0044 | — | — | no test yet |
| REQ-AUDIO-004 | Must | VALIDATED | ADR-0004 | — | — | no test yet |
| REQ-AUDIO-005 | Must | VALIDATED | ADR-0004, ADR-0027, ADR-0033 | — | — | no test yet |
| REQ-AUDIO-006 | Must | VALIDATED | ADR-0004, ADR-0027, ADR-0033 | — | — | no test yet |
| REQ-AUDIO-007 | Must | VALIDATED | ADR-0004 | — | — | no test yet |
| REQ-AUDIO-008 | Must | VALIDATED | ADR-0004, ADR-0027, ADR-0033 | — | — | no test yet |
| REQ-AUDIO-009 | Must | VALIDATED | ADR-0004, ADR-0022, ADR-0045 | — | — | no test yet |
| REQ-AUDIO-010 | Must | VALIDATED | ADR-0027, ADR-0033 | — | — | no test yet |
| REQ-AUDIO-011 | Should | VALIDATED | ADR-0027 | — | — | no test yet |
| REQ-AUDIO-012 | Should | VALIDATED | ADR-0033 | — | — | no test yet |
| REQ-AUDIO-013 | Should | VALIDATED | ADR-0033 | — | — | no test yet |
| REQ-SPR-001 | Must | VALIDATED | ADR-0003, ADR-0026 | — | — | no test yet |
| REQ-SPR-002 | Must | VALIDATED | ADR-0003, ADR-0026 | — | — | no test yet |
| REQ-SPRITE-001 | Must | VALIDATED | ADR-0003, ADR-0026 | — | — | no test yet |
| REQ-SPRITE-002 | Must | VALIDATED | ADR-0005, ADR-0007, ADR-0026 | — | — | no test yet |
| REQ-SPRITE-003 | Must | VALIDATED | ADR-0014, ADR-0016, ADR-0026 | — | — | no test yet |
| REQ-SPRITE-004 | Must | VALIDATED | ADR-0003, ADR-0026 | — | — | no test yet |
| REQ-SPRITE-005 | Should | VALIDATED | ADR-0003, ADR-0026 | — | — | no test yet |
| REQ-SPRITE-006 | Must | VALIDATED | ADR-0003, ADR-0026 | — | — | no test yet |
| REQ-SPRITE-007 | Must | VALIDATED | ADR-0003, ADR-0026 | — | — | no test yet |
| REQ-SPRITE-008 | Could | VALIDATED | ADR-0003, ADR-0026 | — | — | no test yet |
| REQ-SPRITE-009 | Must | VALIDATED | ADR-0026, ADR-0033 | — | — | no test yet |
| REQ-SPRITE-010 | Must | VALIDATED | ADR-0026, ADR-0033, ADR-0047 | — | — | no test yet |
| REQ-ANIM-001 | Must | VALIDATED | ADR-0026, ADR-0033 | — | — | no test yet |
| REQ-ANIM-002 | Must | VALIDATED | ADR-0003 | — | — | no test yet |
| REQ-ANIM-003 | Must | VALIDATED | ADR-0003, ADR-0017 | — | — | no test yet |
| REQ-SHEET-001 | Must | VALIDATED | ADR-0003 | — | — | no test yet |
| REQ-SHEET-002 | Should | VALIDATED | ADR-0007, ADR-0026 | — | — | no test yet |
| REQ-SHEET-003 | Must | VALIDATED | ADR-0032, ADR-0033 | — | — | no test yet |
| REQ-CAM-001 | Must | VALIDATED | ADR-0010, ADR-0014 | — | — | no test yet |
| REQ-CAM-002 | Must | VALIDATED | ADR-0029, ADR-0034 | — | — | no test yet |
| REQ-CAM-003 | Must | VALIDATED | ADR-0010 | — | — | no test yet |
| REQ-CAM-004 | Must | VALIDATED | ADR-0010, ADR-0029, ADR-0048 | — | — | no test yet |
| REQ-CAM-005 | Must | VALIDATED | ADR-0010, ADR-0029 | — | — | no test yet |
| REQ-CAM-006 | Should | VALIDATED | ADR-0029, ADR-0034 | — | — | no test yet |
| REQ-CAM-007 | Must | VALIDATED | ADR-0013, ADR-0014, ADR-0029 | — | — | no test yet |
| REQ-CAM-008 | Must | VALIDATED | ADR-0010 | — | — | no test yet |
| REQ-CAM-009 | Must | VALIDATED | ADR-0029, ADR-0034 | — | — | no test yet |
| REQ-CAM-010 | Should | VALIDATED | ADR-0029, ADR-0034 | — | — | no test yet |
| REQ-MAP-001 | Should | VALIDATED | ADR-0009 | — | — | no test yet |
| REQ-MAP-002 | Should | VALIDATED | ADR-0009, ADR-0028 | — | — | no test yet |
| REQ-MAP-003 | Must | VALIDATED | ADR-0009 | — | — | no test yet |
| REQ-MAP-004 | Must | VALIDATED | ADR-0009, ADR-0028, ADR-0032, ADR-0045 | — | — | no test yet |
| REQ-MAP-005 | Must | VALIDATED | ADR-0009 | — | — | no test yet |
| REQ-MAP-006 | Must | VALIDATED | ADR-0009, ADR-0028 | — | — | no test yet |
| REQ-MAP-007 | Should | VALIDATED | ADR-0009, ADR-0018 | — | — | no test yet |
| REQ-MAP-008 | Should | VALIDATED | ADR-0028 | — | — | no test yet |
| REQ-OBJ-001 | Must | VALIDATED | ADR-0011, ADR-0021 | — | — | no test yet |
| REQ-OBJ-002 | Should | VALIDATED | ADR-0021 | — | — | no test yet |
| REQ-TRAIT-001 | Should | VALIDATED | ADR-0011, ADR-0021 | — | — | no test yet |
| REQ-TRAIT-002 | Should | VALIDATED | ADR-0011 | — | — | no test yet |
| REQ-SCENE-001 | Must | VALIDATED | ADR-0021, ADR-0031, ADR-0036 | — | — | no test yet |
| REQ-SCENE-002 | Must | VALIDATED | ADR-0021, ADR-0031 | — | — | no test yet |
| REQ-SCENE-003 | Should | VALIDATED | ADR-0031 | — | — | no test yet |
| REQ-SCENE-004 | Must | VALIDATED | ADR-0036 | — | — | no test yet |
| REQ-SCENE-005 | Must | VALIDATED | ADR-0036 | — | — | no test yet |
| REQ-SCENE-006 | Must | VALIDATED | ADR-0036 | — | — | no test yet |
| REQ-SCENE-007 | Must | VALIDATED | ADR-0036 | — | — | no test yet |
| REQ-SCENE-008 | Should | VALIDATED | ADR-0036 | — | — | no test yet |
| REQ-WORLD-001 | Must | VALIDATED | ADR-0021, ADR-0024 | — | — | no test yet |
| REQ-PHYS-001 | Should | VALIDATED | ADR-0021, ADR-0028, ADR-0030 | — | — | no test yet |
| REQ-DEF-001 | Must | VALIDATED | ADR-0020, ADR-0028 | — | `DefinitionExceptionTest` (error location and message) | partial: reader pending |
| REQ-DEF-002 | Must | VALIDATED | ADR-0032 | — | — | no test yet |
| REQ-DEF-003 | Must | VALIDATED | ADR-0020, ADR-0032 | — | — | no test yet |
| REQ-ECS-001 | Must | VALIDATED | ADR-0011, ADR-0030 | — | — | no test yet |
| REQ-ECS-002 | Must | VALIDATED | ADR-0011, ADR-0030 | — | — | no test yet |
| REQ-ECS-003 | Must | VALIDATED | ADR-0011, ADR-0030 | — | — | no test yet |
| REQ-ECS-004 | Must | VALIDATED | ADR-0011 | — | — | no test yet |
| REQ-ECS-005 | Must | VALIDATED | ADR-0011, ADR-0030 | — | — | no test yet |
| REQ-ECS-006 | Must | VALIDATED | ADR-0011, ADR-0030 | — | — | no test yet |
| REQ-ECS-007 | Should | VALIDATED | ADR-0011, ADR-0034 | — | — | no test yet |
| REQ-ECS-008 | Must | VALIDATED | ADR-0011, ADR-0030 | — | — | no test yet |
| REQ-ECS-009 | Must | VALIDATED | ADR-0030, ADR-0034, ADR-0041, ADR-0044 | — | — | no test yet |
| REQ-ECS-010 | Must | VALIDATED | ADR-0011, ADR-0018 | — | — | no test yet |
| REQ-ECS-011 | Must | VALIDATED | ADR-0030 | — | — | no test yet |
| REQ-ECS-012 | Must | VALIDATED | ADR-0030 | — | — | no test yet |
| REQ-ECS-013 | Must | VALIDATED | ADR-0034 | — | — | no test yet |
| REQ-STD-001 | Should | VALIDATED | ADR-0011, ADR-0014, ADR-0030 | — | — | no test yet |
| REQ-STD-002 | Should | VALIDATED | ADR-0011 | — | — | no test yet |
| REQ-STD-003 | Should | VALIDATED | ADR-0011, ADR-0013, ADR-0030 | — | — | no test yet |
| REQ-STD-004 | Should | VALIDATED | ADR-0030, ADR-0034 | — | — | no test yet |
| REQ-STD-005 | Should | VALIDATED | ADR-0011, ADR-0030 | — | — | no test yet |
| REQ-STD-006 | Should | VALIDATED | ADR-0011 | — | — | no test yet |
| REQ-STD-007 | Should | VALIDATED | ADR-0011 | — | — | no test yet |
| REQ-STD-008 | Should | VALIDATED | ADR-0011, ADR-0030, ADR-0034 | — | — | no test yet |
| REQ-STD-009 | Could | VALIDATED | ADR-0030, ADR-0034 | — | — | no test yet |
| REQ-STD-010 | Should | VALIDATED | ADR-0028, ADR-0030 | — | — | no test yet |
| REQ-STD-011 | Should | VALIDATED | ADR-0038 | — | — | no test yet |
| REQ-IN-001 | Must | VALIDATED | ADR-0035, ADR-0040 | — | — | no test yet |
| REQ-IN-002 | Should | VALIDATED | ADR-0035 | — | — | no test yet |
| REQ-IN-003 | Must | VALIDATED | ADR-0018, ADR-0035 | — | — | no test yet |
| REQ-IN-004 | Should | VALIDATED | ADR-0035, ADR-0046 | — | — | no test yet |
| REQ-IN-005 | Should | VALIDATED | ADR-0035 | — | — | no test yet |
| REQ-MP-001 | Could | VALIDATED | ADR-0021, ADR-0022 | — | — | no test yet |
| REQ-MP-002 | Could | VALIDATED | ADR-0024, ADR-0038 | — | — | no test yet |
| REQ-NET-001 | Should | VALIDATED | ADR-0038, ADR-0039, ADR-0041, ADR-0044 | — | — | no test yet |
| REQ-NET-002 | Should | VALIDATED | ADR-0039, ADR-0041, ADR-0044, ADR-0045 | — | — | no test yet |
| REQ-NET-003 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-004 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-005 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-006 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-007 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-008 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-009 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-010 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-011 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-012 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-013 | Could | VALIDATED | ADR-0039, ADR-0041, ADR-0044 | — | — | no test yet |
| REQ-NET-014 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-NET-015 | Should | VALIDATED | ADR-0020, ADR-0039 | — | — | no test yet |
| REQ-NET-016 | Should | VALIDATED | ADR-0039, ADR-0041, ADR-0044 | — | — | no test yet |
| REQ-NET-017 | Should | VALIDATED | ADR-0039 | — | — | no test yet |
| REQ-MP-004 | Should | VALIDATED | ADR-0038, ADR-0039 | — | — | no test yet |
| REQ-MP-005 | Should | VALIDATED | ADR-0038 | — | — | no test yet |
| REQ-MP-006 | Should | VALIDATED | ADR-0038 | — | — | no test yet |
| REQ-MP-007 | Should | VALIDATED | ADR-0038 | — | — | no test yet |
| REQ-MP-008 | Should | VALIDATED | ADR-0038 | — | — | no test yet |
| REQ-MP-009 | Should | VALIDATED | ADR-0035, ADR-0038 | — | — | no test yet |
| REQ-MP-010 | Should | VALIDATED | ADR-0038 | — | — | no test yet |
| REQ-MP-011 | Should | VALIDATED | ADR-0038 | — | — | no test yet |
| REQ-MP-012 | Could | VALIDATED | ADR-0038 | — | — | no test yet |
| REQ-MP-013 | Should | VALIDATED | ADR-0038, ADR-0045 | — | — | no test yet |
| REQ-MP-014 | Should | VALIDATED | ADR-0038 | — | — | no test yet |
| REQ-MP-015 | Should | VALIDATED | ADR-0038 | — | — | no test yet |
| REQ-DET-001 | Should | VALIDATED | ADR-0024 | — | — | no test yet |
| REQ-DET-002 | Could | VALIDATED | ADR-0024, ADR-0038, ADR-0041, ADR-0044 | — | — | no test yet |
| REQ-DET-003 | Should | VALIDATED | ADR-0031 | — | — | no test yet |
| REQ-PERF-001 | Should | VALIDATED | ADR-0024, ADR-0031 | — | — | no test yet |
| REQ-PERF-002 | Should | VALIDATED | ADR-0024, ADR-0041, ADR-0044 | — | — | no test yet |
| REQ-SUPPORT-001 | Should | VALIDATED | ADR-0024, ADR-0031, ADR-0042 | — | — | no test yet |

Retired identifiers are listed in `requirements.md`.
