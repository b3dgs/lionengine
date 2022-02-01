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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.NameableAbstract;

/**
 * Collision category, representing a collision point at a specified offset relative to the owner position. Computation
 * is performed depending of the defined {@link CollisionGroup} (and their associated {@link CollisionFormula}).
 * Here a definition example:
 * 
 * <pre>
 * &lt;lionengine:category name="leg_right" axis="Y" x="6" y="0"&gt;
 *     &lt;lionengine:group&gt;block&lt;/lionengine:group&gt;
 *  &lt;/lionengine:category&gt;
 * 
 *  &lt;lionengine:category name="leg_left" axis="Y" x="-6" y="0"&gt;
 *     &lt;lionengine:group&gt;block&lt;/lionengine:group&gt;
 *  &lt;/lionengine:category&gt;
 * 
 *  &lt;lionengine:category name="knee_right" axis="X" x="6" y="0"&gt;
 *     &lt;lionengine:group&gt;block&lt;/lionengine:group&gt;
 *  &lt;/lionengine:category&gt;
 * 
 *  &lt;lionengine:category name="knee_left" axis="X" x="-6" y="0"&gt;
 *     &lt;lionengine:group&gt;block&lt;/lionengine:group&gt;
 *  &lt;/lionengine:category&gt;
 * </pre>
 * 
 * <p>
 * This will define 4 collision points (for ground collision and their borders, plus vertical elements for horizontal
 * collision).
 * </p>
 * 
 * @see CollisionCategoryConfig
 * @see CollisionFormula
 */
public class CollisionCategory extends NameableAbstract
{
    /** Minimum to string characters. */
    private static final int MINIMUM_LENGTH = 64;

    /** Working for this axis. */
    private final Axis axis;
    /** Horizontal offset relative to collision owner. */
    private final int x;
    /** Vertical offset relative to collision owner. */
    private final int y;
    /** Glue flag (keep collision locked on ground even on fast movement over slopes). */
    private final boolean glue;
    /** Defined groups. */
    private final Collection<CollisionGroup> groups;
    /**
     * Collision formula used list (each must be available in
     * {@link com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision#getCollisionFormula(String)}.
     */
    private final Collection<CollisionFormula> formulas = new HashSet<>();

    /**
     * Constructor.
     * 
     * @param name The category name.
     * @param axis The designated axis to apply collision.
     * @param x The horizontal offset.
     * @param y The vertical offset.
     * @param glue The glue flag.
     * @param groups The collision groups used.
     */
    public CollisionCategory(String name, Axis axis, int x, int y, boolean glue, Collection<CollisionGroup> groups)
    {
        super(name);

        this.axis = axis;
        this.x = x;
        this.y = y;
        this.glue = glue;
        this.groups = new ArrayList<>(groups);
        for (final CollisionGroup group : groups)
        {
            formulas.addAll(group.getFormulas());
        }
    }

    /**
     * Get the defined groups as read only.
     * 
     * @return The defined groups.
     */
    public Collection<CollisionGroup> getGroups()
    {
        return groups;
    }

    /**
     * Get the list of collision formulas to test as read only.
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

    /**
     * Get the glue flag.
     * 
     * @return <code>true</code> if glue enabled, <code>false</code> else.
     */
    public boolean isGlue()
    {
        return glue;
    }

    /*
     * Object
     */

    @Override
    public String toString()
    {
        return new StringBuilder(MINIMUM_LENGTH).append(getClass().getSimpleName())
                                                .append(" (name=")
                                                .append(getName())
                                                .append(", axis=")
                                                .append(axis)
                                                .append(", x=")
                                                .append(x)
                                                .append(", y=")
                                                .append(y)
                                                .append(", glue=")
                                                .append(glue)
                                                .append(")")
                                                .append(System.lineSeparator())
                                                .append(Constant.TAB)
                                                .append(groups)
                                                .toString();
    }
}
