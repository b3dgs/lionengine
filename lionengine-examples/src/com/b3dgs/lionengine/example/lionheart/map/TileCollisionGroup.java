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
package com.b3dgs.lionengine.example.lionheart.map;

/**
 * List of collisions group types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum TileCollisionGroup
{
    /** No group. */
    NONE(0.0),
    /** Flat. */
    FLAT(0.0),
    /** Pillar. */
    PILLAR(0.0),
    /** Slope. */
    SLOPE(0.5),
    /** Slide. */
    SLIDE(2.0),
    /** Liana. */
    LIANA_HORIZONTAL(1.0),
    /** Liana leaning. */
    LIANA_LEANING(0.5),
    /** Liana steep. */
    LIANA_STEEP(1.0);

    /** Factor (vertical factor compared to horizontal). */
    private double factor;

    /**
     * Constructor.
     * 
     * @param factor The factor value.
     */
    private TileCollisionGroup(double factor)
    {
        this.factor = factor;
    }

    /**
     * Get the factor value.
     * 
     * @return The factor value.
     */
    public double getFactor()
    {
        return factor;
    }
}
