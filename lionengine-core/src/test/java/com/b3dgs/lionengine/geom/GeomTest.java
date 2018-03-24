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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link Geom}.
 */
public final class GeomTest
{
    /**
     * Test geom class.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testClass() throws Exception
    {
        UtilTests.testPrivateConstructor(Geom.class);
    }

    /**
     * Test intersection function without contact.
     */
    @Test
    public void testNoIntersection()
    {
        Assert.assertFalse(Geom.intersection(new Line(), new Line()).isPresent());
        Assert.assertFalse(Geom.intersection(new Line(0.0, 0.0, 2.0, 2.0), new Line(2.0, 2.0, 4.0, 4.0)).isPresent());
        Assert.assertFalse(Geom.intersection(new Line(1.0, 2.0, 3.0, 4.0), new Line(-1.0, -2.0, -3.0, -4.0))
                               .isPresent());
        Assert.assertFalse(Geom.intersection(new Line(0.0, 2.0, 4.0, 2.0), new Line(0.0, 1.99, 4.0, 1.99)).isPresent());
        Assert.assertFalse(Geom.intersection(new Line(0.0, 2.0, 4.0, 2.0), new Line(0.0, 2.01, 4.0, 2.01)).isPresent());
    }

    /**
     * Test intersection function.
     */
    @Test
    public void testIntersection()
    {
        Assert.assertEquals(new Coord(1.0, 2.0),
                            Geom.intersection(new Line(0.0, 2.0, 2.0, 2.0), new Line(1.0, 0.0, 1.0, 4.0)).get());
        Assert.assertEquals(new Coord(1.0, 2.0),
                            Geom.intersection(new Line(0.0, 4.0, 2.0, 0.0), new Line(0.0, 0.0, 2.0, 4.0)).get());
    }

    /**
     * Test intersection function with null argument 1.
     */
    @Test(expected = LionEngineException.class)
    public void testIntersectionNullArgument1()
    {
        Assert.assertFalse(Geom.intersection(null, new Line()).isPresent());
    }

    /**
     * Test intersection function with null argument 2.
     */
    @Test(expected = LionEngineException.class)
    public void testIntersectionNullArgument2()
    {
        Assert.assertFalse(Geom.intersection(new Line(), null).isPresent());
    }

    /**
     * Test create localizable.
     */
    @Test
    public void testCreateLocalizable()
    {
        final Localizable localizable = Geom.createLocalizable(1.5, 2.5);

        Assert.assertEquals(1.5, localizable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.5, localizable.getY(), UtilTests.PRECISION);
    }

    /**
     * Test if localizable are same.
     */
    @Test
    public void testSameLocalizable()
    {
        final Localizable localizable = Geom.createLocalizable(1.5, 2.5);

        Assert.assertTrue(Geom.same(localizable, localizable));
        Assert.assertTrue(Geom.same(localizable, Geom.createLocalizable(1.5, 2.5)));
        Assert.assertTrue(Geom.same(Geom.createLocalizable(1.5, 2.5), localizable));
        Assert.assertTrue(Geom.same(Geom.createLocalizable(1.5, 2.5), Geom.createLocalizable(1.5, 2.5)));

        final Coord coord = new Coord(1.5, 2.5);

        Assert.assertTrue(Geom.same(coord, coord));
        Assert.assertTrue(Geom.same(coord, localizable));
        Assert.assertTrue(Geom.same(coord, new Coord(1.5, 2.5)));
        Assert.assertTrue(Geom.same(new Coord(1.5, 2.5), coord));
        Assert.assertTrue(Geom.same(coord, Geom.createLocalizable(1.5, 2.5)));
    }

    /**
     * Test localizable equals.
     */
    @Test
    public void testEqualsLocalizable()
    {
        final Localizable localizable = Geom.createLocalizable(1.5, 2.5);

        Assert.assertNotEquals(localizable, null);
        Assert.assertNotEquals(localizable, new Object());
        Assert.assertNotEquals(localizable, Geom.createLocalizable(2.5, 2.5));
        Assert.assertNotEquals(localizable, Geom.createLocalizable(1.5, 1.5));

        Assert.assertEquals(localizable, localizable);
        Assert.assertEquals(localizable, Geom.createLocalizable(1.5, 2.5));
        Assert.assertEquals(Geom.createLocalizable(1.5, 2.5), localizable);
        Assert.assertEquals(Geom.createLocalizable(1.5, 2.5), Geom.createLocalizable(1.5, 2.5));
    }

    /**
     * Test localizable hash code.
     */
    @Test
    public void testHashCodeLocalizable()
    {
        final int localizable = Geom.createLocalizable(1.5, 2.5).hashCode();

        Assert.assertNotEquals(localizable, new Object().hashCode());
        Assert.assertNotEquals(localizable, Geom.createLocalizable(2.5, 2.5).hashCode());
        Assert.assertNotEquals(localizable, Geom.createLocalizable(1.5, 1.5).hashCode());

        Assert.assertEquals(localizable, Geom.createLocalizable(1.5, 2.5).hashCode());
    }

    /**
     * Test localizable to string.
     */
    @Test
    public void testToStringLocalizable()
    {
        Assert.assertEquals("LocalizableImpl [x=1.5, y=2.5]", Geom.createLocalizable(1.5, 2.5).toString());
    }

    /**
     * Test if localizable are not same.
     */
    @Test
    public void testNotSameLocalizable()
    {
        final Localizable localizable = Geom.createLocalizable(2.5, 3.5);

        Assert.assertFalse(Geom.same(localizable, Geom.createLocalizable(1.5, 2.5)));
        Assert.assertFalse(Geom.same(Geom.createLocalizable(1.5, 2.5), localizable));
        Assert.assertFalse(Geom.same(Geom.createLocalizable(1.5, 2.5), Geom.createLocalizable(2.5, 3.5)));
        Assert.assertFalse(Geom.same(Geom.createLocalizable(1.5, 1.5), Geom.createLocalizable(1.5, 2.5)));
        Assert.assertFalse(Geom.same(Geom.createLocalizable(3.5, 2.5), Geom.createLocalizable(1.5, 2.5)));

        final Coord coord = new Coord(2.5, 3.5);

        Assert.assertFalse(Geom.same(coord, new Coord(1.5, 2.5)));
        Assert.assertFalse(Geom.same(new Coord(1.5, 2.5), coord));
        Assert.assertFalse(Geom.same(coord, Geom.createLocalizable(1.5, 2.5)));
    }
}
