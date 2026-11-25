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

import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * One acquisition of an image held by an {@link ImageCache}: read access to its immutable pixels and its texture, and
 * the release of that acquisition (ADR-0005, ADR-0007, ADR-0019, ADR-0026, REQ-SPRITE-002, REQ-RES-004).
 *
 * <p>
 * Every acquisition returns a new handle, and every handle of one image shares the same pixels and the same texture:
 * the image is decoded once. The code that acquired a handle releases it, normally the scene on exit; the image leaves
 * the cache, and its texture is released, when its last handle is released. Releasing a handle is idempotent: a
 * second release has no effect and does not throw. Once released, a handle gives only its key and its discriminator.
 * </p>
 *
 * <p>
 * The pixels are packed {@code 0xAARRGGBB}, row-major from the top-left, with straight alpha (ADR-0033), and never
 * change: a game that needs modified pixels takes a copy with {@link #copyToBuffer()} and adds it to the cache under
 * a discriminator ({@link ImageCache#add(ResourceKey, String, PixelBuffer)}). Runtime thread only (ADR-0018).
 * </p>
 *
 * @since 11.0.0
 */
public final class Image
{
    /** Error message of a released handle. */
    private static final String ERROR_RELEASED = "Image released: ";

    /** The shared image. */
    private final ImageEntry entry;
    /** {@code true} once released. */
    private boolean released;

    /**
     * Creates a handle, for {@link ImageEntry} only.
     *
     * @param entry The shared image, whose reference count includes this handle.
     */
    Image(ImageEntry entry)
    {
        super();

        this.entry = entry;
    }

    /**
     * Returns the resource key of the image.
     *
     * @return The resource key, also after the release.
     */
    public ResourceKey getKey()
    {
        return entry.getKey().key();
    }

    /**
     * Returns the discriminator of the image.
     *
     * @return The discriminator: {@link ImageCache#DECODED} for the image decoded from its resource, otherwise the one
     *         given to {@link ImageCache#add(ResourceKey, String, PixelBuffer)}; also after the release.
     */
    public String getDiscriminator()
    {
        return entry.getKey().discriminator();
    }

    /**
     * Returns the width.
     *
     * @return The width in pixels.
     * @throws IllegalStateException If the handle is released.
     */
    public int getWidth()
    {
        checkAlive();
        return entry.getWidth();
    }

    /**
     * Returns the height.
     *
     * @return The height in pixels.
     * @throws IllegalStateException If the handle is released.
     */
    public int getHeight()
    {
        checkAlive();
        return entry.getHeight();
    }

    /**
     * Returns a pixel.
     *
     * @param x The column, from {@code 0} at the left.
     * @param y The row, from {@code 0} at the top.
     * @return The pixel, packed {@code 0xAARRGGBB} with straight alpha.
     * @throws IllegalStateException If the handle is released.
     * @throws IndexOutOfBoundsException If the pixel is outside the image.
     */
    public int getPixel(int x, int y)
    {
        checkAlive();
        return entry.getPixel(x, y);
    }

    /**
     * Copies the pixels into a new buffer, which the caller owns and may modify; the image does not change.
     *
     * @return The new buffer.
     * @throws IllegalStateException If the handle is released.
     */
    public PixelBuffer copyToBuffer()
    {
        checkAlive();
        return entry.copyToBuffer();
    }

    /**
     * Returns the texture of the image, to draw it with render commands.
     *
     * @return The texture handle of the render backend, shared by every handle of the image and released with the
     *         image.
     * @throws IllegalStateException If the handle is released.
     */
    public int getTexture()
    {
        checkAlive();
        return entry.getTexture();
    }

    /**
     * Uploads the texture ahead of its first use, to avoid a hitch at the first frame that draws it (ADR-0026).
     *
     * @throws IllegalStateException If the handle is released.
     */
    public void prepare()
    {
        checkAlive();
        entry.prepare();
    }

    /**
     * Tells whether the handle is released.
     *
     * @return {@code true} once {@link #release()} was called.
     */
    public boolean isReleased()
    {
        return released;
    }

    /**
     * Releases this acquisition. Idempotent: a second call has no effect. The last release of an image removes it from
     * the cache and releases its texture.
     */
    public void release()
    {
        if (!released)
        {
            released = true;
            entry.release();
        }
    }

    /**
     * Checks that the handle is not released.
     *
     * @throws IllegalStateException If the handle is released.
     */
    private void checkAlive()
    {
        if (released)
        {
            throw new IllegalStateException(ERROR_RELEASED + getKey());
        }
    }
}
