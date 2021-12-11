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
 * Horizontal scanline implementation.
 */
public final class ScanlineHorizontal implements Scanline
{
    private static final ColorRgba WHITE = new ColorRgba(128, 128, 128, 40);
    private static final ColorRgba RED = new ColorRgba(255, 0, 0, 28);
    private static final ColorRgba GREEN = new ColorRgba(0, 255, 0, 28);
    private static final ColorRgba BLUE = new ColorRgba(0, 0, 255, 28);

    private static final int COLORS = 3;
    private static final int HEIGHT = 4;

    private static final String FOLDER_SCANLINE = "scanline";
    private static final String FOLDER_HORIZONTAL = "horizontal";

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
            instance = new ScanlineHorizontal(source, factor);
        }
        return instance;
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
    private ScanlineHorizontal(Resolution source, double factor)
    {
        super();

        this.source = source;
        this.factor = factor;
    }

    private void create(Graphic g2, int width, int height)
    {
        final int size = Math.max(1, (int) Math.round(height / (double) source.getHeight() / factor));

        g2.setColor(WHITE);
        for (int y = 0; y < height; y += HEIGHT * size)
        {
            g2.drawRect(0, y, width, size, true);
            g2.drawRect(0, y + size, width, size, true);
        }

        for (int x = 0; x < width; x += size)
        {
            final int mod = x % COLORS;
            if (mod == 0)
            {
                g2.setColor(RED);
            }
            else if (mod == 1)
            {
                g2.setColor(GREEN);
            }
            else
            {
                g2.setColor(BLUE);
            }
            g2.drawRect(x, 0, size, height, true);
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
                                              FOLDER_HORIZONTAL,
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
