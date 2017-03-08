/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the tile group class.
 */
public class TileGroupTest
{
    /**
     * Test the contains.
     */
    @Test
    public void testContains()
    {
        final Collection<TileRef> tiles = Arrays.asList(new TileRef(0, 1));
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.NONE, tiles);

        Assert.assertTrue(tileGroup.contains(Integer.valueOf(0), 1));
        Assert.assertTrue(tileGroup.contains(new TileGame(Integer.valueOf(0), 1, 1, 1, 1, 1)));

        Assert.assertFalse(tileGroup.contains(Integer.valueOf(0), 2));
        Assert.assertFalse(tileGroup.contains(Integer.valueOf(1), 1));
    }

    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final Collection<TileRef> tiles = Arrays.asList(new TileRef(0, 1));
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.PLAIN, tiles);

        Assert.assertEquals("test", tileGroup.getName());
        Assert.assertEquals(TileGroupType.PLAIN, tileGroup.getType());
        Assert.assertEquals(tiles, tileGroup.getTiles());
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashcode()
    {
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1)));

        Assert.assertEquals(tileGroup.hashCode(), tileGroup.hashCode());
        Assert.assertEquals(tileGroup.hashCode(),
                            new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1))).hashCode());

        Assert.assertNotEquals(tileGroup.hashCode(), new Object().hashCode());
        Assert.assertNotEquals(tileGroup.hashCode(),
                               new TileGroup("toto", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1))).hashCode());
        Assert.assertEquals(tileGroup.hashCode(),
                            new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 2))).hashCode());
        Assert.assertEquals(tileGroup.hashCode(),
                            new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(1, 1))).hashCode());
        Assert.assertNotEquals(tileGroup.hashCode(),
                               new TileGroup("toto", TileGroupType.NONE, Arrays.asList(new TileRef(1, 1))).hashCode());
        Assert.assertNotEquals(tileGroup.hashCode(),
                               new TileGroup("toto",
                                             TileGroupType.TRANSITION,
                                             Arrays.asList(new TileRef(0, 1))).hashCode());
        Assert.assertEquals(tileGroup.hashCode(),
                            new TileGroup("test",
                                          TileGroupType.TRANSITION,
                                          Arrays.asList(new TileRef(0, 2))).hashCode());
        Assert.assertEquals(tileGroup.hashCode(),
                            new TileGroup("test",
                                          TileGroupType.TRANSITION,
                                          Arrays.asList(new TileRef(1, 1))).hashCode());
        Assert.assertNotEquals(tileGroup.hashCode(),
                               new TileGroup("toto",
                                             TileGroupType.TRANSITION,
                                             Arrays.asList(new TileRef(1, 1))).hashCode());
    }

    /**
     * Test the equals.
     */
    @Test
    public void testEquals()
    {
        final TileGroup tileGroup = new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1)));

        Assert.assertEquals(tileGroup, tileGroup);
        Assert.assertEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1))));

        Assert.assertNotEquals(tileGroup, new Object());
        Assert.assertNotEquals(tileGroup, new TileGroup("toto", TileGroupType.NONE, Arrays.asList(new TileRef(0, 1))));
        Assert.assertEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(0, 2))));
        Assert.assertEquals(tileGroup, new TileGroup("test", TileGroupType.NONE, Arrays.asList(new TileRef(1, 1))));
        Assert.assertNotEquals(tileGroup, new TileGroup("toto", TileGroupType.NONE, Arrays.asList(new TileRef(1, 1))));
        Assert.assertNotEquals(tileGroup,
                               new TileGroup("toto", TileGroupType.TRANSITION, Arrays.asList(new TileRef(0, 1))));
        Assert.assertEquals(tileGroup,
                            new TileGroup("test", TileGroupType.TRANSITION, Arrays.asList(new TileRef(0, 2))));
        Assert.assertEquals(tileGroup,
                            new TileGroup("test", TileGroupType.TRANSITION, Arrays.asList(new TileRef(1, 1))));
        Assert.assertNotEquals(tileGroup,
                               new TileGroup("toto", TileGroupType.TRANSITION, Arrays.asList(new TileRef(1, 1))));
    }
}
