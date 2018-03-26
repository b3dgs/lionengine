/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;

/**
 * Test the collision category class.
 */
public class CollisionCategoryTest
{
    /** Formula test. */
    private final CollisionFormula formula = new CollisionFormula("formula",
                                                                  new CollisionRange(Axis.X, 0, 1, 2, 3),
                                                                  new CollisionFunctionLinear(1.0, 2.0),
                                                                  new CollisionConstraint());
    /** Group test. */
    private final CollisionGroup group = new CollisionGroup("group", Arrays.asList(formula));
    /** Category test. */
    private final CollisionCategory category = new CollisionCategory("name", Axis.X, 1, 2, Arrays.asList(group));

    /**
     * Test the category construction.
     */
    @Test
    public void testCategory()
    {
        Assert.assertEquals("name", category.getName());
        Assert.assertEquals(Axis.X, category.getAxis());
        Assert.assertEquals(1, category.getOffsetX());
        Assert.assertEquals(2, category.getOffsetY());
        Assert.assertEquals(group, category.getGroups().iterator().next());
        Assert.assertEquals(formula, category.getFormulas().iterator().next());
    }

    /**
     * Test the category hash code.
     */
    @Test
    public void testHashcode()
    {
        final int hash = category.hashCode();
        Assert.assertEquals(hash, new CollisionCategory("name", Axis.X, 1, 2, Arrays.asList(group)).hashCode());
        Assert.assertEquals(hash, new CollisionCategory("name", Axis.Y, 1, 2, Arrays.asList(group)).hashCode());
        Assert.assertEquals(hash, new CollisionCategory("name", Axis.X, 2, 2, Arrays.asList(group)).hashCode());
        Assert.assertEquals(hash, new CollisionCategory("name", Axis.X, 1, 3, Arrays.asList(group)).hashCode());
        Assert.assertEquals(hash,
                            new CollisionCategory("name", Axis.X, 1, 2, new ArrayList<CollisionGroup>()).hashCode());

        Assert.assertNotEquals(hash, new Object().hashCode());
        Assert.assertNotEquals(hash, new CollisionCategory("void", Axis.X, 1, 2, Arrays.asList(group)).hashCode());
    }

    /**
     * Test the category equality.
     */
    @Test
    public void testEquals()
    {
        Assert.assertEquals(category, category);
        Assert.assertEquals(category, new CollisionCategory("name", Axis.X, 1, 2, Arrays.asList(group)));
        Assert.assertEquals(category, new CollisionCategory("name", Axis.Y, 1, 2, Arrays.asList(group)));
        Assert.assertEquals(category, new CollisionCategory("name", Axis.X, 2, 2, Arrays.asList(group)));
        Assert.assertEquals(category, new CollisionCategory("name", Axis.X, 1, 3, Arrays.asList(group)));
        Assert.assertEquals(category, new CollisionCategory("name", Axis.X, 1, 2, new ArrayList<CollisionGroup>()));

        Assert.assertNotEquals(category, null);
        Assert.assertNotEquals(category, new Object());
        Assert.assertNotEquals(category, new CollisionCategory("void", Axis.X, 1, 2, Arrays.asList(group)));
    }

    /**
     * Test the category to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("CollisionCategory (name=name, axis=X, x=1, y=2)"
                            + System.lineSeparator()
                            + Constant.TAB
                            + "[CollisionGroup (name=group)"
                            + System.lineSeparator()
                            + Constant.TAB
                            + "[CollisionFormula (name=formula)"
                            + System.lineSeparator()
                            + Constant.TAB
                            + "CollisionRange (output=X, minX=0, maxX=1, minY=2, maxY=3)"
                            + System.lineSeparator()
                            + Constant.TAB
                            + "CollisionFunctionLinear (a=1.0, b=2.0)"
                            + System.lineSeparator()
                            + Constant.TAB
                            + "CollisionConstraint{NORTH=[], NORTH_EAST=[], EAST=[], SOUTH_EAST=[], "
                            + "SOUTH=[], SOUTH_WEST=[], WEST=[], NORTH_WEST=[]}]]",
                            category.toString());
    }
}
