/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.collision;

/**
 * Linear collision function implementation. It simply represents the following function:
 * <p>
 * <code>a * input + b</code>
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionFunctionLinear
        implements CollisionFunction
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
        this.a = a;
        this.b = b;
    }

    /*
     * CollisionFunction
     */

    @Override
    public double compute(double input)
    {
        return a * input + b;
    }
}
