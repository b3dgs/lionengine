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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphics.PngWriter;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.testsupport.RecordedCommand;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link TeamColorExample} on a definition and an image written in memory, on the recording backend of
 * {@code test-support} (REQ-DOC-001, REQ-SPRITE-005).
 */
final class TeamColorExampleTest
{
    /** The soldier definition, as in the example. */
    private static final String SOLDIER = """
        {
          "format": "lionengine.sheet",
          "version": 1,
          "image": "sprites/soldier.png",
          "cellWidth": 16,
          "cellHeight": 16,
          "templates": {
            "red": {"#FF00FF": "#FF0000", "#800080": "#800000"},
            "blue": {"#FF00FF": "#0000FF", "#800080": "#000080"}
          }
        }
        """;
    /** Key of the soldier image. */
    private static final ResourceKey IMAGE = new ResourceKey("sprites/soldier.png");

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
     * Each team draws its own variant of the soldier image, with the magenta cape in the team color and the rest
     * unchanged; everything is released at the end.
     *
     * @throws IOException If a resource cannot be written.
     */
    @Test
    void testTeams() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        final int[] pixels = new int[16 * 16];
        Arrays.fill(pixels, 0xFFF0C090);
        pixels[TeamColorExample.CAPE_X] = 0xFFFF00FF;
        write(memory, IMAGE, PngWriter.rgba(16, 16, pixels).write());
        write(memory, TeamColorExample.SOLDIER, SOLDIER.getBytes(StandardCharsets.UTF_8));
        final RecordingBackend backend = new RecordingBackend();

        final TeamColorExample.Result result = TeamColorExample.run(new Resources(memory), backend);
        final List<RecordedCommand> frame = backend.getLastFrame();

        assertEquals(List.of("red", "blue"), result.teams());
        assertEquals(2, frame.size());
        assertNotEquals(frame.get(0).getTexture(), frame.get(1).getTexture());
        assertEquals(List.of(Integer.valueOf(0xFFFF0000), Integer.valueOf(0xFF0000FF)), result.capes());
        assertEquals(3, result.cachedInScene());
        assertEquals(0, result.cachedAtEnd());
        for (final RecordedCommand command : frame)
        {
            assertFalse(backend.isAlive(command.getTexture()));
        }
    }
}
