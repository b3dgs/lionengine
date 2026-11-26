# ADR-0067 — Variant sets of batch 3: the seeded selection, the references to short sounds, and the play of a composed set

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-AUDIO-005`, `REQ-AUDIO-010`, `REQ-DET-001`, `REQ-DET-003`, `REQ-RT-006`
- Open questions resolved: none (Java signatures and details left open by the feature page, needed by batch 3 of [`../features/audio-short-and-long-sounds.md`](../features/audio-short-and-long-sounds.md))
- Supersedes / superseded by: none; completes [ADR-0027](0027-audio-policies-formats-latency-and-first-scope.md), [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md) and [ADR-0066](0066-short-sounds-wav-decoder-cache-and-voices.md) (all unchanged)

## Context

Batch 3 of the feature page is "variant sets". The policies are fixed:
- random selection with no immediate repeat, with optional weights; "composed" means several sounds started together as layers, without offsets (ADR-0027, decision 3);
- the variant randomness uses its own seeded generator, separate from the world's (ADR-0027, decision 3, `REQ-DET-001`);
- weights are non-negative `double` values, `1.0` by default, `0` excluding a variant, and the no-repeat rule applies when at least two variants have a positive weight (ADR-0033);
- variant selection is backend-independent and lives in `audio-api` (ADR-0004);
- the canonical type name is `VariantSet` (ADR-0037).

The core already has the seeded generator of the simulation, `RandomSource` (xoshiro256** seeded by SplitMix64, `REQ-DET-003`), shown by `RandomExample`. It gives the same sequence on every JVM and on Android and allocates nothing.

The documents leave open:
- how a set gets its generator, and whether the sets share one;
- the selection algorithm with weights and the no-repeat rule;
- how a set refers to its sounds and who releases them;
- what playing a composed set returns, since it starts several voices, and how it behaves when the voices are too few;
- the Java signatures.

## Decision

1. **One generator per set.** Each random set owns a `RandomSource` it creates from a `long` seed given at construction; the game never passes a generator, so no set can share the world's.
   - Each set's choices depend only on its seed and its own plays: playing another set, or the world drawing numbers, never changes them.
   - The same seed and the same plays give the same variants on every platform; a reference vector is checked on the JVM and on Android API 21.
   - No new random type: the core `RandomSource` is reused.
2. **Selection.**
   - A play draws `nextDouble()` once, multiplied by the sum of the weights of the eligible variants, and walks the variants in order: the eligible variants are those of positive weight, minus the variant of the last play when at least two variants have a positive weight.
   - If rounding leaves a remainder at the end of the walk, the last eligible variant is chosen.
   - With a single variant of positive weight, that variant is chosen every time without drawing.
   - The choice is made before the sound is played, whatever the backend, and counts as the last play even when no voice plays it.
   - The no-repeat rule applies to positions in the set, so a handle may appear twice.
3. **Weights.** `random(seed, variants)` gives every variant `DEFAULT_WEIGHT` (`1.0`); `random(seed, variants, weights)` takes one weight per variant. A weight that is negative, `NaN` or infinite, a length mismatch, no positive weight, or an infinite sum is refused with `IllegalArgumentException` at construction.
4. **References to the sounds.**
   - A set refers to the `ShortSound` handles it is given (the arrays are copied) without adding a reference or releasing them. The code that loaded the handles keeps them while the set is played and releases them, normally the scene on exit.
   - Playing a set with a released sound throws `IllegalStateException`, as playing that sound would. A lazy handle is decoded at the first play of its set.
   - A set holds no resource, so it has no `release()`.
5. **Types and signatures.** `VariantSet`, a final class with static factories `random(seed, variants...)`, `random(seed, variants, weights)` and `composed(layers...)`. Its getters are `isComposed()`, `size()`, `getSound(index)`, `getWeight(index)` (`1.0` for a layer), `getVoicesPerPlay()` (`1` or the number of layers) and `getLastIndex()` (`NO_INDEX` = `-1` before the first play and for a composed set). The selection itself stays package-private: `Audio` calls it.
6. **Play of a random set.** `Audio.play(set)` and `Audio.play(set, priority)` play the chosen variant as `play(sound, priority)` does and return its voice, or `NO_VOICE`. They refuse a composed set with `IllegalArgumentException`, since one voice cannot stand for its layers.
7. **Play of a composed set.** `Audio.play(set, priority, voices)` accepts either kind. It writes the voice of each sound to the caller's array (layer `i` to `voices[i]`, `NO_VOICE` for a layer not played) and returns the number of voices started, so the play allocates nothing.
   - Every layer is decoded and checked before any starts. They start in the same call, so they start together on the backend.
   - Under `REFUSE`, the set is refused with `IllegalStateException` before any layer starts unless a voice is free for each layer.
   - Under `STEAL` and `DROP_NEW`, each layer takes a voice as a single play would, at the set's priority. A layer never steals the voice of another layer of the same play.
   - The voices are stopped one by one with `stop(voice)`.
8. **No allocation.** Selection, the play of both kinds and the end of their voices allocate nothing, except the first play of a lazy sound; an allocation test covers it.

## Options considered

- Generator:
  - One `RandomSource` per set, from a seed (chosen): independent sets, simple determinism, no shared state.
  - One generator in `Audio` for every set (rejected: the order of plays of unrelated sets would change each set's choices).
  - A generator passed by the game (rejected: the world's could be passed, which ADR-0027 forbids).
- Handles:
  - The set refers to handles released by their owner (chosen): one owner per load, as for every cache handle (ADR-0007).
  - The set adds a reference and releases it (rejected: a second release path for the same load, and a set to release on every scene exit).
  - The set loads by keys from a cache (rejected: the set would own resources and need a lifecycle of its own).
- Return of a composed play:
  - The caller's array plus a count (chosen): no allocation, one voice per layer kept.
  - A new voice group object (rejected: an allocation per play, or a pooled object with its own lifecycle).
  - The first voice only (rejected: the other layers could not be stopped).
- Too few voices for a composed set:
  - Refused at once under `REFUSE`, best effort under the other policies, never stealing a layer of the same play (chosen).
  - Always all or nothing (rejected: `DROP_NEW` and `STEAL` are best-effort policies).

## Consequences

- Positive consequences:
  - Games play varied effects that never repeat at once, with weights, deterministic per seed and independent of the simulation.
  - Layered effects start together on every backend.
  - No allocation per play.
- Costs / constraints:
  - A composed set needs a caller array of `getVoicesPerPlay()` entries.
  - Offsets between layers stay out of scope.
  - `Audio.play(null)` no longer compiles without a cast, since `play(ShortSound)` and `play(VariantSet)` both match.
- Impact on modules and APIs: `audio-api`: `VariantSet`; `Audio.play(VariantSet)`, `play(VariantSet, int)` and `play(VariantSet, int, int[])`; its test JAR: `SilentAudioBackend` (extracted from `AudioTest`).
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: `VariantSetTest` (no repeat, weights, exclusion, a single variant, seeds, the reference vector, arguments); `AudioVariantSetTest` (composition on `FakeAudioBackend` under each policy, released and lazy sounds, an allocation test); `VariantSetExampleTest`; device test `AudioTest.testVariantSets`.
  - Documentation: the feature page, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md`, the roadmap and the traceability.
  - Android/DEX: plain Java (arrays, `RandomSource`), run by the device test of the module.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the batches; reviewed in the pull request of audio batch 3.
