/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * The raw scale implementation.
 */
final class RawScale2x
{
    /** Scale factor. */
    public static final int SCALE = 2;
    /** Scale square factor. */
    public static final int SCALE_S = SCALE * SCALE;

    private static final int MAX_PARALLEL = 4;
    private static final int COUNT = Runtime.getRuntime().availableProcessors() > MAX_PARALLEL ? MAX_PARALLEL : 1;

    private final ExecutorService exec = COUNT > 1 ? Executors.newFixedThreadPool(COUNT) : null;
    private final Function<int[], int[]> get = COUNT > 1 ? this::getScaledDataParallel : this::getScaledDataSingle;
    /** Width. */
    private final int width;
    /** Height. */
    private final int height;
    /** Cache. */
    private int[] dstImage = new int[0];
    private final int th;

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
        th = height / COUNT;
    }

    /**
     * Close tasks.
     */
    void close()
    {
        if (exec != null)
        {
            exec.shutdown();
            try
            {
                exec.awaitTermination(1, TimeUnit.SECONDS);
            }
            catch (@SuppressWarnings("unused") final InterruptedException exception)
            {
                // Skip
            }
        }
    }

    /**
     * Get the scaled data.
     * 
     * @param srcImage The image source
     * @return The data array.
     */
    int[] getScaledData(int[] srcImage)
    {
        return get.apply(srcImage);
    }

    /**
     * Get the scaled data.
     * 
     * @param srcImage The image source
     * @return The data array.
     */
    private int[] getScaledDataSingle(int[] srcImage)
    {
        final int l = srcImage.length * SCALE_S;
        if (dstImage.length != l)
        {
            dstImage = new int[l];
        }
        process(srcImage, 0, height);

        return dstImage;
    }

    /**
     * Get the scaled data.
     * 
     * @param srcImage The image source.
     * @return The data array.
     */
    private int[] getScaledDataParallel(int[] srcImage)
    {
        final int l = srcImage.length * SCALE_S;
        if (dstImage.length != l)
        {
            dstImage = new int[l];
        }

        final CountDownLatch latch = new CountDownLatch(COUNT);
        for (int i = 0; i < COUNT; i++)
        {
            final int start = th * i;
            final int end;
            if (i == COUNT - 1)
            {
                end = height;
            }
            else
            {
                end = Math.min(start + th, height);
            }

            exec.execute(() ->
            {
                process(srcImage, start, end);
                latch.countDown();
            });
        }
        try
        {
            latch.await(1, TimeUnit.SECONDS);
        }
        catch (@SuppressWarnings("unused") final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
        }

        return dstImage;
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

    // CHECKSTYLE OFF: MagicNumber
    private void process(int[] srcImage, int start, int end)
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
