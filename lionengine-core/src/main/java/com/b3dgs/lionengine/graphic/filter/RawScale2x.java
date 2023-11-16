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
final class RawScale2x extends RawScale
{
    /** Scale factor. */
    public static final int SCALE = 2;
    /** Scale square factor. */
    public static final int SCALE_S = SCALE * SCALE;

    /**
     * Internal constructor.
     * 
     * @param dataWidth The data width.
     * @param dataHeight The data height.
     */
    RawScale2x(int dataWidth, int dataHeight)
    {
        super(SCALE_S, dataWidth, dataHeight);
    }

    // CHECKSTYLE OFF: MagicNumber
    @Override
    protected void process(int[] srcImage, int start, int end)
    {
        for (int y = start; y < end; y++)
        {
            for (int x = 0; x < width; x++)
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

                dstImage[x * SCALE + y * SCALE * width * SCALE] = e0;
                dstImage[x * SCALE + 1 + y * SCALE * width * SCALE] = e1;
                dstImage[x * SCALE + (y * SCALE + 1) * width * SCALE] = e2;
                dstImage[x * SCALE + 1 + (y * SCALE + 1) * width * SCALE] = e3;
            }
        }
    }
    // CHECKSTYLE ON: MagicNumber
}
