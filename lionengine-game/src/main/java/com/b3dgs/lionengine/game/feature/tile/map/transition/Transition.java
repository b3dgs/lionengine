/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents a tile transition from two groups.
 * 
 * @param type The transition type.
 * @param groups The groups transition.
 */
public record Transition(TransitionType type, GroupTransition groups)
{
    /**
     * Create the transition.
     * 
     * @param type The transition type.
     * @param groupIn The group inside.
     * @param groupOut The group outside.
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public Transition(TransitionType type, String groupIn, String groupOut)
    {
        this(type, new GroupTransition(groupIn, groupOut));
    }

    /**
     * Get the transition type.
     * 
     * @return The transition type.
     */
    public TransitionType getType()
    {
        return type;
    }

    /**
     * Get the group inside.
     * 
     * @return The group inside.
     */
    public String getIn()
    {
        return groups.getIn();
    }

    /**
     * Get the group outside.
     * 
     * @return The group outside.
     */
    public String getOut()
    {
        return groups.getOut();
    }

    /**
     * Check if transition if symmetric with other.
     * 
     * @param other The other transition to check with.
     * @return <code>true</code> if symmetric transition, <code>false</code> else.
     */
    private boolean isSymmetric(Transition other)
    {
        return getIn().equals(other.getOut()) && getOut().equals(other.getIn()) && type == other.type.getSymetric();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + groups.hashCode();
        return prime * result + type.hashCode();
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
        final Transition other = (Transition) object;
        return groups.equals(other.groups) && type == other.type || isSymmetric(other);
    }
}
