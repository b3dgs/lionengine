/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.graphic.ColorGradient;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageSurface;
import com.b3dgs.lionengine.graphic.Transform;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Main interface with the graphic output, representing the screen buffer.
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
    public void drawImage(ImageSurface image, int x, int y)
    {
        g.drawImage((BufferedImage) image.getSurface(), null, x, y);
    }

    @Override
    public void drawImage(ImageSurface image, Transform transform, int x, int y)
    {
        if (lastTransform != transform)
        {
            lastTransform = transform;
            final AffineTransform at = new AffineTransform();
            at.scale(transform.getScaleX(), transform.getScaleY());
            final int interpolation = UtilMath.clamp(transform.getInterpolation(),
                                                     AffineTransformOp.TYPE_NEAREST_NEIGHBOR,
                                                     AffineTransformOp.TYPE_BICUBIC);
            op = new AffineTransformOp(at, interpolation);
        }
        g.drawImage((BufferedImage) image.getSurface(), op, x, y);
    }

    @Override
    public void drawImage(ImageSurface image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2)
    {
        g.drawImage((BufferedImage) image.getSurface(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
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
        final int px = (int) origin.getX(viewer.getViewpointX(x), width);
        final int py = (int) origin.getY(viewer.getViewpointY(y), height);
        drawRect(px, py, width, height, fill);
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
        final int px = (int) origin.getX(viewer.getViewpointX(x), width);
        final int py = (int) origin.getY(viewer.getViewpointY(y), height);
        drawGradient(px, py, width, height);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawLine(Viewer viewer, double x1, double y1, double x2, double y2)
    {
        g.drawLine((int) viewer.getViewpointX(x1),
                   (int) viewer.getViewpointY(y1),
                   (int) viewer.getViewpointX(x2),
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
        final int px = (int) origin.getX(viewer.getViewpointX(x), width);
        final int py = (int) origin.getY(viewer.getViewpointY(y), height);
        drawOval(px, py, width, height, fill);
    }

    @Override
    public void setColor(ColorRgba color)
    {
        g.setColor(new Color(color.getRgba(), true));
    }

    @Override
    public void setColorGradient(ColorGradient gc)
    {
        final Color color1 = new Color(gc.getColor1().getRgba());
        final Color color2 = new Color(gc.getColor2().getRgba());
        gradientPaint = new GradientPaint(gc.getX1(), gc.getY1(), color1, gc.getX2(), gc.getY2(), color2);
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
