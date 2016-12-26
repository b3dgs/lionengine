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
    public void testGeomClass() throws Exception
    {
        UtilTests.testPrivateConstructor(Geom.class);
    }

    /**
     * Test the coord intersection function.
     */
    @Test
    public void testCoordIntersection()
    {
        final Coord coordZero = Geom.intersection(new Line(), new Line());
        Assert.assertEquals(new Coord(0.0, 0.0), coordZero);

        final Line line1 = new Line(1, 2, 3, 4);
        final Line line2 = new Line(-1, 2, -3, 4);
        final Coord coord = Geom.intersection(line1, line2);
        Assert.assertNotNull(coord);
    }

    /**
     * Test geom create localizable.
     */
    @Test
    public void testGeomCreateLocalizable()
    {
        final Localizable localizable = Geom.createLocalizable(1.0, 2.0);

        Assert.assertEquals(1.0, localizable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, localizable.getY(), UtilTests.PRECISION);
    }
}
