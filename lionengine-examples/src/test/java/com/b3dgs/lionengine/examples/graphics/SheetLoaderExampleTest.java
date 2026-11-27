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
package com.b3dgs.lionengine.examples.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphics.PngWriter;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link SheetLoaderExample} on definitions and an image written in memory (REQ-DOC-001, REQ-SHEET-003).
 */
final class SheetLoaderExampleTest
{
    /** The hero definition, as in the example. */
    private static final String HERO = """
        {
          "format": "lionengine.sheet",
          "version": 1,
          "image": "sprites/hero.png",
          "cellWidth": 16,
          "cellHeight": 16,
          "pivotY": 15,
          "animations": {
            "walk": {"frames": [0, 1, 2, 3], "frameDuration": 0.1, "loop": "loop"}
          },
          "templates": {
            "red": {"#FF00FF": "#FF0000"}
          }
        }
        """;
    /** A definition whose walk animation names a cell outside the sheet. */
    private static final String BROKEN = """
        {
          "format": "lionengine.sheet",
          "version": 1,
          "image": "sprites/hero.png",
          "cellWidth": 16,
          "cellHeight": 16,
          "animations": {
            "walk": {"frames": [0, 1, 2, 30], "frameDuration": 0.1, "loop": "loop"}
          }
        }
        """;

    /**
     * Writes bytes into memory.
     *
     * @param memory The memory provider.
     * @param key The key.
     * @param bytes The bytes.
     * @throws IOException If the bytes cannot be written.
     */
    private static void write(MemoryProvider memory, ResourceKey key, byte[] bytes) throws IOException
    {
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(bytes);
        }
    }

    /**
     * The hero sheet is read with its grid and pivot; the broken definition is reported with its file, line, column
     * and path, and is not added.
     *
     * @throws IOException If a resource cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        write(memory, new ResourceKey("sprites/hero.png"), PngWriter.rgba(64, 32, new int[64 * 32]).write());
        write(memory, SheetLoaderExample.HERO, HERO.getBytes(StandardCharsets.UTF_8));
        write(memory, SheetLoaderExample.BROKEN, BROKEN.getBytes(StandardCharsets.UTF_8));

        final SheetLoaderExample.Result result = SheetLoaderExample.run(new Resources(memory), new RecordingBackend());

        assertEquals(4, result.columns());
        assertEquals(2, result.rows());
        assertEquals(15.0, result.pivotY());
        assertEquals(16, result.frameY());
        assertEquals(1, result.sheets());
        assertEquals("Frame outside the sheet of 8 cells: 30 (sheets/broken.json:8:34, $.animations.walk.frames[3], "
                     + "frames)",
                     result.error().getMessage());
    }
}
