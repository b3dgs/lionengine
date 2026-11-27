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
import com.b3dgs.lionengine.audio.LongSound;
import com.b3dgs.lionengine.audio.LongSoundLoader;
import com.b3dgs.lionengine.audio.ShortSound;
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Sets the master and per-sound volume, loops an ambient sound, and pauses every sound while the application is in the
 * background (REQ-AUDIO-011, ADR-0031, ADR-0069).
 *
 * <p>
 * A gain is a {@code double} from {@link Audio#MIN_GAIN} to {@link Audio#MAX_GAIN}; the gain of a voice is the master
 * gain of the {@link Audio} times the gain of its sound, and a change of either reaches the voices playing at once.
 * The scene of a forest sets the master volume, lowers its footsteps, and plays the wind as a long sound that loops:
 * it is streamed, opens its file again at each pass without gap, and never ends by itself, so the scene stops it on
 * exit. When the application goes to the background, the master pause holds every voice, the wind included, and the
 * foreground resumes them where they were. A single voice is paused with {@link Audio#pause(int)}.
 * </p>
 *
 * @since 11.0.0
 */
public final class AudioControlsExample
{
    /** Key of the footstep sound, in the game data. */
    public static final ResourceKey STEP = new ResourceKey("sounds/step.wav");
    /** Key of the wind, in the game data. */
    public static final ResourceKey WIND = new ResourceKey("ambient/wind.wav");
    /** Master gain of the game, from its settings. */
    public static final double MASTER_GAIN = 0.8;
    /** Gain of the footsteps: half as loud as the other sounds. */
    public static final double STEP_GAIN = 0.5;
    /** Gain of the wind, in the background of the scene. */
    public static final double WIND_GAIN = 0.25;

    /**
     * Private constructor.
     */
    private AudioControlsExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Enters the forest: sets the master gain, preloads the footsteps at their gain, and starts the wind looping.
     *
     * @param cache The short-sound cache over the game data holding {@link #STEP}.
     * @param loader The long-sound loader over the game data holding {@link #WIND}.
     * @param audio The audio system.
     * @return The sounds of the scene, the wind playing.
     */
    public static Forest enter(ShortSoundCache cache, LongSoundLoader loader, Audio audio)
    {
        audio.setMasterGain(MASTER_GAIN);
        final ShortSound step = cache.load(STEP);
        step.setGain(STEP_GAIN);
        final LongSound wind = loader.stream(WIND);
        wind.setGain(WIND_GAIN);
        wind.setLooping(true);
        wind.prepare();
        return new Forest(step, wind, audio.play(wind));
    }

    /**
     * Plays a footstep at its gain.
     *
     * @param audio The audio system.
     * @param forest The sounds of the scene.
     * @return The voice of the footstep.
     */
    public static int step(Audio audio, Forest forest)
    {
        return audio.play(forest.step());
    }

    /**
     * Pauses every sound, as the platform's background event does (ADR-0031).
     *
     * @param audio The audio system.
     */
    public static void background(Audio audio)
    {
        audio.setMasterPaused(true);
    }

    /**
     * Resumes every sound where it was, as the platform's foreground event does.
     *
     * @param audio The audio system.
     */
    public static void foreground(Audio audio)
    {
        audio.setMasterPaused(false);
    }

    /**
     * Exits the forest: stops the wind, which never ends by itself, and releases the sounds.
     *
     * @param audio The audio system.
     * @param forest The sounds of the scene.
     */
    public static void exit(Audio audio, Forest forest)
    {
        audio.stop(forest.windVoice());
        forest.wind().release();
        forest.step().release();
    }

    /**
     * The sounds of the forest.
     *
     * @param step The footsteps.
     * @param wind The wind, looping.
     * @param windVoice The voice of the wind.
     * @since 11.0.0
     */
    public record Forest(ShortSound step, LongSound wind, int windVoice)
    {
        // Value type of the example.
    }
}
