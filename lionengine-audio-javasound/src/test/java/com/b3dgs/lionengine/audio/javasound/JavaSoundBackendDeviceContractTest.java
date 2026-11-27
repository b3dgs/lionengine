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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.AudioBackendContract;

/**
 * Binding of the {@link AudioBackendContract} suite on the {@link JavaSoundBackend} with the default line: a test that
 * needs a sound device, outside CI (tag {@value JavaSoundDevice#TAG}, ADR-0045, ADR-0070, REQ-AUDIO-002).
 *
 * <p>
 * The clock is the device: the suite waits for the time of the frames, plus a margin covering the line buffer and the
 * mixing of the next buffer.
 * </p>
 */
@Tag(JavaSoundDevice.TAG)
final class JavaSoundBackendDeviceContractTest extends AudioBackendContract
{
    /** Margin added to the time of the frames, in nanoseconds: the line buffer, a buffer and the scheduling. */
    private static final long MARGIN_NANOS = TimeUnit.MILLISECONDS.toNanos(150L);

    /**
     * Skips the suite without a sound device.
     */
    @BeforeAll
    static void requireLine()
    {
        JavaSoundDevice.requireLine();
    }

    @Override
    protected AudioBackend createBackend(InboxSpy inbox)
    {
        return new JavaSoundBackend(inbox);
    }

    @Override
    protected void advance(AudioBackend backend, int frames)
    {
        final long deadline = System.nanoTime()
                              + TimeUnit.SECONDS.toNanos(frames) / JavaSoundBackend.SAMPLE_RATE
                              + MARGIN_NANOS;
        while (System.nanoTime() < deadline)
        {
            LockSupport.parkNanos(deadline - System.nanoTime());
        }
    }
}
