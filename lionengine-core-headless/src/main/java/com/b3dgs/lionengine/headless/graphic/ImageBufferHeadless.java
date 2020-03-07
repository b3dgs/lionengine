/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.headless.graphic;

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
final class ImageBufferHeadless implements ImageBuffer
{
    /** Buffered image. */
    private final int[] buffer;
    /** Buffer width. */
    private final int width;
    /** Buffer height. */
    private final int height;
    /** Transparency. */
    private final Transparency transparency;

    /**
     * Internal constructor.
     * 
     * @param width The image width (must be strictly positive).
     * @param height The image height (must be strictly positive).
     * @param transparency The transparency used (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    ImageBufferHeadless(int width, int height, Transparency transparency)
    {
        super();

        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);
        Check.notNull(transparency);

        this.width = width;
        this.height = height;
        this.transparency = transparency;
        buffer = new int[width * height];
    }

    /**
     * Internal constructor.
     * 
     * @param width The image width (must be strictly positive).
     * @param height The image height (must be strictly positive).
     * @param pixels The pixels raw data (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    ImageBufferHeadless(int width, int height, int[] pixels)
    {
        super();

        this.width = width;
        this.height = height;
        buffer = new int[pixels.length];
        System.arraycopy(pixels, 0, buffer, 0, pixels.length);
        transparency = Transparency.BITMASK;
    }

    /**
     * Internal constructor.
     * 
     * @param image The image used (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    ImageBufferHeadless(ImageBufferHeadless image)
    {
        super();

        Check.notNull(image);

        width = image.getWidth();
        height = image.getHeight();
        buffer = new int[width * height];
        System.arraycopy(image.buffer, 0, buffer, 0, buffer.length);
        transparency = image.getTransparency();
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
        return new GraphicHeadless(this);
    }

    @Override
    public void dispose()
    {
        // Nothing to do
    }

    @Override
    public void setRgb(int x, int y, int rgb)
    {
        buffer[y * width + x] = rgb;
    }

    @Override
    public void setRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        int yoff = offset;
        int off;
        int color;

        for (int y = startY; y < startY + h; y++, yoff += scansize)
        {
            off = yoff;
            for (int x = startX; x < startX + w; x++)
            {
                color = rgbArray[off++];
                buffer[y * width + x] = color;
            }
        }
    }

    @Override
    public int getRgb(int x, int y)
    {
        final int pixel = buffer[y * width + x];
        if (UtilConversion.mask(pixel >> Constant.BYTE_4) == 0)
        {
            return ColorRgba.TRANSPARENT.getRgba();
        }
        return pixel;
    }

    @Override
    public int[] getRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        int yoff = offset;
        int off;
        int[] pixels = rgbArray;
        if (pixels == null)
        {
            pixels = new int[offset + h * scansize];
        }

        for (int y = startY; y < startY + h; y++, yoff += scansize)
        {
            off = yoff;
            for (int x = startX; x < startX + w; x++)
            {
                pixels[off++] = getRgb(x, y);
            }
        }

        return pixels;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageBufferHeadless getSurface()
    {
        return this;
    }

    @Override
    public Transparency getTransparency()
    {
        return transparency;
    }

    @Override
    public ColorRgba getTransparentColor()
    {
        return ColorRgba.TRANSPARENT;
    }
}
