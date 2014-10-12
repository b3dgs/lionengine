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
package com.b3dgs.lionengine.core.swt;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Misc tools for SWT.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ToolsSwt
{
    /**
     * Create a hidden cursor.
     * 
     * @return Hidden cursor.
     */
    static Cursor createHiddenCursor()
    {
        final Color white = ScreenSwt.display.getSystemColor(SWT.COLOR_WHITE);
        final Color black = ScreenSwt.display.getSystemColor(SWT.COLOR_BLACK);
        final PaletteData palette = new PaletteData(new RGB[]
        {
                white.getRGB(), black.getRGB()
        });
        final ImageData sourceData = new ImageData(16, 16, 1, palette);
        sourceData.transparentPixel = 0;

        return new Cursor(ScreenSwt.display, sourceData, 0, 0);
    }

    /**
     * Create an image.
     * 
     * @param width The image width.
     * @param height The image height.
     * @param transparency The image transparency.
     * @return The image.
     */
    static Image createImage(int width, int height, int transparency)
    {
        final Image image = new Image(ScreenSwt.display, width, height);
        if (transparency != SWT.TRANSPARENCY_NONE)
        {
            final ImageData data = image.getImageData();
            data.transparentPixel = 0;
            return new Image(ScreenSwt.display, data);
        }
        return image;
    }

    /**
     * Get an image from an image file.
     * 
     * @param inputStream The image input stream.
     * @param alpha <code>true</code> to enable alpha, <code>false</code> else.
     * @return The created image from file.
     */
    static Image getImage(InputStream inputStream, boolean alpha)
    {
        return new Image(ScreenSwt.display, inputStream);
    }

    /**
     * Get an image from an image.
     * 
     * @param image The image.
     * @return The created image from file.
     */
    static Image getImage(Image image)
    {
        return new Image(ScreenSwt.display, image, SWT.IMAGE_COPY);
    }

    /**
     * Apply color mask to the image.
     * 
     * @param image The image reference.
     * @param maskColor The color mask.
     * @return The masked image.
     */
    static Image applyMask(Image image, int maskColor)
    {
        // TODO To be implemented
        throw new LionEngineException("Not implemented yet !");
    }

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions (> 0).
     * @param v The number of vertical divisions (> 0).
     * @return The splited images array (can not be empty).
     */
    static Image[] splitImage(Image image, int h, int v)
    {
        final int total = h * v;
        final ImageData data = image.getImageData();
        final int width = data.width / h, height = data.height / v;
        final Image[] images = new Image[total];
        int frame = 0;

        for (int y = 0; y < v; y++)
        {
            for (int x = 0; x < h; x++)
            {
                images[frame] = new Image(ScreenSwt.display, width, height);
                final GC gc = new GC(images[frame]);
                gc.drawImage(image, x * width, y * height, width, height, 0, 0, width, height);
                gc.dispose();
                frame++;
            }
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
    static Image rotate(Image image, int angle)
    {
        final ImageData sourceData = image.getImageData();
        final int width = sourceData.width;
        final int height = sourceData.height;
        final ImageData newData = new ImageData(width, height, sourceData.depth, sourceData.palette);
        newData.transparentPixel = sourceData.transparentPixel;

        final Image rotated = new Image(ScreenSwt.display, newData);
        final GC gc = new GC(rotated);
        final org.eclipse.swt.graphics.Transform transform = new org.eclipse.swt.graphics.Transform(ScreenSwt.display);
        final float rotate = (float) Math.toRadians(angle);
        final float cos = (float) Math.cos(rotate);
        final float sin = (float) Math.sin(rotate);

        transform.setElements(cos, sin, -sin, cos, (float) (width / 2.0), (float) (height / 2.0));
        gc.setTransform(transform);
        gc.drawImage(image, -width / 2, -height / 2);
        gc.dispose();

        return rotated;
    }

    /**
     * Resize input image.
     * 
     * @param image The input image.
     * @param width The new width.
     * @param height The new height.
     * @return The new image with new size.
     */
    static Image resize(Image image, int width, int height)
    {
        return new Image(ScreenSwt.display, image.getImageData().scaledTo(width, height));
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    static Image flipHorizontal(Image image)
    {
        final ImageData data = image.getImageData();
        final int width = data.width, height = data.height;
        final Image flipped = new Image(ScreenSwt.display, width, height);
        final GC gc = new GC(flipped);
        final org.eclipse.swt.graphics.Transform transform = new org.eclipse.swt.graphics.Transform(ScreenSwt.display);

        transform.scale(-1.0f, 1.0f);
        gc.setTransform(transform);
        gc.drawImage(image, 0, 0);
        gc.dispose();

        return flipped;
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    static Image flipVertical(Image image)
    {
        final ImageData data = image.getImageData();
        final int width = data.width, height = data.height;
        final Image flipped = new Image(ScreenSwt.display, width, height);
        final GC gc = new GC(flipped);
        final org.eclipse.swt.graphics.Transform transform = new org.eclipse.swt.graphics.Transform(ScreenSwt.display);

        transform.scale(1.0f, -1.0f);
        gc.setTransform(transform);
        gc.drawImage(image, 0, 0);
        gc.dispose();

        return flipped;
    }

    /**
     * Apply a filter to the input image.
     * 
     * @param image The input image.
     * @param filter The filter to use.
     * @return The filtered image as a new instance.
     * @throws LionEngineException If the filter is not supported.
     */
    static Image applyFilter(Image image, Filter filter) throws LionEngineException
    {
        // TODO To be implemented
        throw new LionEngineException("Not implemented yet !");
    }

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param outputStream The output stream.
     */
    static void saveImage(Image image, OutputStream outputStream)
    {
        final ImageLoader imageLoader = new ImageLoader();
        imageLoader.data = new ImageData[]
        {
            image.getImageData()
        };
        imageLoader.save(outputStream, SWT.IMAGE_PNG);
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
    static Image getRasterBuffer(Image image, int fr, int fg, int fb, int er, int eg, int eb, int refSize)
    {
        // TODO To be implemented
        throw new LionEngineException("Not implemented yet !");
    }

    /**
     * Constructor.
     */
    private ToolsSwt()
    {
        throw new RuntimeException();
    }
}
