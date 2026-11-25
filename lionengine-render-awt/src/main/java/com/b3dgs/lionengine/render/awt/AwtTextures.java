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
package com.b3dgs.lionengine.render.awt;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.b3dgs.lionengine.render.TextureStore;

/**
 * Textures of the AWT renderer (ADR-0026, ADR-0047, REQ-SPRITE-010).
 *
 * <p>
 * Each texture keeps the shared pixel array until it is released; its image is created from the array at its first
 * use or at {@link #prepare(int)}, on the runtime thread, which is the render thread. Handles count from {@code 1} and
 * are never reused, so a released handle never draws another texture. Runtime thread only.
 * </p>
 */
final class AwtTextures implements TextureStore
{
    /** Initial number of texture slots. */
    private static final int INITIAL_SLOTS = 16;

    /** Pixels by handle, {@code null} for a free slot. */
    private int[][] pixels = new int[INITIAL_SLOTS][];
    /** Widths by handle. */
    private int[] widths = new int[INITIAL_SLOTS];
    /** Heights by handle. */
    private int[] heights = new int[INITIAL_SLOTS];
    /** Images by handle, {@code null} until uploaded. */
    private BufferedImage[] images = new BufferedImage[INITIAL_SLOTS];
    /** Last handle given. */
    private int last;

    /**
     * Creates an empty store.
     */
    AwtTextures()
    {
        super();
    }

    /**
     * Returns the image of a texture, created from its pixels at the first call.
     *
     * @param texture The texture handle.
     * @return The image, {@code null} if the texture is not alive.
     */
    BufferedImage getImage(int texture)
    {
        if (!isAlive(texture))
        {
            return null;
        }
        if (images[texture] == null)
        {
            final int width = widths[texture];
            final int height = heights[texture];
            final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            image.getRaster().setDataElements(0, 0, width, height, pixels[texture]);
            images[texture] = image;
        }
        return images[texture];
    }

    /**
     * Tells whether a texture is created and not released.
     *
     * @param texture The texture handle.
     * @return {@code true} if the texture is alive.
     */
    boolean isAlive(int texture)
    {
        return texture > 0 && texture <= last && pixels[texture] != null;
    }

    @Override
    public int create(int width, int height, int[] argb)
    {
        if (width <= 0 || height <= 0 || argb == null || argb.length < (long) width * height)
        {
            throw new IllegalArgumentException("Invalid texture: " + width + 'x' + height);
        }
        last++;
        if (last == pixels.length)
        {
            final int slots = pixels.length * 2;
            pixels = Arrays.copyOf(pixels, slots);
            widths = Arrays.copyOf(widths, slots);
            heights = Arrays.copyOf(heights, slots);
            images = Arrays.copyOf(images, slots);
        }
        pixels[last] = argb;
        widths[last] = width;
        heights[last] = height;
        return last;
    }

    @Override
    public void prepare(int texture)
    {
        if (getImage(texture) == null)
        {
            throw new IllegalArgumentException("Texture not alive: " + texture);
        }
    }

    @Override
    public void release(int texture)
    {
        if (isAlive(texture))
        {
            pixels[texture] = null;
            images[texture] = null;
        }
    }
}
