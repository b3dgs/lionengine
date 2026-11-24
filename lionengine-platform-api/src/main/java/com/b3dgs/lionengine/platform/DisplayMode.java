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
 * Mode of the display device: size, refresh rate and color depth (ADR-0002, ADR-0022, ADR-0033, ADR-0043,
 * REQ-DISPLAY-003).
 *
 * <p>
 * Sizes are device pixels of the default monitor. A refresh rate or a bit depth of {@link #UNKNOWN} means unknown or
 * multiple; {@code platform-awt} maps the AWT values {@code 0} and {@code -1} to it. Immutable and thread-safe.
 * </p>
 *
 * @param width The width in device pixels (positive).
 * @param height The height in device pixels (positive).
 * @param refreshRate The refresh rate in Hz, {@link #UNKNOWN} if unknown or multiple.
 * @param bitDepth The color depth in bits per pixel, {@link #UNKNOWN} if unknown or multiple.
 * @since 11.0.0
 */
public record DisplayMode(int width, int height, int refreshRate, int bitDepth)
{
    /** Value of an unknown or multiple refresh rate or bit depth. */
    public static final int UNKNOWN = 0;

    /**
     * Creates a display mode.
     *
     * @param width The width in device pixels (positive).
     * @param height The height in device pixels (positive).
     * @param refreshRate The refresh rate in Hz, {@link #UNKNOWN} if unknown or multiple (not negative).
     * @param bitDepth The color depth in bits per pixel, {@link #UNKNOWN} if unknown or multiple (not negative).
     * @throws IllegalArgumentException If a size is not positive, or the refresh rate or the bit depth is negative.
     */
    public DisplayMode
    {
        if (width <= 0 || height <= 0 || refreshRate < 0 || bitDepth < 0)
        {
            throw new IllegalArgumentException("Invalid display mode: "
                                               + width
                                               + 'x'
                                               + height
                                               + ' '
                                               + refreshRate
                                               + "Hz "
                                               + bitDepth
                                               + "bits");
        }
    }

    /**
     * Tells whether the refresh rate is known.
     *
     * @return {@code true} if the refresh rate is not {@link #UNKNOWN}.
     */
    public boolean isRefreshRateKnown()
    {
        return refreshRate != UNKNOWN;
    }

    /**
     * Tells whether the bit depth is known.
     *
     * @return {@code true} if the bit depth is not {@link #UNKNOWN}.
     */
    public boolean isBitDepthKnown()
    {
        return bitDepth != UNKNOWN;
    }
}
