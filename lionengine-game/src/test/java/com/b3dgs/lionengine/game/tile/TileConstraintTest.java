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
package com.b3dgs.lionengine.game.tile;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.Orientation;

/**
 * Test the tile constraint class.
 */
public class TileConstraintTest
{
    /**
     * Test the constraint creation.
     */
    @Test
    public void testConstraint()
    {
        final TileConstraint constraint = new TileConstraint(Orientation.NORTH);

        Assert.assertEquals(Orientation.NORTH, constraint.getOrientation());
        Assert.assertTrue(constraint.getAllowed().isEmpty());

        final TileRef tile = new TileRef(0, 1);
        constraint.add(tile);

        Assert.assertEquals(tile, constraint.getAllowed().iterator().next());
    }

    /**
     * Test the constraint hash code.
     */
    @Test
    public void testHashcode()
    {
        final TileConstraint constraint = new TileConstraint(Orientation.NORTH);
        constraint.add(new TileRef(0, 1));

        final TileConstraint constraint2 = new TileConstraint(Orientation.NORTH);
        constraint2.add(new TileRef(0, 1));

        Assert.assertEquals(constraint.hashCode(), constraint.hashCode());
        Assert.assertEquals(constraint.hashCode(), constraint2.hashCode());

        Assert.assertNotEquals(constraint.hashCode(), new Object().hashCode());
        Assert.assertNotEquals(constraint.hashCode(), new TileConstraint(Orientation.NORTH).hashCode());
        Assert.assertNotEquals(constraint.hashCode(), new TileConstraint(Orientation.SOUTH).hashCode());
    }

    /**
     * Test the constraint equals.
     */
    @Test
    public void testEquals()
    {
        final TileConstraint constraint = new TileConstraint(Orientation.NORTH);
        constraint.add(new TileRef(0, 1));

        final TileConstraint constraint2 = new TileConstraint(Orientation.NORTH);
        constraint2.add(new TileRef(0, 1));

        Assert.assertEquals(constraint, constraint);
        Assert.assertEquals(constraint, constraint2);

        Assert.assertNotEquals(constraint, new Object());
        Assert.assertNotEquals(constraint, new TileConstraint(Orientation.NORTH));
        Assert.assertNotEquals(constraint, new TileConstraint(Orientation.SOUTH));
    }

    /**
     * Test the constraint to string.
     */
    @Test
    public void testToString()
    {
        final TileConstraint constraint = new TileConstraint(Orientation.NORTH);
        constraint.add(new TileRef(0, 1));

        Assert.assertEquals("NORTH [tileRef(sheet=0 | number=1)]", constraint.toString());
    }
}
