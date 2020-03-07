/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.UtilMath;

/**
 * Color utility class.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilColor
{
    /**
     * Get color as integer value.
     * 
     * @param r The red value [0-255].
     * @param g The green value [0-255].
     * @param b The blue value [0-255].
     * @param a The alpha value [0-255].
     * @return The integer color.
     */
    public static int getRgbaValue(int r, int g, int b, int a)
    {
        // CHECKSTYLE IGNORE LINE: BooleanExpressionComplexity
        return (a & 0xFF) << Constant.BYTE_4
               | (r & 0xFF) << Constant.BYTE_3
               | (g & 0xFF) << Constant.BYTE_2
               | (b & 0xFF) << Constant.BYTE_1;
    }

    /**
     * Apply an rgb factor.
     * 
     * @param rgb The original rgb.
     * @param fr The red factor.
     * @param fg The green factor.
     * @param fb The blue factor.
     * @return The multiplied rgb.
     */
    public static int multiplyRgb(int rgb, double fr, double fg, double fb)
    {
        if (rgb == 0)
        {
            return rgb;
        }

        final int a = rgb >> Constant.BYTE_4 & 0xFF;
        final int r = (int) UtilMath.clamp((rgb >> Constant.BYTE_3 & 0xFF) * fr, 0, 255);
        final int g = (int) UtilMath.clamp((rgb >> Constant.BYTE_2 & 0xFF) * fg, 0, 255);
        final int b = (int) UtilMath.clamp((rgb >> Constant.BYTE_1 & 0xFF) * fb, 0, 255);

        return getRgbaValue(r, g, b, a);
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
        final int alpha = value >> Constant.BYTE_4 & 0xFF;
        if (alpha == 0)
        {
            return 0;
        }
        final int red = value >> Constant.BYTE_3 & 0xFF;
        final int green = value >> Constant.BYTE_2 & 0xFF;
        final int blue = value >> Constant.BYTE_1 & 0xFF;

        return getRgbaValue(UtilMath.clamp(red + r, 0, 255),
                            UtilMath.clamp(green + g, 0, 255),
                            UtilMath.clamp(blue + b, 0, 255),
                            UtilMath.clamp(alpha, 0, 255));
    }

    /**
     * Return the delta between two colors.
     * 
     * @param a The first color (must not be <code>null</code>).
     * @param b The second color (must not be <code>null</code>).
     * @return The delta between the two colors.
     * @throws LionEngineException If invalid arguments.
     */
    public static double getDelta(ColorRgba a, ColorRgba b)
    {
        Check.notNull(a);
        Check.notNull(b);

        final int dr = a.getRed() - b.getRed();
        final int dg = a.getGreen() - b.getGreen();
        final int db = a.getBlue() - b.getBlue();

        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    /**
     * Get the weighted color of an area.
     * 
     * @param surface The surface reference (must not be <code>null</code>).
     * @param sx The starting horizontal location.
     * @param sy The starting vertical location.
     * @param width The area width.
     * @param height The area height.
     * @return The weighted color.
     */
    public static ColorRgba getWeightedColor(ImageBuffer surface, int sx, int sy, int width, int height)
    {
        Check.notNull(surface);

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
     * @param colorA The first color (must not be <code>null</code>).
     * @param colorB The second color (must not be <code>null</code>).
     * @return <code>true</code> if exclusive, <code>false</code> else.
     * @throws LionEngineException If invalid arguments.
     */
    public static boolean isOpaqueTransparentExclusive(ColorRgba colorA, ColorRgba colorB)
    {
        Check.notNull(colorA);
        Check.notNull(colorB);

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
