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
package com.b3dgs.lionengine.headless.graphic;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.graphic.ColorGradient;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageSurface;
import com.b3dgs.lionengine.graphic.Transform;

/**
 * Main interface with the graphic output, representing the screen buffer.
 */
final class GraphicHeadless implements Graphic
{
    /** Graphic representation. */
    private ImageBufferHeadless g;
    /** Current color. */
    private ColorRgba color = ColorRgba.WHITE;
    /** Color gradient. */
    private ColorGradient gradient;

    /**
     * Internal constructor.
     */
    GraphicHeadless()
    {
        super();
    }

    /**
     * Internal constructor.
     * 
     * @param g The graphic buffer.
     */
    GraphicHeadless(ImageBufferHeadless g)
    {
        super();

        this.g = g;
    }

    /*
     * Graphic
     */

    @Override
    public void clear(int x, int y, int width, int height)
    {
        for (int i = x; i < width; i++)
        {
            for (int j = y; j < height; j++)
            {
                g.setRgb(x, y, ColorRgba.BLACK.getRgba());
            }
        }
    }

    @Override
    public void dispose()
    {
        g = null;
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy)
    {
        // Nothing to do
    }

    @Override
    public void drawImage(ImageSurface image, int x, int y)
    {
        final ImageBufferHeadless surface = image.getSurface();
        for (int i = x; i < image.getWidth(); i++)
        {
            for (int j = y; j < image.getHeight(); j++)
            {
                g.setRgb(x + i, y + j, surface.getRgb(i, j));
            }
        }
    }

    @Override
    public void drawImage(ImageSurface image, Transform transform, int x, int y)
    {
        final ImageBufferHeadless surface = image.getSurface();
        for (int i = x; i < image.getWidth(); i++)
        {
            for (int j = y; j < image.getHeight(); j++)
            {
                g.setRgb(x + i, y + j, surface.getRgb(i, j));
            }
        }
    }

    @Override
    public void drawImage(ImageSurface image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2)
    {
        // Nothing to do
    }

    @Override
    public void drawImage(ImageSurface image,
                          int dx1,
                          int dy1,
                          int dx2,
                          int dy2,
                          int sx1,
                          int sy1,
                          int sx2,
                          int sy2,
                          int angle,
                          int angleX,
                          int angleY)
    {
        // Nothing to do
    }

    @Override
    public void drawRect(int x, int y, int width, int height, boolean fill)
    {
        if (fill)
        {
            for (int i = x; i < width; i++)
            {
                for (int j = y; j < height; j++)
                {
                    g.setRgb(x, y, color.getRgba());
                }
            }
        }
        else
        {
            for (int i = x; i < width; i++)
            {
                g.setRgb(i, y, color.getRgba());
                g.setRgb(i, y + height - 1, color.getRgba());
            }
            for (int j = y; j < height; j++)
            {
                g.setRgb(x, j, color.getRgba());
                g.setRgb(x + width - 1, j, color.getRgba());
            }
        }
    }

    @Override
    public void drawRect(Viewer viewer, Origin origin, double x, double y, int width, int height, boolean fill)
    {
        final int px = (int) origin.getX(viewer.getViewpointX(x), width);
        final int py = (int) origin.getY(viewer.getViewpointY(y), height);
        drawRect(px, py, width, height, fill);
    }

    @Override
    public void drawGradient(int x, int y, int width, int height)
    {
        for (int i = x; i < width; i++)
        {
            for (int j = y; j < height; j++)
            {
                g.setRgb(x, y, gradient.getColor1().getRgba());
            }
        }
    }

    @Override
    public void drawGradient(Viewer viewer, Origin origin, double x, double y, int width, int height)
    {
        final int px = (int) origin.getX(viewer.getViewpointX(x), width);
        final int py = (int) origin.getY(viewer.getViewpointY(y), height);
        drawGradient(px, py, width, height);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        final double dx = x1 - (double) x2;
        final double dy = y1 - (double) y2;
        final double norm = Math.sqrt(dx * dx + dy * dy);
        final double sx;
        final double sy;
        if (Double.compare(norm, 0.0) == 0)
        {
            sx = 0;
            sy = 0;
        }
        else
        {
            sx = dx / norm;
            sy = dy / norm;
        }

        double x = x1;
        double y = y1;

        for (int count = 0; count < norm; count++)
        {
            g.setRgb((int) Math.floor(x), (int) Math.floor(y), color.getRgba());
            x += sx;
            y += sy;
        }
    }

    @Override
    public void drawLine(Viewer viewer, double x1, double y1, double x2, double y2)
    {
        drawLine((int) viewer.getViewpointX(x1),
                 (int) viewer.getViewpointY(y1),
                 (int) viewer.getViewpointX(x2),
                 (int) viewer.getViewpointY(y2));
    }

    @Override
    public void drawOval(int x, int y, int width, int height, boolean fill)
    {
        // Nothing to do
    }

    @Override
    public void drawOval(Viewer viewer, Origin origin, double x, double y, int width, int height, boolean fill)
    {
        // Nothing to do
    }

    @Override
    public void setColor(ColorRgba color)
    {
        this.color = color;
    }

    @Override
    public void setColorGradient(ColorGradient gc)
    {
        gradient = gc;
    }

    @Override
    public void setGraphic(Object graphic)
    {
        if (graphic instanceof ImageBufferHeadless)
        {
            g = (ImageBufferHeadless) graphic;
        }
        else
        {
            g = null;
        }
    }

    @Override
    public void setAlpha(int alpha)
    {
        // Nothing to do
    }

    @Override
    public Object getGraphic()
    {
        return g;
    }

    @Override
    public ColorRgba getColor()
    {
        return color;
    }
}
