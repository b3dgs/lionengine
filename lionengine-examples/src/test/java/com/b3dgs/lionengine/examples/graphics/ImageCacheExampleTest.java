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
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link ImageCacheExample} on an image built in memory (REQ-DOC-001, REQ-SPRITE-002, REQ-SPR-001).
 */
final class ImageCacheExampleTest
{
    /** Pixels of the image: a 2 by 2 image with transparency. */
    private static final int[] PIXELS =
    {
        0xFF102030, 0x80405060, 0x00708090, 0xFFFFFFFF
    };

    /**
     * The two users share one texture, the copy has its own with the mark, and every texture is released at the end.
     *
     * @throws IOException If the image cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(ImageCacheExample.IMAGE))
        {
            output.write(PngWriter.rgba(2, 2, PIXELS).write());
        }
        final RecordingBackend backend = new RecordingBackend();

        final ImageCacheExample.Result result = ImageCacheExample.run(new Resources(memory), backend);

        assertEquals(result.levelTexture(), result.hudTexture());
        assertNotEquals(result.levelTexture(), result.highlightTexture());
        assertEquals(2, result.cachedInUse());
        assertEquals(0, result.cachedAtEnd());
        assertFalse(backend.isAlive(result.levelTexture()));
        assertFalse(backend.isAlive(result.highlightTexture()));
    }
}
