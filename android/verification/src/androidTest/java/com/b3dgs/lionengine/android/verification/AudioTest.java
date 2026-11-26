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
package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.AudioDecoder;
import com.b3dgs.lionengine.audio.AudioDecodingException;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.ShortSound;
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.audio.VariantSet;
import com.b3dgs.lionengine.audio.VoiceListener;
import com.b3dgs.lionengine.audio.VoicePolicy;
import com.b3dgs.lionengine.audio.WavDecoder;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.StreamProvider;

/**
 * Runs {@code audio-api} on Android API 21 (ADR-0044, ADR-0065, ADR-0066, ADR-0067): the PCM format and buffer, a
 * decoder throwing the decoding exception, the WAV decoder, the short-sound cache, the voices of the audio system and
 * the variant sets. Expected values are the ones of the JVM tests (PcmBufferTest, WavDecoderTest, AudioTest,
 * VariantSetTest).
 */
@RunWith(AndroidJUnit4.class)
public final class AudioTest
{
    /** Stereo format at 10 Hz, for round durations. */
    private static final PcmFormat STEREO = new PcmFormat(10, PcmFormat.STEREO);

    /**
     * Creates the test.
     */
    public AudioTest()
    {
        super();
    }

    /**
     * Frames, duration and the copy of a range of frames; invalid formats and ranges are refused.
     */
    @Test
    public void testPcm()
    {
        final PcmBuffer buffer = new PcmBuffer(STEREO, new short[]
        {
            1, 2, 3, 4, 5, 6, 7, 8
        });
        final short[] target = new short[6];

        buffer.readFrames(1, target, 2, 2);

        assertEquals(4, buffer.getFrames());
        assertEquals(0.4, buffer.getDuration(), 1e-12);
        assertArrayEquals(new short[]
        {
            0, 0, 3, 4, 5, 6
        }, target);
        assertThrows(IllegalArgumentException.class, () -> new PcmFormat(44_100, 3));
        assertThrows(IllegalArgumentException.class, () -> buffer.readFrames(1, target, 0, 4));
    }

    /**
     * A decoder is a functional interface; its decoding exception keeps its message.
     */
    @Test
    public void testDecoder()
    {
        final AudioDecoder decoder = input ->
        {
            throw new AudioDecodingException("Not a sound");
        };
        final ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);

        final AudioDecodingException exception = assertThrows(AudioDecodingException.class,
                                                              () -> decoder.decode(input));

        assertEquals("Not a sound", exception.getMessage());
    }

    /**
     * Writes a mono 16-bit WAV file of 8 kHz.
     *
     * @param samples The samples.
     * @return The bytes of the file.
     */
    private static byte[] wav(short[] samples)
    {
        final int data = samples.length * 2;
        final byte[] bytes = new byte[44 + data];
        final int[] header =
        {
            0x46464952, 36 + data, 0x45564157, 0x20746D66, 16, 0x0001_0001, 8_000, 16_000, 0x0010_0002, 0x61746164, data
        };
        for (int i = 0; i < header.length; i++)
        {
            for (int b = 0; b < 4; b++)
            {
                bytes[i * 4 + b] = (byte) (header[i] >> b * 8);
            }
        }
        for (int i = 0; i < samples.length; i++)
        {
            bytes[44 + i * 2] = (byte) samples[i];
            bytes[45 + i * 2] = (byte) (samples[i] >> 8);
        }
        return bytes;
    }

    /**
     * The WAV decoder decodes 16-bit PCM and refuses a sound above its frame limit.
     *
     * @throws IOException If reading fails.
     */
    @Test
    public void testWav() throws IOException
    {
        final byte[] bytes = wav(new short[]
        {
            0, Short.MAX_VALUE, Short.MIN_VALUE, -2
        });
        final short[] samples = new short[4];

        final PcmBuffer buffer = new WavDecoder().decode(new ByteArrayInputStream(bytes));
        buffer.readFrames(0, samples, 0, 4);

        assertEquals(new PcmFormat(8_000, PcmFormat.MONO), buffer.getFormat());
        assertArrayEquals(new short[]
        {
            0, Short.MAX_VALUE, Short.MIN_VALUE, -2
        }, samples);
        final WavDecoder small = new WavDecoder(3);
        final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        assertThrows(AudioDecodingException.class, () -> small.decode(input));
    }

    /**
     * A short sound is cached once and played on voices; beyond the limit the oldest voice of the lowest priority is
     * stolen.
     */
    @Test
    public void testVoices()
    {
        final ResourceKey key = new ResourceKey("sounds/shot.wav");
        final byte[] bytes = wav(new short[80]);
        final StreamProvider provider = new StreamProvider();
        provider.register(key, () -> new ByteArrayInputStream(bytes));
        final ShortSoundCache cache = new ShortSoundCache(new Resources(provider));
        final CountingBackend backend = new CountingBackend();
        final Audio audio = new Audio(backend, 2, VoicePolicy.STEAL);

        final ShortSound shot = cache.load(key);
        final ShortSound again = cache.load(key);
        final int first = audio.play(shot, 10);
        final int second = audio.play(again, 50);
        final int third = audio.play(shot, 50);

        assertEquals(1, cache.size());
        assertEquals(0.01, shot.getDuration(), 1e-12);
        assertFalse(audio.isPlaying(first));
        assertTrue(audio.isPlaying(second));
        assertTrue(audio.isPlaying(third));
        assertEquals(1, backend.stopped);
        assertEquals(AudioBackend.NO_VOICE, audio.play(shot, 0));
        shot.release();
        again.release();
        assertEquals(0, cache.size());
    }

    /**
     * A random set chooses the same variants as on the JVM for the same seed, never twice the same in a row; a composed
     * set starts every layer in one play.
     */
    @Test
    public void testVariantSets()
    {
        final byte[] bytes = wav(new short[80]);
        final StreamProvider provider = new StreamProvider();
        final ShortSoundCache cache = new ShortSoundCache(new Resources(provider));
        final ShortSound[] sounds = new ShortSound[3];
        for (int i = 0; i < sounds.length; i++)
        {
            final ResourceKey key = new ResourceKey("sounds/step" + i + ".wav");
            provider.register(key, () -> new ByteArrayInputStream(bytes));
            sounds[i] = cache.load(key);
        }
        final Audio audio = new Audio(new CountingBackend(), 4, VoicePolicy.STEAL);
        final VariantSet random = VariantSet.random(2026L, sounds);
        final int[] chosen = new int[16];

        for (int i = 0; i < chosen.length; i++)
        {
            audio.play(random);
            chosen[i] = random.getLastIndex();
        }
        audio.stopAll();
        final int[] voices = new int[3];
        final int started = audio.play(VariantSet.composed(sounds), 50, voices);

        assertArrayEquals(new int[]
        {
            1, 0, 2, 1, 2, 1, 2, 1, 2, 0, 2, 0, 2, 1, 0, 2
        }, chosen);
        assertEquals(3, started);
        assertEquals(3, audio.getPlayingCount());
    }

    /**
     * Backend that plays nothing and counts the voices stopped.
     */
    private static final class CountingBackend implements AudioBackend
    {
        /** Voices stopped. */
        private int stopped;
        /** Next voice. */
        private int next = 1;

        /**
         * Creates the backend.
         */
        CountingBackend()
        {
            super();
        }

        @Override
        public int getMaxVoices()
        {
            return 4;
        }

        @Override
        public void setVoiceListener(VoiceListener listener)
        {
            // Voices never end by themselves here.
        }

        @Override
        public int play(PcmBuffer buffer)
        {
            return next++;
        }

        @Override
        public void stop(int voice)
        {
            stopped++;
        }

        @Override
        public boolean isPlaying(int voice)
        {
            return false;
        }

        @Override
        public void close()
        {
            // Holds nothing.
        }
    }
}
