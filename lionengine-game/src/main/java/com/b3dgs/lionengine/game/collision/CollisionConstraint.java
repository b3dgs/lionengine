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
package com.b3dgs.lionengine.game.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import com.b3dgs.lionengine.game.Orientation;

/**
 * Represents the collision constraints around a collision.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.game.configurer.ConfigCollisionConstraint
 */
public class CollisionConstraint
{
    /** Constraints defined. */
    private final Map<Orientation, Collection<String>> constraints;

    /**
     * Create a collision constraint.
     */
    public CollisionConstraint()
    {
        constraints = new EnumMap<Orientation, Collection<String>>(Orientation.class);
        for (final Orientation orientation : Orientation.values())
        {
            constraints.put(orientation, new ArrayList<String>());
        }
    }

    /**
     * Add the group constraint at the specified orientation.
     * 
     * @param orientation The orientation.
     * @param group The group where constraint is applied.
     */
    public void add(Orientation orientation, String group)
    {
        final Collection<String> groups = constraints.get(orientation);
        groups.add(group);
    }

    /**
     * Get the constraints defined.
     * 
     * @return The constraints defined.
     */
    public Map<Orientation, Collection<String>> getConstraints()
    {
        return constraints;
    }

    /**
     * Get the constraints defined for the specified orientation.
     * 
     * @param orientation The orientation value.
     * @return The constraints defined for this orientation.
     */
    public Collection<String> getConstraints(Orientation orientation)
    {
        return constraints.get(orientation);
    }

    /**
     * Check if constraint is defined for the group at the specified orientation.
     * 
     * @param orientation The orientation to check on.
     * @param group The group to check.
     * @return <code>true</code> if constraint defined, <code>false</code> else.
     */
    public boolean has(Orientation orientation, String group)
    {
        return getConstraints(orientation).contains(group);
    }
}
