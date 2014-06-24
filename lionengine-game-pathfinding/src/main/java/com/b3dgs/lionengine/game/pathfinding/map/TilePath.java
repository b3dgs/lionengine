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
package com.b3dgs.lionengine.game.pathfinding.map;

import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.game.map.TileGame;

/**
 * Representation of a default tile, used for pathfinding.
 * 
 * @param <C> collision type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class TilePath<C extends Enum<C> & CollisionTile>
        extends TileGame<C>
{
    /** Blocked flag. */
    private boolean blocking;

    /**
     * Constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     * @param pattern The tile pattern.
     * @param number The tile number.
     * @param collision The tile collision.
     */
    public TilePath(int width, int height, Integer pattern, int number, C collision)
    {
        super(width, height, pattern, number, collision);
    }

    /**
     * Check if this collision is blocking.
     * 
     * @param collision The tile collision.
     * @return <code>true</code> if blocking, <code>false</code> else.
     */
    public abstract boolean checkBlocking(C collision);

    /**
     * Check if current tile is blocking or not.
     * 
     * @return <code>true</code> if blocking, <code>false</code> else.
     */
    public boolean isBlocking()
    {
        return blocking;
    }

    /**
     * Set blocking state.
     * 
     * @param blocking The blocking state.
     */
    public void setBlocking(boolean blocking)
    {
        this.blocking = blocking;
    }
}
