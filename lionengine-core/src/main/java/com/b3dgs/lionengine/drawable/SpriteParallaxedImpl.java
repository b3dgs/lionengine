/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;

/**
 * Parallaxed sprite implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class SpriteParallaxedImpl
        implements SpriteParallaxed
{
    /** Parallax line error. */
    private static final String ERROR_PARALLAX_LINE = "The number of parallax lines must be strictly positive !";

    /** Parallax surface file name. */
    private final Media media;
    /** Number of parallax line. */
    private final int linesNumber;
    /** Parallax width. */
    private final int sx;
    /** Parallax height. */
    private final int sy;
    /** Width original. */
    private int widthOriginal;
    /** Height original. */
    private int heightOriginal;
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
     * Constructor.
     * 
     * @param media The sprite media.
     * @param linesNumber The number of line.
     * @param sx The parallax width.
     * @param sy The parallax height.
     */
    SpriteParallaxedImpl(Media media, int linesNumber, int sx, int sy)
    {
        Check.argument(linesNumber > 0, SpriteParallaxedImpl.ERROR_PARALLAX_LINE);
        Check.argument(sx > 0, "Width must be strict positive !");
        Check.argument(sy > 0, "Height must be strict positive !");
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
    public void scale(int percent)
    {
        stretch(percent, percent);
    }

    @Override
    public void stretch(int widthPercent, int heightPercent)
    {
        factorH = widthPercent / 100.0;
        factorV = heightPercent / 100.0;
    }

    @Override
    public void prepare(Filter filter)
    {
        ImageBuffer surface = Core.GRAPHIC.getImageBuffer(media, false);
        widthOriginal = surface.getWidth();
        heightOriginal = surface.getHeight();

        if (0 != Double.compare(factorH, 1.0) || 0 != Double.compare(factorV, 1.0))
        {
            surface = Core.GRAPHIC.resize(surface, (int) (surface.getWidth() * factorH),
                    (int) (surface.getHeight() * factorV));
        }
        if (Filter.BILINEAR == filter)
        {
            surface = Core.GRAPHIC.applyFilter(surface, filter);
        }

        lineWidth = (int) Math.floor(surface.getWidth() * sx / 100.0);
        lineHeight = (int) Math.floor(surface.getHeight() / linesNumber * sy / 100.0);
        lines = Core.GRAPHIC.splitImage(surface, 1, linesNumber);
        final double factH = sx / 100.0 / 0.6;

        for (int i = 0; i < linesNumber; i++)
        {
            final int width = (int) Math.ceil(lines[i].getWidth() * (sx + i * 2 * factH) / 100);
            final int height = lines[i].getHeight() * sy / 100;
            lines[i] = Core.GRAPHIC.resize(lines[i], width, height);
        }
    }

    @Override
    public void render(Graphic g, int line, int x, int y)
    {
        g.drawImage(lines[line], x, y);
    }

    @Override
    public int getWidthOriginal()
    {
        return widthOriginal;
    }

    @Override
    public int getHeightOriginal()
    {
        return heightOriginal;
    }

    @Override
    public ImageBuffer getLine(int line)
    {
        return lines[line];
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g, int x, int y)
    {
        for (int line = 0; line < linesNumber; line++)
        {
            g.drawImage(lines[line], x, y);
        }
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
