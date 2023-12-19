/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertArrayEquals;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

/**
 * Test {@link PathData}.
 */
final class PathDataTest
{
    /**
     * Test the getter.
     */
    @Test
    void testGetter()
    {
        final PathData data = new PathData("category", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN));

        assertEquals("category", data.getName());
        assertEquals(1.0, data.getCost());
        assertTrue(data.isBlocking());
        assertArrayEquals(EnumSet.of(MovementTile.UP, MovementTile.DOWN).toArray(),
                          data.getAllowedMovements().toArray());
    }

    /**
     * Test the equality.
     */
    @Test
    void testEquals()
    {
        final PathData data = new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN));

        assertEquals(data, data);
        assertEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        assertEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.DOWN, MovementTile.UP)));

        assertNotEquals(data, null);
        assertNotEquals(data, new Object());
        assertNotEquals(data, new PathData("", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        assertNotEquals(data, new PathData("c", 0.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        assertNotEquals(data, new PathData("c", 1.0, false, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        assertNotEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP)));
        assertNotEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.DOWN)));
    }

    /**
     * Test the hash code.
     */
    @Test
    void testHashCode()
    {
        final PathData data = new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN));

        assertHashEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        assertHashEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.DOWN, MovementTile.UP)));

        assertHashNotEquals(data, new Object());
        assertHashNotEquals(data, new PathData("", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        assertHashNotEquals(data, new PathData("c", 0.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        assertHashNotEquals(data, new PathData("c", 1.0, false, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        assertHashNotEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP)));
        assertHashNotEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.DOWN)));
    }
}
