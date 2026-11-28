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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Lifetime} and {@link Lifetime#TYPE}.
 */
final class LifetimeTest
{
    /**
     * A new instance never expires.
     */
    @Test
    void testNew()
    {
        assertEquals(Double.POSITIVE_INFINITY, Lifetime.TYPE.create().getDuration());
    }

    /**
     * The setter changes the duration; the reset gives back the state of a new instance.
     */
    @Test
    void testSetAndReset()
    {
        final Lifetime lifetime = Lifetime.TYPE.create();
        lifetime.setDuration(1.5);
        assertEquals(1.5, lifetime.getDuration());

        lifetime.setDuration(0.0);
        assertEquals(0.0, lifetime.getDuration());

        Lifetime.TYPE.reset(lifetime);
        assertEquals(Double.POSITIVE_INFINITY, lifetime.getDuration());
    }

    /**
     * A negative or not-a-number duration is refused and leaves the value unchanged.
     */
    @Test
    void testInvalidDuration()
    {
        final Lifetime lifetime = new Lifetime();
        lifetime.setDuration(2.0);

        assertThrows(IllegalArgumentException.class, () -> lifetime.setDuration(-0.5));
        assertThrows(IllegalArgumentException.class, () -> lifetime.setDuration(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> lifetime.setDuration(Double.NEGATIVE_INFINITY));
        assertEquals(2.0, lifetime.getDuration());
    }

    /**
     * The count-down subtracts the step and expires at or under zero; an infinite duration never expires.
     */
    @Test
    void testCountDown()
    {
        final Lifetime lifetime = new Lifetime();
        lifetime.setDuration(0.5);
        assertFalse(lifetime.countDown(0.25));
        assertEquals(0.25, lifetime.getDuration());
        assertTrue(lifetime.countDown(0.25));
        assertEquals(0.0, lifetime.getDuration());

        lifetime.setDuration(0.1);
        assertTrue(lifetime.countDown(0.25));
        assertEquals(0.1 - 0.25, lifetime.getDuration());

        lifetime.setDuration(0.0);
        assertTrue(lifetime.countDown(1e-9));

        lifetime.setDuration(Double.POSITIVE_INFINITY);
        assertFalse(lifetime.countDown(1e9));
        assertEquals(Double.POSITIVE_INFINITY, lifetime.getDuration());
    }
}
