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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

/**
 * Represents the range of the collision for a specified axis. It allows to restrict the computation area of the
 * collision, depending of the input value.
 * 
 * @param output The output target.
 * @param minX The minimum accepted value.
 * @param maxX The maximum accepted value.
 * @param minY The minimum accepted value.
 * @param maxY The maximum accepted value.
 * 
 * @see CollisionRangeConfig
 */
public record CollisionRange(Axis output, int minX, int maxX, int minY, int maxY)
{
    /**
     * Get the output target.
     * 
     * @return The output target.
     */
    public Axis getOutput()
    {
        return output;
    }

    /**
     * Get the minimum horizontal accepted value.
     * 
     * @return The minimum horizontal accepted value.
     */
    public int getMinX()
    {
        return minX;
    }

    /**
     * Get the maximum horizontal accepted value.
     * 
     * @return The maximum horizontal accepted value.
     */
    public int getMaxX()
    {
        return maxX;
    }

    /**
     * Get the minimum vertical accepted value.
     * 
     * @return The minimum vertical accepted value.
     */
    public int getMinY()
    {
        return minY;
    }

    /**
     * Get the maximum vertical accepted value.
     * 
     * @return The maximum vertical accepted value.
     */
    public int getMaxY()
    {
        return maxY;
    }
}
