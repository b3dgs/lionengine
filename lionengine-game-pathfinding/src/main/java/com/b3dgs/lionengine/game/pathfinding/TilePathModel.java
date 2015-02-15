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
package com.b3dgs.lionengine.game.pathfinding;

/**
 * Representation of a default tile, used for pathfinding.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class TilePathModel
        implements TilePath
{
    /** Blocked flag. */
    private boolean blocking;

    /**
     * Create a tile path.
     */
    public TilePathModel()
    {
        blocking = false;
    }

    /**
     * Check if this collision is blocking.
     * 
     * @return <code>true</code> if blocking, <code>false</code> else.
     */
    public abstract boolean checkBlocking();

    /*
     * TilePath
     */

    @Override
    public void setBlocking(boolean blocking)
    {
        this.blocking = blocking;
    }

    @Override
    public boolean isBlocking()
    {
        return blocking;
    }
}
