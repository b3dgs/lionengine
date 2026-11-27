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
package com.b3dgs.lionengine.platform;

/**
 * Where the logical frame is presented on the output surface (ADR-0006, ADR-0035, ADR-0043, ADR-0046).
 *
 * <p>
 * The rectangle is in device pixels of the output surface, origin at the top-left, Y down; the scales convert
 * logical-frame pixels to device pixels on each axis (equal for {@code FIT} and {@code INTEGER}, possibly different
 * for {@code STRETCH}). The platform computes it from the effective scaling policy, the logical frame size and the
 * surface size, at the frame boundary of each change. Render backends present with it; input adapters convert
 * device-pixel pointer positions back with {@link #toLogicalX(double)} and {@link #toLogicalY(double)}, at inbox
 * delivery on the runtime thread. Immutable and thread-safe.
 * </p>
 *
 * @param x The left edge of the presented frame, in device pixels.
 * @param y The top edge of the presented frame, in device pixels.
 * @param width The width of the presented frame, in device pixels (not negative).
 * @param height The height of the presented frame, in device pixels (not negative).
 * @param scaleX The horizontal scale from logical-frame pixels to device pixels (positive).
 * @param scaleY The vertical scale from logical-frame pixels to device pixels (positive).
 * @since 11.0.0
 */
public record PresentationGeometry(int x, int y, int width, int height, double scaleX, double scaleY)
{
    /**
     * Creates a geometry.
     *
     * @param x The left edge of the presented frame, in device pixels.
     * @param y The top edge of the presented frame, in device pixels.
     * @param width The width of the presented frame, in device pixels (not negative).
     * @param height The height of the presented frame, in device pixels (not negative).
     * @param scaleX The horizontal scale from logical-frame pixels to device pixels (positive and finite).
     * @param scaleY The vertical scale from logical-frame pixels to device pixels (positive and finite).
     * @throws IllegalArgumentException If a size is negative or a scale is not positive and finite.
     */
    public PresentationGeometry
    {
        if (width < 0 || height < 0 || !isValidScale(scaleX) || !isValidScale(scaleY))
        {
            throw new IllegalArgumentException("Invalid presentation geometry");
        }
    }

    /**
     * Tells whether a scale is valid.
     *
     * @param scale The scale.
     * @return {@code true} if the scale is positive and finite.
     */
    private static boolean isValidScale(double scale)
    {
        return scale > 0.0 && !Double.isInfinite(scale);
    }

    /**
     * Tells whether a device-pixel position is inside the presented frame.
     *
     * @param deviceX The horizontal position in device pixels.
     * @param deviceY The vertical position in device pixels.
     * @return {@code true} if the position is inside the presented frame.
     */
    public boolean contains(double deviceX, double deviceY)
    {
        return deviceX >= x && deviceY >= y && deviceX < x + width && deviceY < y + height;
    }

    /**
     * Converts a horizontal device-pixel position to logical-frame pixels.
     *
     * @param deviceX The horizontal position in device pixels.
     * @return The horizontal position in logical-frame pixels, outside {@code [0, logical width)} when the device
     *         position is outside the presented frame.
     */
    public double toLogicalX(double deviceX)
    {
        return (deviceX - x) / scaleX;
    }

    /**
     * Converts a vertical device-pixel position to logical-frame pixels.
     *
     * @param deviceY The vertical position in device pixels.
     * @return The vertical position in logical-frame pixels, outside {@code [0, logical height)} when the device
     *         position is outside the presented frame.
     */
    public double toLogicalY(double deviceY)
    {
        return (deviceY - y) / scaleY;
    }
}
