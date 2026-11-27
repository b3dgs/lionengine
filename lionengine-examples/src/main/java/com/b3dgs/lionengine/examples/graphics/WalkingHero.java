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

import com.b3dgs.lionengine.graphics.AnimatedSprite;
import com.b3dgs.lionengine.graphics.Animation;
import com.b3dgs.lionengine.graphics.AnimationLoop;
import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.graphics.Sprite;
import com.b3dgs.lionengine.graphics.SpriteSheet;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.runtime.RenderCallback;
import com.b3dgs.lionengine.runtime.UpdateCallback;

/**
 * The game of {@link AnimatedSpriteExample}: a hero that walks in the update step and is drawn in the render phase
 * (REQ-ANIM-003, REQ-LOOP-006).
 */
final class WalkingHero implements UpdateCallback, RenderCallback
{
    /** The walk: the four cells, 0.1 second each, looping. */
    static final Animation WALK = new Animation(new int[]
    {
        0, 1, 2, 3
    }, 0.1, AnimationLoop.LOOP);
    /** Walking speed in pixels per second. */
    static final double SPEED = 60.0;

    /** Vertical position of the hero in pixels. */
    private static final double ROW = 60.0;

    /** The walking hero. */
    private final AnimatedSprite hero;
    /** The render backend. */
    private final RenderBackend backend;
    /** The commands of a frame. */
    private final CommandBuffer commands = new CommandBuffer(AnimatedSpriteExample.WIDTH,
                                                             AnimatedSpriteExample.HEIGHT,
                                                             1);
    /** Horizontal position after the last step, in pixels. */
    private double x;
    /** Horizontal position before the last step, in pixels. */
    private double previousX;
    /** Number of steps. */
    private long steps;
    /** Number of frames drawn. */
    private int frames;

    /**
     * Creates the game, the hero walking from the first step.
     *
     * @param sheet The sheet of the hero, its cells the frames of the walk.
     * @param image The image the frames are drawn from: the image of the sheet, or a variant of it.
     * @param backend The render backend.
     */
    WalkingHero(SpriteSheet sheet, Image image, RenderBackend backend)
    {
        super();

        hero = new AnimatedSprite(new Sprite(sheet, image));
        this.backend = backend;
        hero.play(WALK);
    }

    /**
     * Returns the hero.
     *
     * @return The hero.
     */
    AnimatedSprite getHero()
    {
        return hero;
    }

    /**
     * Returns the number of steps.
     *
     * @return The number of update steps run.
     */
    long getSteps()
    {
        return steps;
    }

    /**
     * Returns the number of frames.
     *
     * @return The number of frames drawn.
     */
    int getFrames()
    {
        return frames;
    }

    @Override
    public void update(double stepSeconds)
    {
        previousX = x;
        x += SPEED * stepSeconds;
        hero.getSprite().setPosition(x, ROW);
        hero.update(stepSeconds);
        steps++;
    }

    @Override
    public void render(double alpha, double unscaledDeltaSeconds)
    {
        commands.clear();
        hero.render(commands, previousX + (x - previousX) * alpha, ROW);
        backend.present(commands);
        frames++;
    }
}
