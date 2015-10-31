/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Drawable factory. Can create the following elements:
 * <ul>
 * <li>{@link Image}</li>
 * <li>{@link Sprite}</li>
 * <li>{@link SpriteAnimated}</li>
 * <li>{@link SpriteTiled}</li>
 * <li>{@link SpriteParallaxed}</li>
 * <li>{@link SpriteFont}</li>
 * </ul>
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Drawable
{
    /**
     * Load an image from a file.
     * <p>
     * Once created, image must call {@link Image#load()} before any other operations.
     * </p>
     * 
     * @param media The image media.
     * @return The loaded image.
     * @throws LionEngineException If an error occurred when reading the image.
     */
    public static Image loadImage(Media media)
    {
        return new ImageImpl(media);
    }

    /**
     * Load an image from a java image (sharing the same surface). It may be useful in case of multiple images.
     * <p>
     * {@link Image#load()} must not be called as surface has already been loaded.
     * </p>
     * 
     * @param surface The surface reference.
     * @return The loaded image.
     * @throws LionEngineException If the surface is <code>null</code>.
     */
    public static Image loadImage(ImageBuffer surface)
    {
        return new ImageImpl(surface);
    }

    /**
     * Load a sprite from a file.
     * <p>
     * Once created, sprite must call {@link Sprite#load()} before any other operations.
     * </p>
     * 
     * @param media The sprite media.
     * @return The loaded sprite.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    public static Sprite loadSprite(Media media)
    {
        return new SpriteImpl(media);
    }

    /**
     * Load a sprite from a buffered image (sharing the same surface). It may be useful in case of multiple sprites.
     * <p>
     * {@link Sprite#load()} must not be called as surface has already been loaded.
     * </p>
     * 
     * @param surface The surface reference.
     * @return The loaded sprite.
     * @throws LionEngineException If surface is <code>null</code>.
     */
    public static Sprite loadSprite(ImageBuffer surface)
    {
        return new SpriteImpl(surface);
    }

    /**
     * Load an animated sprite from a file, giving horizontal and vertical frames.
     * <p>
     * Once created, sprite must call {@link SpriteAnimated#load()} before any other operations.
     * </p>
     * 
     * @param media The sprite media.
     * @param horizontalFrames The number of horizontal frames.
     * @param verticalFrames The number of vertical frames.
     * @return The loaded animated sprite.
     * @throws LionEngineException If arguments are invalid or image cannot be read.
     */
    public static SpriteAnimated loadSpriteAnimated(Media media, int horizontalFrames, int verticalFrames)
    {
        return new SpriteAnimatedImpl(media, horizontalFrames, verticalFrames);
    }

    /**
     * Load an animated sprite, giving horizontal and vertical frames (sharing the same surface). It may be useful in
     * case of multiple animated sprites.
     * <p>
     * {@link SpriteAnimated#load()} must not be called as surface has already been loaded.
     * </p>
     * 
     * @param surface The surface reference.
     * @param horizontalFrames The number of horizontal frames.
     * @param verticalFrames The number of vertical frames.
     * @return The loaded animated sprite.
     * @throws LionEngineException If arguments are invalid.
     */
    public static SpriteAnimated loadSpriteAnimated(ImageBuffer surface, int horizontalFrames, int verticalFrames)
    {
        return new SpriteAnimatedImpl(surface, horizontalFrames, verticalFrames);
    }

    /**
     * Load a tiled sprite from a file, giving tile dimension.
     * <p>
     * Once created, sprite must call {@link SpriteTiled#load()} before any other operations.
     * </p>
     * 
     * @param media The sprite media.
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @return The loaded tiled sprite.
     * @throws LionEngineException If arguments are invalid or image cannot be read.
     */
    public static SpriteTiled loadSpriteTiled(Media media, int tileWidth, int tileHeight)
    {
        return new SpriteTiledImpl(media, tileWidth, tileHeight);
    }

    /**
     * Load a tiled sprite using an image reference, giving tile dimension (sharing the same surface). It may be
     * useful in case of multiple tiled sprites.
     * <p>
     * {@link SpriteTiled#load()} must not be called as surface has already been loaded.
     * </p>
     * 
     * @param surface The surface reference.
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @return The loaded tiled sprite.
     * @throws LionEngineException If arguments are invalid.
     */
    public static SpriteTiled loadSpriteTiled(ImageBuffer surface, int tileWidth, int tileHeight)
    {
        return new SpriteTiledImpl(surface, tileWidth, tileHeight);
    }

    /**
     * Load a parallaxed sprite, for parallax effect.
     * <p>
     * Once created, sprite must call {@link SpriteParallaxed#load(boolean)} before any other operations.
     * </p>
     * 
     * @param media The sprite media.
     * @param linesNumber The number of parallax lines.
     * @param startWidth The starting width percent (100 is default, 50 is twice smaller, 200 is twice larger).
     * @param startHeight The starting height percent (100 is default, 50 is twice smaller, 200 is twice larger).
     * @return The loaded parallaxed sprite.
     * @throws LionEngineException If arguments are invalid.
     */
    public static SpriteParallaxed loadSpriteParallaxed(Media media, int linesNumber, int startWidth, int startHeight)
    {
        return new SpriteParallaxedImpl(media, linesNumber, startWidth, startHeight);
    }

    /**
     * Load a font based on an image.
     * <p>
     * Once created, sprite must call {@link SpriteFont#load()} before any other operations.
     * </p>
     * 
     * @param media The font sprite media.
     * @param data The font data media.
     * @param letterWidth The font image letter width.
     * @param letterHeight The font image letter height.
     * @return The created font sprite.
     * @throws LionEngineException If an error occurred when creating the font.
     */
    public static SpriteFont loadSpriteFont(Media media, Media data, int letterWidth, int letterHeight)
    {
        return new SpriteFontImpl(media, data, letterWidth, letterHeight);
    }

    /**
     * Private constructor.
     */
    private Drawable()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
