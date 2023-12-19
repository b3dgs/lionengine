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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
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
 * @param name The formula name.
 * @param range The range reference.
 * @param function The function used.
 * @param constraint The constraint used.
 * 
 * @see CollisionFormulaConfig
 * @see CollisionRange
 * @see CollisionFunction
 * @see CollisionConstraint
 */
public record CollisionFormula(String name,
                               CollisionRange range,
                               CollisionFunction function,
                               CollisionConstraint constraint)
                              implements Nameable
{
    /**
     * Create formula.
     * 
     * @param name The formula name.
     * @param range The range reference.
     * @param function The function used.
     * @param constraint The constraint used.
     * @throws LionEngineException If invalid arguments.
     */
    public CollisionFormula
    {
        Check.notNull(name);
        Check.notNull(range);
        Check.notNull(function);
        Check.notNull(constraint);
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

    @Override
    public String getName()
    {
        return name;
    }
}
