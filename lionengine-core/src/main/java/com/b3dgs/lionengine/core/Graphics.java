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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.Transform;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Implementation provider for the {@link FactoryGraphic}.
 * <p>
 * This class is Thread-Safe.
 * </p>
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
     * @param config The config reference.
     * @return The screen instance.
     */
    public static Screen createScreen(Config config)
    {
        return factoryGraphic.createScreen(config);
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
    public static ImageBuffer getImageBuffer(Media media)
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
     * @param h The number of horizontal divisions (strictly positive).
     * @param v The number of vertical divisions (strictly positive).
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
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param media The output media.
     * @throws LionEngineException If an error occurred when saving the image.
     */
    public static void saveImage(ImageBuffer image, Media media)
    {
        factoryGraphic.saveImage(image, media);
    }

    /**
     * Get raster buffer from data.
     * 
     * @param img The image buffer.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     * @param size The reference size.
     * @return The rastered image.
     */
    public static ImageBuffer getRasterBuffer(ImageBuffer img, int fr, int fg, int fb, int er, int eg, int eb, int size)
    {
        return factoryGraphic.getRasterBuffer(img, fr, fg, fb, er, eg, eb, size);
    }

    /**
     * Private constructor.
     */
    private Graphics()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
