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

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;

/**
 * Represents the graphic context factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface FactoryGraphic
{
    /**
     * Create a renderer.
     * 
     * @param config The config reference.
     * @return The renderer instance.
     */
    Renderer createRenderer(Config config);

    /**
     * Create a screen.
     * 
     * @param renderer The renderer reference.
     * @param config The config reference.
     * @return The screen instance.
     */
    Screen createScreen(Renderer renderer, Config config);

    /**
     * Crate a text.
     * 
     * @param fontName The font name.
     * @param size The font size (in pixel).
     * @param style The font style.
     * @return The created text.
     */
    Text createText(String fontName, int size, TextStyle style);

    /**
     * Create a graphic context.
     * 
     * @return The graphic context.
     */
    Graphic createGraphic();

    /**
     * Create an image buffer.
     * 
     * @param width The image width.
     * @param height The image height.
     * @param transparency The image transparency.
     * @return The image buffer.
     */
    ImageBuffer createImageBuffer(int width, int height, Transparency transparency);

    /**
     * Get an image buffer from an image file.
     * 
     * @param media The image media.
     * @param alpha <code>true</code> to enable alpha, <code>false</code> else.
     * @return The created image buffer from file.
     */
    ImageBuffer getImageBuffer(Media media, boolean alpha);

    /**
     * Get an image buffer from an image buffer.
     * 
     * @param imageBuffer The image buffer.
     * @return The created image buffer from file.
     */
    ImageBuffer getImageBuffer(ImageBuffer imageBuffer);

    /**
     * Apply color mask to image.
     * 
     * @param imageBuffer The image reference.
     * @param maskColor The color mask.
     * @return The masked image buffer.
     */
    ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor);

    /**
     * Split an image into an array of sub image (data not shared).
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions.
     * @param v The number of vertical divisions.
     * @return The splited images.
     */
    ImageBuffer[] splitImage(ImageBuffer image, int h, int v);

    /**
     * Rotate input image buffer.
     * 
     * @param image The input image buffer.
     * @param angle The angle to apply in degree (0-359)
     * @return The new image buffer with angle applied.
     */
    ImageBuffer rotate(ImageBuffer image, int angle);

    /**
     * Resize input image buffer.
     * 
     * @param image The input image buffer.
     * @param width The new width.
     * @param height The new height.
     * @return The new image buffer with new size.
     */
    ImageBuffer resize(ImageBuffer image, int width, int height);

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image buffer.
     * @return The flipped image buffer as a new instance.
     */
    ImageBuffer flipHorizontal(ImageBuffer image);

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image buffer.
     * @return The flipped image buffer as a new instance.
     */
    ImageBuffer flipVertical(ImageBuffer image);

    /**
     * Apply a filter to the input image buffer.
     * 
     * @param image The input image.
     * @param filter The filter to use.
     * @return The filtered image as a new instance.
     */
    ImageBuffer applyFilter(ImageBuffer image, Filter filter);

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param media The output media.
     */
    void saveImage(ImageBuffer image, Media media);

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
    ImageBuffer getRasterBuffer(ImageBuffer image, int fr, int fg, int fb, int er, int eg, int eb, int refSize);

    /**
     * Load a raster data from a file.
     * 
     * @param media The raster media path.
     * @return The raster data.
     */
    int[][] loadRaster(Media media);

    /**
     * Create a transform.
     * 
     * @return The created transform.
     */
    Transform createTransform();
}
