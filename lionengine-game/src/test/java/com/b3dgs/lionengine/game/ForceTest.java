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
package com.b3dgs.lionengine.game;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link Force}.
 */
public final class ForceTest
{
    /**
     * Assert force equals expected values.
     * 
     * @param h The expected horizontal direction.
     * @param v The expected vertical direction.
     * @param velocity The expected velocity.
     * @param sensibility The expected sensibility.
     * @param force The force to check.
     */
    private static void assertForce(double h, double v, double velocity, double sensibility, Force force)
    {
        Assert.assertEquals(h, force.getDirectionHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(v, force.getDirectionVertical(), UtilTests.PRECISION);
        Assert.assertEquals(velocity, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(sensibility, force.getSensibility(), UtilTests.PRECISION);
    }

    /**
     * Test from vector.
     */
    @Test
    public void testFromVector()
    {
        assertForce(0.0, 0.0, 0.0, 0.0, Force.fromVector(0.0, 0.0, 0.0, 0.0));
        assertForce(0.75, 1.0, 4.0, 0.0, Force.fromVector(0.0, 1.0, 3.0, 5.0));
        assertForce(-1.0, -3.0, 1.0, 0.0, Force.fromVector(0.0, 1.0, -1.0, -2.0));
    }

    /**
     * Test constructor default.
     */
    @Test
    public void testConstructorDefault()
    {
        assertForce(0.0, 0.0, 0.0, 0.0, new Force());
    }

    /**
     * Test constructor parameters.
     */
    @Test
    public void testConstructorParameter()
    {
        assertForce(1.0, 2.0, 0.0, 0.0, new Force(1.0, 2.0));
        assertForce(1.0, 2.0, 3.0, 4.0, new Force(1.0, 2.0, 3.0, 4.0));
    }

    /**
     * Test getters.
     */
    @Test
    public void testGetters()
    {
        final Force force = new Force(1.0, 2.0);
        force.setVelocity(1.0);
        force.setSensibility(2.0);

        Assert.assertEquals(1.0, force.getVelocity(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, force.getSensibility(), UtilTests.PRECISION);
    }

    /**
     * Test add direction.
     */
    @Test
    public void testAddDirection()
    {
        final Force force = new Force(1.0, 2.0);
        force.addDirection(1.0, -1, -2.0);
        force.addDirection(1.0, new Force(2.0, 1.0));

        assertForce(2.0, 1.0, 0.0, 0.0, force);
    }

    /**
     * Test add direction with extrapolation.
     */
    @Test
    public void testAddDirectionExtrapolation()
    {
        final Force force = new Force(0.0, 0.0);
        force.addDirection(0.5, 1, 2.0);

        assertForce(0.5, 1.0, 0.0, 0.0, force);
    }

    /**
     * Test set direction.
     */
    @Test
    public void testSetDirection()
    {
        final Force force = new Force(0.0, 0.0);
        force.setDirection(1.0, 2.0);

        assertForce(1.0, 2.0, 0.0, 0.0, force);

        force.setDirection(new Force(-1.0, -2.0));

        assertForce(-1.0, -2.0, 0.0, 0.0, force);
    }

    /**
     * Test set destination.
     */
    @Test
    public void testSetDestination()
    {
        final Force force = new Force(1.0, 2.0);
        force.setVelocity(1.0);
        force.setDestination(2.0, 3.0);

        assertForce(1.0, 2.0, 1.0, 0.0, force);
        Assert.assertTrue(force.isIncreasingHorizontal());
        Assert.assertFalse(force.isDecreasingHorizontal());

        force.update(1.0);

        assertForce(2.0, 3.0, 1.0, 0.0, force);
        Assert.assertTrue(force.isIncreasingHorizontal());
        Assert.assertFalse(force.isDecreasingHorizontal());

        force.setDestination(-1.0, -2.0);

        assertForce(2.0, 3.0, 1.0, 0.0, force);
        Assert.assertTrue(force.isIncreasingHorizontal());
        Assert.assertFalse(force.isDecreasingHorizontal());

        force.update(1.0);

        assertForce(1.0, 2.0, 1.0, 0.0, force);
        Assert.assertFalse(force.isIncreasingHorizontal());
        Assert.assertTrue(force.isDecreasingHorizontal());

        force.update(1.0);

        assertForce(0.0, 1.0, 1.0, 0.0, force);

        force.update(1.0);

        assertForce(-1.0, 0.0, 1.0, 0.0, force);

        force.update(1.0);

        assertForce(-1.0, -1.0, 1.0, 0.0, force);

        force.update(1.0);

        assertForce(-1.0, -2.0, 1.0, 0.0, force);

        force.update(1.0);

        assertForce(-1.0, -2.0, 1.0, 0.0, force);
    }

    /**
     * Test sensibility.
     */
    @Test
    public void testSensibility()
    {
        final Force force = new Force(1.0, 1.0, 0.2, 0.3);
        force.setDestination(2.0, 2.0);
        force.update(1.0);

        assertForce(1.2, 1.2, 0.2, 0.3, force);

        force.update(1.0);

        assertForce(1.4, 1.4, 0.2, 0.3, force);

        force.update(1.0);

        assertForce(1.6, 1.6, 0.2, 0.3, force);

        force.update(1.0);

        assertForce(2.0, 2.0, 0.2, 0.3, force);

        force.setDestination(1.5, 1.5);
        force.update(1.0);

        assertForce(1.8, 1.8, 0.2, 0.3, force);

        force.update(1.0);

        assertForce(1.5, 1.5, 0.2, 0.3, force);

        force.update(1.0);

        assertForce(1.5, 1.5, 0.2, 0.3, force);
    }

    /**
     * Test set direction minimum.
     */
    @Test
    public void testSetDirectionMinimum()
    {
        final Force force = new Force(1.0, 2.0);
        force.setDirectionMinimum(new Force(3.0, 4.0));
        force.update(1.0);

        assertForce(3.0, 4.0, 0.0, 0.0, force);
    }

    /**
     * Test set direction maximum.
     */
    @Test
    public void testSetDirectionMaximum()
    {
        final Force force = new Force(3.0, 4.0);
        force.setDirectionMaximum(new Force(1.0, 2.0));
        force.update(1.0);

        assertForce(1.0, 2.0, 0.0, 0.0, force);
    }

    /**
     * Test copy.
     */
    @Test
    public void testCopy()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        Assert.assertEquals(force, new Force(force));
    }

    /**
     * Test equals.
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
     * Test hash code.
     */
    @Test
    public void testHashCode()
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
     * Test to string.
     */
    @Test
    public void testToString()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        Assert.assertEquals("Force [fh=1.0, fv=2.0, velocity=3.0, sensibility=4.0]", force.toString());
    }
}
