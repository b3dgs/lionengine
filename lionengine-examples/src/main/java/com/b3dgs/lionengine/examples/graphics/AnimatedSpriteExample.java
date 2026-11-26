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

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.graphics.AnimatedSprite;
import com.b3dgs.lionengine.graphics.Animation;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.SheetGrid;
import com.b3dgs.lionengine.graphics.SheetGroup;
import com.b3dgs.lionengine.graphics.SpriteSheet;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.runtime.Clock;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;

/**
 * A hero walking across the screen in the game loop: the animation advances in the update step only, the render
 * phase draws the current frame at a position interpolated between two steps (REQ-ANIM-003, REQ-LOOP-006).
 *
 * <p>
 * The update step of the game moves the hero and calls {@link AnimatedSprite#update(double)} with the constant step
 * of the loop; the render phase, run more often than the update here (a 144 Hz display for 60 steps per second), only
 * reads: it draws the frame of the last step with {@link AnimatedSprite#render(CommandBuffer, double, double)} at a
 * position interpolated with the {@code alpha} of the loop. So the animation progresses exactly as the number of steps
 * says, whatever the display rate. Without a platform, the example drives the loop with a simulated clock, as
 * {@code GameLoopExample} does, so it runs instantly and always gives the same result.
 * </p>
 *
 * @since 11.0.0
 */
public final class AnimatedSpriteExample
{
    /** Key of the hero image: 64 by 16 pixels, four cells of 16 pixels. */
    public static final ResourceKey HERO = new ResourceKey("sprites/hero.png");
    /** The walk: the four cells, 0.1 second each, looping. */
    public static final Animation WALK = WalkingHero.WALK;
    /** Walking speed in pixels per second. */
    public static final double SPEED = WalkingHero.SPEED;
    /** Simulated time run by the example, in seconds. */
    public static final double SIMULATED_SECONDS = 1.0;
    /** Size of a cell of the hero image, in pixels. */
    public static final int CELL = 16;
    /** Logical frame width in pixels. */
    public static final int WIDTH = 160;
    /** Logical frame height in pixels. */
    public static final int HEIGHT = 120;

    /** Duration of one display frame at 144 Hz, in nanoseconds. */
    private static final long FRAME_NANOS = 6_944_444L;
    /** Id of the hero sheet. */
    private static final ResourceKey SHEET = new ResourceKey("sheets/hero");

    /**
     * Private constructor.
     */
    private AnimatedSpriteExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the loop until {@link #SIMULATED_SECONDS} have passed, then releases the sheet.
     *
     * @param resources The resources holding {@link #HERO}.
     * @param backend The render backend, which presents each frame.
     * @param log The log given to the loop.
     * @return The number of steps and frames, and where the animation ended.
     */
    public static Result run(Resources resources, RenderBackend backend, Log log)
    {
        final SheetGroup group = new SheetGroup(new ImageCache(resources, backend.getTextureStore()));
        final SpriteSheet sheet = group.add(SHEET, HERO, new SheetGrid(CELL, CELL));
        final WalkingHero game = new WalkingHero(sheet, sheet.getImage(), backend);
        final long[] now =
        {
            0L
        };
        final Clock clock = () -> now[0];
        final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, clock, log, game, game);
        final long steps = Math.round(SIMULATED_SECONDS * LoopConfig.DEFAULT.updateRate());

        loop.iterate();
        while (game.getSteps() < steps)
        {
            now[0] += FRAME_NANOS;
            loop.iterate();
        }
        loop.stop();
        loop.iterate();
        group.release();
        final AnimatedSprite hero = game.getHero();
        return new Result(game.getSteps(), game.getFrames(), hero.getFrameIndex(), hero.getFrameTime());
    }

    /**
     * Result of the example.
     *
     * @param steps The number of update steps.
     * @param frames The number of frames drawn, more than the steps.
     * @param frameIndex The frame of the walk after the last step.
     * @param frameTime The time spent in that frame, in seconds.
     * @since 11.0.0
     */
    public record Result(long steps, int frames, int frameIndex, double frameTime)
    {
        // Value type of the example.
    }
}
