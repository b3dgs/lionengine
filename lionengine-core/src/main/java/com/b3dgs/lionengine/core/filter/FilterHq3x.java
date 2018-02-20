/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.filter;

import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transform;

/**
 * HQ3X implementation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class FilterHq3x implements Filter
{
    /**
     * Create an Hq3x filter.
     */
    public FilterHq3x()
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

        final RawScale3x scaler = new RawScale3x(width, height);
        final ImageBuffer image = Graphics.createImageBuffer(width * RawScale3x.SCALE,
                                                             height * RawScale3x.SCALE,
                                                             source.getTransparentColor());
        image.setRgb(0,
                     0,
                     width * RawScale3x.SCALE,
                     height * RawScale3x.SCALE,
                     scaler.getScaledData(srcData),
                     0,
                     width * RawScale3x.SCALE);
        return image;
    }

    @Override
    public Transform getTransform(double scaleX, double scaleY)
    {
        final Transform transform = Graphics.createTransform();
        transform.scale(scaleX / RawScale3x.SCALE, scaleY / RawScale3x.SCALE);
        return transform;
    }
}
