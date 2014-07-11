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

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;

/**
 * Misc tools for engine image creation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilityImage
{
    /** Reading image message. */
    private static final String ERROR_IMAGE_READING = "Error on reading image !";
    /** Save image message. */
    private static final String ERROR_IMAGE_SAVE = "Unable to save image: ";
    /** Error image buffer implementation. */
    private static final String ERROR_IMAGE_BUFFER_IMPL = "Unsupported image buffer implementation !";

    /**
     * Get the image buffer.
     * 
     * @param image The image buffer.
     * @return The buffer.
     */
    static Image getBuffer(ImageBuffer image)
    {
        if (image instanceof ImageBufferSwt)
        {
            return ((ImageBufferSwt) image).getBuffer();
        }
        throw new LionEngineException(UtilityImage.ERROR_IMAGE_BUFFER_IMPL);
    }

    /**
     * Get the image transparency equivalence.
     * 
     * @param transparency The transparency type.
     * @return The transparency value.
     */
    static int getTransparency(Transparency transparency)
    {
        switch (transparency)
        {
            case OPAQUE:
                return SWT.TRANSPARENCY_NONE;
            case BITMASK:
                return SWT.TRANSPARENCY_MASK;
            case TRANSLUCENT:
                return SWT.TRANSPARENCY_ALPHA;
            default:
                return 0;
        }
    }

    /**
     * Create an image.
     * 
     * @param width The image width.
     * @param height The image height.
     * @param transparency The image transparency.
     * @return The image.
     */
    static ImageBuffer createImage(int width, int height, Transparency transparency)
    {
        return new ImageBufferSwt(ToolsSwt.createImage(width, height, UtilityImage.getTransparency(transparency)));
    }

    /**
     * Get an image from an image file.
     * 
     * @param media The image media.
     * @param alpha <code>true</code> to enable alpha, <code>false</code> else.
     * @return The created image from file.
     */
    static ImageBuffer getImage(Media media, boolean alpha)
    {
        try (final InputStream inputStream = media.getInputStream();)
        {
            return new ImageBufferSwt(ToolsSwt.getImage(inputStream, alpha));
        }
        catch (final IOException
                     | SWTException exception)
        {
            throw new LionEngineException(exception, UtilityImage.ERROR_IMAGE_READING);
        }
    }

    /**
     * Get an image from an image.
     * 
     * @param image The image.
     * @return The created image from file.
     */
    static ImageBuffer getImage(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.getImage(UtilityImage.getBuffer(image)));
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
        return new ImageBufferSwt(ToolsSwt.applyMask(UtilityImage.getBuffer(image), maskColor.getRgba()));
    }

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions (> 0).
     * @param v The number of vertical divisions (> 0).
     * @return The splited images array (can not be empty).
     */
    static ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        final Image[] images = ToolsSwt.splitImage(UtilityImage.getBuffer(image), h, v);
        final ImageBuffer[] imageBuffers = new ImageBuffer[images.length];
        for (int i = 0; i < imageBuffers.length; i++)
        {
            imageBuffers[i] = new ImageBufferSwt(images[i]);
        }
        return imageBuffers;
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
        return new ImageBufferSwt(ToolsSwt.rotate(UtilityImage.getBuffer(image), angle));
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
        return new ImageBufferSwt(ToolsSwt.resize(UtilityImage.getBuffer(image), width, height));
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    static ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.flipHorizontal(UtilityImage.getBuffer(image)));
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    static ImageBuffer flipVertical(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.flipVertical(UtilityImage.getBuffer(image)));
    }

    /**
     * Apply a filter to the input image.
     * 
     * @param image The input image.
     * @param filter The filter to use.
     * @return The filtered image as a new instance.
     * @throws LionEngineException If the filter is not supported.
     */
    static ImageBuffer applyFilter(ImageBuffer image, Filter filter) throws LionEngineException
    {
        return new ImageBufferSwt(ToolsSwt.applyFilter(UtilityImage.getBuffer(image), filter));
    }

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param media The output media.
     */
    static void saveImage(ImageBuffer image, Media media)
    {
        try (final OutputStream outputStream = media.getOutputStream())
        {
            ToolsSwt.saveImage(UtilityImage.getBuffer(image), outputStream);
        }
        catch (final IOException
                     | SWTException exception)
        {
            throw new LionEngineException(exception, UtilityImage.ERROR_IMAGE_SAVE);
        }
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
        return new ImageBufferSwt(ToolsSwt.getRasterBuffer(UtilityImage.getBuffer(image), fr, fg, fb, er, eg, eb,
                refSize));
    }

    /**
     * Constructor.
     */
    private UtilityImage()
    {
        throw new RuntimeException();
    }
}
