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
package com.b3dgs.lionengine.core.swt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.graphic.ColorGradient;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageSurface;
import com.b3dgs.lionengine.graphic.Transform;
import com.b3dgs.lionengine.graphic.Viewer;

/**
 * Main interface with the graphic output, representing the screen buffer.
 */
final class GraphicSwt implements Graphic
{
    /** Flip image cache. */
    private final Map<ImageSurface, Image> cacheFlip = new HashMap<>();
    /** The graphic output. */
    private GC gc;
    /** Device used. */
    private Device device;
    /** Gradient paint. */
    private Color gradientColor1;
    /** Gradient paint. */
    private Color gradientColor2;
    /** Last color. */
    private Color lastColor;

    /**
     * Internal constructor.
     */
    GraphicSwt()
    {
        super();
    }

    /**
     * Internal constructor.
     * 
     * @param g The graphics output.
     */
    GraphicSwt(GC g)
    {
        gc = g;
        device = g.getDevice();
    }

    /*
     * Graphic
     */

    @Override
    public void clear(int x, int y, int width, int height)
    {
        gc.setBackground(device.getSystemColor(SWT.COLOR_BLACK));
        gc.fillRectangle(0, 0, width, height);
        gc.setBackground(device.getSystemColor(SWT.COLOR_WHITE));
        gc.setForeground(device.getSystemColor(SWT.COLOR_WHITE));
    }

    @Override
    public void dispose()
    {
        for (final Image image : cacheFlip.values())
        {
            image.dispose();
        }
        cacheFlip.clear();
        gc.dispose();
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy)
    {
        gc.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public void drawImage(ImageSurface image, int x, int y)
    {
        gc.drawImage((Image) image.getSurface(), x, y);
    }

    @Override
    public void drawImage(ImageSurface image, Transform transform, int x, int y)
    {
        final Image buffer = image.getSurface();
        final int width = (int) (image.getWidth() * transform.getScaleX());
        final int height = (int) (image.getHeight() * transform.getScaleY());
        gc.drawImage(buffer, x, y, image.getWidth(), image.getHeight(), x, y, width, height);
    }

    @Override
    public void drawImage(ImageSurface image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2)
    {
        final Image surface = image.getSurface();
        if (sx2 < sx1)
        {
            if (!cacheFlip.containsKey(image))
            {
                final Image flip = ToolsSwt.flipHorizontal(surface);
                cacheFlip.put(image, flip);
            }
            gc.drawImage(cacheFlip.get(image), dx1, dy1);
        }
        else
        {
            gc.drawImage(surface, sx1, sy1, sx2 - sx1, sy2 - sy1, dx1, dy1, dx2 - dx1, dy2 - dy1);
        }
    }

    @Override
    public void drawRect(int x, int y, int width, int height, boolean fill)
    {
        if (width == 0 && height == 0)
        {
            gc.drawPoint(x, y);
        }
        else
        {
            if (fill)
            {
                gc.fillRectangle(x, y, width, height);
            }
            else
            {
                gc.drawRectangle(x, y, width, height);
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
        gc.setBackground(gradientColor1);
        gc.setForeground(gradientColor2);
        gc.fillGradientRectangle(x, y, width, height, false);
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
        gc.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawLine(Viewer viewer, double x1, double y1, double x2, double y2)
    {
        gc.drawLine((int) viewer.getViewpointX(x1),
                    (int) viewer.getViewpointY(y1),
                    (int) viewer.getViewpointX(x2),
                    (int) viewer.getViewpointY(y2));
    }

    @Override
    public void drawOval(int x, int y, int width, int height, boolean fill)
    {
        if (fill)
        {
            gc.fillOval(x, y, width, height);
        }
        else
        {
            gc.drawOval(x, y, width, height);
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
        if (lastColor != null)
        {
            lastColor.dispose();
        }
        lastColor = new Color(device, color.getRed(), color.getGreen(), color.getBlue());
        gc.setAlpha(color.getAlpha());
        gc.setBackground(lastColor);
        gc.setForeground(lastColor);
    }

    @Override
    public void setColorGradient(ColorGradient gc)
    {
        final ColorRgba color1 = gc.getColor1();
        final ColorRgba color2 = gc.getColor2();
        if (gradientColor1 != null)
        {
            gradientColor1.dispose();
        }
        if (gradientColor2 != null)
        {
            gradientColor2.dispose();
        }
        gradientColor1 = new Color(device, color1.getRed(), color1.getGreen(), color1.getBlue());
        gradientColor2 = new Color(device, color2.getRed(), color2.getGreen(), color2.getBlue());
    }

    @Override
    public void setGraphic(Object graphic)
    {
        if (gc != null)
        {
            gc.dispose();
        }
        gc = (GC) graphic;
        if (gc != null)
        {
            device = gc.getDevice();
        }
    }

    @Override
    public Object getGraphic()
    {
        return gc;
    }

    @Override
    public ColorRgba getColor()
    {
        final Color color = gc.getBackground();
        return new ColorRgba(color.getRed(), color.getGreen(), color.getBlue(), gc.getAlpha());
    }
}
