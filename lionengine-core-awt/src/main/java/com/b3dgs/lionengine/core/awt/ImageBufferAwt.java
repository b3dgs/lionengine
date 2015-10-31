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
package com.b3dgs.lionengine.core.awt;

import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.Transparency;

/**
 * Image buffer implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ImageBufferAwt implements ImageBuffer
{
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

    @SuppressWarnings("unchecked")
    @Override
    public BufferedImage getSurface()
    {
        return bufferedImage;
    }

    @Override
    public Transparency getTransparency()
    {
        return ToolsAwt.getTransparency(bufferedImage.getTransparency());
    }
}
