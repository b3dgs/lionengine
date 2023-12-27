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

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.graphic.ColorRgba;

/**
 * The CRT scale implementation.
 */
final class CrtScale
{
    private static final double QUART = Constant.QUART;
    private static final int COUNT = Math.max(1, Runtime.getRuntime().availableProcessors() / 2 - 1);

    private static final ColorRgba C = new ColorRgba(100, 100, 100, 255);
    private static final ColorRgba C1 = new ColorRgba(100, 80, 80, 255);
    private static final ColorRgba C2 = new ColorRgba(80, 100, 80, 255);
    private static final ColorRgba C3 = new ColorRgba(80, 80, 100, 255);

    // CHECKSTYLE OFF: BooleanExpressionComplexity|MagicNumber
    private static int rgb(int rgb1, int rgb2, int rgb3, int rgb4, ColorRgba color, double factor)
    {
        final int r = (int) (((rgb1 >> Constant.BYTE_3 & 0xFF) * factor
                              + (rgb2 >> Constant.BYTE_3 & 0xFF) * factor
                              + (rgb3 >> Constant.BYTE_3 & 0xFF) * factor
                              + (rgb4 >> Constant.BYTE_3 & 0xFF) * factor)
                             * (color.getRed() / 100.0));
        final int g = (int) (((rgb1 >> Constant.BYTE_2 & 0xFF) * factor
                              + (rgb2 >> Constant.BYTE_2 & 0xFF) * factor
                              + (rgb3 >> Constant.BYTE_2 & 0xFF) * factor
                              + (rgb4 >> Constant.BYTE_2 & 0xFF) * factor)
                             * (color.getGreen() / 100.0));
        final int b = (int) (((rgb1 >> Constant.BYTE_1 & 0xFF) * factor
                              + (rgb2 >> Constant.BYTE_1 & 0xFF) * factor
                              + (rgb3 >> Constant.BYTE_1 & 0xFF) * factor
                              + (rgb4 >> Constant.BYTE_1 & 0xFF) * factor)
                             * (color.getBlue() / 100.0));

        return -16_777_216
               | (r & 0xFF) << Constant.BYTE_3
               | (g & 0xFF) << Constant.BYTE_2
               | (b & 0xFF) << Constant.BYTE_1;
    }
    // CHECKSTYLE ON: BooleanExpressionComplexity|MagicNumber

    private final ExecutorService exec;
    private final Function<int[], int[]> get = COUNT > 1 ? this::getScaledParallel : this::getScaledSingle;
    private final int width;
    private final int height;
    private final int scale;
    private final int scale2;
    private final int th;

    private int[] dst = {};

    /**
     * Internal constructor.
     * 
     * @param dataWidth The data width.
     * @param dataHeight The data height.
     * @param scale The scale factor.
     */
    CrtScale(int dataWidth, int dataHeight, int scale)
    {
        super();

        exec = COUNT > 1 ? Executors.newFixedThreadPool(COUNT, r -> new Thread(r, getClass().getSimpleName())) : null;

        this.scale = scale;
        scale2 = scale * scale;
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
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Get the scaled data.
     * 
     * @param src The image source
     * @return The data array.
     */
    int[] getScaled(int[] src)
    {
        return get.apply(src);
    }

    /**
     * Get the scaled data.
     * 
     * @param src The image source
     * @return The data array.
     */
    private int[] getScaledSingle(int[] src)
    {
        final int l = src.length * scale2;
        if (dst.length != l)
        {
            dst = new int[l];
        }
        process(src, 0, height);

        return dst;
    }

    /**
     * Get the scaled data.
     * 
     * @param src The image source.
     * @return The data array.
     */
    int[] getScaledParallel(int[] src)
    {
        final int l = src.length * scale2;
        if (dst.length != l)
        {
            dst = new int[l];
        }

        final CountDownLatch latch = new CountDownLatch(COUNT);
        for (int i = 0; i < COUNT; i++)
        {
            final int start;
            final int end;
            if (i == 0)
            {
                start = th * i;
            }
            else
            {
                start = (th - 1) * i;
            }
            if (i == COUNT - 1)
            {
                end = height;
            }
            else
            {
                end = Math.min(start + th, height - 1);
            }

            exec.execute(() ->
            {
                process(src, start, end);
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

        return dst;
    }

    // CHECKSTYLE OFF: MagicNumber|CyclomaticComplexity
    private void process(int[] src, int start, int end)
    {
        for (int y = start; y < end; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int dx = x * scale;
                final int dy = y * width * scale;
                final boolean evenX = x % 2 == 0;
                final boolean evenY = y % 2 == 0;

                if (evenX && evenY || y == 0 || y == height - 1)
                {
                    final int rgb = src[x + y * width];
                    final int avr = rgb(rgb, rgb, rgb, rgb, C, QUART);

                    dst[dx + dy * scale] = avr;
                    dst[dx + 1 + dy * scale] = avr;
                    dst[dx + (dy + width) * scale] = avr;
                    dst[dx + 1 + (dy + width) * scale] = avr;
                }
                else if (!evenX && evenY)
                {
                    dst[dx + dy * scale] = rgb(src[x - 1 + y * width],
                                               src[x + 1 + y * width],
                                               src[x + (y - 1) * width],
                                               src[x + (y + 1) * width],
                                               C1,
                                               QUART);
                    dst[dx + 1 + dy * scale] = rgb(src[x - 1 + y * width],
                                                   src[x + 1 + y * width],
                                                   src[x + (y - 1) * width],
                                                   src[x + (y + 1) * width],
                                                   C1,
                                                   QUART);
                    dst[dx + (dy + width) * scale] = rgb(src[x - 1 + y * width],
                                                         src[x + 1 + y * width],
                                                         src[x + (y - 1) * width],
                                                         src[x + (y + 1) * width],
                                                         C1,
                                                         QUART);
                    dst[dx + 1 + (dy + width) * scale] = rgb(src[x - 1 + y * width],
                                                             src[x + 1 + y * width],
                                                             src[x + (y - 1) * width],
                                                             src[x + (y + 1) * width],
                                                             C1,
                                                             QUART);
                }
                else if (evenX && !evenY)
                {
                    dst[dx + dy * scale] = rgb(src[x - 1 + y * width],
                                               src[x + 1 + y * width],
                                               src[x + (y - 1) * width],
                                               src[x + (y + 1) * width],
                                               C2,
                                               QUART);
                    dst[dx + 1 + dy * scale] = rgb(src[x - 1 + y * width],
                                                   src[x + 1 + y * width],
                                                   src[x + (y - 1) * width],
                                                   src[x + (y + 1) * width],
                                                   C2,
                                                   QUART);
                    dst[dx + (dy + width) * scale] = rgb(src[x - 1 + y * width],
                                                         src[x + 1 + y * width],
                                                         src[x + (y - 1) * width],
                                                         src[x + (y + 1) * width],
                                                         C2,
                                                         QUART);
                    dst[dx + 1 + (dy + width) * scale] = rgb(src[x - 1 + y * width],
                                                             src[x + 1 + y * width],
                                                             src[x + (y - 1) * width],
                                                             src[x + (y + 1) * width],
                                                             C2,
                                                             QUART);
                }
                else if (!evenX && !evenY)
                {
                    dst[dx + dy * scale] = rgb(src[x - 1 + y * width],
                                               src[x + 1 + y * width],
                                               src[x + (y - 1) * width],
                                               src[x + (y + 1) * width],
                                               C3,
                                               QUART);
                    dst[dx + 1 + dy * scale] = rgb(src[x - 1 + y * width],
                                                   src[x + 1 + y * width],
                                                   src[x + (y - 1) * width],
                                                   src[x + (y + 1) * width],
                                                   C3,
                                                   QUART);
                    dst[dx + (dy + width) * scale] = rgb(src[x - 1 + y * width],
                                                         src[x + 1 + y * width],
                                                         src[x + (y - 1) * width],
                                                         src[x + (y + 1) * width],
                                                         C3,
                                                         QUART);
                    dst[dx + 1 + (dy + width) * scale] = rgb(src[x - 1 + y * width],
                                                             src[x + 1 + y * width],
                                                             src[x + (y - 1) * width],
                                                             src[x + (y + 1) * width],
                                                             C3,
                                                             QUART);
                }
            }
        }
    }
    // CHECKSTYLE ON: MagicNumber
}
