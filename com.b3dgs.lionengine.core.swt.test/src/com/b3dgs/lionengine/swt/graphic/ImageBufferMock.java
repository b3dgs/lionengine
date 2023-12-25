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
package com.b3dgs.lionengine.swt.graphic;

import java.util.Arrays;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Mock image buffer.
 */
public class ImageBufferMock implements ImageBuffer
{
    /** Width. */
    private final int width;
    /** Height. */
    private final int height;
    /** Rgba. */
    private final int[] rgba;

    /**
     * Constructor.
     * 
     * @param width The buffer width (must be strictly positive).
     * @param height The buffer height (must be strictly positive).
     */
    public ImageBufferMock(int width, int height)
    {
        super();

        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);

        this.width = width;
        this.height = height;
        rgba = new int[width * height];
    }

    @Override
    public void prepare()
    {
        // Nothing to do
    }

    @Override
    public Graphic createGraphic()
    {
        final Graphic g = new GraphicMock();
        g.setGraphic(new Object());
        return g;
    }

    @Override
    public void dispose()
    {
        // Nothing to do
    }

    @Override
    public void setRgb(int x, int y, int rgb)
    {
        rgba[x % width + y * width] = rgb;
    }

    @Override
    public void setRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        System.arraycopy(rgbArray, 0, rgba, 0, w * h);
    }

    @Override
    public int getRgb(int x, int y)
    {
        return rgba[x % width + y * width];
    }

    @Override
    public int[] getRgbRef()
    {
        return rgba;
    }

    @Override
    public int[] getRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        System.arraycopy(rgba, 0, rgbArray, 0, w * h);

        return rgbArray;
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

    @Override
    public <T> T getSurface()
    {
        return null;
    }

    @Override
    public Transparency getTransparency()
    {
        return Transparency.BITMASK;
    }

    @Override
    public ColorRgba getTransparentColor()
    {
        return ColorRgba.TRANSPARENT;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() == getClass())
        {
            return false;
        }
        final ImageBufferMock other = (ImageBufferMock) object;
        return width == other.width && height == other.height && Arrays.equals(rgba, other.rgba);
    }
}
