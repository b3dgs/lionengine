# Implementation guide

**Status: VALIDATED — practical rules for writing engine code that passes the quality gates of the build: the `b3dgs-parent` checkstyle configuration, the compiler and Javadoc settings, the package and module rules, a Sonar analysis with the organization's `b3dgs` quality profile, and the Android/DEX and performance constraints. [`conventions.md`](conventions.md) stays the owner of the conventions; this page tells how to meet them in code.**

Read this page before writing Java in the engine. Every rule below is enforced by the build or by the Sonar analysis; a batch is not done until all of them pass on the whole reactor.

## 1. Before you write code

1. Read [`../AGENTS.md`](../AGENTS.md), your batch's page in [`features/`](features/README.md), and the row of your module in [`architecture/interfaces.md`](architecture/interfaces.md).
2. Find the contracts you implement or use: the types declared by the contract skeleton are already compiled in `core`, `runtime`, `resources`, `render-api` and `platform-api`. Implement them, do not redeclare them. A signature that must change is changed in your pull request, together with its users and the catalog (ADR-0049).
3. Reference files that pass every gate today, to copy the style from:
   - interface with thread rules: `lionengine-core/src/main/java/com/b3dgs/lionengine/Inbox.java`;
   - role-named callback: `lionengine-runtime/src/main/java/com/b3dgs/lionengine/runtime/UpdateCallback.java`;
   - value type (record with validation): `lionengine-resources/src/main/java/com/b3dgs/lionengine/resources/ResourceKey.java`;
   - value type with an explicit canonical constructor (defensive copy): `lionengine-platform-api/src/main/java/com/b3dgs/lionengine/platform/DisplayRequest.java`;
   - final class with constants, private static helpers and getters: `lionengine-core/src/main/java/com/b3dgs/lionengine/DefinitionException.java`;
   - parameterized unit test: `lionengine-resources/src/test/java/com/b3dgs/lionengine/resources/ResourceKeyTest.java`;
   - allocation test with the per-thread counter: `testDisabledLevelAllocatesNothing` in `lionengine-core/src/test/java/com/b3dgs/lionengine/LogTest.java`;
   - factory reading a `Configuration`, with a factory check through `createError`: `lionengine-examples/src/main/java/com/b3dgs/lionengine/examples/core/DefinitionExample.java`;
   - example with its test run by the build: `DefinitionExampleTest` next to it in `lionengine-examples/src/test/java`;
   - abstract contract suite in a test JAR, bound in another module: `LoopDriverContract` in `lionengine-core/src/test/java`, bound by `GameLoopContractTest` in `lionengine-runtime/src/test/java` (test-scope `test-jar` dependency in `lionengine-runtime/pom.xml`);
   - test fixture published in a test JAR: `ControllableClock`, `RecordingScene` (runtime), `FakeWorkerPool`, `InboxSpy`, `ManualLoopDriver` (core), `FakeResourceProvider` (resources);
   - contract suite taking the content to hold, bound by each implementation in its own module: `ResourceProviderContract` (resources test JAR), bound by `FileProviderTest` (`@TempDir`) and `MemoryProviderTest`;
   - contract suite with hooks that inject events and run frames: `PlatformContract` (platform-api test JAR), bound by `HeadlessPlatformTest` in `test-support`; `RenderBackendContract` (render-api test JAR), bound by `RecordingBackendTest`;
   - hot-path data structure without allocation: `CommandBuffer` (parallel primitive arrays, chained setters on the last entry, sort into preallocated arrays) and its allocation test;
   - headless test of a game, the structure a game copies: `HeadlessGameTest` in `lionengine-examples/src/test/java`.

## 2. Commands

JDK 25 and Maven 3.9 (the build refuses an older JDK). From the repository root:

| Goal | Command |
|---|---|
| Everything the CI checks | `mvn -B verify`, then `python3 -I scripts/check-modules.py`, `python3 -I scripts/check-api-index.py`, `python3 -I scripts/check-provenance.py`, `python3 -I scripts/check-docs.py` |
| One module and what it needs | `mvn -B -pl lionengine-<module> -am verify` |
| With the platform tests (tag `platform`: real windows, need a display), as the Linux CI jobs run them | `mvn -B verify -Pplatform-tests`; on Linux without a display, `xvfb-run -a mvn -B verify -Pplatform-tests` |
| With the tests that need a sound device too (tag `audio-device`, ADR-0070): a desktop with a display and a sound device, never CI | `mvn -B verify -Paudio-device-tests` |
| Tests of one module | `mvn -B -pl lionengine-<module> -am test` |
| Checkstyle only (fast) | `mvn -B validate` |
| Android/DEX: lint `NewApi` on the shared sources, D8 and R8 builds of the installed modules (Android SDK needed, `ANDROID_HOME` set) | `mvn -B install`, then in `android/`: `./gradlew :api-lint:lintDebug :verification:verifyDebugDex :verification:verifyReleaseDex` |
| Android/DEX: the device tests and Java feature probes on an API 21 emulator or device (CI job `android`) | in `android/`: `./gradlew :verification:connectedDebugAndroidTest` |
| Sonar analysis (needs the owner's token) | `mvn -B verify -Psonar -Dsonar.host.url=https://sonarcloud.io -Dsonar.organization=b3dgs -Dsonar.projectKey=b3dgs_lionengine -Dsonar.token=<token>` |
| Sonar results of a pull request (no token) | `curl -s "https://sonarcloud.io/api/issues/search?componentKeys=b3dgs_lionengine&pullRequest=<number>&resolved=false"`; quality gate: `api/qualitygates/project_status?projectKey=b3dgs_lionengine&pullRequest=<number>` |

Never pass `-Dcheckstyle.skip`, `-DskipTests`, `-Dmaven.javadoc.skip` or `-o` to make a change pass. The only allowed `maven.javadoc.skip` is the property of a module that has no public type yet; the batch that adds its first public type removes it.

## 3. Checkstyle (b3dgs-buildtools 3.0.0-SNAPSHOT, checkstyle 14.1.0)

Runs at `validate` on `src/main/java` and fails on the first error. Tests are not checked by checkstyle, but follow the same style. The rules that matter in practice:

- **Header.** Every Java file starts with the exact GPL header of `checkstyle-header.txt` (years `2013-2026`, two ` * ` lines with a trailing space). Copy it from an existing file.
- **Braces.** `{` on its own line for every block, including classes, methods, `if`, loops, `switch`, lambdas and records; `}` alone on its line, so `else`, `catch` and `finally` start a new line.
- **Layout.** At most 120 columns; 4 spaces, no tab; a wrapped line starts with the operator (`+`, `&&`) and ends with the comma; one blank line between members (fields may stay grouped); no trailing comment after code on the same line.
- **Javadoc layout.** `/**` stands alone on its line and the text starts on the next one; every line starts with an aligned `*` followed by a space; block tags (`@param`, ...) start their line and are preceded by an empty ` *` line; Javadoc only where it documents a declaration (never inside a method body).
- **Javadoc style.** The first sentence (the summary) ends with a period. Paragraphs are a blank ` *` line, then `<p>` alone on its line, the text, `</p>`. Every `@param`, `@return` and `@throws` has a description. Tag order is `@param`, `@return`, `@see`, `@since`, `@throws`. A wrapped tag description is indented by at least four spaces. Checkstyle validates Javadoc that exists; missing Javadoc on public and protected members is caught by doclint (section 4). Private members are documented too, as in every reference file.
- **Numbers and strings.** Only `-255`, `-1`, `0`, `1`, `2`, `100`, `255` may appear as literals; any other number is a named constant, which is also required for every default value (`conventions.md`). A string literal may appear only once per file: use a constant for a repeated message. Character literals are not counted.
- **Size and complexity.** Per method: at most 2 `return` statements (1 for `void`), cyclomatic complexity 10, NPath 200, 30 statements, 50 NCSS, 150 lines, at most 3 boolean operators in one expression, at most one nested `if`, one nested loop and one nested `try`. Per class: at most 7 instantiated types (data abstraction coupling), 25 referenced types (fan-out), 100 methods, 2000 lines; anonymous classes at most 20 lines. Split into private methods or small package-private classes before reaching a limit.
- **Declarations.** Every class declares a constructor. A class with only private constructors is `final`, and a utility class hides its constructor. Fields are private (package and protected are allowed) and never initialized to their default value (`= 0`, `= null`, `= false`). A local variable that is not reassigned is `final`, and a `catch` parameter is `final`. Parameters are never reassigned. Nested types come after the methods. One top-level type per file.
- **Types and imports.** Declare `Map`, `Set` and `List`, not `HashMap`, `HashSet` or `TreeMap` (illegal in declarations). No static import in main code, no star import. Import groups `java`, `javax`, `org`, `android`, `com`, separated by a blank line and sorted.
- **Forbidden constructs.** Catching `Exception`, `RuntimeException`, `Throwable` or `Error`; declaring `throws RuntimeException`; `System.out.print`; `TODO:` comments (open an issue instead); `clone`; finalizers; mutable exceptions (exception fields are `final`); escaped Unicode characters; abbreviations of more than four capitals in a name (`readPng`, not `readPNG`); a `main` method (examples suppress it with the comment below); a `switch` without `default`, or with fall-through.
- **Suppression** is exceptional and always justified in the pull request: `// CHECKSTYLE IGNORE LINE: CheckName` on its own line affects that line and the next; `// CHECKSTYLE OFF: CheckName` and `// CHECKSTYLE ON: CheckName` frame a block. Never suppress a complexity limit: split the code.

## 4. Compiler, Javadoc, package rule and modules

- **Compiler:** `-Xlint:all -Werror`, so every warning fails the build. In practice:
  - exceptions declare no `serialVersionUID` (Sonar S4926) and are annotated `@SuppressWarnings("serial")` for the `serial` lint; their fields stay serializable (see `ResourceException`, which keeps the key path as a string);
  - no raw types or unchecked casts;
  - constructors never call an overridable method and never leak `this` (`this-escape`): prefer `final` classes;
  - no deprecated API.
- **Release level:** shared modules compile with `--release 21`, desktop-only modules (`platform-awt`, `render-awt`, `input-awt`, `audio-javasound`, `graphics-imageio`) with `--release 25` (ADR-0044).
- **Javadoc:** doclint `all`, failing on any warning, on public and protected members. Every public type, method, constructor, field, enum constant and record component (`@param` in the record comment) is documented; explicit record constructors too. `@since 11.0.0` goes on each public type.
- **Package rule** (`radial-encapsulation-maven-plugin`, ADR-0015, ADR-0037): inside a module a package may use itself and its ancestors only. A type used by several sub-packages lives in their common ancestor, usually the module's root package. Package names must not be string prefixes of each other (`load` next to `loader`). No module creates a sub-package named like another module's root.
- **Module rows:** `scripts/check-modules.py` enforces [`architecture/modules.md`](architecture/modules.md): main dependencies within the row; test scope only on the test JARs of modules already depended on, and on `test-support` when it does not depend on you (ADR-0045). The fakes and abstract contract suites of your module's contracts go in its `src/test/java`, published as its test JAR.

## 5. Sonar (`b3dgs` quality profile, SonarCloud project `b3dgs_lionengine`)

The CI runs the analysis when the `SONAR_TOKEN` secret is available, and the `sonar` job fails when the quality gate fails. The `b3dgs` profile of the organization is stricter than "Sonar way". A pull request is analyzed on its changed files, compared with `rewrite/v11`. The branch `rewrite/v11` is analyzed after every merge (the auto-merge workflow starts `build.yml`) and kept as a long-lived branch (pattern `rewrite/.*` in the SonarCloud project settings); any other branch built by hand is short-lived and counts every file that differs from `master` as new code, so an issue anywhere in the rewrite fails it. Target for every batch:
- the default quality gate on new code: coverage of at least 80 %, duplicated lines at most 3 %, no new bug or vulnerability, maintainability, reliability and security ratings A, hotspots reviewed;
- no new issue of any severity.

Rules that most often fire on engine code:

| Rule | What to do |
|---|---|
| S1192 duplicated literals, S1214 constants in interfaces | Constants in the implementing class or a final holder class, never in an interface |
| S3776 cognitive complexity above 15, S107 more than 7 parameters | Split the method; group parameters in a value type (outside hot paths) |
| S1609 single-method interface | Annotate role-named callbacks with `@FunctionalInterface` |
| S112, S1166, S2139, S108 | Throw specific exceptions, never swallow one, keep the cause, never leave a `catch` empty |
| S2142 | On `InterruptedException`, restore the interrupt flag (`Thread.currentThread().interrupt()`) before leaving |
| S2095, S2093 | Close streams with try-with-resources; the owner of a stream is documented (`conventions.md`) |
| S2384, S2386 | Copy mutable collections and arrays given or returned, or document the sharing explicitly when it is the contract (`TextureStore`, ADR-0047) and justify it in the pull request |
| S9395 implicit `long` to `double` conversion | Cast explicitly, arguments of `assertEquals(double, double, double)` included |
| S6213 restricted identifier, S7158 `length() > 0` | Never name a method `record`, `var` or `yield`; test emptiness with `isEmpty()` where the API level allows it, otherwise avoid the check (a separator field) |
| S2972 inner class over 40 lines of code | Split it (one role per class) or make it a top-level class |
| S1948, S2057, S4926 | Exceptions: serializable fields, no `serialVersionUID`, `@SuppressWarnings("serial")` (S2057 does not apply to exceptions) |
| S1711 standard functional interface redefined | Ignored for the whole project in `pom.xml`: shared modules declare their own callback interfaces because `java.util.function` is not on Android API 21 (ADR-0037) |
| S1312 logger not `private static final LOG` | Ignored for the whole project in `pom.xml`: a `Log` is an instance passed explicitly or held by the platform object, never static (ADR-0031) |
| S4032 package holding only `package-info.java` | Ignored for the whole project in `pom.xml`: every module declares its root package before it has types ([`features/build-skeleton.md`](features/build-skeleton.md)) |
| S3423 POM element order, S103 lines over 120 characters | Keep the order of the aggregator `pom.xml`: `modules`, `scm`, `properties`, `dependencies`, `repositories`, `build`; wrap long lines in POMs too |
| S7637 action not pinned (workflows) | Pin every third-party action to a full commit SHA, with the version in a comment |
| S3077, S2886 | Shared mutable state: atomics or immutable values; only the documented thread-safe entry points (inbox, loop control) are concurrent |
| S1172, S1068, S1144, S1854 | No unused parameters, fields, private methods or dead stores. A callback parameter an implementation does not need is documented as such |
| S1186 | An intentionally empty method contains a comment saying why |
| Tests: S2699, S5778, S5785, S5786, S5976, S3415, S2187 | Every test asserts; one call inside `assertThrows`; `assertTrue`/`assertFalse` for booleans; test classes and methods package-private; parameterized tests instead of copies; expected value first; no test class without tests |

## 6. Android/DEX constraints (shared modules)

Shared code is everything except the five desktop-only modules. It must dex and run on API 21 (ADR-0012, ADR-0044, [`android-compatibility.md`](android-compatibility.md)):
- **Forbidden** (failure observed on API 21; alternatives in [`android-compatibility.md`](android-compatibility.md)):
  - qualified enum constants in a pattern `switch` over a sealed interface, virtual threads, `VarHandle` (the build fails);
  - `String.lines()`, `HexFormat`, sequenced-collection methods (`getFirst`, `addFirst`, `reversed`, ... on a `List` or a `LinkedHashSet`), `CharSequence.isEmpty()` (also on a `StringBuilder`), `String.formatted`, `InputStream.readAllBytes`, `Collectors.teeing`;
  - `java.nio.file`, `SplittableRandom`, `Cleaner`.
- **Not verified, so not used:** anything newer than API 21 that is not in the verified list. When a task needs one, use the basic alternative (a hand-written loop, `java.io`) and say so in the pull request; the Android job then shows whether it works (lint `NewApi`, emulator tests), and a new probe in `android/verification` records it.
- **Verified:** lambdas, `var`, switch expressions, text blocks, records, sealed classes, pattern matching for `switch` and `instanceof`, default, static and private interface methods, `List.of`, `Map.of`, `Set.of`, `List.copyOf`, `Set.copyOf`, `Map.copyOf`, `String.isBlank`, `strip`, `repeat`, `Math.clamp`, `java.util.zip`.
- **Never** in shared public APIs or hot paths: `java.util.function`, `Optional`, streams, `java.time` (ADR-0037). No reflection, `ServiceLoader` or class scanning (ADR-0020). No `java.desktop` type outside the desktop-only modules.

## 7. Performance (per-step and per-frame paths)

Android's runtime has no escape analysis and a stop-the-world collector on old devices, so these rules are strict (ADR-0019, ADR-0030):
- No allocation in steady state: preallocate in constructors or at load time, reuse instances (inbox events, command entries, query buffers), grow capacity only at documented points (flush, between frames).
- Indexed loops over arrays or `List` with `get(i)`; no enhanced `for` over collections (it allocates an iterator), no streams.
- No boxing: primitive fields, arrays and own primitive collections; no `Map<Long, ...>` on a hot path. No varargs (they allocate an array). No capturing lambda created per call: keep callbacks in fields.
- `enum.values()` clones its array: cache it in a static final field.
- No string building, formatting or concatenation on a hot path; logging is guarded by `isEnabled` with constant messages (ADR-0031).
- Exceptions only for contract violations, never for control flow.
- `double` for world quantities and `float` only at the render boundary (ADR-0014). Integer arithmetic where results must be identical on every platform (random source, shake noise).
- No `HashMap` iteration or identity hash where the order is observable (ADR-0030).
- Prove it: allocation tests with the JVM per-thread allocation counter (test code only), and the benchmark harness of `testing.md`; a hot-path change without a measurement is not accepted.
- Threads: everything runs on the runtime thread unless its Javadoc says otherwise. Only the inbox (`post`), the loop control requests and worker tasks cross threads; no `synchronized` on a per-step path.

## 8. Maintainability

- One responsibility per class, package-private by default, `final` unless designed for extension. Interfaces only for contracts with several implementations; no `I` prefix and no `Impl` suffix.
- Value types are records or final immutable classes, validated at construction (`IllegalArgumentException`, also for a `null` argument, with a message naming the value). Wrong-state calls throw `IllegalStateException`.
- `null` only where the Javadoc says so (expected absence plus a `has...` check); never `Optional`.
- No static mutable state and no singleton; everything is wired explicitly by the application or `composition`.
- Units in names when the type does not carry them (`stepSeconds`, `widthPixels`); defaults as named constants with their unit in the Javadoc.
- Every class has its unit test in the same package under `src/test/java`; a contract has an abstract suite in its module's test JAR, bound by each implementation.
- No code without a requirement: a behavior not written in the feature page is an engine change request, not a guess (`AGENTS.md`, section 2).

## 9. Definition of done for a code batch

- [ ] `mvn -B verify` and the four scripts pass on the whole reactor; no skip flag.
- [ ] No new Sonar issue; coverage of new code at least 80 % (JaCoCo report in `target/site/jacoco`).
- [ ] Complete Javadoc; `maven.javadoc.skip` removed from the module if it now has a public type.
- [ ] Allocation test for every per-step or per-frame path the batch adds.
- [ ] Android constraints of section 6 respected; anything unverified reported in the pull request.
- [ ] Example and API-index entry when the batch makes a contract usable; `interfaces.md` updated when a seam changes; traceability test column filled; [`engine-features.md`](engine-features.md) updated when a capability changes; the batch's row of the batch status in [`features/README.md`](features/README.md) updated, with the next batch.
