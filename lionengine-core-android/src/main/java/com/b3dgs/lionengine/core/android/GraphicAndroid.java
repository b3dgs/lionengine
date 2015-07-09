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
package com.b3dgs.lionengine.core.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;

import com.b3dgs.lionengine.Align;
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
final class GraphicAndroid implements Graphic
{
    /**
     * Get the image buffer.
     * 
     * @param imageBuffer The image buffer.
     * @return The buffer.
     */
    private static Bitmap getBuffer(ImageBuffer imageBuffer)
    {
        return ((ImageBufferAndroid) imageBuffer).getBuffer();
    }

    /** Paint mode. */
    private final Paint paint;
    /** Last matrix. */
    private final Matrix scale;
    /** Flip matrix. */
    private final Matrix flip;
    /** The graphic output. */
    private Canvas g;
    /** Last gradient. */
    private ColorGradient gradientColor;
    /** Linear gradient. */
    private LinearGradient linearGradient;

    /**
     * Internal constructor.
     */
    GraphicAndroid()
    {
        this(null);
    }

    /**
     * Internal constructor.
     * 
     * @param g The graphics output.
     */
    GraphicAndroid(Canvas g)
    {
        paint = new Paint();
        scale = new Matrix();
        flip = new Matrix();
        flip.preScale(-1, 1);
        this.g = g;
    }

    /**
     * Draw a string on screen.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param alignment The text alignment.
     * @param text The text to write.
     * @param paint The paint used.
     */
    void drawString(int x, int y, Align alignment, String text, Paint paint)
    {
        paint.setTextAlign(Paint.Align.valueOf(alignment.name()));
        g.drawText(text, x, y, paint);
    }

    /*
     * Graphic
     */

    @Override
    public void clear(int x, int y, int width, int height)
    {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        g.drawRect(x, y, width, height, paint);
    }

    @Override
    public void dispose()
    {
        // Nothing to do
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy)
    {
        throw new UnsupportedOperationException("copyArea is not supported !");
    }

    @Override
    public void drawImage(ImageBuffer image, int x, int y)
    {
        paint.setAlpha(255);
        g.drawBitmap(GraphicAndroid.getBuffer(image), x, y, paint);
    }

    @Override
    public void drawImage(ImageBuffer image, Transform transform, int x, int y)
    {
        paint.setAlpha(255);
        scale.setScale((float) transform.getScaleX(), (float) transform.getScaleY());
        g.drawBitmap(GraphicAndroid.getBuffer(image), scale, paint);
    }

    @Override
    public void drawImage(ImageBuffer image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2)
    {
        paint.setAlpha(255);
        final Rect dest = new Rect(dx1, dy1, dx2, dy2);
        final Rect src = new Rect(sx1, sy1, sx2, sy2);
        if (sx1 > sx2)
        {
            final Bitmap part = Bitmap.createBitmap(GraphicAndroid.getBuffer(image), sx2, sy1, sx1 - sx2, sy2 - sy1,
                    flip, false);
            g.drawBitmap(part, dx1, dy1, paint);
        }
        else
        {
            g.drawBitmap(GraphicAndroid.getBuffer(image), src, dest, paint);
        }
    }

    @Override
    public void drawRect(int x, int y, int width, int height, boolean fill)
    {
        if (fill)
        {
            paint.setStyle(Paint.Style.FILL);
            g.drawRect(x, y, x + width, y + height, paint);
        }
        else
        {
            paint.setStyle(Paint.Style.STROKE);
            g.drawRect(x, y, x + width, y + height, paint);
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
        paint.setShader(linearGradient);
        drawRect(x, y, width, height, true);
        paint.setShader(null);
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
        g.drawLine(x1, y1, x2, y2, paint);
    }

    @Override
    public void drawLine(Viewer viewer, double x1, double y1, double x2, double y2)
    {
        g.drawLine((int) viewer.getViewpointX(x1), (int) viewer.getViewpointY(y1), (int) viewer.getViewpointX(x2),
                (int) viewer.getViewpointY(y2), paint);
    }

    @Override
    public void drawOval(int x, int y, int width, int height, boolean fill)
    {
        if (fill)
        {
            paint.setStyle(Paint.Style.FILL);
        }
        else
        {
            paint.setStyle(Paint.Style.STROKE);
        }
        g.drawCircle(x, y, width * height, paint);
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
        paint.setColor(color.getRgba());
    }

    @Override
    public void setColorGradient(ColorGradient gc)
    {
        if (gc != gradientColor)
        {
            linearGradient = new LinearGradient(gc.getX1(), gc.getY1(), gc.getX2(), gc.getY2(),
                    gc.getColor1().getRgba(), gc.getColor2().getRgba(), TileMode.CLAMP);
        }
    }

    @Override
    public void setGraphic(Object graphic)
    {
        g = (Canvas) graphic;
    }

    @Override
    public Object getGraphic()
    {
        return g;
    }

    @Override
    public ColorRgba getColor()
    {
        return new ColorRgba(paint.getColor());
    }
}
