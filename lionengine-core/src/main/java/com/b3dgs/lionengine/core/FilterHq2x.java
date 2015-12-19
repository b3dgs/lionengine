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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.Transform;
import com.b3dgs.lionengine.Transparency;

/**
 * HQ2X implementation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class FilterHq2x implements Filter
{
    /** Scale factor. */
    public static final int SCALE = 2;

    /**
     * Create an Hq2x filter.
     */
    public FilterHq2x()
    {
        // Nothing to do
    }

    /*
     * Filter
     */

    @Override
    public ImageBuffer filter(ImageBuffer source)
    {
        final int width = source.getWidth();
        final int height = source.getHeight();
        final int[] srcData = new int[width * height];
        source.getRgb(0, 0, width, height, srcData, 0, width);

        final RawScale2x scaler = new RawScale2x(srcData, width, height);
        final ImageBuffer image = Graphics.createImageBuffer(width * SCALE, height * SCALE, Transparency.OPAQUE);
        image.setRgb(0, 0, width * SCALE, height * SCALE, scaler.getScaledData(), 0, width * SCALE);

        return image;
    }

    @Override
    public Transform getTransform(double scaleX, double scaleY)
    {
        final Transform transform = Graphics.createTransform();
        transform.scale(scaleX / SCALE, scaleY / SCALE);
        return transform;
    }

    /**
     * The raw scale implementation.
     */
    private static final class RawScale2x
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
         * Internal constructor.
         * 
         * @param imageData The image data array.
         * @param dataWidth The data width.
         * @param dataHeight The data height.
         */
        RawScale2x(int[] imageData, int dataWidth, int dataHeight)
        {
            width = dataWidth;
            height = dataHeight;
            srcImage = imageData;
            final int doubleSize = SCALE * SCALE;
            dstImage = new int[imageData.length * doubleSize];
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
         * @param x The location x.
         * @param y The location y.
         * @param p The pixel destination value.
         */
        private void setDestPixel(int x, int y, int p)
        {
            dstImage[x + y * width * SCALE] = p;
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
            final int b = getSourcePixel(x, y - 1);
            final int d = getSourcePixel(x - 1, y);
            final int e = getSourcePixel(x, y);
            final int f = getSourcePixel(x + 1, y);
            final int h = getSourcePixel(x, y + 1);
            int e0 = e;
            int e1 = e;
            int e2 = e;
            int e3 = e;
            if (RawScale2x.different(b, h) && RawScale2x.different(d, f))
            {
                e0 = !RawScale2x.different(d, b) ? d : e;
                e1 = !RawScale2x.different(b, f) ? f : e;
                e2 = !RawScale2x.different(d, h) ? d : e;
                e3 = !RawScale2x.different(h, f) ? f : e;
            }

            setDestPixel(x * SCALE, y * SCALE, e0);
            setDestPixel(x * SCALE + 1, y * SCALE, e1);
            setDestPixel(x * SCALE, y * SCALE + 1, e2);
            setDestPixel(x * SCALE + 1, y * SCALE + 1, e3);
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
}
