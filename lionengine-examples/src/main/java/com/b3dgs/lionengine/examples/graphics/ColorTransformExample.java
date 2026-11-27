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

import com.b3dgs.lionengine.graphics.ColorMatrix;
import com.b3dgs.lionengine.graphics.ColorTransform;
import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.Sprite;
import com.b3dgs.lionengine.render.ColorCapability;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Draws one image four ways with color transforms: as is, as a tinted and faded ghost, as a white hit flash, and
 * turned to gray stone (REQ-SPRITE-004, ADR-0056).
 *
 * <p>
 * A {@link ColorTransform} describes the colors independently of the backend. Its static part, the mask of the hit
 * flash and the grading of the stone, is baked once on the CPU into a variant of the image cached by the
 * {@link ImageCache}, and drawn by a sprite like any image. Its per-frame part, the tint and the alpha of the ghost, is
 * written into the render commands when the backend applies them per frame, as every current backend does; on a
 * backend that does not, {@link ColorTransform#getBakedPart(ColorCapability)} moves them into the baked variant
 * instead, so the same code draws the same colors everywhere. The scene releases its images, variants included.
 * </p>
 *
 * @since 11.0.0
 */
public final class ColorTransformExample
{
    /** Key of the hero image, in the game data: 16 by 16 pixels. */
    public static final ResourceKey HERO = new ResourceKey("sprites/hero.png");
    /** Tint of the ghost: light blue, packed {@code 0xAARRGGBB}. */
    public static final int GHOST_TINT = 0xFF80C0FF;
    /** Alpha of the ghost: half transparent. */
    public static final double GHOST_ALPHA = 0.5;
    /** The ghost: tinted and faded, per frame. */
    public static final ColorTransform GHOST = ColorTransform.NONE.withTint(GHOST_TINT).withAlpha(GHOST_ALPHA);
    /** The hit flash: a white silhouette, baked. */
    public static final ColorTransform FLASH = ColorTransform.NONE.withMask(0xFFFFFF, 1.0);
    /** The stone: gray, baked. */
    public static final ColorTransform STONE = ColorTransform.NONE.withGrading(ColorMatrix.saturation(0.0));
    /** Logical frame width in pixels. */
    public static final int WIDTH = 160;
    /** Logical frame height in pixels. */
    public static final int HEIGHT = 120;

    /** Initial capacity of the command buffer: the four sprites. */
    private static final int CAPACITY = 4;
    /** Distance between two sprites in pixels. */
    private static final double STEP = 24.0;
    /** Vertical position of the sprites in pixels. */
    private static final double ROW = 60.0;

    /**
     * Private constructor.
     */
    private ColorTransformExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Draws a frame of the four sprites and releases the images.
     *
     * @param resources The resources holding {@link #HERO}.
     * @param backend The render backend, which presents the frame and tells which effects it applies per frame.
     * @return The number of cached images during the frame and at the end.
     */
    public static Result run(Resources resources, RenderBackend backend)
    {
        final ImageCache cache = new ImageCache(resources, backend.getTextureStore());
        final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, CAPACITY);
        final Image hero = cache.load(HERO);
        final Image[] images =
        {
            cache.bake(hero, ColorTransform.NONE),
            cache.bake(hero, GHOST.getBakedPart(backend)),
            cache.bake(hero, FLASH.getBakedPart(backend)),
            cache.bake(hero, STONE.getBakedPart(backend))
        };
        final ColorTransform[] transforms =
        {
            ColorTransform.NONE, GHOST, FLASH, STONE
        };

        for (int i = 0; i < images.length; i++)
        {
            final Sprite sprite = new Sprite(images[i]);
            final ColorTransform frame = transforms[i].getFramePart(backend);
            sprite.setTint(frame.getTint());
            sprite.setAlpha(frame.getAlpha());
            sprite.setPosition(STEP * (i + 1), ROW);
            sprite.render(commands);
        }
        backend.present(commands);
        final int cachedInScene = cache.size();

        hero.release();
        for (final Image image : images)
        {
            image.release();
        }
        return new Result(cachedInScene, cache.size());
    }

    /**
     * Result of the example.
     *
     * @param cachedInScene The number of cached images during the frame: the hero and its variants.
     * @param cachedAtEnd The number of cached images once every handle was released.
     * @since 11.0.0
     */
    public record Result(int cachedInScene, int cachedAtEnd)
    {
        // Value type of the example.
    }
}
