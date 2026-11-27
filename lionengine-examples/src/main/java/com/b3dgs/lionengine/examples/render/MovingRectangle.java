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

import java.util.Arrays;

import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.runtime.Scene;

/**
 * Scene drawing a colored square that crosses the logical frame at a fixed speed (REQ-SPRITE-010, REQ-LOOP-003).
 *
 * <p>
 * It shows the three rules of drawing: create textures through the backend's {@link TextureStore} when the scene
 * enters, and release them when it exits; move in {@link #update(double)} with the fixed step, in logical-frame pixels
 * per second; draw in {@link #render(double, double)} by adding render commands at the position interpolated with
 * {@code alpha}. The render callback of the game clears the {@link CommandBuffer} before rendering the scenes, then
 * presents it through the render backend. The square comes back on the left once it has left the frame on the right.
 * </p>
 *
 * @since 11.0.0
 */
public final class MovingRectangle implements Scene
{
    /** Side of the square in pixels. */
    public static final int SIZE = 16;
    /** Speed of the square in logical-frame pixels per second. */
    public static final double SPEED = 60.0;
    /** Color of the square, packed {@code 0xAARRGGBB}. */
    public static final int COLOR = 0xFF3080FF;

    /** Textures of the render backend. */
    private final TextureStore textures;
    /** Render commands of the frame. */
    private final CommandBuffer commands;
    /** Logical frame width in pixels. */
    private final int width;
    /** Vertical position of the square in pixels. */
    private final float y;
    /** Texture of the square. */
    private int texture;
    /** Horizontal position at the last step, in pixels. */
    private double x;
    /** Horizontal position at the step before, in pixels. */
    private double previousX;

    /**
     * Creates the scene.
     *
     * @param textures The textures of the render backend.
     * @param commands The render commands of the frame, cleared and presented by the render callback.
     * @param width The logical frame width in pixels.
     * @param height The logical frame height in pixels.
     */
    public MovingRectangle(TextureStore textures, CommandBuffer commands, int width, int height)
    {
        this.textures = textures;
        this.commands = commands;
        this.width = width;
        y = (float) ((height - SIZE) / 2.0);
    }

    /**
     * Returns the horizontal position of the square at the last step.
     *
     * @return The position of its left edge in logical-frame pixels.
     */
    public double getX()
    {
        return x;
    }

    @Override
    public void enter()
    {
        final int[] pixels = new int[SIZE * SIZE];
        Arrays.fill(pixels, COLOR);
        texture = textures.create(SIZE, SIZE, pixels);
    }

    @Override
    public void update(double stepSeconds)
    {
        previousX = x;
        x += SPEED * stepSeconds;
        if (x >= width)
        {
            x -= width + SIZE;
            previousX -= width + SIZE;
        }
    }

    @Override
    public void render(double alpha, double unscaledDeltaSeconds)
    {
        commands.add(texture, 0, 0, SIZE, SIZE).position((float) (previousX + (x - previousX) * alpha), y);
    }

    @Override
    public void exit()
    {
        textures.release(texture);
    }

    @Override
    public void dispose()
    {
        // Nothing more to release: the texture is released at exit.
    }
}
