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
 * the entry and released with its last reference, when the entry leaves the cache. A development reload replaces the
 * pixels and the texture together, with new ones (ADR-0055). Runtime thread only, checked by the cache.
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
    private int width;
    /** Height in pixels. */
    private int height;
    /** The texture of the pixels. */
    private int texture;
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
     * Tells whether the entry belongs to a cache.
     *
     * @param owner The cache.
     * @return {@code true} if the entry is held by this cache.
     */
    boolean belongsTo(ImageCache owner)
    {
        return cache == owner;
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
     *
     * @throws IllegalStateException If called on another thread than the owner of the cache; nothing changes then.
     */
    void release()
    {
        cache.checkThread();
        references--;
        if (references == 0)
        {
            textures.release(texture);
            argb = null;
            cache.remove(key, getBytes());
        }
    }

    /**
     * Replaces the pixels and the texture, for a development reload: creates the new texture, then releases the
     * previous one.
     *
     * @param newWidth The new width in pixels.
     * @param newHeight The new height in pixels.
     * @param newArgb The new pixels, owned by the entry from now on and never modified.
     */
    void replace(int newWidth, int newHeight, int[] newArgb)
    {
        final int newTexture = textures.create(newWidth, newHeight, newArgb);
        textures.release(texture);
        width = newWidth;
        height = newHeight;
        argb = newArgb;
        texture = newTexture;
    }

    /**
     * Returns the bytes held by the pixels.
     *
     * @return The bytes, 4 per pixel.
     */
    long getBytes()
    {
        return ImageCache.bytes(width, height);
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
     *
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    void prepare()
    {
        cache.checkThread();
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
