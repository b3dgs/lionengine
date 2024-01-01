/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.LionEngineException;

/**
 * List of supported filters.
 */
public interface Filter
{
    /**
     * Apply a filter to the image source.
     * 
     * @param source The image source (must not be <code>null</code>).
     * @return The filtered image.
     * @throws LionEngineException If invalid argument.
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

    /**
     * Get the internal buffer scaling.
     * 
     * @return 1 by default, more for better quality.
     */
    default int getScale()
    {
        return 1;
    }

    /**
     * Close filter. Does nothing by default.
     */
    default void close()
    {
        // Nothing by default
    }
}
