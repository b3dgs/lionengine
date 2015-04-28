/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;

/**
 * HQ3X implementation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Hq3x
{
    /**
     * The raw scale implementation.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    private static final class RawScale3x
    {
        /**
         * Compute E0 pixel.
         * 
         * @param b The b value.
         * @param d The d value.
         * @param e The e value.
         * @return The computed value.
         */
        private static int computeE0(int b, int d, int e)
        {
            return !RawScale3x.different(d, b) ? d : e;
        }

        /**
         * Compute E1 pixel.
         * 
         * @param a The a value.
         * @param b The b value.
         * @param c The c value.
         * @param d The d value.
         * @param e The e value.
         * @param f The f value.
         * @return The computed value.
         */
        private static int computeE1(int a, int b, int c, int d, int e, int f)
        {
            return !RawScale3x.different(d, b) && RawScale3x.different(e, c) || !RawScale3x.different(b, f)
                    && RawScale3x.different(e, a) ? b : e;
        }

        /**
         * Compute E2 pixel.
         * 
         * @param b The b value.
         * @param e The e value.
         * @param f The f value.
         * @return The computed value.
         */
        private static int computeE2(int b, int e, int f)
        {
            return !RawScale3x.different(b, f) ? f : e;
        }

        /**
         * Compute E3 pixel.
         * 
         * @param a The a value.
         * @param b The b value.
         * @param d The d value.
         * @param e The e value.
         * @param g The g value.
         * @param h The h value.
         * @return The computed value.
         */
        private static int computeE3(int a, int b, int d, int e, int g, int h)
        {
            return !RawScale3x.different(d, b) && RawScale3x.different(e, g) || !RawScale3x.different(d, h)
                    && RawScale3x.different(e, a) ? d : e;
        }

        /**
         * Compute E5 pixel.
         * 
         * @param b The b value.
         * @param c The c value.
         * @param e The e value.
         * @param f The f value.
         * @param h The h value.
         * @param i The i value.
         * @return The computed value.
         */
        private static int computeE5(int b, int c, int e, int f, int h, int i)
        {
            return !RawScale3x.different(b, f) && RawScale3x.different(e, i) || !RawScale3x.different(h, f)
                    && RawScale3x.different(e, c) ? f : e;
        }

        /**
         * Compute E6 pixel.
         * 
         * @param d The d value.
         * @param e The e value.
         * @param h The h value.
         * @return The computed value.
         */
        private static int computeE6(int d, int e, int h)
        {
            return !RawScale3x.different(d, h) ? d : e;
        }

        /**
         * Compute E7 pixel.
         * 
         * @param d The d value.
         * @param e The e value.
         * @param f The f value.
         * @param g The g value.
         * @param h The h value.
         * @param i The i value.
         * @return The computed value.
         */
        private static int computeE7(int d, int e, int f, int g, int h, int i)
        {
            return !RawScale3x.different(d, h) && RawScale3x.different(e, i) || !RawScale3x.different(h, f)
                    && RawScale3x.different(e, g) ? h : e;
        }

        /**
         * Compute E8 pixel.
         * 
         * @param e The e value.
         * @param f The f value.
         * @param h The h value.
         * @return The computed value.
         */
        private static int computeE8(int e, int f, int h)
        {
            return !RawScale3x.different(h, f) ? f : e;
        }

        /** Source data array. */
        private final int[] srcImage;
        /** Destination data array. */
        private final int[] dstImage;
        /** Width. */
        private final int width;
        /** Height. */
        private final int height;

        /**
         * Internal constructor.
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
                e0 = RawScale3x.computeE0(b, d, e);
                e1 = RawScale3x.computeE1(a, b, c, d, e, f);
                e2 = RawScale3x.computeE2(b, e, f);
                e3 = RawScale3x.computeE3(a, b, d, e, g, h);
                e4 = e;
                e5 = RawScale3x.computeE5(b, c, e, f, h, i);
                e6 = RawScale3x.computeE6(d, e, h);
                e7 = RawScale3x.computeE7(d, e, f, g, h, i);
                e8 = RawScale3x.computeE8(e, f, h);
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
     * Create an Hq3x filter.
     * 
     * @param srcImage The buffer source.
     */
    public Hq3x(ImageBuffer srcImage)
    {
        width = srcImage.getWidth();
        height = srcImage.getHeight();
        srcData = new int[width * height];
        srcImage.getRgb(0, 0, width, height, srcData, 0, width);
    }

    /**
     * Filtered buffer.
     * 
     * @return The filtered buffer.
     */
    public ImageBuffer getScaledImage()
    {
        final RawScale3x scaler = new RawScale3x(srcData, width, height);
        final ImageBuffer image = Graphics.createImageBuffer(width * 3, height * 3, Transparency.OPAQUE);
        image.setRgb(0, 0, width * 3, height * 3, scaler.getScaledData(), 0, width * 3);
        return image;
    }
}
