/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.game.Orientation;

/**
 * Test {@link CollisionConstraint}.
 */
public final class CollisionConstraintTest
{
    /** Constraint test. */
    private final CollisionConstraint constraint = new CollisionConstraint();

    /**
     * Prepare test.
     */
    @BeforeEach
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

        assertTrue(empty.getConstraints().values().iterator().next().isEmpty());
        assertEquals(Arrays.asList("group"), constraint.getConstraints(Orientation.EAST));
        assertTrue(constraint.has(Orientation.EAST, "group"));
    }

    /**
     * Test the constraint equality.
     */
    @Test
    public void testEquals()
    {
        assertEquals(constraint, constraint);

        final CollisionConstraint same = new CollisionConstraint();
        same.add(Orientation.EAST, "group");

        assertEquals(constraint, same);

        assertNotEquals(constraint, null);
        assertNotEquals(constraint, new Object());

        final CollisionConstraint other = new CollisionConstraint();
        other.add(Orientation.NORTH, "group");

        assertNotEquals(constraint, other);

        final CollisionConstraint other2 = new CollisionConstraint();
        other2.add(Orientation.EAST, "void");

        assertNotEquals(constraint, other2);
    }

    /**
     * Test the constraint hash code.
     */
    @Test
    public void testHashCode()
    {
        final CollisionConstraint same = new CollisionConstraint();
        same.add(Orientation.EAST, "group");

        assertHashEquals(constraint, same);
        assertHashNotEquals(constraint, new Object());

        final CollisionConstraint other = new CollisionConstraint();
        other.add(Orientation.NORTH, "group");

        assertHashNotEquals(constraint, other);

        final CollisionConstraint other2 = new CollisionConstraint();
        other2.add(Orientation.EAST, "void");

        assertHashNotEquals(constraint, other2);
    }

    /**
     * Test the constraint to string.
     */
    @Test
    public void testToString()
    {
        assertEquals("CollisionConstraint{NORTH=[], NORTH_EAST=[], EAST=[group], SOUTH_EAST=[], "
                     + "SOUTH=[], SOUTH_WEST=[], WEST=[], NORTH_WEST=[]}",
                     constraint.toString());
    }
}
