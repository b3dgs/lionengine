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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.InboxEvent;
import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;

import com.sun.management.ThreadMXBean;

/**
 * Tests of the fades of the {@link SoftwareMixer}, headless, the test mixing the buffers (ADR-0071, REQ-AUDIO-012):
 * the linear ramp per buffer and its rounding to whole buffers, the completion posted once the last buffer is mixed,
 * a gain change and a new fade during a ramp, the pause, a voice ending during its ramp, the event pool, and the
 * allocation of the mixing path with fades running.
 */
final class SoftwareMixerFadeTest
{
    /** Output rate. */
    private static final int RATE = 44_100;
    /** Frames of a buffer of the tests. */
    private static final int FRAMES = 64;
    /** Output stereo format. */
    private static final PcmFormat STEREO = new PcmFormat(RATE, PcmFormat.STEREO);
    /** A duration of a little less than four buffers, so a ramp of four buffers. */
    private static final double FOUR_BUFFERS = (4.0 * FRAMES - 1.0) / RATE;
    /** A duration of a little less than two buffers, so a ramp of two buffers. */
    private static final double TWO_BUFFERS = (2.0 * FRAMES - 1.0) / RATE;
    /** Mixes of the allocation test. */
    private static final int ITERATIONS = 2_000;
    /** Bytes tolerated by the allocation test. */
    private static final long ALLOCATION_TOLERANCE = 1_024L;

    /**
     * Creates a sound of a constant value.
     *
     * @param frames The frames.
     * @return The sound, of value 1,000.
     */
    private static PcmBuffer constant(int frames)
    {
        final short[] samples = new short[frames * PcmFormat.STEREO];
        Arrays.fill(samples, (short) 1_000);
        return new PcmBuffer(STEREO, samples);
    }

    /** The inbox. */
    private final InboxSpy inbox = new InboxSpy();
    /** The mixer. */
    private final SoftwareMixer mixer = new SoftwareMixer(inbox, 4, RATE, FRAMES);
    /** The bytes of a buffer. */
    private final byte[] bytes = new byte[mixer.getBufferBytes()];
    /** The voices whose fade ended. */
    private final List<Integer> faded = new ArrayList<>();

    /**
     * Mixes one buffer and returns its first left sample, every frame of it being checked equal.
     *
     * @return The value of the buffer.
     */
    private int mix()
    {
        assertTrue(mixer.mix(bytes));
        final int first = sample(0);
        for (int i = 0; i < FRAMES * PcmFormat.STEREO; i++)
        {
            assertEquals(first, sample(i), "Sample " + i);
        }
        return first;
    }

    /**
     * Reads a sample of the mixed buffer.
     *
     * @param index The sample, interleaved.
     * @return The value.
     */
    private int sample(int index)
    {
        return (short) (bytes[index * 2] & 0xFF | bytes[index * 2 + 1] << 8);
    }

    /**
     * The gain steps linearly once per buffer, constant within a buffer, the last buffer at the target; the completion
     * is posted once that buffer is mixed, reaches the listener at the delivery, and the voice plays on at the target.
     */
    @Test
    void testRampPerBuffer()
    {
        mixer.setFadeListener(faded::add);
        final int voice = mixer.play(constant(100 * FRAMES), 1.0, false);
        mixer.fade(voice, 0.0, FOUR_BUFFERS);

        assertEquals(750, mix());
        assertEquals(500, mix());
        assertEquals(250, mix());
        inbox.deliverAll();
        assertTrue(faded.isEmpty());
        assertEquals(0, mix());
        assertTrue(faded.isEmpty());
        inbox.deliverAll();

        assertEquals(List.of(Integer.valueOf(voice)), faded);
        assertEquals(0, mix());
        assertTrue(mixer.isPlaying(voice));
        inbox.deliverAll();
        assertEquals(1, faded.size());
    }

    /**
     * A duration is rounded up to whole buffers, at least one: no duration reaches the target at the next buffer.
     */
    @Test
    void testRounding()
    {
        mixer.setFadeListener(faded::add);
        final int voice = mixer.play(constant(100 * FRAMES), 1.0, false);

        mixer.fade(voice, 0.5, 0.0);
        assertEquals(500, mix());
        inbox.deliverAll();
        assertEquals(1, faded.size());
        mixer.fade(voice, 0.0, (FRAMES + 1.0) / RATE);
        assertEquals(250, mix());
        assertEquals(0, mix());
        inbox.deliverAll();

        assertEquals(2, faded.size());
    }

    /**
     * A gain set during a ramp becomes its target, the buffers left kept; a new fade replaces the ramp from the gain
     * reached, the replaced one posting nothing.
     */
    @Test
    void testRetargetAndReplace()
    {
        mixer.setFadeListener(faded::add);
        final int voice = mixer.play(constant(100 * FRAMES), 1.0, false);
        mixer.fade(voice, 0.0, FOUR_BUFFERS);
        assertEquals(750, mix());
        assertEquals(500, mix());

        mixer.setGain(voice, 1.0);
        assertEquals(750, mix());
        mixer.fade(voice, 0.25, TWO_BUFFERS);
        assertEquals(500, mix());
        assertEquals(250, mix());
        inbox.deliverAll();

        assertEquals(List.of(Integer.valueOf(voice)), faded);
        mixer.setGain(voice, 0.5);
        assertEquals(500, mix());
    }

    /**
     * A ramp holds while its voice or the output is paused.
     */
    @Test
    void testPauseHolds()
    {
        mixer.setFadeListener(faded::add);
        final int voice = mixer.play(constant(100 * FRAMES), 1.0, false);
        mixer.fade(voice, 0.0, FOUR_BUFFERS);
        assertEquals(750, mix());

        mixer.pause(voice);
        mix();
        mix();
        mixer.resume(voice);
        assertEquals(500, mix());
        mixer.pauseOutput();
        mix();
        mixer.resumeOutput();
        assertEquals(250, mix());
        assertEquals(0, mix());
        inbox.deliverAll();

        assertEquals(List.of(Integer.valueOf(voice)), faded);
    }

    /**
     * A voice that ends during its ramp, its last buffer included, posts its end only; a stopped voice posts nothing.
     */
    @Test
    void testDroppedWithVoice()
    {
        final List<Integer> ended = new ArrayList<>();
        mixer.setFadeListener(faded::add);
        mixer.setVoiceListener(ended::add);
        final int shorter = mixer.play(constant(2 * FRAMES), 1.0, false);
        final int last = mixer.play(constant(4 * FRAMES - 1), 1.0, false);
        final int stopped = mixer.play(constant(100 * FRAMES), 1.0, false);
        mixer.fade(shorter, 0.0, FOUR_BUFFERS);
        mixer.fade(last, 0.0, FOUR_BUFFERS);
        mixer.fade(stopped, 0.0, FOUR_BUFFERS);
        mix();
        mixer.stop(stopped);

        for (int i = 0; i < 5; i++)
        {
            assertTrue(mixer.mix(bytes));
        }
        inbox.deliverAll();

        assertTrue(faded.isEmpty());
        assertEquals(List.of(Integer.valueOf(shorter), Integer.valueOf(last)), ended);
    }

    /**
     * Completions keep being posted when they are not delivered while fades keep being started: the pool of events
     * grows on the runtime thread, and every completion reaches the listener in order.
     */
    @Test
    void testPoolGrows()
    {
        mixer.setFadeListener(faded::add);
        final int[] voices = new int[mixer.getMaxVoices()];
        for (int v = 0; v < voices.length; v++)
        {
            voices[v] = mixer.play(constant(100 * FRAMES), 1.0, true);
        }
        final List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 25; i++)
        {
            for (final int voice : voices)
            {
                mixer.fade(voice, i % 2, 0.0);
                expected.add(Integer.valueOf(voice));
            }
            assertTrue(mixer.mix(bytes));
        }

        assertTrue(mixer.getEventPoolSize() > voices.length * 2);
        inbox.deliverAll();
        assertEquals(expected, faded);
        assertFalse(faded.isEmpty());
    }

    /**
     * Mixing allocates nothing with fades running, ramping and completing, nor do the fades started between buffers
     * and the delivery of their completions, once the pool holds enough events; the inbox of the test allocates
     * nothing either.
     */
    @Test
    void testFadingMixAllocatesNothing()
    {
        final ArrayInbox spy = new ArrayInbox();
        final SoftwareMixer busy = new SoftwareMixer(spy, 8, RATE, JavaSoundBackend.BUFFER_FRAMES);
        final byte[] buffer = new byte[busy.getBufferBytes()];
        final int[] counted = new int[1];
        busy.setFadeListener(voice -> counted[0]++);
        final int longRamp = busy.play(constant(1_000), 1.0, true);
        final int resampled = busy.play(new PcmBuffer(new PcmFormat(22_050, PcmFormat.MONO), new short[777]), 1.0, true);
        final int streamed = busy.play(new RampStream(STEREO, -1L), 1.0);
        final int repeated = busy.play(constant(3_000), 1.0, true);
        busy.fade(longRamp, 0.0, 1_000.0);
        busy.fade(resampled, 0.5, 1_000.0);
        busy.fade(streamed, 0.25, 1_000.0);
        final Runnable workload = () ->
        {
            for (int i = 0; i < ITERATIONS; i++)
            {
                busy.fade(repeated, i % 2, i % 3 * 0.01);
                busy.mix(buffer);
                if (i % 4 == 0)
                {
                    spy.deliverAll();
                }
            }
            spy.deliverAll();
        };
        workload.run();
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();

        final long before = bean.getCurrentThreadAllocatedBytes();
        workload.run();
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, "Allocated " + allocated + " bytes");
        assertTrue(counted[0] > 0);
        assertTrue(busy.isPlaying(longRamp));
    }

    /**
     * Inbox of a fixed array, single-threaded, allocating nothing, for the allocation test.
     */
    private static final class ArrayInbox implements Inbox
    {
        /** Capacity of the inbox. */
        private static final int CAPACITY = 64;
        /** The events posted. */
        private final InboxEvent[] events = new InboxEvent[CAPACITY];
        /** The events posted and not delivered. */
        private int count;

        /**
         * Creates the inbox.
         */
        ArrayInbox()
        {
            super();
        }

        @Override
        public boolean post(InboxEvent event)
        {
            final boolean accepted = count < CAPACITY;
            if (accepted)
            {
                events[count] = event;
                count++;
            }
            return accepted;
        }

        @Override
        public void deliverAll()
        {
            final int posted = count;
            count = 0;
            for (int i = 0; i < posted; i++)
            {
                final InboxEvent event = events[i];
                events[i] = null;
                event.deliver();
            }
        }

        @Override
        public long getDroppedCount()
        {
            return 0L;
        }
    }
}
