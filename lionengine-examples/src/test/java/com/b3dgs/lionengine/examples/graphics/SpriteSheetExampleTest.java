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

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphics.PngWriter;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link SpriteSheetExample} on images built in memory (REQ-DOC-001, REQ-SHEET-001, REQ-SHEET-002).
 */
final class SpriteSheetExampleTest
{
    /**
     * Writes an image of a size into memory.
     *
     * @param memory The memory provider.
     * @param key The key of the image.
     * @param width The width.
     * @param height The height.
     * @throws IOException If the image cannot be written.
     */
    private static void write(MemoryProvider memory, ResourceKey key, int width, int height) throws IOException
    {
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(PngWriter.rgba(width, height, new int[width * height]).write());
        }
    }

    /**
     * The tileset grid comes from its image, each command draws the source rectangle of its cell, the map keeps the
     * images after the scene released its handle, and everything is released at the end.
     *
     * @throws IOException If an image cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        write(memory, SpriteSheetExample.HERO_IMAGE, 64, 32);
        write(memory, SpriteSheetExample.TILES_IMAGE, 31, 21);
        final RecordingBackend backend = new RecordingBackend();
        final CommandBuffer commands = new CommandBuffer(64, 48, 4);

        final SpriteSheetExample.Result result = SpriteSheetExample.run(new Resources(memory), backend, commands);

        assertEquals(3, result.tileColumns());
        assertEquals(2, result.tileRows());
        assertEquals(2, result.cachedInScene());
        assertEquals(2, result.cachedAfterScene());
        assertEquals(0, result.cachedAtEnd());
        assertEquals(2, commands.size());
        assertEquals(21, commands.getSourceX(0));
        assertEquals(11, commands.getSourceY(0));
        assertEquals(8, commands.getSourceWidth(0));
        assertEquals(16, commands.getSourceX(1));
        assertEquals(16, commands.getSourceY(1));
        assertEquals(16, commands.getSourceHeight(1));
        assertEquals(40.0F, commands.getX(1));
        assertEquals(1, commands.getLayer(1));
        assertNotEquals(commands.getTexture(0), commands.getTexture(1));
        assertFalse(backend.isAlive(commands.getTexture(0)));
        assertFalse(backend.isAlive(commands.getTexture(1)));
    }
}
