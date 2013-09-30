/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.drawable;

import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Media;

/**
 * Handle gfx resources, such as images, sprites, tiles, animations...
 */
public final class Drawable
{
    /**
     * Load an image from a file.
     * 
     * @param media The image media.
     * @return The loaded image.
     */
    public static Image loadImage(Media media)
    {
        return new ImageImpl(media);
    }

    /**
     * Create an image from a buffered image (sharing the same surface). It may be useful in case of multiple images.
     * 
     * @param surface The file name.
     * @return The loaded image.
     */
    public static Image loadImage(BufferedImage surface)
    {
        return new ImageImpl(surface);
    }

    /**
     * Load a sprite from a file.
     * 
     * @param media The sprite media.
     * @return The loaded sprite.
     */
    public static Sprite loadSprite(Media media)
    {
        return new SpriteImpl(media);
    }

    /**
     * Create a sprite from a buffered image (sharing the same surface). It may be useful in case of multiple sprites.
     * 
     * @param surface The surface reference.
     * @return The loaded sprite.
     */
    public static Sprite loadSprite(BufferedImage surface)
    {
        return new SpriteImpl(surface);
    }

    /**
     * Load an animated sprite from a file, giving horizontal and vertical frames.
     * 
     * @param media The sprite media.
     * @param horizontalFrames The number of horizontal frames.
     * @param verticalFrames The number of vertical frames.
     * @return The loaded animated sprite.
     */
    public static SpriteAnimated loadSpriteAnimated(Media media, int horizontalFrames, int verticalFrames)
    {
        return new SpriteAnimatedImpl(media, horizontalFrames, verticalFrames);
    }

    /**
     * Create an animated sprite, giving horizontal and vertical frames (sharing the same surface). It may be useful in
     * case of multiple animated sprites.
     * 
     * @param surface The surface reference.
     * @param horizontalFrames The number of horizontal frames.
     * @param verticalFrames The number of vertical frames.
     * @return The loaded animated sprite.
     */
    public static SpriteAnimated loadSpriteAnimated(BufferedImage surface, int horizontalFrames, int verticalFrames)
    {
        return new SpriteAnimatedImpl(surface, horizontalFrames, verticalFrames);
    }

    /**
     * Load a tiled sprite from a file, giving tile dimension.
     * 
     * @param media The sprite media.
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @return The loaded tiled sprite.
     */
    public static SpriteTiled loadSpriteTiled(Media media, int tileWidth, int tileHeight)
    {
        return new SpriteTiledImpl(media, tileWidth, tileHeight);
    }

    /**
     * Create a tiled sprite using an image reference, giving tile dimension (sharing the same surface). It may be
     * useful in case of multiple tiled sprites.
     * 
     * @param surface The surface reference.
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @return The loaded tiled sprite.
     */
    public static SpriteTiled loadSpriteTiled(BufferedImage surface, int tileWidth, int tileHeight)
    {
        return new SpriteTiledImpl(surface, tileWidth, tileHeight);
    }

    /**
     * Load a parallaxed sprite, for parallax effect.
     * 
     * @param media The sprite media.
     * @param linesNumber The number of parallax lines.
     * @param sx The starting width.
     * @param sy The starting height.
     * @return The loaded parallaxed sprite.
     */
    public static SpriteParallaxed loadSpriteParallaxed(Media media, int linesNumber, int sx, int sy)
    {
        return new SpriteParallaxedImpl(media, linesNumber, sx, sy);
    }

    /**
     * Load a font based on an image.
     * 
     * @param media The font sprite media.
     * @param data The font data media.
     * @param lw The font image letter width.
     * @param lh The font image letter height.
     * @return The created font sprite.
     */
    public static SpriteFont loadSpriteFont(Media media, Media data, int lw, int lh)
    {
        return new SpriteFontImpl(media, data, lw, lh);
    }

    /**
     * Private constructor.
     */
    private Drawable()
    {
        throw new RuntimeException();
    }
}
