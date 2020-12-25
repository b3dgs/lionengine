/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Force}.
 */
final class ForceTest
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
        assertEquals(h, force.getDirectionHorizontal());
        assertEquals(v, force.getDirectionVertical());
        assertEquals(velocity, force.getVelocity());
        assertEquals(sensibility, force.getSensibility());
    }

    /**
     * Test from vector.
     */
    @Test
    void testFromVector()
    {
        assertForce(0.0, 0.0, 0.0, 0.0, Force.fromVector(0.0, 0.0, 0.0, 0.0));
        assertForce(0.75, 1.0, 4.0, 0.0, Force.fromVector(0.0, 1.0, 3.0, 5.0));
        assertForce(-1.0, -3.0, 1.0, 0.0, Force.fromVector(0.0, 1.0, -1.0, -2.0));
    }

    /**
     * Test constructor default.
     */
    @Test
    void testConstructorDefault()
    {
        assertForce(0.0, 0.0, 0.0, 0.0, new Force());
    }

    /**
     * Test constructor parameters.
     */
    @Test
    void testConstructorParameter()
    {
        assertForce(1.0, 2.0, 0.0, 0.0, new Force(1.0, 2.0));
        assertForce(1.0, 2.0, 3.0, 4.0, new Force(1.0, 2.0, 3.0, 4.0));
    }

    /**
     * Test getters.
     */
    @Test
    void testGetters()
    {
        final Force force = new Force(1.0, 2.0);
        force.setVelocity(1.0);
        force.setSensibility(2.0);

        assertEquals(1.0, force.getVelocity());
        assertEquals(2.0, force.getSensibility());
    }

    /**
     * Test add direction.
     */
    @Test
    void testAddDirection()
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
    void testAddDirectionExtrapolation()
    {
        final Force force = new Force(0.0, 0.0);
        force.addDirection(0.5, 1, 2.0);

        assertForce(0.5, 1.0, 0.0, 0.0, force);
    }

    /**
     * Test set direction.
     */
    @Test
    void testSetDirection()
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
    void testSetDestination()
    {
        final Force force = new Force(1.0, 2.0);
        force.setVelocity(1.0);
        force.setDestination(2.0, 3.0);

        assertForce(1.0, 2.0, 1.0, 0.0, force);
        assertTrue(force.isIncreasingHorizontal());
        assertFalse(force.isDecreasingHorizontal());

        force.update(1.0);

        assertForce(2.0, 3.0, 1.0, 0.0, force);
        assertTrue(force.isIncreasingHorizontal());
        assertFalse(force.isDecreasingHorizontal());

        force.setDestination(-1.0, -2.0);

        assertForce(2.0, 3.0, 1.0, 0.0, force);
        assertTrue(force.isIncreasingHorizontal());
        assertFalse(force.isDecreasingHorizontal());

        force.update(1.0);

        assertForce(1.0, 2.0, 1.0, 0.0, force);
        assertFalse(force.isIncreasingHorizontal());
        assertTrue(force.isDecreasingHorizontal());

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
    void testSensibility()
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
    void testSetDirectionMinimum()
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
    void testSetDirectionMaximum()
    {
        final Force force = new Force(3.0, 4.0);
        force.setDirectionMaximum(new Force(1.0, 2.0));
        force.update(1.0);

        assertForce(1.0, 2.0, 0.0, 0.0, force);
    }

    /**
     * Test zeroing force.
     */
    @Test
    void testZero()
    {
        final Force force = new Force();
        force.setVelocity(1.0);
        force.setDestination(1.0, 2.0);
        force.update(1.0);

        assertForce(1.0, 1.0, 1.0, 0.0, force);
        assertFalse(force.isZero());

        force.zero();

        assertForce(0.0, 0.0, 1.0, 0.0, force);
        assertTrue(force.isZero());
    }

    /**
     * Test copy.
     */
    @Test
    void testCopy()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        assertEquals(force, new Force(force));
    }

    /**
     * Test ensures no negative zero.
     */
    @Test
    void testNoNegativeZero()
    {
        final Force force = new Force(-0.0, -0.0, 1.0, 0.0);

        assertEquals(0.0, force.getDirectionHorizontal());
        assertEquals(0.0, force.getDirectionVertical());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        assertEquals(force, force);
        assertEquals(force, new Force(1.0, 2.0, 3.0, 4.0));

        assertNotEquals(force, null);
        assertNotEquals(force, new Object());
        assertNotEquals(force, new Force(1.0, 2.0, 3.0, 1.0));
        assertNotEquals(force, new Force(1.0, 2.0, 1.0, 4.0));
        assertNotEquals(force, new Force(1.0, 2.0, 1.0, 1.0));
        assertNotEquals(force, new Force(1.0, 1.0, 3.0, 4.0));
        assertNotEquals(force, new Force(1.0, 1.0, 3.0, 1.0));
        assertNotEquals(force, new Force(1.0, 1.0, 1.0, 4.0));
        assertNotEquals(force, new Force(2.0, 2.0, 3.0, 4.0));
        assertNotEquals(force, new Force(2.0, 2.0, 3.0, 1.0));
        assertNotEquals(force, new Force(2.0, 2.0, 1.0, 4.0));
        assertNotEquals(force, new Force(2.0, 2.0, 1.0, 1.0));
        assertNotEquals(force, new Force(2.0, 1.0, 3.0, 4.0));
        assertNotEquals(force, new Force(2.0, 1.0, 3.0, 1.0));
        assertNotEquals(force, new Force(2.0, 1.0, 1.0, 4.0));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final Force hash = new Force(1.0, 2.0, 3.0, 4.0);

        assertHashEquals(hash, new Force(1.0, 2.0, 3.0, 4.0));

        assertHashNotEquals(hash, new Object());
        assertHashNotEquals(hash, new Force(1.0, 2.0, 3.0, 1.0));
        assertHashNotEquals(hash, new Force(1.0, 2.0, 1.0, 4.0));
        assertHashNotEquals(hash, new Force(1.0, 2.0, 1.0, 1.0));
        assertHashNotEquals(hash, new Force(1.0, 1.0, 3.0, 4.0));
        assertHashNotEquals(hash, new Force(1.0, 1.0, 3.0, 1.0));
        assertHashNotEquals(hash, new Force(1.0, 1.0, 1.0, 4.0));
        assertHashNotEquals(hash, new Force(2.0, 2.0, 3.0, 4.0));
        assertHashNotEquals(hash, new Force(2.0, 2.0, 3.0, 1.0));
        assertHashNotEquals(hash, new Force(2.0, 2.0, 1.0, 4.0));
        assertHashNotEquals(hash, new Force(2.0, 2.0, 1.0, 1.0));
        assertHashNotEquals(hash, new Force(2.0, 1.0, 3.0, 4.0));
        assertHashNotEquals(hash, new Force(2.0, 1.0, 3.0, 1.0));
        assertHashNotEquals(hash, new Force(2.0, 1.0, 1.0, 4.0));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        assertEquals("Force [fh=1.0, fv=2.0, velocity=3.0, sensibility=4.0]", force.toString());
    }
}
