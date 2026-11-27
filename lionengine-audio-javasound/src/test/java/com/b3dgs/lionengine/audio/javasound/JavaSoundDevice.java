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

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.b3dgs.lionengine.InboxSpy;

/**
 * Fixtures of the tests that need a sound device (docs/testing.md, ADR-0070).
 *
 * <p>
 * They carry the JUnit tag {@link #TAG}, which every build leaves out, the platform tests of the CI included, since
 * no CI runner has a sound device; the Maven profile {@code audio-device-tests} runs them on a desktop with one. They
 * also skip themselves when no line can be opened.
 * </p>
 */
final class JavaSoundDevice
{
    /** Tag of the tests that need a sound device. */
    static final String TAG = "audio-device";

    /**
     * Skips the calling tests when no Java Sound line can be opened for the output format.
     */
    static void requireLine()
    {
        assumeTrue(LineOutput.isAvailable(JavaSoundBackend.FORMAT), "No Java Sound line");
        boolean opened;
        try
        {
            new JavaSoundBackend(new InboxSpy()).close();
            opened = true;
        }
        catch (final IllegalStateException exception)
        {
            opened = false;
        }
        assumeTrue(opened, "Java Sound line busy");
    }

    /**
     * Private constructor.
     */
    private JavaSoundDevice()
    {
        throw new UnsupportedOperationException();
    }
}
