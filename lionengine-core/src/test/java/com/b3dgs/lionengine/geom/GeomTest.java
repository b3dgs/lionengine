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
        Assert.assertNotNull(Geom.createCoord(0, 0));
        Assert.assertNotNull(Geom.createLine());
        Assert.assertNotNull(Geom.createLine(0.0, 0.0, 0.0, 0.0));
        Assert.assertNotNull(Geom.createPoint());
        Assert.assertNotNull(Geom.createPoint(0, 0));
        Assert.assertNotNull(Geom.createPolygon());
        Assert.assertNotNull(Geom.createRectangle());
        Assert.assertNotNull(Geom.createRectangle(0.0, 0.0, 0.0, 0.0));
    }
}
