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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;

/**
 * Contract suite of {@link AudioBackend} (ADR-0045, ADR-0065, ADR-0068, ADR-0069, REQ-AUDIO-007, REQ-AUDIO-009,
 * REQ-AUDIO-011).
 *
 * <p>
 * Published in the {@code audio-api} test JAR: every backend binds it by creating a backend on an inbox and letting
 * frames of {@link #FORMAT} play. {@link FakeAudioBackend} binds it with its clock; a real backend waits for the time
 * of the frames. Buffer voices and streamed voices are both checked; the streams of the suite are
 * {@link CountingStream}s, which a real backend reads on its own thread. The controls are checked by what they change
 * in time: a looping voice never ends, a paused voice does not end and its stream is not read, a paused output holds
 * every voice; gains are checked for their refusals, since what a backend sounds like, its gain included, is checked by
 * its own platform tests.
 * </p>
 */
public abstract class AudioBackendContract
{
    /** Format of the sounds of the suite. */
    protected static final PcmFormat FORMAT = new PcmFormat(44_100, PcmFormat.STEREO);
    /** Frames of the sound of the suite: 0.05 s. */
    protected static final int FRAMES = 2_205;

    /**
     * Creates a sound of the suite: a quiet square wave.
     *
     * @param frames The number of frames.
     * @return The sound.
     */
    protected static PcmBuffer sound(int frames)
    {
        final short[] samples = new short[frames * FORMAT.channels()];
        for (int i = 0; i < samples.length; i++)
        {
            samples[i] = (short) ((i / 100 & 1) == 0 ? 1_000 : -1_000);
        }
        return new PcmBuffer(FORMAT, samples);
    }

    /**
     * Creates a backend posting to an inbox.
     *
     * @param inbox The inbox.
     * @return The backend, open.
     */
    protected abstract AudioBackend createBackend(InboxSpy inbox);

    /**
     * Lets frames of {@link #FORMAT} play, then returns, so that every end posted by then is in the inbox.
     *
     * @param backend The backend.
     * @param frames The number of frames.
     */
    protected abstract void advance(AudioBackend backend, int frames);

    /**
     * A sound plays on a positive voice until its end, which reaches the listener at the inbox delivery, once.
     */
    @Test
    void testPlayToEnd()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final List<Integer> ended = new ArrayList<>();
        backend.setVoiceListener(ended::add);

        final int voice = backend.play(sound(FRAMES), 1.0, false);

        assertTrue(voice > 0);
        assertTrue(backend.isPlaying(voice));
        advance(backend, 2 * FRAMES);
        assertTrue(ended.isEmpty());
        inbox.deliverAll();
        assertEquals(List.of(Integer.valueOf(voice)), ended);
        assertFalse(backend.isPlaying(voice));
        inbox.deliverAll();
        assertEquals(1, ended.size());
        backend.close();
    }

    /**
     * Voices are distinct and never reused, even after their end.
     */
    @Test
    void testVoicesDistinct()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final int first = backend.play(sound(FRAMES), 1.0, false);
        final int second = backend.play(sound(FRAMES), 1.0, false);
        advance(backend, 2 * FRAMES);
        inbox.deliverAll();

        final int third = backend.play(sound(FRAMES), 1.0, false);

        assertNotEquals(first, second);
        assertNotEquals(first, third);
        assertNotEquals(second, third);
        backend.close();
    }

    /**
     * A stopped voice stops at once and posts no end; stopping again, or an unknown voice, has no effect.
     */
    @Test
    void testStop()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final List<Integer> ended = new ArrayList<>();
        backend.setVoiceListener(ended::add);
        final int voice = backend.play(sound(FRAMES), 1.0, false);

        backend.stop(voice);
        backend.stop(voice);
        backend.stop(Integer.MAX_VALUE);
        advance(backend, 2 * FRAMES);
        inbox.deliverAll();

        assertFalse(backend.isPlaying(voice));
        assertFalse(backend.isPlaying(Integer.MAX_VALUE));
        assertTrue(ended.isEmpty());
        backend.close();
    }

    /**
     * Beyond the voice limit nothing is played and {@link AudioBackend#NO_VOICE} is returned; a voice is free again
     * once one is stopped.
     */
    @Test
    void testVoiceLimit()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final PcmBuffer longer = sound(20 * FRAMES);
        final int max = backend.getMaxVoices();
        assertTrue(max > 0);
        int first = AudioBackend.NO_VOICE;
        for (int i = 0; i < max; i++)
        {
            final int voice = backend.play(longer, 1.0, false);
            assertTrue(voice > 0);
            if (i == 0)
            {
                first = voice;
            }
        }

        assertEquals(AudioBackend.NO_VOICE, backend.play(longer, 1.0, false));
        backend.stop(first);
        assertTrue(backend.play(longer, 1.0, false) > 0);
        backend.close();
    }

    /**
     * Without listener an end is delivered without effect; the listener can be replaced.
     */
    @Test
    void testListener()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final List<Integer> ended = new ArrayList<>();
        backend.setVoiceListener(voice -> ended.add(Integer.valueOf(-voice)));
        backend.setVoiceListener(null);
        backend.play(sound(FRAMES), 1.0, false);
        advance(backend, 2 * FRAMES);
        inbox.deliverAll();
        backend.setVoiceListener(ended::add);
        final int voice = backend.play(sound(FRAMES), 1.0, false);
        advance(backend, 2 * FRAMES);
        inbox.deliverAll();

        assertEquals(List.of(Integer.valueOf(voice)), ended);
        backend.close();
    }

    /**
     * Closing stops every voice and is idempotent; a closed backend refuses to play; a {@code null} buffer is refused.
     */
    @Test
    void testClose()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final PcmBuffer buffer = sound(FRAMES);
        assertThrows(IllegalArgumentException.class, () -> backend.play((PcmBuffer) null, 1.0, false));
        final int voice = backend.play(buffer, 1.0, false);

        backend.close();
        backend.close();

        assertFalse(backend.isPlaying(voice));
        assertThrows(IllegalStateException.class, () -> backend.play(buffer, 1.0, false));
    }

    /**
     * A stream plays on a positive voice until it returns fewer frames than asked; it is read to its end, never after,
     * and its end reaches the listener at the inbox delivery, once.
     */
    @Test
    void testStreamToEnd()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final List<Integer> ended = new ArrayList<>();
        backend.setVoiceListener(ended::add);
        final CountingStream stream = new CountingStream(3 * FRAMES);

        final int voice = backend.play(stream, 1.0);

        assertTrue(voice > 0);
        assertTrue(backend.isPlaying(voice));
        advance(backend, 5 * FRAMES);
        assertTrue(ended.isEmpty());
        inbox.deliverAll();
        assertEquals(List.of(Integer.valueOf(voice)), ended);
        assertFalse(backend.isPlaying(voice));
        assertEquals(3 * FRAMES, stream.getRead());
        final int calls = stream.getCalls();
        advance(backend, 2 * FRAMES);
        inbox.deliverAll();
        assertEquals(calls, stream.getCalls());
        assertEquals(1, ended.size());
        backend.close();
    }

    /**
     * A stopped stream is no longer read once the stop returned and posts no end; buffer and streamed voices share the
     * numbering.
     */
    @Test
    void testStreamStop()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final List<Integer> ended = new ArrayList<>();
        backend.setVoiceListener(ended::add);
        final CountingStream stream = new CountingStream(100 * FRAMES);
        final int buffered = backend.play(sound(FRAMES), 1.0, false);
        final int voice = backend.play(stream, 1.0);
        advance(backend, FRAMES / 2);

        backend.stop(voice);
        final int calls = stream.getCalls();
        advance(backend, 2 * FRAMES);
        inbox.deliverAll();

        assertNotEquals(buffered, voice);
        assertTrue(stream.getRead() > 0);
        assertEquals(calls, stream.getCalls());
        assertFalse(backend.isPlaying(voice));
        assertEquals(List.of(Integer.valueOf(buffered)), ended);
        backend.close();
    }

    /**
     * Streamed and buffer voices share the voice limit; a stream refused for lack of voice is never read.
     */
    @Test
    void testStreamVoiceLimit()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final int max = backend.getMaxVoices();
        int first = AudioBackend.NO_VOICE;
        for (int i = 0; i < max; i++)
        {
            final int voice = backend.play(new CountingStream(100 * FRAMES), 1.0);
            assertTrue(voice > 0);
            if (i == 0)
            {
                first = voice;
            }
        }
        final CountingStream refused = new CountingStream(FRAMES);

        assertEquals(AudioBackend.NO_VOICE, backend.play(refused, 1.0));
        assertEquals(AudioBackend.NO_VOICE, backend.play(sound(FRAMES), 1.0, false));
        advance(backend, FRAMES);
        assertEquals(0, refused.getCalls());
        backend.stop(first);
        assertTrue(backend.play(sound(FRAMES), 1.0, false) > 0);
        backend.close();
    }

    /**
     * Closing stops the streams, which are no longer read; a closed backend refuses to play a stream; a {@code null}
     * stream is refused.
     */
    @Test
    void testStreamClose()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final CountingStream stream = new CountingStream(100 * FRAMES);
        assertThrows(IllegalArgumentException.class, () -> backend.play((PcmStream) null, 1.0));
        final int voice = backend.play(stream, 1.0);
        advance(backend, FRAMES / 2);

        backend.close();
        final int calls = stream.getCalls();
        advance(backend, FRAMES);

        assertFalse(backend.isPlaying(voice));
        assertEquals(calls, stream.getCalls());
        final CountingStream late = new CountingStream(FRAMES);
        assertThrows(IllegalStateException.class, () -> backend.play(late, 1.0));
    }

    /**
     * Gains in {@code [0, 1]} are accepted at the play and later, also for an ended or unknown voice, without effect;
     * NaN, infinite and out of range gains are refused at the play and later.
     */
    @Test
    void testGain()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final PcmBuffer buffer = sound(FRAMES);
        final int voice = backend.play(buffer, 0.0, false);
        final int stream = backend.play(new CountingStream(FRAMES), 0.5);

        backend.setGain(voice, 1.0);
        backend.setGain(stream, 0.25);
        backend.setGain(Integer.MAX_VALUE, 0.5);
        final double[] refused =
        {
            -0.5, 1.5, Double.NaN, Double.POSITIVE_INFINITY
        };
        final CountingStream refusedStream = new CountingStream(FRAMES);
        for (final double gain : refused)
        {
            assertThrows(IllegalArgumentException.class, () -> backend.play(buffer, gain, false));
            assertThrows(IllegalArgumentException.class, () -> backend.play(refusedStream, gain));
            assertThrows(IllegalArgumentException.class, () -> backend.setGain(voice, gain));
        }
        advance(backend, 3 * FRAMES);
        inbox.deliverAll();

        assertFalse(backend.isPlaying(voice));
        assertFalse(backend.isPlaying(stream));
        backend.setGain(voice, 0.5);
        assertEquals(0, refusedStream.getCalls());
        backend.close();
    }

    /**
     * A looping buffer voice never ends by itself and posts no end, until it is stopped; a looping buffer of no frame
     * ends at once.
     */
    @Test
    void testLooping()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final List<Integer> ended = new ArrayList<>();
        backend.setVoiceListener(ended::add);

        final int voice = backend.play(sound(FRAMES), 1.0, true);
        final int empty = backend.play(sound(0), 1.0, true);
        advance(backend, 5 * FRAMES + FRAMES / 3);
        inbox.deliverAll();

        assertTrue(backend.isPlaying(voice));
        assertFalse(backend.isPlaying(empty));
        assertEquals(List.of(Integer.valueOf(empty)), ended);
        backend.stop(voice);
        advance(backend, FRAMES);
        inbox.deliverAll();
        assertFalse(backend.isPlaying(voice));
        assertEquals(1, ended.size());
        backend.close();
    }

    /**
     * A paused buffer voice keeps its voice, is still playing and does not end; resumed, it ends after its remaining
     * frames; pausing or resuming twice, an ended or an unknown voice has no effect; stopping a paused voice posts no
     * end.
     */
    @Test
    void testPause()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final List<Integer> ended = new ArrayList<>();
        backend.setVoiceListener(ended::add);
        final int voice = backend.play(sound(20 * FRAMES), 1.0, false);
        final int stopped = backend.play(sound(20 * FRAMES), 1.0, false);

        backend.pause(voice);
        backend.pause(voice);
        backend.pause(stopped);
        advance(backend, 40 * FRAMES);
        inbox.deliverAll();

        assertTrue(backend.isPlaying(voice));
        assertTrue(ended.isEmpty());
        backend.stop(stopped);
        backend.resume(voice);
        backend.resume(voice);
        backend.pause(Integer.MAX_VALUE);
        backend.resume(Integer.MAX_VALUE);
        advance(backend, 40 * FRAMES);
        inbox.deliverAll();
        assertEquals(List.of(Integer.valueOf(voice)), ended);
        backend.pause(voice);
        backend.resume(voice);
        assertFalse(backend.isPlaying(voice));
        assertFalse(backend.isPlaying(stopped));
        backend.close();
    }

    /**
     * A paused voice counts in the voice limit.
     */
    @Test
    void testPausedVoiceLimit()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final PcmBuffer longer = sound(20 * FRAMES);
        final int max = backend.getMaxVoices();
        for (int i = 0; i < max; i++)
        {
            backend.pause(backend.play(longer, 1.0, false));
        }

        assertEquals(AudioBackend.NO_VOICE, backend.play(longer, 1.0, false));
        backend.close();
    }

    /**
     * A paused stream is not read once the pause returned, and is read again once resumed, to its end.
     */
    @Test
    void testStreamPause()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final List<Integer> ended = new ArrayList<>();
        backend.setVoiceListener(ended::add);
        final CountingStream stream = new CountingStream(3 * FRAMES);
        final int voice = backend.play(stream, 1.0);
        advance(backend, FRAMES / 2);

        backend.pause(voice);
        final int calls = stream.getCalls();
        advance(backend, 5 * FRAMES);
        inbox.deliverAll();

        assertEquals(calls, stream.getCalls());
        assertTrue(backend.isPlaying(voice));
        assertTrue(ended.isEmpty());
        backend.resume(voice);
        advance(backend, 5 * FRAMES);
        inbox.deliverAll();
        assertEquals(3 * FRAMES, stream.getRead());
        assertEquals(List.of(Integer.valueOf(voice)), ended);
        backend.close();
    }

    /**
     * A paused output holds every voice: none ends, no stream is read, a voice played meanwhile waits; once resumed
     * they play on, except a voice paused on its own; pausing or resuming the output twice has no effect.
     */
    @Test
    void testOutputPause()
    {
        final InboxSpy inbox = new InboxSpy();
        final AudioBackend backend = createBackend(inbox);
        final List<Integer> ended = new ArrayList<>();
        backend.setVoiceListener(ended::add);
        final int alone = backend.play(sound(FRAMES), 1.0, false);
        backend.pause(alone);
        final CountingStream stream = new CountingStream(FRAMES);
        final int streamed = backend.play(stream, 1.0);

        backend.pauseOutput();
        backend.pauseOutput();
        final int calls = stream.getCalls();
        final int during = backend.play(sound(FRAMES), 1.0, false);
        advance(backend, 5 * FRAMES);
        inbox.deliverAll();

        assertEquals(calls, stream.getCalls());
        assertTrue(ended.isEmpty());
        assertTrue(backend.isPlaying(during));
        backend.resumeOutput();
        backend.resumeOutput();
        advance(backend, 5 * FRAMES);
        inbox.deliverAll();
        assertEquals(2, ended.size());
        assertTrue(ended.contains(Integer.valueOf(streamed)));
        assertTrue(ended.contains(Integer.valueOf(during)));
        assertTrue(backend.isPlaying(alone));
        backend.resume(alone);
        advance(backend, 5 * FRAMES);
        inbox.deliverAll();
        assertEquals(3, ended.size());
        backend.close();
        backend.pauseOutput();
        backend.resumeOutput();
    }

    /**
     * Stream of the suite in {@link #FORMAT}: a quiet square wave of a given number of frames, counting the frames read
     * and the calls. Thread-safe, since a real backend reads it on its own thread.
     */
    protected static final class CountingStream implements PcmStream
    {
        /** The frames of the sound. */
        private final int frames;
        /** The frames read. */
        private int read;
        /** The calls of {@link #read(short[], int, int)}. */
        private int calls;

        /**
         * Creates the stream.
         *
         * @param frames The frames of the sound.
         */
        public CountingStream(int frames)
        {
            this.frames = frames;
        }

        /**
         * Returns the frames read.
         *
         * @return The frames given to the backend.
         */
        public synchronized int getRead()
        {
            return read;
        }

        /**
         * Returns the number of reads.
         *
         * @return The calls of {@link #read(short[], int, int)}.
         */
        public synchronized int getCalls()
        {
            return calls;
        }

        @Override
        public PcmFormat getFormat()
        {
            return FORMAT;
        }

        @Override
        public synchronized int read(short[] target, int offset, int count)
        {
            final int channels = FORMAT.channels();
            if (target == null || offset < 0 || count < 0 || offset > target.length - count * channels)
            {
                throw new IllegalArgumentException("Invalid frame range");
            }
            calls++;
            final int copied = Math.min(count, frames - read);
            for (int i = 0; i < copied * channels; i++)
            {
                target[offset + i] = (short) (((read * channels + i) / 100 & 1) == 0 ? 1_000 : -1_000);
            }
            read += copied;
            return copied;
        }
    }
}
