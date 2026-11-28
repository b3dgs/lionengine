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
 * Tests of {@link Movable} and {@link Movable#TYPE}.
 */
final class MovableTest
{
    /**
     * A new instance has a zero velocity and no speed limit.
     */
    @Test
    void testNew()
    {
        final Movable movable = Movable.TYPE.create();

        assertEquals(0.0, movable.getVelocityX());
        assertEquals(0.0, movable.getVelocityY());
        assertEquals(Double.POSITIVE_INFINITY, movable.getMaxSpeed());
    }

    /**
     * The setters change their fields; the reset gives back the state of a new instance.
     */
    @Test
    void testSetAndReset()
    {
        final Movable movable = Movable.TYPE.create();
        movable.setVelocity(3.0, -4.0);
        movable.setMaxSpeed(6.0);

        assertEquals(3.0, movable.getVelocityX());
        assertEquals(-4.0, movable.getVelocityY());
        assertEquals(6.0, movable.getMaxSpeed());

        movable.setMaxSpeed(0.0);
        assertEquals(0.0, movable.getMaxSpeed());

        Movable.TYPE.reset(movable);
        assertEquals(0.0, movable.getVelocityX());
        assertEquals(0.0, movable.getVelocityY());
        assertEquals(Double.POSITIVE_INFINITY, movable.getMaxSpeed());
    }

    /**
     * A negative or not-a-number maximum speed is refused and leaves the value unchanged.
     */
    @Test
    void testInvalidMaxSpeed()
    {
        final Movable movable = new Movable();
        movable.setMaxSpeed(2.0);

        assertThrows(IllegalArgumentException.class, () -> movable.setMaxSpeed(-0.5));
        assertThrows(IllegalArgumentException.class, () -> movable.setMaxSpeed(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> movable.setMaxSpeed(Double.NEGATIVE_INFINITY));
        assertEquals(2.0, movable.getMaxSpeed());
    }

    /**
     * The clamp scales the magnitude down to the maximum speed, direction kept; a speed at or under the limit is
     * unchanged; a zero limit stops; no limit never clamps.
     */
    @Test
    void testClamp()
    {
        final Movable movable = new Movable();
        movable.setVelocity(30.0, -40.0);
        movable.setMaxSpeed(10.0);
        movable.clamp();
        assertEquals(6.0, movable.getVelocityX(), 1e-12);
        assertEquals(-8.0, movable.getVelocityY(), 1e-12);

        movable.setVelocity(8.0, 8.0);
        movable.clamp();
        assertEquals(10.0 / Math.sqrt(2.0), movable.getVelocityX(), 1e-12);
        assertEquals(10.0 / Math.sqrt(2.0), movable.getVelocityY(), 1e-12);

        movable.setVelocity(0.0, -10.0);
        movable.clamp();
        assertEquals(0.0, movable.getVelocityX());
        assertEquals(-10.0, movable.getVelocityY());

        movable.setMaxSpeed(0.0);
        movable.clamp();
        assertEquals(0.0, movable.getVelocityX());
        assertEquals(0.0, Math.abs(movable.getVelocityY()));

        movable.setMaxSpeed(Double.POSITIVE_INFINITY);
        movable.setVelocity(1e6, -1e6);
        movable.clamp();
        assertEquals(1e6, movable.getVelocityX());
        assertEquals(-1e6, movable.getVelocityY());
    }
}
