/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import java.util.EnumSet;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.UtilTests;

/**
 * Test the path data class.
 */
public class PathDataTest
{
    /**
     * Test the getter.
     */
    @Test
    public void testGetter()
    {
        final PathData data = new PathData("category", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN));

        Assert.assertEquals("category", data.getName());
        Assert.assertEquals(1.0, data.getCost(), UtilTests.PRECISION);
        Assert.assertTrue(data.isBlocking());
        Assert.assertArrayEquals(EnumSet.of(MovementTile.UP, MovementTile.DOWN).toArray(),
                                 data.getAllowedMovements().toArray());
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashCode()
    {
        final int c = new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)).hashCode();

        Assert.assertEquals(c, new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)).hashCode());
        Assert.assertEquals(c, new PathData("c", 1.0, true, EnumSet.of(MovementTile.DOWN, MovementTile.UP)).hashCode());

        Assert.assertNotEquals(c, new Object().hashCode());
        Assert.assertNotEquals(c,
                               new PathData("", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)).hashCode());
        Assert.assertEquals(c, new PathData("c", 0.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)).hashCode());
        Assert.assertEquals(c,
                            new PathData("c", 1.0, false, EnumSet.of(MovementTile.UP, MovementTile.DOWN)).hashCode());
        Assert.assertEquals(c, new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP)).hashCode());
        Assert.assertEquals(c, new PathData("c", 1.0, true, EnumSet.of(MovementTile.DOWN)).hashCode());
    }

    /**
     * Test the equality.
     */
    @Test
    public void testEquals()
    {
        final PathData data = new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN));

        Assert.assertEquals(data, data);
        Assert.assertEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        Assert.assertEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.DOWN, MovementTile.UP)));

        Assert.assertNotEquals(data, null);
        Assert.assertNotEquals(data, new Object());
        Assert.assertNotEquals(data, new PathData("", 1.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        Assert.assertEquals(data, new PathData("c", 0.0, true, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        Assert.assertEquals(data, new PathData("c", 1.0, false, EnumSet.of(MovementTile.UP, MovementTile.DOWN)));
        Assert.assertEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.UP)));
        Assert.assertEquals(data, new PathData("c", 1.0, true, EnumSet.of(MovementTile.DOWN)));
    }
}
