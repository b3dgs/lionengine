/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Represents a color with red, green, blue and alpha.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ColorRgba
{
    /** Red color. */
    public static final ColorRgba RED = new ColorRgba(255, 0, 0);
    /** Green color. */
    public static final ColorRgba GREEN = new ColorRgba(0, 255, 0);
    /** Blue color. */
    public static final ColorRgba BLUE = new ColorRgba(0, 0, 255);
    /** Cyan color. */
    public static final ColorRgba CYAN = new ColorRgba(0, 255, 255);
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
    }

    /**
     * Create a color.
     * 
     * @param value The color value.
     */
    public ColorRgba(int value)
    {
        this.value = value;
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
}
