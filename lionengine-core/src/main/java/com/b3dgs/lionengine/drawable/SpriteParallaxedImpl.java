/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.drawable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;

/**
 * Parallaxed sprite implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class SpriteParallaxedImpl implements SpriteParallaxed
{
    /** Parallax surface file name. */
    private final Media media;
    /** Number of parallax line. */
    private final int linesNumber;
    /** Parallax width. */
    private final int sx;
    /** Parallax height. */
    private final int sy;
    /** Surface of each line. */
    private ImageBuffer[] lines;
    /** Line width. */
    private int lineWidth;
    /** Line height. */
    private int lineHeight;
    /** Rendering factor h. */
    private double factorH;
    /** Rendering factor v. */
    private double factorV;

    /**
     * Internal constructor.
     * 
     * @param media The sprite media.
     * @param linesNumber The number of line.
     * @param sx The parallax width.
     * @param sy The parallax height.
     * @throws LionEngineException If arguments are invalid.
     */
    SpriteParallaxedImpl(Media media, int linesNumber, int sx, int sy) throws LionEngineException
    {
        Check.notNull(media);
        Check.superiorStrict(linesNumber, 0);
        Check.superiorStrict(sx, 0);
        Check.superiorStrict(sy, 0);

        this.media = media;
        this.linesNumber = linesNumber;
        this.sx = sx;
        this.sy = sy;
        factorH = 1.0;
        factorV = 1.0;
        lines = null;
    }

    /*
     * SpriteParallaxed
     */

    @Override
    public void load(boolean alpha) throws LionEngineException
    {
        ImageBuffer surface = Graphics.getImageBuffer(media);

        if (0 != Double.compare(factorH, 1.0) || 0 != Double.compare(factorV, 1.0))
        {
            surface = Graphics.resize(surface, (int) (surface.getWidth() * factorH),
                    (int) (surface.getHeight() * factorV));
        }

        lineWidth = (int) Math.floor(surface.getWidth() * sx / 100.0);
        lineHeight = (int) Math.floor(surface.getHeight() / linesNumber * sy / 100.0);
        lines = Graphics.splitImage(surface, 1, linesNumber);
        final double ratio = 0.6;
        final double factH = sx / 100.0 / ratio;

        for (int i = 0; i < linesNumber; i++)
        {
            final int width = (int) Math.ceil(lines[i].getWidth() * (sx + i * 2 * factH) / 100);
            final int height = lines[i].getHeight() * sy / 100;
            lines[i] = Graphics.resize(lines[i], width, height);
        }
    }

    @Override
    public void stretch(int widthPercent, int heightPercent)
    {
        factorH = widthPercent / 100.0;
        factorV = heightPercent / 100.0;
    }

    @Override
    public void render(Graphic g, int line, int x, int y)
    {
        g.drawImage(lines[line], x, y);
    }

    @Override
    public int getLineWidth(int line)
    {
        return lines[line].getWidth();
    }

    @Override
    public int getWidth()
    {
        return lineWidth;
    }

    @Override
    public int getHeight()
    {
        return lineHeight;
    }
}
