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
package com.b3dgs.lionengine.core.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.Transparency;

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
        final Transparency value;
        switch (transparency)
        {
            case SWT.TRANSPARENCY_NONE:
                value = Transparency.OPAQUE;
                break;
            case SWT.TRANSPARENCY_MASK:
                value = Transparency.BITMASK;
                break;
            case SWT.TRANSPARENCY_PIXEL:
            case SWT.TRANSPARENCY_ALPHA:
                value = Transparency.TRANSLUCENT;
                break;
            default:
                value = Transparency.OPAQUE;
        }
        return value;
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
    ImageBufferSwt(Device device, ImageData data)
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
    ImageBufferSwt(Image image)
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

    /*
     * ImageBuffer
     */

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
        final int pixel = data.getPixel(x, y);
        if (pixel == data.transparentPixel)
        {
            return ColorRgba.TRANSPARENT.getRgba();
        }
        final RGB rgb = data.palette.getRGB(pixel);
        return new ColorRgba(rgb.red, rgb.green, rgb.blue, data.getAlpha(x, y)).getRgba();
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
}
