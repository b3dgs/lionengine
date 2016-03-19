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
package com.b3dgs.lionengine.core.android;

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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.graphic.UtilColor;

/**
 * Misc tools for Android.
 */
public final class ToolsAndroid
{
    /**
     * Create an image.
     * 
     * @param width The image width.
     * @param height The image height.
     * @return The image.
     */
    public static Bitmap createImage(int width, int height)
    {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    /**
     * Get an image from an image file.
     * 
     * @param input The image input stream.
     * @return The created image from file.
     */
    public static Bitmap getImage(InputStream input)
    {
        return BitmapFactory.decodeStream(input);
    }

    /**
     * Get an image from an image.
     * 
     * @param image The image.
     * @return The created image.
     */
    public static Bitmap getImage(Bitmap image)
    {
        return Bitmap.createBitmap(image);
    }

    /**
     * Apply color mask to the image.
     * 
     * @param image The image reference.
     * @param maskColor The color mask.
     * @return The masked image.
     */
    public static Bitmap applyMask(Bitmap image, int maskColor)
    {
        final Paint mask = new Paint();
        mask.setXfermode(new AvoidXfermode(maskColor, 0, AvoidXfermode.Mode.TARGET));
        final Canvas canvas = new Canvas(image);
        canvas.drawBitmap(image, 0, 0, mask);
        return image;
    }

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions (strictly positive).
     * @param v The number of vertical divisions (strictly positive).
     * @return The splited images array (can not be empty).
     */
    public static Bitmap[] splitImage(Bitmap image, int h, int v)
    {
        final Bitmap[] buffers = new Bitmap[h * v];
        final int width = image.getWidth() / h;
        final int height = image.getHeight() / v;
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
                canvas.drawBitmap(image, source, dest, paint);
                buffers[i] = part;
                i++;
            }
        }

        return buffers;
    }

    /**
     * Rotate input image.
     * 
     * @param image The input image.
     * @param angle The angle to apply in degree (0-359)
     * @return The new image with angle applied.
     */
    public static Bitmap rotate(Bitmap image, int angle)
    {
        final Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
    }

    /**
     * Resize input image.
     * 
     * @param image The input image.
     * @param width The new width.
     * @param height The new height.
     * @return The new image with new size.
     */
    public static Bitmap resize(Bitmap image, int width, int height)
    {
        return Bitmap.createScaledBitmap(image, width, height, false);
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    public static Bitmap flipHorizontal(Bitmap image)
    {
        final Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    public static Bitmap flipVertical(Bitmap image)
    {
        final Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
    }

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param output The output stream.
     * @return <code>true</code> if saved, <code>false</code> else.
     */
    public static boolean saveImage(Bitmap image, OutputStream output)
    {
        return image.compress(CompressFormat.PNG, 100, output);
    }

    /**
     * Get raster buffer from data.
     * 
     * @param image The image.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     * @param refSize The reference size.
     * @return The rastered image.
     */
    public static Bitmap getRasterBuffer(Bitmap image, int fr, int fg, int fb, int er, int eg, int eb, int refSize)
    {
        final Bitmap raster = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);

        final int divisorRed = 0x010000;
        final int divisorGreen = 0x000100;
        final int divisorBlue = 0x000001;

        final double sr = -((er - fr) / divisorRed) / (double) refSize;
        final double sg = -((eg - fg) / divisorGreen) / (double) refSize;
        final double sb = -((eb - fb) / divisorBlue) / (double) refSize;

        for (int i = 0; i < raster.getWidth(); i++)
        {
            for (int j = 0; j < raster.getHeight(); j++)
            {
                final int r = (int) (sr * (j % refSize)) * divisorRed;
                final int g = (int) (sg * (j % refSize)) * divisorGreen;
                final int b = (int) (sb * (j % refSize)) * divisorBlue;

                raster.setPixel(i, j, UtilColor.filterRgb(image.getPixel(i, j), fr + r, fg + g, fb + b));
            }
        }

        return raster;
    }

    /**
     * Private constructor.
     */
    private ToolsAndroid()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
