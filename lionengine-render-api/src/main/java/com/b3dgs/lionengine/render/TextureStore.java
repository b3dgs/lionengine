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
package com.b3dgs.lionengine.render;

/**
 * Textures of a render backend, created from primitive pixel data (ADR-0026, ADR-0033, ADR-0047, REQ-SPRITE-010).
 *
 * <p>
 * Pixel data is an {@code int[]} of packed {@code 0xAARRGGBB} values, row-major, origin at the top-left, stride equal
 * to the width, with straight (non-premultiplied) alpha; a backend may premultiply at upload. The array is shared
 * read-only: neither the caller nor the backend modifies it after {@link #create(int, int, int[])}, and the backend may
 * keep it as its copy for recovering a lost context until {@link #release(int)}. The {@code graphics} cache hands the
 * array of its immutable cached source and releases the texture when it releases the source.
 * </p>
 *
 * <p>
 * Threads: every method is called on the runtime thread (cache rule, ADR-0018); the GPU upload itself happens on the
 * render thread, at first use or at {@link #prepare(int)}, never on a loading worker.
 * </p>
 *
 * @since 11.0.0
 */
public interface TextureStore
{
    /**
     * Creates a texture. No GPU work happens here.
     *
     * @param width The width in pixels (positive).
     * @param height The height in pixels (positive).
     * @param argb The pixels, at least {@code width * height} values, shared read-only from now on (never
     *            {@code null}).
     * @return The texture handle, valid until {@link #release(int)}.
     * @throws IllegalArgumentException If a size is not positive or {@code argb} is {@code null} or too short.
     */
    int create(int width, int height, int[] argb);

    /**
     * Uploads the texture ahead of its first use, to avoid a hitch at first render.
     *
     * @param texture The texture handle.
     * @throws IllegalArgumentException If the handle is unknown or released.
     */
    void prepare(int texture);

    /**
     * Releases the texture and its GPU memory. Idempotent: releasing a released handle has no effect.
     *
     * @param texture The texture handle.
     */
    void release(int texture);
}
