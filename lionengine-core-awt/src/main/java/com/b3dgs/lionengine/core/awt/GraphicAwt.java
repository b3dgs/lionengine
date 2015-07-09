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
package com.b3dgs.lionengine.core.awt;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import com.b3dgs.lionengine.ColorGradient;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Transform;

/**
 * Main interface with the graphic output, representing the screen buffer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class GraphicAwt implements Graphic
{
    /** The graphic output. */
    private Graphics2D g;
    /** Gradient paint. */
    private GradientPaint gradientPaint;
    /** Last transform. */
    private Transform lastTransform;
    /** Affine transform. */
    private AffineTransformOp op;

    /**
     * Internal constructor.
     */
    GraphicAwt()
    {
        g = null;
    }

    /**
     * Internal constructor.
     * 
     * @param g The graphics output.
     */
    GraphicAwt(Graphics2D g)
    {
        this.g = g;
    }

    /*
     * Graphic
     */

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
        g.drawImage(UtilityImage.getBuffer(image), null, x, y);
    }

    @Override
    public void drawImage(ImageBuffer image, Transform transform, int x, int y)
    {
        if (lastTransform != transform)
        {
            lastTransform = transform;
            final AffineTransform at = new AffineTransform();
            at.scale(transform.getScaleX(), transform.getScaleY());
            op = new AffineTransformOp(at, transform.getInterpolation());
        }
        g.drawImage(UtilityImage.getBuffer(image), op, x, y);
    }

    @Override
    public void drawImage(ImageBuffer image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2)
    {
        g.drawImage(UtilityImage.getBuffer(image), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
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
    public void drawRect(Viewer viewer, Origin origin, double x, double y, int width, int height, boolean fill)
    {
        drawRect((int) origin.getX(viewer.getViewpointX(x), width), (int) origin.getY(viewer.getViewpointY(y), height),
                width, height, fill);
    }

    @Override
    public void drawGradient(int x, int y, int width, int height)
    {
        g.setPaint(gradientPaint);
        g.fillRect(x, y, width, height);
        g.setPaint(null);
    }

    @Override
    public void drawGradient(Viewer viewer, Origin origin, double x, double y, int width, int height)
    {
        drawGradient((int) origin.getX(viewer.getViewpointX(x), width),
                (int) origin.getY(viewer.getViewpointY(y), height), width, height);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawLine(Viewer viewer, double x1, double y1, double x2, double y2)
    {
        g.drawLine((int) viewer.getViewpointX(x1), (int) viewer.getViewpointY(y1), (int) viewer.getViewpointX(x2),
                (int) viewer.getViewpointY(y2));
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
    public void drawOval(Viewer viewer, Origin origin, double x, double y, int width, int height, boolean fill)
    {
        drawOval((int) origin.getX(viewer.getViewpointX(x), width), (int) origin.getY(viewer.getViewpointY(y), height),
                width, height, fill);
    }

    @Override
    public void setColor(ColorRgba color)
    {
        g.setColor(new Color(color.getRgba(), true));
    }

    @Override
    public void setColorGradient(ColorGradient gc)
    {
        gradientPaint = new GradientPaint(gc.getX1(), gc.getY1(), new Color(gc.getColor1().getRgba()), gc.getX2(),
                gc.getY2(), new Color(gc.getColor2().getRgba()));
    }

    @Override
    public void setGraphic(Object graphic)
    {
        if (graphic instanceof Graphics2D)
        {
            g = (Graphics2D) graphic;
        }
        else if (graphic instanceof GraphicAwt)
        {
            g = ((GraphicAwt) graphic).g;
        }
        else
        {
            g = null;
        }
    }

    @Override
    public Object getGraphic()
    {
        return g;
    }

    @Override
    public ColorRgba getColor()
    {
        return new ColorRgba(g.getColor().getRGB());
    }
}
