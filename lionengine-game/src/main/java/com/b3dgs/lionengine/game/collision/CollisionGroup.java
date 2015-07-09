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

import com.b3dgs.lionengine.Nameable;

/**
 * Represents the collision group, which can be applied to a {@link com.b3dgs.lionengine.game.map.Tile}. It allows to
 * reference easily a set of {@link CollisionFormula} previously defined on the
 * {@link com.b3dgs.lionengine.game.map.MapTile}. Here a definition example:
 * 
 * <pre>
 * &lt;lionengine:groups xmlns:lionengine="http://lionengine.b3dgs.com">
 *    &lt;lionengine:group name="block">
 *       &lt;lionengine:formula>top&lt;/lionengine:formula>
 *       &lt;lionengine:formula>bottom&lt;/lionengine:formula>
 *       &lt;lionengine:formula>left&lt;/lionengine:formula>
 *       &lt;lionengine:formula>right&lt;/lionengine:formula>
 *    &lt;/lionengine:group>
 * &lt;/lionengine:groups>
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.game.configurer.ConfigCollisionGroup
 * @see CollisionFormula
 */
public class CollisionGroup implements Nameable
{
    /** The group name. */
    private final String group;
    /** The collision formulas used. */
    private final Collection<CollisionFormula> formulas;

    /**
     * Create a collision group.
     * 
     * @param group The group name.
     * @param formulas The collision formulas reference.
     */
    public CollisionGroup(String group, Collection<CollisionFormula> formulas)
    {
        this.group = group;
        this.formulas = formulas;
    }

    /**
     * Get collision formulas reference.
     * 
     * @return The collision formulas reference.
     */
    public Collection<CollisionFormula> getFormulas()
    {
        return formulas;
    }

    /*
     * Nameable
     */

    @Override
    public String getName()
    {
        return group;
    }
}
