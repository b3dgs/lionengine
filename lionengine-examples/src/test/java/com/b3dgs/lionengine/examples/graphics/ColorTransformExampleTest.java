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
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphics.PngWriter;
import com.b3dgs.lionengine.render.ColorEffect;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.testsupport.RecordedCommand;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link ColorTransformExample} on an image written in memory, on the recording backend of {@code test-support}
 * with and without the per-frame color effects (REQ-DOC-001, REQ-SPRITE-004).
 */
final class ColorTransformExampleTest
{
    /**
     * Writes the hero image into memory.
     *
     * @return The resources holding it.
     * @throws IOException If the image cannot be written.
     */
    private static Resources resources() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        final int[] pixels = new int[16 * 16];
        Arrays.fill(pixels, 0xFF204080);
        try (OutputStream output = memory.openWrite(ColorTransformExample.HERO))
        {
            output.write(PngWriter.rgba(16, 16, pixels).write());
        }
        return new Resources(memory);
    }

    /**
     * With tint and alpha applied per frame, the ghost draws the hero texture with the tint and the alpha in its
     * command; the hit flash and the stone are two baked variants; everything is released at the end.
     *
     * @throws IOException If the image cannot be written.
     */
    @Test
    void testPerFrame() throws IOException
    {
        final RecordingBackend backend = new RecordingBackend();

        final ColorTransformExample.Result result = ColorTransformExample.run(resources(), backend);
        final List<RecordedCommand> frame = backend.getLastFrame();

        assertEquals(4, frame.size());
        assertEquals(frame.get(0).getTexture(), frame.get(1).getTexture());
        assertEquals(ColorTransformExample.GHOST_TINT, frame.get(1).getTint());
        assertEquals(0.5F, frame.get(1).getAlpha());
        assertNotEquals(frame.get(0).getTexture(), frame.get(2).getTexture());
        assertNotEquals(frame.get(0).getTexture(), frame.get(3).getTexture());
        assertNotEquals(frame.get(2).getTexture(), frame.get(3).getTexture());
        assertEquals(CommandBuffer.NO_TINT, frame.get(2).getTint());
        assertEquals(3, result.cachedInScene());
        assertEquals(0, result.cachedAtEnd());
        for (final RecordedCommand command : frame)
        {
            assertFalse(backend.isAlive(command.getTexture()));
        }
    }

    /**
     * On a backend applying neither tint nor alpha per frame, the ghost is baked too: its command draws a variant of
     * its own without tint and opaque, and the scene caches one more image.
     *
     * @throws IOException If the image cannot be written.
     */
    @Test
    void testCpuFallback() throws IOException
    {
        final RecordingBackend backend = new RecordingBackend();
        backend.setSupported(ColorEffect.TINT, false);
        backend.setSupported(ColorEffect.ALPHA, false);

        final ColorTransformExample.Result result = ColorTransformExample.run(resources(), backend);
        final List<RecordedCommand> frame = backend.getLastFrame();

        assertNotEquals(frame.get(0).getTexture(), frame.get(1).getTexture());
        assertEquals(CommandBuffer.NO_TINT, frame.get(1).getTint());
        assertEquals(1.0F, frame.get(1).getAlpha());
        assertEquals(4, result.cachedInScene());
        assertEquals(0, result.cachedAtEnd());
    }
}
