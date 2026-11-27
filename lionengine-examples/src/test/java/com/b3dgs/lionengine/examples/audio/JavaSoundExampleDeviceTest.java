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
package com.b3dgs.lionengine.examples.audio;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.javasound.JavaSoundBackend;

/**
 * Runs {@link JavaSoundExample#main()} on the speakers: a test that needs a sound device, outside CI (tag
 * {@code audio-device}, docs/testing.md, REQ-DOC-001, REQ-AUDIO-002).
 */
@Tag("audio-device")
final class JavaSoundExampleDeviceTest
{
    /** Longest run of the jingle. */
    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    /**
     * Skips the test when no Java Sound line can be opened.
     */
    @BeforeAll
    static void requireLine()
    {
        boolean opened;
        try
        {
            new JavaSoundBackend(new InboxSpy()).close();
            opened = true;
        }
        catch (IllegalStateException _)
        {
            opened = false;
        }
        assumeTrue(opened, "No Java Sound line");
    }

    /**
     * The jingle plays to its end and the backend is closed.
     */
    @Test
    void testMain()
    {
        assertTimeoutPreemptively(TIMEOUT, JavaSoundExample::main);
    }
}
