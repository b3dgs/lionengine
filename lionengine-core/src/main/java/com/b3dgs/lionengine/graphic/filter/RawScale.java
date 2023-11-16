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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * The raw scale base.
 */
abstract class RawScale
{
    private static final int MAX_PARALLEL = 4;
    private static final int COUNT = Runtime.getRuntime().availableProcessors() > MAX_PARALLEL ? MAX_PARALLEL : 1;

    /** Cache. */
    protected int[] dstImage = new int[0];
    /** Width. */
    protected final int width;
    /** Height. */
    protected final int height;

    private final int scaleSquare;
    private final ExecutorService exec;
    private final Function<int[], int[]> get = COUNT > 1 ? this::getScaledDataParallel : this::getScaledDataSingle;
    private final int th;

    /**
     * Internal constructor.
     * 
     * @param scaleSquare The scale squared value.
     * @param dataWidth The data width.
     * @param dataHeight The data height.
     */
    protected RawScale(int scaleSquare, int dataWidth, int dataHeight)
    {
        super();

        this.scaleSquare = scaleSquare;
        exec = COUNT > 1 ? Executors.newFixedThreadPool(COUNT, r -> new Thread(r, getClass().getSimpleName())) : null;

        width = dataWidth;
        height = dataHeight;
        th = height / COUNT;
    }

    /**
     * Process image.
     * 
     * @param srcImage The source image.
     * @param start The vertical line start.
     * @param end The vertical line end.
     */
    protected abstract void process(int[] srcImage, int start, int end);

    /**
     * Get pixel source.
     * 
     * @param srcImage The image source.
     * @param x The location x.
     * @param y The location y.
     * @return The pixel value found.
     */
    protected int getSourcePixel(int[] srcImage, int x, int y)
    {
        int x1 = Math.max(0, x);
        x1 = Math.min(width - 1, x1);
        int y1 = Math.max(0, y);
        y1 = Math.min(height - 1, y1);

        return srcImage[x1 + y1 * width];
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
        final int l = srcImage.length * scaleSquare;
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
     * @param srcImage The image source
     * @return The data array.
     */
    private int[] getScaledDataParallel(int[] srcImage)
    {
        final int l = srcImage.length * scaleSquare;
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
}
