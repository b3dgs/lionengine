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
import com.b3dgs.lionengine.audio.FadeListener;
import com.b3dgs.lionengine.audio.LongSound;
import com.b3dgs.lionengine.audio.LongSoundLoader;
import com.b3dgs.lionengine.audio.MixerGroup;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.PcmSource;
import com.b3dgs.lionengine.audio.PcmStream;
import com.b3dgs.lionengine.audio.Playlist;
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
 * Runs {@code audio-api} on Android API 21 (ADR-0044, ADR-0065, ADR-0066, ADR-0067, ADR-0068, ADR-0069): the PCM
 * format and buffer, a decoder throwing the decoding exception, the WAV decoder, the short-sound cache, the voices of
 * the audio system, the variant sets, the streamed long sounds with a playlist, and the controls (gain, looping,
 * pause). Expected values are the ones of the JVM tests (PcmBufferTest, WavDecoderTest, AudioTest, VariantSetTest,
 * StreamBufferTest, LongSoundTest, PlaylistTest, AudioControlsTest, StreamBufferLoopTest).
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
     * The WAV decoder streams a file in pieces; a long sound streams through its bounded buffer, which the backend
     * pulls to its end; the end of its voice starts the next track of a playlist, which stops after its last track.
     *
     * @throws IOException If reading fails.
     */
    @Test
    public void testLongSounds() throws IOException
    {
        final short[] ramp = new short[10_000];
        for (int i = 0; i < ramp.length; i++)
        {
            ramp[i] = (short) i;
        }
        final byte[] bytes = wav(ramp);
        final PcmSource source = new WavDecoder(1).open(new ByteArrayInputStream(bytes));
        final short[] piece = new short[3];
        assertEquals(3, source.read(piece, 0, 3));
        assertArrayEquals(new short[]
        {
            0, 1, 2
        }, piece);

        final StreamProvider provider = new StreamProvider();
        final ResourceKey first = new ResourceKey("music/first.wav");
        final ResourceKey second = new ResourceKey("music/second.wav");
        provider.register(first, () -> new ByteArrayInputStream(bytes));
        provider.register(second, () -> new ByteArrayInputStream(bytes));
        final LongSoundLoader loader = new LongSoundLoader(new Resources(provider));
        final CountingBackend backend = new CountingBackend();
        final Audio audio = new Audio(backend, 2, VoicePolicy.STEAL);
        final LongSound music = loader.stream(first, 2, 1_024);
        final Playlist playlist = new Playlist(audio, music, loader.stream(second));

        playlist.play();
        final int voice = playlist.getVoice();
        final short[] target = new short[4_000];
        int frames = 0;
        int read;
        do
        {
            read = backend.stream.read(target, 0, target.length);
            for (int i = 0; i < read; i++)
            {
                assertEquals((short) (frames + i), target[i]);
            }
            frames += read;
        }
        while (read == target.length);
        backend.listener.onVoiceEnded(voice);

        assertEquals(10_000, frames);
        assertEquals(1, playlist.getTrack());
        assertFalse(music.isPrepared());
        backend.listener.onVoiceEnded(playlist.getVoice());
        assertFalse(playlist.isPlaying());
        assertEquals(LongSoundLoader.DEFAULT_CHUNKS, playlist.getSound(1).getChunks());
    }

    /**
     * The gain of a voice is master × sound and follows their changes; a looping short sound is played looping; pause,
     * resume and the master pause reach the backend; a streamed long sound that loops starts its file again without
     * gap and never ends.
     *
     * @throws IOException If reading fails.
     */
    @Test
    public void testControls() throws IOException
    {
        final short[] ramp = new short[3_000];
        for (int i = 0; i < ramp.length; i++)
        {
            ramp[i] = (short) i;
        }
        final byte[] bytes = wav(ramp);
        final StreamProvider provider = new StreamProvider();
        final ResourceKey key = new ResourceKey("sounds/wind.wav");
        provider.register(key, () -> new ByteArrayInputStream(bytes));
        final CountingBackend backend = new CountingBackend();
        final Audio audio = new Audio(backend, 2, VoicePolicy.STEAL);
        final ShortSound shot = new ShortSoundCache(new Resources(provider)).load(key);

        audio.setMasterGain(0.5);
        shot.setGain(0.5);
        shot.setLooping(true);
        final int voice = audio.play(shot);
        assertEquals(0.25, backend.gain, 1e-12);
        assertTrue(backend.looping);
        shot.setGain(1.0);
        assertEquals(0.5, backend.gain, 1e-12);
        assertThrows(IllegalArgumentException.class, () -> shot.setGain(Double.NaN));
        audio.pause(voice);
        assertTrue(audio.isPaused(voice));
        assertEquals(1, backend.paused);
        audio.resume(voice);
        assertFalse(audio.isPaused(voice));
        audio.setMasterPaused(true);
        assertTrue(backend.outputPaused);
        audio.setMasterPaused(false);
        assertFalse(backend.outputPaused);
        shot.release();
        assertFalse(audio.isPlaying(voice));

        final LongSound wind = new LongSoundLoader(new Resources(provider)).stream(key, 2, 1_024);
        wind.setLooping(true);
        audio.play(wind);
        final short[] target = new short[1_000];
        for (int frames = 0; frames < 10_000; frames += target.length)
        {
            assertEquals(target.length, backend.stream.read(target, 0, target.length));
            for (int i = 0; i < target.length; i++)
            {
                assertEquals((short) ((frames + i) % ramp.length), target[i]);
            }
        }
        wind.release();
    }

    /**
     * The groups exist by default and a game adds its own; the gain of a voice is master × group × sound; a fade of a
     * group gives the backend the new effective gain and its duration, and a completion reaches the listener of the
     * audio system for a voice that plays.
     *
     * @throws IOException If reading fails.
     */
    @Test
    public void testGroupsAndFades() throws IOException
    {
        final byte[] bytes = wav(new short[1_000]);
        final StreamProvider provider = new StreamProvider();
        final ResourceKey key = new ResourceKey("sounds/click.wav");
        provider.register(key, () -> new ByteArrayInputStream(bytes));
        final CountingBackend backend = new CountingBackend();
        final Audio audio = new Audio(backend, 2, VoicePolicy.STEAL);
        final ShortSound click = new ShortSoundCache(new Resources(provider)).load(key);
        final int[] faded = new int[1];
        audio.setFadeListener(voice -> faded[0] = voice);

        assertTrue(audio.hasGroup(MixerGroup.MUSIC));
        assertTrue(audio.hasGroup(MixerGroup.EFFECTS));
        final MixerGroup ui = audio.getGroup(MixerGroup.UI);
        assertEquals("menu", audio.addGroup("menu").getId());
        click.setGroup(MixerGroup.UI);
        audio.setMasterGain(0.5);
        ui.setGain(0.5);
        click.setGain(0.5);
        final int voice = audio.play(click);
        assertEquals(0.125, backend.gain, 1e-12);

        ui.fade(0.0, 0.25);
        assertEquals(0.0, backend.gain, 1e-12);
        assertEquals(0.25, backend.seconds, 1e-12);
        assertThrows(IllegalArgumentException.class, () -> ui.fade(0.0, Double.NaN));
        backend.fadeListener.onFadeEnded(voice);
        assertEquals(voice, faded[0]);
        click.release();
    }

    /**
     * Backend that plays nothing, counts the voices stopped and paused, and keeps its listeners, its last stream, its
     * last gain, fade duration and looping, and the pause of its output.
     */
    private static final class CountingBackend implements AudioBackend
    {
        /** Voices stopped. */
        private int stopped;
        /** Next voice. */
        private int next = 1;
        /** The listener. */
        private VoiceListener listener;
        /** The fade listener. */
        private FadeListener fadeListener;
        /** The duration of the last fade. */
        private double seconds;
        /** The last stream played. */
        private PcmStream stream;
        /** The last gain given. */
        private double gain;
        /** The looping of the last buffer played. */
        private boolean looping;
        /** Voices paused. */
        private int paused;
        /** {@code true} while the output is paused. */
        private boolean outputPaused;

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
            this.listener = listener;
        }

        @Override
        public void setFadeListener(FadeListener listener)
        {
            fadeListener = listener;
        }

        @Override
        public int play(PcmBuffer buffer, double gain, boolean looping)
        {
            this.gain = gain;
            this.looping = looping;
            return next++;
        }

        @Override
        public int play(PcmStream stream, double gain)
        {
            this.stream = stream;
            this.gain = gain;
            return next++;
        }

        @Override
        public void setGain(int voice, double gain)
        {
            this.gain = gain;
        }

        @Override
        public void fade(int voice, double gain, double seconds)
        {
            this.gain = gain;
            this.seconds = seconds;
        }

        @Override
        public void pause(int voice)
        {
            paused++;
        }

        @Override
        public void resume(int voice)
        {
            // Plays nothing, so nothing to resume.
        }

        @Override
        public void pauseOutput()
        {
            outputPaused = true;
        }

        @Override
        public void resumeOutput()
        {
            outputPaused = false;
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
