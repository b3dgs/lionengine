/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
final class RawScale3x extends RawScale
{
    /** Scale factor. */
    public static final int SCALE = 3;
    /** Scale square factor. */
    public static final int SCALE_S = SCALE * SCALE;

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
        if (d != b)
        {
            return d;
        }
        return e;
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
        if (d == b && e != c || b == f && e != a)
        {
            return b;
        }
        return e;
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
        if (b == f)
        {
            return f;
        }
        return e;
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
        if (d == b && e != g || d == h && e != a)
        {
            return d;
        }
        return e;
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
        if (b == f && e != i || h == f && e != c)
        {
            return f;
        }
        return e;
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
        if (d == h)
        {
            return d;
        }
        return e;
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
        if (d == h && e != i || h == f && e != g)
        {
            return h;
        }
        return e;
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
        if (h == f)
        {
            return f;
        }
        return e;
    }

    /**
     * Internal constructor.
     * 
     * @param dataWidth The data width.
     * @param dataHeight The data height.
     */
    RawScale3x(int dataWidth, int dataHeight)
    {
        super(SCALE_S, dataWidth, dataHeight);
    }

    /**
     * Set destination pixel.
     * 
     * @param dstImage The image destination.
     * @param x location x.
     * @param y location y.
     * @param p pixel destination value.
     */
    private void setDestPixel(int[] dstImage, int x, int y, int p)
    {
        dstImage[x + y * width * SCALE] = p;
    }

    // CHECKSTYLE OFF: MagicNumber
    // CHECKSTYLE IGNORE LINE: Count
    @Override
    protected void process(int[] srcImage, int start, int end)
    {
        for (int y = start; y < end; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int a = getSourcePixel(srcImage, x - 1, y - 1);
                final int b = getSourcePixel(srcImage, x, y - 1);
                final int c = getSourcePixel(srcImage, x + 1, y - 1);
                final int d = getSourcePixel(srcImage, x - 1, y);
                final int e = getSourcePixel(srcImage, x, y);
                final int f = getSourcePixel(srcImage, x + 1, y);
                final int g = getSourcePixel(srcImage, x - 1, y + 1);
                final int h = getSourcePixel(srcImage, x, y + 1);
                final int i = getSourcePixel(srcImage, x + 1, y + 1);
                int e0 = e;
                int e1 = e;
                int e2 = e;
                int e3 = e;
                int e4 = e;
                int e5 = e;
                int e6 = e;
                int e7 = e;
                int e8 = e;

                if (b != h && d != f)
                {
                    e0 = computeE0(b, d, e);
                    e1 = computeE1(a, b, c, d, e, f);
                    e2 = computeE2(b, e, f);
                    e3 = computeE3(a, b, d, e, g, h);
                    e4 = e;
                    e5 = computeE5(b, c, e, f, h, i);
                    e6 = computeE6(d, e, h);
                    e7 = computeE7(d, e, f, g, h, i);
                    e8 = computeE8(e, f, h);
                }

                setDestPixel(dstImage, x * SCALE, y * SCALE, e0);
                setDestPixel(dstImage, x * SCALE + 1, y * SCALE, e1);
                setDestPixel(dstImage, x * SCALE + 2, y * SCALE, e2);
                setDestPixel(dstImage, x * SCALE, y * SCALE + 1, e3);
                setDestPixel(dstImage, x * SCALE + 1, y * SCALE + 1, e4);
                setDestPixel(dstImage, x * SCALE + 2, y * SCALE + 1, e5);
                setDestPixel(dstImage, x * SCALE, y * SCALE + 2, e6);
                setDestPixel(dstImage, x * SCALE + 1, y * SCALE + 2, e7);
                setDestPixel(dstImage, x * SCALE + 2, y * SCALE + 2, e8);
            }
        }
        // CHECKSTYLE ON: MagicNumber
    }
}
