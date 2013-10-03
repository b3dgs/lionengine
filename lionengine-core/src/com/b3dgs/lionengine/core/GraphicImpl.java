/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Resolution;

/**
 * Main interface with the graphic output, representing the screen buffer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class GraphicImpl
        implements Graphic
{
    /**
     * Get the image buffer.
     * 
     * @param imageBuffer The image buffer.
     * @return The buffer.
     */
    private static BufferedImage getBuffer(ImageBuffer imageBuffer)
    {
        return ((ImageBufferImpl) imageBuffer).getBuffer();
    }

    /** The graphic output. */
    private Graphics2D g;

    /**
     * Constructor.
     */
    GraphicImpl()
    {
        // Nothing to do
    }

    /**
     * Constructor.
     * 
     * @param g The graphics output.
     */
    GraphicImpl(Graphics2D g)
    {
        this.g = g;
    }

    /*
     * Graphic
     */

    @Override
    public void clear(Resolution resolution)
    {
        g.clearRect(0, 0, resolution.getWidth(), resolution.getHeight());
    }

    @Override
    public void clear(int x, int y, int width, int height)
    {
        g.clearRect(x, y, width, height);
    }

    @Override
    public void dispose()
    {
        g.dispose();
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy)
    {
        g.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public void drawImage(ImageBuffer image, int x, int y)
    {
        g.drawImage(GraphicImpl.getBuffer(image), null, x, y);
    }

    @Override
    public void drawImage(ImageBuffer image, AffineTransformOp op, int x, int y)
    {
        g.drawImage(GraphicImpl.getBuffer(image), op, x, y);
    }

    @Override
    public void drawImage(ImageBuffer image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2)
    {
        g.drawImage(GraphicImpl.getBuffer(image), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
    }

    @Override
    public void drawRect(int x, int y, int width, int height, boolean fill)
    {
        if (fill)
        {
            g.fillRect(x, y, width, height);
        }
        else
        {
            g.drawRect(x, y, width, height);
        }
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawOval(int x, int y, int width, int height, boolean fill)
    {
        if (fill)
        {
            g.fillOval(x, y, width, height);
        }
        else
        {
            g.drawOval(x, y, width, height);
        }
    }

    @Override
    public void setColor(ColorRgba color)
    {
        g.setColor(new Color(color.getRgba(), true));
    }

    @Override
    public <G> void setGraphic(G graphic)
    {
        g = (Graphics2D) graphic;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <G> G getGraphic()
    {
        return (G) g;
    }

    @Override
    public ColorRgba getColor()
    {
        return new ColorRgba(g.getColor().getRGB());
    }
}
