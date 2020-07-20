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
package com.b3dgs.lionengine.game;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;

/**
 * Test {@link CoordTile}.
 */
final class CoordTileTest
{
    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        final CoordTile coord = new CoordTile(1, 2);

        assertEquals(1, coord.getX());
        assertEquals(2, coord.getY());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final CoordTile coord = new CoordTile(1, 2);

        assertEquals(coord, coord);
        assertEquals(coord, new CoordTile(1, 2));

        assertNotEquals(coord, null);
        assertNotEquals(coord, new Object());
        assertNotEquals(coord, new CoordTile(0, 2));
        assertNotEquals(coord, new CoordTile(1, 0));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final CoordTile hash = new CoordTile(1, 2);

        assertHashEquals(hash, new CoordTile(1, 2));

        assertHashNotEquals(hash, new CoordTile(0, 2));
        assertHashNotEquals(hash, new CoordTile(1, 0));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        final CoordTile config = new CoordTile(1, 2);

        assertHashEquals("CoordTile [tx=1, ty=2]", config.toString());
    }
}
