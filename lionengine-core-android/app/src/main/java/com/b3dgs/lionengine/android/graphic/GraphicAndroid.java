/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionengine.android.graphic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.graphic.ColorGradient;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageSurface;
import com.b3dgs.lionengine.graphic.Transform;

import java.util.HashMap;
import java.util.Map;

/**
 * Main interface with the graphic output, representing the screen buffer.
 */
final class GraphicAndroid implements Graphic
{
    /** Gradient cache. */
    private final Map<ColorGradient, LinearGradient> colorGradients = new HashMap<>();
    /** Paint mode. */
    private final Paint paint = new Paint();
    /** Last matrix. */
    private final Matrix scale = new Matrix();
    /** Flip matrix. */
    private final Matrix flip = new Matrix();
    /** The graphic output. */
    private Canvas g;
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
        super();

        this.g = g;
        flip.preScale(-1, 1);
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
        g.drawRect(x, y, x + (float) width, y + (float) height, paint);
    }

    @Override
    public void dispose()
    {
        colorGradients.clear();
        linearGradient = null;
    }

    @Override
    public void copyArea(int sx, int sy, int width, int height, int dx, int dy)
    {
        // TODO : throw new UnsupportedOperationException("copyArea is not supported !");
    }

    @Override
    public void drawImage(ImageSurface image, int x, int y)
    {
        final Bitmap surface = image.getSurface();
        g.drawBitmap(surface, x, y, paint);
    }

    @Override
    public void drawImage(ImageSurface image, Transform transform, int x, int y)
    {
        scale.setScale((float) transform.getScaleX(), (float) transform.getScaleY());

        final Bitmap surface = image.getSurface();
        g.drawBitmap(surface, scale, paint);
    }

    @Override
    public void drawImage(ImageSurface image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2)
    {
        drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, 0, 0, 0);
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
        final Bitmap surface = image.getSurface();
        final Rect dest = new Rect(dx1, dy1, dx2, dy2);
        final Rect src = new Rect(sx1, sy1, sx2, sy2);
        if (sx1 > sx2)
        {
            final Bitmap part = Bitmap.createBitmap(surface, sx2, sy1, sx1 - sx2, sy2 - sy1, flip, false);
            g.drawBitmap(part, dx1, dy1, paint);
        }
        else
        {
            g.drawBitmap(surface, src, dest, paint);
        }
    }

    @Override
    public void drawRect(int x, int y, int width, int height, boolean fill)
    {
        if (fill)
        {
            paint.setStyle(Paint.Style.FILL);
            g.drawRect(x, y, x + (float) width, y + (float) height, paint);
        }
        else
        {
            paint.setStyle(Paint.Style.STROKE);
            g.drawRect(x, y, x + (float) width, y + (float) height, paint);
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
        paint.setShader(linearGradient);
        drawRect(x, y, width, height, true);
        paint.setShader(null);
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
        g.drawLine(x1, y1, x2, y2, paint);
    }

    @Override
    public void drawLine(Viewer viewer, double x1, double y1, double x2, double y2)
    {
        g.drawLine((int) viewer.getViewpointX(x1),
                   (int) viewer.getViewpointY(y1),
                   (int) viewer.getViewpointX(x2),
                   (int) viewer.getViewpointY(y2),
                   paint);
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
        g.drawCircle(x, y, width * (float) height, paint);
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
        paint.setColor(color.getRgba());
    }

    @Override
    public void setColorGradient(ColorGradient cg)
    {
        if (!colorGradients.containsKey(cg))
        {
            final int c1 = cg.getColor1().getRgba();
            final int c2 = cg.getColor2().getRgba();
            final LinearGradient gradient = new LinearGradient(cg.getX1(),
                                                               cg.getY1(),
                                                               cg.getX2(),
                                                               cg.getY2(),
                                                               c1,
                                                               c2,
                                                               TileMode.CLAMP);
            colorGradients.put(cg, gradient);
        }
        linearGradient = colorGradients.get(cg);
    }

    @Override
    public void setAlpha(int alpha)
    {
        paint.setAlpha(alpha);
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
