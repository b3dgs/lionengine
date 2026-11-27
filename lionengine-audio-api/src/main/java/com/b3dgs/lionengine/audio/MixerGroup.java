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
 * Mixer group of an {@link Audio}: a gain shared by every sound of the group, changed at once or faded (ADR-0033,
 * ADR-0071, REQ-AUDIO-012).
 *
 * <p>
 * The groups {@link #MUSIC}, {@link #EFFECTS} and {@link #UI} exist in every {@link Audio}; a game adds its own with
 * {@link Audio#addGroup(String)}, and gets any of them with {@link Audio#getGroup(String)}. Each sound is in exactly
 * one group, named by its id on its handle: {@link #EFFECTS} by default for a {@link ShortSound}, {@link #MUSIC} for a
 * {@link LongSound} ({@link ShortSound#setGroup(String)}, {@link LongSound#setGroup(String)}). The effective gain of a
 * voice is master × group × sound.
 * </p>
 *
 * <p>
 * {@link #setGain(double)} reaches every voice of the group at once, as a step at its next audio buffer;
 * {@link #fade(double, double)} sets the gain of the group to a target and ramps every voice of the group to its new
 * effective gain in the backend, linearly over a duration, each voice reporting its completion to the
 * {@link FadeListener} of the {@link Audio}. A voice keeps the group it started with, as its looping.
 * </p>
 *
 * <p>
 * Threads: runtime thread only, as its {@link Audio}. Changing the gain or fading allocates nothing.
 * </p>
 *
 * @since 11.0.0
 */
public final class MixerGroup
{
    /** Id of the music group, the default group of the long sounds. */
    public static final String MUSIC = "music";
    /** Id of the effects group, the default group of the short sounds. */
    public static final String EFFECTS = "effects";
    /** Id of the user interface group. */
    public static final String UI = "ui";

    /**
     * Checks a group id.
     *
     * @param id The id.
     * @return The id.
     * @throws IllegalArgumentException If it is {@code null} or empty.
     */
    static String checkId(String id)
    {
        if (id == null || id.isEmpty())
        {
            throw new IllegalArgumentException("Mixer group id must not be null or empty");
        }
        return id;
    }

    /** The audio system of the group. */
    private final Audio audio;
    /** The id. */
    private final String id;
    /** The gain. */
    private double gain = Audio.MAX_GAIN;

    /**
     * Creates a group of full gain.
     *
     * @param audio The audio system of the group.
     * @param id The id, checked.
     */
    MixerGroup(Audio audio, String id)
    {
        super();

        this.audio = audio;
        this.id = id;
    }

    /**
     * Returns the id of the group.
     *
     * @return The id, never {@code null} nor empty.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the gain of the group; every voice of the group gets its new effective gain at once, from its next audio
     * buffer; a voice of the group that is fading keeps fading, toward its new effective gain, in its remaining time.
     *
     * @param gain The gain, from {@link Audio#MIN_GAIN} to {@link Audio#MAX_GAIN}.
     * @throws IllegalArgumentException If the gain is NaN or outside its range; nothing changes then.
     */
    public void setGain(double gain)
    {
        Audio.checkGain(gain);
        this.gain = gain;
        audio.updateGain(this);
    }

    /**
     * Fades the group: its gain becomes the target at once, so that {@link #getGain()} returns it and a sound played
     * from now on starts at it, and every voice of the group ramps linearly from the gain it has to its new effective
     * gain over the duration, in the backend. Each voice reports the end of its ramp to the {@link FadeListener} of the
     * {@link Audio} through the inbox; a group with no voice playing reports nothing. A fade to {@code 0} stops no
     * voice.
     *
     * @param gain The target gain, from {@link Audio#MIN_GAIN} to {@link Audio#MAX_GAIN}.
     * @param seconds The duration, in seconds, finite and not negative.
     * @throws IllegalArgumentException If the gain or the duration is invalid; nothing changes then.
     */
    public void fade(double gain, double seconds)
    {
        Audio.checkGain(gain);
        Audio.checkSeconds(seconds);
        this.gain = gain;
        audio.fade(this, seconds);
    }

    /**
     * Returns the gain of the group.
     *
     * @return The gain, {@link Audio#MAX_GAIN} by default; the target of its last fade.
     */
    public double getGain()
    {
        return gain;
    }
}
