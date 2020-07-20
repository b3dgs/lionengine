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
package com.b3dgs.lionengine.game.feature.tile;

import static com.b3dgs.lionengine.UtilAssert.assertArrayEquals;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Test {@link TileGroup}.
 */
final class TileGroupTest
{
    /**
     * Get tile from number.
     * 
     * @param number The tile number.
     * @return The tile set.
     */
    private static Set<Integer> getTile(int number)
    {
        return new HashSet<>(Arrays.asList(Integer.valueOf(number)));
    }

    /**
     * Test the contains.
     */
    @Test
    void testContains()
    {
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.NONE, getTile(1));

        assertTrue(tileGroup.contains(Integer.valueOf(1)));
        assertTrue(tileGroup.contains(new TileGame(1, 1, 1, 1, 1)));

        assertFalse(tileGroup.contains(Integer.valueOf(2)));
    }

    /**
     * Test the getters.
     */
    @Test
    void testGetters()
    {
        final Set<Integer> tiles = getTile(1);
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.PLAIN, tiles);

        assertEquals("test", tileGroup.getName());
        assertEquals(TileGroupType.PLAIN, tileGroup.getType());
        assertArrayEquals(tiles.toArray(), tileGroup.getTiles().toArray());
    }

    /**
     * Test the equals.
     */
    @Test
    void testEquals()
    {
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.NONE, getTile(1));

        assertEquals(tileGroup, tileGroup);
        assertEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, getTile(1)));
        assertEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, getTile(2)));
        assertEquals(tileGroup, new TileGroup("test", TileGroupType.TRANSITION, getTile(1)));
        assertEquals(tileGroup, new TileGroup("test", TileGroupType.TRANSITION, getTile(2)));

        assertNotEquals(tileGroup, new Object());
        assertNotEquals(tileGroup, new TileGroup("toto", TileGroupType.NONE, getTile(1)));
        assertNotEquals(tileGroup, new TileGroup("toto", TileGroupType.TRANSITION, getTile(1)));
    }

    /**
     * Test the hash code.
     */
    @Test
    void testHashCode()
    {
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.NONE, getTile(1));

        assertHashEquals(tileGroup, tileGroup);
        assertHashEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, getTile(1)));
        assertHashEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, getTile(2)));
        assertHashEquals(tileGroup, new TileGroup("test", TileGroupType.TRANSITION, getTile(1)));
        assertHashEquals(tileGroup, new TileGroup("test", TileGroupType.TRANSITION, getTile(2)));

        assertHashNotEquals(tileGroup, new Object());
        assertHashNotEquals(tileGroup, new TileGroup("toto", TileGroupType.NONE, getTile(1)));
        assertHashNotEquals(tileGroup, new TileGroup("toto", TileGroupType.TRANSITION, getTile(1)));
    }
}
