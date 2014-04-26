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
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;

/**
 * Graphic factory implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class FactoryGraphicAndroid
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
        return ((ImageBufferAndroid) imageBuffer).getBuffer();
    }

    /**
     * Constructor.
     */
    FactoryGraphicAndroid()
    {
        // Nothing to do
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Renderer createRenderer(Config config)
    {
        return new RendererAndroid(config);
    }

    @Override
    public Screen createScreen(Renderer renderer)
    {
        return new ScreenAndroid(renderer);
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicAndroid();
    }

    @Override
    public Transform createTransform()
    {
        return new TransformAndroid();
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return new TextAndroid(fontName, size, style);
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        final Bitmap buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        return new ImageBufferAndroid(buffer);
    }

    @Override
    public ImageBuffer getImageBuffer(Media media, boolean alpha)
    {
        final InputStream inputStream = media.getInputStream();
        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        final ImageBufferAndroid image = new ImageBufferAndroid(bitmap);
        try
        {
            inputStream.close();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
        return image;
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        return new ImageBufferAndroid(Bitmap.createBitmap(FactoryGraphicAndroid.getBuffer(imageBuffer)));
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        final Bitmap bitmap = FactoryGraphicAndroid.getBuffer(imageBuffer);
        final Paint mask = new Paint();
        mask.setXfermode(new AvoidXfermode(maskColor.getRgba(), 0, AvoidXfermode.Mode.TARGET));
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, mask);
        return new ImageBufferAndroid(bitmap);
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer imageBuffer, int h, int v)
    {
        final Bitmap bitmap = FactoryGraphicAndroid.getBuffer(imageBuffer);
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
                buffers[i] = new ImageBufferAndroid(part);
                i++;
            }
        }

        return buffers;
    }

    @Override
    public ImageBuffer rotate(ImageBuffer imageBuffer, int angle)
    {
        final Bitmap bitmap = FactoryGraphicAndroid.getBuffer(imageBuffer);
        final Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        final Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, imageBuffer.getWidth(), imageBuffer.getHeight(),
                matrix, false);
        return new ImageBufferAndroid(rotated);
    }

    @Override
    public ImageBuffer resize(ImageBuffer imageBuffer, int width, int height)
    {
        final Bitmap bitmap = FactoryGraphicAndroid.getBuffer(imageBuffer);
        final Bitmap resized = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return new ImageBufferAndroid(resized);
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer imageBuffer)
    {
        final Bitmap bitmap = FactoryGraphicAndroid.getBuffer(imageBuffer);
        final Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        final Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return new ImageBufferAndroid(rotated);
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer imageBuffer)
    {
        final Bitmap bitmap = FactoryGraphicAndroid.getBuffer(imageBuffer);
        final Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        final Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return new ImageBufferAndroid(rotated);
    }

    @Override
    public ImageBuffer applyFilter(ImageBuffer imageBuffer, Filter filter)
    {
        if (filter == Filter.NONE)
        {
            return imageBuffer;
        }
        throw new UnsupportedOperationException("applyFilter is not supported !");
    }

    @Override
    public void saveImage(ImageBuffer imageBuffer, Media media)
    {
        final OutputStream outputStream = media.getOutputStream();
        final Bitmap bitmap = FactoryGraphicAndroid.getBuffer(imageBuffer);
        if (!bitmap.compress(CompressFormat.PNG, 100, outputStream))
        {
            throw new LionEngineException("Unable to save the file !");
        }
        try
        {
            outputStream.close();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer imageBuffer, int fr, int fg, int fb, int er, int eg, int eb,
            int refSize)
    {
        final Bitmap image = FactoryGraphicAndroid.getBuffer(imageBuffer);
        final boolean method = true;
        final Bitmap raster = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);

        final double sr = -((er - fr) / 0x010000) / (double) refSize;
        final double sg = -((eg - fg) / 0x000100) / (double) refSize;
        final double sb = -((eb - fb) / 0x000001) / (double) refSize;

        if (method)
        {
            for (int i = 0; i < raster.getWidth(); i++)
            {
                for (int j = 0; j < raster.getHeight(); j++)
                {
                    final int r = (int) (sr * (j % refSize)) * 0x010000;
                    final int g = (int) (sg * (j % refSize)) * 0x000100;
                    final int b = (int) (sb * (j % refSize)) * 0x000001;

                    raster.setPixel(i, j, ColorRgba.filterRgb(image.getPixel(i, j), fr + r, fg + g, fb + b));
                }
            }
        }
        else
        {
            final int[] org = new int[image.getWidth() * image.getHeight() * 4];
            image.getPixels(org, 0, 0, 0, 0, image.getWidth(), image.getHeight());
            final int width = raster.getWidth();
            final int height = raster.getHeight();
            final int[] pixels = new int[width * height * 4];
            raster.getPixels(org, 0, 0, 0, 0, width, height);

            for (int j = 0; j < height; j++)
            {
                for (int i = 0; i < width; i++)
                {
                    pixels[j * width + i] = ColorRgba.filterRgb(org[j * width + i], fr, fg, fb);
                }
            }
        }

        return new ImageBufferAndroid(raster);
    }

    @Override
    public int[][] loadRaster(Media media)
    {
        return Core.GRAPHIC.loadRaster(media);
    }
}
