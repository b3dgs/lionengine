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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;

/**
 * Test {@link CollisionGroup}.
 */
final class CollisionGroupTest
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
    void testSame()
    {
        assertTrue(CollisionGroup.same(null, null));
        assertTrue(CollisionGroup.same("a", "a"));

        assertFalse(CollisionGroup.same(null, "a"));
        assertFalse(CollisionGroup.same("a", null));
        assertFalse(CollisionGroup.same("a", "b"));
    }

    /**
     * Test the group.
     */
    @Test
    void testGroup()
    {
        assertEquals("group", group.getName());
        assertEquals(formula, group.getFormulas().iterator().next());
    }

    /**
     * Test the group hash code.
     */
    @Test
    void testHashCode()
    {
        assertHashEquals(group, new CollisionGroup("group", new ArrayList<>()));
        assertHashEquals(group, new CollisionGroup("group", Arrays.asList(formula)));

        assertHashNotEquals(group, new Object());
        assertHashNotEquals(group, new CollisionGroup("void", Arrays.asList(formula)));
    }

    /**
     * Test the group equality.
     */
    @Test
    void testEquals()
    {
        assertEquals(group, group);
        assertEquals(group, new CollisionGroup("group", new ArrayList<>()));
        assertEquals(group, new CollisionGroup("group", Arrays.asList(formula)));

        assertNotEquals(group, null);
        assertNotEquals(group, new Object());
        assertNotEquals(group, new CollisionGroup("void", Arrays.asList(formula)));
    }

    /**
     * Test the group to string.
     */
    @Test
    void testToString()
    {
        assertEquals("CollisionGroup (name=group)"
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
                     + "CollisionConstraint{NORTH=[], NORTH_EAST=[], EAST=[], SOUTH_EAST=[], SOUTH=[], "
                     + "SOUTH_WEST=[], WEST=[], NORTH_WEST=[]}"
                     + System.lineSeparator()
                     + "]",
                     group.toString());
    }
}
