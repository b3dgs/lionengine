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
package com.b3dgs.lionengine.core.awt;

import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;

/**
 * Image buffer implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ImageBufferAwt
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
            case java.awt.Transparency.OPAQUE:
                return Transparency.OPAQUE;
            case java.awt.Transparency.BITMASK:
                return Transparency.BITMASK;
            case java.awt.Transparency.TRANSLUCENT:
                return Transparency.TRANSLUCENT;
            default:
                return Transparency.OPAQUE;
        }
    }

    /** Buffered image. */
    private final BufferedImage bufferedImage;

    /**
     * Internal constructor.
     * 
     * @param bufferedImage The buffered image.
     */
    ImageBufferAwt(BufferedImage bufferedImage)
    {
        this.bufferedImage = bufferedImage;
    }

    /**
     * Get the image buffer.
     * 
     * @return The image buffer.
     */
    BufferedImage getBuffer()
    {
        return bufferedImage;
    }

    /*
     * ImageBuffer
     */

    @Override
    public Graphic createGraphic()
    {
        return new GraphicAwt(bufferedImage.createGraphics());
    }

    @Override
    public void dispose()
    {
        // Nothing to do
    }

    @Override
    public void setRgb(int x, int y, int rgb)
    {
        bufferedImage.setRGB(x, y, rgb);
    }

    @Override
    public void setRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        bufferedImage.setRGB(startX, startY, w, h, rgbArray, offset, scansize);
    }

    @Override
    public int getRgb(int x, int y)
    {
        return bufferedImage.getRGB(x, y);
    }

    @Override
    public int[] getRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        return bufferedImage.getRGB(startX, startY, w, h, rgbArray, offset, scansize);
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

    @Override
    public Transparency getTransparency()
    {
        return ImageBufferAwt.getTransparency(bufferedImage.getTransparency());
    }
}
