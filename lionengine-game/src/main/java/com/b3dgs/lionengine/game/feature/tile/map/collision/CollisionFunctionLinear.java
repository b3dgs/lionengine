/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.feature.tile.map.collision;

/**
 * Linear collision function implementation. It simply represents the following function:
 * <p>
 * <code>a * input + b</code>
 * </p>
 */
public class CollisionFunctionLinear implements CollisionFunction
{
    /** Multiplication factor. */
    private final double a;
    /** Offset value. */
    private final double b;

    /**
     * Create a linear function.
     * 
     * @param a The multiplication factor.
     * @param b The offset value.
     */
    public CollisionFunctionLinear(double a, double b)
    {
        super();

        this.a = a;
        this.b = b;
    }

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

    /*
     * CollisionFunction
     */

    @Override
    public double compute(double input)
    {
        return Math.floor(a * input + b);
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

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(a);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(b);
        result = prime * result + (int) (temp ^ temp >>> 32);
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
        final CollisionFunctionLinear other = (CollisionFunctionLinear) object;
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(other.a)
               && Double.doubleToLongBits(b) == Double.doubleToLongBits(other.b);

    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(getClass().getSimpleName())
                                  .append(" (a=")
                                  .append(a)
                                  .append(", b=")
                                  .append(b)
                                  .append(")")
                                  .toString();
    }
}
