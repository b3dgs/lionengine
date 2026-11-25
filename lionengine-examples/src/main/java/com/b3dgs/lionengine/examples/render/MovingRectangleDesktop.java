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

import java.util.Collections;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.platform.awt.AwtPlatform;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.render.awt.AwtRenderBackend;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.SceneManager;

/**
 * Shows {@link MovingRectangle} in a desktop window, with the AWT platform and renderer (REQ-PLATFORM-001, REQ-FE-003,
 * REQ-DOC-001).
 *
 * <p>
 * The wiring is the one of {@code MovingRectangleTest}, with the desktop modules instead of the headless ones: the
 * {@link AwtPlatform} owns the window and runs the loop on its own thread, and the {@link AwtRenderBackend} presents
 * the logical frame of 320 by 240 pixels, scaled to the window with {@code FIT} scaling. Resize the window to see the
 * scaling; close it to stop the game.
 * </p>
 *
 * @since 11.0.0
 */
public final class MovingRectangleDesktop
{
    /** Logical frame width in pixels. */
    public static final int WIDTH = 320;
    /** Logical frame height in pixels. */
    public static final int HEIGHT = 240;

    /** Initial capacity of the command buffer. */
    private static final int COMMANDS = 16;

    /**
     * Private constructor.
     */
    private MovingRectangleDesktop()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates the game: a platform with its renderer attached, the scene and the loop, ready to run.
     *
     * @param log The log of the game.
     * @return The game.
     * @throws IllegalStateException If the environment has no display.
     */
    public static Game create(Log log)
    {
        final AwtPlatform platform = new AwtPlatform(WIDTH,
                                                     HEIGHT,
                                                     new DisplayRequest(ScreenMode.WINDOWED,
                                                                        null,
                                                                        ScalingPolicy.FIT,
                                                                        Collections.emptyList()),
                                                     log.getSink());
        final AwtRenderBackend backend = new AwtRenderBackend(platform);
        platform.attach(backend);
        return Game.wire(platform, backend, log);
    }

    /**
     * Opens the window and runs the game until the window is closed.
     *
     * @param args Unused.
     */
    // CHECKSTYLE IGNORE LINE: UncommentedMain
    public static void main(String[] args)
    {
        create(new Log()).run();
    }

    /**
     * The game of the example, ready to run.
     *
     * @param platform The desktop platform, with the renderer attached.
     * @param loop The game loop.
     * @param scenes The scenes, the square being active once the loop has started.
     * @param square The scene of the square.
     * @since 11.0.0
     */
    public record Game(AwtPlatform platform, GameLoop loop, SceneManager scenes, MovingRectangle square)
    {
        /**
         * Wires the game on a platform and its renderer, as {@code MovingRectangleTest} does on the headless ones: the
         * scene draws into a command buffer that the render callback clears, fills and presents each frame.
         *
         * @param platform The desktop platform.
         * @param backend The renderer attached to the platform.
         * @param log The log of the game.
         * @return The game.
         */
        private static Game wire(AwtPlatform platform, AwtRenderBackend backend, Log log)
        {
            final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, COMMANDS);
            final MovingRectangle square = new MovingRectangle(backend.getTextureStore(), commands, WIDTH, HEIGHT);
            final SceneManager scenes = new SceneManager();
            scenes.switchTo(square);
            final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, log, scenes, (alpha, delta) ->
            {
                commands.clear();
                scenes.render(alpha, delta);
                backend.present(commands);
            });
            return new Game(platform, loop, scenes, square);
        }

        /**
         * Opens the window, runs the loop until the window is closed, then exits the scene, which releases its
         * texture. The scene is cleared on the calling thread once the runtime thread has ended.
         */
        public void run()
        {
            platform.start(loop, loop.getInbox());
            scenes.clear();
        }
    }
}
