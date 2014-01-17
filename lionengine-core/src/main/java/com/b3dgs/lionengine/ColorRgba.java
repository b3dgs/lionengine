/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine;

import com.b3dgs.lionengine.core.UtilityMath;

/**
 * Represents a color with red, green, blue and alpha.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ColorRgba
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

    /** Color value. */
    private final int value;
    /** Red. */
    private final int red;
    /** Green. */
    private final int green;
    /** Blue. */
    private final int blue;
    /** Alpha. */
    private final int alpha;

    /**
     * Constructor.
     * 
     * @param r The red value [0-255].
     * @param g The green value [0-255].
     * @param b The blue value [0-255].
     */
    public ColorRgba(int r, int g, int b)
    {
        this(r, g, b, 255);
    }

    /**
     * Constructor.
     * 
     * @param r The red value [0-255].
     * @param g The green value [0-255].
     * @param b The blue value [0-255].
     * @param a The alpha value [0-255].
     */
    public ColorRgba(int r, int g, int b, int a)
    {
        Check.argument(r >= 0 && r <= 255, "Wrong red value !");
        Check.argument(g >= 0 && g <= 255, "Wrong green value !");
        Check.argument(b >= 0 && b <= 255, "Wrong blue value !");
        Check.argument(a >= 0 && a <= 255, "Wrong alpha value !");
        value = (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0;
        alpha = a;
        red = r;
        green = g;
        blue = b;
    }

    /**
     * Create a color.
     * 
     * @param value The color value.
     */
    public ColorRgba(int value)
    {
        this.value = value;
        alpha = value >> 24 & 0xFF;
        red = value >> 16 & 0xFF;
        green = value >> 8 & 0xFF;
        blue = value >> 0 & 0xFF;
    }

    /**
     * Increase color.
     * 
     * @param r The increase red value.
     * @param g The increase green value.
     * @param b The increase blue value.
     * @return The increased color value.
     */
    public int inc(int r, int g, int b)
    {
        if (alpha == 0)
        {
            return 0;
        }
        return (255 & 0xFF) << 24 | (UtilityMath.fixBetween(red + r, 0, 255) & 0xFF) << 16
                | (UtilityMath.fixBetween(green + g, 0, 255) & 0xFF) << 8
                | (UtilityMath.fixBetween(blue + b, 0, 255) & 0xFF) << 0;
    }

    /**
     * Get the color value.
     * 
     * @return The color value.
     */
    public int getRgba()
    {
        return value;
    }

    /**
     * Get red value.
     * 
     * @return The red value.
     */
    public int getRed()
    {
        return red;
    }

    /**
     * Get green value.
     * 
     * @return The green value.
     */
    public int getGreen()
    {
        return green;
    }

    /**
     * Get blue value.
     * 
     * @return The blue value.
     */
    public int getBlue()
    {
        return blue;
    }

    /**
     * Get alpha value.
     * 
     * @return The alpha value.
     */
    public int getAlpha()
    {
        return alpha;
    }
}
