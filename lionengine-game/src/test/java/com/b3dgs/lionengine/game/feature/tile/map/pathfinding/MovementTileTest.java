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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilTests;

/**
 * Test {@link MovementTile}.
 */
final class MovementTileTest
{
    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    void testEnum() throws Exception
    {
        UtilTests.testEnum(MovementTile.class);
    }

    /**
     * Test is.
     */
    @Test
    void testIs()
    {
        assertTrue(MovementTile.UP.is(0, 1));
        assertTrue(MovementTile.DOWN.is(0, -1));
        assertTrue(MovementTile.LEFT.is(-1, 0));
        assertTrue(MovementTile.RIGHT.is(1, 0));
        assertTrue(MovementTile.DIAGONAL_UP_LEFT.is(-1, 1));
        assertTrue(MovementTile.DIAGONAL_UP_RIGHT.is(1, 1));
        assertTrue(MovementTile.DIAGONAL_DOWN_LEFT.is(-1, -1));
        assertTrue(MovementTile.DIAGONAL_DOWN_RIGHT.is(1, -1));
        assertTrue(MovementTile.NONE.is(0, 0));

        assertFalse(MovementTile.UP.is(1, 1));
        assertFalse(MovementTile.DOWN.is(1, -1));
        assertFalse(MovementTile.LEFT.is(-1, 1));
        assertFalse(MovementTile.RIGHT.is(1, 1));
        assertFalse(MovementTile.DIAGONAL_UP_LEFT.is(0, 0));
        assertFalse(MovementTile.DIAGONAL_UP_RIGHT.is(0, 0));
        assertFalse(MovementTile.DIAGONAL_DOWN_LEFT.is(0, 0));
        assertFalse(MovementTile.DIAGONAL_DOWN_RIGHT.is(0, 0));
        assertFalse(MovementTile.NONE.is(1, 1));
    }

    /**
     * Test is.
     */
    @Test
    void testFrom()
    {
        assertEquals(MovementTile.UP, MovementTile.from(0, 1));
        assertEquals(MovementTile.DOWN, MovementTile.from(0, -1));
        assertEquals(MovementTile.LEFT, MovementTile.from(-1, 0));
        assertEquals(MovementTile.RIGHT, MovementTile.from(1, 0));
        assertEquals(MovementTile.DIAGONAL_UP_LEFT, MovementTile.from(-1, 1));
        assertEquals(MovementTile.DIAGONAL_UP_RIGHT, MovementTile.from(1, 1));
        assertEquals(MovementTile.DIAGONAL_DOWN_LEFT, MovementTile.from(-1, -1));
        assertEquals(MovementTile.DIAGONAL_DOWN_RIGHT, MovementTile.from(1, -1));
        assertEquals(MovementTile.NONE, MovementTile.from(0, 0));
        assertEquals(MovementTile.NONE, MovementTile.from(10, 10));
    }

    /**
     * Test get name.
     */
    @Test
    void testGetName()
    {
        for (final MovementTile movement : MovementTile.values())
        {
            assertEquals(UtilConversion.toTitleCase(movement.getName()), movement.getName());
        }
    }
}
