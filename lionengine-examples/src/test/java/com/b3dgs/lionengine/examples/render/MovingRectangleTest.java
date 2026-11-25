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
package com.b3dgs.lionengine.examples.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.runtime.ControllableClock;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.SceneManager;
import com.b3dgs.lionengine.testsupport.HeadlessPlatform;
import com.b3dgs.lionengine.testsupport.RecordedCommand;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Draws {@link MovingRectangle} headless, on the recording backend of {@code test-support} (REQ-DOC-001,
 * REQ-SPRITE-010, REQ-TEST-001).
 *
 * <p>
 * The wiring is the one of a real game: the backend attached to the platform, its texture store given to the scene,
 * and a render callback that clears the command buffer, renders the scenes and presents the buffer. The recording
 * backend keeps each presented frame, so the test reads where the square was drawn.
 * </p>
 */
final class MovingRectangleTest
{
    /** Logical frame width. */
    private static final int WIDTH = 320;
    /** Logical frame height. */
    private static final int HEIGHT = 240;
    /** Duration of one display frame at 60 Hz, in nanoseconds. */
    private static final long FRAME_NANOS = 16_666_667L;

    /**
     * After one second at 60 Hz, the square has moved 60 pixels; it is drawn once per frame, vertically centered.
     */
    @Test
    void testMovement()
    {
        final HeadlessPlatform platform = new HeadlessPlatform(WIDTH,
                                                               HEIGHT,
                                                               new DisplayRequest(ScreenMode.WINDOWED,
                                                                                  null,
                                                                                  ScalingPolicy.FIT,
                                                                                  Collections.emptyList()),
                                                               new Log().getSink());
        final RecordingBackend backend = new RecordingBackend();
        platform.attach(backend);
        final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, 16);
        final MovingRectangle square = new MovingRectangle(backend.getTextureStore(), commands, WIDTH, HEIGHT);
        final SceneManager scenes = new SceneManager();
        scenes.switchTo(square);
        final ControllableClock clock = new ControllableClock();
        final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, clock, new Log(), scenes, (alpha, delta) ->
        {
            commands.clear();
            scenes.render(alpha, delta);
            backend.present(commands);
        });
        platform.start(loop, loop.getInbox());

        platform.iterate();
        for (int frame = 0; frame < 60; frame++)
        {
            clock.advance(FRAME_NANOS);
            platform.iterate();
        }

        assertEquals(61, backend.getFrameCount());
        // The 60 Hz step is rounded to whole nanoseconds: 60 steps move the square slightly more than 60 pixels.
        assertEquals(MovingRectangle.SPEED, square.getX(), 1e-5);
        final List<RecordedCommand> last = backend.getLastFrame();
        assertEquals(1, last.size());
        final RecordedCommand command = last.get(0);
        assertEquals(MovingRectangle.SIZE, command.getSourceWidth());
        assertEquals((HEIGHT - MovingRectangle.SIZE) / 2.0F, command.getY());
        // Each frame lasts exactly one step, so alpha is 0: the square is drawn at the position of the previous step.
        assertEquals(MovingRectangle.SPEED - 1.0, command.getX(), 1e-3);
        assertTrue(backend.isAlive(command.getTexture()));

        scenes.clear();
        assertFalse(backend.isAlive(command.getTexture()));
    }
}
