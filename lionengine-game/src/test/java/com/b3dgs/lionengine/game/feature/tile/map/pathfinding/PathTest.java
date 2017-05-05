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

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the path class.
 */
public class PathTest
{
    /**
     * Test the path.
     */
    @Test
    public void testPath()
    {
        final Path path = new Path();

        Assert.assertFalse(path.contains(0, 1));
        Assert.assertEquals(0, path.getLength());

        path.appendStep(0, 1);

        Assert.assertTrue(path.contains(0, 1));
        Assert.assertEquals(1, path.getLength());
        Assert.assertEquals(0, path.getX(0));
        Assert.assertEquals(1, path.getY(0));

        path.prependStep(1, 2);

        Assert.assertTrue(path.contains(1, 2));
        Assert.assertEquals(2, path.getLength());
        Assert.assertEquals(1, path.getX(0));
        Assert.assertEquals(2, path.getY(0));
        Assert.assertEquals(0, path.getX(1));
        Assert.assertEquals(1, path.getY(1));
    }
}
