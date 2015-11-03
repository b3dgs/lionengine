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
package com.b3dgs.lionengine.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Transform;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.test.mock.FactoryGraphicMock;

/**
 * Test the transform class.
 */
public class TransformTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test the transformations.
     */
    @Test
    public void testTransform()
    {
        final Transform transform = Graphics.createTransform();
        transform.scale(2.0, 3.0);
        transform.setInterpolation(true);

        Assert.assertEquals(2.0, transform.getScaleX(), 0.1);
        Assert.assertEquals(3.0, transform.getScaleY(), 0.1);
        Assert.assertEquals(getInterpolation(transform.getInterpolation()), transform.getInterpolation());

        transform.setInterpolation(false);
        Assert.assertEquals(getInterpolation(transform.getInterpolation()), transform.getInterpolation());
    }

    /**
     * Get the interpolation equivalence.
     * 
     * @param value The engine side value.
     * @return The target side value.
     */
    protected int getInterpolation(int value)
    {
        return value;
    }
}
