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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transform;

/**
 * No filter implementation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class FilterNone implements Filter
{
    /** Single instance. */
    public static final FilterNone INSTANCE = new FilterNone();

    /**
     * Create filter.
     */
    private FilterNone()
    {
        super();
    }

    /*
     * Filter
     */

    @Override
    public ImageBuffer filter(ImageBuffer source)
    {
        return source;
    }

    @Override
    public Transform getTransform(double scaleX, double scaleY)
    {
        return new TransformNone(scaleX, scaleY);
    }
}
