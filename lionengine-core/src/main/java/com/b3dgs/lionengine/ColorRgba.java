/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * <p>
 * This class is Thread-Safe.
 * </p>
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
    /** Opaque color. */
    public static final ColorRgba OPAQUE = new ColorRgba(0, 0, 0, 255);

    /** Minimum color value. */
    private static final int MIN_COLOR = 0x000000;
    /** Maximum alpha value. */
    private static final int MAX_ALPHA = 0xFF000000;
    /** Maximum red value. */
    private static final int MAX_RED = 0xFF0000;
    /** Maximum green value. */
    private static final int MAX_GREEN = 0x00FF00;
    /** Maximum blue value. */
    private static final int MAX_BLUE = 0x0000FF;

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
        if (rgb == 0)
        {
            return rgb;
        }

        final int a = rgb & MAX_ALPHA;
        final int r = UtilMath.clamp((rgb & MAX_RED) + fr, MIN_COLOR, MAX_RED);
        final int g = UtilMath.clamp((rgb & MAX_GREEN) + fg, MIN_COLOR, MAX_GREEN);
        final int b = UtilMath.clamp((rgb & MAX_BLUE) + fb, MIN_COLOR, MAX_BLUE);

        return a | r | g | b;
    }

    /**
     * Get the increased color value. Current color not modified.
     * 
     * @param value The original color value.
     * @param r The increase red value.
     * @param g The increase green value.
     * @param b The increase blue value.
     * @return The increased color value.
     */
    public static int inc(int value, int r, int g, int b)
    {
        final int alpha = mask(value >> Constant.BYTE_4);
        if (alpha == 0)
        {
            return 0;
        }
        final int red = mask(value >> Constant.BYTE_3);
        final int green = mask(value >> Constant.BYTE_2);
        final int blue = mask(value >> Constant.BYTE_1);

        final int alphaMask = mask(UtilMath.clamp(alpha, 0, 255)) << Constant.BYTE_4;
        final int redMask = mask(UtilMath.clamp(red + r, 0, 255)) << Constant.BYTE_3;
        final int greenMask = mask(UtilMath.clamp(green + g, 0, 255)) << Constant.BYTE_2;
        final int blueMask = mask(UtilMath.clamp(blue + b, 0, 255)) << Constant.BYTE_1;

        return alphaMask | redMask | greenMask | blueMask;
    }

    /**
     * Return the delta between two colors.
     * 
     * @param a The first color.
     * @param b The second color.
     * @return The delta between the two colors.
     */
    public static double getDelta(ColorRgba a, ColorRgba b)
    {
        final double dr = a.getRed() - (double) b.getRed();
        final double dg = a.getGreen() - (double) b.getGreen();
        final double db = a.getBlue() - (double) b.getBlue();
        return Math.sqrt(dr * dr + dg * dg + db * db);
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
        final int startIndex = 0;
        final int start = data[startIndex];

        final int stepIndex = 1;
        final int step = data[stepIndex];

        final int forceIndex = 2;
        final int force = data[forceIndex];

        final int amplitudeIndex = 3;
        final int amplitude = data[amplitudeIndex];

        final int offsetIndex = 4;
        final int offset = data[offsetIndex];

        final int typeIndex = 5;
        if (0 == data[typeIndex])
        {
            return start + step * (int) (force * UtilMath.sin(i * (amplitude / (double) max) - offset));
        }
        return start + step * (int) (force * UtilMath.cos(i * (amplitude / (double) max) - offset));
    }

    /**
     * Get the weighted color of an area.
     * 
     * @param surface The surface reference.
     * @param sx The starting horizontal location.
     * @param sy The starting vertical location.
     * @param width The area width.
     * @param height The area height.
     * @return The weighted color.
     */
    public static ColorRgba getWeightedColor(ImageBuffer surface, int sx, int sy, int width, int height)
    {
        int r = 0;
        int g = 0;
        int b = 0;
        int count = 0;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                final ColorRgba color = new ColorRgba(surface.getRgb(sx + x, sy + y));
                if (color.getAlpha() > 0)
                {
                    r += color.getRed();
                    g += color.getGreen();
                    b += color.getBlue();
                    count++;
                }
            }
        }
        if (count == 0)
        {
            return TRANSPARENT;
        }
        return new ColorRgba((int) Math.floor(r / (double) count),
                             (int) Math.floor(g / (double) count),
                             (int) Math.floor(b / (double) count));
    }

    /**
     * Check if colors transparency type are exclusive (one is {@link #OPAQUE} and the other {@link #TRANSPARENT}).
     * 
     * @param colorA The first color.
     * @param colorB The second color.
     * @return <code>true</code> if exclusive, <code>false</code> else.
     */
    public static boolean isOpaqueTransparentExclusive(ColorRgba colorA, ColorRgba colorB)
    {
        return isOpaqueTransparentExclusive(colorA.getRgba(), colorB.getRgba());
    }

    /**
     * Check if colors transparency type are exclusive (one is {@link #OPAQUE} and the other {@link #TRANSPARENT}).
     * 
     * @param colorA The first color.
     * @param colorB The second color.
     * @return <code>true</code> if exclusive, <code>false</code> else.
     */
    public static boolean isOpaqueTransparentExclusive(int colorA, int colorB)
    {
        return colorA == ColorRgba.TRANSPARENT.getRgba() && colorB == ColorRgba.OPAQUE.getRgba()
               || colorA == ColorRgba.OPAQUE.getRgba() && colorB == ColorRgba.TRANSPARENT.getRgba();
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
     * Create an opaque color.
     * 
     * @param r The red value [0-255].
     * @param g The green value [0-255].
     * @param b The blue value [0-255].
     * @throws LionEngineException If color value is not in a valid range.
     */
    public ColorRgba(int r, int g, int b)
    {
        this(r, g, b, 255);
    }

    /**
     * Create a 4 channels color.
     * 
     * @param r The red value [0-255].
     * @param g The green value [0-255].
     * @param b The blue value [0-255].
     * @param a The alpha value [0-255].
     * @throws LionEngineException If color value is not in a valid range.
     */
    public ColorRgba(int r, int g, int b, int a)
    {
        Check.superiorOrEqual(r, 0);
        Check.inferiorOrEqual(r, 255);

        Check.superiorOrEqual(g, 0);
        Check.inferiorOrEqual(g, 255);

        Check.superiorOrEqual(b, 0);
        Check.inferiorOrEqual(b, 255);

        Check.superiorOrEqual(a, 0);
        Check.inferiorOrEqual(a, 255);

        value = mask(a) << Constant.BYTE_4
                | mask(r) << Constant.BYTE_3
                | mask(g) << Constant.BYTE_2
                | mask(b) << Constant.BYTE_1;
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
        alpha = mask(value >> Constant.BYTE_4);
        red = mask(value >> Constant.BYTE_3);
        green = mask(value >> Constant.BYTE_2);
        blue = mask(value >> Constant.BYTE_1);
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

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + alpha;
        result = prime * result + blue;
        result = prime * result + green;
        result = prime * result + red;
        result = prime * result + value;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof ColorRgba)
        {
            final ColorRgba color = (ColorRgba) object;
            return color.value == value;
        }
        return false;
    }
}
