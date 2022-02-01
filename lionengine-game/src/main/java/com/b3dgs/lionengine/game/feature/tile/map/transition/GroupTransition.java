/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents transition between two groups.
 */
public class GroupTransition
{
    /** Transition string representation. */
    private static final String TRANSITION = " -> ";

    /** The first group. */
    private final String groupIn;
    /** The second group. */
    private final String groupOut;

    /**
     * Create the group transition.
     * 
     * @param groupIn The first group.
     * @param groupOut The second group.
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public GroupTransition(String groupIn, String groupOut)
    {
        super();

        Check.notNull(groupIn);
        Check.notNull(groupOut);

        this.groupIn = groupIn;
        this.groupOut = groupOut;
    }

    /**
     * The first group.
     * 
     * @return The first group.
     */
    public String getIn()
    {
        return groupIn;
    }

    /**
     * The second group.
     * 
     * @return The second group.
     */
    public String getOut()
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
        result = prime * result + groupIn.hashCode();
        result = prime * result + groupOut.hashCode();
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
        final GroupTransition other = (GroupTransition) object;
        return groupIn.equals(other.groupIn) && groupOut.equals(other.groupOut);
    }

    @Override
    public String toString()
    {
        return new StringBuilder(groupIn).append(TRANSITION).append(groupOut).toString();
    }
}
