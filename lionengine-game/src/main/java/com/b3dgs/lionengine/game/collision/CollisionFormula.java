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

import com.b3dgs.lionengine.Nameable;

/**
 * Collision formula representation. It define the way of collision is computed, and its constraints compared to
 * adjacent tiles.
 * 
 * <pre>
 * &lt;lionengine:formulas xmlns:lionengine="http://lionengine.b3dgs.com">
 *     &lt;lionengine:formula name="top">
 *         &lt;lionengine:range output="Y" minX="0" maxX="15" minY="15" maxY="15"/>
 *         &lt;lionengine:function type="LINEAR" a="0" b="16"/>
 *         &lt;lionengine:constraint top="none"/>
 *     &lt;/lionengine:formula>
 *     &lt;lionengine:formula name="bottom">
 *         &lt;lionengine:range output="Y" minX="0" maxX="15" minY="0" maxY="0"/>
 *         &lt;lionengine:function type="LINEAR" a="0" b="-1"/>
 *         &lt;lionengine:constraint bottom="none"/>
 *     &lt;/lionengine:formula>
 *     &lt;lionengine:formula name="left">
 *         &lt;lionengine:range output="X" minX="0" maxX="0" minY="0" maxY="15"/>
 *         &lt;lionengine:function type="LINEAR" a="0" b="-1"/>
 *         &lt;lionengine:constraint left="none"/>
 *     &lt;/lionengine:formula>
 *     &lt;lionengine:formula name="right">
 *         &lt;lionengine:range output="X" minX="15" maxX="15" minY="0" maxY="15"/>
 *         &lt;lionengine:function type="LINEAR" a="0" b="16"/>
 *         &lt;lionengine:constraint right="none"/>
 *     &lt;/lionengine:formula>
 * &lt;/lionengine:formulas>
 * </pre>
 * 
 * <p>
 * This will create 4 formulas, defining a collision for each side.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.game.configurer.ConfigCollisionFormula
 * @see CollisionRange
 * @see CollisionFunction
 * @see CollisionConstraint
 */
public class CollisionFormula implements Nameable
{
    /** Formula name. */
    private final String name;
    /** Range representation. */
    private final CollisionRange range;
    /** Function used. */
    private final CollisionFunction function;
    /** Constraint defined. */
    private final CollisionConstraint constraint;

    /**
     * Create a collision formula.
     * 
     * @param name The formula name.
     * @param range The range reference.
     * @param function The function used.
     * @param constraint The constraint used.
     */
    public CollisionFormula(String name,
                            CollisionRange range,
                            CollisionFunction function,
                            CollisionConstraint constraint)
    {
        this.name = name;
        this.range = range;
        this.function = function;
        this.constraint = constraint;
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
     * Get the constraint defined.
     * 
     * @return The constraint defined.
     */
    public CollisionConstraint getConstraint()
    {
        return constraint;
    }

    /*
     * Nameable
     */

    @Override
    public String getName()
    {
        return name;
    }
}
