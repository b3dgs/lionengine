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
package com.b3dgs.lionengine.geom;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link Polygon}.
 */
public final class PolygonTest
{
    /**
     * Test constructor default.
     */
    @Test
    public void testConstructorDefault()
    {
        final Polygon polygon = new Polygon();

        Assert.assertTrue(polygon.getPoints().isEmpty());
        Assert.assertFalse(polygon.getRectangle().isPresent());
    }

    /**
     * Test add point.
     */
    @Test
    public void testAddPoint()
    {
        final Polygon polygon = new Polygon();

        Assert.assertTrue(polygon.getPoints().isEmpty());

        polygon.addPoint(1.0, 2.0);

        Assert.assertTrue(polygon.getPoints().isEmpty());
        Assert.assertFalse(polygon.getRectangle().isPresent());

        polygon.addPoint(3.0, 4.0);

        Assert.assertEquals(new Line(1.0, 2.0, 3.0, 4.0), polygon.getPoints().iterator().next());
        Assert.assertFalse(polygon.getRectangle().isPresent());

        polygon.addPoint(-1.0, -2.0);

        Assert.assertEquals(new Line(1.0, 2.0, 3.0, 4.0), polygon.getPoints().iterator().next());
        Assert.assertFalse(polygon.getRectangle().isPresent());

        polygon.addPoint(-3.0, -4.0);

        Assert.assertEquals(new Rectangle(-3.0, -4.0, 6.0, 8.0), polygon.getRectangle().get());
    }

    /**
     * Test add point with required an internal array resize.
     */
    @Test
    public void testAddPointResize()
    {
        final Polygon polygon = new Polygon();

        Assert.assertFalse(polygon.getRectangle().isPresent());

        polygon.addPoint(0.0, 0.0);

        Assert.assertFalse(polygon.getRectangle().isPresent());

        polygon.addPoint(-1.0, -2.0);
        polygon.addPoint(-3.0, -4.0);
        polygon.addPoint(1.0, 2.0);
        polygon.addPoint(3.0, 4.0);

        Assert.assertTrue(polygon.getRectangle().isPresent());
        Assert.assertEquals(2, polygon.getPoints().size());
    }

    /**
     * Test update bound when adding point.
     */
    @Test
    public void testAddPointUpdateBounds()
    {
        final Polygon polygon = new Polygon();
        polygon.addPoint(-1.0, -2.0);
        polygon.addPoint(-3.0, -4.0);
        polygon.addPoint(1.0, 2.0);
        polygon.addPoint(3.0, 4.0);

        Assert.assertTrue(polygon.getRectangle().isPresent());

        polygon.addPoint(0.0, 0.0);

        Assert.assertEquals(new Rectangle(-3.0, -4.0, 6.0, 8.0), polygon.getRectangle().get());

        polygon.addPoint(-4.0, -5.0);

        Assert.assertEquals(new Rectangle(-4.0, -5.0, 7.0, 9.0), polygon.getRectangle().get());
    }

    /**
     * Test reset.
     */
    @Test
    public void testReset()
    {
        final Polygon polygon = new Polygon();
        polygon.addPoint(1.0, 2.0);
        polygon.addPoint(3.0, 4.0);

        Assert.assertFalse(polygon.getPoints().isEmpty());
        Assert.assertFalse(polygon.getRectangle().isPresent());

        polygon.reset();

        Assert.assertTrue(polygon.getPoints().isEmpty());
        Assert.assertFalse(polygon.getRectangle().isPresent());
    }

    /**
     * Test intersect.
     */
    @Test
    public void testIntersect()
    {
        final Polygon polygon = new Polygon();

        Assert.assertFalse(polygon.intersects(null));
        Assert.assertFalse(polygon.intersects(new Rectangle(1.0, 2.0, 2.0, 2.0)));

        polygon.addPoint(-1.0, -2.0);
        polygon.addPoint(-3.0, -4.0);
        polygon.addPoint(1.0, 2.0);
        polygon.addPoint(3.0, 4.0);

        Assert.assertFalse(polygon.intersects(null));
        Assert.assertFalse(polygon.getRectangle().get().toString(),
                           polygon.intersects(new Rectangle(-10.5, -10.5, 1.0, 1.0)));
        Assert.assertTrue(polygon.getRectangle().get().toString(),
                          polygon.intersects(new Rectangle(1.5, 1.5, 1.0, 1.0)));
    }

    /**
     * Test contains.
     */
    @Test
    public void testContains()
    {
        final Polygon polygon = new Polygon();

        Assert.assertFalse(polygon.contains(null));
        Assert.assertFalse(polygon.contains(new Rectangle(1.0, 2.0, 2.0, 2.0)));

        polygon.addPoint(-1.0, -2.0);
        polygon.addPoint(-3.0, -4.0);
        polygon.addPoint(1.0, 2.0);
        polygon.addPoint(3.0, 4.0);

        Assert.assertFalse(polygon.contains(null));
        Assert.assertFalse(polygon.getRectangle().get().toString(),
                           polygon.contains(new Rectangle(10.25, 20.25, 0.5, 0.5)));
        Assert.assertTrue(polygon.getRectangle().get().toString(),
                          polygon.contains(new Rectangle(1.25, 2.25, 0.5, 0.5)));
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        final Polygon polygon = new Polygon();

        Assert.assertEquals("Polygon []", polygon.toString());

        polygon.addPoint(1.0, 2.0);

        Assert.assertEquals("Polygon [ p=1 x=1.0, y=2.0]", polygon.toString());

        polygon.addPoint(3.0, 4.0);

        Assert.assertEquals("Polygon [ p=1 x=1.0, y=2.0 p=2 x=3.0, y=4.0]", polygon.toString());
    }
}
