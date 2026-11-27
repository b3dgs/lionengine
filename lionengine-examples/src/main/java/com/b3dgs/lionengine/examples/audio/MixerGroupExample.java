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
import com.b3dgs.lionengine.audio.MixerGroup;
import com.b3dgs.lionengine.audio.ShortSound;
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Uses the mixer groups and a fade: a volume slider for the user interface, and a music fade-out on scene exit
 * (REQ-AUDIO-012, ADR-0033, ADR-0071).
 *
 * <p>
 * Every {@link Audio} has the groups {@link MixerGroup#MUSIC}, {@link MixerGroup#EFFECTS} and {@link MixerGroup#UI};
 * a long sound is in the music group and a short sound in the effects group by default, and a handle names another
 * group with {@code setGroup}. The gain of a voice is master × group × sound. The options screen sets the volume of
 * the interface sounds from a slider with {@link MixerGroup#setGain(double)}, which reaches the voices playing at
 * once. On exit, the scene of the town fades its music out over {@link #FADE_OUT_SECONDS} instead of cutting it: the
 * backend ramps the gain per audio buffer, and the end of the fade comes back through the inbox to the
 * {@link Audio#setFadeListener(com.b3dgs.lionengine.audio.FadeListener) fade listener}, on the runtime thread, where
 * the scene stops the music and releases its sounds. A fade to {@code 0} stops nothing by itself.
 * </p>
 *
 * @since 11.0.0
 */
public final class MixerGroupExample
{
    /** Key of the music of the town, in the game data. */
    public static final ResourceKey THEME = new ResourceKey("music/town.wav");
    /** Key of the click of the buttons, in the game data. */
    public static final ResourceKey CLICK = new ResourceKey("sounds/click.wav");
    /** Duration of the music fade-out on exit, in seconds. */
    public static final double FADE_OUT_SECONDS = 1.5;

    /**
     * Private constructor.
     */
    private MixerGroupExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the volume of the interface sounds from the position of a slider.
     *
     * @param audio The audio system.
     * @param slider The position of the slider, from {@link Audio#MIN_GAIN} to {@link Audio#MAX_GAIN}.
     */
    public static void setInterfaceVolume(Audio audio, double slider)
    {
        audio.getGroup(MixerGroup.UI).setGain(slider);
    }

    /**
     * Enters the town: plays the music, streamed and looping, in the music group, and preloads the click in the
     * interface group.
     *
     * @param cache The short-sound cache over the game data holding {@link #CLICK}.
     * @param loader The long-sound loader over the game data holding {@link #THEME}.
     * @param audio The audio system.
     * @return The sounds of the scene, the music playing.
     */
    public static Town enter(ShortSoundCache cache, LongSoundLoader loader, Audio audio)
    {
        final ShortSound click = cache.load(CLICK);
        click.setGroup(MixerGroup.UI);
        final LongSound theme = loader.stream(THEME);
        theme.setLooping(true);
        return new Town(click, theme, audio.play(theme));
    }

    /**
     * Plays the click of a button, at the volume of the interface.
     *
     * @param audio The audio system.
     * @param town The sounds of the scene.
     * @return The voice of the click.
     */
    public static int playClick(Audio audio, Town town)
    {
        return audio.play(town.click());
    }

    /**
     * Starts the exit of the town: fades the music out; once the fade ends, the music is stopped and the sounds are
     * released, at the inbox delivery of its end.
     *
     * @param audio The audio system.
     * @param town The sounds of the scene.
     */
    public static void exit(Audio audio, Town town)
    {
        audio.setFadeListener(voice ->
        {
            if (voice == town.themeVoice())
            {
                audio.stop(voice);
                town.theme().release();
                town.click().release();
                audio.setFadeListener(null);
            }
        });
        town.theme().fade(Audio.MIN_GAIN, FADE_OUT_SECONDS);
    }

    /**
     * The sounds of the town.
     *
     * @param click The click of the buttons, in the interface group.
     * @param theme The music, looping, in the music group.
     * @param themeVoice The voice of the music.
     * @since 11.0.0
     */
    public record Town(ShortSound click, LongSound theme, int themeVoice)
    {
        // Value type of the example.
    }
}
