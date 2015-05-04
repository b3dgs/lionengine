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

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionCategory;
import com.b3dgs.lionengine.game.map.MapTileCollision;

/**
 * Collision category, representing a collision point at a specified offset relative to the owner position. Computation
 * is performed depending of the defined {@link CollisionGroup} (and their associated {@link CollisionFormula}).
 * Here a definition example:
 * 
 * <pre>
 *  {@code<lionengine:category name="leg_right" axis="Y" x="6" y="0">}
 *     {@code<lionengine:group>block</lionengine:group>}
 *  {@code</lionengine:category>}
 * 
 *  {@code<lionengine:category name="leg_left" axis="Y" x="-6" y="0">}
 *     {@code<lionengine:group>block</lionengine:group>}
 *  {@code</lionengine:category>}
 * 
 *  {@code<lionengine:category name="knee_right" axis="X" x="6" y="0">}
 *     {@code<lionengine:group>block</lionengine:group>}
 *  {@code</lionengine:category>}
 * 
 *  {@code<lionengine:category name="knee_left" axis="X" x="-6" y="0">}
 *     {@code<lionengine:group>block</lionengine:group>}
 *  {@code</lionengine:category>}
 * 
 *  This will define 4 collision points (for ground collision and their borders, plus vertical elements for horizontal
 * collision).
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see ConfigCollisionCategory
 * @see CollisionFormula
 */
public class CollisionCategory
{
    /** Category name. */
    private final String name;
    /** Working for this axis. */
    private final Axis axis;
    /** Horizontal offset relative to collision owner. */
    private final int x;
    /** Vertical offset relative to collision owner. */
    private final int y;
    /** Collision formula used list (each must be available in {@link MapTileCollision#getCollisionFormula(String)}. */
    private final Collection<CollisionFormula> formulas;

    /**
     * Constructor.
     * 
     * @param name The category name.
     * @param axis The designated axis to apply collision.
     * @param x The horizontal offset.
     * @param y The vertical offset.
     * @param groups The collision groups used.
     */
    public CollisionCategory(String name, Axis axis, int x, int y, Collection<CollisionGroup> groups)
    {
        this.name = name;
        this.axis = axis;
        this.x = x;
        this.y = y;
        formulas = new HashSet<>();
        for (final CollisionGroup group : groups)
        {
            formulas.addAll(group.getFormulas());
        }
    }

    /**
     * Get the collision category name.
     * 
     * @return The collision category name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the list of collision formulas to test.
     * 
     * @return The collision formulas list.
     */
    public Collection<CollisionFormula> getFormulas()
    {
        return formulas;
    }

    /**
     * Get the designated axis to use.
     * 
     * @return The axis used.
     */
    public Axis getAxis()
    {
        return axis;
    }

    /**
     * Get the horizontal offset relative to owner for collision checking.
     * 
     * @return The horizontal offset relative to owner for collision checking.
     */
    public int getOffsetX()
    {
        return x;
    }

    /**
     * Get the vertical offset relative to owner for collision checking.
     * 
     * @return The vertical offset relative to owner for collision checking.
     */
    public int getOffsetY()
    {
        return y;
    }
}
