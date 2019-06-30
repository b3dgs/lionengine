/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.Mover;

/**
 * Represents something that can be transformed with a translation and size modification.
 */
@FeatureInterface
public interface Transformable extends Feature, Mover, Shape
{
    /**
     * Add a listener.
     * 
     * @param listener The listener to add (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void addListener(TransformableListener listener);

    /**
     * Remove a listener.
     * 
     * @param listener The listener to remove (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void removeListener(TransformableListener listener);

    /**
     * Set current size.
     * 
     * @param width The current width.
     * @param height The current height.
     */
    void setSize(int width, int height);

    /**
     * Transform the transformable.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The width.
     * @param height The height.
     */
    void transform(double x, double y, int width, int height);

    /**
     * Get the old width.
     * 
     * @return The old width.
     */
    int getOldWidth();

    /**
     * Get the old height.
     * 
     * @return The old height.
     */
    int getOldHeight();
}
