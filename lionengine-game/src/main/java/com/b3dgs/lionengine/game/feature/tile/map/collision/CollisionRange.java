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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

/**
 * Represents the range of the collision for a specified axis. It allows to restrict the computation area of the
 * collision, depending of the input value.
 * 
 * @see CollisionRangeConfig
 */
public class CollisionRange
{
    /** Minimum to string characters. */
    private static final int MINIMUM_LENGTH = 64;

    /** Output target (where should be stored computation result). */
    private final Axis output;
    /** Minimum horizontal value relative to tile. */
    private final int minX;
    /** Maximum horizontal value relative to tile. */
    private final int maxX;
    /** Minimum vertical value relative to tile. */
    private final int minY;
    /** Maximum vertical value relative to tile. */
    private final int maxY;

    /**
     * Create a collision range.
     * 
     * @param output The output target.
     * @param minX The minimum accepted value.
     * @param maxX The maximum accepted value.
     * @param minY The minimum accepted value.
     * @param maxY The maximum accepted value.
     */
    public CollisionRange(Axis output, int minX, int maxX, int minY, int maxY)
    {
        super();

        this.output = output;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

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

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + maxX;
        result = prime * result + maxY;
        result = prime * result + minX;
        result = prime * result + minY;
        result = prime * result + output.hashCode();
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
        final CollisionRange other = (CollisionRange) object;
        final boolean sameRange = maxX == other.maxX && maxY == other.maxY && minX == other.minX && minY == other.minY;
        return sameRange && output == other.output;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MINIMUM_LENGTH).append(CollisionRange.class.getSimpleName())
                                                .append(" (")
                                                .append("output=")
                                                .append(output)
                                                .append(", ")
                                                .append("minX=")
                                                .append(minX)
                                                .append(", maxX=")
                                                .append(maxX)
                                                .append(", minY=")
                                                .append(minY)
                                                .append(", maxY=")
                                                .append(maxY)
                                                .append(")")
                                                .toString();
    }
}
