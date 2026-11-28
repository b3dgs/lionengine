/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.traits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Gravity} and {@link Gravity#TYPE}.
 */
final class GravityTest
{
    /** Tolerance on one accelerated velocity, in meters per second. */
    private static final double EPSILON = 1e-12;

    /**
     * A new instance has no acceleration and no terminal velocity.
     */
    @Test
    void testNew()
    {
        final Gravity gravity = Gravity.TYPE.create();

        assertEquals(0.0, gravity.getAcceleration());
        assertEquals(Double.POSITIVE_INFINITY, gravity.getTerminalVelocity());
    }

    /**
     * The setters change their fields; the reset gives back the state of a new instance.
     */
    @Test
    void testSetAndReset()
    {
        final Gravity gravity = Gravity.TYPE.create();
        gravity.setAcceleration(-9.81);
        gravity.setTerminalVelocity(20.0);

        assertEquals(-9.81, gravity.getAcceleration());
        assertEquals(20.0, gravity.getTerminalVelocity());

        gravity.setTerminalVelocity(0.0);
        assertEquals(0.0, gravity.getTerminalVelocity());

        Gravity.TYPE.reset(gravity);
        assertEquals(0.0, gravity.getAcceleration());
        assertEquals(Double.POSITIVE_INFINITY, gravity.getTerminalVelocity());
    }

    /**
     * A negative or not-a-number terminal velocity is refused and leaves the value unchanged.
     */
    @Test
    void testInvalidTerminalVelocity()
    {
        final Gravity gravity = new Gravity();
        gravity.setTerminalVelocity(2.0);

        assertThrows(IllegalArgumentException.class, () -> gravity.setTerminalVelocity(-0.5));
        assertThrows(IllegalArgumentException.class, () -> gravity.setTerminalVelocity(Double.NaN));
        assertThrows(IllegalArgumentException.class,
                     () -> gravity.setTerminalVelocity(Double.NEGATIVE_INFINITY));
        assertEquals(2.0, gravity.getTerminalVelocity());
    }

    /**
     * The acceleration is added with the step, limited to the terminal velocity in its direction only; a velocity
     * already beyond the limit is kept; no acceleration changes nothing.
     */
    @Test
    void testAccelerate()
    {
        final Gravity gravity = new Gravity();
        assertEquals(-4.0, gravity.accelerate(-4.0, 0.5));

        gravity.setAcceleration(-10.0);
        assertEquals(-1.0, gravity.accelerate(0.0, 0.1), EPSILON);
        assertEquals(-1e6, gravity.accelerate(0.0, 1e5), EPSILON);

        gravity.setTerminalVelocity(3.0);
        assertEquals(-3.0, gravity.accelerate(-2.5, 0.1));
        assertEquals(-3.0, gravity.accelerate(-3.0, 0.1));
        assertEquals(-8.0, gravity.accelerate(-8.0, 0.1));
        assertEquals(4.0, gravity.accelerate(5.0, 0.1), EPSILON);
        assertEquals(-3.0, gravity.accelerate(10.0, 2.0));

        gravity.setAcceleration(4.0);
        gravity.setTerminalVelocity(2.0);
        assertEquals(2.0, gravity.accelerate(1.9, 0.1));
        assertEquals(6.0, gravity.accelerate(6.0, 0.1));
        assertEquals(-9.6, gravity.accelerate(-10.0, 0.1), EPSILON);

        gravity.setTerminalVelocity(0.0);
        assertEquals(0.0, gravity.accelerate(-1.0, 1.0));
        assertEquals(0.0, gravity.accelerate(0.0, 1.0));
    }
}
