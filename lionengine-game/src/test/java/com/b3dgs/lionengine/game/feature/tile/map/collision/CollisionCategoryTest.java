/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Test {@link CollisionCategory}.
 */
final class CollisionCategoryTest
{
    /** Formula test. */
    private final CollisionFormula formula = new CollisionFormula("formula",
                                                                  new CollisionRange(Axis.X, 0, 1, 2, 3),
                                                                  new CollisionFunctionLinear(1.0, 2.0),
                                                                  new CollisionConstraint());
    /** Group test. */
    private final CollisionGroup group = new CollisionGroup("group", Arrays.asList(formula));
    /** Category test. */
    private final CollisionCategory category = new CollisionCategory("name", Axis.X, 1, 2, true, Arrays.asList(group));

    /**
     * Test the category construction.
     */
    @Test
    void testCategory()
    {
        assertEquals("name", category.getName());
        assertEquals(Axis.X, category.getAxis());
        assertEquals(1, category.getOffsetX());
        assertEquals(2, category.getOffsetY());
        assertEquals(group, category.getGroups().iterator().next());
    }

    /**
     * Test the category equality.
     */
    @Test
    void testEquals()
    {
        assertEquals(category, category);
        assertEquals(category, new CollisionCategory("name", Axis.X, 1, 2, true, Arrays.asList(group)));
        assertNotEquals(category, new CollisionCategory("name", Axis.Y, 1, 2, true, Arrays.asList(group)));
        assertNotEquals(category, new CollisionCategory("name", Axis.X, 2, 2, true, Arrays.asList(group)));
        assertNotEquals(category, new CollisionCategory("name", Axis.X, 1, 3, true, Arrays.asList(group)));
        assertNotEquals(category, new CollisionCategory("name", Axis.X, 1, 2, false, Arrays.asList(group)));
        assertNotEquals(category, new CollisionCategory("name", Axis.X, 1, 2, true, new ArrayList<>()));

        assertNotEquals(category, null);
        assertNotEquals(category, new Object());
        assertNotEquals(category, new CollisionCategory("void", Axis.X, 1, 2, true, Arrays.asList(group)));
    }

    /**
     * Test the category hash code.
     */
    @Test
    void testHashCode()
    {
        assertHashEquals(category, new CollisionCategory("name", Axis.X, 1, 2, true, Arrays.asList(group)));
        assertHashNotEquals(category, new CollisionCategory("name", Axis.Y, 1, 2, true, Arrays.asList(group)));
        assertHashNotEquals(category, new CollisionCategory("name", Axis.X, 2, 2, true, Arrays.asList(group)));
        assertHashNotEquals(category, new CollisionCategory("name", Axis.X, 1, 3, true, Arrays.asList(group)));
        assertHashNotEquals(category, new CollisionCategory("name", Axis.X, 1, 2, false, Arrays.asList(group)));
        assertHashNotEquals(category, new CollisionCategory("name", Axis.X, 1, 2, true, new ArrayList<>()));

        assertHashNotEquals(category, new Object());
        assertHashNotEquals(category, new CollisionCategory("void", Axis.X, 1, 2, true, Arrays.asList(group)));
    }

    /**
     * Test the category to string.
     */
    @Test
    void testToString()
    {
        assertEquals("""
                CollisionCategory[name=name, axis=X, x=1, y=2, glue=true,\s\
                groups=[CollisionGroup[name=group, formulas=[CollisionFormula[name=formula,\s\
                range=CollisionRange[output=X, minX=0, maxX=1, minY=2, maxY=3],\s\
                function=CollisionFunctionLinear[a=1.0, b=2.0], constraint=CollisionConstraint{NORTH=[],\s\
                NORTH_EAST=[], EAST=[], SOUTH_EAST=[], SOUTH=[], SOUTH_WEST=[], WEST=[], NORTH_WEST=[]}]]]]]""",
                     category.toString());
    }
}
