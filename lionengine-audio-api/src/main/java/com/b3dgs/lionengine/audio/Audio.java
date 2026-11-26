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

/**
 * Audio system of the game: plays short sounds on a bounded number of voices of an {@link AudioBackend}, with
 * priorities and an overflow policy (ADR-0004, ADR-0018, ADR-0027, ADR-0033, ADR-0065, ADR-0066, REQ-AUDIO-001,
 * REQ-AUDIO-003, REQ-AUDIO-004, REQ-AUDIO-010).
 *
 * <p>
 * The game creates one {@code Audio} over its backend and keeps it for its whole life: it is the only user of the
 * backend and its voice listener. {@link #play(ShortSound, int)} starts a short sound at once on a new voice, an
 * {@code int} returned to the caller, which can {@link #stop(int)} it; stopping a voice that already ended has no
 * effect. Voices are never reused, so a stale voice never stops another sound.
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
 * the runtime thread. Playing, stopping and the end of a voice allocate nothing, except the first play of a lazy
 * short sound, which decodes it (ADR-0019).
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
        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY)
        {
            throw new IllegalArgumentException("Priority outside 0 to 100: " + priority);
        }
        final PcmBuffer buffer = sound.getBuffer();
        int voice = AudioBackend.NO_VOICE;
        if (count < maxVoices || freeVoice(priority))
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
     * Stops a voice at once; no effect on a voice that ended, was stopped or does not exist.
     *
     * @param voice The voice returned by {@link #play(ShortSound, int)}.
     */
    public void stop(int voice)
    {
        final int slot = slot(voice);
        if (slot != NONE)
        {
            backend.stop(voice);
            remove(slot);
        }
    }

    /**
     * Stops every voice, for example when a scene exits.
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
     * Tells whether a voice is playing.
     *
     * @param voice The voice.
     * @return {@code true} from its play until it is stopped or its end is delivered.
     */
    public boolean isPlaying(int voice)
    {
        return slot(voice) != NONE;
    }

    /**
     * Returns the number of voices playing.
     *
     * @return The voices neither stopped nor ended, at most {@link #getMaxVoices()}.
     */
    public int getPlayingCount()
    {
        return count;
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
     * Frees a voice for a new sound when every voice is busy, according to the policy.
     *
     * @param priority The priority of the new sound.
     * @return {@code true} if a voice was stolen, {@code false} if the new sound is dropped.
     * @throws IllegalStateException Under {@link VoicePolicy#REFUSE}.
     */
    private boolean freeVoice(int priority)
    {
        if (policy == VoicePolicy.REFUSE)
        {
            throw new IllegalStateException("Every short voice is busy: " + maxVoices + " voices playing");
        }
        return policy == VoicePolicy.STEAL && steal(priority);
    }

    /**
     * Stops the oldest voice of the lowest priority, if that priority is not above a new sound's.
     *
     * @param priority The priority of the new sound.
     * @return {@code true} if a voice was stopped.
     */
    private boolean steal(int priority)
    {
        int victim = 0;
        for (int i = 1; i < count; i++)
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
