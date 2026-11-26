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
 * Contract suite of {@link AudioBackend} (ADR-0045, ADR-0065, ADR-0068, REQ-AUDIO-007, REQ-AUDIO-009).
 *
 * <p>
 * Published in the {@code audio-api} test JAR: every backend binds it by creating a backend on an inbox and letting
 * frames of {@link #FORMAT} play. {@link FakeAudioBackend} binds it with its clock; a real backend waits for the time
 * of the frames. Buffer voices and streamed voices are both checked; the streams of the suite are
 * {@link CountingStream}s, which a real backend reads on its own thread. What a backend sounds like is checked by its
 * own platform tests.
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

        final int voice = backend.play(sound(FRAMES));

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
        final int first = backend.play(sound(FRAMES));
        final int second = backend.play(sound(FRAMES));
        advance(backend, 2 * FRAMES);
        inbox.deliverAll();

        final int third = backend.play(sound(FRAMES));

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
        final int voice = backend.play(sound(FRAMES));

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
            final int voice = backend.play(longer);
            assertTrue(voice > 0);
            if (i == 0)
            {
                first = voice;
            }
        }

        assertEquals(AudioBackend.NO_VOICE, backend.play(longer));
        backend.stop(first);
        assertTrue(backend.play(longer) > 0);
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
        backend.play(sound(FRAMES));
        advance(backend, 2 * FRAMES);
        inbox.deliverAll();
        backend.setVoiceListener(ended::add);
        final int voice = backend.play(sound(FRAMES));
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
        assertThrows(IllegalArgumentException.class, () -> backend.play((PcmBuffer) null));
        final int voice = backend.play(buffer);

        backend.close();
        backend.close();

        assertFalse(backend.isPlaying(voice));
        assertThrows(IllegalStateException.class, () -> backend.play(buffer));
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

        final int voice = backend.play(stream);

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
        final int buffered = backend.play(sound(FRAMES));
        final int voice = backend.play(stream);
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
            final int voice = backend.play(new CountingStream(100 * FRAMES));
            assertTrue(voice > 0);
            if (i == 0)
            {
                first = voice;
            }
        }
        final CountingStream refused = new CountingStream(FRAMES);

        assertEquals(AudioBackend.NO_VOICE, backend.play(refused));
        assertEquals(AudioBackend.NO_VOICE, backend.play(sound(FRAMES)));
        advance(backend, FRAMES);
        assertEquals(0, refused.getCalls());
        backend.stop(first);
        assertTrue(backend.play(sound(FRAMES)) > 0);
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
        assertThrows(IllegalArgumentException.class, () -> backend.play((PcmStream) null));
        final int voice = backend.play(stream);
        advance(backend, FRAMES / 2);

        backend.close();
        final int calls = stream.getCalls();
        advance(backend, FRAMES);

        assertFalse(backend.isPlaying(voice));
        assertEquals(calls, stream.getCalls());
        assertThrows(IllegalStateException.class, () -> backend.play(new CountingStream(FRAMES)));
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
