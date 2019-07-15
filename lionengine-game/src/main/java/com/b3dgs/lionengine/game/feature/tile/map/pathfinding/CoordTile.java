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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

/**
 * Represents a coordinate in tile.
 */
public class CoordTile
{
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 22;

    /** Horizontal tile location. */
    private final int tx;
    /** Vertical tile location. */
    private final int ty;

    /**
     * Create a coord tile.
     * 
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     */
    public CoordTile(int tx, int ty)
    {
        super();

        this.tx = tx;
        this.ty = ty;
    }

    /**
     * Get the horizontal tile location.
     * 
     * @return The horizontal tile location.
     */
    public int getX()
    {
        return tx;
    }

    /**
     * Get the vertical tile location.
     * 
     * @return The vertical tile location.
     */
    public int getY()
    {
        return ty;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + tx;
        result = prime * result + ty;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final CoordTile other = (CoordTile) object;
        return other.tx == tx && other.ty == ty;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [tx=")
                                            .append(tx)
                                            .append(", ty=")
                                            .append(ty)
                                            .append("]")
                                            .toString();
    }
}
