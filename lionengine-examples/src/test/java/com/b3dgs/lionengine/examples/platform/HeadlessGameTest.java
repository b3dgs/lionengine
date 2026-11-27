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
package com.b3dgs.lionengine.examples.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.PlatformEvent;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.runtime.ControllableClock;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.LoopState;
import com.b3dgs.lionengine.runtime.Scene;
import com.b3dgs.lionengine.runtime.SceneManager;
import com.b3dgs.lionengine.testsupport.HeadlessPlatform;

/**
 * Tests a game without a window, on the headless platform of {@code test-support} (REQ-TEST-001, REQ-FE-001,
 * REQ-PLATFORM-002, REQ-DOC-001).
 *
 * <p>
 * The game is wired as on a real platform: a scene manager given to the game loop as both callbacks, the loop started
 * by the platform with its inbox. The test then runs one frame at a time, moving a controllable clock by one 60 Hz
 * display frame, and posts the events a real platform would: going to the background, coming back, closing the
 * window. A game copies this structure for its own headless tests, with {@code test-support} in test scope.
 * </p>
 */
final class HeadlessGameTest
{
    /** Logical frame width of the game. */
    private static final int WIDTH = 320;
    /** Logical frame height of the game. */
    private static final int HEIGHT = 240;
    /** Duration of one display frame at 60 Hz, in nanoseconds. */
    private static final long FRAME_NANOS = 16_666_667L;

    /** Clock of the loop, moved by the test. */
    private final ControllableClock clock = new ControllableClock();
    /** Scene of the game. */
    private final Level level = new Level();
    /** Scenes of the game. */
    private final SceneManager scenes = new SceneManager();
    /** Game loop. */
    private final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, clock, new Log(), scenes, scenes);
    /** Platform without window. */
    private final HeadlessPlatform platform = new HeadlessPlatform(WIDTH,
                                                                   HEIGHT,
                                                                   new DisplayRequest(ScreenMode.WINDOWED,
                                                                                      null,
                                                                                      ScalingPolicy.FIT,
                                                                                      Collections.emptyList()),
                                                                   new Log().getSink());

    /**
     * Runs frames of 60 Hz.
     *
     * @param frames The number of frames.
     */
    private void run(int frames)
    {
        for (int i = 0; i < frames; i++)
        {
            clock.advance(FRAME_NANOS);
            platform.iterate();
        }
    }

    /**
     * The game updates while in the foreground, only renders in the background, and stops when the player confirms
     * the close; the level is then exited and disposed.
     */
    @Test
    void testLifecycle()
    {
        scenes.switchTo(level);
        platform.start(loop, loop.getInbox());
        platform.iterate();
        run(60);
        assertEquals(60, level.updates);

        // An event is delivered at the start of the next frame, and the pause it requests applies at the frame
        // boundary after it: one more step runs.
        platform.post(PlatformEvent.Kind.BACKGROUND);
        run(30);
        assertEquals(61, level.updates);
        assertEquals(LoopState.PAUSED, loop.getState());

        // The resume applies one frame after its delivery and discards the time spent in the background: no catch-up.
        platform.post(PlatformEvent.Kind.FOREGROUND);
        run(30);
        assertEquals(89, level.updates);

        final boolean[] asked = new boolean[1];
        platform.addCloseListener(() ->
        {
            final boolean confirmed = asked[0];
            asked[0] = true;
            return confirmed;
        });
        platform.post(PlatformEvent.Kind.CLOSE_REQUESTED);
        run(1);
        assertEquals(LoopState.RUNNING, loop.getState());

        platform.post(PlatformEvent.Kind.CLOSE_REQUESTED);
        run(1);
        assertEquals(LoopState.STOPPED, loop.getState());

        scenes.clear();
        assertTrue(level.disposed);
        assertEquals(123, level.renders);
    }

    /**
     * A resize keeps the logical frame and changes where it is presented: centered at the largest scale that fits.
     */
    @Test
    void testResize()
    {
        platform.start(loop, loop.getInbox());
        platform.resize(1280, 720);
        platform.iterate();

        assertEquals(WIDTH, platform.getLogicalWidth());
        assertEquals(160, platform.getPresentationGeometry().x());
        assertEquals(960, platform.getPresentationGeometry().width());
        assertEquals(3.0, platform.getPresentationGeometry().scaleX());
    }

    /**
     * Level of the game: counts its updates and renders.
     */
    private static final class Level implements Scene
    {
        /** Number of updates. */
        private int updates;
        /** Number of renders. */
        private int renders;
        /** {@code true} once disposed. */
        private boolean disposed;

        /**
         * Creates the level.
         */
        Level()
        {
            super();
        }

        @Override
        public void enter()
        {
            // The level would load its content here.
        }

        @Override
        public void update(double stepSeconds)
        {
            updates++;
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            renders++;
        }

        @Override
        public void exit()
        {
            // The level would stop its sounds here.
        }

        @Override
        public void dispose()
        {
            disposed = true;
        }
    }
}
