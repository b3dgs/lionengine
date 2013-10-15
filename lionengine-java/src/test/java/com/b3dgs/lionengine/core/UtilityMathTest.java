/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Coord;
import com.b3dgs.lionengine.Line;
import com.b3dgs.lionengine.Polygon;
import com.b3dgs.lionengine.Rectangle;

/**
 * Test the utility math class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityMathTest
{
    /**
     * Test utility math.
     */
    @Test
    public void testMath()
    {
        final Line line1 = UtilityMath.createLine();
        line1.set(1.0, -1.0, 1.0, 1.0);
        final Line line2 = UtilityMath.createLine(0.0, 0.0, 2.0, 0.0);
        final Coord point = new Coord(1.0, 0.0);
        final Coord intersect = UtilityMath.intersection(line1, line2);
        Assert.assertEquals(point.getX(), intersect.getX(), 0.000000001);
        Assert.assertEquals(point.getX(), intersect.getX(), 0.000000001);

        final Rectangle rectangle1 = UtilityMath.createRectangle();
        rectangle1.set(0.0, 0.0, 10.0, 10.0);
        final Rectangle rectangle2 = UtilityMath.createRectangle(1.0, 1.0, 5.0, 5.0);
        Assert.assertTrue(rectangle1.contains(rectangle2));
        Assert.assertTrue(rectangle2.intersects(rectangle1));

        Assert.assertEquals(1.0, rectangle2.getX(), 0.000000001);
        Assert.assertEquals(1.0, rectangle2.getY(), 0.000000001);

        Assert.assertEquals(1.0, rectangle2.getMinX(), 0.000000001);
        Assert.assertEquals(1.0, rectangle2.getMinY(), 0.000000001);
        Assert.assertEquals(6.0, rectangle2.getMaxX(), 0.000000001);
        Assert.assertEquals(6.0, rectangle2.getMaxX(), 0.000000001);
        Assert.assertEquals(5.0, rectangle2.getWidth(), 0.000000001);
        Assert.assertEquals(5.0, rectangle2.getHeight(), 0.000000001);

        Assert.assertTrue(rectangle1.contains(2, 3));

        final Polygon polygon = UtilityMath.createPolygon();
        polygon.addPoint(0, 0);
        polygon.addPoint(0, 10);
        polygon.addPoint(10, 0);
        Assert.assertFalse(polygon.getRectangle().contains(-1, -1));
        polygon.addPoint(10, 10);
        Assert.assertTrue(polygon.getRectangle().contains(rectangle2));
        Assert.assertTrue(polygon.intersects(rectangle2));
        polygon.reset();
        Assert.assertFalse(polygon.contains(rectangle2));
        Assert.assertFalse(polygon.intersects(rectangle2));

        try
        {
            UtilityMath.intersection(line1, line1);
        }
        catch (final IllegalStateException exception)
        {
            // Success
        }
    }
}
