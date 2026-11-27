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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.graphics.Animator;
import com.b3dgs.lionengine.graphics.PngWriter;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.testsupport.RecordedCommand;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link AnimatedSpriteExample} with its simulated clock on the recording backend of {@code test-support}
 * (REQ-DOC-001, REQ-ANIM-003, REQ-SPR-002).
 */
final class AnimatedSpriteExampleTest
{
    /**
     * More frames are drawn than steps run, yet the animation ends exactly where an animator advanced by the same
     * steps alone ends: rendering never feeds time back; the last frame draws the cell of that frame.
     *
     * @throws IOException If the image cannot be written.
     */
    @Test
    void testUpdateStepOnly() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(AnimatedSpriteExample.HERO))
        {
            output.write(PngWriter.rgba(64, 16, new int[64 * 16]).write());
        }
        final RecordingBackend backend = new RecordingBackend();

        final AnimatedSpriteExample.Result result = AnimatedSpriteExample.run(new Resources(memory), backend, new Log());

        final Animator reference = new Animator();
        reference.play(AnimatedSpriteExample.WALK);
        for (long i = 0; i < result.steps(); i++)
        {
            reference.update(LoopConfig.DEFAULT.stepSeconds());
        }
        assertEquals(LoopConfig.DEFAULT.updateRate(), result.steps());
        assertTrue(result.frames() > 2 * result.steps(), result.frames() + " frames");
        assertEquals(reference.getFrameIndex(), result.frameIndex());
        assertEquals(reference.getFrameTime(), result.frameTime());
        final List<RecordedCommand> last = backend.getLastFrame();
        assertEquals(1, last.size());
        assertEquals(reference.getFrame() * AnimatedSpriteExample.CELL, last.get(0).getSourceX());
        assertTrue(last.get(0).getX() <= AnimatedSpriteExample.SPEED * AnimatedSpriteExample.SIMULATED_SECONDS + 1.0F);
    }
}
