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
package com.b3dgs.lionengine.game;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the force class.
 */
public class ForceTest
{
    /**
     * Test the force from vector.
     */
    @Test
    public void testFromVector()
    {
        final Force force1 = Force.fromVector(0.0, 1.0, 3.0, 5.0);

        Assert.assertEquals(0.75, force1.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, force1.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(4.0, force1.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force1.getSensibility(), UtilTests.PRECISION);

        final Force force2 = Force.fromVector(0.0, 1.0, -1.0, -2.0);

        Assert.assertEquals(-1.0, force2.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(-3.0, force2.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, force2.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force2.getSensibility(), UtilTests.PRECISION);
    }

    /**
     * Test the force default.
     */
    @Test
    public void testDefault()
    {
        final Force force = new Force();

        Assert.assertEquals(0.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getSensibility(), UtilTests.PRECISION);
    }

    /**
     * Test the force getters.
     */
    @Test
    public void testGetters()
    {
        final Force force = new Force(1.0, 2.0);

        Assert.assertEquals(1.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, force.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getSensibility(), UtilTests.PRECISION);

        force.setVelocity(1.0);
        force.setSensibility(2.0);

        Assert.assertEquals(1.0, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, force.getSensibility(), UtilTests.PRECISION);
    }

    /**
     * Test the add direction.
     */
    @Test
    public void testAddDirection()
    {
        final Force force = new Force(1.0, 2.0);
        force.addDirection(1.0, -1, -2.0);
        force.addDirection(1.0, new Force(2.0, 1.0));

        Assert.assertEquals(2.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, force.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getSensibility(), UtilTests.PRECISION);
    }

    /**
     * Test the add direction with extrapolation.
     */
    @Test
    public void testAddDirectionExtrapolation()
    {
        final Force force = new Force(0.0, 0.0);
        force.addDirection(0.5, 1, 2.0);

        Assert.assertEquals(0.5, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, force.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getSensibility(), UtilTests.PRECISION);
    }

    /**
     * Test the set direction.
     */
    @Test
    public void testSetDirection()
    {
        final Force force = new Force(0.0, 0.0);

        force.setDirection(1.0, 2.0);

        Assert.assertEquals(1.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, force.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getSensibility(), UtilTests.PRECISION);

        force.setDirection(new Force(-1.0, -2.0));

        Assert.assertEquals(-1.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(-2.0, force.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getSensibility(), UtilTests.PRECISION);
    }

    /**
     * Test the set destination.
     */
    @Test
    public void testSetDestination()
    {
        final Force force = new Force(1.0, 2.0);
        force.setVelocity(1.0);
        force.setDestination(2.0, 3.0);

        Assert.assertEquals(1.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, force.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getSensibility(), UtilTests.PRECISION);
        Assert.assertTrue(force.isIncreasingHorizontal());
        Assert.assertFalse(force.isDecreasingHorizontal());

        force.update(1.0);

        Assert.assertEquals(2.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, force.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getSensibility(), UtilTests.PRECISION);
        Assert.assertTrue(force.isIncreasingHorizontal());
        Assert.assertFalse(force.isDecreasingHorizontal());

        force.setDestination(-1.0, -2.0);

        Assert.assertEquals(2.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertTrue(force.isIncreasingHorizontal());
        Assert.assertFalse(force.isDecreasingHorizontal());

        force.update(1.0);

        Assert.assertEquals(1.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, force.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertFalse(force.isIncreasingHorizontal());
        Assert.assertTrue(force.isDecreasingHorizontal());

        force.update(1.0);

        Assert.assertEquals(0.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, force.getDirectionVertical(), UtilTests.PRECISION);

        force.update(1.0);

        Assert.assertEquals(-1.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, force.getDirectionVertical(), UtilTests.PRECISION);

        force.update(1.0);

        Assert.assertEquals(-1.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(-1.0, force.getDirectionVertical(), UtilTests.PRECISION);

        force.update(1.0);

        Assert.assertEquals(-1.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(-2.0, force.getDirectionVertical(), UtilTests.PRECISION);

        force.update(1.0);

        Assert.assertEquals(-1.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(-2.0, force.getDirectionVertical(), UtilTests.PRECISION);
    }

    /**
     * Test the sensibility.
     */
    @Test
    public void testSensibility()
    {
        final Force force = new Force(1.0, 1.0, 0.2, 0.3);
        force.setDestination(2.0, 2.0);

        force.update(1.0);

        Assert.assertEquals(1.2, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.2, force.getDirectionVertical(), UtilTests.PRECISION);

        force.update(1.0);

        Assert.assertEquals(1.4, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.4, force.getDirectionVertical(), UtilTests.PRECISION);

        force.update(1.0);

        Assert.assertEquals(1.6, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.6, force.getDirectionVertical(), UtilTests.PRECISION);

        force.update(1.0);

        Assert.assertEquals(2.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, force.getDirectionVertical(), UtilTests.PRECISION);

        force.setDestination(1.5, 1.5);

        force.update(1.0);

        Assert.assertEquals(1.8, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.8, force.getDirectionVertical(), UtilTests.PRECISION);

        force.update(1.0);

        Assert.assertEquals(1.5, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.5, force.getDirectionVertical(), UtilTests.PRECISION);

        force.update(1.0);

        Assert.assertEquals(1.5, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(1.5, force.getDirectionVertical(), UtilTests.PRECISION);
    }

    /**
     * Test the set direction minimum.
     */
    @Test
    public void testSetDirectionMinnimum()
    {
        final Force force = new Force(1.0, 2.0);
        force.setDirectionMinimum(new Force(3.0, 4.0));
        force.update(1.0);

        Assert.assertEquals(3.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(4.0, force.getDirectionVertical(), UtilTests.PRECISION);
    }

    /**
     * Test the set direction maximum.
     */
    @Test
    public void testSetDirectionMaximum()
    {
        final Force force = new Force(3.0, 4.0);
        force.setDirectionMaximum(new Force(1.0, 2.0));
        force.update(1.0);

        Assert.assertEquals(1.0, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, force.getDirectionVertical(), UtilTests.PRECISION);
    }

    /**
     * Test the force copy.
     */
    @Test
    public void testCopy()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        Assert.assertEquals(force, new Force(force));
    }

    /**
     * Test the force hash code.
     */
    @Test
    public void testHashcode()
    {
        final int hash = new Force(1.0, 2.0, 3.0, 4.0).hashCode();

        Assert.assertEquals(hash, new Force(1.0, 2.0, 3.0, 4.0).hashCode());

        Assert.assertNotEquals(hash, new Object().hashCode());
        Assert.assertNotEquals(hash, new Force(1.0, 2.0, 3.0, 1.0).hashCode());
        Assert.assertNotEquals(hash, new Force(1.0, 2.0, 1.0, 4.0).hashCode());
        Assert.assertNotEquals(hash, new Force(1.0, 2.0, 1.0, 1.0).hashCode());
        Assert.assertNotEquals(hash, new Force(1.0, 1.0, 3.0, 4.0).hashCode());
        Assert.assertNotEquals(hash, new Force(1.0, 1.0, 3.0, 1.0).hashCode());
        Assert.assertNotEquals(hash, new Force(1.0, 1.0, 1.0, 4.0).hashCode());
        Assert.assertNotEquals(hash, new Force(2.0, 2.0, 3.0, 4.0).hashCode());
        Assert.assertNotEquals(hash, new Force(2.0, 2.0, 3.0, 1.0).hashCode());
        Assert.assertNotEquals(hash, new Force(2.0, 2.0, 1.0, 4.0).hashCode());
        Assert.assertNotEquals(hash, new Force(2.0, 2.0, 1.0, 1.0).hashCode());
        Assert.assertNotEquals(hash, new Force(2.0, 1.0, 3.0, 4.0).hashCode());
        Assert.assertNotEquals(hash, new Force(2.0, 1.0, 3.0, 1.0).hashCode());
        Assert.assertNotEquals(hash, new Force(2.0, 1.0, 1.0, 4.0).hashCode());
    }

    /**
     * Test the force equality.
     */
    @Test
    public void testEquals()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        Assert.assertEquals(force, force);
        Assert.assertEquals(force, new Force(1.0, 2.0, 3.0, 4.0));

        Assert.assertNotEquals(force, null);
        Assert.assertNotEquals(force, new Object());
        Assert.assertNotEquals(force, new Force(1.0, 2.0, 3.0, 1.0));
        Assert.assertNotEquals(force, new Force(1.0, 2.0, 1.0, 4.0));
        Assert.assertNotEquals(force, new Force(1.0, 2.0, 1.0, 1.0));
        Assert.assertNotEquals(force, new Force(1.0, 1.0, 3.0, 4.0));
        Assert.assertNotEquals(force, new Force(1.0, 1.0, 3.0, 1.0));
        Assert.assertNotEquals(force, new Force(1.0, 1.0, 1.0, 4.0));
        Assert.assertNotEquals(force, new Force(2.0, 2.0, 3.0, 4.0));
        Assert.assertNotEquals(force, new Force(2.0, 2.0, 3.0, 1.0));
        Assert.assertNotEquals(force, new Force(2.0, 2.0, 1.0, 4.0));
        Assert.assertNotEquals(force, new Force(2.0, 2.0, 1.0, 1.0));
        Assert.assertNotEquals(force, new Force(2.0, 1.0, 3.0, 4.0));
        Assert.assertNotEquals(force, new Force(2.0, 1.0, 3.0, 1.0));
        Assert.assertNotEquals(force, new Force(2.0, 1.0, 1.0, 4.0));
    }

    /**
     * Test the force to string.
     */
    @Test
    public void testToString()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        Assert.assertEquals("Force [fh=1.0, fv=2.0, velocity=3.0, sensibility=4.0]", force.toString());
    }
}
