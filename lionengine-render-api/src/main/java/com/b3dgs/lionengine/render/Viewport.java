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
 * Rectangle of the logical frame that clips and offsets the following render commands (ADR-0021, ADR-0031).
 *
 * <p>
 * Coordinates are pixels of the logical frame, origin at the top-left, Y down. A scene with several views sets one
 * viewport per view; the default viewport is the whole frame. The part outside the logical frame is clipped. Immutable
 * and thread-safe.
 * </p>
 *
 * @param x The left edge in logical-frame pixels.
 * @param y The top edge in logical-frame pixels.
 * @param width The width in pixels (positive).
 * @param height The height in pixels (positive).
 * @since 11.0.0
 */
public record Viewport(int x, int y, int width, int height)
{
    /**
     * Creates a viewport.
     *
     * @param x The left edge in logical-frame pixels.
     * @param y The top edge in logical-frame pixels.
     * @param width The width in pixels (positive).
     * @param height The height in pixels (positive).
     * @throws IllegalArgumentException If {@code width} or {@code height} is not positive.
     */
    public Viewport
    {
        if (width <= 0 || height <= 0)
        {
            throw new IllegalArgumentException("Invalid viewport size: " + width + 'x' + height);
        }
    }
}
