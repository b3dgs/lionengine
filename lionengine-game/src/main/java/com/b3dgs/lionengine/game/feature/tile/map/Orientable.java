/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map;

import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Represents something which can be oriented.
 * 
 * @see Orientation
 */
@FeatureInterface
public interface Orientable extends Feature
{
    /**
     * Adjust orientation to face to specified tile.
     * 
     * @param tx The horizontal tile to face.
     * @param ty The vertical tile to face.
     */
    void pointTo(int tx, int ty);

    /**
     * Adjust orientation to face to specified entity.
     * 
     * @param tiled The tiled to face to.
     */
    void pointTo(Tiled tiled);

    /**
     * Set the orientation to use.
     * 
     * @param orientation The orientation to use (must not be <code>null</code>).
     */
    void setOrientation(Orientation orientation);

    /**
     * Get the current orientation.
     * 
     * @return The current orientation.
     */
    Orientation getOrientation();
}
