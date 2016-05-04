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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.game.Orientation;

/**
 * Test the collision constraint class.
 */
public class CollisionConstraintTest
{
    /** Constraint test. */
    private final CollisionConstraint constraint = new CollisionConstraint();

    /**
     * Prepare test.
     */
    @Before
    public void before()
    {
        constraint.add(Orientation.EAST, "group");
    }

    /**
     * Test the constraint.
     */
    @Test
    public void testConstraint()
    {
        final CollisionConstraint empty = new CollisionConstraint();
        Assert.assertTrue(empty.getConstraints().values().iterator().next().isEmpty());

        Assert.assertEquals(Arrays.asList("group"), constraint.getConstraints(Orientation.EAST));
        Assert.assertTrue(constraint.has(Orientation.EAST, "group"));
    }

    /**
     * Test the constraint hash code.
     */
    @Test
    public void testHashcode()
    {
        final int hash = constraint.hashCode();

        final CollisionConstraint same = new CollisionConstraint();
        same.add(Orientation.EAST, "group");

        Assert.assertEquals(hash, same.hashCode());

        Assert.assertNotEquals(hash, new Object().hashCode());

        final CollisionConstraint other = new CollisionConstraint();
        other.add(Orientation.NORTH, "group");

        Assert.assertNotEquals(hash, other.hashCode());

        final CollisionConstraint other2 = new CollisionConstraint();
        other2.add(Orientation.EAST, "void");

        Assert.assertNotEquals(hash, other2.hashCode());
    }

    /**
     * Test the constraint equality.
     */
    @Test
    public void testEquals()
    {
        Assert.assertEquals(constraint, constraint);

        final CollisionConstraint same = new CollisionConstraint();
        same.add(Orientation.EAST, "group");

        Assert.assertEquals(constraint, same);

        Assert.assertNotEquals(constraint, null);
        Assert.assertNotEquals(constraint, new Object());

        final CollisionConstraint other = new CollisionConstraint();
        other.add(Orientation.NORTH, "group");

        Assert.assertNotEquals(constraint, other);

        final CollisionConstraint other2 = new CollisionConstraint();
        other2.add(Orientation.EAST, "void");

        Assert.assertNotEquals(constraint, other2);
    }

    /**
     * Test the constraint to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("CollisionConstraint{NORTH=[], NORTH_EAST=[], EAST=[group], SOUTH_EAST=[], "
                            + "SOUTH=[], SOUTH_WEST=[], WEST=[], NORTH_WEST=[]}",
                            constraint.toString());
    }
}
