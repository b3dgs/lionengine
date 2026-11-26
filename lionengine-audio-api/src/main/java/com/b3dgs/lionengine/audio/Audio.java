/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.audio;

import java.util.Arrays;

/**
 * Audio system of the game: plays short sounds and variant sets on a bounded number of voices of an
 * {@link AudioBackend}, with priorities and an overflow policy, and long sounds (ADR-0004, ADR-0018, ADR-0027,
 * ADR-0033, ADR-0065, ADR-0066, ADR-0067, ADR-0068, REQ-AUDIO-001, REQ-AUDIO-003, REQ-AUDIO-004, REQ-AUDIO-005,
 * REQ-AUDIO-007, REQ-AUDIO-010).
 *
 * <p>
 * The game creates one {@code Audio} over its backend and keeps it for its whole life: it is the only user of the
 * backend and its voice listener. {@link #play(ShortSound, int)} starts a short sound at once on a new voice, an
 * {@code int} returned to the caller, which can {@link #stop(int)} it; stopping a voice that already ended has no
 * effect. Voices are never reused, so a stale voice never stops another sound. A {@link VariantSet} plays one
 * variant chosen by the set with {@link #play(VariantSet, int)}, or every layer of a composed set at once with
 * {@link #play(VariantSet, int, int[])}, which writes the voices to an array of the caller.
 * </p>
 *
 * <p>
 * Long sounds: {@link #play(LongSound)} plays a {@link LongSound} on a voice of the backend outside the short-sound
 * limit and its policy, and {@link #stop(int)} stops it as any voice; a {@link Playlist} plays its tracks through it.
 * A long sound plays on one voice at a time.
 * </p>
 *
 * <p>
 * Voices: at most {@link #getMaxVoices()} short sounds play at once, {@link #DEFAULT_MAX_VOICES} by default, within
 * the voices of the backend. A sound has a priority from {@link #MIN_PRIORITY} to {@link #MAX_PRIORITY},
 * {@link #DEFAULT_PRIORITY} by default. When every voice is busy, the {@link VoicePolicy} decides: by default the
 * oldest voice of the lowest priority is stolen, if that priority is not above the new sound's; drop-new and refuse
 * are selectable. A voice is free again as soon as it is stopped, and when the end its backend posted to the inbox is
 * delivered.
 * </p>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018), not thread-safe; the backend calls the listener at the inbox delivery, on
 * the runtime thread. Playing a sound or a variant set, stopping and the end of a voice allocate nothing, except the
 * first play of a lazy short sound, which decodes it (ADR-0019), and the play of a long sound that is not prepared.
 * </p>
 *
 * @since 11.0.0
 */
public final class Audio
{
    /** Default number of simultaneous short voices (ADR-0027, ADR-0033). */
    public static final int DEFAULT_MAX_VOICES = 16;
    /** Lowest priority. */
    public static final int MIN_PRIORITY = 0;
    /** Highest priority. */
    public static final int MAX_PRIORITY = 100;
    /** Default priority. */
    public static final int DEFAULT_PRIORITY = 50;

    /** No slot found. */
    private static final int NONE = -1;
    /** End of the messages of busy voices. */
    private static final String ERROR_PLAYING = " voices playing";
    /** Name of the variant set argument. */
    private static final String NAME_SET = "Set";
    /** Initial capacity of the long voices, grown when more play at once. */
    private static final int INITIAL_LONG_VOICES = 2;

    /** The backend. */
    private final AudioBackend backend;
    /** The overflow policy. */
    private final VoicePolicy policy;
    /** The voice limit. */
    private final int maxVoices;
    /** Voices playing, in start order, the oldest first. */
    private final int[] voices;
    /** Priority of each voice playing. */
    private final int[] priorities;
    /** Number of voices playing. */
    private int count;
    /** Voices playing long sounds, in start order. */
    private int[] longVoices = new int[INITIAL_LONG_VOICES];
    /** The long sound of each long voice. */
    private LongSound[] longSounds = new LongSound[INITIAL_LONG_VOICES];
    /** Number of long voices playing. */
    private int longCount;

    /**
     * Creates the audio system with {@link #DEFAULT_MAX_VOICES} voices, or fewer if the backend has fewer, and the
     * {@link VoicePolicy#STEAL} policy.
     *
     * @param backend The audio backend, of which this system becomes the voice listener (never {@code null}).
     * @throws IllegalArgumentException If {@code backend} is {@code null}.
     */
    public Audio(AudioBackend backend)
    {
        this(backend, Math.min(DEFAULT_MAX_VOICES, maxVoices(backend)), VoicePolicy.STEAL);
    }

    /**
     * Creates the audio system.
     *
     * @param backend The audio backend, of which this system becomes the voice listener (never {@code null}).
     * @param maxVoices The number of short sounds playing at once (from {@code 1} to the voices of the backend).
     * @param policy The overflow policy (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}, or the limit is outside its range.
     */
    public Audio(AudioBackend backend, int maxVoices, VoicePolicy policy)
    {
        super();

        final int backendVoices = maxVoices(backend);
        ShortSoundCache.checkNotNull(policy, "Policy");
        if (maxVoices < 1 || maxVoices > backendVoices)
        {
            throw new IllegalArgumentException("Voice limit outside 1 to " + backendVoices + ": " + maxVoices);
        }
        this.backend = backend;
        this.maxVoices = maxVoices;
        this.policy = policy;
        voices = new int[maxVoices];
        priorities = new int[maxVoices];
        backend.setVoiceListener(this::ended);
    }

    /**
     * Returns the voices of a backend.
     *
     * @param backend The backend.
     * @return Its voice limit.
     * @throws IllegalArgumentException If {@code backend} is {@code null}.
     */
    private static int maxVoices(AudioBackend backend)
    {
        ShortSoundCache.checkNotNull(backend, "Backend");
        return backend.getMaxVoices();
    }

    /**
     * Plays a short sound at {@link #DEFAULT_PRIORITY}.
     *
     * @param sound The sound, not released (never {@code null}).
     * @return The voice playing it, positive; {@link AudioBackend#NO_VOICE} if it was not played (every voice busy
     *         under the policy, or no voice free in the backend).
     * @throws IllegalArgumentException If {@code sound} is {@code null}.
     * @throws IllegalStateException If the sound is released, if every voice is busy under {@link VoicePolicy#REFUSE},
     *             or for the errors of a lazy sound decoded now.
     * @see #play(ShortSound, int)
     */
    public int play(ShortSound sound)
    {
        return play(sound, DEFAULT_PRIORITY);
    }

    /**
     * Plays a short sound at once, from its start, on a new voice; a lazy sound is decoded first (ADR-0027).
     *
     * @param sound The sound, not released (never {@code null}).
     * @param priority The priority of the voice, from {@link #MIN_PRIORITY} to {@link #MAX_PRIORITY}.
     * @return The voice playing it, positive; {@link AudioBackend#NO_VOICE} if it was not played (every voice busy
     *         under the policy, or no voice free in the backend).
     * @throws IllegalArgumentException If {@code sound} is {@code null}, or the priority is outside its range.
     * @throws IllegalStateException If the sound is released, if every voice is busy under {@link VoicePolicy#REFUSE},
     *             if a lazy sound would exceed the limit of its cache, or if called on another thread than the owner
     *             of the cache of a lazy sound.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the resource of a lazy sound is missing or
     *             unreadable.
     * @throws AudioDecodingException If the content of a lazy sound is invalid or too long.
     */
    public int play(ShortSound sound, int priority)
    {
        ShortSoundCache.checkNotNull(sound, "Sound");
        checkPriority(priority);
        return start(sound.getBuffer(), priority, count);
    }

    /**
     * Plays a random variant set at {@link #DEFAULT_PRIORITY}.
     *
     * @param set The random set, of which no variant is released (never {@code null}).
     * @return The voice playing the chosen variant, positive; {@link AudioBackend#NO_VOICE} if it was not played.
     * @throws IllegalArgumentException If {@code set} is {@code null} or composed.
     * @throws IllegalStateException As {@link #play(VariantSet, int)}.
     * @see #play(VariantSet, int)
     */
    public int play(VariantSet set)
    {
        return play(set, DEFAULT_PRIORITY);
    }

    /**
     * Plays a random variant set: the set chooses a variant (ADR-0027, ADR-0067), which is played as
     * {@link #play(ShortSound, int)} plays a sound. The choice counts as the last play of the set even when the
     * variant is not played.
     *
     * @param set The random set, of which no variant is released (never {@code null}).
     * @param priority The priority of the voice, from {@link #MIN_PRIORITY} to {@link #MAX_PRIORITY}.
     * @return The voice playing the chosen variant, positive; {@link AudioBackend#NO_VOICE} if it was not played
     *         (every voice busy under the policy, or no voice free in the backend).
     * @throws IllegalArgumentException If {@code set} is {@code null} or composed, or the priority is outside its
     *             range.
     * @throws IllegalStateException If the chosen variant is released, for the errors of a lazy variant decoded now,
     *             or if every voice is busy under {@link VoicePolicy#REFUSE}.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the resource of a lazy variant is missing or
     *             unreadable.
     * @throws AudioDecodingException If the content of a lazy variant is invalid or too long.
     * @see #play(VariantSet, int, int[])
     */
    public int play(VariantSet set, int priority)
    {
        ShortSoundCache.checkNotNull(set, NAME_SET);
        if (set.isComposed())
        {
            throw new IllegalArgumentException("Composed variant set: play it with a voices array");
        }
        checkPriority(priority);
        return start(set.getSound(set.select()).getBuffer(), priority, count);
    }

    /**
     * Plays a variant set of either kind and writes its voices to an array, allocating nothing.
     *
     * <ul>
     * <li>A random set plays one chosen variant as {@link #play(VariantSet, int)} does; its voice is written to
     * {@code voices[0]}.</li>
     * <li>A composed set starts every layer in this call, in its order, so that they start together; the voice of
     * layer {@code i} is written to {@code voices[i]}. Every layer is decoded and checked before any starts. Under
     * {@link VoicePolicy#REFUSE}, the set is refused before any layer starts unless a voice is free for each layer;
     * under the other policies, each layer takes a voice as a single play would, but never a voice of another layer of
     * the same play, and a layer that finds no voice gets {@link AudioBackend#NO_VOICE}.</li>
     * </ul>
     *
     * <p>
     * Each started voice is stopped with {@link #stop(int)} and ends by itself as any voice.
     * </p>
     *
     * @param set The set, of which no sound is released (never {@code null}).
     * @param priority The priority of the voices, from {@link #MIN_PRIORITY} to {@link #MAX_PRIORITY}.
     * @param voices The array receiving the voices, at least {@link VariantSet#getVoicesPerPlay()} long; its first
     *            {@link VariantSet#getVoicesPerPlay()} entries are written, positive or {@link AudioBackend#NO_VOICE},
     *            the others are left as they are (never {@code null}).
     * @return The number of voices started, from {@code 0} to {@link VariantSet#getVoicesPerPlay()}.
     * @throws IllegalArgumentException If an argument is {@code null}, the priority is outside its range, or the array
     *             is too short; nothing starts then.
     * @throws IllegalStateException If a sound to play is released, if a lazy sound would exceed the limit of its
     *             cache, if called on another thread than the owner of the cache of a lazy sound, or if the voices are
     *             too few under {@link VoicePolicy#REFUSE}; no layer of a composed set starts then.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the resource of a lazy sound is missing or
     *             unreadable.
     * @throws AudioDecodingException If the content of a lazy sound is invalid or too long.
     */
    public int play(VariantSet set, int priority, int[] voices)
    {
        ShortSoundCache.checkNotNull(set, NAME_SET);
        ShortSoundCache.checkNotNull(voices, "Voices");
        checkPriority(priority);
        final int needed = set.getVoicesPerPlay();
        if (voices.length < needed)
        {
            throw new IllegalArgumentException("Voices array of " + voices.length + " entries, " + needed + " needed");
        }
        final int started;
        if (set.isComposed())
        {
            started = playLayers(set, priority, voices);
        }
        else
        {
            voices[0] = play(set, priority);
            started = voices[0] == AudioBackend.NO_VOICE ? 0 : 1;
        }
        return started;
    }

    /**
     * Plays a long sound at once from its start, on a voice of the backend outside the short-sound limit; a sound that
     * is not prepared is prepared first, which reads its resource now (ADR-0068).
     *
     * <p>
     * The voice ends by itself at the end of the sound, delivered through the inbox, or is stopped with
     * {@link #stop(int)}. A streamed sound is no longer prepared once its voice ends or is stopped.
     * </p>
     *
     * @param sound The sound, not released and not playing (never {@code null}).
     * @return The voice playing it, positive; {@link AudioBackend#NO_VOICE} if no voice of the backend is free, and the
     *         sound stays prepared.
     * @throws IllegalArgumentException If {@code sound} is {@code null}.
     * @throws IllegalStateException If the sound is released or already playing, or if called on another thread than
     *             the owner of its loader.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the resource of a sound not prepared is missing or
     *             unreadable.
     * @throws AudioDecodingException If the content of a sound not prepared is invalid, or a fully decoded one is above
     *             the frame limit of its decoder.
     */
    public int play(LongSound sound)
    {
        ShortSoundCache.checkNotNull(sound, "Long sound");
        return play(sound, null);
    }

    /**
     * Stops a voice at once, of a short sound or of a long sound; no effect on a voice that ended, was stopped or does
     * not exist.
     *
     * @param voice The voice returned by a play.
     */
    public void stop(int voice)
    {
        final int slot = slot(voice);
        if (slot != NONE)
        {
            backend.stop(voice);
            remove(slot);
        }
        else
        {
            final int longSlot = longSlot(voice);
            if (longSlot != NONE)
            {
                backend.stop(voice);
                removeLong(longSlot).stopped();
            }
        }
    }

    /**
     * Stops every short voice, for example when a scene exits; long sounds and playlists are stopped by their own
     * voice or {@link Playlist#stop()}.
     */
    public void stopAll()
    {
        while (count > 0)
        {
            backend.stop(voices[count - 1]);
            count--;
        }
    }

    /**
     * Tells whether a voice is playing, of a short sound or of a long sound.
     *
     * @param voice The voice.
     * @return {@code true} from its play until it is stopped or its end is delivered.
     */
    public boolean isPlaying(int voice)
    {
        return slot(voice) != NONE || longSlot(voice) != NONE;
    }

    /**
     * Returns the number of short voices playing.
     *
     * @return The short voices neither stopped nor ended, at most {@link #getMaxVoices()}.
     */
    public int getPlayingCount()
    {
        return count;
    }

    /**
     * Returns the number of long voices playing.
     *
     * @return The long sounds playing, outside the short-sound limit.
     */
    public int getLongPlayingCount()
    {
        return longCount;
    }

    /**
     * Returns the voice limit.
     *
     * @return The number of short sounds playing at once.
     */
    public int getMaxVoices()
    {
        return maxVoices;
    }

    /**
     * Returns the overflow policy.
     *
     * @return The policy applied when every voice is busy.
     */
    public VoicePolicy getPolicy()
    {
        return policy;
    }

    /**
     * Plays a long sound for a playlist, which listens to its end.
     *
     * @param sound The sound, not {@code null}.
     * @param listener The listener of its end, {@code null} for none.
     * @return The voice, or {@link AudioBackend#NO_VOICE}.
     * @throws IllegalStateException As {@link #play(LongSound)}.
     * @throws com.b3dgs.lionengine.resources.ResourceException As {@link #play(LongSound)}.
     * @throws AudioDecodingException As {@link #play(LongSound)}.
     */
    int play(LongSound sound, TrackListener listener)
    {
        if (sound.isPlaying())
        {
            throw new IllegalStateException("Long sound already playing: " + sound.getKey());
        }
        final int voice = sound.start(backend);
        if (voice != AudioBackend.NO_VOICE)
        {
            if (longCount == longVoices.length)
            {
                longVoices = Arrays.copyOf(longVoices, longCount * 2);
                longSounds = Arrays.copyOf(longSounds, longCount * 2);
            }
            longVoices[longCount] = voice;
            longSounds[longCount] = sound;
            longCount++;
            sound.started(this, voice, listener);
        }
        return voice;
    }

    /**
     * Checks a priority.
     *
     * @param priority The priority.
     * @throws IllegalArgumentException If it is outside {@link #MIN_PRIORITY} to {@link #MAX_PRIORITY}.
     */
    private static void checkPriority(int priority)
    {
        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY)
        {
            throw new IllegalArgumentException("Priority outside 0 to 100: " + priority);
        }
    }

    /**
     * Starts every layer of a composed set, after decoding and checking them all.
     *
     * @param set The composed set.
     * @param priority The priority of the voices, checked.
     * @param voices The array receiving the voices, long enough.
     * @return The number of voices started.
     * @throws IllegalStateException If a layer is released or cannot be decoded now, or if the voices are too few
     *             under {@link VoicePolicy#REFUSE}.
     */
    private int playLayers(VariantSet set, int priority, int[] voices)
    {
        final int layers = set.size();
        for (int i = 0; i < layers; i++)
        {
            set.getSound(i).getBuffer();
        }
        if (policy == VoicePolicy.REFUSE && maxVoices - count < layers)
        {
            throw new IllegalStateException("Short voices too few for a composed set of "
                                            + layers
                                            + " layers: "
                                            + count
                                            + " of "
                                            + maxVoices
                                            + ERROR_PLAYING);
        }
        int started = 0;
        for (int i = 0; i < layers; i++)
        {
            // The voices of this play are the last slots: only the slots before them may be stolen.
            voices[i] = start(set.getSound(i).getBuffer(), priority, count - started);
            if (voices[i] != AudioBackend.NO_VOICE)
            {
                started++;
            }
        }
        return started;
    }

    /**
     * Starts samples on a new voice, freeing one first if every voice is busy.
     *
     * @param buffer The samples.
     * @param priority The priority of the voice, checked.
     * @param candidates The number of the oldest slots that may be stolen.
     * @return The voice, or {@link AudioBackend#NO_VOICE}.
     * @throws IllegalStateException If every voice is busy under {@link VoicePolicy#REFUSE}.
     */
    private int start(PcmBuffer buffer, int priority, int candidates)
    {
        int voice = AudioBackend.NO_VOICE;
        if (count < maxVoices || freeVoice(priority, candidates))
        {
            voice = backend.play(buffer);
            if (voice != AudioBackend.NO_VOICE)
            {
                voices[count] = voice;
                priorities[count] = priority;
                count++;
            }
        }
        return voice;
    }

    /**
     * Frees a voice for a new sound when every voice is busy, according to the policy.
     *
     * @param priority The priority of the new sound.
     * @param candidates The number of the oldest slots that may be stolen.
     * @return {@code true} if a voice was stolen, {@code false} if the new sound is dropped.
     * @throws IllegalStateException Under {@link VoicePolicy#REFUSE}.
     */
    private boolean freeVoice(int priority, int candidates)
    {
        if (policy == VoicePolicy.REFUSE)
        {
            throw new IllegalStateException("Every short voice is busy: " + maxVoices + ERROR_PLAYING);
        }
        return policy == VoicePolicy.STEAL && candidates > 0 && steal(priority, candidates);
    }

    /**
     * Stops the oldest voice of the lowest priority among the oldest slots, if that priority is not above a new
     * sound's.
     *
     * @param priority The priority of the new sound.
     * @param candidates The number of the oldest slots that may be stolen, positive.
     * @return {@code true} if a voice was stopped.
     */
    private boolean steal(int priority, int candidates)
    {
        int victim = 0;
        for (int i = 1; i < candidates; i++)
        {
            if (priorities[i] < priorities[victim])
            {
                victim = i;
            }
        }
        final boolean stolen = priorities[victim] <= priority;
        if (stolen)
        {
            backend.stop(voices[victim]);
            remove(victim);
        }
        return stolen;
    }

    /**
     * Frees the slot of a voice that ended by itself, at the inbox delivery of its end.
     *
     * @param voice The voice.
     */
    private void ended(int voice)
    {
        final int slot = slot(voice);
        if (slot != NONE)
        {
            remove(slot);
        }
        else
        {
            final int longSlot = longSlot(voice);
            if (longSlot != NONE)
            {
                removeLong(longSlot).ended();
            }
        }
    }

    /**
     * Finds the slot of a long voice.
     *
     * @param voice The voice.
     * @return The index of its slot, {@link #NONE} if it is not playing.
     */
    private int longSlot(int voice)
    {
        int slot = NONE;
        for (int i = 0; i < longCount && slot == NONE; i++)
        {
            if (longVoices[i] == voice)
            {
                slot = i;
            }
        }
        return slot;
    }

    /**
     * Removes a long slot, keeping the start order of the others.
     *
     * @param slot The index of the slot.
     * @return The long sound of the slot.
     */
    private LongSound removeLong(int slot)
    {
        final LongSound sound = longSounds[slot];
        final int moved = longCount - slot - 1;
        System.arraycopy(longVoices, slot + 1, longVoices, slot, moved);
        System.arraycopy(longSounds, slot + 1, longSounds, slot, moved);
        longCount--;
        longSounds[longCount] = null;
        return sound;
    }

    /**
     * Finds the slot of a voice.
     *
     * @param voice The voice.
     * @return The index of its slot, {@link #NONE} if it is not playing.
     */
    private int slot(int voice)
    {
        int slot = NONE;
        for (int i = 0; i < count && slot == NONE; i++)
        {
            if (voices[i] == voice)
            {
                slot = i;
            }
        }
        return slot;
    }

    /**
     * Removes a slot, keeping the start order of the others.
     *
     * @param slot The index of the slot.
     */
    private void remove(int slot)
    {
        final int moved = count - slot - 1;
        System.arraycopy(voices, slot + 1, voices, slot, moved);
        System.arraycopy(priorities, slot + 1, priorities, slot, moved);
        count--;
    }
}
