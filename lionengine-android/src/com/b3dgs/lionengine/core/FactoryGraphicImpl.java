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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;

/**
 * Graphic factory implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class FactoryGraphicImpl
        implements FactoryGraphic
{
    /**
     * Get the image buffer.
     * 
     * @param imageBuffer The image buffer.
     * @return The buffer.
     */
    private static Bitmap getBuffer(ImageBuffer imageBuffer)
    {
        return ((ImageBufferImpl) imageBuffer).getBuffer();
    }

    /**
     * Constructor.
     */
    FactoryGraphicImpl()
    {
        // Nothing to do
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Screen createScreen(Config config)
    {
        return new ScreenImpl(config);
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return new TextImpl(fontName, size, style);
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicImpl();
    }

    @Override
    public ImageBuffer createCompatibleImage(int width, int height, Transparency transparency)
    {
        final Bitmap buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        return new ImageBufferImpl(buffer);
    }

    @Override
    public ImageBuffer getImageBuffer(InputStream inputStream, boolean alpha) throws IOException
    {
        return new ImageBufferImpl(BitmapFactory.decodeStream(inputStream));
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        return new ImageBufferImpl(Bitmap.createBitmap(FactoryGraphicImpl.getBuffer(imageBuffer)));
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        final Bitmap bitmap = FactoryGraphicImpl.getBuffer(imageBuffer);
        final Paint mask = new Paint();
        mask.setXfermode(new AvoidXfermode(maskColor.getRgba(), 0, AvoidXfermode.Mode.TARGET));
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, mask);
        return new ImageBufferImpl(bitmap);
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer imageBuffer, int h, int v)
    {
        final Bitmap bitmap = FactoryGraphicImpl.getBuffer(imageBuffer);
        final ImageBuffer[] buffers = new ImageBuffer[h * v];
        final int width = bitmap.getWidth() / h;
        final int height = bitmap.getHeight() / v;
        final Rect dest = new Rect(0, 0, width, height);

        final Paint paint = new Paint();
        int i = 0;
        for (int r = 0; r < h; r++)
        {
            for (int c = 0; c < v; c++)
            {
                final Bitmap part = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                final Canvas canvas = new Canvas(part);
                final Rect source = new Rect(r * width, c * height, r * width + width, c * height + height);
                canvas.drawBitmap(bitmap, source, dest, paint);
                buffers[i] = new ImageBufferImpl(part);
                i++;
            }
        }

        return buffers;
    }

    @Override
    public ImageBuffer rotate(ImageBuffer imageBuffer, int angle)
    {
        final Bitmap bitmap = FactoryGraphicImpl.getBuffer(imageBuffer);
        final Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        final Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, imageBuffer.getWidth(), imageBuffer.getHeight(),
                matrix, false);
        return new ImageBufferImpl(rotated);
    }

    @Override
    public ImageBuffer resize(ImageBuffer imageBuffer, int width, int height)
    {
        final Bitmap bitmap = FactoryGraphicImpl.getBuffer(imageBuffer);
        final Bitmap resized = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return new ImageBufferImpl(resized);
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer imageBuffer)
    {
        final Bitmap bitmap = FactoryGraphicImpl.getBuffer(imageBuffer);
        final Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        final Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return new ImageBufferImpl(rotated);
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer imageBuffer)
    {
        final Bitmap bitmap = FactoryGraphicImpl.getBuffer(imageBuffer);
        final Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        final Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return new ImageBufferImpl(rotated);
    }

    @Override
    public ImageBuffer applyFilter(ImageBuffer imageBuffer, Filter filter)
    {
        // TODO: ApplyFilter
        return imageBuffer;
    }

    @Override
    public void saveImage(ImageBuffer imageBuffer, OutputStream outputStream) throws IOException
    {
        if (!FactoryGraphicImpl.getBuffer(imageBuffer).compress(CompressFormat.PNG, 100, outputStream))
        {
            throw new LionEngineException("Unable to save the file !");
        }
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer imageBuffer, int fr, int fg, int fb, int er, int eg, int eb,
            int refSize)
    {
        // TODO: GetRasterBuffer
        return imageBuffer;
    }
}
