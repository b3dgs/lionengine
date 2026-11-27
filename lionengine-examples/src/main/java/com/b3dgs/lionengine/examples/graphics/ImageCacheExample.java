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

import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.PixelBuffer;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Shares one decoded image between two users through the image cache, adds a modified copy as a derived image, and
 * releases everything (REQ-SPRITE-002, REQ-SPR-001, REQ-RES-004, REQ-RT-005).
 *
 * <p>
 * The game creates one {@link ImageCache} with its {@link Resources} and the texture store of its render backend, and
 * gives it to its scenes. Each user loads the image it needs and releases it on exit: here the level and its heads-up
 * display both load the hero image, which is decoded once and drawn with one texture. A modified copy of the pixels
 * becomes a derived image under its own discriminator, with its own texture. The image leaves the cache, and its
 * texture is released, when its last handle is released, so {@link ImageCache#size()} tells whether a scene released
 * everything it acquired. The cache counts the memory of its pixels, 4 bytes each, and this one has an opt-in limit:
 * an image that would exceed it is refused instead of being cached (ADR-0055). The cache belongs to the thread of its
 * first operation, the runtime thread of the game; another thread is refused.
 * </p>
 *
 * @since 11.0.0
 */
public final class ImageCacheExample
{
    /** Key of the image, in the game data. */
    public static final ResourceKey IMAGE = new ResourceKey("sprites/hero.png");
    /** Discriminator of the highlighted copy. */
    public static final String HIGHLIGHT = "highlight";
    /** Color written on the top-left pixel of the highlighted copy: opaque yellow. */
    public static final int MARK = 0xFFFFFF00;
    /** Limit of the cached pixels in bytes: 4 MiB, one 1024 by 1024 image. */
    public static final long LIMIT_BYTES = 4L * 1024L * 1024L;

    /**
     * Private constructor.
     */
    private ImageCacheExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads {@link #IMAGE} for two users, adds a highlighted copy, then releases every handle.
     *
     * @param resources The resources holding {@link #IMAGE}.
     * @param textures The texture store of the render backend.
     * @return The textures used, the number of cached images while in use and at the end, and the memory used.
     */
    public static Result run(Resources resources, TextureStore textures)
    {
        final ImageCache cache = new ImageCache(resources, textures, LIMIT_BYTES);

        final Image level = cache.load(IMAGE);
        final Image hud = cache.load(IMAGE);

        final PixelBuffer copy = level.copyToBuffer();
        copy.setPixel(0, 0, MARK);
        final Image highlight = cache.add(IMAGE, HIGHLIGHT, copy);

        final int levelTexture = level.getTexture();
        final int hudTexture = hud.getTexture();
        final int highlightTexture = highlight.getTexture();
        final int cachedInUse = cache.size();
        final long bytesInUse = cache.getUsedBytes();

        level.release();
        hud.release();
        highlight.release();
        return new Result(levelTexture, hudTexture, highlightTexture, cachedInUse, cache.size(), bytesInUse);
    }

    /**
     * Result of the example.
     *
     * @param levelTexture The texture of the image loaded by the level.
     * @param hudTexture The texture of the image loaded by the heads-up display, the same one.
     * @param highlightTexture The texture of the highlighted copy.
     * @param cachedInUse The number of cached images while they were in use.
     * @param cachedAtEnd The number of cached images once every handle was released.
     * @param bytesInUse The memory of the cached pixels while they were in use, in bytes.
     * @since 11.0.0
     */
    public record Result(int levelTexture,
                         int hudTexture,
                         int highlightTexture,
                         int cachedInUse,
                         int cachedAtEnd,
                         long bytesInUse)
    {
        // Value type of the example.
    }
}
