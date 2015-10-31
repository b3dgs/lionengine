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
package com.b3dgs.lionengine.core.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.Transparency;

/**
 * Image buffer implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ImageBufferAndroid implements ImageBuffer
{
    /** Buffered image. */
    private final Bitmap bufferedImage;

    /**
     * Internal constructor.
     * 
     * @param bufferedImage The buffered image.
     */
    ImageBufferAndroid(Bitmap bufferedImage)
    {
        this.bufferedImage = bufferedImage;
    }

    /**
     * Get the image buffer.
     * 
     * @return The image buffer.
     */
    Bitmap getBuffer()
    {
        return bufferedImage;
    }

    /*
     * ImageBuffer
     */

    @Override
    public void prepare()
    {
        // Nothing to do
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicAndroid(new Canvas(bufferedImage));
    }

    @Override
    public void dispose()
    {
        bufferedImage.recycle();
    }

    @Override
    public void setRgb(int x, int y, int rgb)
    {
        bufferedImage.setPixel(x, y, rgb);
    }

    @Override
    public void setRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        bufferedImage.setPixels(rgbArray, offset, scansize, startX, startY, w, h);
    }

    @Override
    public int getRgb(int x, int y)
    {
        return bufferedImage.getPixel(x, y);
    }

    @Override
    public int[] getRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        bufferedImage.getPixels(rgbArray, offset, scansize, startX, startY, w, h);
        return rgbArray;
    }

    @Override
    public int getWidth()
    {
        return bufferedImage.getWidth();
    }

    @Override
    public int getHeight()
    {
        return bufferedImage.getHeight();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Bitmap getSurface()
    {
        return bufferedImage;
    }

    @Override
    public Transparency getTransparency()
    {
        if (bufferedImage.hasAlpha())
        {
            return Transparency.TRANSLUCENT;
        }
        return Transparency.BITMASK;
    }
}
