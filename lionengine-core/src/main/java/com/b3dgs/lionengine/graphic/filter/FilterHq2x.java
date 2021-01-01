/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.filter;

import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transform;

/**
 * HQ2X implementation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class FilterHq2x implements Filter
{
    /**
     * Create an Hq2x filter.
     */
    public FilterHq2x()
    {
        super();
    }

    /*
     * Filter
     */

    @Override
    public ImageBuffer filter(ImageBuffer source)
    {
        final int width = source.getWidth();
        final int height = source.getHeight();
        final int[] srcData = new int[width * height];
        source.getRgb(0, 0, width, height, srcData, 0, width);

        final RawScale2x scaler = new RawScale2x(width, height);
        final ImageBuffer image = Graphics.createImageBuffer(width * RawScale2x.SCALE,
                                                             height * RawScale2x.SCALE,
                                                             source.getTransparentColor());
        image.setRgb(0,
                     0,
                     width * RawScale2x.SCALE,
                     height * RawScale2x.SCALE,
                     scaler.getScaledData(srcData),
                     0,
                     width * RawScale2x.SCALE);

        return image;
    }

    @Override
    public Transform getTransform(double scaleX, double scaleY)
    {
        final Transform transform = Graphics.createTransform();
        transform.scale(scaleX / RawScale2x.SCALE, scaleY / RawScale2x.SCALE);
        return transform;
    }
}
