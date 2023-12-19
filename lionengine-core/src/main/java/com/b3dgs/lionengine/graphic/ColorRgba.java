/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @param r The red value.
 * @param g The green value.
 * @param b The blue value.
 * @param a The alpha value.
 * @param rgba The rgba value.
 */
public record ColorRgba(int r, int g, int b, int a, int rgba)
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
     * Create an opaque color.
     * 
     * @param r The red value [0-255].
     * @param g The green value [0-255].
     * @param b The blue value [0-255].
     * @throws LionEngineException If color value is not in a valid range.
     */
    public ColorRgba(int r, int g, int b)
    {
        this(r, g, b, 255, UtilColor.getRgbaValue(r, g, b, 255));
    }

    /**
     * Create a transparent color.
     * 
     * @param r The red value [0-255].
     * @param g The green value [0-255].
     * @param b The blue value [0-255].
     * @param a The alpha value [0-255].
     * @throws LionEngineException If color value is not in a valid range.
     */
    public ColorRgba(int r, int g, int b, int a)
    {
        this(r, g, b, a, UtilColor.getRgbaValue(r, g, b, a));
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
     * @param r The red value [0-255].
     * @param g The green value [0-255].
     * @param b The blue value [0-255].
     * @param a The alpha value [0-255].
     * @param rgba The rgba value.
     * @throws LionEngineException If color value is not in a valid range.
     */
    public ColorRgba
    {
        Check.superiorOrEqual(r, 0);
        Check.inferiorOrEqual(r, 255);

        Check.superiorOrEqual(g, 0);
        Check.inferiorOrEqual(g, 255);

        Check.superiorOrEqual(b, 0);
        Check.inferiorOrEqual(b, 255);

        Check.superiorOrEqual(a, 0);
        Check.inferiorOrEqual(a, 255);

        Check.equality(UtilColor.getRgbaValue(r, g, b, a), rgba);
    }

    /**
     * Get the color value.
     * 
     * @return The color value.
     */
    public int getRgba()
    {
        return rgba;
    }

    /**
     * Get red value.
     * 
     * @return The red value.
     */
    public int getRed()
    {
        return r;
    }

    /**
     * Get green value.
     * 
     * @return The green value.
     */
    public int getGreen()
    {
        return g;
    }

    /**
     * Get blue value.
     * 
     * @return The blue value.
     */
    public int getBlue()
    {
        return b;
    }

    /**
     * Get alpha value.
     * 
     * @return The alpha value.
     */
    public int getAlpha()
    {
        return a;
    }
}
