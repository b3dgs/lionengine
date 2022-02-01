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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

/**
 * Describe the collision function to apply from an input value.
 * 
 * @see CollisionFunctionConfig
 * @see CollisionFormula
 */
public interface CollisionFunction
{
    /**
     * Compute the collision from input value.
     * 
     * @param input The input value.
     * @return The output value.
     */
    double compute(double input);

    /**
     * Get the collision vertical render from input value.
     * 
     * @param input The input value.
     * @return The output value.
     */
    int getRenderX(double input);

    /**
     * Get the collision horizontal render from input value.
     * 
     * @param input The input value.
     * @return The output value.
     */
    int getRenderY(double input);

    /**
     * Get the collision function type.
     * 
     * @return The function type.
     */
    CollisionFunctionType getType();
}
