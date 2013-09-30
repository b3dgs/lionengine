/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.e_shmup.a_scrollingmap;

import com.b3dgs.lionengine.utility.UtilityRandom;

/**
 * Star implementation.
 */
final class Star
{
    /** Horizontal vector. */
    private final double vx;
    /** Vertical vector. */
    private final double vy;
    /** Star id. */
    private final int id;
    /** Horizontal location. */
    private double x;
    /** Vertical location. */
    private double y;

    /**
     * Constructor.
     * 
     * @param x The location x.
     * @param y The location y.
     * @param vx The horizontal vector.
     * @param vy The vertical vector.
     * @param id The id.
     */
    Star(double x, double y, double vx, double vy, int id)
    {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.id = id;
    }

    /**
     * Update the star.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        x += vx * extrp;
        y += vy * extrp;
        if (y > 210)
        {
            y = -10;
            x = UtilityRandom.getRandomInteger(-20, 340);
        }
    }

    /**
     * Get the id.
     * 
     * @return The id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Get the x location.
     * 
     * @return The x location.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Get the y location.
     * 
     * @return The y location.
     */
    public double getY()
    {
        return y;
    }
}
