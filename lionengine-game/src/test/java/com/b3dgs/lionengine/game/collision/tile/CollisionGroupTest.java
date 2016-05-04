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

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.game.Axis;

/**
 * Test the collision group class.
 */
public class CollisionGroupTest
{
    /** Formula test. */
    private final CollisionFormula formula = new CollisionFormula("formula",
                                                                  new CollisionRange(Axis.X, 0, 1, 2, 3),
                                                                  new CollisionFunctionLinear(1.0, 2.0),
                                                                  new CollisionConstraint());
    /** Group test. */
    private final CollisionGroup group = new CollisionGroup("group", Arrays.asList(formula));

    /**
     * Test the group same utility function.
     */
    @Test
    public void testSame()
    {
        Assert.assertTrue(CollisionGroup.same(null, null));
        Assert.assertTrue(CollisionGroup.same("a", "a"));

        Assert.assertFalse(CollisionGroup.same(null, "a"));
        Assert.assertFalse(CollisionGroup.same("a", null));
        Assert.assertFalse(CollisionGroup.same("a", "b"));
    }

    /**
     * Test the group.
     */
    @Test
    public void testGroup()
    {
        Assert.assertEquals("group", group.getName());
        Assert.assertEquals(formula, group.getFormulas().iterator().next());
    }

    /**
     * Test the group hash code.
     */
    @Test
    public void testHashcode()
    {
        final int hash = group.hashCode();
        Assert.assertEquals(hash, new CollisionGroup("group", new ArrayList<CollisionFormula>()).hashCode());
        Assert.assertEquals(hash, new CollisionGroup("group", Arrays.asList(formula)).hashCode());

        Assert.assertNotEquals(hash, new Object().hashCode());
        Assert.assertNotEquals(hash, new CollisionGroup("void", Arrays.asList(formula)).hashCode());
    }

    /**
     * Test the group equality.
     */
    @Test
    public void testEquals()
    {
        Assert.assertEquals(group, group);
        Assert.assertEquals(group, new CollisionGroup("group", new ArrayList<CollisionFormula>()));
        Assert.assertEquals(group, new CollisionGroup("group", Arrays.asList(formula)));

        Assert.assertNotEquals(group, null);
        Assert.assertNotEquals(group, new Object());
        Assert.assertNotEquals(group, new CollisionGroup("void", Arrays.asList(formula)));
    }

    /**
     * Test the group to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("CollisionGroup (name=group)"
                            + Constant.NEW_LINE
                            + Constant.TAB
                            + "[CollisionFormula (name=formula)"
                            + Constant.NEW_LINE
                            + Constant.TAB
                            + "CollisionRange (output=X, minX=0, maxX=1, minY=2, maxY=3)"
                            + Constant.NEW_LINE
                            + Constant.TAB
                            + "CollisionFunctionLinear (a=1.0, b=2.0)"
                            + Constant.NEW_LINE
                            + Constant.TAB
                            + "CollisionConstraint{NORTH=[], NORTH_EAST=[], EAST=[], SOUTH_EAST=[], SOUTH=[], "
                            + "SOUTH_WEST=[], WEST=[], NORTH_WEST=[]}]",
                            group.toString());
    }
}
