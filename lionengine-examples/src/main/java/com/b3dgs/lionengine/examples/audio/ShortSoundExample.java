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
package com.b3dgs.lionengine.examples.audio;

import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.ShortSound;
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.audio.VoicePolicy;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Preloads short sounds from WAV files, plays them on a limited number of voices with priorities, and releases them
 * (REQ-AUDIO-003, REQ-AUDIO-004, REQ-AUDIO-010, REQ-AUDIO-013, ADR-0066).
 *
 * <p>
 * The game creates one {@link ShortSoundCache} with its {@link Resources} and one {@link Audio} over its backend, and
 * gives both to its scenes. A scene preloads the sounds it needs on entry, so that playing never decodes, and
 * releases them on exit; a sound is decoded once and shared by every load. {@link Audio#play(ShortSound, int)} starts
 * a sound at once on a new voice; with every voice busy, the default policy steals the oldest voice of the lowest
 * priority, never one more important than the new sound. Here two voices play footsteps at a low priority; an
 * explosion at a high priority takes the place of the oldest footstep, then a distant footstep, at a lower priority
 * than every voice playing, finds no voice it may take. A short sound lasts at most 10 s: a longer file is refused
 * and belongs to the long sounds.
 * </p>
 *
 * @since 11.0.0
 */
public final class ShortSoundExample
{
    /** Key of the footstep sound, in the game data. */
    public static final ResourceKey STEP = new ResourceKey("sounds/step.wav");
    /** Key of the explosion sound, in the game data. */
    public static final ResourceKey EXPLOSION = new ResourceKey("sounds/explosion.wav");
    /** Voices of the example: two short sounds at once. */
    public static final int VOICES = 2;
    /** Priority of the footsteps: low. */
    public static final int STEP_PRIORITY = 10;
    /** Priority of the distant footsteps: below the footsteps. */
    public static final int DISTANT_STEP_PRIORITY = 5;
    /** Priority of the explosion: high. */
    public static final int EXPLOSION_PRIORITY = 90;

    /**
     * Private constructor.
     */
    private ShortSoundExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates the audio system of the example: {@link #VOICES} voices, oldest lowest priority stolen first.
     *
     * @param backend The audio backend.
     * @return The audio system, the voice listener of the backend.
     */
    public static Audio createAudio(AudioBackend backend)
    {
        return new Audio(backend, VOICES, VoicePolicy.STEAL);
    }

    /**
     * Preloads the two sounds, plays two footsteps, an explosion and a distant footstep, then releases the sounds.
     *
     * @param cache The short-sound cache over the game data holding {@link #STEP} and {@link #EXPLOSION}.
     * @param audio The audio system.
     * @return The voices played and what the cache held.
     */
    public static Result run(ShortSoundCache cache, Audio audio)
    {
        final ShortSound step = cache.load(STEP);
        final ShortSound explosion = cache.load(EXPLOSION);

        final int firstStep = audio.play(step, STEP_PRIORITY);
        final int secondStep = audio.play(step, STEP_PRIORITY);
        final int blast = audio.play(explosion, EXPLOSION_PRIORITY);
        final int distantStep = audio.play(step, DISTANT_STEP_PRIORITY);
        final boolean firstStepPlaying = audio.isPlaying(firstStep);
        final int cachedInUse = cache.size();

        step.release();
        explosion.release();
        return new Result(secondStep, blast, firstStepPlaying, distantStep, cachedInUse, cache.size());
    }

    /**
     * Result of the example.
     *
     * @param secondStep The voice of the second footstep, still playing.
     * @param blast The voice of the explosion.
     * @param firstStepPlaying {@code false}: the explosion took the voice of the first footstep.
     * @param distantStep The voice of the distant footstep: {@link AudioBackend#NO_VOICE}, no voice it may take.
     * @param cachedInUse The number of cached sounds while in use.
     * @param cachedAtEnd The number of cached sounds once released.
     * @since 11.0.0
     */
    public record Result(int secondStep,
                         int blast,
                         boolean firstStepPlaying,
                         int distantStep,
                         int cachedInUse,
                         int cachedAtEnd)
    {
        // Value type of the example.
    }
}
