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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
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
        return new ImageBufferImpl(Bitmap.createBitmap(getBuffer(imageBuffer)));
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        // TODO: ApplyMask
        return imageBuffer;
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer imageBuffer, int row, int col)
    {
        final Bitmap bitmap = getBuffer(imageBuffer);
        final ImageBuffer[] buffers = new ImageBuffer[row * col];
        final int width = bitmap.getWidth() / col;
        final int height = bitmap.getHeight() / row;

        int i = 0;
        for (int r = 0; r < row; r++)
        {
            for (int c = 0; c < col; c++)
            {
                buffers[i] = new ImageBufferImpl(Bitmap.createBitmap(bitmap, c * width, r * width, width, height));
                i++;
            }
        }

        return buffers;
    }

    @Override
    public ImageBuffer rotate(ImageBuffer imageBuffer, int angle)
    {
        final Bitmap bitmap = getBuffer(imageBuffer);
        final Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        final Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return new ImageBufferImpl(rotated);
    }

    @Override
    public ImageBuffer resize(ImageBuffer imageBuffer, int width, int height)
    {
        final Bitmap bitmap = getBuffer(imageBuffer);
        final Bitmap resized = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return new ImageBufferImpl(resized);
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer imageBuffer)
    {
        final Bitmap bitmap = getBuffer(imageBuffer);
        final Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        final Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return new ImageBufferImpl(rotated);
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer imageBuffer)
    {
        final Bitmap bitmap = getBuffer(imageBuffer);
        final Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
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
        // TODO: SaveImage
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer imageBuffer, int fr, int fg, int fb, int er, int eg, int eb,
            int refSize)
    {
        // TODO: GetRasterBuffer
        return imageBuffer;
    }
}
