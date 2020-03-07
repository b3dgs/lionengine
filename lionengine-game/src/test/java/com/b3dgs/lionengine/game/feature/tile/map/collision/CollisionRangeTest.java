/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * Test {@link CollisionRange}.
 */
public final class CollisionRangeTest
{
    /** Range test. */
    private final CollisionRange range = new CollisionRange(Axis.X, 0, 1, 2, 3);

    /**
     * Test the range class.
     */
    @Test
    public void testRange()
    {
        assertEquals(Axis.X, range.getOutput());
        assertEquals(0, range.getMinX());
        assertEquals(1, range.getMaxX());
        assertEquals(2, range.getMinY());
        assertEquals(3, range.getMaxY());
    }

    /**
     * Test the range equality.
     */
    @Test
    public void testEquals()
    {
        assertEquals(range, range);
        assertEquals(range, new CollisionRange(Axis.X, 0, 1, 2, 3));

        assertNotEquals(range, null);
        assertNotEquals(range, new Object());
        assertNotEquals(range, new CollisionRange(Axis.Y, 0, 1, 2, 3));
        assertNotEquals(range, new CollisionRange(Axis.X, 1, 1, 2, 3));
        assertNotEquals(range, new CollisionRange(Axis.X, 0, 2, 2, 3));
        assertNotEquals(range, new CollisionRange(Axis.X, 0, 1, 3, 3));
        assertNotEquals(range, new CollisionRange(Axis.X, 0, 1, 2, 4));
    }

    /**
     * Test the range hash code.
     */
    @Test
    public void testHashCode()
    {
        assertHashEquals(range, new CollisionRange(Axis.X, 0, 1, 2, 3));

        assertHashNotEquals(range, new Object());
        assertHashNotEquals(range, new CollisionRange(Axis.Y, 0, 1, 2, 3));
        assertHashNotEquals(range, new CollisionRange(Axis.X, 1, 1, 2, 3));
        assertHashNotEquals(range, new CollisionRange(Axis.X, 0, 2, 2, 3));
        assertHashNotEquals(range, new CollisionRange(Axis.X, 0, 1, 3, 3));
        assertHashNotEquals(range, new CollisionRange(Axis.X, 0, 1, 2, 4));
    }

    /**
     * Test the range to string.
     */
    @Test
    public void testToString()
    {
        assertEquals("CollisionRange (output=X, minX=0, maxX=1, minY=2, maxY=3)", range.toString());
    }
}
