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
package com.b3dgs.lionengine.game.collision.tile;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.Axis;

/**
 * Test the collision range class.
 */
public class CollisionRangeTest
{
    /** Range test. */
    private final CollisionRange range = new CollisionRange(Axis.X, 0, 1, 2, 3);

    /**
     * Test the range class.
     */
    @Test
    public void testRange()
    {
        Assert.assertEquals(Axis.X, range.getOutput());
        Assert.assertEquals(0, range.getMinX());
        Assert.assertEquals(1, range.getMaxX());
        Assert.assertEquals(2, range.getMinY());
        Assert.assertEquals(3, range.getMaxY());
    }

    /**
     * Test the range hash code.
     */
    @Test
    public void testHashcode()
    {
        final int hash = range.hashCode();
        Assert.assertEquals(hash, new CollisionRange(Axis.X, 0, 1, 2, 3).hashCode());

        Assert.assertNotEquals(hash, new Object().hashCode());
        Assert.assertNotEquals(hash, new CollisionRange(Axis.Y, 0, 1, 2, 3).hashCode());
        Assert.assertNotEquals(hash, new CollisionRange(Axis.X, 1, 1, 2, 3).hashCode());
        Assert.assertNotEquals(hash, new CollisionRange(Axis.X, 0, 2, 2, 3).hashCode());
        Assert.assertNotEquals(hash, new CollisionRange(Axis.X, 0, 1, 3, 3).hashCode());
        Assert.assertNotEquals(hash, new CollisionRange(Axis.X, 0, 1, 2, 4).hashCode());
    }

    /**
     * Test the range equality.
     */
    @Test
    public void testEquals()
    {
        Assert.assertEquals(range, range);
        Assert.assertEquals(range, new CollisionRange(Axis.X, 0, 1, 2, 3));

        Assert.assertNotEquals(range, null);
        Assert.assertNotEquals(range, new Object());
        Assert.assertNotEquals(range, new CollisionRange(Axis.Y, 0, 1, 2, 3));
        Assert.assertNotEquals(range, new CollisionRange(Axis.X, 1, 1, 2, 3));
        Assert.assertNotEquals(range, new CollisionRange(Axis.X, 0, 2, 2, 3));
        Assert.assertNotEquals(range, new CollisionRange(Axis.X, 0, 1, 3, 3));
        Assert.assertNotEquals(range, new CollisionRange(Axis.X, 0, 1, 2, 4));
    }

    /**
     * Test the range to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("CollisionRange (output=X, minX=0, maxX=1, minY=2, maxY=3)", range.toString());
    }
}
