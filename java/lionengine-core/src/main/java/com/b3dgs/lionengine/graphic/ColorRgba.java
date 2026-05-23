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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents a color with red, green, blue and alpha.
 * 
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @param red The red value.
 * @param green The green value.
 * @param blue The blue value.
 * @param alpha The alpha value.
 * @param rgba The rgba value.
 */
public record ColorRgba(int red, int green, int blue, int alpha, int rgba)
{
    /** Red color. */
    public static final ColorRgba RED = new ColorRgba(255, 0, 0);
    /** Green color. */
    public static final ColorRgba GREEN = new ColorRgba(0, 255, 0);
    /** Blue color. */
    public static final ColorRgba BLUE = new ColorRgba(0, 0, 255);
    /** Cyan color. */
    public static final ColorRgba CYAN = new ColorRgba(0, 255, 255);
    /** Purple color. */
    public static final ColorRgba PURPLE = new ColorRgba(255, 0, 255);
    /** Yellow color. */
    public static final ColorRgba YELLOW = new ColorRgba(255, 255, 0);
    /** White color. */
    public static final ColorRgba WHITE = new ColorRgba(255, 255, 255);
    /** Light gray. */
    public static final ColorRgba GRAY_LIGHT = new ColorRgba(192, 192, 192);
    /** Grey color. */
    public static final ColorRgba GRAY = new ColorRgba(128, 128, 128);
    /** Light gray. */
    public static final ColorRgba GRAY_DARK = new ColorRgba(64, 64, 64);
    /** Black color. */
    public static final ColorRgba BLACK = new ColorRgba(0, 0, 0);
    /** Transparent color. */
    public static final ColorRgba TRANSPARENT = new ColorRgba(0, 0, 0, 0);
    /** Opaque color. */
    public static final ColorRgba OPAQUE = new ColorRgba(0, 0, 0, 255);

    /**
     * Get color as integer value.
     * 
     * @param red The red value [0-255].
     * @param green The green value [0-255].
     * @param blue The blue value [0-255].
     * @param alpha The alpha value [0-255].
     * @return The integer color.
     */
    public static int getValue(int red, int green, int blue, int alpha)
    {
        // CHECKSTYLE IGNORE LINE: BooleanExpressionComplexity
        return (alpha & 0xFF) << Constant.BYTE_4
               | (red & 0xFF) << Constant.BYTE_3
               | (green & 0xFF) << Constant.BYTE_2
               | (blue & 0xFF) << Constant.BYTE_1;
    }

    /**
     * Create an opaque color.
     * 
     * @param red The red value [0-255].
     * @param green The green value [0-255].
     * @param blue The blue value [0-255].
     * @throws LionEngineException If color value is not in a valid range.
     */
    public ColorRgba(int red, int green, int blue)
    {
        this(red, green, blue, 255, getValue(red, green, blue, 255));
    }

    /**
     * Create a transparent color.
     * 
     * @param red The red value [0-255].
     * @param green The green value [0-255].
     * @param blue The blue value [0-255].
     * @param alpha The alpha value [0-255].
     * @throws LionEngineException If color value is not in a valid range.
     */
    public ColorRgba(int red, int green, int blue, int alpha)
    {
        this(red, green, blue, alpha, getValue(red, green, blue, alpha));
    }

    /**
     * Create a color.
     * 
     * @param rgba The color value.
     */
    public ColorRgba(int rgba)
    {
        this(rgba >> Constant.BYTE_3 & 0xFF,
             rgba >> Constant.BYTE_2 & 0xFF,
             rgba >> Constant.BYTE_1 & 0xFF,
             rgba >> Constant.BYTE_4 & 0xFF,
             rgba);
    }

    /**
     * Create a 4 channels color.
     * 
     * @param red The red value [0-255].
     * @param green The green value [0-255].
     * @param blue The blue value [0-255].
     * @param alpha The alpha value [0-255].
     * @param rgba The rgba value.
     * @throws LionEngineException If color value is not in a valid range.
     */
    public ColorRgba
    {
        Check.superiorOrEqual(red, 0);
        Check.inferiorOrEqual(red, 255);

        Check.superiorOrEqual(green, 0);
        Check.inferiorOrEqual(green, 255);

        Check.superiorOrEqual(blue, 0);
        Check.inferiorOrEqual(blue, 255);

        Check.superiorOrEqual(alpha, 0);
        Check.inferiorOrEqual(alpha, 255);

        Check.equality(getValue(red, green, blue, alpha), rgba);
    }
}
