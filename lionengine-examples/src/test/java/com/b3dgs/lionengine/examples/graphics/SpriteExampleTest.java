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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.graphics.PngWriter;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.testsupport.RecordedCommand;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link SpriteExample} on images written in memory, with the recording backend of {@code test-support}
 * (REQ-DOC-001, REQ-SPRITE-001, REQ-SPRITE-003, REQ-SPRITE-006, REQ-SPRITE-007).
 */
final class SpriteExampleTest
{
    /** Frames run: one second at 60 Hz, plus the first frame, which runs no step. */
    private static final int FRAMES = 61;

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
     * The last frame holds one command per sprite: the sign below at its set position with its top-left pivot, the
     * hero above, from its frame cell, at twice its size about its center, turned by the steps run and drawn at its
     * interpolated position; the images are released when the scene is left.
     *
     * @throws IOException If an image cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        write(memory, SpriteExample.HERO_IMAGE, 64, 32);
        write(memory, SpriteExample.SIGN_IMAGE, 16, 8);
        final RecordingBackend backend = new RecordingBackend();

        final SpriteExample.Result result = SpriteExample.run(new Resources(memory),
                                                              backend,
                                                              new Log(),
                                                              FRAMES);
        final List<RecordedCommand> frame = backend.getLastFrame();
        final RecordedCommand sign = frame.get(0);
        final RecordedCommand hero = frame.get(1);
        final double step = LoopConfig.DEFAULT.stepSeconds();

        assertTrue(result.steps() > 1, () -> "Steps: " + result.steps());
        assertEquals(FRAMES, backend.getFrameCount());
        assertEquals(2, frame.size());
        assertEquals(0, sign.getLayer());
        assertEquals(4.0F, sign.getX());
        assertEquals(4.0F, sign.getY());
        assertEquals(0.0F, sign.getPivotX());
        assertEquals(16, sign.getSourceWidth());
        assertEquals(8, sign.getSourceHeight());
        assertEquals(HeroScene.HERO_LAYER, hero.getLayer());
        assertEquals(16, hero.getSourceX());
        assertEquals(16, hero.getSourceY());
        assertEquals(2.0F, hero.getScaleX());
        assertEquals(2.0F, hero.getScaleY());
        assertEquals(8.0F, hero.getPivotX());
        assertEquals(8.0F, hero.getPivotY());
        assertEquals(HeroScene.TURN * step * result.steps() % 360.0, hero.getRotation(), 1.0E-3);
        assertTrue(hero.getX() >= HeroScene.SPEED * step * (result.steps() - 1) - 1.0E-3, () -> "X: " + hero.getX());
        assertTrue(hero.getX() <= HeroScene.SPEED * step * result.steps() + 1.0E-3, () -> "X: " + hero.getX());
        assertEquals((float) HeroScene.HERO_Y, hero.getY());
        assertNotEquals(sign.getTexture(), hero.getTexture());
        assertEquals(0, result.cachedAtEnd());
        assertFalse(backend.isAlive(sign.getTexture()));
        assertFalse(backend.isAlive(hero.getTexture()));
    }
}
