/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.awt.graphic;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Image buffer implementation.
 */
final class ImageBufferAwt implements ImageBuffer
{
    /** Buffered image. */
    private final BufferedImage bufferedImage;

    /**
     * Internal constructor.
     * 
     * @param bufferedImage The buffered image (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    ImageBufferAwt(BufferedImage bufferedImage)
    {
        super();

        Check.notNull(bufferedImage);

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
        // Slow, can be replaced without unexpected behavior
        // bufferedImage.setRGB(startX, startY, w, h, rgbArray, offset, scansize);
        System.arraycopy(rgbArray,
                         0,
                         ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData(),
                         0,
                         rgbArray.length);
    }

    @Override
    public int getRgb(int x, int y)
    {
        final int pixel = bufferedImage.getRGB(x, y);
        if (UtilConversion.mask(pixel >> Constant.BYTE_4) == 0)
        {
            return ColorRgba.TRANSPARENT.getRgba();
        }
        return pixel;
    }

    @Override
    public int[] getRgbRef()
    {
        return ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
    }

    @Override
    public int[] getRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        // Fast, but not working with flipped rendered images
        // final int[] imgData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        // System.arraycopy(imgData, 0, rgbArray, 0, imgData.length);
        // return rgbArray;
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
        final Transparency value;
        switch (bufferedImage.getTransparency())
        {
            case java.awt.Transparency.OPAQUE:
                value = Transparency.OPAQUE;
                break;
            case java.awt.Transparency.BITMASK:
                value = Transparency.BITMASK;
                break;
            default:
                value = Transparency.TRANSLUCENT;
        }
        return value;
    }

    @Override
    public ColorRgba getTransparentColor()
    {
        return ColorRgba.TRANSPARENT;
    }
}
