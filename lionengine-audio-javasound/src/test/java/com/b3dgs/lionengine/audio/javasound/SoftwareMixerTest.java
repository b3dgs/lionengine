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
package com.b3dgs.lionengine.audio.javasound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;

import com.sun.management.ThreadMXBean;

/**
 * Tests of the {@link SoftwareMixer}, headless, the test mixing the buffers (ADR-0069, ADR-0070, REQ-AUDIO-002):
 * gains per buffer, sums and clipping, looping, pause, the output pause, stream pulls, format conversion, end posting
 * and the allocation of the mixing path.
 */
final class SoftwareMixerTest
{
    /** Output rate. */
    private static final int RATE = 44_100;
    /** Frames of a buffer of the tests. */
    private static final int FRAMES = 64;
    /** Output stereo format. */
    private static final PcmFormat STEREO = new PcmFormat(RATE, PcmFormat.STEREO);
    /** Output-rate mono format. */
    private static final PcmFormat MONO = new PcmFormat(RATE, PcmFormat.MONO);
    /** Mixes of the allocation test. */
    private static final int ITERATIONS = 2_000;
    /** Bytes tolerated by the allocation test. */
    private static final long ALLOCATION_TOLERANCE = 1_024L;

    /**
     * Creates a sound of a constant value.
     *
     * @param format The format.
     * @param frames The frames.
     * @param value The value of every sample.
     * @return The sound.
     */
    private static PcmBuffer constant(PcmFormat format, int frames, int value)
    {
        final short[] samples = new short[frames * format.channels()];
        Arrays.fill(samples, (short) value);
        return new PcmBuffer(format, samples);
    }

    /**
     * Creates a ramp: frame {@code i} has the value {@code i * scale} on every channel.
     *
     * @param format The format.
     * @param frames The frames.
     * @param scale The value step per frame.
     * @return The sound.
     */
    private static PcmBuffer ramp(PcmFormat format, int frames, int scale)
    {
        final int channels = format.channels();
        final short[] samples = new short[frames * channels];
        for (int i = 0; i < samples.length; i++)
        {
            samples[i] = (short) (i / channels * scale);
        }
        return new PcmBuffer(format, samples);
    }

    /**
     * Reads a sample of a mixed buffer.
     *
     * @param bytes The buffer.
     * @param frame The frame.
     * @param channel The channel, {@code 0} left, {@code 1} right.
     * @return The sample.
     */
    private static int sample(byte[] bytes, int frame, int channel)
    {
        final int index = (frame * 2 + channel) * 2;
        return (short) (bytes[index] & 0xFF | bytes[index + 1] << 8);
    }

    /** The inbox. */
    private final InboxSpy inbox = new InboxSpy();
    /** The mixer. */
    private final SoftwareMixer mixer = new SoftwareMixer(inbox, 4, RATE, FRAMES);
    /** The bytes of a buffer. */
    private final byte[] bytes = new byte[mixer.getBufferBytes()];

    /**
     * Mixes one buffer.
     *
     * @return The buffer.
     */
    private byte[] mix()
    {
        assertTrue(mixer.mix(bytes));
        return bytes;
    }

    /**
     * Asserts every frame of the buffer has a value on both channels.
     *
     * @param expected The value.
     */
    private void assertAll(int expected)
    {
        for (int i = 0; i < FRAMES; i++)
        {
            assertEquals(expected, sample(bytes, i, 0), "Left of frame " + i);
            assertEquals(expected, sample(bytes, i, 1), "Right of frame " + i);
        }
    }

    /**
     * The gain multiplies the samples, is read once per buffer, and a change applies from the next buffer; voices are
     * summed.
     */
    @Test
    void testGainPerBuffer()
    {
        final int voice = mixer.play(constant(STEREO, 10 * FRAMES, 1_000), 0.5, false);

        mix();
        assertAll(500);
        mixer.setGain(voice, 0.25);
        assertEquals(0.25, mixer.getGain(voice), 1e-6);
        mix();
        assertAll(250);
        mixer.play(constant(STEREO, 10 * FRAMES, 1_000), 1.0, false);
        mix();
        assertAll(1_250);
        mixer.setGain(voice, 0.0);
        mix();
        assertAll(1_000);
    }

    /**
     * The sum is clipped to 16 bits.
     */
    @Test
    void testClipping()
    {
        mixer.play(constant(STEREO, FRAMES, 30_000), 1.0, false);
        mixer.play(constant(STEREO, FRAMES, 30_000), 1.0, false);
        mix();
        assertAll(Short.MAX_VALUE);
        mixer.play(constant(STEREO, FRAMES, -30_000), 1.0, false);
        mixer.play(constant(STEREO, FRAMES, -30_000), 1.0, false);
        mix();
        assertAll(Short.MIN_VALUE);
    }

    /**
     * A looping buffer starts again from its first frame after its last one, without gap, across buffers; a mono
     * sound plays on both channels.
     */
    @Test
    void testLoopingWrap()
    {
        final int voice = mixer.play(ramp(MONO, 10, 1), 1.0, true);

        for (int buffer = 0; buffer < 3; buffer++)
        {
            mix();
            for (int i = 0; i < FRAMES; i++)
            {
                assertEquals((buffer * FRAMES + i) % 10, sample(bytes, i, 0));
                assertEquals((buffer * FRAMES + i) % 10, sample(bytes, i, 1));
            }
        }
        assertTrue(mixer.isPlaying(voice));
        assertTrue(inbox.getPosted().isEmpty());
    }

    /**
     * A sound ends inside a buffer, the rest being silent; its end is posted once from the mixing and reaches the
     * listener at the delivery.
     */
    @Test
    void testEndPosted()
    {
        final List<Integer> ended = new ArrayList<>();
        mixer.setVoiceListener(ended::add);
        final int voice = mixer.play(ramp(STEREO, 10, 3), 1.0, false);

        mix();

        for (int i = 0; i < FRAMES; i++)
        {
            assertEquals(i < 10 ? i * 3 : 0, sample(bytes, i, 0));
        }
        assertFalse(mixer.isPlaying(voice));
        assertEquals(1, inbox.getPosted().size());
        assertTrue(ended.isEmpty());
        inbox.deliverAll();
        assertEquals(List.of(Integer.valueOf(voice)), ended);
        mix();
        inbox.deliverAll();
        assertEquals(1, ended.size());
    }

    /**
     * A paused voice is silent and holds its position; resumed, it continues where it was; stopped, it posts nothing.
     */
    @Test
    void testPause()
    {
        final int voice = mixer.play(ramp(MONO, 10 * FRAMES, 1), 1.0, false);
        mix();

        mixer.pause(voice);
        mix();
        assertAll(0);
        mixer.resume(voice);
        mix();
        for (int i = 0; i < FRAMES; i++)
        {
            assertEquals(FRAMES + i, sample(bytes, i, 0));
        }
        mixer.pause(voice);
        mixer.stop(voice);
        mix();
        assertFalse(mixer.isPlaying(voice));
        assertTrue(inbox.getPosted().isEmpty());
    }

    /**
     * The output pause silences every voice and holds them; once resumed they continue, except a voice paused on its
     * own; a voice played meanwhile starts at the resume.
     */
    @Test
    void testOutputPause()
    {
        final int alone = mixer.play(ramp(MONO, 10 * FRAMES, 1), 1.0, false);
        mixer.pause(alone);
        mixer.pauseOutput();
        final int during = mixer.play(ramp(MONO, 10 * FRAMES, 2), 1.0, false);

        mix();
        assertAll(0);
        mixer.resumeOutput();
        mix();
        for (int i = 0; i < FRAMES; i++)
        {
            assertEquals(i * 2, sample(bytes, i, 0));
        }
        mixer.resume(alone);
        mix();
        for (int i = 0; i < FRAMES; i++)
        {
            assertEquals(FRAMES * 2 + i * 2 + i, sample(bytes, i, 0));
        }
        assertTrue(mixer.isPlaying(during));
    }

    /**
     * A stream is pulled only while mixing, to its end and never after; its frames are mixed in order; its end is
     * posted.
     */
    @Test
    void testStreamPull()
    {
        final RampStream stream = new RampStream(STEREO, 3L * FRAMES + 5L);
        final int voice = mixer.play(stream, 1.0);
        assertEquals(0, stream.getCalls());

        for (int buffer = 0; buffer < 3; buffer++)
        {
            mix();
            for (int i = 0; i < FRAMES; i++)
            {
                assertEquals(buffer * FRAMES + i, sample(bytes, i, 1));
            }
        }
        mix();
        assertEquals(3 * FRAMES + 4, sample(bytes, 4, 0));
        assertEquals(0, sample(bytes, 5, 0));
        assertFalse(mixer.isPlaying(voice));
        assertEquals(3L * FRAMES + 5L, stream.getRead());
        final int calls = stream.getCalls();
        mix();
        assertEquals(calls, stream.getCalls());
        assertEquals(1, inbox.getPosted().size());
    }

    /**
     * A paused or stopped stream is not pulled.
     */
    @Test
    void testStreamPausedNotPulled()
    {
        final RampStream stream = new RampStream(STEREO, -1L);
        final int voice = mixer.play(stream, 1.0);
        mix();
        mixer.pause(voice);
        stream.forbid();
        for (int i = 0; i < 10 * MixVoice.WINDOW_FRAMES / FRAMES; i++)
        {
            mix();
        }
        mixer.stop(voice);
        mix();

        assertFalse(stream.isViolated());
    }

    /**
     * A sound at half the output rate is resampled by linear interpolation; at twice the rate every other frame is
     * played; a resampled stream is continuous across the window.
     */
    @Test
    void testResampling()
    {
        mixer.play(ramp(new PcmFormat(RATE / 2, PcmFormat.MONO), 10 * FRAMES, 100), 1.0, false);
        mix();
        for (int i = 0; i < FRAMES; i++)
        {
            assertEquals(i * 50, sample(bytes, i, 0));
        }
        mixer.close();

        final SoftwareMixer down = new SoftwareMixer(inbox, 1, RATE, FRAMES);
        down.play(ramp(new PcmFormat(RATE * 2, PcmFormat.STEREO), 10 * FRAMES, 1), 1.0, false);
        assertTrue(down.mix(bytes));
        for (int i = 0; i < FRAMES; i++)
        {
            assertEquals(i * 2, sample(bytes, i, 1));
        }
        down.close();

        final SoftwareMixer stream = new SoftwareMixer(inbox, 1, RATE, FRAMES);
        stream.play(new RampStream(new PcmFormat(RATE / 2, PcmFormat.MONO), -1L), 1.0);
        final int buffers = 3 * MixVoice.WINDOW_FRAMES / FRAMES;
        for (int buffer = 0; buffer < buffers; buffer++)
        {
            assertTrue(stream.mix(bytes));
            for (int i = 0; i < FRAMES; i += 2)
            {
                assertEquals((buffer * FRAMES + i) / 2, sample(bytes, i, 0));
            }
        }
    }

    /**
     * A looping resampled buffer interpolates its last frame toward its first one.
     */
    @Test
    void testResampledLoop()
    {
        mixer.play(ramp(new PcmFormat(RATE / 2, PcmFormat.MONO), 4, 100), 1.0, true);
        mix();
        final int[] expected =
        {
            0, 50, 100, 150, 200, 250, 300, 150, 0, 50
        };
        for (int i = 0; i < expected.length; i++)
        {
            assertEquals(expected[i], sample(bytes, i, 0));
        }
    }

    /**
     * Ends keep being posted when they are not delivered while sounds keep being played: the pool of events grows on
     * the runtime thread, and every end reaches the listener in order.
     */
    @Test
    void testEndPoolGrows()
    {
        final List<Integer> ended = new ArrayList<>();
        mixer.setVoiceListener(ended::add);
        final List<Integer> played = new ArrayList<>();
        for (int i = 0; i < 25; i++)
        {
            for (int v = 0; v < mixer.getMaxVoices(); v++)
            {
                played.add(Integer.valueOf(mixer.play(constant(STEREO, 1, 1), 1.0, false)));
            }
            mix();
        }

        inbox.deliverAll();
        assertEquals(played, ended);
    }

    /**
     * An end rejected by a full inbox is lost and counted by the inbox; the mixer goes on.
     */
    @Test
    void testInboxFull()
    {
        final InboxSpy full = new InboxSpy(1);
        final SoftwareMixer small = new SoftwareMixer(full, 2, RATE, FRAMES);
        small.play(constant(STEREO, 1, 1), 1.0, false);
        small.play(constant(STEREO, 1, 1), 1.0, false);

        assertTrue(small.mix(bytes));

        assertEquals(1L, full.getDroppedCount());
        assertTrue(small.play(constant(STEREO, 1, 1), 1.0, false) > 0);
        assertTrue(small.mix(bytes));
    }

    /**
     * Mixing allocates nothing: buffers at the output rate and resampled, mono and stereo, looping, and streams.
     */
    @Test
    void testMixAllocatesNothing()
    {
        final SoftwareMixer busy = new SoftwareMixer(inbox, 8, RATE, JavaSoundBackend.BUFFER_FRAMES);
        final byte[] buffer = new byte[busy.getBufferBytes()];
        busy.play(ramp(STEREO, 1_000, 1), 0.5, true);
        busy.play(ramp(new PcmFormat(22_050, PcmFormat.MONO), 777, 1), 0.5, true);
        busy.play(ramp(new PcmFormat(48_000, PcmFormat.STEREO), 3_000, 1), 0.5, true);
        busy.play(new RampStream(STEREO, -1L), 0.5);
        busy.play(new RampStream(new PcmFormat(32_000, PcmFormat.MONO), -1L), 0.5);
        final int paused = busy.play(new RampStream(STEREO, -1L), 0.5);
        busy.pause(paused);
        for (int i = 0; i < ITERATIONS; i++)
        {
            busy.mix(buffer);
        }
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < ITERATIONS; i++)
        {
            busy.mix(buffer);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, "Allocated " + allocated + " bytes");
        assertEquals(6, countPlaying(busy));
    }

    /**
     * Counts the voices playing among the first ones.
     *
     * @param target The mixer.
     * @return The voices playing.
     */
    private static int countPlaying(SoftwareMixer target)
    {
        int count = 0;
        for (int voice = 1; voice <= target.getMaxVoices(); voice++)
        {
            if (target.isPlaying(voice))
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Invalid arguments are refused; a closed mixer mixes nothing and refuses to play.
     */
    @Test
    void testArgumentsAndClose()
    {
        assertThrows(IllegalArgumentException.class, () -> new SoftwareMixer(null, 1, RATE, FRAMES));
        assertThrows(IllegalArgumentException.class, () -> new SoftwareMixer(inbox, 0, RATE, FRAMES));
        assertThrows(IllegalArgumentException.class, () -> new SoftwareMixer(inbox, 1, 0, FRAMES));
        assertThrows(IllegalArgumentException.class, () -> new SoftwareMixer(inbox, 1, RATE, 0));
        final PcmBuffer sound = constant(STEREO, FRAMES, 1);
        final int voice = mixer.play(sound, 1.0, false);

        mixer.close();

        assertFalse(mixer.mix(bytes));
        assertFalse(mixer.isPlaying(voice));
        assertEquals(Double.NaN, mixer.getGain(voice));
        assertThrows(IllegalStateException.class, () -> mixer.play(sound, 1.0, false));
    }
}
