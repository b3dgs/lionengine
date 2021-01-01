/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Represents the graphic factory.
 */
public interface FactoryGraphic
{
    /**
     * Create a screen.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The screen instance.
     * @throws LionEngineException If invalid argument.
     */
    Screen createScreen(Config config);

    /**
     * Create a graphic context.
     * 
     * @return The graphic context.
     */
    Graphic createGraphic();

    /**
     * Create a transform.
     * 
     * @return The created transform.
     */
    Transform createTransform();

    /**
     * Crate a text.
     * 
     * @param fontName The font name (must not be <code>null</code>).
     * @param size The font size in pixel (must be strictly positive).
     * @param style The font style (must not be <code>null</code>).
     * @return The created text.
     * @throws LionEngineException If invalid arguments.
     */
    Text createText(String fontName, int size, TextStyle style);

    /**
     * Create an image buffer.
     * 
     * @param width The image width (must be strictly positive).
     * @param height The image height (must be strictly positive).
     * @return The image buffer.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer createImageBuffer(int width, int height);

    /**
     * Create an image buffer.
     * 
     * @param width The image width (must be strictly positive).
     * @param height The image height (must be strictly positive).
     * @param transparency The color transparency pixel (must be strictly positive).
     * @return The image buffer.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer createImageBuffer(int width, int height, ColorRgba transparency);

    /**
     * Get an image buffer from an image file.
     * 
     * @param media The image media (must not be <code>null</code>).
     * @return The created image buffer from file.
     * @throws LionEngineException If an error occurred when reading the image.
     */
    ImageBuffer getImageBuffer(Media media);

    /**
     * Get an image buffer from an image buffer.
     * 
     * @param imageBuffer The image buffer (must not be <code>null</code>).
     * @return The created image buffer from file.
     * @throws LionEngineException If invalid argument.
     */
    ImageBuffer getImageBuffer(ImageBuffer imageBuffer);

    /**
     * Apply color mask to the image.
     * 
     * @param imageBuffer The image reference (must not be <code>null</code>).
     * @param maskColor The color mask (must not be <code>null</code>).
     * @return The masked image buffer.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor);

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split (must not be <code>null</code>).
     * @param h The number of horizontal divisions (must be strictly positive).
     * @param v The number of vertical divisions (must be strictly positive).
     * @return The splited images array (can not be empty).
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer[] splitImage(ImageBuffer image, int h, int v);

    /**
     * Rotate input image buffer.
     * 
     * @param image The input image buffer (must not be <code>null</code>).
     * @param angle The angle to apply in degree (0-359)
     * @return The new image buffer with angle applied.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer rotate(ImageBuffer image, int angle);

    /**
     * Resize input image buffer.
     * 
     * @param image The input image buffer (must not be <code>null</code>).
     * @param width The new width (must be strictly positive).
     * @param height The new height (must be strictly positive).
     * @return The new image buffer with new size.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer resize(ImageBuffer image, int width, int height);

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image buffer (must not be <code>null</code>).
     * @return The flipped image buffer as a new instance.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer flipHorizontal(ImageBuffer image);

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image buffer (must not be <code>null</code>).
     * @return The flipped image buffer as a new instance.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer flipVertical(ImageBuffer image);

    /**
     * Save an image into a file.
     * 
     * @param image The image to save (must not be <code>null</code>).
     * @param media The output media (must not be <code>null</code>).
     * @throws LionEngineException If an error occurred when saving the image.
     */
    void saveImage(ImageBuffer image, Media media);

    /**
     * Generate tile set from images.
     * 
     * @param images The images to assemble.
     * @param media The tile set output.
     */
    void generateTileset(ImageBuffer[] images, Media media);

    /**
     * Get raster buffer from data.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @return The rastered image.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer getRasterBuffer(ImageBuffer image, double fr, double fg, double fb);

    /**
     * Get raster buffers from palette.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The raster palette (must not be <code>null</code>).
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer[] getRasterBuffer(ImageBuffer image, ImageBuffer palette);

    /**
     * Get raster buffer from first palette, fill for each height until tile size.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The raster palette (must not be <code>null</code>).
     * @param fh The horizontal frames.
     * @param fv The vertical frames.
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer[] getRasterBufferSmooth(ImageBuffer image, ImageBuffer palette, int fh, int fv);

    /**
     * Get raster buffer from first palette, fill for each height until tile size.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The raster palette (must not be <code>null</code>).
     * @param tileHeight The tile height.
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer[] getRasterBufferSmooth(ImageBuffer image, ImageBuffer palette, int tileHeight);

    /**
     * Get raster buffer with offsets applied.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The palette offset (must not be <code>null</code>).
     * @param raster The raster color (must not be <code>null</code>).
     * @param offsets The offsets number (rasters inside).
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    ImageBuffer[] getRasterBufferOffset(Media image, Media palette, Media raster, int offsets);
}
