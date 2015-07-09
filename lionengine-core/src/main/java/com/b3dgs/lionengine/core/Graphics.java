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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Implementation provider for the {@link FactoryGraphic}.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Graphics
{
    /** Factory graphic implementation. */
    private static volatile FactoryGraphic factoryGraphic;

    /**
     * Set the graphic factory used.
     * 
     * @param factoryGraphic The graphic factory used.
     */
    public static void setFactoryGraphic(FactoryGraphic factoryGraphic)
    {
        Graphics.factoryGraphic = factoryGraphic;
    }

    /**
     * Create a screen.
     * 
     * @param renderer The renderer reference.
     * @return The screen instance.
     */
    public static Screen createScreen(Renderer renderer)
    {
        return factoryGraphic.createScreen(renderer);
    }

    /**
     * Create a graphic context.
     * 
     * @return The graphic context.
     */
    public static Graphic createGraphic()
    {
        return factoryGraphic.createGraphic();
    }

    /**
     * Create a transform.
     * 
     * @return The created transform.
     */
    public static Transform createTransform()
    {
        return factoryGraphic.createTransform();
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
        return factoryGraphic.createText(fontName, size, style);
    }

    /**
     * Create an image buffer.
     * 
     * @param width The image width.
     * @param height The image height.
     * @param transparency The image transparency.
     * @return The image buffer.
     */
    public static ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        return factoryGraphic.createImageBuffer(width, height, transparency);
    }

    /**
     * Get an image buffer from an image file.
     * 
     * @param media The image media.
     * @return The created image buffer from file.
     * @throws LionEngineException If an error occurred when reading the image.
     */
    public static ImageBuffer getImageBuffer(Media media) throws LionEngineException
    {
        return factoryGraphic.getImageBuffer(media);
    }

    /**
     * Get an image buffer from an image buffer.
     * 
     * @param imageBuffer The image buffer.
     * @return The created image buffer from file.
     */
    public static ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        return factoryGraphic.getImageBuffer(imageBuffer);
    }

    /**
     * Apply color mask to the image.
     * 
     * @param imageBuffer The image reference.
     * @param maskColor The color mask.
     * @return The masked image buffer.
     */
    public static ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        return factoryGraphic.applyMask(imageBuffer, maskColor);
    }

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions (> 0).
     * @param v The number of vertical divisions (> 0).
     * @return The splited images array (can not be empty).
     */
    public static ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        return factoryGraphic.splitImage(image, h, v);
    }

    /**
     * Rotate input image buffer.
     * 
     * @param image The input image buffer.
     * @param angle The angle to apply in degree (0-359)
     * @return The new image buffer with angle applied.
     */
    public static ImageBuffer rotate(ImageBuffer image, int angle)
    {
        return factoryGraphic.rotate(image, angle);
    }

    /**
     * Resize input image buffer.
     * 
     * @param image The input image buffer.
     * @param width The new width.
     * @param height The new height.
     * @return The new image buffer with new size.
     */
    public static ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        return factoryGraphic.resize(image, width, height);
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image buffer.
     * @return The flipped image buffer as a new instance.
     */
    public static ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return factoryGraphic.flipHorizontal(image);
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image buffer.
     * @return The flipped image buffer as a new instance.
     */
    public static ImageBuffer flipVertical(ImageBuffer image)
    {
        return factoryGraphic.flipVertical(image);
    }

    /**
     * Apply a filter to the input image buffer.
     * 
     * @param image The input image.
     * @param filter The filter to use.
     * @return The filtered image as a new instance.
     * @throws LionEngineException If the filter is not supported.
     */
    public static ImageBuffer applyFilter(ImageBuffer image, Filter filter) throws LionEngineException
    {
        return factoryGraphic.applyFilter(image, filter);
    }

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param media The output media.
     * @throws LionEngineException If an error occurred when saving the image.
     */
    public static void saveImage(ImageBuffer image, Media media) throws LionEngineException
    {
        factoryGraphic.saveImage(image, media);
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
    public static ImageBuffer getRasterBuffer(ImageBuffer image, int fr, int fg, int fb, int er, int eg, int eb,
            int refSize)
    {
        return factoryGraphic.getRasterBuffer(image, fr, fg, fb, er, eg, eb, refSize);
    }

    /**
     * Load a raster data from a file.
     * 
     * @param media The raster media.
     * @return The raster data (can not be empty).
     * @throws LionEngineException If the raster data from the media are invalid.
     */
    public static int[][] loadRaster(Media media) throws LionEngineException
    {
        final XmlNode raster = Stream.loadXml(media);
        final String[] colors =
        {
            "Red", "Green", "Blue"
        };
        final int indexs = 6;
        final int[][] rasters = new int[colors.length][indexs];
        final int indexStart = 0;
        final int indexStep = 1;
        final int indexForce = 2;
        final int indexAmplitude = 3;
        final int indexOffset = 4;
        final int indexType = 5;
        for (int c = 0; c < colors.length; c++)
        {
            final XmlNode color = raster.getChild(colors[c]);
            rasters[c][indexStart] = Integer.decode(color.readString("start")).intValue();
            rasters[c][indexStep] = Integer.decode(color.readString("step")).intValue();
            rasters[c][indexForce] = color.readInteger("force");
            rasters[c][indexAmplitude] = color.readInteger("amplitude");
            rasters[c][indexOffset] = color.readInteger("offset");
            rasters[c][indexType] = color.readInteger("type");
        }
        return rasters;
    }

    /**
     * Private constructor.
     */
    private Graphics()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
