/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Transparency;
import com.b3dgs.lionengine.graphic.UtilColor;

/**
 * Misc tools for SWT.
 */
public final class ToolsSwt
{
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
     * Create an image.
     * 
     * @param width The image width.
     * @param height The image height.
     * @param transparency The image transparency.
     * @return The image.
     * @throws SWTException If error on getting data.
     */
    public static Image createImage(int width, int height, int transparency)
    {
        final Device device = getDisplay();
        if (transparency != SWT.TRANSPARENCY_NONE)
        {
            final PaletteData palette = new PaletteData(0xff, 0xff00, 0xff0000);
            final ImageData data = new ImageData(width, height, 24, palette);
            data.transparentPixel = -1;

            final byte[] alpha = new byte[data.width];
            Arrays.fill(alpha, (byte) 255);

            for (int y = 0; y < data.height; y++)
            {
                data.setAlphas(0, y, alpha.length, alpha, 0);
            }
        }
        return new Image(device, width, height);
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
    public static Image createImage(int width, int height, ColorRgba transparency)
    {
        final Device device = getDisplay();

        final PaletteData palette = new PaletteData(0xff, 0xff00, 0xff0000);
        final ImageData data = new ImageData(width, height, 24, palette);
        final int transparent = palette.getPixel(new RGB(transparency.getRed(),
                                                         transparency.getGreen(),
                                                         transparency.getBlue()));
        data.transparentPixel = transparent;

        final int[] pixels = new int[data.width];
        for (int i = 0; i < pixels.length; i++)
        {
            pixels[i] = transparent;
        }
        for (int y = 0; y < data.height; y++)
        {
            data.setPixels(0, y, pixels.length, pixels, 0);
        }

        return new Image(device, data);
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
        if (Transparency.OPAQUE == transparency)
        {
            value = SWT.TRANSPARENCY_NONE;
        }
        else if (Transparency.BITMASK == transparency)
        {
            value = SWT.TRANSPARENCY_PIXEL;
        }
        else if (Transparency.TRANSLUCENT == transparency)
        {
            value = SWT.TRANSPARENCY_ALPHA;
        }
        else
        {
            throw new LionEngineException(transparency);
        }
        return value;
    }

    /**
     * Create a hidden cursor.
     * 
     * @param device The device reference.
     * @return Hidden cursor.
     * @throws SWTException If error on getting data.
     */
    public static Cursor createHiddenCursor(Device device)
    {
        final Color white = device.getSystemColor(SWT.COLOR_WHITE);
        final Color black = device.getSystemColor(SWT.COLOR_BLACK);
        final PaletteData palette = new PaletteData(new RGB[]
        {
            white.getRGB(), black.getRGB()
        });
        final ImageData sourceData = new ImageData(16, 16, 1, palette);
        sourceData.transparentPixel = 0;

        return new Cursor(device, sourceData, 0, 0);
    }

    /**
     * Get an image data from an image file.
     * 
     * @param input The image input stream.
     * @return The created image from file.
     * @throws SWTException If error on getting data.
     */
    public static ImageData getImageData(InputStream input)
    {
        return new ImageData(input);
    }

    /**
     * Get an image from an image file.
     * 
     * @param device The device reference.
     * @param input The image input stream.
     * @return The created image from file.
     * @throws SWTException If error on getting data.
     */
    public static Image getImage(Device device, InputStream input)
    {
        return new Image(device, input);
    }

    /**
     * Get an image from an image.
     * 
     * @param image The image.
     * @return The created image from file.
     * @throws SWTException If error on getting data.
     */
    public static Image getImage(Image image)
    {
        return new Image(image.getDevice(), image, SWT.IMAGE_COPY);
    }

    /**
     * Apply color mask to the image.
     * 
     * @param image The image reference.
     * @param maskColor The color mask.
     * @return The masked image.
     * @throws SWTException If error on getting data.
     */
    public static Image applyMask(Image image, int maskColor)
    {
        final ImageData sourceData = image.getImageData();
        final int width = sourceData.width;
        final int height = sourceData.height;

        final ImageData newData = new ImageData(width, height, sourceData.depth, sourceData.palette);
        final ColorRgba mask = new ColorRgba(maskColor);
        newData.transparentPixel = newData.palette.getPixel(new RGB(mask.getRed(), mask.getGreen(), mask.getBlue()));

        return new Image(image.getDevice(), newData);
    }

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions (strictly positive).
     * @param v The number of vertical divisions (strictly positive).
     * @return The splited images array (can not be empty).
     * @throws SWTException If error on getting data.
     */
    public static Image[] splitImage(Image image, int h, int v)
    {
        final int total = h * v;
        final ImageData data = image.getImageData();
        final int width = data.width / h;
        final int height = data.height / v;
        final Image[] images = new Image[total];
        int frame = 0;

        for (int y = 0; y < v; y++)
        {
            for (int x = 0; x < h; x++)
            {
                images[frame] = new Image(image.getDevice(), width, height);
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
     * @param angle The angle to apply in degree (0-359).
     * @return The new image with angle applied.
     * @throws SWTException If error on getting data.
     */
    public static Image rotate(Image image, int angle)
    {
        final ImageData sourceData = image.getImageData();
        final int width = sourceData.width;
        final int height = sourceData.height;

        final Rectangle rectangle = new Rectangle(0, 0, width, height);
        final Rectangle r = rectangle.rotate(angle);

        final ImageData newData = new ImageData(r.getWidth(), r.getHeight(), sourceData.depth, sourceData.palette);
        newData.transparentPixel = sourceData.transparentPixel;

        final Device device = image.getDevice();
        final Image rotated = new Image(device, newData);
        final GC gc = new GC(rotated);
        final org.eclipse.swt.graphics.Transform transform = new org.eclipse.swt.graphics.Transform(device);
        final float rotate = (float) Math.toRadians(angle);
        final float cos = (float) Math.cos(rotate);
        final float sin = (float) Math.sin(rotate);

        transform.setElements(cos, sin, -sin, cos, (float) (width / 2.0), (float) (height / 2.0));
        gc.setTransform(transform);
        gc.drawImage(image, (int) ((r.getWidth() - width) / 2.0), (int) ((r.getHeight() - height) / 2.0));
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
     * @throws SWTException If error on getting data.
     */
    public static Image resize(Image image, int width, int height)
    {
        return new Image(image.getDevice(), image.getImageData().scaledTo(width, height));
    }

    /**
     * Flip an image depending of the axis.
     * 
     * @param image The image source.
     * @param vertical <code>true</code> if vertical, <code>false</code> if horizontal.
     * @return The flipped image data.
     * @throws SWTException If error on getting data.
     */
    public static Image flip(Image image, boolean vertical)
    {
        final ImageData data = image.getImageData();
        final ImageData flip = image.getImageData();
        for (int y = 0; y < data.height; y++)
        {
            for (int x = 0; x < data.width; x++)
            {
                final int pixel = data.getPixel(x, y);
                if (vertical)
                {
                    flip.setPixel(data.width - x - 1, y, pixel);
                }
                else
                {
                    flip.setPixel(x, data.height - y - 1, pixel);
                }
            }
        }
        return new Image(image.getDevice(), flip);
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image.
     * @return The flipped image as a new instance.
     * @throws SWTException If error on getting data.
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
     * @throws SWTException If error on getting data.
     */
    public static Image flipVertical(Image image)
    {
        return flip(image, true);
    }

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param output The output stream.
     * @throws SWTException If error on getting data.
     */
    public static void saveImage(Image image, OutputStream output)
    {
        final ImageLoader imageLoader = new ImageLoader();
        final ImageData data = image.getImageData();
        final boolean hasAlpha = data.alphaData != null;
        for (int x = 0; x < data.width; x++)
        {
            for (int y = 0; y < data.height; y++)
            {
                if (data.getPixel(x, y) == data.transparentPixel)
                {
                    data.setAlpha(x, y, 0);
                }
                else if (!hasAlpha)
                {
                    data.setAlpha(x, y, 255);
                }
            }
        }
        data.transparentPixel = -1;
        imageLoader.data = new ImageData[]
        {
            data
        };
        imageLoader.save(output, SWT.IMAGE_PNG);
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
     * @throws SWTException If error on getting data.
     */
    public static Image getRasterBuffer(Image image, int fr, int fg, int fb, int er, int eg, int eb, int refSize)
    {
        final ImageData data = image.getImageData();
        final PaletteData palette = data.palette;
        final RGB[] colors = palette.getRGBs();
        final Map<Integer, RGB> newColors = new TreeMap<>();
        final Map<RGB, Integer> newColorsPixel = new HashMap<>();
        if (colors != null)
        {
            for (final RGB color : colors)
            {
                final ColorRgba colorRgba = new ColorRgba(color.red, color.green, color.blue);
                newColors.put(Integer.valueOf(colorRgba.getRgba()), color);
                newColorsPixel.put(color, Integer.valueOf(palette.getPixel(color)));
            }
        }

        final int divisorRed = 0x010000;
        final int divisorGreen = 0x000100;
        final int divisorBlue = 0x000001;

        final double sr = -((er - fr) / (double) divisorRed) / refSize;
        final double sg = -((eg - fg) / (double) divisorGreen) / refSize;
        final double sb = -((eb - fb) / (double) divisorBlue) / refSize;

        int lastPixel = newColorsPixel.size();
        final int[][] pixels = new int[data.width][data.height];
        for (int i = 0; i < data.width; i++)
        {
            for (int j = 0; j < data.height; j++)
            {
                final int r = (int) (sr * (j % refSize)) * divisorRed;
                final int g = (int) (sg * (j % refSize)) * divisorGreen;
                final int b = (int) (sb * (j % refSize)) * divisorBlue;

                final int pixel = data.getPixel(i, j);
                if (pixel != data.transparentPixel)
                {
                    final RGB rgb = palette.getRGB(pixel);
                    final ColorRgba colorRgba = new ColorRgba(rgb.red, rgb.green, rgb.blue);

                    final int filter = UtilColor.filterRgb(colorRgba.getRgba(), fr + r, fg + g, fb + b);
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

        return new Image(image.getDevice(), data);
    }

    /**
     * Private constructor.
     */
    private ToolsSwt()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
