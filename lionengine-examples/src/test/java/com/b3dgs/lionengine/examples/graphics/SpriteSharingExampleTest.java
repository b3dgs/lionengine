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
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphics.PngWriter;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.testsupport.RecordedCommand;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link SpriteSharingExample} on an image written in memory, with the recording backend of
 * {@code test-support} (REQ-DOC-001, REQ-SPRITE-002, REQ-SPR-001).
 */
final class SpriteSharingExampleTest
{
    /**
     * The coins and the display icon draw one texture, the gold coin its own; the display keeps drawing after the level
     * left, and every image and texture is released at the end.
     *
     * @throws IOException If the image cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(SpriteSharingExample.COIN))
        {
            output.write(PngWriter.rgba(8, 8, new int[8 * 8]).write());
        }
        final RecordingBackend backend = new RecordingBackend();

        final SpriteSharingExample.Result result = SpriteSharingExample.run(new Resources(memory), backend);
        final List<RecordedCommand> level = backend.getFrame(0);
        final List<RecordedCommand> display = backend.getFrame(1);
        final int coin = level.get(0).getTexture();
        final RecordedCommand gold = level.get(SpriteSharingExample.COINS);
        final RecordedCommand icon = level.get(SpriteSharingExample.COINS + 1);

        assertEquals(SpriteSharingExample.COINS + 2, level.size());
        for (int i = 0; i < SpriteSharingExample.COINS; i++)
        {
            assertEquals(coin, level.get(i).getTexture());
            assertEquals(i * 8.0F, level.get(i).getX());
        }
        assertNotEquals(coin, gold.getTexture());
        assertEquals(coin, icon.getTexture());
        assertEquals(SpriteSharingExample.DISPLAY_LAYER, icon.getLayer());
        assertEquals(1, display.size());
        assertEquals(coin, display.get(0).getTexture());
        assertEquals(2, result.cachedInLevel());
        assertEquals(2L * 8L * 8L * 4L, result.bytesInLevel());
        assertEquals(1, result.cachedAfterLevel());
        assertEquals(0, result.cachedAtEnd());
        assertFalse(backend.isAlive(coin));
        assertFalse(backend.isAlive(gold.getTexture()));
    }
}
