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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;

import com.b3dgs.lionengine.ColorRgba;

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
    public static Cursor createHiddenCursor()
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
    public static Image createImage(int width, int height, int transparency)
    {
        final Image image = new Image(ScreenSwt.display, width, height);
        if (transparency != SWT.TRANSPARENCY_NONE)
        {
            final ImageData data = image.getImageData();
            data.transparentPixel = ColorRgba.TRANSPARENT.getRgba();
            return new Image(ScreenSwt.display, data);
        }
        return image;
    }

    /**
     * Get an image data from an image file.
     * 
     * @param inputStream The image input stream.
     * @return The created image from file.
     */
    public static ImageData getImageData(InputStream inputStream)
    {
        return new ImageData(inputStream);
    }

    /**
     * Get an image from an image file.
     * 
     * @param inputStream The image input stream.
     * @return The created image from file.
     */
    public static Image getImage(InputStream inputStream)
    {
        return new Image(ScreenSwt.display, inputStream);
    }

    /**
     * Get an image from an image.
     * 
     * @param image The image.
     * @return The created image from file.
     */
    public static Image getImage(Image image)
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
    public static Image applyMask(Image image, int maskColor)
    {
        final ImageData sourceData = image.getImageData();
        final int width = sourceData.width;
        final int height = sourceData.height;

        final ImageData newData = new ImageData(width, height, sourceData.depth, sourceData.palette);
        final ColorRgba mask = new ColorRgba(maskColor);
        newData.transparentPixel = newData.palette.getPixel(new RGB(mask.getRed(), mask.getGreen(), mask.getBlue()));

        return new Image(ScreenSwt.display, newData);
    }

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions (> 0).
     * @param v The number of vertical divisions (> 0).
     * @return The splited images array (can not be empty).
     */
    public static Image[] splitImage(Image image, int h, int v)
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
    public static Image rotate(Image image, int angle)
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
    public static Image resize(Image image, int width, int height)
    {
        return new Image(ScreenSwt.display, image.getImageData().scaledTo(width, height));
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    public static Image flipHorizontal(Image image)
    {
        return flip(image, false);
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     */
    public static Image flipVertical(Image image)
    {
        return flip(image, true);
    }

    /**
     * Apply a filter to the input image.
     * 
     * @param image The input image.
     * @return The filtered image as a new instance.
     */
    public static Image applyBilinearFilter(Image image)
    {
        final ImageData data = image.getImageData();
        final int width = data.width, height = data.height;

        final Image filtered = createImage(width * 2, height * 2, SWT.TRANSPARENCY_ALPHA);
        final GC gc = new GC(filtered);

        final Transform transform = new Transform(ScreenSwt.display);
        transform.scale(2.0f, 2.0f);
        gc.setTransform(transform);
        gc.drawImage(image, 0, 0);
        gc.dispose();

        final Image filtered2 = createImage(width, height, SWT.TRANSPARENCY_ALPHA);
        final GC gc2 = new GC(filtered2);
        final Transform transform2 = new Transform(ScreenSwt.display);
        transform2.scale(0.5f, 0.5f);
        gc2.setTransform(transform2);
        gc2.drawImage(filtered, 0, 0);
        gc2.dispose();

        return filtered2;
    }

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param outputStream The output stream.
     */
    public static void saveImage(Image image, OutputStream outputStream)
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
    public static Image getRasterBuffer(Image image, int fr, int fg, int fb, int er, int eg, int eb, int refSize)
    {
        final ImageData data = image.getImageData();
        final PaletteData palette = data.palette;
        final RGB[] colors = palette.getRGBs();
        final Map<Integer, RGB> newColors = new TreeMap<>();
        final Map<RGB, Integer> newColorsPixel = new HashMap<>();
        for (final RGB color : colors)
        {
            final ColorRgba colorRgba = new ColorRgba(color.red, color.green, color.blue);
            newColors.put(Integer.valueOf(colorRgba.getRgba()), color);
            newColorsPixel.put(color, Integer.valueOf(palette.getPixel(color)));
        }

        final double sr = -((er - fr) / 0x010000) / (double) refSize;
        final double sg = -((eg - fg) / 0x000100) / (double) refSize;
        final double sb = -((eb - fb) / 0x000001) / (double) refSize;

        int lastPixel = newColorsPixel.size();
        final int pixels[][] = new int[data.width][data.height];
        for (int i = 0; i < data.width; i++)
        {
            for (int j = 0; j < data.height; j++)
            {
                final int r = (int) (sr * (j % refSize)) * 0x010000;
                final int g = (int) (sg * (j % refSize)) * 0x000100;
                final int b = (int) (sb * (j % refSize)) * 0x000001;

                final int pixel = data.getPixel(i, j);
                if (pixel != data.transparentPixel)
                {
                    final RGB rgb = palette.getRGB(pixel);
                    final ColorRgba colorRgba = new ColorRgba(rgb.red, rgb.green, rgb.blue);

                    final int filter = ColorRgba.filterRgb(colorRgba.getRgba(), fr + r, fg + g, fb + b);
                    final ColorRgba output = new ColorRgba(filter);

                    final Integer rasterRgba = Integer.valueOf(output.getRgba());
                    final RGB rasterColor = new RGB(output.getRed(), output.getGreen(), output.getBlue());
                    if (!newColors.containsKey(rasterRgba))
                    {
                        newColors.put(rasterRgba, rasterColor);
                        newColorsPixel.put(rasterColor, Integer.valueOf(lastPixel));
                        lastPixel++;
                    }
                    pixels[i][j] = newColorsPixel.get(rasterColor).intValue();
                    data.setPixel(i, j, pixels[i][j]);
                }
            }
        }
        final RGB[] newColorsRgb = new RGB[newColorsPixel.size()];
        for (final Map.Entry<RGB, Integer> current : newColorsPixel.entrySet())
        {
            newColorsRgb[current.getValue().intValue()] = current.getKey();
        }
        palette.colors = newColorsRgb;

        return new Image(ScreenSwt.display, data);
    }

    /**
     * Flip an image depending of the axis.
     * 
     * @param image The image source.
     * @param vertical <code>true</code> if vertical, <code>false</code> if horizontal.
     * @return The flipped image data.
     */
    public static Image flip(Image image, boolean vertical)
    {
        final ImageData data = image.getImageData();
        final ImageData flip = image.getImageData();
        for (int y = 0; y < data.height; y++)
        {
            for (int x = 0; x < data.width; x++)
            {
                flip.setPixel(data.width - x - 1, y, data.getPixel(x, y));
            }
        }
        return new Image(ScreenSwt.display, flip);
    }

    /**
     * Private constructor.
     */
    private ToolsSwt()
    {
        throw new RuntimeException();
    }
}
