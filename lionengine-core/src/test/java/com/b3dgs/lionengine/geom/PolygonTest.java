/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

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

        assertTrue(polygon.getPoints().isEmpty());
        assertEquals(new Rectangle(0.0, 0.0, 0.0, 0.0), polygon.getArea());
    }

    /**
     * Test add point.
     */
    @Test
    public void testAddPoint()
    {
        final Polygon polygon = new Polygon();

        assertTrue(polygon.getPoints().isEmpty());

        polygon.addPoint(1.0, 2.0);

        assertTrue(polygon.getPoints().isEmpty());
        assertEquals(new Rectangle(0.0, 0.0, 0.0, 0.0), polygon.getArea());

        polygon.addPoint(3.0, 4.0);

        assertEquals(new Line(1.0, 2.0, 3.0, 4.0), polygon.getPoints().iterator().next());
        assertEquals(new Rectangle(0.0, 0.0, 0.0, 0.0), polygon.getArea());

        polygon.addPoint(-1.0, -2.0);

        assertEquals(new Line(1.0, 2.0, 3.0, 4.0), polygon.getPoints().iterator().next());
        assertEquals(new Rectangle(0.0, 0.0, 0.0, 0.0), polygon.getArea());

        polygon.addPoint(-3.0, -4.0);

        assertEquals(new Rectangle(-3.0, -4.0, 6.0, 8.0), polygon.getArea());
    }

    /**
     * Test add point with required an internal array resize.
     */
    @Test
    public void testAddPointResize()
    {
        final Polygon polygon = new Polygon();

        assertEquals(new Rectangle(0.0, 0.0, 0.0, 0.0), polygon.getArea());

        polygon.addPoint(0.0, 0.0);

        assertEquals(new Rectangle(0.0, 0.0, 0.0, 0.0), polygon.getArea());

        polygon.addPoint(-1.0, -2.0);
        polygon.addPoint(-3.0, -4.0);
        polygon.addPoint(1.0, 2.0);
        polygon.addPoint(3.0, 4.0);

        assertEquals(new Rectangle(-3.0, -4.0, 6.0, 8.0), polygon.getArea());
        assertEquals(2, polygon.getPoints().size());
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

        assertEquals(new Rectangle(-3.0, -4.0, 6.0, 8.0), polygon.getArea());

        polygon.addPoint(0.0, 0.0);

        assertEquals(new Rectangle(-3.0, -4.0, 6.0, 8.0), polygon.getArea());

        polygon.addPoint(-4.0, -5.0);

        assertEquals(new Rectangle(-4.0, -5.0, 7.0, 9.0), polygon.getArea());
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

        assertFalse(polygon.getPoints().isEmpty());
        assertEquals(new Rectangle(0.0, 0.0, 0.0, 0.0), polygon.getArea());

        polygon.reset();

        assertTrue(polygon.getPoints().isEmpty());
        assertEquals(new Rectangle(0.0, 0.0, 0.0, 0.0), polygon.getArea());
    }

    /**
     * Test intersect.
     */
    @Test
    public void testIntersect()
    {
        final Polygon polygon = new Polygon();

        assertFalse(polygon.intersects(null));
        assertFalse(polygon.intersects(Geom.createArea(1.0, 2.0, 2.0, 2.0)));

        polygon.addPoint(-1.0, -2.0);
        polygon.addPoint(-3.0, -4.0);
        polygon.addPoint(1.0, 2.0);
        polygon.addPoint(3.0, 4.0);

        assertFalse(polygon.intersects(null));
        assertFalse(polygon.intersects(Geom.createArea(-10.5, -10.5, 1.0, 1.0)), polygon.getArea().toString());
        assertTrue(polygon.intersects(Geom.createArea(1.5, 1.5, 1.0, 1.0)), polygon.getArea().toString());
    }

    /**
     * Test contains.
     */
    @Test
    public void testContains()
    {
        final Polygon polygon = new Polygon();

        assertFalse(polygon.contains(null));
        assertFalse(polygon.contains(Geom.createArea(1.0, 2.0, 2.0, 2.0)));

        polygon.addPoint(-1.0, -2.0);
        polygon.addPoint(-3.0, -4.0);
        polygon.addPoint(1.0, 2.0);
        polygon.addPoint(3.0, 4.0);

        assertFalse(polygon.contains(null));
        assertFalse(polygon.contains(Geom.createArea(10.25, 20.25, 0.5, 0.5)), polygon.getArea().toString());
        assertTrue(polygon.contains(Geom.createArea(1.25, 2.25, 0.5, 0.5)), polygon.getArea().toString());
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        final Polygon polygon = new Polygon();

        assertEquals("Polygon []", polygon.toString());

        polygon.addPoint(1.0, 2.0);

        assertEquals("Polygon [ p=1 x=1.0, y=2.0]", polygon.toString());

        polygon.addPoint(3.0, 4.0);

        assertEquals("Polygon [ p=1 x=1.0, y=2.0 p=2 x=3.0, y=4.0]", polygon.toString());
    }
}
