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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Transparency;

/**
 * Image buffer implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ImageBufferImpl
        implements ImageBuffer
{
    /**
     * Get the transparency equivalence.
     * 
     * @param transparency The transparency.
     * @return The equivalence.
     */
    static Transparency getTransparency(int transparency)
    {
        switch (transparency)
        {
            case SWT.TRANSPARENCY_NONE:
                return Transparency.OPAQUE;
            case SWT.TRANSPARENCY_MASK:
                return Transparency.BITMASK;
            case SWT.TRANSPARENCY_ALPHA:
                return Transparency.TRANSLUCENT;
            default:
                return Transparency.OPAQUE;
        }
    }

    /** Transparency. */
    private final Transparency transparency;
    /** Image. */
    private Image image;
    /** GC. */
    private GC gc;

    /**
     * Constructor.
     * 
     * @param image The image.
     */
    ImageBufferImpl(Image image)
    {
        this.image = image;
        final ImageData data = image.getImageData();
        transparency = ImageBufferImpl.getTransparency(data.getTransparencyType());
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
    public Graphic createGraphic()
    {
        gc = new GC(image);
        return new GraphicImpl(gc);
    }

    @Override
    public void setRgb(int x, int y, int rgb)
    {
        final ImageData data = image.getImageData();
        data.setPixel(x, y, rgb);
        image.dispose();
        image = new Image(image.getDevice(), data);
    }

    @Override
    public void setRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        final ImageData data = image.getImageData();
        data.setPixels(startX, startY, w, rgbArray, offset);
        image.dispose();
        image = new Image(image.getDevice(), data);
    }

    @Override
    public int getRgb(int x, int y)
    {
        return image.getImageData().getPixel(x, y);
    }

    @Override
    public int[] getRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        image.getImageData().getPixels(startX, startY, w, rgbArray, offset);
        return rgbArray;
    }

    @Override
    public int getWidth()
    {
        return image.getBounds().width;
    }

    @Override
    public int getHeight()
    {
        return image.getBounds().height;
    }

    @Override
    public Transparency getTransparency()
    {
        return transparency;
    }
}
