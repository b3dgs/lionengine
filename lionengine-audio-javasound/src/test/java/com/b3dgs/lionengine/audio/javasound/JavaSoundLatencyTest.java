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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.PcmBuffer;

/**
 * Latency of the {@link JavaSoundBackend} on the default line, from the request to the first sample: a test that needs
 * a sound device, outside CI (tag {@value JavaSoundDevice#TAG}, ADR-0027, ADR-0070, REQ-AUDIO-002).
 *
 * <p>
 * A sound played now starts at the output frame the mixer mixes next; the latency is the time from the play until the
 * line reports that frame played. It covers the line buffer and the mixing, not what the driver and the device add
 * after the frame position the line reports. The median of {@link #PLAYS} plays must stay within the desktop target of
 * 50 ms (ADR-0027); the measured value is in the message of the assertion and in the report of the test.
 * </p>
 */
@Tag(JavaSoundDevice.TAG)
final class JavaSoundLatencyTest
{
    /** Number of plays measured. */
    private static final int PLAYS = 11;
    /** Desktop latency target, in nanoseconds (ADR-0027). */
    private static final long TARGET_NANOS = TimeUnit.MILLISECONDS.toNanos(50L);
    /** Longest wait of one play, in nanoseconds. */
    private static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(2L);
    /** Pause between two measures, in milliseconds. */
    private static final long PAUSE_MILLIS = 37L;

    /**
     * Skips the test without a sound device.
     */
    @BeforeAll
    static void requireLine()
    {
        JavaSoundDevice.requireLine();
    }

    /**
     * Measures one play.
     *
     * @param backend The backend.
     * @param sound The sound.
     * @return The latency in nanoseconds.
     */
    private static long measure(JavaSoundBackend backend, PcmBuffer sound)
    {
        final long start = System.nanoTime();
        final long frame = backend.getMixedFrames();
        backend.play(sound, 1.0, false);
        while (backend.getPlayedFrames() < frame && System.nanoTime() - start < TIMEOUT_NANOS)
        {
            Thread.onSpinWait();
        }
        return System.nanoTime() - start;
    }

    /**
     * The median latency from the play to its first sample played by the line is within 50 ms.
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testLatency() throws InterruptedException
    {
        final JavaSoundBackend backend = new JavaSoundBackend(new InboxSpy());
        final short[] samples = new short[JavaSoundBackend.BUFFER_FRAMES * 2];
        final PcmBuffer sound = new PcmBuffer(JavaSoundBackend.FORMAT, samples);
        final long[] latencies = new long[PLAYS];
        Thread.sleep(PAUSE_MILLIS * 2);
        for (int i = 0; i < PLAYS; i++)
        {
            latencies[i] = measure(backend, sound);
            Thread.sleep(PAUSE_MILLIS);
        }
        backend.close();
        Arrays.sort(latencies);

        final long median = latencies[PLAYS / 2];
        final String report = "Java Sound latency, request to first sample: median "
                              + TimeUnit.NANOSECONDS.toMicros(median) / 1_000.0
                              + " ms, from "
                              + TimeUnit.NANOSECONDS.toMicros(latencies[0]) / 1_000.0
                              + " to "
                              + TimeUnit.NANOSECONDS.toMicros(latencies[PLAYS - 1]) / 1_000.0
                              + " ms";
        System.getLogger(JavaSoundLatencyTest.class.getName()).log(System.Logger.Level.INFO, report);
        assertTrue(median <= TARGET_NANOS, report);
    }
}
