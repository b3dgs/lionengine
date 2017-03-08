/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.util.UtilConversion;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Color utility class.
 */
public final class UtilColor
{
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
        final int alpha = UtilConversion.mask(value >> Constant.BYTE_4);
        if (alpha == 0)
        {
            return 0;
        }
        final int red = UtilConversion.mask(value >> Constant.BYTE_3);
        final int green = UtilConversion.mask(value >> Constant.BYTE_2);
        final int blue = UtilConversion.mask(value >> Constant.BYTE_1);

        final int alphaMask = UtilConversion.mask(UtilMath.clamp(alpha, 0, 255)) << Constant.BYTE_4;
        final int redMask = UtilConversion.mask(UtilMath.clamp(red + r, 0, 255)) << Constant.BYTE_3;
        final int greenMask = UtilConversion.mask(UtilMath.clamp(green + g, 0, 255)) << Constant.BYTE_2;
        final int blueMask = UtilConversion.mask(UtilMath.clamp(blue + b, 0, 255)) << Constant.BYTE_1;

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
    public static int getRasterColor(int i, RasterData data, int max)
    {
        final int start = data.getStart();
        final int step = data.getStep();
        final int force = data.getForce();
        final int amplitude = data.getAmplitude();
        final int offset = data.getOffset();

        if (0 == data.getType())
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
            return ColorRgba.TRANSPARENT;
        }
        return new ColorRgba((int) Math.floor(r / (double) count),
                             (int) Math.floor(g / (double) count),
                             (int) Math.floor(b / (double) count));
    }

    /**
     * Check if colors transparency type are exclusive (one is {@link ColorRgba#OPAQUE} and the other
     * {@link ColorRgba#TRANSPARENT}).
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
     * Check if colors transparency type are exclusive (one is {@link ColorRgba#OPAQUE} and the other
     * {@link ColorRgba#TRANSPARENT}).
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
     * Private constructor.
     */
    private UtilColor()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
