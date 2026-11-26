/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.render.awt;

import com.b3dgs.lionengine.render.CpuFilter;

/**
 * Scale2x (AdvMAME2x) pixel-art scaler, standing in for {@code HQ2X} until its license record (ADR-0025, ADR-0033,
 * ADR-0063, REQ-FILTER-004).
 *
 * <p>
 * Each source pixel {@code E} becomes four target pixels, from its neighbours {@code B} (above), {@code D} (left),
 * {@code F} (right) and {@code H} (below); at the edges a missing neighbour is {@code E}. When {@code B != H} and
 * {@code D != F}, the top-left pixel is {@code D} if {@code D == B}, the top-right {@code F} if {@code B == F}, the
 * bottom-left {@code D} if {@code D == H} and the bottom-right {@code F} if {@code H == F}, each one {@code E}
 * otherwise; in every other case the four are {@code E}. Allocates nothing. Stateless and thread-safe.
 * </p>
 */
final class Scale2x implements CpuFilter
{
    /**
     * Creates the filter.
     */
    Scale2x()
    {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If the target is not twice the source on each axis, or a buffer is smaller
     *             than its size.
     */
    @Override
    public void apply(int[] source, int sourceWidth, int sourceHeight, int[] target, int targetWidth, int targetHeight)
    {
        if (targetWidth != 2 * sourceWidth
            || targetHeight != 2 * sourceHeight
            || source.length < sourceWidth * sourceHeight
            || target.length < targetWidth * targetHeight)
        {
            throw new IllegalArgumentException("Scale2x needs a target twice the source: "
                                               + sourceWidth
                                               + 'x'
                                               + sourceHeight
                                               + " to "
                                               + targetWidth
                                               + 'x'
                                               + targetHeight);
        }
        for (int y = 0; y < sourceHeight; y++)
        {
            for (int x = 0; x < sourceWidth; x++)
            {
                scale(source, sourceWidth, sourceHeight, x, y, target);
            }
        }
    }

    /**
     * Picks the side neighbour when it equals the vertical one, the center otherwise.
     *
     * @param side The left or right neighbour.
     * @param vertical The neighbour above or below.
     * @param center The center pixel.
     * @return The target pixel.
     */
    private static int pick(int side, int vertical, int center)
    {
        return side == vertical ? side : center;
    }

    /**
     * Writes the four target pixels of one source pixel.
     *
     * @param source The source pixels.
     * @param width The source width.
     * @param height The source height.
     * @param x The source column.
     * @param y The source row.
     * @param target The target pixels, twice the source width.
     */
    private static void scale(int[] source, int width, int height, int x, int y, int[] target)
    {
        final int index = y * width + x;
        final int e = source[index];
        final int b = y > 0 ? source[index - width] : e;
        final int h = y < height - 1 ? source[index + width] : e;
        final int d = x > 0 ? source[index - 1] : e;
        final int f = x < width - 1 ? source[index + 1] : e;
        final int top = 2 * y * 2 * width + 2 * x;
        final int bottom = top + 2 * width;
        if (b != h && d != f)
        {
            target[top] = pick(d, b, e);
            target[top + 1] = pick(f, b, e);
            target[bottom] = pick(d, h, e);
            target[bottom + 1] = pick(f, h, e);
        }
        else
        {
            target[top] = e;
            target[top + 1] = e;
            target[bottom] = e;
            target[bottom + 1] = e;
        }
    }
}
