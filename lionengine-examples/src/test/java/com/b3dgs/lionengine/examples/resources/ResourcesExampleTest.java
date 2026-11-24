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
package com.b3dgs.lionengine.examples.resources;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.resources.ResourceException;

/**
 * Runs {@link ResourcesExample} (REQ-DOC-001, REQ-RES-001, REQ-RES-006).
 */
final class ResourcesExampleTest
{
    /** Game data directory of each test. */
    @TempDir
    File directory;

    /**
     * Writes the greeting file of the game data directory.
     *
     * @throws IOException If the data file cannot be written.
     */
    private void writeData() throws IOException
    {
        final File text = new File(directory, "text");
        assertTrue(text.mkdir());
        Files.writeString(new File(text, "greeting.txt").toPath(), "Hello from the files", StandardCharsets.UTF_8);
    }

    /**
     * The greeting comes from the file, then from memory once overridden; the absent key is missing.
     *
     * @throws IOException If the data file cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        writeData();

        assertEquals(new ResourcesExample.Result("Hello from the files",
                                                 ResourcesExample.OVERRIDE,
                                                 ResourceException.Reason.MISSING),
                     ResourcesExample.run(directory, new Log()));
    }

    /**
     * The example runs on a data directory, and prints its usage without one.
     *
     * @throws IOException If the data file cannot be written.
     */
    @Test
    void testMain() throws IOException
    {
        writeData();

        assertDoesNotThrow(() -> ResourcesExample.main(new String[]
        {
            directory.getPath()
        }));
        assertDoesNotThrow(() -> ResourcesExample.main(new String[0]));
    }
}
