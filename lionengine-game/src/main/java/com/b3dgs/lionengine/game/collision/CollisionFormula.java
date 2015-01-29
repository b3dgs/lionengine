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

import java.util.Collection;

/**
 * Collision tile representation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionFormula
{
    /** Formula name. */
    private final String name;
    /** Range representation. */
    private final CollisionRange range;
    /** Function used. */
    private final CollisionFunction function;
    /** Constraints list. */
    private final Collection<CollisionConstraint> constraints;

    /**
     * Create a collision formula.
     * 
     * @param name The formula name.
     * @param range The range reference.
     * @param function The function used.
     * @param constraints The constraints used.
     */
    public CollisionFormula(String name, CollisionRange range, CollisionFunction function,
            Collection<CollisionConstraint> constraints)
    {
        this.name = name;
        this.range = range;
        this.function = function;
        this.constraints = constraints;
    }

    /**
     * Get the formula name.
     * 
     * @return The formula name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the range used.
     * 
     * @return The range used.
     */
    public CollisionRange getRange()
    {
        return range;
    }

    /**
     * Get the function used.
     * 
     * @return The function used.
     */
    public CollisionFunction getFunction()
    {
        return function;
    }

    /**
     * Get the constraints list.
     * 
     * @return The constraints list.
     */
    public Collection<CollisionConstraint> getConstraints()
    {
        return constraints;
    }
}
