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
package com.b3dgs.lionengine.core.swt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.Verbose;

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
     * Get a unique display for the thread caller. Create a new one if not existing.
     * 
     * @return The display associated with the thread caller.
     */
    public static synchronized Display getDisplay()
    {
        final Display display = Display.findDisplay(Thread.currentThread());
        if (display == null)
        {
            return new Display();
        }
        return display;
    }

    /**
     * Get the image buffer.
     * 
     * @param image The image buffer.
     * @return The buffer.
     */
    public static Image getBuffer(ImageBuffer image)
    {
        if (image instanceof ImageBufferSwt)
        {
            return ((ImageBufferSwt) image).getBuffer();
        }
        throw new LionEngineException(ERROR_IMAGE_BUFFER_IMPL);
    }

    /**
     * Get the image transparency equivalence.
     * 
     * @param transparency The transparency type.
     * @return The transparency value.
     */
    public static int getTransparency(Transparency transparency)
    {
        final int value;
        switch (transparency)
        {
            case OPAQUE:
                value = SWT.TRANSPARENCY_NONE;
                break;
            case BITMASK:
                value = SWT.TRANSPARENCY_MASK;
                break;
            case TRANSLUCENT:
                value = SWT.TRANSPARENCY_ALPHA;
                break;
            default:
                value = 0;
        }
        return value;
    }

    /**
     * Crate a text.
     * 
     * @param fontName The font name.
     * @param size The font size (in pixel).
     * @param style The font style.
     * @return The created text.
     */
    public static Text createText(String fontName, int size, TextStyle style)
    {
        return new TextSwt(getDisplay(), fontName, size, style);
    }

    /**
     * Create an image.
     * 
     * @param width The image width.
     * @param height The image height.
     * @param transparency The image transparency.
     * @return The image.
     * @throws SWTException If error on getting data.
     */
    public static Image createImage(int width, int height, int transparency) throws SWTException
    {
        final Device device = getDisplay();
        final Image image = new Image(device, width, height);
        if (transparency != SWT.TRANSPARENCY_NONE)
        {
            final ImageData data = image.getImageData();
            data.transparentPixel = ColorRgba.TRANSPARENT.getRgba();
            image.dispose();
            return new Image(device, data);
        }
        return image;
    }

    /**
     * Create an image.
     * 
     * @param width The image width.
     * @param height The image height.
     * @param transparency The image transparency.
     * @return The image.
     */
    public static ImageBuffer createImage(int width, int height, Transparency transparency)
    {
        Check.superiorOrEqual(width, 0);
        Check.superiorOrEqual(height, 0);

        final Image image = createImage(width, height, getTransparency(transparency));
        final ImageBufferSwt buffer = new ImageBufferSwt(image);

        final Graphic g = buffer.createGraphic();
        g.setColor(ColorRgba.BLACK);
        g.drawRect(0, 0, width, height, true);
        g.dispose();

        return buffer;
    }

    /**
     * Get an image from an image file. Image must call {@link ImageBuffer#prepare()} before any rendering.
     * 
     * @param media The image media.
     * @return The created image from file.
     * @throws LionEngineException If image cannot be read.
     */
    public static ImageBuffer getImage(Media media) throws LionEngineException
    {
        final InputStream input = media.getInputStream();
        try
        {
            return new ImageBufferSwt(getDisplay(), ToolsSwt.getImageData(input));
        }
        catch (final SWTException exception)
        {
            throw new LionEngineException(exception, ERROR_IMAGE_READING);
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (final IOException exception2)
            {
                Verbose.exception(UtilityImage.class, "getImage", exception2);
            }
        }
    }

    /**
     * Get an image from an image.
     * 
     * @param image The image.
     * @return The created image from file.
     */
    public static ImageBuffer getImage(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.getImage(getBuffer(image)));
    }

    /**
     * Apply color mask to the image.
     * 
     * @param image The image reference.
     * @param maskColor The color mask.
     * @return The masked image.
     */
    public static ImageBuffer applyMask(ImageBuffer image, ColorRgba maskColor)
    {
        return new ImageBufferSwt(ToolsSwt.applyMask(getBuffer(image), maskColor.getRgba()));
    }

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions (strictly positive).
     * @param v The number of vertical divisions (strictly positive).
     * @return The splited images array (can not be empty).
     */
    public static ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        final Image[] images = ToolsSwt.splitImage(getBuffer(image), h, v);
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
    public static ImageBuffer rotate(ImageBuffer image, int angle)
    {
        return new ImageBufferSwt(ToolsSwt.rotate(getBuffer(image), angle));
    }

    /**
     * Resize input image.
     * 
     * @param image The input image.
     * @param width The new width.
     * @param height The new height.
     * @return The new image with new size.
     */
    public static ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        return new ImageBufferSwt(ToolsSwt.resize(getBuffer(image), width, height));
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    public static ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.flipHorizontal(getBuffer(image)));
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    public static ImageBuffer flipVertical(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.flipVertical(getBuffer(image)));
    }

    /**
     * Apply a filter to the input image.
     * 
     * @param image The input image.
     * @return The filtered image as a new instance.
     */
    public static ImageBuffer applyBilinearFilter(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.applyBilinearFilter(getBuffer(image)));
    }

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param media The output media.
     * @throws LionEngineException If image cannot be read.
     */
    public static void saveImage(ImageBuffer image, Media media) throws LionEngineException
    {
        final OutputStream output = media.getOutputStream();
        try
        {
            ToolsSwt.saveImage(getBuffer(image), output);
        }
        catch (final SWTException exception)
        {
            throw new LionEngineException(exception, ERROR_IMAGE_SAVE);
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (final IOException exception2)
            {
                Verbose.exception(UtilityImage.class, "saveImage", exception2);
            }
        }
    }

    /**
     * Get raster buffer from data.
     * 
     * @param img The image.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     * @param ref The reference size.
     * @return The rastered image.
     */
    public static ImageBuffer getRasterBuffer(ImageBuffer img, int fr, int fg, int fb, int er, int eg, int eb, int ref)
    {
        return new ImageBufferSwt(ToolsSwt.getRasterBuffer(getBuffer(img), fr, fg, fb, er, eg, eb, ref));
    }

    /**
     * Private constructor.
     */
    private UtilityImage()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
