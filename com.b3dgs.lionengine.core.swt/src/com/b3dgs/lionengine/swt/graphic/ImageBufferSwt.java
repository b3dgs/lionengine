/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.swt.graphic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Image buffer implementation.
 */
public final class ImageBufferSwt implements ImageBuffer
{
    /**
     * Get the transparency equivalence.
     * 
     * @param transparency The transparency.
     * @return The equivalence.
     */
    public static Transparency getTransparency(int transparency)
    {
        return switch (transparency)
        {
            case SWT.TRANSPARENCY_NONE -> Transparency.OPAQUE;
            case SWT.TRANSPARENCY_MASK, SWT.TRANSPARENCY_PIXEL -> Transparency.BITMASK;
            case SWT.TRANSPARENCY_ALPHA -> Transparency.TRANSLUCENT;
            default -> Transparency.OPAQUE;
        };
    }

    /** Device. */
    private final Device device;
    /** Last image data. */
    private final ImageData data;
    /** Transparency. */
    private final Transparency transparency;
    /** Image. */
    private Image image;
    /** GC. */
    private GC gc;

    /**
     * Internal constructor.
     * 
     * @param device The device reference.
     * @param data The image data.
     */
    public ImageBufferSwt(Device device, ImageData data)
    {
        this.device = device;
        this.data = data;
        transparency = getTransparency(data.getTransparencyType());
    }

    /**
     * Internal constructor.
     * 
     * @param image The image.
     */
    public ImageBufferSwt(Image image)
    {
        device = image.getDevice();
        this.image = image;
        data = image.getImageData();
        transparency = getTransparency(data.getTransparencyType());
    }

    /**
     * Get the image buffer.
     * 
     * @return The image buffer.
     */
    Image getBuffer()
    {
        return image;
    }

    @Override
    public void prepare()
    {
        image = new Image(device, data);
    }

    @Override
    public Graphic createGraphic()
    {
        gc = new GC(image);
        return new GraphicSwt(gc);
    }

    @Override
    public void dispose()
    {
        if (gc != null)
        {
            gc.dispose();
            gc = null;
        }
        if (image != null)
        {
            image.dispose();
            image = null;
        }
    }

    @Override
    public void setRgb(int x, int y, int rgb)
    {
        final ColorRgba rgba = new ColorRgba(rgb);
        final RGB color = new RGB(rgba.getRed(), rgba.getGreen(), rgba.getBlue());
        final int pixel = data.palette.getPixel(color);
        data.setPixel(x, y, pixel);
        image.dispose();
        image = new Image(device, data);
    }

    @Override
    public void setRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        data.setPixels(startX, startY, w, rgbArray, offset);
        image.dispose();
        image = new Image(device, data);
    }

    @Override
    public int getRgb(int x, int y)
    {
        final int alpha = data.getAlpha(x, y);
        final int pixel = data.getPixel(x, y);
        final RGB rgb = data.palette.getRGB(pixel);
        if (alpha == 0)
        {
            return new ColorRgba(rgb.red, rgb.green, rgb.blue, 255).getRgba();
        }
        return new ColorRgba(rgb.red, rgb.green, rgb.blue, data.getAlpha(x, y)).getRgba();
    }

    @Override
    public int[] getRgbRef()
    {
        final int length = data.data.length;
        final int[] array = new int[length];
        System.arraycopy(data.data, 0, array, 0, length);
        return array;
    }

    @Override
    public int[] getRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        data.getPixels(startX, startY, w, rgbArray, offset);
        return rgbArray;
    }

    @Override
    public int getWidth()
    {
        return data.width;
    }

    @Override
    public int getHeight()
    {
        return data.height;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Image getSurface()
    {
        return image;
    }

    @Override
    public Transparency getTransparency()
    {
        return transparency;
    }

    @Override
    public ColorRgba getTransparentColor()
    {
        final int pixel = data.transparentPixel;
        if (pixel == -1)
        {
            return null;
        }
        final RGB rgb = data.palette.getRGB(pixel);
        return new ColorRgba(rgb.red, rgb.green, rgb.blue, 255);
    }
}
