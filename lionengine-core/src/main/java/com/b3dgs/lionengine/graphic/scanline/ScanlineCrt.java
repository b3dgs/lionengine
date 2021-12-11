/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.scanline;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Scanline;

/**
 * Crt scanline implementation.
 */
public final class ScanlineCrt implements Scanline
{
    private static final int ALPHA = 112;
    private static final String FOLDER_SCANLINE = "scanline";
    private static final String FOLDER_CRT = "crt";

    private static Scanline instance;

    /**
     * Get instance.
     * 
     * @param source The source resolution.
     * @param factor The line factor.
     * @return The instance.
     */
    public static synchronized Scanline getInstance(Resolution source, double factor)
    {
        if (instance == null)
        {
            instance = new ScanlineCrt(source, factor);
        }
        return instance;
    }

    private static ColorRgba[][] createColor()
    {
        final int f = 14;
        final ColorRgba[][] color = new ColorRgba[][]
        {
            {
                new ColorRgba(5 * f / 3, 1 * f / 4, 1 * f / 3, (int) (ALPHA * 0.9)),
                new ColorRgba(1 * f / 3, 5 * f / 4, 1 * f / 3, (int) (ALPHA * 0.9)),
                new ColorRgba(1 * f / 3, 1 * f / 4, 5 * f / 3, (int) (ALPHA * 0.9))
            },
            {
                new ColorRgba(10 * f / 2, 2 * f / 2, 2 * f / 2, (int) (ALPHA * 0.75)),
                new ColorRgba(2 * f / 2, 10 * f / 2, 2 * f / 2, (int) (ALPHA * 0.75)),
                new ColorRgba(2 * f / 2, 2 * f / 2, 10 * f / 2, (int) (ALPHA * 0.75))
            },
            {
                new ColorRgba(18 * f, 4 * f, 4 * f, (int) (ALPHA * 0.5)),
                new ColorRgba(4 * f, 18 * f, 4 * f, (int) (ALPHA * 0.5)),
                new ColorRgba(4 * f, 4 * f, 18 * f, (int) (ALPHA * 0.5))
            },
            {
                new ColorRgba(12 * f, 3 * f, 3 * f, (int) (ALPHA * 0.6)),
                new ColorRgba(3 * f, 12 * f, 3 * f, (int) (ALPHA * 0.6)),
                new ColorRgba(3 * f, 3 * f, 12 * f, (int) (ALPHA * 0.6))
            },
            {
                new ColorRgba(8 * f / 2, 5 * f / 2, 2 * f / 2, (int) (ALPHA * 0.8)),
                new ColorRgba(2 * f / 2, 8 * f / 2, 2 * f / 2, (int) (ALPHA * 0.8)),
                new ColorRgba(2 * f / 2, 2 * f / 2, 8 * f / 2, (int) (ALPHA * 0.8))
            }
        };
        return color;
    }

    private final Map<Config, ImageBuffer> cache = new HashMap<>();
    private final Resolution source;
    private final double factor;
    private ImageBuffer scanline;

    /**
     * Create scanline.
     * 
     * @param source The source resolution.
     * @param factor The line factor.
     */
    private ScanlineCrt(Resolution source, double factor)
    {
        super();

        this.source = source;
        this.factor = factor;
    }

    private void create(Graphic g2, int width, int height)
    {
        final ColorRgba[][] color = createColor();
        int y = 0;
        int c = 0;
        int i = 0;
        final int size = Math.max(1, (int) Math.round(height / (double) source.getHeight() / factor));
        while (y < height)
        {
            for (int x = 0; x < width; x += size)
            {
                g2.setColor(color[i][c]);
                g2.drawRect(x, y, size, size, true);

                c++;
                if (c >= color[0].length)
                {
                    c = 0;
                }
            }
            i++;
            if (i >= color.length)
            {
                i = 0;
            }
            y += size;
            c = 0;
        }
    }

    @Override
    public void prepare(Config config)
    {
        final Resolution output = config.getOutput();
        scanline = cache.get(config);
        if (scanline == null)
        {
            final Media media = Medias.create(FOLDER_SCANLINE,
                                              FOLDER_CRT,
                                              output.getWidth() + Constant.UNDERSCORE + output.getHeight() + ".png");
            if (media.exists())
            {
                scanline = Graphics.getImageBuffer(media);
            }
            else
            {
                scanline = Graphics.createImageBufferAlpha(output.getWidth(), output.getHeight());
                final Graphic g2 = scanline.createGraphic();
                create(g2, scanline.getWidth(), scanline.getHeight());
                g2.dispose();
                Graphics.saveImage(scanline, media);
            }
            cache.put(config, scanline);
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.drawImage(scanline, 0, 0);
    }
}
