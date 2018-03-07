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
    public void testClass() throws Exception
    {
        UtilTests.testPrivateConstructor(Geom.class);
    }

    /**
     * Test the intersection function without contact.
     */
    @Test
    public void testNoIntersection()
    {
        Assert.assertNull(Geom.intersection(new Line(), new Line()));
        Assert.assertNull(Geom.intersection(new Line(0.0, 0.0, 2.0, 2.0), new Line(2.0, 2.0, 4.0, 4.0)));
        Assert.assertNull(Geom.intersection(new Line(1.0, 2.0, 3.0, 4.0), new Line(-1.0, -2.0, -3.0, -4.0)));
        Assert.assertNull(Geom.intersection(new Line(0.0, 2.0, 4.0, 2.0), new Line(0.0, 1.99, 4.0, 1.99)));
        Assert.assertNull(Geom.intersection(new Line(0.0, 2.0, 4.0, 2.0), new Line(0.0, 2.01, 4.0, 2.01)));
    }

    /**
     * Test the intersection function.
     */
    @Test
    public void testIntersection()
    {
        Assert.assertEquals(new Coord(1.0, 2.0),
                            Geom.intersection(new Line(0.0, 2.0, 2.0, 2.0), new Line(1.0, 0.0, 1.0, 4.0)));
        Assert.assertEquals(new Coord(1.0, 2.0),
                            Geom.intersection(new Line(0.0, 4.0, 2.0, 0.0), new Line(0.0, 0.0, 2.0, 4.0)));
    }

    /**
     * Test the intersection function with null argument 1.
     */
    @Test(expected = LionEngineException.class)
    public void testIntersectionNullArgument1()
    {
        Assert.assertNull(Geom.intersection(null, new Line()));
    }

    /**
     * Test the intersection function with null argument 2.
     */
    @Test(expected = LionEngineException.class)
    public void testIntersectionNullArgument2()
    {
        Assert.assertNull(Geom.intersection(new Line(), null));
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
}
