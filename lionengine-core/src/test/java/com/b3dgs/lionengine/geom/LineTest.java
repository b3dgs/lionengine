/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.geom;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the line class.
 */
public class LineTest
{
    /**
     * Test the line class.
     */
    @Test
    public void testLine()
    {
        final Line line1 = Geom.createLine();
        line1.set(1.0, -1.0, 1.0, 1.0);

        final Line line2 = Geom.createLine(0.0, 0.0, 2.0, 0.0);
        final Coord point = Geom.createCoord(1.0, 0.0);
        final Coord intersect = Geom.intersection(line1, line2);

        Assert.assertEquals(point.getX(), intersect.getX(), 0.000000001);
        Assert.assertEquals(point.getX(), intersect.getX(), 0.000000001);
    }
}
