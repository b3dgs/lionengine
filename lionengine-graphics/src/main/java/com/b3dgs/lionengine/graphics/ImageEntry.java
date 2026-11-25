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
package com.b3dgs.lionengine.graphics;

import com.b3dgs.lionengine.render.TextureStore;

/**
 * Image held by an {@link ImageCache}: its immutable pixels, its texture and its reference count, shared by every
 * {@link Image} handle acquired on it (ADR-0019, ADR-0026, ADR-0047).
 *
 * <p>
 * The pixels are never modified after creation; the texture store shares them read-only. The texture is created with
 * the entry and released with its last reference, when the entry leaves the cache. Runtime thread only.
 * </p>
 */
final class ImageEntry
{
    /** The cache holding the entry. */
    private final ImageCache cache;
    /** The texture store. */
    private final TextureStore textures;
    /** The key of the entry in the cache. */
    private final ImageKey key;
    /** Width in pixels. */
    private final int width;
    /** Height in pixels. */
    private final int height;
    /** The texture of the pixels. */
    private final int texture;
    /** Pixels, packed {@code 0xAARRGGBB}, row-major; {@code null} once the entry left the cache. */
    private int[] argb;
    /** Number of handles not released. */
    private int references;

    /**
     * Creates the entry and its texture.
     *
     * @param cache The cache holding the entry.
     * @param key The key of the entry.
     * @param width The width in pixels.
     * @param height The height in pixels.
     * @param argb The pixels, owned by the entry from now on and never modified.
     */
    ImageEntry(ImageCache cache, ImageKey key, int width, int height, int[] argb)
    {
        super();

        this.cache = cache;
        textures = cache.getTextures();
        this.key = key;
        this.width = width;
        this.height = height;
        this.argb = argb;
        texture = textures.create(width, height, argb);
    }

    /**
     * Acquires a new handle on the entry.
     *
     * @return The handle, one more reference.
     */
    Image acquire()
    {
        references++;
        return new Image(this);
    }

    /**
     * Releases one reference; the last one releases the texture and removes the entry from the cache.
     */
    void release()
    {
        references--;
        if (references == 0)
        {
            textures.release(texture);
            argb = null;
            cache.remove(key);
        }
    }

    /**
     * Returns the key of the entry.
     *
     * @return The key.
     */
    ImageKey getKey()
    {
        return key;
    }

    /**
     * Returns the width.
     *
     * @return The width in pixels.
     */
    int getWidth()
    {
        return width;
    }

    /**
     * Returns the height.
     *
     * @return The height in pixels.
     */
    int getHeight()
    {
        return height;
    }

    /**
     * Returns the texture.
     *
     * @return The texture handle.
     */
    int getTexture()
    {
        return texture;
    }

    /**
     * Uploads the texture ahead of its first use.
     */
    void prepare()
    {
        textures.prepare(texture);
    }

    /**
     * Returns a pixel.
     *
     * @param x The column.
     * @param y The row.
     * @return The pixel.
     * @throws IndexOutOfBoundsException If the pixel is outside the image.
     */
    int getPixel(int x, int y)
    {
        if (x < 0 || x >= width || y < 0 || y >= height)
        {
            throw new IndexOutOfBoundsException("Pixel (" + x + ", " + y + ") outside " + width + " x " + height);
        }
        return argb[y * width + x];
    }

    /**
     * Copies the pixels into a new buffer.
     *
     * @return The buffer, owned by the caller.
     */
    PixelBuffer copyToBuffer()
    {
        final PixelBuffer copy = new PixelBuffer(width, height);
        copy.setPixels(argb);
        return copy;
    }
}
