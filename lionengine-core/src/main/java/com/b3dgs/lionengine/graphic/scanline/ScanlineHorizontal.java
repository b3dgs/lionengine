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
package com.b3dgs.lionengine.graphic.scanline;

import com.b3dgs.lionengine.Config;
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
    private static final ColorRgba COLOR = new ColorRgba(0, 0, 0, 96);

    private final Resolution source;
    private final double factor;

    private ImageBuffer scanline;

    /**
     * Create scanline.
     * 
     * @param source The source resolution.
     * @param factor The line factor.
     */
    public ScanlineHorizontal(Resolution source, double factor)
    {
        super();

        this.source = source;
        this.factor = factor;
    }

    private void create(Graphic g2, int width, int height)
    {
        final int size = Math.max(1, (int) Math.round(height / (double) source.getHeight() / factor));

        for (int y = 0; y < height; y += size * 2)
        {
            g2.drawRect(0, y, width, size, true);
        }
    }

    @Override
    public void prepare(Config config)
    {
        final Resolution output = config.getOutput();
        scanline = Graphics.createImageBufferAlpha(output.getWidth(), output.getHeight());
        final Graphic g2 = scanline.createGraphic();
        g2.setColor(COLOR);
        create(g2, scanline.getWidth(), scanline.getHeight());
        g2.dispose();
    }

    @Override
    public void render(Graphic g)
    {
        g.drawImage(scanline, 0, 0);
    }
}
