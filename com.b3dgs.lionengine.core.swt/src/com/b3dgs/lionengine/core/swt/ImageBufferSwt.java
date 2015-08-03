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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;

/**
 * Image buffer implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ImageBufferSwt implements ImageBuffer
{
    /**
     * Get the transparency equivalence.
     * 
     * @param transparency The transparency.
     * @return The equivalence.
     */
    static Transparency getTransparency(int transparency)
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

    /** Transparency. */
    private final Transparency transparency;
    /** Last image data. */
    private final ImageData data;
    /** Image. */
    private Image image;
    /** GC. */
    private GC gc;

    /**
     * Internal constructor.
     * 
     * @param data The image data.
     */
    ImageBufferSwt(ImageData data)
    {
        this.data = data;
        transparency = ImageBufferSwt.getTransparency(data.getTransparencyType());
    }

    /**
     * Internal constructor.
     * 
     * @param image The image.
     */
    ImageBufferSwt(Image image)
    {
        this.image = image;
        data = image.getImageData();
        transparency = ImageBufferSwt.getTransparency(data.getTransparencyType());
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
    public void prepare() throws LionEngineException
    {
        image = new Image(ScreenSwt.display, data);
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
        final Device device = image.getDevice();
        image.dispose();
        image = new Image(device, data);
    }

    @Override
    public void setRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        data.setPixels(startX, startY, w, rgbArray, offset);
        final Device device = image.getDevice();
        image.dispose();
        image = new Image(device, data);
    }

    @Override
    public int getRgb(int x, int y)
    {
        final int pixel = data.getPixel(x, y);
        final PaletteData palette = data.palette;
        final RGB rgb = palette.getRGB(pixel);
        if (pixel == data.transparentPixel)
        {
            return ColorRgba.TRANSPARENT.getRgba();
        }
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

    @Override
    public Transparency getTransparency()
    {
        return transparency;
    }
}
