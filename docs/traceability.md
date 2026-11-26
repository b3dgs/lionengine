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
| REQ-TECH-006 | Must | VALIDATED | ADR-0001, ADR-0012, ADR-0041, ADR-0044, ADR-0049 | — | `android` CI job: `verifyDebugDex` (D8) and `verifyReleaseDex` (R8) on every shared module; device tests `CoreTest`, `RuntimeTest`, `ResourcesTest`, `RenderTest`, `PlatformTest`, `GraphicsTest` on an API 21 emulator (`android/verification`) | verified by the Android job |
| REQ-TECH-007 | Must | VALIDATED | ADR-0001, ADR-0012, ADR-0041, ADR-0044 | — | `android` CI job: lint `NewApi` as an error on the shared sources (`android/api-lint`); probes `LanguageProbeTest`, `LibraryProbeTest` on an API 21 emulator; results in `android-compatibility.md` | verified by the Android job |
| REQ-TECH-008 | Must | VALIDATED | ADR-0001 | — | — | no test yet |
| REQ-CORE-002 | Must | VALIDATED | ADR-0012, ADR-0015 | — | — | no test yet |
| REQ-CORE-003 | Must | VALIDATED | ADR-0012, ADR-0032, ADR-0042, ADR-0049, ADR-0051 | — | javadoc doclint `all`, `failOnWarnings`; probe: an undocumented public class fails the build | verified by the build (probe) |
| REQ-CORE-004 | Must | VALIDATED | ADR-0012, ADR-0045, ADR-0049, ADR-0050 | — | — | no test yet |
| REQ-CORE-005 | Must | VALIDATED | ADR-0012, ADR-0015, ADR-0040, ADR-0042, ADR-0045, ADR-0046, ADR-0047, ADR-0048, ADR-0049 | — | `radial-encapsulation-maven-plugin` per module, `scripts/check-modules.py`, Maven graph; probes: sibling package, undeclared module, row and test-scope violations | verified by the build (probes) |
| REQ-CORE-006 | Should | VALIDATED | ADR-0031 | — | `LogTest` (levels, arguments, sink replacement, allocation with a disabled level), `SystemErrSinkTest` | verified by tests |
| REQ-TEST-001 | Must | VALIDATED | ADR-0017, ADR-0022, ADR-0045, ADR-0050 | — | `ControllableClock` (runtime test JAR) used by `GameLoopTest`; `HeadlessPlatform` (`test-support`) bound to `PlatformContract` by `HeadlessPlatformTest`; `RecordingBackend` bound to `RenderBackendContract` by `RecordingBackendTest`; `HeadlessGameTest`, `MovingRectangleTest` (examples) | verified by tests |
| REQ-DOC-001 | Must | VALIDATED | ADR-0023, ADR-0042 | — | `DefinitionExampleTest`, `RandomExampleTest`, `GameLoopExampleTest`, `SceneExampleTest`, `ResourcesExampleTest`, `ImageDecodingExampleTest`, `ImageCacheExampleTest`, `SpriteSheetExampleTest`, `SheetLoaderExampleTest`, `SpriteExampleTest`, `SpriteSharingExampleTest`, `ColorTransformExampleTest`, `HeadlessGameTest`, `MovingRectangleTest` and `MovingRectangleDesktopTest` (platform test) run the examples in the build | partial: examples of `core`, `runtime`, `resources`, `graphics` and the platform and rendering batches; the other modules have none yet |
| REQ-DOC-002 | Should | VALIDATED | ADR-0023 | — | `scripts/check-api-index.py` on every filled row (`core`, `runtime`, `resources`, `graphics`, `platform-api`, `render-api`, `platform-awt`, `render-awt`, `test-support`) | partial: the other modules have no row filled yet |
| REQ-DOC-003 | Should | VALIDATED | ADR-0023 | — | — | no test yet |
| REQ-DOC-004 | Should | VALIDATED | ADR-0023, ADR-0042 | — | sources and Javadoc JARs built by `mvn -B verify` | verified by the build |
| REQ-DOC-005 | Should | VALIDATED | ADR-0023, ADR-0032, ADR-0042 | — | `scripts/check-api-index.py`; probe: unknown type and example fail | check in place; the index names the types of the implemented modules |
| REQ-DOC-006 | Should | VALIDATED | ADR-0023, ADR-0032, ADR-0042 | — | `scripts/check-provenance.py`; probes: missing entry and incomplete generated entry fail | check in place; no asset yet |
| REQ-DOC-007 | Must | VALIDATED | ADR-0012, ADR-0032 | — | — | no test yet |
| REQ-FE-001 | Must | VALIDATED | ADR-0022 | — | `PlatformContract` (`platform-api` test JAR), bound by `HeadlessPlatformTest` and `AwtPlatformContractTest` (platform test) | verified by tests |
| REQ-FE-002 | Should | VALIDATED | ADR-0022 | — | `AwtPlatformContractTest`, `AwtRenderBackendContractTest`, `HeadlessPlatformTest`, `RecordingBackendTest` | partial: desktop AWT and headless exist; the capability matrix per frontend and Android come later |
| REQ-FE-003 | Must | VALIDATED | ADR-0002, ADR-0008 | — | `PlatformContract.testInitialState`, `testApplyAtFrameBoundary`, `testFiltersOfBackend`; `RenderBackendContract`; `scripts/check-modules.py` (no frontend dependency of `platform-api` or `render-api`); `MovingRectangleDesktopTest` (platform test) | verified by tests |
| REQ-PLATFORM-001 | Must | VALIDATED | ADR-0008, ADR-0022, ADR-0033, ADR-0045 | — | `AwtPlatformContractTest`, `AwtPlatformWindowTest`, `AwtRenderBackendWindowTest` (platform tests); `scripts/check-modules.py`; `android` CI job: desktop-only modules are outside the Android build, and `java.desktop` in a shared module fails it (compilation of `android/api-lint`; probe shown in pull request #909, commit `60fd53c`) | verified by the build and the Android job |
| REQ-PLATFORM-002 | Must | VALIDATED | ADR-0017, ADR-0018, ADR-0031, ADR-0040, ADR-0045, ADR-0046 | — | `PlatformContract` (`testBackgroundAndForeground`, `testCloseStops`, `testCloseVeto`, `testListenerAddedDuringDelivery`, `testResize`, `testInformationalEvents`) bound by `HeadlessPlatformTest`; `HeadlessGameTest` | partial: context loss and recovery by a backend come with platform batches 2 and 3 |
| REQ-DISPLAY-001 | Must | VALIDATED | ADR-0002, ADR-0043 | — | — | no test yet |
| REQ-DISPLAY-002 | Must | VALIDATED | ADR-0002, ADR-0006 | — | — | no test yet |
| REQ-DISPLAY-003 | Must | VALIDATED | ADR-0002, ADR-0022, ADR-0033, ADR-0043 | — | `DisplayModeTest` (value type, unknown values) | partial: enumeration and selection pending |
| REQ-DISPLAY-004 | Must | VALIDATED | ADR-0002, ADR-0006, ADR-0014, ADR-0046 | — | `FrameScalingTest` (each policy); `PlatformContract.testResize` (logical frame unchanged) | verified by tests |
| REQ-DISPLAY-005 | Must | VALIDATED | ADR-0002, ADR-0018 | — | — | no test yet |
| REQ-DISPLAY-006 | Must | VALIDATED | ADR-0002 | — | — | no test yet |
| REQ-DISPLAY-007 | Must | VALIDATED | ADR-0006, ADR-0046 | — | `FrameScalingTest` (`FIT`, `INTEGER`, `STRETCH`, `INTEGER` fallback, empty surface); `PlatformContract.testIntegerFallback` (effective policy reported) | verified by tests |
| REQ-DISPLAY-008 | Must | VALIDATED | ADR-0006, ADR-0018, ADR-0046 | — | `DisplayRequestTest` (request and effective-result values) | partial: delivery at the frame boundary pending |
| REQ-DISPLAY-009 | Should | VALIDATED | ADR-0025, ADR-0043 | — | — | no test yet |
| REQ-DISPLAY-010 | Should | VALIDATED | ADR-0043 | — | `AwtWindowEventsTest.testDevicePixels` (conversion with a fake scale) | partial: the platform test on a scaled desktop display is a hardware test, not run |
| REQ-FILTER-001 | Must | VALIDATED | ADR-0002, ADR-0025 | — | `OutputFilterTest` (standard identifiers) | partial: capability query pending |
| REQ-FILTER-002 | Should | VALIDATED | ADR-0002, ADR-0006, ADR-0046 | — | `PlatformContract.testFiltersOfBackend` (unsupported filter skipped and reported) | partial: fallback replacements come with output filtering |
| REQ-FILTER-003 | Could | VALIDATED | ADR-0002, ADR-0033, ADR-0047 | — | `OutputFilterTest` (custom ids) | partial: registration on the backend pending |
| REQ-FILTER-004 | Should | VALIDATED | ADR-0025, ADR-0033 | — | — | no test yet |
| REQ-LOOP-001 | Must | VALIDATED | ADR-0017 | — | `LoopDriverContract` (core test JAR), bound by `GameLoopContractTest` and `ManualLoopDriverTest` | verified by tests |
| REQ-LOOP-002 | Must | VALIDATED | ADR-0017, ADR-0018 | — | `LoopDriverContract` run on two implementations (`GameLoop`, `ManualLoopDriver`) | verified by tests |
| REQ-LOOP-003 | Should | VALIDATED | ADR-0017 | — | `GameLoopTest` (call order: inbox, updates, render) | verified by tests |
| REQ-LOOP-004 | Must | VALIDATED | ADR-0001, ADR-0017 | — | `GameLoopTest.testIndependentOfCadence` | verified by tests |
| REQ-LOOP-005 | Must | VALIDATED | ADR-0001, ADR-0017 | — | `GameLoopTest` (several updates, one render; `alpha` given to the render) | partial: extrapolation itself is presentation code of the traits batch |
| REQ-LOOP-006 | Must | VALIDATED | ADR-0001, ADR-0017 | — | by design: the render receives `alpha` only and never feeds the simulation | partial: the state-level test comes with `Transformable` |
| REQ-LOOP-007 | Must | VALIDATED | ADR-0017 | — | `GameLoopTest` (exact update counts, `alpha` bounds, cadence independence), `GameLoopExampleTest` | verified by tests |
| REQ-LOOP-008 | Must | VALIDATED | ADR-0017, ADR-0031 | — | `GameLoopTest.testLagCap`, `LoopConfigTest` | verified by tests |
| REQ-LOOP-009 | Must | VALIDATED | ADR-0017 | — | `GameLoopTest.testPauseResume`, `testTimeScale` | verified by tests |
| REQ-LOOP-010 | Must | VALIDATED | ADR-0017, ADR-0018 | — | `GameLoopTest` (update, render and listener failures, stop during an iteration), `GameLoopInboxTest.testEventFailure`, `LoopDriverContract` | verified by tests |
| REQ-LOOP-011 | Must | VALIDATED | ADR-0031 | — | `GameLoopTest.testNoDrift` (10^6 steps), `testRemainderCarried`, `LoopConfigTest` (ranges), lag counters and listener | verified by tests |
| REQ-LOOP-012 | Should | VALIDATED | ADR-0031 | — | `GameLoopTest.testSnapping`, `testSnappingDisabled`, unscaled delta during pause and at time scale 0.25 | verified by tests |
| REQ-RT-001 | Must | VALIDATED | ADR-0018, ADR-0040 | — | `GameLoopInboxTest.testConcurrentPosts` (delivery on the runtime thread); thread rules in the Javadoc | partial: contract tests on a test platform come with `test-support` |
| REQ-RT-002 | Must | VALIDATED | ADR-0018, ADR-0040 | — | `GameLoopInboxTest` (order, delivery before the updates, posts during a delivery) | verified by tests for the inbox; listener registration rules come with each listener holder |
| REQ-RT-003 | Must | VALIDATED | ADR-0018, ADR-0007, ADR-0055 | — | `ThreadConfinementTest` (owner is the first user, other threads refused, race, no allocation); `CacheConventionContract.testOtherThreadRefused` bound by `ImageCacheConventionTest`; `ImageCacheTest` (`testOtherThread`; decoded pixels never change: copies are separate, `testDerivedImage`) | verified by tests (images; audio and tile map caches bind the suite with their caches) |
| REQ-RT-004 | Should | VALIDATED | ADR-0019, ADR-0031, ADR-0040, ADR-0055 | — | `AsyncLoadingTest` (states at inbox delivery, synchronous pool, failures, cancellation and late result, full inbox with and without worker, interrupted worker); `ImageCacheTest` (`testLoadAsync`, failures, cancel and share); `LoadingScreenExampleTest`; `AwtWorkerPoolTest` (the desktop worker pool); device test `CoreTest.testAsyncLoading` on API 21 | verified by tests (images; sounds and maps add their requests) |
| REQ-RT-005 | Must | VALIDATED | ADR-0019, ADR-0026, ADR-0027, ADR-0055 | — | `CacheConventionContract` bound by `ImageCacheConventionTest` (reference counting, release, idempotent release, no eviction, reload, failed reload); `ImageCacheTest` (texture released with the last handle, `testReload`, `testReloadLimit`, `testLimit`); `SheetGroupTest` (scope release: `testRelease`, `testHandles`) | partial: context-loss recovery is tested by the backends that can lose a context (Phase 4) |
| REQ-RT-006 | Must | VALIDATED | ADR-0019 | — | JVM allocation tests with the per-thread counter: `LogTest.testDisabledLevelAllocatesNothing`, `GameLoopInboxTest.testIterationAllocatesNothing`, `SceneManagerLoopTest.testIterationAllocatesNothing`, `CommandBufferTest.testSteadyStateAllocatesNothing`, `ThreadConfinementTest.testCheckAllocatesNothing`, `SpriteSheetTest.testLookupAllocatesNothing`; no finalizer or `Cleaner` (review) | partial: the per-step paths of the later modules (ECS, sprites, animation, audio) add their own tests; Android measured later (ADR-0024) |
| REQ-RT-007 | Must | VALIDATED | ADR-0020, ADR-0032 | — | `RegistryTest` (registration, unknown and duplicate ids, typed parameters, two registries); `DefinitionExampleTest` | verified by tests; the no-reflection check of shared modules is not written yet |
| REQ-RT-008 | Must | VALIDATED | ADR-0020 | — | `PlatformContract.testUnknownCapability`; `HeadlessPlatformTest.testCapability` (present and absent) | verified by tests |
| REQ-RT-009 | Must | VALIDATED | ADR-0031, ADR-0040 | — | `GameLoopInboxTest.testOverflow` (counted, reported once, control requests while full), `testConcurrentPosts` | verified by tests |
| REQ-RT-010 | Should | VALIDATED | ADR-0031, ADR-0040 | — | `AsyncLoadingTest` (state changes at delivery only, `IllegalStateException` before ready, late result discarded after cancel); `LoadGroupTest` (mean progress, state, failure, cancel, no allocation); worker counts by the platforms (`HeadlessPlatformTest`, `AwtWorkerPoolTest`) | verified by tests |
| REQ-RES-001 | Must | VALIDATED | ADR-0005, ADR-0032 | — | `ResourceProviderContract` bound by `StreamProviderTest`, `MemoryProviderTest`, `FileProviderTest`; `ResourcesTest`; `ResourcesExampleTest` | verified by tests |
| REQ-RES-002 | Should | VALIDATED | ADR-0005, ADR-0032 | — | `FileProviderTest`, `MemoryProviderTest`, `StreamProviderTest` (with the provider suite) | partial: archive and Android asset providers pending |
| REQ-RES-003 | Must | VALIDATED | ADR-0005, ADR-0019 | — | `ResourceProviderContract.testCloseTwice`, `testOpenNewStream`; `ResourcesTest.testCallerOwnsStream`; `FileProviderTest` (no `File` in the contract, key mapped under the root) | verified by tests |
| REQ-RES-004 | Must | VALIDATED | ADR-0005, ADR-0007, ADR-0045 | — | `CacheConventionContract` (`resources` test JAR) bound by `ImageCacheConventionTest` | verified by tests (graphics; audio and tile maps bind it with their caches) |
| REQ-RES-005 | Must | VALIDATED | ADR-0007 | — | `ResourceKeyTest` (key rules, equality); `CacheConventionContract` (key equality, idempotent release, missing-resource exception) bound by `ImageCacheConventionTest`; `ImageCacheTest` (key and discriminator) | verified by tests |
| REQ-RES-006 | Must | VALIDATED | ADR-0032 | — | `ResourceExceptionTest` (reasons, key, cause); `ResourcesTest` (order, override, no fallback, read-only refusal, first writable); `ResourceProviderContract` (error reasons, `testConcurrentReads`) bound by every provider | verified by tests |
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
| REQ-SPR-001 | Must | VALIDATED | ADR-0003, ADR-0026 | — | `PixelBufferTest` (pixels changed by the caller, arrays copied in and out); `ImageDecoderContract` (`testNewBuffer`); `ImageCacheTest` (`testDerivedImage`: a modified copy becomes a derived image, the cached image never changes); `SpriteCacheTest` (`testModifiedCopy`: a modified copy drawn by its own sprites, the cached image and its sprites unchanged); `ImageDecodingExampleTest`, `ImageCacheExampleTest`, `SpriteSharingExampleTest` | verified by tests |
| REQ-SPR-002 | Must | VALIDATED | ADR-0003, ADR-0026, ADR-0058 | — | `AnimatorTest.testDeterministic` (the same animation gives the same frame and time after 1,000 short steps and 250 steps four times longer), `testCarryAndLongStep`; `AnimatedSpriteExampleTest` (deterministic progress in the game loop); sheets: see REQ-SHEET-001 to REQ-SHEET-003; device test `GraphicsTest.testAnimator` on API 21 | verified by tests |
| REQ-SPRITE-001 | Must | VALIDATED | ADR-0003, ADR-0026 | — | `SpriteTest` (`testImage`, `testSheet`, `testLoadErrors`: missing key as `ResourceException` `MISSING`, invalid data as `ImageDecodingException`, nothing cached); `SpriteExampleTest` (headless load and render) | verified by tests |
| REQ-SPRITE-002 | Must | VALIDATED | ADR-0005, ADR-0007, ADR-0026, ADR-0055 | — | `ImageCacheTest` (key and discriminator, hit, miss, textures, release, double release, memory count and opt-in limit); `CacheConventionContract` bound by `ImageCacheConventionTest`; `ImageCacheExampleTest`; `SpriteCacheTest` (sprites of several users over one cached image draw one texture, each releases its own handle, the texture leaves with the last one; a crowd of 1,000 sprites; a sheet sharing an image; a miss; a reload) and `SpriteSharingExampleTest`; `ColorBakeTest` (a baked variant shared by every code baking the same transform); device test `GraphicsTest` on API 21 | verified by tests |
| REQ-SPRITE-003 | Must | VALIDATED | ADR-0014, ADR-0016, ADR-0026 | — | `SpriteTest` (`testRender`, `testComposition`: scale then rotation then translation about the pivot, mirror; `testRotationKept`: rotation counter-clockwise on screen, not normalized); `AwtRenderBackendTest` (`testCompositionOrder` and `testRotation` on pixels); `SpriteExampleTest`; device test `GraphicsTest` on API 21 | verified by tests |
| REQ-SPRITE-004 | Must | VALIDATED | ADR-0003, ADR-0026, ADR-0056 | — | `ColorMatrixTest`, `ColorTransformTest` (description, mask, grading, tint, alpha, order, CPU path independent of the split, split per capability); `ColorBakeTest` (CPU bake cached and shared); `SpriteTest` (`testColor`, `testCapability`: sprite render with and without the per-frame capability, `testSheetVariant`); `RenderBackendContract.testColorEffects` (capability query), `RecordingBackendTest.testColorEffects`; `AwtRenderBackendTest.testColorParity` (AWT per-frame tint and alpha against the CPU bake); `ColorTransformExampleTest`; device test `GraphicsTest` on API 21 | partial: OpenGL and Android parity come with those backends |
| REQ-SPRITE-005 | Should | VALIDATED | ADR-0003, ADR-0026, ADR-0057 | — | `ColorMappingTest` (exact ARGB match, single pass, entries replaced and removed, order, equality, text, no allocation); `ColorTransformTest` (`testMapping`: first part, static on every backend; `testEqualityAndText`); `SheetLoaderTest` (templates kept as mappings, a key color given twice in two forms); `SpriteSheetTest` (`testNoTemplate`, `testTemplates`); `TemplateVariantTest` (a definition read through the resources, two variants drawn by two sprites, one variant per mapping whatever the template name, combined with grading and mask); `TeamColorExampleTest`; device test `GraphicsTest.testTemplateVariant` on API 21 | verified by tests |
| REQ-SPRITE-006 | Must | VALIDATED | ADR-0003, ADR-0026 | — | `SpriteTest` (`testImage`, `testOddCenter`, `testSheet`: default pivot at the image center or the pivot of the sheet; `testPivotPlacement`: the pivot is drawn at the position and stays there when scaled and rotated); `SpriteExampleTest`; device test `GraphicsTest` on API 21 | verified by tests |
| REQ-SPRITE-007 | Must | VALIDATED | ADR-0003, ADR-0026 | — | `SpriteTest` (`testRender`, `testRenderAt`, `testLayers`, `testReleased`: one command per render on the recording backend of `test-support`, nothing added for a released image); `Sprite` API reviewed: `graphics` and `render-api` types only; module checks (REQ-CORE-005) | verified by tests |
| REQ-SPRITE-008 | Could | VALIDATED | ADR-0003, ADR-0026 | — | review of `CommandBuffer` (plain entries in parallel arrays, read by rank: a backend may batch consecutive entries of one texture) | verified by review |
| REQ-SPRITE-009 | Must | VALIDATED | ADR-0026, ADR-0033, ADR-0054 | — | `PngDecoderTest` (every color type and bit depth with each filter type, scaling, 16-bit reduction, `tRNS`, rejections: interlaced, animated, truncated, CRC, invalid chunks, size limits); `ImageDecoderContract` bound by `PngDecoderContractTest`; `PixelBufferTest`; device test `GraphicsTest` on API 21 | verified by tests |
| REQ-SPRITE-010 | Must | VALIDATED | ADR-0026, ADR-0033, ADR-0047 | — | `CommandBufferTest` (fields, defaults, `testPresentationOrder`, `testStableSort`, viewports, growth, `testSteadyStateAllocatesNothing`); `RenderBackendContract`; `MovingRectangleTest`; `SpriteTest` (`testLayers`, `testSteadyStateAllocatesNothing`: 64 sprites a frame for 1,000 frames) | partial: nearest-neighbor sampling is checked with `render-awt` (platform batch 3) |
| REQ-ANIM-001 | Must | VALIDATED | ADR-0026, ADR-0033 | — | `AnimationTest` (defined in code: one duration for every frame or one per frame, copies, equality, loop names, invalid frames, durations and loop mode); `SheetLoaderTest` (loaded from the sheet definition, `frameDurations` overriding `frameDuration`, each invalid animation field located in the file); `SpriteSheetTest` (`testNoAnimation`, `testAnimations`); `AnimationDefinitionExampleTest`; device test `GraphicsTest.testAnimation` on API 21 | verified by tests |
| REQ-ANIM-002 | Must | VALIDATED | ADR-0003, ADR-0058 | — | `AnimatorTest` (`testNew`, `testPlay`, `testStop`, `testPauseResume`, `testResumeWhilePlaying`, `testPauseKeepsTime`: every call in every state, invalid calls refused without change; `testLoop`, `testPingPong`, `testOnce`, `testFrameDurations`); `AnimatorExampleTest`; device test `GraphicsTest.testAnimator` on API 21 | verified by tests |
| REQ-ANIM-003 | Must | VALIDATED | ADR-0003, ADR-0017, ADR-0058 | — | `AnimatorTest` (`testReadingChangesNothing`: reading the frame never changes the animator; `testDeterministic`, `testInvalidStep`: time advances only by `update(stepSeconds)`); `AnimatorExampleTest`; `AnimatedSpriteTest` (`testRenderReadsOnly`: rendering at interpolated positions changes neither the frame nor the time; `testUpdateDrawsFrames`); `AnimatedSpriteExampleTest` (game loop with a controllable clock, 144 Hz rendering for 60 steps per second: the same frame and time as an animator advanced by the steps alone); device test `GraphicsTest.testAnimatedSprite` on API 21 | verified by tests |
| REQ-SHEET-001 | Must | VALIDATED | ADR-0003, ADR-0033 | — | `SpriteSheetTest` (index math, margin, spacing, derived and given counts, grid outside the image, out-of-range indexes, no allocation per lookup); `SheetGridTest`; `SpriteSheetExampleTest`; device test `GraphicsTest.testSpriteSheet` on API 21 | verified by tests |
| REQ-SHEET-002 | Should | VALIDATED | ADR-0007, ADR-0026, ADR-0028 | — | `SheetGroupTest` (duplicate and unknown ids, shared images, handles, release, failures, other thread); `SpriteSheetExampleTest`; device test `GraphicsTest.testSpriteSheet` on API 21 | verified by tests |
| REQ-SHEET-003 | Must | VALIDATED | ADR-0032, ADR-0033 | — | `SheetLoaderTest` (valid file with every field, defaults, each missing field, each invalid field including animations and templates, a key color given twice in two forms, templates kept, unknown fields, frames and grid against the image, resource errors, limits); `SheetLoaderExampleTest`; device test `GraphicsTest.testSheetLoader` on API 21 | verified by tests |
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
| REQ-SCENE-001 | Must | VALIDATED | ADR-0021, ADR-0031, ADR-0036 | — | `SceneManagerTest` (lifecycle order), `SceneManagerLoopTest` (with `GameLoop` and the controllable clock), `SceneExampleTest`; `runtime` on `core` only by `scripts/check-modules.py` | verified by tests |
| REQ-SCENE-002 | Must | VALIDATED | ADR-0021, ADR-0031, ADR-0059 | — | `SceneOverlayTest.testSwitchReplacesStack`, `testLeaveAllFailures`; `SceneManagerTest.testSwitchAtFrameBoundary`, `testFirstRequestWins`, `testClear`; `SceneManagerLoopTest.testSwitchInCatchUpFrame`, `testSwitchWhilePaused`, `testIterationAllocatesNothing` | verified by tests |
| REQ-SCENE-003 | Should | VALIDATED | ADR-0031, ADR-0059 | — | `SceneOverlayTest` (`testPush`, `testPop`, `testPopFromOverlay`: push and pop at a frame boundary, enter and exit of the pushed or popped scene; only the top scene updated, every scene rendered bottom to top, the covered scene not exited; `testDeepStack`, `testOneChangePerFrame`, `testSwitchReplacesStack`, `testInvalidRequests`, failures, `testPushPopAllocatesNothing`); `PauseOverlayExampleTest`; device test `RuntimeTest.testOverlayStack` on API 21 | verified by tests |
| REQ-SCENE-004 | Must | VALIDATED | ADR-0036 | — | `SceneFlowTest` (`testStart`: start scene entered, context given; `testNewInstancePerEntry`: a new instance at each entry, context passed to the factories; `testSeveralFlows`); `SceneFlowExampleTest`; device test `RuntimeTest.testSceneFlow` on API 21 | verified by tests |
| REQ-SCENE-005 | Must | VALIDATED | ADR-0036 | — | `SceneFlowTest.testResolutionOrder` (top scene first, then any scene, declaration order, conditions, no match returning `false`), `testFactoryFailures` (invalid trigger name); `SceneFlowExampleTest` (condition on the context) | verified by tests |
| REQ-SCENE-006 | Must | VALIDATED | ADR-0036 | — | `SceneFlowTest` (`testSwitchAtFrameBoundary`: application at the next boundary, lifecycle order of `SWITCH`; `testOnePending`; `testExit`: `EXIT` leaves the stack and requests the loop stop); `SceneFlowOverlayTest` (`PUSH` opens an overlay, `POP` closes it, switch and exit from an overlay leave the whole stack top-down, a `POP` without overlay refused at application); `SceneOverlayTest` (the manager's push and pop); `SceneFlowExampleTest`; device test `RuntimeTest.testSceneFlowOverlay` on API 21 | verified by tests |
| REQ-SCENE-007 | Must | VALIDATED | ADR-0036 | — | `SceneFlowTest.testInvalid` (invalid and duplicated ids, start given twice, undeclared source and target, invalid trigger name, wrong shape for `SWITCH` and `EXIT`), `testInvalidStartAndNulls`; `SceneFlowOverlayTest.testInvalidShapes` (`PUSH` without target, `POP` with a target, `POP` from a scene no `PUSH` opens) | verified by tests |
| REQ-SCENE-008 | Should | VALIDATED | ADR-0036 | — | `SceneFlowTest` (`getSceneIds`, listener with source, trigger and target in `testSwitchAtFrameBoundary` and `testExit`); `SceneFlowExampleTest` | verified by tests |
| REQ-WORLD-001 | Must | VALIDATED | ADR-0021, ADR-0024 | — | — | no test yet |
| REQ-PHYS-001 | Should | VALIDATED | ADR-0021, ADR-0028, ADR-0030 | — | — | no test yet |
| REQ-DEF-001 | Must | VALIDATED | ADR-0020, ADR-0028 | — | `JsonReaderTest` (every error with line, column and path; numbers; escapes; limits), `JsonValueTest`, `DefinitionExceptionTest` | verified by tests |
| REQ-DEF-002 | Must | VALIDATED | ADR-0032 | — | `ConfigurationTest` (format and version, strict fields, comment), `JsonReaderTest` (limits), `RegistryTest`; `SheetLoaderTest` (first definition format of a module) | verified by tests |
| REQ-DEF-003 | Must | VALIDATED | ADR-0020, ADR-0032 | — | `RegistryTest` (id validation), `ConfigurationTest` (typed getters with and without defaults) | verified by tests |
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
| REQ-DET-003 | Should | VALIDATED | ADR-0031 | — | `RandomSourceTest` (SplitMix64 and xoshiro256** reference outputs, seed expansion, bias) | verified by tests |
| REQ-PERF-001 | Should | VALIDATED | ADR-0024, ADR-0031 | — | — | no test yet |
| REQ-PERF-002 | Should | VALIDATED | ADR-0024, ADR-0041, ADR-0044 | — | — | no test yet |
| REQ-SUPPORT-001 | Should | VALIDATED | ADR-0024, ADR-0031, ADR-0042 | — | — | no test yet |
| REQ-AGENT-001 | Should | VALIDATED | ADR-0053 | OQ-170 | — | no test yet |
| REQ-AGENT-002 | Should | VALIDATED | ADR-0053 | OQ-170 | `scripts/local-task.sh --dry-run`; run against a stub gateway (gate, escalation, scope check) | script exercised without a model |
| REQ-AGENT-003 | Should | VALIDATED | — | — | — | no test yet |
| REQ-AGENT-004 | Should | VALIDATED | ADR-0053 | — | `auto-merge-agent-prs.yml` (jobs `reset-review`, `verify` on the label) | no test yet: checked on the first `local/` pull request |

Retired identifiers are listed in `requirements.md`.
