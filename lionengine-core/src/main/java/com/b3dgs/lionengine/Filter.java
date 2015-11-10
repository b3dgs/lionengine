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
package com.b3dgs.lionengine;

import com.b3dgs.lionengine.core.Graphics;

/**
 * List of supported filters.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public interface Filter
{
    /** No filter. */
    Filter NO_FILTER = new Filter()
    {
        @Override
        public ImageBuffer filter(ImageBuffer source)
        {
            return source;
        }

        @Override
        public Transform getTransform(double scaleX, double scaleY)
        {
            final Transform transform = Graphics.createTransform();
            transform.scale(scaleX, scaleY);
            return transform;
        }
    };

    /**
     * Apply a filter to the image source.
     * 
     * @param source The image source.
     * @return The filtered image.
     */
    ImageBuffer filter(ImageBuffer source);

    /**
     * Get the associated transform.
     * 
     * @param scaleX The horizontal scale.
     * @param scaleY The vertical scale.
     * @return The associated transform.
     */
    Transform getTransform(double scaleX, double scaleY);
}
