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
package com.b3dgs.lionengine.graphic.engine;

import java.util.Arrays;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;

/**
 * Rasterbar implementation.
 */
final class RasterbarImpl implements Rasterbar
{
    /** No alpha clamp. */
    private static final int NO_ALPHA = 0x00FFFFFF;
    /** Palette colors. */
    private static final int COLORS = 256;
    private static final int SHIFT = 16;
    private static final int MAX_TRIES = 100_000;

    private final Graphic graphic;

    private int phSize;
    private int phBits;
    private int[] phKeys;
    private int[][] phVals;

    private final boolean[] highByteFilter = new boolean[Constant.UNSIGNED_BYTE];

    private int paletteSize;
    private int[] paletteColors;
    private int[][] paletteGradients;

    private int phMult;
    private int[] yTable;

    private int cachedH = -1;
    private int cachedY1;
    private int cachedOffsetY;
    private int cachedFactorY;

    private ImageBuffer buf;
    private int w;
    private int h;

    private int y1;
    private int marginY;
    private int offsetY;
    private int factorY;

    private Renderable renderer = RenderableVoid.getInstance();

    /**
     * Constructor.
     * 
     * @param graphic The graphic reference.
     */
    RasterbarImpl(Graphic graphic)
    {
        this.graphic = graphic;
    }

    /**
     * Init data.
     * 
     * @param w The source width.
     * @param h The source height.
     * @param buf The render buffer.
     */
    void init(int w, int h, ImageBuffer buf)
    {
        this.w = w;
        this.h = h;
        this.buf = buf;
    }

    @Override
    public void clearRasterbarColor()
    {
        renderer = RenderableVoid.getInstance();
        paletteColors = null;
        paletteGradients = null;
    }

    @Override
    public void addRasterbarColor(ImageBuffer buffer)
    {
        renderer = this::render;

        if (paletteColors == null)
        {
            paletteColors = new int[COLORS];
            paletteGradients = new int[COLORS][];
        }

        final int bw = buffer.getWidth();
        final int bh = buffer.getHeight();

        for (int bx = 0; bx < bw; bx++)
        {
            final int p = buffer.getRgb(bx, 0) & NO_ALPHA;

            int slot = -1;
            for (int s = 0; s < paletteSize; s++)
            {
                if (paletteColors[s] == p)
                {
                    slot = s;
                    break;
                }
            }

            if (slot == -1)
            {
                slot = paletteSize++;
                paletteColors[slot] = p;
                paletteGradients[slot] = new int[bh - 1];
            }

            final int[] v = paletteGradients[slot];
            for (int by = 0; by < bh - 1; by++)
            {
                v[by] = buffer.getRgb(bx, by + 1);
            }
        }
    }

    @Override
    public void prepare()
    {
        Arrays.fill(highByteFilter, false);

        for (int s = 0; s < paletteSize; s++)
        {
            highByteFilter[paletteColors[s] >>> SHIFT & 0xFF] = true;
        }

        // CHECKSTYLE IGNORE LINE: MagicNumber
        phSize = Integer.highestOneBit(paletteSize * 4 - 1) << 1;
        phBits = Integer.numberOfTrailingZeros(phSize);
        phKeys = new int[phSize];
        phVals = new int[phSize][];

        outer:
        for (int mult = 1, tries = 0; tries < MAX_TRIES; mult += 2, tries++)
        {
            final boolean[] used = new boolean[phSize];
            for (int s = 0; s < paletteSize; s++)
            {
                final int idx = mix(paletteColors[s]) * mult >>> 32 - phBits;
                if (used[idx])
                {
                    continue outer;
                }
                used[idx] = true;
            }

            phMult = mult;
            Arrays.fill(phKeys, -1);
            for (int s = 0; s < paletteSize; s++)
            {
                final int idx = mix(paletteColors[s]) * mult >>> 32 - phBits;
                phKeys[idx] = paletteColors[s];
                phVals[idx] = paletteGradients[s];
            }
            return;
        }
        throw new IllegalStateException("Hash cannot be found " + paletteSize + " colors");
    }

    @Override
    public void setRasterbarOffset(int offsetY, int factorY)
    {
        this.offsetY = offsetY;
        this.factorY = factorY;
    }

    @Override
    public void setRasterbarY(int y1, int y2)
    {
        this.y1 = y1;
        marginY = y2 - y1;
    }

    @Override
    public void renderRasterbar()
    {
        renderer.render(graphic);
    }

    /**
     * Render.
     * 
     * @param g The graphic output.
     */
    // CHECKSTYLE IGNORE LINE: CyclomaticComplexity
    private void render(Graphic g)
    {
        updateCache();

        final int[] bu = buf.getRgbRef();
        final int n = bu.length;
        final int w = this.w;
        final boolean[] hbf = highByteFilter;
        int rowStart = 0;

        for (int y = h; y >= 0 && rowStart < n; y--)
        {
            final int lineEnd = Math.min(rowStart + w, n);
            if (y < marginY)
            {
                for (int i = rowStart; i < lineEnd; i++)
                {
                    final int pixel = bu[i] & NO_ALPHA;
                    if (hbf[pixel >>> SHIFT & 0xFF])
                    {
                        final int[] k = hashGet(pixel);
                        // CHECKSTYLE IGNORE LINE: NestedIfDepth
                        if (k != null)
                        {
                            bu[i] = k[0];
                        }
                    }
                }
            }
            else
            {
                final int yr = yTable[y];

                for (int i = rowStart; i < lineEnd; i++)
                {
                    if (hbf[bu[i] >>> SHIFT & 0xFF])
                    {
                        final int[] k = hashGet(bu[i] & NO_ALPHA);
                        // CHECKSTYLE IGNORE LINE: NestedIfDepth
                        if (k != null)
                        {
                            final int len = k.length;
                            // CHECKSTYLE IGNORE LINE: NestedIfDepth
                            if (len > 1)
                            {
                                final int v = k[yr < len ? yr : len - 1];
                                // CHECKSTYLE IGNORE LINE: NestedIfDepth
                                if (v != Integer.MIN_VALUE)
                                {
                                    bu[i] = v;
                                }
                            }
                        }
                    }
                }
            }
            rowStart += w;
        }
    }

    private void updateCache()
    {
        if (h != cachedH || y1 != cachedY1 || offsetY != cachedOffsetY || factorY != cachedFactorY)
        {
            if (yTable == null || yTable.length != h + 1)
            {
                yTable = new int[h + 1];
            }

            for (int y = 0; y <= h; y++)
            {
                final int r = (y1 + y + offsetY) / factorY;
                yTable[y] = r < 1 ? 1 : r;
            }

            cachedH = h;
            cachedY1 = y1;
            cachedOffsetY = offsetY;
            cachedFactorY = factorY;
        }
    }

    private int[] hashGet(int color)
    {
        final int slot = mix(color) * phMult >>> 32 - phBits;
        return phKeys[slot] == color ? phVals[slot] : null;
    }

    // CHECKSTYLE OFF: MagicNumber
    private static int mix(int a)
    {
        int c = a;
        c ^= c >>> 8;
        c *= 0x9E3779B9;
        c ^= c >>> 11;
        return c;
    }
    // CHECKSTYLE ON: MagicNumber
}
