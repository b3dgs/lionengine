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
package com.b3dgs.lionengine.test.geom;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Line;
import com.b3dgs.lionengine.geom.Polygon;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Test the polygon class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PolygonTest
{
    /**
     * Test the polygon class.
     */
    @Test
    public void testPolygon()
    {
        final Polygon polygon = Geom.createPolygon();
        polygon.addPoint(0, 0);
        polygon.addPoint(0, 10);
        polygon.addPoint(10, 0);
        Assert.assertFalse(polygon.getRectangle().contains(-1, -1));
        polygon.addPoint(10, 10);
        polygon.addPoint(-10, -10);

        for (final Line line : polygon.getPoints())
        {
            Assert.assertNotNull(line);
        }

        final Rectangle rectangle1 = Geom.createRectangle();
        rectangle1.set(0.0, 0.0, 10.0, 10.0);
        final Rectangle rectangle2 = Geom.createRectangle(1.0, 1.0, 5.0, 5.0);
        Assert.assertTrue(polygon.getRectangle().contains(rectangle2));
        Assert.assertTrue(polygon.intersects(rectangle2));
        polygon.reset();
        Assert.assertFalse(polygon.contains(rectangle2));
        Assert.assertFalse(polygon.intersects(rectangle2));

        for (final Line line : polygon.getPoints())
        {
            Assert.assertNotNull(line);
        }
    }
}
