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

import java.awt.image.BufferedImage;

/**
 * HQ3X implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Hq3x
{
    /**
     * The raw scale implementation.
     */
    private static final class RawScale3x
    {
        /** Source data array. */
        private final int[] srcImage;
        /** Destination data array. */
        private final int[] dstImage;
        /** Width. */
        private final int width;
        /** Height. */
        private final int height;

        /**
         * Constructor.
         * 
         * @param imageData The image data array.
         * @param dataWidth The data width.
         * @param dataHeight The data height.
         */
        RawScale3x(int[] imageData, int dataWidth, int dataHeight)
        {
            width = dataWidth;
            height = dataHeight;
            srcImage = imageData;
            dstImage = new int[imageData.length * 9];
        }

        /**
         * Check the difference.
         * 
         * @param a The color a.
         * @param b The color b.
         * @return <code>true</code> if different, <code>false</code> else.
         */
        private static boolean different(int a, int b)
        {
            return a != b;
        }

        /**
         * Set destination pixel.
         * 
         * @param x location x.
         * @param y location y.
         * @param p pixel destination value.
         */
        private void setDestPixel(int x, int y, int p)
        {
            dstImage[x + y * width * 3] = p;
        }

        /**
         * Get pixel source.
         * 
         * @param x The location x.
         * @param y The location y.
         * @return The pixel value found.
         */
        private int getSourcePixel(int x, int y)
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
         * @param x The location x.
         * @param y The location y.
         */
        private void process(int x, int y)
        {
            final int a = getSourcePixel(x - 1, y - 1);
            final int b = getSourcePixel(x, y - 1);
            final int c = getSourcePixel(x + 1, y - 1);
            final int d = getSourcePixel(x - 1, y);
            final int e = getSourcePixel(x, y);
            final int f = getSourcePixel(x + 1, y);
            final int g = getSourcePixel(x - 1, y + 1);
            final int h = getSourcePixel(x, y + 1);
            final int i = getSourcePixel(x + 1, y + 1);
            int e0 = e;
            int e1 = e;
            int e2 = e;
            int e3 = e;
            int e4 = e;
            int e5 = e;
            int e6 = e;
            int e7 = e;
            int e8 = e;

            if (RawScale3x.different(b, h) && RawScale3x.different(d, f))
            {
                e0 = !RawScale3x.different(d, b) ? d : e;
                e1 = !RawScale3x.different(d, b) && RawScale3x.different(e, c) || !RawScale3x.different(b, f)
                        && RawScale3x.different(e, a) ? b : e;
                e2 = !RawScale3x.different(b, f) ? f : e;
                e3 = !RawScale3x.different(d, b) && RawScale3x.different(e, g) || !RawScale3x.different(d, h)
                        && RawScale3x.different(e, a) ? d : e;
                e4 = e;
                e5 = !RawScale3x.different(b, f) && RawScale3x.different(e, i) || !RawScale3x.different(h, f)
                        && RawScale3x.different(e, c) ? f : e;
                e6 = !RawScale3x.different(d, h) ? d : e;
                e7 = !RawScale3x.different(d, h) && RawScale3x.different(e, i) || !RawScale3x.different(h, f)
                        && RawScale3x.different(e, g) ? h : e;
                e8 = !RawScale3x.different(h, f) ? f : e;
            }

            setDestPixel(x * 3, y * 3, e0);
            setDestPixel(x * 3 + 1, y * 3, e1);
            setDestPixel(x * 3 + 2, y * 3, e2);
            setDestPixel(x * 3, y * 3 + 1, e3);
            setDestPixel(x * 3 + 1, y * 3 + 1, e4);
            setDestPixel(x * 3 + 2, y * 3 + 1, e5);
            setDestPixel(x * 3, y * 3 + 2, e6);
            setDestPixel(x * 3 + 1, y * 3 + 2, e7);
            setDestPixel(x * 3 + 2, y * 3 + 2, e8);
        }

        /**
         * Get the scaled data.
         * 
         * @return The data array.
         */
        public int[] getScaledData()
        {
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    process(x, y);
                }
            }

            return dstImage;
        }
    }

    /** Source data array. */
    private final int[] srcData;
    /** Width. */
    private final int width;
    /** Height. */
    private final int height;

    /**
     * Constructor.
     * 
     * @param srcImage The buffer source.
     */
    Hq3x(BufferedImage srcImage)
    {
        width = srcImage.getWidth();
        height = srcImage.getHeight();
        srcData = new int[width * height];
        srcImage.getRGB(0, 0, width, height, srcData, 0, width);
    }

    /**
     * Filtered buffer.
     * 
     * @return The filtered buffer.
     */
    BufferedImage getScaledImage()
    {
        final RawScale3x scaler = new RawScale3x(srcData, width, height);
        final BufferedImage image = new BufferedImage(width * 3, height * 3, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width * 3, height * 3, scaler.getScaledData(), 0, width * 3);
        return image;
    }
}
