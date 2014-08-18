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
    /** Transparent color. */
    public static final ColorRgba TRANSPARENT = new ColorRgba(0, 0, 0, 0);

    /**
     * Apply a filter rgb.
     * 
     * @param rgb The original rgb.
     * @param fr The red filter.
     * @param fg The green filter.
     * @param fb The blue filter.
     * @return The filtered rgb.
     */
    public static int filterRgb(int rgb, int fr, int fg, int fb)
    {
        if (-16711423 == rgb || 0 == rgb || 16711935 == rgb)
        {
            return rgb;
        }

        final int a = rgb & 0xFF000000;
        int r = (rgb & 0xFF0000) + fr;
        int g = (rgb & 0x00FF00) + fg;
        int b = (rgb & 0x0000FF) + fb;

        if (r < 0x000000)
        {
            r = 0x000000;
        }
        if (r > 0xFF0000)
        {
            r = 0xFF0000;
        }
        if (g < 0x000000)
        {
            g = 0x000000;
        }
        if (g > 0x00FF00)
        {
            g = 0x00FF00;
        }
        if (b < 0x000000)
        {
            b = 0x000000;
        }
        if (b > 0x0000FF)
        {
            b = 0x0000FF;
        }

        return a | r | g | b;
    }

    /**
     * Get raster color.
     * 
     * @param i The color offset.
     * @param data The raster data.
     * @param max The max offset.
     * @return The rastered color.
     */
    public static int getRasterColor(int i, int[] data, int max)
    {
        if (0 == data[5])
        {
            return data[0] + data[1] * (int) (data[2] * UtilMath.sin(i * (data[3] / (double) max) - data[4]));
        }
        return data[0] + data[1] * (int) (data[2] * UtilMath.cos(i * (data[3] / (double) max) - data[4]));
    }

    /**
     * Return the masked value by 0xFF.
     * 
     * @param value The input value.
     * @return The masked value.
     */
    private static int mask(int value)
    {
        return value & 0xFF;
    }

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

        value = ColorRgba.mask(a) << 24 | ColorRgba.mask(r) << 16 | ColorRgba.mask(g) << 8 | ColorRgba.mask(b) << 0;
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
        alpha = ColorRgba.mask(value >> 24);
        red = ColorRgba.mask(value >> 16);
        green = ColorRgba.mask(value >> 8);
        blue = ColorRgba.mask(value >> 0);
    }

    /**
     * Get the increased color value. Current color not modified.
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
        return ColorRgba.mask(255) << 24 | ColorRgba.mask(UtilMath.fixBetween(red + r, 0, 255)) << 16
                | ColorRgba.mask(UtilMath.fixBetween(green + g, 0, 255)) << 8
                | ColorRgba.mask(UtilMath.fixBetween(blue + b, 0, 255)) << 0;
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
