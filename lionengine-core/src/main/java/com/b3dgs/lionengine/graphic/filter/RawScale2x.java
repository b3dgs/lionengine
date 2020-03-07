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

/**
 * The raw scale implementation.
 */
final class RawScale2x
{
    /** Scale factor. */
    public static final int SCALE = 2;

    /** Width. */
    private final int width;
    /** Height. */
    private final int height;

    /**
     * Internal constructor.
     * 
     * @param dataWidth The data width.
     * @param dataHeight The data height.
     */
    RawScale2x(int dataWidth, int dataHeight)
    {
        super();

        width = dataWidth;
        height = dataHeight;
    }

    /**
     * Get the scaled data.
     * 
     * @param srcImage The image source.
     * @return The data array.
     */
    int[] getScaledData(int[] srcImage)
    {
        final int[] dstImage = new int[srcImage.length * SCALE * SCALE];

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                process(srcImage, dstImage, x, y);
            }
        }

        return dstImage;
    }

    /**
     * Set destination pixel.
     * 
     * @param dstImage The image destination.
     * @param x The location x.
     * @param y The location y.
     * @param p The pixel destination value.
     */
    private void setDestPixel(int[] dstImage, int x, int y, int p)
    {
        dstImage[x + y * width * SCALE] = p;
    }

    /**
     * Get pixel source.
     * 
     * @param srcImage The image source.
     * @param x The location x.
     * @param y The location y.
     * @return The pixel value found.
     */
    private int getSourcePixel(int[] srcImage, int x, int y)
    {
        int x1 = Math.max(0, x);
        x1 = Math.min(width - 1, x1);
        int y1 = Math.max(0, y);
        y1 = Math.min(height - 1, y1);

        return srcImage[x1 + y1 * width];
    }

    /**
     * Process filter.
     * 
     * @param srcImage The image source.
     * @param dstImage The image destination.
     * @param x The location x.
     * @param y The location y.
     */
    private void process(int[] srcImage, int[] dstImage, int x, int y)
    {
        final int b = getSourcePixel(srcImage, x, y - 1);
        final int d = getSourcePixel(srcImage, x - 1, y);
        final int e = getSourcePixel(srcImage, x, y);
        final int f = getSourcePixel(srcImage, x + 1, y);
        final int h = getSourcePixel(srcImage, x, y + 1);
        int e0 = e;
        int e1 = e;
        int e2 = e;
        int e3 = e;
        if (b != h && d != f)
        {
            e0 = d == b ? d : e;
            e1 = b == f ? f : e;
            e2 = d == h ? d : e;
            e3 = h == f ? f : e;
        }

        setDestPixel(dstImage, x * SCALE, y * SCALE, e0);
        setDestPixel(dstImage, x * SCALE + 1, y * SCALE, e1);
        setDestPixel(dstImage, x * SCALE, y * SCALE + 1, e2);
        setDestPixel(dstImage, x * SCALE + 1, y * SCALE + 1, e3);
    }
}
