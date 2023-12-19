/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.geom;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Localizable;

/**
 * Test {@link Geom}.
 */
final class GeomTest
{
    /**
     * Test geom class.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(Geom.class);
    }

    /**
     * Test intersection function without contact.
     */
    @Test
    void testNoIntersection()
    {
        assertFalse(Geom.intersection(new Line(), new Line()).isPresent());
        assertFalse(Geom.intersection(new Line(0.0, 0.0, 2.0, 2.0), new Line(2.0, 2.0, 4.0, 4.0)).isPresent());
        assertFalse(Geom.intersection(new Line(1.0, 2.0, 3.0, 4.0), new Line(-1.0, -2.0, -3.0, -4.0)).isPresent());
        assertFalse(Geom.intersection(new Line(0.0, 2.0, 4.0, 2.0), new Line(0.0, 1.99, 4.0, 1.99)).isPresent());
        assertFalse(Geom.intersection(new Line(0.0, 2.0, 4.0, 2.0), new Line(0.0, 2.01, 4.0, 2.01)).isPresent());
    }

    /**
     * Test intersection function.
     */
    @Test
    void testIntersection()
    {
        assertEquals(new Coord(1.0, 2.0),
                     Geom.intersection(new Line(0.0, 2.0, 2.0, 2.0), new Line(1.0, 0.0, 1.0, 4.0)).get());
        assertEquals(new Coord(1.0, 2.0),
                     Geom.intersection(new Line(0.0, 4.0, 2.0, 0.0), new Line(0.0, 0.0, 2.0, 4.0)).get());
    }

    /**
     * Test intersection function with null argument 1.
     */
    @Test
    void testIntersectionNullArgument1()
    {
        assertThrows(() -> Geom.intersection(null, new Line()).isPresent(), "Unexpected null argument !");
    }

    /**
     * Test intersection function with null argument 2.
     */
    @Test
    void testIntersectionNullArgument2()
    {
        assertThrows(() -> Geom.intersection(new Line(), null).isPresent(), "Unexpected null argument !");
    }

    /**
     * Test create localizable.
     */
    @Test
    void testCreateLocalizable()
    {
        final Localizable localizable = Geom.createLocalizable(1.5, 2.5);

        assertEquals(1.5, localizable.getX());
        assertEquals(2.5, localizable.getY());
    }

    /**
     * Test if localizable are same.
     */
    @Test
    void testSameLocalizable()
    {
        final Localizable localizable = Geom.createLocalizable(1.5, 2.5);

        assertTrue(Geom.same(localizable, localizable));
        assertTrue(Geom.same(localizable, Geom.createLocalizable(1.5, 2.5)));
        assertTrue(Geom.same(Geom.createLocalizable(1.5, 2.5), localizable));
        assertTrue(Geom.same(Geom.createLocalizable(1.5, 2.5), Geom.createLocalizable(1.5, 2.5)));

        final Coord coord = new Coord(1.5, 2.5);

        assertTrue(Geom.same(coord, coord));
        assertTrue(Geom.same(coord, localizable));
        assertTrue(Geom.same(coord, new Coord(1.5, 2.5)));
        assertTrue(Geom.same(new Coord(1.5, 2.5), coord));
        assertTrue(Geom.same(coord, Geom.createLocalizable(1.5, 2.5)));
    }

    /**
     * Test if localizable are not same.
     */
    @Test
    void testNotSameLocalizable()
    {
        final Localizable localizable = Geom.createLocalizable(2.5, 3.5);

        assertFalse(Geom.same(localizable, Geom.createLocalizable(1.5, 2.5)));
        assertFalse(Geom.same(Geom.createLocalizable(1.5, 2.5), localizable));
        assertFalse(Geom.same(Geom.createLocalizable(1.5, 2.5), Geom.createLocalizable(2.5, 3.5)));
        assertFalse(Geom.same(Geom.createLocalizable(1.5, 1.5), Geom.createLocalizable(1.5, 2.5)));
        assertFalse(Geom.same(Geom.createLocalizable(3.5, 2.5), Geom.createLocalizable(1.5, 2.5)));

        final Coord coord = new Coord(2.5, 3.5);

        assertFalse(Geom.same(coord, new Coord(1.5, 2.5)));
        assertFalse(Geom.same(new Coord(1.5, 2.5), coord));
        assertFalse(Geom.same(coord, Geom.createLocalizable(1.5, 2.5)));
    }

    /**
     * Test localizable equals.
     */
    @Test
    void testEqualsLocalizable()
    {
        final Localizable localizable = Geom.createLocalizable(1.5, 2.5);

        assertNotEquals(localizable, null);
        assertNotEquals(localizable, new Object());
        assertNotEquals(localizable, Geom.createLocalizable(2.5, 2.5));
        assertNotEquals(localizable, Geom.createLocalizable(1.5, 1.5));

        assertEquals(localizable, localizable);
        assertEquals(localizable, Geom.createLocalizable(1.5, 2.5));
        assertEquals(Geom.createLocalizable(1.5, 2.5), localizable);
        assertEquals(Geom.createLocalizable(1.5, 2.5), Geom.createLocalizable(1.5, 2.5));
    }

    /**
     * Test localizable hash code.
     */
    @Test
    void testHashCodeLocalizable()
    {
        final Localizable localizable = Geom.createLocalizable(1.5, 2.5);

        assertHashNotEquals(localizable, new Object());
        assertHashNotEquals(localizable, Geom.createLocalizable(2.5, 2.5));
        assertHashNotEquals(localizable, Geom.createLocalizable(1.5, 1.5));

        assertHashEquals(localizable, Geom.createLocalizable(1.5, 2.5));
    }

    /**
     * Test localizable to string.
     */
    @Test
    void testToStringLocalizable()
    {
        assertEquals("LocalizableImpl[x=1.5, y=2.5]", Geom.createLocalizable(1.5, 2.5).toString());
    }

    /**
     * Test create area.
     */
    @Test
    void testCreateArea()
    {
        final Area area = Geom.createArea(1.5, 2.5, 3.5, 4.5);

        assertEquals(1.5, area.getX());
        assertEquals(2.5, area.getY());
        assertEquals(3.5, area.getWidthReal());
        assertEquals(4.5, area.getHeightReal());
        assertEquals(3, area.getWidth());
        assertEquals(4, area.getHeight());
    }

    /**
     * Test area intersects.
     */
    @Test
    void testAreaIntersects()
    {
        final Area area1 = Geom.createArea(0.0, 0.0, 10.0, 10.0);
        final Area area2 = Geom.createArea(1.0, 1.0, 5.0, 5.0);
        final Area area3 = Geom.createArea(4.0, 1.0, 5.0, 5.0);
        final Area area4 = Geom.createArea(1.0, 4.0, 5.0, 5.0);
        final Area area5 = Geom.createArea(6.0, 6.0, 5.0, 5.0);

        assertFalse(area1.intersects(null));

        assertTrue(area2.intersects(area1));
        assertTrue(area2.intersects(area3));
        assertTrue(area2.intersects(area4));
        assertFalse(area2.intersects(area5));

        assertTrue(area3.intersects(area1));
        assertTrue(area3.intersects(area2));
        assertTrue(area3.intersects(area4));
        assertFalse(area3.intersects(area5));

        assertTrue(area5.intersects(area1));
        assertFalse(area5.intersects(area2));
        assertFalse(area5.intersects(area3));
        assertFalse(area5.intersects(area4));
    }

    /**
     * Test area contains.
     */
    @Test
    void testAreaContains()
    {
        final Area area1 = Geom.createArea(0.0, 0.0, 10.0, 10.0);
        final Area area2 = Geom.createArea(1.0, 1.0, 5.0, 5.0);
        final Area area3 = Geom.createArea(4.0, 1.0, 5.0, 5.0);
        final Area area4 = Geom.createArea(1.0, 4.0, 5.0, 5.0);
        final Area area5 = Geom.createArea(6.0, 6.0, 5.0, 5.0);

        assertFalse(area1.contains(null));

        assertTrue(area1.contains(area2));

        assertFalse(area2.contains(area1));
        assertFalse(area2.contains(area3));
        assertFalse(area2.contains(area4));
        assertFalse(area2.contains(area5));

        assertFalse(area4.contains(area3));
        assertFalse(area4.contains(area2));
        assertTrue(area4.contains(area4));
        assertFalse(area4.contains(area5));

        assertTrue(area1.contains(2, 3));

        assertFalse(area1.contains(-2, -3));
        assertFalse(area1.contains(-1, 11));
        assertFalse(area1.contains(0, 11));
        assertFalse(area1.contains(-1, 10));
        assertFalse(area1.contains(11, 12));
        assertFalse(area1.contains(11, -3));
    }

    /**
     * Test area equals.
     */
    @Test
    void testEqualsArea()
    {
        final Area area = Geom.createArea(1.5, 2.5, 3.5, 4.5);

        assertEquals(area, area);
        assertEquals(area, Geom.createArea(1.5, 2.5, 3.5, 4.5));
        assertEquals(Geom.createArea(1.5, 2.5, 3.5, 4.5), area);
        assertEquals(Geom.createArea(1.5, 2.5, 3.5, 4.5), Geom.createArea(1.5, 2.5, 3.5, 4.5));

        assertNotEquals(area, null);
        assertNotEquals(area, new Object());
        assertNotEquals(area, Geom.createArea(2.5, 2.5, 3.5, 4.5));
        assertNotEquals(area, Geom.createArea(1.5, 1.5, 3.5, 4.5));
        assertNotEquals(area, Geom.createArea(1.5, 2.5, 4.5, 4.5));
        assertNotEquals(area, Geom.createArea(1.5, 2.5, 3.5, 3.5));
        assertNotEquals(area, Geom.createArea(2.5, 1.5, 4.5, 3.5));
    }

    /**
     * Test area hash code.
     */
    @Test
    void testHashCodeArea()
    {
        final Area area = Geom.createArea(1.5, 2.5, 3.5, 4.5);

        assertHashEquals(area, Geom.createArea(1.5, 2.5, 3.5, 4.5));

        assertHashNotEquals(area, new Object());
        assertHashNotEquals(area, Geom.createArea(2.5, 2.5, 3.5, 4.5));
        assertHashNotEquals(area, Geom.createArea(1.5, 1.5, 3.5, 4.5));
        assertHashNotEquals(area, Geom.createArea(1.5, 2.5, 4.5, 4.5));
        assertHashNotEquals(area, Geom.createArea(1.5, 2.5, 3.5, 3.5));
        assertHashNotEquals(area, Geom.createArea(2.5, 1.5, 4.5, 3.5));
    }

    /**
     * Test area to string.
     */
    @Test
    void testToStringArea()
    {
        assertEquals("AreaImpl[x=1.5, y=2.5, width=3.5, height=4.5]", Geom.createArea(1.5, 2.5, 3.5, 4.5).toString());
    }
}
