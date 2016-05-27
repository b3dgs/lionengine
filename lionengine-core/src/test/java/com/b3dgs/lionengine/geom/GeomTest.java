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
package com.b3dgs.lionengine.geom;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the geom class.
 */
public class GeomTest
{
    /**
     * Test geom class.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testGeomClass() throws Exception
    {
        UtilTests.testPrivateConstructor(Geom.class);
    }

    /**
     * Test geom create coord.
     */
    @Test
    public void testGeomCreateCoord()
    {
        Assert.assertNotNull(Geom.createCoord());

        final Coord coord = Geom.createCoord(1.0, 2.0);
        Assert.assertNotNull(coord);

        final Coord coordCopy = Geom.createCoord(coord);
        Assert.assertEquals(coord.getX(), coordCopy.getX(), UtilTests.PRECISION);
        Assert.assertEquals(coord.getY(), coordCopy.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the coord intersection function.
     */
    @Test
    public void testCoordIntersection()
    {
        final Coord coordZero = Geom.intersection(Geom.createLine(), Geom.createLine());
        Assert.assertEquals(Geom.createCoord(0.0, 0.0), coordZero);

        final Line line1 = Geom.createLine(1, 2, 3, 4);
        final Line line2 = Geom.createLine(-1, 2, -3, 4);
        final Coord coord = Geom.intersection(line1, line2);
        Assert.assertNotNull(coord);
    }

    /**
     * Test geom create line.
     */
    @Test
    public void testGeomCreateLine()
    {
        Assert.assertNotNull(Geom.createLine());

        final Line line = Geom.createLine(0.0, 1.0, 2.0, 3.0);
        Assert.assertNotNull(line);

        final Line lineCopy = Geom.createLine(line);
        Assert.assertEquals(line.getX1(), lineCopy.getX1(), UtilTests.PRECISION);
        Assert.assertEquals(line.getY1(), lineCopy.getY1(), UtilTests.PRECISION);
        Assert.assertEquals(line.getX2(), lineCopy.getX2(), UtilTests.PRECISION);
        Assert.assertEquals(line.getY2(), lineCopy.getY2(), UtilTests.PRECISION);
    }

    /**
     * Test geom create point.
     */
    @Test
    public void testGeomCreatePoint()
    {
        Assert.assertNotNull(Geom.createPoint());

        final Point point = Geom.createPoint(1, 2);
        Assert.assertNotNull(point);

        final Point pointCopy = Geom.createPoint(point);
        Assert.assertEquals(point.getX(), pointCopy.getX());
        Assert.assertEquals(point.getY(), pointCopy.getY());
    }

    /**
     * Test geom create rectangle.
     */
    @Test
    public void testGeomCreateRectangle()
    {
        Assert.assertNotNull(Geom.createRectangle());

        final Rectangle rectangle = Geom.createRectangle(0.0, 1.0, 2.0, 3.0);
        Assert.assertNotNull(rectangle);

        final Rectangle rectangleCopy = Geom.createRectangle(rectangle);
        Assert.assertEquals(rectangle.getMinX(), rectangleCopy.getMinX(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getMaxX(), rectangleCopy.getMaxX(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getMinY(), rectangleCopy.getMinY(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getMaxY(), rectangleCopy.getMaxY(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getWidthReal(), rectangleCopy.getWidthReal(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getHeightReal(), rectangleCopy.getHeightReal(), UtilTests.PRECISION);

        rectangleCopy.translate(2.0, 3.0);
        Assert.assertEquals(rectangle.getX() + 2.0, rectangleCopy.getX(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getY() + 3.0, rectangleCopy.getY(), UtilTests.PRECISION);
    }

    /**
     * Test geom create polygon.
     */
    @Test
    public void testGeomCreatePolygon()
    {
        Assert.assertNotNull(Geom.createPolygon());
    }

    /**
     * Test geom with null point.
     */
    @Test(expected = LionEngineException.class)
    public void testGeomNullPoint()
    {
        Assert.assertNull(Geom.createPoint(null));
    }

    /**
     * Test geom with null coord.
     */
    @Test(expected = LionEngineException.class)
    public void testGeomNullCoord()
    {
        Assert.assertNull(Geom.createCoord(null));
    }

    /**
     * Test geom with null line.
     */
    @Test(expected = LionEngineException.class)
    public void testGeomNullLine()
    {
        Assert.assertNull(Geom.createLine(null));
    }

    /**
     * Test geom with null rectangle.
     */
    @Test(expected = LionEngineException.class)
    public void testGeomNullRectangle()
    {
        Assert.assertNull(Geom.createRectangle(null));
    }
}
