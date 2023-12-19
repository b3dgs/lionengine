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
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link CollisionFunctionLinear}.
 */
final class CollisionFunctionLinearTest
{
    /** Function test. */
    private final CollisionFunctionLinear function = new CollisionFunctionLinear(2.0, 3.0);

    /**
     * Test the function.
     */
    @Test
    void testFunction()
    {
        assertEquals(13.0, function.compute(5.0));
        assertEquals(-7.0, function.compute(-5.0));
        assertEquals(-7.0, function.getRenderX(-5.0));
        assertEquals(13.0, function.getRenderX(5.0));
        assertEquals(-7.0, function.getRenderY(-5.0));
        assertEquals(13.0, function.getRenderY(5.0));
        assertEquals(-9.0, new CollisionFunctionLinear(-2.0, 3.0).getRenderY(5.0));
        assertEquals(CollisionFunctionType.LINEAR, function.getType());
        assertEquals(2.0, function.getA());
        assertEquals(3.0, function.getB());
    }

    /**
     * Test the function hash code.
     */
    @Test
    void testHashCode()
    {
        assertHashEquals(function, new CollisionFunctionLinear(2.0, 3.0));

        assertHashNotEquals(function, new Object());
        assertHashNotEquals(function, new CollisionFunctionLinear(1.5, 3.0));
        assertHashNotEquals(function, new CollisionFunctionLinear(2.0, 3.5));
    }

    /**
     * Test the function equality.
     */
    @Test
    void testEquals()
    {
        assertEquals(function, function);
        assertEquals(function, new CollisionFunctionLinear(2.0, 3.0));

        assertNotEquals(function, null);
        assertNotEquals(function, new Object());
        assertNotEquals(function, new CollisionFunctionLinear(1.5, 3.0));
        assertNotEquals(function, new CollisionFunctionLinear(2.0, 3.5));
    }

    /**
     * Test the function to string.
     */
    @Test
    void testToString()
    {
        assertEquals("CollisionFunctionLinear[a=2.0, b=3.0]", function.toString());
    }
}
