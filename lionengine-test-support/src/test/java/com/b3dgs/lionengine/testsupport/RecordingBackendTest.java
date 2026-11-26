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
package com.b3dgs.lionengine.testsupport;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.render.ColorEffect;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.render.RenderBackendContract;
import com.b3dgs.lionengine.render.Viewport;

/**
 * Test {@link RecordingBackend} against the {@link RenderBackend} contract of the {@code render-api} test JAR, and its
 * records (REQ-TEST-001, REQ-SPRITE-010).
 */
final class RecordingBackendTest extends RenderBackendContract
{
    @Override
    protected RenderBackend createBackend()
    {
        return new RecordingBackend();
    }

    /**
     * Each presentation records a frame with the commands in presentation order and every value.
     */
    @Test
    void testFrames()
    {
        final RecordingBackend backend = new RecordingBackend();
        final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, 1);
        assertThrows(IllegalStateException.class, backend::getLastFrame);

        commands.add(1, 0, 0, 8, 8).layer(1);
        commands.setViewport(new Viewport(10, 20, 30, 40));
        commands.add(2, 1, 2, 3, 4)
                .position(5.0F, 6.0F)
                .scale(-1.0F, 2.0F)
                .rotation(90.0F)
                .pivot(1.5F, 2.5F)
                .tint(0x80FF00FF)
                .alpha(0.5F);
        backend.present(commands);
        commands.clear();
        backend.present(commands);

        assertEquals(2, backend.getFrameCount());
        assertTrue(backend.getLastFrame().isEmpty());
        final List<RecordedCommand> frame = backend.getFrame(0);
        assertEquals(2, frame.size());
        assertEquals(1, frame.get(1).getTexture());
        assertEquals(new Viewport(0, 0, WIDTH, HEIGHT), frame.get(1).getViewport());

        final RecordedCommand command = frame.get(0);
        assertEquals(2, command.getTexture());
        assertEquals(1, command.getSourceX());
        assertEquals(2, command.getSourceY());
        assertEquals(3, command.getSourceWidth());
        assertEquals(4, command.getSourceHeight());
        assertEquals(5.0F, command.getX());
        assertEquals(6.0F, command.getY());
        assertEquals(-1.0F, command.getScaleX());
        assertEquals(2.0F, command.getScaleY());
        assertEquals(90.0F, command.getRotation());
        assertEquals(1.5F, command.getPivotX());
        assertEquals(2.5F, command.getPivotY());
        assertEquals(0x80FF00FF, command.getTint());
        assertEquals(0.5F, command.getAlpha());
        assertEquals(0, command.getLayer());
        assertEquals(new Viewport(10, 20, 30, 40), command.getViewport());
        assertThrows(UnsupportedOperationException.class, () -> frame.add(command));
    }

    /**
     * Textures keep their pixels and size until released; preparation is recorded.
     */
    @Test
    void testTextureRecords()
    {
        final RecordingBackend backend = new RecordingBackend();
        final int[] pixels =
        {
            1, 2, 3, 4, 5, 6
        };
        final int texture = backend.create(3, 2, pixels);

        assertTrue(backend.isAlive(texture));
        assertFalse(backend.isPrepared(texture));
        assertArrayEquals(pixels, backend.getPixels(texture));
        assertEquals(3, backend.getTextureWidth(texture));
        assertEquals(2, backend.getTextureHeight(texture));

        backend.prepare(texture);
        assertTrue(backend.isPrepared(texture));

        backend.release(texture);
        assertFalse(backend.isAlive(texture));
        assertFalse(backend.isPrepared(texture));
        assertNull(backend.getPixels(texture));
        assertThrows(IllegalArgumentException.class, () -> backend.getTextureWidth(texture));
    }

    /**
     * The test chooses the filters implemented besides {@code NONE}.
     */
    @Test
    void testSupportedFilters()
    {
        final RecordingBackend backend = new RecordingBackend(OutputFilter.BILINEAR);

        assertTrue(backend.isSupported(OutputFilter.NONE));
        assertTrue(backend.isSupported(OutputFilter.BILINEAR));
        assertFalse(backend.isSupported(OutputFilter.CRT));
        assertThrows(IllegalArgumentException.class, () -> new RecordingBackend((OutputFilter[]) null));
        assertThrows(IllegalArgumentException.class, () -> new RecordingBackend(OutputFilter.CRT, null));
    }

    /**
     * Every color effect is applied per frame until the test switches it off, and it can be switched on again; the
     * commands are recorded as written either way (ADR-0056).
     */
    @Test
    void testColorEffects()
    {
        final RecordingBackend backend = new RecordingBackend();
        final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, 1);

        assertTrue(backend.isSupported(ColorEffect.TINT));
        assertTrue(backend.isSupported(ColorEffect.ALPHA));

        backend.setSupported(ColorEffect.TINT, false);
        commands.add(1, 0, 0, 1, 1).tint(0xFF00FF00).alpha(0.5F);
        backend.present(commands);

        assertFalse(backend.isSupported(ColorEffect.TINT));
        assertTrue(backend.isSupported(ColorEffect.ALPHA));
        assertEquals(0xFF00FF00, backend.getLastFrame().get(0).getTint());
        assertEquals(0.5F, backend.getLastFrame().get(0).getAlpha());
        backend.setSupported(ColorEffect.TINT, true);
        assertTrue(backend.isSupported(ColorEffect.TINT));
        assertThrows(IllegalArgumentException.class, () -> backend.setSupported(null, false));
    }
}
