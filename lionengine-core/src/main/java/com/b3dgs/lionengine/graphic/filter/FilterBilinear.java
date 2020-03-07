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
package com.b3dgs.lionengine.graphic.filter;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transform;

/**
 * Bilinear filter implementation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public class FilterBilinear implements Filter
{
    /**
     * Compute bilinear filtering.
     * 
     * @param in The source pixels.
     * @param out The destination pixels.
     * @param width The image width.
     * @param height The image height.
     * @param radius The radius used.
     */
    private static void compute(int[] in, int[] out, int width, int height, int radius)
    {
        final int widthMinus1 = width - 1;
        final int tableSize = 2 * radius + 1;
        final int[] divide = new int[Constant.UNSIGNED_BYTE * tableSize];

        for (int i = 0; i < Constant.UNSIGNED_BYTE * tableSize; i++)
        {
            divide[i] = i / tableSize;
        }

        int inIndex = 0;
        for (int y = 0; y < height; y++)
        {
            compute(in, out, y, inIndex, divide, width, widthMinus1, height, radius);
            inIndex += width;
        }
    }

    /**
     * Compute bilinear filtering.
     * 
     * @param in The source pixels.
     * @param out The destination pixels.
     * @param y The current vertical line.
     * @param inIndex The width index.
     * @param divide The divide table.
     * @param width The image width.
     * @param widthMinus1 The width less 1.
     * @param height The image height.
     * @param radius The radius used.
     */
    private static void compute(int[] in,
                                int[] out,
                                int y,
                                int inIndex,
                                int[] divide,
                                int width,
                                int widthMinus1,
                                int height,
                                int radius)
    {
        int outIndex = y;
        int ta = 0;
        int tr = 0;
        int tg = 0;
        int tb = 0;

        for (int i = -radius; i <= radius; i++)
        {
            final int rgb = in[inIndex + UtilMath.clamp(i, 0, width - 1)];
            ta += rgb >> Constant.BYTE_4 & 0xFF;
            tr += rgb >> Constant.BYTE_3 & 0xFF;
            tg += rgb >> Constant.BYTE_2 & 0xFF;
            tb += rgb & 0xFF;
        }

        for (int x = 0; x < width; x++)
        {
            out[outIndex] = divide[ta] << Constant.BYTE_4
                            | divide[tr] << Constant.BYTE_3
                            | divide[tg] << Constant.BYTE_2
                            | divide[tb];

            int i1 = x + radius + 1;
            if (i1 > widthMinus1)
            {
                i1 = widthMinus1;
            }
            int i2 = x - radius;
            if (i2 < 0)
            {
                i2 = 0;
            }
            final int rgb1 = in[inIndex + i1];
            final int rgb2 = in[inIndex + i2];
            final int red = 0xFF_00_00;
            final int green = 0xFF_00;
            final int blue = 0xFF;

            ta += (rgb1 >> Constant.BYTE_4 & 0xFF) - (rgb2 >> Constant.BYTE_4 & 0xFF);
            tr += (rgb1 & red) - (rgb2 & red) >> Constant.BYTE_3;
            tg += (rgb1 & green) - (rgb2 & green) >> Constant.BYTE_2;
            tb += (rgb1 & blue) - (rgb2 & blue);
            outIndex += height;
        }
    }

    /**
     * Create the filter.
     */
    public FilterBilinear()
    {
        super();
    }

    /*
     * Filter
     */

    @Override
    public ImageBuffer filter(ImageBuffer source)
    {
        final int width = source.getWidth();
        final int height = source.getHeight();

        final int[] inPixels = new int[width * height];
        final int[] outPixels = new int[width * height];
        source.getRgb(0, 0, width, height, inPixels, 0, width);

        compute(inPixels, outPixels, width, height, 1);
        compute(outPixels, inPixels, height, width, 1);

        final ImageBuffer dest = Graphics.createImageBuffer(width, height, source.getTransparentColor());
        dest.setRgb(0, 0, width, height, inPixels, 0, width);
        return dest;
    }

    @Override
    public Transform getTransform(double scaleX, double scaleY)
    {
        final Transform transform = Graphics.createTransform();
        transform.scale(scaleX, scaleY);
        return transform;
    }
}
