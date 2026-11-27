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
import com.b3dgs.lionengine.audio.codec.ogg.OggVorbisDecoder;
import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Plays OGG Vorbis files on desktop: the music of a level streamed and looped, and a coin effect as a short sound
 * (REQ-AUDIO-002, REQ-AUDIO-003, REQ-AUDIO-006, REQ-AUDIO-011, ADR-0073).
 *
 * <p>
 * OGG Vorbis is decoded on desktop by {@link OggVorbisDecoder} of {@code audio-codec-ogg}, which the game registers
 * once for the {@code ogg} extension on its short-sound cache and on its long-sound loader; decoders are never
 * discovered. The same decoder serves both: it decodes an effect entirely when it is loaded, within its frame limit
 * and the 10 s of a short sound, and streams a music chunk by chunk, with no length limit. The music loops without gap:
 * its buffer opens the file again when it ends. Nothing else changes: the audio system, the backend and the handles
 * are those of the WAV examples. On Android the platform decodes OGG Vorbis, so this module is desktop only.
 * </p>
 *
 * @since 11.0.0
 */
public final class OggVorbisExample
{
    /** Extension of the OGG Vorbis files. */
    public static final String OGG = "ogg";
    /** Key of the music of the level, in the game data. */
    public static final ResourceKey MUSIC = new ResourceKey("music/level.ogg");
    /** Key of the coin effect, in the game data. */
    public static final ResourceKey COIN = new ResourceKey("sfx/coin.ogg");

    /**
     * Private constructor.
     */
    private OggVorbisExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Registers the OGG Vorbis decoder for both audio families, as the game does once at its start.
     *
     * @param cache The short-sound cache.
     * @param loader The long-sound loader.
     */
    public static void register(ShortSoundCache cache, LongSoundLoader loader)
    {
        final OggVorbisDecoder ogg = new OggVorbisDecoder();
        cache.setDecoder(OGG, ogg);
        loader.setDecoder(OGG, ogg);
    }

    /**
     * Enters the level: preloads the coin effect, then streams the music, looping, and plays it at once.
     *
     * @param cache The short-sound cache, with the decoder registered, over the game data holding {@link #COIN}.
     * @param loader The long-sound loader, with the decoder registered, over the game data holding {@link #MUSIC}.
     * @param audio The audio system.
     * @return The level sounds; the music is playing.
     */
    public static Level enter(ShortSoundCache cache, LongSoundLoader loader, Audio audio)
    {
        final ShortSound coin = cache.load(COIN);
        final LongSound music = loader.stream(MUSIC);
        music.setLooping(true);
        music.prepare();
        final int voice = audio.play(music);
        return new Level(music, voice, coin);
    }

    /**
     * Plays the coin effect, as when the player collects a coin.
     *
     * @param audio The audio system.
     * @param level The level sounds.
     * @return The voice playing the effect, {@code AudioBackend.NO_VOICE} if it was not played.
     */
    public static int collect(Audio audio, Level level)
    {
        return audio.play(level.coin());
    }

    /**
     * Exits the level: stops the music and releases both sounds.
     *
     * @param audio The audio system.
     * @param level The level sounds.
     */
    public static void exit(Audio audio, Level level)
    {
        audio.stop(level.musicVoice());
        level.music().release();
        level.coin().release();
    }

    /**
     * The sounds of the level.
     *
     * @param music The music, streamed and looping.
     * @param musicVoice The voice playing the music.
     * @param coin The coin effect.
     * @since 11.0.0
     */
    public record Level(LongSound music, int musicVoice, ShortSound coin)
    {
        // Value type of the example.
    }
}
