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
package com.b3dgs.lionengine.game.feature.tile.map.transition.fog;

import java.util.function.BooleanSupplier;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Represents something that have a field of view, able to see until a defined range only.
 */
@FeatureInterface
public interface Fovable extends Feature, Tiled
{
    /**
     * Set the field of view value (in tile).
     * 
     * @param fov The field of view value (in tile).
     */
    void setFov(int fov);

    /**
     * Get the field of view in tile.
     * 
     * @return The field of view in tile.
     */
    int getInTileFov();

    /**
     * Set the checker rule to update.
     * 
     * @param checker The checker reference (must not be <code>null</code>).
     * @throws LionEngineException If <code>null</code> argument.
     */
    void setCanUpdate(BooleanSupplier checker);

    /**
     * Check if can update field of view.
     * 
     * @return <code>true</code> if can update field of view, <code>false</code> else.
     */
    boolean canUpdate();
}
