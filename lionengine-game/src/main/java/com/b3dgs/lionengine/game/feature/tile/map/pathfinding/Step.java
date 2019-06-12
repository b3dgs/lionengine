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
 * Path step.
 */
public final class Step
{
    /** Step location x. */
    private final int x;
    /** Step location y. */
    private final int y;

    /**
     * Internal constructor.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    public Step(int x, int y)
    {
        super();

        this.x = x;
        this.y = y;
    }

    /**
     * Get location x.
     * 
     * @return The location x.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get location y.
     * 
     * @return The location y.
     */
    public int getY()
    {
        return y;
    }

    @Override
    public int hashCode()
    {
        return x * y;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Step)
        {
            final Step o = (Step) other;
            return o.getX() == x && o.getY() == y;
        }
        return false;
    }
}
