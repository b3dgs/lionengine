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
package com.b3dgs.lionengine.core.filter;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transform;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Blur filter implementation.
 */
public class FilterBlur implements Filter
{
    /** Clamp edges. */
    public static final int CLAMP_EDGES = 0;
    /** Wrap edges. */
    public static final int WRAP_EDGES = 1;
    /** Default radius. */
    private static final float RADIUS_DEFAULT = 1.5F;

    /** Current radius. */
    private volatile float radius;
    /** Alpha flag. */
    private volatile boolean alpha;
    /** Edge mode. */
    private volatile int edge;

    /**
     * Create the filter.
     */
    public FilterBlur()
    {
        radius = RADIUS_DEFAULT;
        alpha = true;
        edge = CLAMP_EDGES;
    }

    /**
     * Set the radius value.
     * 
     * @param radius The radius value.
     */
    public void setRadius(float radius)
    {
        this.radius = radius;
    }

    /**
     * Set the alpha flag.
     * 
     * @param alpha The alpha flag.
     */
    public void setAlpha(boolean alpha)
    {
        this.alpha = alpha;
    }

    /**
     * Set the edge mode.
     * 
     * @param edge The edge mode.
     * @see #CLAMP_EDGES
     * @see #WRAP_EDGES
     */
    public void setEdgeMode(int edge)
    {
        this.edge = edge;
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

        final Kernel kernel = createKernel(radius, width, height);
        compute(kernel, inPixels, outPixels, width, height, alpha, edge);
        compute(kernel, outPixels, inPixels, height, width, alpha, edge);

        final ImageBuffer dest = Graphics.createImageBuffer(width, height, source.getTransparency());
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

    /**
     * Compute blur.
     * 
     * @param kernel The kernel used.
     * @param in The source pixels.
     * @param out The destination pixels.
     * @param width The image width.
     * @param height The image height.
     * @param alpha The alpha flag.
     * @param edge The edge flag.
     */
    private static void compute(Kernel kernel, int[] in, int[] out, int width, int height, boolean alpha, int edge)
    {
        final float[] matrix = kernel.getMatrix();
        final int cols = kernel.getWidth();
        final int cols2 = cols / 2;

        for (int y = 0; y < height; y++)
        {
            int index = y;
            final int ioffset = y * width;
            for (int x = 0; x < width; x++)
            {
                float r = 0;
                float g = 0;
                float b = 0;
                float a = 0;

                final int moffset = cols2;
                for (int col = -cols2; col <= cols2; col++)
                {
                    final float f = matrix[moffset + col];
                    if (Double.doubleToRawLongBits(f) != 0L)
                    {
                        final int ix = checkEdge(width, x, col, edge);
                        final int rgb = in[ioffset + ix];
                        a += f * (rgb >> Constant.BYTE_4 & 0xff);
                        r += f * (rgb >> Constant.BYTE_3 & 0xff);
                        g += f * (rgb >> Constant.BYTE_2 & 0xff);
                        b += f * (rgb & 0xff);
                    }
                }
                final int ia;
                if (alpha)
                {
                    ia = clamp((int) (a + Constant.HALF));
                }
                else
                {
                    ia = 0xff;
                }
                final int ir = clamp((int) (r + 0.5));
                final int ig = clamp((int) (g + 0.5));
                final int ib = clamp((int) (b + 0.5));
                out[index] = ia << Constant.BYTE_4 | ir << Constant.BYTE_3 | ig << Constant.BYTE_2 | ib;
                index += height;
            }
        }
    }

    /**
     * Check the edge value.
     * 
     * @param width The image width.
     * @param x The current horizontal pixel.
     * @param col The column size.
     * @param edge The edge flag.
     * @return The edge offset.
     */
    private static int checkEdge(int width, int x, int col, int edge)
    {
        int ix = x + col;
        if (ix < 0)
        {
            if (edge == CLAMP_EDGES)
            {
                ix = 0;
            }
            else
            {
                ix = (x + width) % width;
            }
        }
        else if (ix >= width)
        {
            if (edge == CLAMP_EDGES)
            {
                ix = width - 1;
            }
            else
            {
                ix = (x + width) % width;
            }
        }
        return ix;
    }

    /**
     * Create a blur kernel.
     * 
     * @param radius The blur radius.
     * @param width The image width.
     * @param height The image height.
     * @return The blur kernel.
     */
    private static Kernel createKernel(float radius, int width, int height)
    {
        final int r = (int) Math.ceil(radius);
        final int rows = r * 2 + 1;
        final float[] matrix = new float[rows];
        final float sigma = radius / 3;
        final float sigma22 = 2 * sigma * sigma;
        final float sigmaPi2 = (float) (2 * Math.PI * sigma);
        final float sqrtSigmaPi2 = (float) Math.sqrt(sigmaPi2);
        final float radius2 = radius * radius;
        float total = 0;
        int index = 0;
        for (int row = -r; row <= r; row++)
        {
            final float distance = row * (float) row;
            if (distance > radius2)
            {
                matrix[index] = 0;
            }
            else
            {
                matrix[index] = (float) Math.exp(-distance / sigma22) / sqrtSigmaPi2;
            }
            total += matrix[index];
            index++;
        }
        if (total < 0 || total > 0)
        {
            for (int i = 0; i < rows; i++)
            {
                matrix[i] /= total;
            }
        }

        final float[] data = new float[width * height];
        System.arraycopy(matrix, 0, data, 0, rows);

        return new Kernel(rows, data);
    }

    /**
     * Clamp a value to the range 0-255.
     * 
     * @param value The value to clamp.
     * @return The clamped value.
     */
    private static int clamp(int value)
    {
        return UtilMath.clamp(value, 0, 255);
    }

    /**
     * Local kernel representation.
     */
    private static class Kernel
    {
        /** Kernel width. */
        private final int width;
        /** Kernel matrix. */
        private final float[] matrix;

        /**
         * Create the kernel.
         * 
         * @param width The kernel width.
         * @param matrix The kernel matrix.
         */
        Kernel(int width, float[] matrix)
        {
            this.width = width;
            this.matrix = matrix;
        }

        /**
         * Get the matrix width.
         * 
         * @return The matrix width.
         */
        public int getWidth()
        {
            return width;
        }

        /**
         * Get the kernel matrix.
         * 
         * @return The kernel matrix.
         */
        public float[] getMatrix()
        {
            return matrix;
        }
    }
}
