/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.map;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.game.Orientation;

/**
 * Represents a map tile constraint. It describes the allowed tiles for the specified orientation.
 */
public class TileConstraint
{
    /** Constraint orientation. */
    private final Orientation orientation;
    /** Allowed tiles. */
    private final Collection<TileRef> allowed = new HashSet<TileRef>();

    /**
     * Create constraint.
     * 
     * @param orientation The orientation.
     */
    public TileConstraint(Orientation orientation)
    {
        this.orientation = orientation;
    }

    /**
     * Add an allowed tile.
     * 
     * @param tile The allowed tile reference.
     */
    public void add(TileRef tile)
    {
        allowed.add(tile);
    }

    /**
     * Get the allowed tiles for this orientation.
     * 
     * @return The allowed tiles.
     */
    public Collection<TileRef> getAllowed()
    {
        return allowed;
    }

    /**
     * Get the constraint orientation.
     * 
     * @return The constraint orientation.
     */
    public Orientation getOrientation()
    {
        return orientation;
    }
}
