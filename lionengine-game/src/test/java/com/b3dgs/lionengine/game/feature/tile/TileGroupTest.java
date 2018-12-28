/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile;

import static com.b3dgs.lionengine.UtilAssert.assertArrayEquals;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

/**
 * Test {@link TileGroup}.
 */
public final class TileGroupTest
{
    /**
     * Test the contains.
     */
    @Test
    public void testContains()
    {
        final Collection<TileRef> tiles = Arrays.asList(new TileRef(0, 1));
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.NONE, tiles);

        assertTrue(tileGroup.contains(Integer.valueOf(0), 1));
        assertTrue(tileGroup.contains(new TileGame(Integer.valueOf(0), 1, 1, 1, 1, 1)));

        assertFalse(tileGroup.contains(Integer.valueOf(0), 2));
        assertFalse(tileGroup.contains(Integer.valueOf(1), 1));
    }

    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final Collection<TileRef> tiles = Arrays.asList(new TileRef(0, 1));
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.PLAIN, tiles);

        assertEquals("test", tileGroup.getName());
        assertEquals(TileGroupType.PLAIN, tileGroup.getType());
        assertArrayEquals(tiles.toArray(), tileGroup.getTiles().toArray());
    }

    /**
     * Test the equals.
     */
    @Test
    public void testEquals()
    {
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1)));

        assertEquals(tileGroup, tileGroup);
        assertEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1))));

        assertNotEquals(tileGroup, new Object());
        assertNotEquals(tileGroup, new TileGroup("toto", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1))));
        assertEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 2))));
        assertEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(1, 1))));
        assertNotEquals(tileGroup, new TileGroup("toto", TileGroupType.NONE, Arrays.asList(new TileRef(1, 1))));
        assertNotEquals(tileGroup, new TileGroup("toto", TileGroupType.TRANSITION, Arrays.asList(new TileRef(0, 1))));
        assertEquals(tileGroup, new TileGroup("test", TileGroupType.TRANSITION, Arrays.asList(new TileRef(0, 2))));
        assertEquals(tileGroup, new TileGroup("test", TileGroupType.TRANSITION, Arrays.asList(new TileRef(1, 1))));
        assertNotEquals(tileGroup, new TileGroup("toto", TileGroupType.TRANSITION, Arrays.asList(new TileRef(1, 1))));
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashCode()
    {
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1)));

        assertHashEquals(tileGroup, tileGroup);
        assertHashEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1))));

        assertHashNotEquals(tileGroup, new Object());
        assertHashNotEquals(tileGroup, new TileGroup("toto", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1))));
        assertHashEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 2))));
        assertHashEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(1, 1))));
        assertHashNotEquals(tileGroup, new TileGroup("toto", TileGroupType.NONE, Arrays.asList(new TileRef(1, 1))));
        assertHashNotEquals(tileGroup,
                            new TileGroup("toto", TileGroupType.TRANSITION, Arrays.asList(new TileRef(0, 1))));
        assertHashEquals(tileGroup, new TileGroup("test", TileGroupType.TRANSITION, Arrays.asList(new TileRef(0, 2))));
        assertHashEquals(tileGroup, new TileGroup("test", TileGroupType.TRANSITION, Arrays.asList(new TileRef(1, 1))));
        assertHashNotEquals(tileGroup,
                            new TileGroup("toto", TileGroupType.TRANSITION, Arrays.asList(new TileRef(1, 1))));
    }
}
