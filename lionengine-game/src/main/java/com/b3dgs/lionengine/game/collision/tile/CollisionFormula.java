/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.collision.tile;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Nameable;

/**
 * Collision formula representation. It define the way of collision is computed, and its constraints compared to
 * adjacent tiles.
 * 
 * <pre>
 * &lt;lionengine:formulas xmlns:lionengine="http://lionengine.b3dgs.com"&gt;
 *     &lt;lionengine:formula name="top"&gt;
 *         &lt;lionengine:range output="Y" minX="0" maxX="15" minY="15" maxY="15"/&gt;
 *         &lt;lionengine:function type="LINEAR" a="0" b="16"/&gt;
 *         &lt;lionengine:constraint top="none"/&gt;
 *     &lt;/lionengine:formula&gt;
 *     &lt;lionengine:formula name="bottom"&gt;
 *         &lt;lionengine:range output="Y" minX="0" maxX="15" minY="0" maxY="0"/&gt;
 *         &lt;lionengine:function type="LINEAR" a="0" b="-1"/&gt;
 *         &lt;lionengine:constraint bottom="none"/&gt;
 *     &lt;/lionengine:formula&gt;
 *     &lt;lionengine:formula name="left"&gt;
 *         &lt;lionengine:range output="X" minX="0" maxX="0" minY="0" maxY="15"/&gt;
 *         &lt;lionengine:function type="LINEAR" a="0" b="-1"/&gt;
 *         &lt;lionengine:constraint left="none"/&gt;
 *     &lt;/lionengine:formula&gt;
 *     &lt;lionengine:formula name="right"&gt;
 *         &lt;lionengine:range output="X" minX="15" maxX="15" minY="0" maxY="15"/&gt;
 *         &lt;lionengine:function type="LINEAR" a="0" b="16"/&gt;
 *         &lt;lionengine:constraint right="none"/&gt;
 *     &lt;/lionengine:formula&gt;
 * &lt;/lionengine:formulas&gt;
 * </pre>
 * 
 * <p>
 * This will create 4 formulas, defining a collision for each side.
 * </p>
 * 
 * @see CollisionFormulaConfig
 * @see CollisionRange
 * @see CollisionFunction
 * @see CollisionConstraint
 */
public class CollisionFormula implements Nameable
{
    /** Minimum to string characters. */
    private static final int MINIMUM_LENGTH = 64;

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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof CollisionFormula))
        {
            return false;
        }
        final CollisionFormula other = (CollisionFormula) obj;
        return getName().equals(other.getName());
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MINIMUM_LENGTH).append(getClass().getSimpleName())
                                                .append(" (name=")
                                                .append(name)
                                                .append(")")
                                                .append(Constant.NEW_LINE)
                                                .append(Constant.TAB)
                                                .append(range)
                                                .append(Constant.NEW_LINE)
                                                .append(Constant.TAB)
                                                .append(function)
                                                .append(Constant.NEW_LINE)
                                                .append(Constant.TAB)
                                                .append(constraint)
                                                .toString();
    }
}
