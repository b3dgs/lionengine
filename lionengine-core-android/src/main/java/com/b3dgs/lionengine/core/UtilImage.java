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
package com.b3dgs.lionengine.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Misc tools for engine image creation.
 */
public final class UtilImage
{
    /** Reading image message. */
    private static final String ERROR_IMAGE_READING = "Error on reading image !";
    /** Save image message. */
    private static final String ERROR_IMAGE_SAVE = "Unable to save image: ";

    /**
     * Create an image.
     * 
     * @param width The image width.
     * @param height The image height.
     * @return The image.
     */
    static ImageBuffer createImage(int width, int height)
    {
        return new ImageBufferAndroid(ToolsAndroid.createImage(width, height));
    }

    /**
     * Get an image from an image file.
     * 
     * @param media The image input media.
     * @return The created image from file.
     * @throws LionEngineException If an error occurred when reading the image.
     */
    static ImageBuffer getImage(Media media)
    {
        try
        {
            final InputStream input = media.getInputStream();
            final Bitmap image = ToolsAndroid.getImage(input);
            input.close();
            return new ImageBufferAndroid(image);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, UtilImage.ERROR_IMAGE_READING);
        }
    }

    /**
     * Get an image from an image.
     * 
     * @param image The image.
     * @return The created image.
     */
    static ImageBuffer getImage(ImageBuffer image)
    {
        return new ImageBufferAndroid((Bitmap) image.getSurface());
    }

    /**
     * Apply color mask to the image.
     * 
     * @param image The image reference.
     * @param maskColor The color mask.
     * @return The masked image.
     */
    static ImageBuffer applyMask(ImageBuffer image, ColorRgba maskColor)
    {
        return new ImageBufferAndroid(ToolsAndroid.applyMask((Bitmap) image.getSurface(), maskColor.getRgba()));
    }

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions (strictly positive).
     * @param v The number of vertical divisions (strictly positive).
     * @return The splited images array (can not be empty).
     */
    static ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        final Bitmap[] bitmaps = ToolsAndroid.splitImage((Bitmap) image.getSurface(), h, v);
        final ImageBuffer[] images = new ImageBuffer[bitmaps.length];
        for (int i = 0; i < images.length; i++)
        {
            images[i] = new ImageBufferAndroid(bitmaps[i]);
        }
        return images;
    }

    /**
     * Rotate input image.
     * 
     * @param image The input image.
     * @param angle The angle to apply in degree (0-359)
     * @return The new image with angle applied.
     */
    static ImageBuffer rotate(ImageBuffer image, int angle)
    {
        return new ImageBufferAndroid(ToolsAndroid.rotate((Bitmap) image.getSurface(), angle));
    }

    /**
     * Resize input image.
     * 
     * @param image The input image.
     * @param width The new width.
     * @param height The new height.
     * @return The new image with new size.
     */
    static ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        return new ImageBufferAndroid(ToolsAndroid.resize((Bitmap) image.getSurface(), width, height));
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    static ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return new ImageBufferAndroid(ToolsAndroid.flipHorizontal((Bitmap) image.getSurface()));
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    static ImageBuffer flipVertical(ImageBuffer image)
    {
        return new ImageBufferAndroid(ToolsAndroid.flipVertical((Bitmap) image.getSurface()));
    }

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param media The output media.
     * @throws LionEngineException If an error occurred when saving the image.
     */
    static void saveImage(ImageBuffer image, Media media)
    {
        final OutputStream output = media.getOutputStream();
        if (ToolsAndroid.saveImage((Bitmap) image.getSurface(), output))
        {
            try
            {
                output.close();
            }
            catch (final IOException exception)
            {
                throw new LionEngineException(exception, UtilImage.ERROR_IMAGE_SAVE);
            }
        }
        throw new LionEngineException(UtilImage.ERROR_IMAGE_SAVE);
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
    static ImageBuffer getRasterBuffer(ImageBuffer image, int fr, int fg, int fb, int er, int eg, int eb, int refSize)
    {
        final Bitmap bitmap = (Bitmap) image.getSurface();
        return new ImageBufferAndroid(ToolsAndroid.getRasterBuffer(bitmap, fr, fg, fb, er, eg, eb, refSize));
    }

    /**
     * Private constructor.
     */
    private UtilImage()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
