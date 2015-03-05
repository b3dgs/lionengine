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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the geom class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class GeomTest
{
    /**
     * Test geom class.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test(expected = InvocationTargetException.class)
    public void testGeomClass() throws ReflectiveOperationException
    {
        final Constructor<Geom> constructor = Geom.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Geom geom = constructor.newInstance();
        Assert.assertNotNull(geom);
        Assert.fail();
    }

    /**
     * Test geom.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testGeom() throws Exception
    {
        Assert.assertNotNull(Geom.createCoord());

        final Coord coord = Geom.createCoord(1, 2);
        Assert.assertNotNull(coord);
        final Coord coordCopy = Geom.createCoord(coord);
        Assert.assertEquals(coord.getX(), coordCopy.getX(), 0.001);
        Assert.assertEquals(coord.getY(), coordCopy.getY(), 0.001);

        Assert.assertNotNull(Geom.createLine());

        final Line line = Geom.createLine(0.0, 0.0, 0.0, 0.0);
        Assert.assertNotNull(line);
        final Line lineCopy = Geom.createLine(line);
        Assert.assertEquals(line.getX1(), lineCopy.getX1(), 0.001);
        Assert.assertEquals(line.getY1(), lineCopy.getY1(), 0.001);
        Assert.assertEquals(line.getX2(), lineCopy.getX2(), 0.001);
        Assert.assertEquals(line.getY2(), lineCopy.getY2(), 0.001);

        Assert.assertNotNull(Geom.createPoint());

        final Point point = Geom.createPoint(1, 2);
        Assert.assertNotNull(point);
        final Point pointCopy = Geom.createPoint(point);
        Assert.assertEquals(point.getX(), pointCopy.getX());
        Assert.assertEquals(point.getY(), pointCopy.getY());

        Assert.assertNotNull(Geom.createRectangle());

        final Rectangle rectangle = Geom.createRectangle(0.0, 0.0, 0.0, 0.0);
        Assert.assertNotNull(rectangle);
        final Rectangle rectangleCopy = Geom.createRectangle(rectangle);
        Assert.assertEquals(rectangle.getMinX(), rectangleCopy.getMinX(), 0.001);
        Assert.assertEquals(rectangle.getMaxX(), rectangleCopy.getMaxX(), 0.001);
        Assert.assertEquals(rectangle.getMinY(), rectangleCopy.getMinY(), 0.001);
        Assert.assertEquals(rectangle.getMaxY(), rectangleCopy.getMaxY(), 0.001);
        Assert.assertEquals(rectangle.getWidth(), rectangleCopy.getWidth(), 0.001);
        Assert.assertEquals(rectangle.getHeight(), rectangleCopy.getHeight(), 0.001);

        rectangleCopy.translate(2.0, 3.0);
        Assert.assertEquals(rectangle.getX() + 2.0, rectangleCopy.getX(), 0.001);
        Assert.assertEquals(rectangle.getY() + 3.0, rectangleCopy.getY(), 0.001);

        Assert.assertNotNull(Geom.createPolygon());
    }
}
