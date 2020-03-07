/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.game.Orientation;

/**
 * Test {@link CollisionFormula}.
 */
public final class CollisionFormulaTest
{
    /** Range test. */
    private final CollisionRange range = new CollisionRange(Axis.X, 0, 1, 2, 3);
    /** Function test. */
    private final CollisionFunction function = new CollisionFunctionLinear(1.0, 2.0);
    /** Constraint test. */
    private final CollisionConstraint constaint = new CollisionConstraint();
    /** Formula test. */
    private final CollisionFormula formula = new CollisionFormula("formula", range, function, constaint);

    /**
     * Test the collision formula construction
     */
    @Test
    public void testFormula()
    {
        assertEquals("formula", formula.getName());
        assertEquals(range, formula.getRange());
        assertEquals(function, formula.getFunction());
        assertEquals(constaint, formula.getConstraint());
    }

    /**
     * Test the collision formula equality
     */
    @Test
    public void testEquals()
    {
        assertEquals(formula, formula);
        assertEquals(formula,
                     new CollisionFormula("formula", new CollisionRange(Axis.Y, 0, 1, 2, 3), function, constaint));
        assertEquals(formula, new CollisionFormula("formula", range, new CollisionFunctionLinear(2.0, 2.0), constaint));

        final CollisionConstraint newConstraint = new CollisionConstraint();
        newConstraint.add(Orientation.EAST, "test");

        assertEquals(formula, new CollisionFormula("formula", range, function, newConstraint));

        assertNotEquals(formula, null);
        assertNotEquals(formula, new Object());
        assertNotEquals(formula, new CollisionFormula("void", range, function, constaint));
    }

    /**
     * Test the collision formula hash code
     */
    @Test
    public void testHashCode()
    {
        assertEquals(formula,
                     new CollisionFormula("formula", new CollisionRange(Axis.Y, 0, 1, 2, 3), function, constaint));
        assertEquals(formula, new CollisionFormula("formula", range, new CollisionFunctionLinear(2.0, 2.0), constaint));

        final CollisionConstraint newConstraint = new CollisionConstraint();
        newConstraint.add(Orientation.EAST, "test");

        assertEquals(formula, new CollisionFormula("formula", range, function, newConstraint));

        assertNotEquals(formula, new Object());
        assertNotEquals(formula, new CollisionFormula("void", range, function, constaint));
    }

    /**
     * Test the formula to string.
     */
    @Test
    public void testToString()
    {
        assertEquals("CollisionFormula (name=formula)"
                     + System.lineSeparator()
                     + Constant.TAB
                     + "CollisionRange (output=X, minX=0, maxX=1, minY=2, maxY=3)"
                     + System.lineSeparator()
                     + Constant.TAB
                     + "CollisionFunctionLinear (a=1.0, b=2.0)"
                     + System.lineSeparator()
                     + Constant.TAB
                     + "CollisionConstraint{NORTH=[], NORTH_EAST=[], EAST=[], SOUTH_EAST=[], "
                     + "SOUTH=[], SOUTH_WEST=[], WEST=[], NORTH_WEST=[]}"
                     + System.lineSeparator(),
                     formula.toString());
    }
}
