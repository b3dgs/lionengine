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
package com.b3dgs.lionengine.game.tile;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;

/**
 * Represents a tile transition from two groups.
 */
public class TileTransition
{
    /** The transition type. */
    private final TileTransitionType type;
    /** The group in. */
    private final String groupIn;
    /** The group out. */
    private final String groupOut;

    /**
     * Create the transition.
     * 
     * @param type The transition type.
     * @param groupIn The group inside.
     * @param groupOut The group outside.
     * @throws LionEngineException If <code>null</code> type.
     */
    public TileTransition(TileTransitionType type, String groupIn, String groupOut)
    {
        Check.notNull(type);

        this.type = type;
        this.groupIn = groupIn;
        this.groupOut = groupOut;
    }

    /**
     * Get the transition type.
     * 
     * @return The transition type.
     */
    public TileTransitionType getType()
    {
        return type;
    }

    /**
     * Get the group inside.
     * 
     * @return The group inside.
     */
    public String getGroupIn()
    {
        return groupIn;
    }

    /**
     * Get the group outside.
     * 
     * @return The group outside.
     */
    public String getGroupOut()
    {
        return groupOut;
    }

    /*
     * Object
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        if (groupIn != null)
        {
            result = prime * result + groupIn.hashCode();
        }
        if (groupOut != null)
        {
            result = prime * result + groupOut.hashCode();
        }
        result = prime * result + type.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || !(obj instanceof TileTransition))
        {
            return false;
        }
        final TileTransition other = (TileTransition) obj;

        final boolean sameGroups = CollisionGroup.same(groupIn, other.groupIn)
                                   && CollisionGroup.same(groupOut, other.groupOut);

        return sameGroups && type.equals(other.type);
    }
}
