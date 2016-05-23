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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.game.Orientation;

/**
 * Test the collision formula class.
 */
public class CollisionFormulaTest
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
        Assert.assertEquals("formula", formula.getName());
        Assert.assertEquals(range, formula.getRange());
        Assert.assertEquals(function, formula.getFunction());
        Assert.assertEquals(constaint, formula.getConstraint());
    }

    /**
     * Test the collision formula hash code
     */
    @Test
    public void testHashcode()
    {
        final int hash = formula.hashCode();
        Assert.assertEquals(hash,
                            new CollisionFormula("formula",
                                                 new CollisionRange(Axis.Y, 0, 1, 2, 3),
                                                 function,
                                                 constaint).hashCode());
        Assert.assertEquals(hash,
                            new CollisionFormula("formula",
                                                 range,
                                                 new CollisionFunctionLinear(2.0, 2.0),
                                                 constaint).hashCode());

        final CollisionConstraint newConstraint = new CollisionConstraint();
        newConstraint.add(Orientation.EAST, "test");

        Assert.assertEquals(hash, new CollisionFormula("formula", range, function, newConstraint).hashCode());

        Assert.assertNotEquals(hash, new Object().hashCode());
        Assert.assertNotEquals(hash, new CollisionFormula("void", range, function, constaint).hashCode());
    }

    /**
     * Test the collision formula equality
     */
    @Test
    public void testEquals()
    {
        Assert.assertEquals(formula, formula);
        Assert.assertEquals(formula,
                            new CollisionFormula("formula",
                                                 new CollisionRange(Axis.Y, 0, 1, 2, 3),
                                                 function,
                                                 constaint));
        Assert.assertEquals(formula,
                            new CollisionFormula("formula", range, new CollisionFunctionLinear(2.0, 2.0), constaint));

        final CollisionConstraint newConstraint = new CollisionConstraint();
        newConstraint.add(Orientation.EAST, "test");

        Assert.assertEquals(formula, new CollisionFormula("formula", range, function, newConstraint));

        Assert.assertNotEquals(formula, null);
        Assert.assertNotEquals(formula, new Object());
        Assert.assertNotEquals(formula, new CollisionFormula("void", range, function, constaint));
    }

    /**
     * Test the formula to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("CollisionFormula (name=formula)"
                            + Constant.NEW_LINE
                            + Constant.TAB
                            + "CollisionRange (output=X, minX=0, maxX=1, minY=2, maxY=3)"
                            + Constant.NEW_LINE
                            + Constant.TAB
                            + "CollisionFunctionLinear (a=1.0, b=2.0)"
                            + Constant.NEW_LINE
                            + Constant.TAB
                            + "CollisionConstraint{NORTH=[], NORTH_EAST=[], EAST=[], SOUTH_EAST=[], "
                            + "SOUTH=[], SOUTH_WEST=[], WEST=[], NORTH_WEST=[]}",
                            formula.toString());
    }
}
