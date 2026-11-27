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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.PcmBuffer;

/**
 * Tests of the {@link JavaSoundBackend} with its mixing thread on a {@link ManualOutput}, headless (ADR-0018,
 * ADR-0070, REQ-AUDIO-002): the thread mixes into the output and posts the ends from itself; closing stops it; no
 * stream is read once a stop, a pause or a close returned, while the thread mixes as fast as it can.
 */
final class JavaSoundBackendTest
{
    /** Rounds of the race tests. */
    private static final int ROUNDS = 200;
    /** Buffers mixed before each stop of the race tests. */
    private static final int BUFFERS = 3;

    /**
     * Creates a backend on a manual output.
     *
     * @param inbox The inbox.
     * @param output The output.
     * @return The backend.
     */
    private static JavaSoundBackend create(InboxSpy inbox, ManualOutput output)
    {
        return new JavaSoundBackend(inbox, JavaSoundBackend.DEFAULT_MAX_VOICES, output);
    }

    /**
     * The defaults match the latency target: 4 buffers between a play and the device, within 50 ms; the voices are
     * twice the short voices of {@link Audio} (OQ-175).
     */
    @Test
    void testDefaults()
    {
        final double latency = (double) JavaSoundBackend.BUFFER_FRAMES
                               * (JavaSoundBackend.LINE_BUFFERS + 1)
                               / JavaSoundBackend.SAMPLE_RATE;
        assertTrue(latency <= 0.05, "Latency " + latency);
        assertEquals(32, JavaSoundBackend.DEFAULT_MAX_VOICES);
        assertEquals(44_100, JavaSoundBackend.FORMAT.sampleRate());
        assertEquals(2, JavaSoundBackend.FORMAT.channels());
        final ManualOutput output = new ManualOutput(JavaSoundBackend.BUFFER_FRAMES);
        final JavaSoundBackend backend = create(new InboxSpy(), output);
        final Audio audio = new Audio(backend);
        final PcmBuffer sound = new PcmBuffer(JavaSoundBackend.FORMAT, new short[2 * 100_000]);
        for (int i = 0; i < 16; i++)
        {
            assertNotEquals(AudioBackend.NO_VOICE, backend.play(sound, 1.0, false));
        }
        assertNotEquals(AudioBackend.NO_VOICE, backend.play(sound, 1.0, false));
        assertEquals(0, audio.getPlayingCount());
        backend.close();
    }

    /**
     * The mixing thread writes the mixed buffers to the output, and the ends are posted from it, delivered on the
     * calling thread.
     */
    @Test
    void testMixesOnItsThread()
    {
        final InboxSpy inbox = new InboxSpy();
        final ManualOutput output = new ManualOutput(JavaSoundBackend.BUFFER_FRAMES);
        final JavaSoundBackend backend = create(inbox, output);
        final List<Thread> threads = new ArrayList<>();
        backend.setVoiceListener(voice -> threads.add(Thread.currentThread()));
        final short[] samples = new short[2 * JavaSoundBackend.BUFFER_FRAMES];
        java.util.Arrays.fill(samples, (short) 1_000);
        // Waits for the first buffer, mixed silent at the start of the thread, so that the voice is in the second.
        output.advance(0);
        final int voice = backend.play(new PcmBuffer(JavaSoundBackend.FORMAT, samples), 0.5, false);

        output.advance(1);
        output.advance(1);

        final byte[] last = output.getLast();
        assertEquals(500, (short) (last[0] & 0xFF | last[1] << 8));
        assertFalse(backend.isPlaying(voice));
        inbox.deliverAll();
        assertEquals(List.of(Thread.currentThread()), threads);
        assertTrue(backend.getMixedFrames() >= 2L * JavaSoundBackend.BUFFER_FRAMES);
        assertTrue(backend.getPlayedFrames() >= JavaSoundBackend.BUFFER_FRAMES);
        backend.close();
    }

    /**
     * Closing stops the mixing thread and closes the output; closing again has no effect.
     */
    @Test
    void testCloseStopsThread()
    {
        final ManualOutput output = new ManualOutput(JavaSoundBackend.BUFFER_FRAMES);
        final JavaSoundBackend backend = create(new InboxSpy(), output);
        assertTrue(backend.isMixing());

        backend.close();
        backend.close();

        assertFalse(backend.isMixing());
        assertTrue(output.isClosed());
        final RampStream stream = new RampStream(JavaSoundBackend.FORMAT, 1L);
        assertThrows(IllegalStateException.class, () -> backend.play(stream, 1.0));
    }

    /**
     * No stream is read once a stop returned, while the thread mixes as fast as it can.
     */
    @Test
    void testNoReadAfterStop()
    {
        final ManualOutput output = new ManualOutput(JavaSoundBackend.BUFFER_FRAMES);
        final JavaSoundBackend backend = create(new InboxSpy(), output);
        output.setFreeRunning(true);
        final List<RampStream> streams = new ArrayList<>();
        for (int i = 0; i < ROUNDS; i++)
        {
            final RampStream stream = new RampStream(JavaSoundBackend.FORMAT, -1L);
            streams.add(stream);
            final int voice = backend.play(stream, 1.0);
            final long written = output.getWritten();
            while (output.getWritten() < written + BUFFERS)
            {
                Thread.onSpinWait();
            }
            if (i % 2 == 0)
            {
                backend.stop(voice);
            }
            else
            {
                backend.pause(voice);
            }
            stream.forbid();
            backend.stop(voice);
        }
        final long written = output.getWritten();
        while (output.getWritten() < written + BUFFERS)
        {
            Thread.onSpinWait();
        }
        backend.close();

        for (final RampStream stream : streams)
        {
            assertTrue(stream.getCalls() > 0);
            assertFalse(stream.isViolated());
        }
    }

    /**
     * No stream is read once the output pause or the close returned, while the thread mixes as fast as it can.
     */
    @Test
    void testNoReadAfterOutputPauseAndClose()
    {
        final ManualOutput output = new ManualOutput(JavaSoundBackend.BUFFER_FRAMES);
        final JavaSoundBackend backend = create(new InboxSpy(), output);
        output.setFreeRunning(true);
        final RampStream paused = new RampStream(JavaSoundBackend.FORMAT, -1L);
        backend.play(paused, 1.0);
        final long start = output.getWritten();
        while (output.getWritten() < start + BUFFERS)
        {
            Thread.onSpinWait();
        }
        backend.pauseOutput();
        paused.forbid();
        final long written = output.getWritten();
        while (output.getWritten() < written + BUFFERS)
        {
            Thread.onSpinWait();
        }
        assertFalse(paused.isViolated());
        backend.resumeOutput();
        final RampStream closed = new RampStream(JavaSoundBackend.FORMAT, -1L);
        backend.play(closed, 1.0);
        final long again = output.getWritten();
        while (output.getWritten() < again + BUFFERS)
        {
            Thread.onSpinWait();
        }

        backend.close();
        closed.forbid();

        assertFalse(backend.isMixing());
        assertFalse(closed.isViolated());
        assertTrue(closed.getCalls() > 0);
    }

    /**
     * Invalid arguments are refused before any line is opened, so that this test needs no device.
     */
    @Test
    void testArguments()
    {
        final InboxSpy inbox = new InboxSpy();
        assertThrows(IllegalArgumentException.class, () -> new JavaSoundBackend(null));
        assertThrows(IllegalArgumentException.class, () -> new JavaSoundBackend(inbox, 0));
    }
}
