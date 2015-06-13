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
package com.b3dgs.lionengine.core.awt;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;

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
    /** Bilinear filter. */
    private static final float[] BILINEAR_FILTER = new float[]
    {
            1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f
    };

    /**
     * Get the image buffer.
     * 
     * @param image The image buffer.
     * @return The buffer.
     */
    static BufferedImage getBuffer(ImageBuffer image)
    {
        if (image instanceof ImageBufferAwt)
        {
            return ((ImageBufferAwt) image).getBuffer();
        }
        throw new LionEngineException(ERROR_IMAGE_BUFFER_IMPL);
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
                return java.awt.Transparency.OPAQUE;
            case BITMASK:
                return java.awt.Transparency.BITMASK;
            case TRANSLUCENT:
                return java.awt.Transparency.TRANSLUCENT;
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
     * @return The image instance.
     */
    static ImageBuffer createImage(int width, int height, Transparency transparency)
    {
        return new ImageBufferAwt(ToolsAwt.createImage(width, height, getTransparency(transparency)));
    }

    /**
     * Get an image from an input stream.
     * 
     * @param media The image input media.
     * @param alpha <code>true</code> to enable alpha, <code>false</code> else.
     * @return The loaded image.
     * @throws LionEngineException If error when getting image.
     */
    static ImageBuffer getImage(Media media, boolean alpha) throws LionEngineException
    {
        Check.notNull(media);
        try (InputStream inputStream = media.getInputStream())
        {
            return new ImageBufferAwt(ToolsAwt.getImage(inputStream, alpha));
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_IMAGE_READING);
        }
    }

    /**
     * Save image to output stream.
     * 
     * @param image The image to save.
     * @param media The output media.
     * @throws LionEngineException If error when saving image.
     */
    static void saveImage(ImageBuffer image, Media media) throws LionEngineException
    {
        Check.notNull(media);
        try (OutputStream outputStream = media.getOutputStream())
        {
            ToolsAwt.saveImage(getBuffer(image), outputStream);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_IMAGE_SAVE);
        }
    }

    /**
     * Create an image.
     * 
     * @param image The image reference.
     * @param transparency The transparency;
     * @return The image copy.
     */
    static ImageBuffer copyImage(ImageBuffer image, Transparency transparency)
    {
        return new ImageBufferAwt(ToolsAwt.copyImage(getBuffer(image), getTransparency(transparency)));
    }

    /**
     * Apply a bilinear filter to the image.
     * 
     * @param image The image reference.
     * @return The filtered image.
     */
    static ImageBuffer applyBilinearFilter(ImageBuffer image)
    {
        final Kernel kernel = new Kernel(3, 3, BILINEAR_FILTER);
        final ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return new ImageBufferAwt(op.filter(getBuffer(image), null));
    }

    /**
     * Apply a mask to an existing image.
     * 
     * @param image The existing image.
     * @param rgba The rgba color value.
     * @return The masked image.
     */
    static ImageBuffer applyMask(ImageBuffer image, int rgba)
    {
        return new ImageBufferAwt(ToolsAwt.applyMask(getBuffer(image), rgba));
    }

    /**
     * Rotate an image with an angle in degree.
     * 
     * @param image The input image.
     * @param angle The angle in degree to apply.
     * @return The rotated image.
     */
    static ImageBuffer rotate(ImageBuffer image, int angle)
    {
        return new ImageBufferAwt(ToolsAwt.rotate(getBuffer(image), angle));
    }

    /**
     * Resize input image buffer.
     * 
     * @param image The input image buffer.
     * @param width The new width.
     * @param height The new height.
     * @return The new image buffer with new size.
     */
    static ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        return new ImageBufferAwt(ToolsAwt.resize(getBuffer(image), width, height));
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image buffer.
     * @return The flipped image buffer as a new instance.
     */
    static ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return new ImageBufferAwt(ToolsAwt.flipHorizontal(getBuffer(image)));
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image buffer.
     * @return The flipped image buffer as a new instance.
     */
    static ImageBuffer flipVertical(ImageBuffer image)
    {
        return new ImageBufferAwt(ToolsAwt.flipVertical(getBuffer(image)));
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
        final BufferedImage[] images = ToolsAwt.splitImage(getBuffer(image), h, v);
        final ImageBuffer[] imageBuffers = new ImageBuffer[h * v];
        for (int i = 0; i < imageBuffers.length; i++)
        {
            imageBuffers[i] = new ImageBufferAwt(images[i]);
        }
        return imageBuffers;
    }

    /**
     * Get raster buffer from data.
     * 
     * @param image The image buffer.
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
        return new ImageBufferAwt(ToolsAwt.getRasterBuffer(getBuffer(image), fr, fg, fb, er, eg, eb, refSize));
    }

    /**
     * Private constructor.
     */
    private UtilityImage()
    {
        throw new RuntimeException();
    }
}
