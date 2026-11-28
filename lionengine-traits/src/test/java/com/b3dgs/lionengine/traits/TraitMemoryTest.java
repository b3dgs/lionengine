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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.ecs.TraitType;
import com.sun.management.ThreadMXBean;

/**
 * Bytes per instance of the standard traits, documented in their Javadoc (REQ-STD-007, ADR-0034, ADR-0079,
 * ADR-0080, ADR-0081, ADR-0082).
 *
 * <p>
 * Measured as the benchmark measures the traits ({@code StepBenchmarkTest}): the bytes allocated on this
 * thread by creating many instances with the trait type, divided by their number (the object header and the fields,
 * as laid out by this JVM). The documented value is the one of the desktop JVM with its default layout; a JVM with a
 * smaller layout (compact object headers) measures less, so the test asserts the documented value as an upper bound
 * of the measure rounded to the byte (the counter's own noise is a fraction of a byte per instance) and logs it.
 * </p>
 */
final class TraitMemoryTest
{
    /** Bytes per {@link Transformable}, as documented. */
    private static final double TRANSFORMABLE_BYTES = 64.0;
    /** Bytes per {@link Movable}, as documented. */
    private static final double MOVABLE_BYTES = 40.0;
    /** Bytes per {@link Gravity}, as documented. */
    private static final double GRAVITY_BYTES = 32.0;
    /** Bytes per {@link Lifetime}, as documented. */
    private static final double LIFETIME_BYTES = 24.0;
    /** Bytes per {@link Collidable}, as documented. */
    private static final double COLLIDABLE_BYTES = 56.0;
    /** Instances created per measure. */
    private static final int INSTANCES = 10_000;

    /**
     * Each standard trait takes at most its documented bytes per instance.
     */
    @Test
    void testBytesPerInstance()
    {
        bytesPerInstance(Transformable.TYPE);
        bytesPerInstance(Movable.TYPE);
        bytesPerInstance(Gravity.TYPE);
        bytesPerInstance(Lifetime.TYPE);
        bytesPerInstance(Collidable.TYPE);
        final double transformable = bytesPerInstance(Transformable.TYPE);
        final double movable = bytesPerInstance(Movable.TYPE);
        final double gravity = bytesPerInstance(Gravity.TYPE);
        final double lifetime = bytesPerInstance(Lifetime.TYPE);
        final double collidable = bytesPerInstance(Collidable.TYPE);
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        log.log(LogLevel.INFO, "Transformable bytes per instance:", transformable);
        log.log(LogLevel.INFO, "Movable bytes per instance:", movable);
        log.log(LogLevel.INFO, "Gravity bytes per instance:", gravity);
        log.log(LogLevel.INFO, "Lifetime bytes per instance:", lifetime);
        log.log(LogLevel.INFO, "Collidable bytes per instance:", collidable);

        assertTrue(Math.round(transformable) <= TRANSFORMABLE_BYTES, () -> "Transformable: " + transformable);
        assertTrue(Math.round(movable) <= MOVABLE_BYTES, () -> "Movable: " + movable);
        assertTrue(Math.round(gravity) <= GRAVITY_BYTES, () -> "Gravity: " + gravity);
        assertTrue(Math.round(lifetime) <= LIFETIME_BYTES, () -> "Lifetime: " + lifetime);
        assertTrue(Math.round(collidable) <= COLLIDABLE_BYTES, () -> "Collidable: " + collidable);
    }

    /**
     * Measures the bytes per instance of a trait type.
     *
     * @param type The type.
     * @return The bytes allocated per created instance.
     */
    private static double bytesPerInstance(TraitType<?> type)
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final Object[] instances = new Object[INSTANCES];
        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < INSTANCES; i++)
        {
            instances[i] = type.create();
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;
        assertNotNull(instances[INSTANCES - 1]);
        return allocated / (double) INSTANCES;
    }
}
