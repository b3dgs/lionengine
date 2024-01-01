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
 * Linear collision function implementation. It simply represents the following function:
 * <p>
 * <code>a * input + b</code>
 * </p>
 * 
 * @param a The multiplication factor.
 * @param b The offset value.
 */
public record CollisionFunctionLinear(double a, double b) implements CollisionFunction
{
    /**
     * Get the factor value.
     * 
     * @return The factor value.
     */
    public double getA()
    {
        return a;
    }

    /**
     * Get the offset value.
     * 
     * @return The offset value.
     */
    public double getB()
    {
        return b;
    }

    @Override
    public double compute(double input)
    {
        return a * input + b;
    }

    @Override
    public int getRenderX(double input)
    {
        return (int) Math.floor(compute(input));
    }

    @Override
    public int getRenderY(double input)
    {
        if (Double.compare(a, 0.0) < 0)
        {
            return (int) Math.floor(compute(input + 1));
        }
        return (int) Math.floor(compute(input));
    }

    @Override
    public CollisionFunctionType getType()
    {
        return CollisionFunctionType.LINEAR;
    }
}
