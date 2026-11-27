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
package com.b3dgs.lionengine.examples.ecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.ecs.World;

/**
 * Runs {@link ObjectLifecycleExample} (REQ-DOC-001, REQ-ECS-001, REQ-ECS-002, REQ-ECS-011).
 */
final class ObjectLifecycleExampleTest
{
    /** Steps played by the test. */
    private static final int STEPS = 20;

    /**
     * The bullets of a volley are alive after the flush of their step and expire after their lifetime; their IDs are
     * stale afterwards; the world never grows.
     */
    @Test
    void testVolleys()
    {
        final World world = ObjectLifecycleExample.createWorld();
        final long[] bullets = new long[ObjectLifecycleExample.VOLLEY * ObjectLifecycleExample.LIFETIME_STEPS];
        final long[] firstVolley = new long[ObjectLifecycleExample.VOLLEY];

        for (int step = 0; step < STEPS; step++)
        {
            final int alive = ObjectLifecycleExample.step(world, bullets, step);
            final int volleys = Math.min(step + 1, ObjectLifecycleExample.LIFETIME_STEPS);
            assertEquals(volleys * ObjectLifecycleExample.VOLLEY, alive);
            if (step == 0)
            {
                System.arraycopy(bullets, 0, firstVolley, 0, firstVolley.length);
            }
        }
        for (final long bullet : bullets)
        {
            assertTrue(world.isAlive(bullet));
        }
        for (final long stale : firstVolley)
        {
            assertFalse(world.isAlive(stale));
            assertThrows(IllegalArgumentException.class, () -> world.remove(stale));
        }
        assertEquals(ObjectLifecycleExample.CAPACITY, world.getObjectCapacity());
    }

    /**
     * The example runs: three volleys are alive after its last step.
     */
    @Test
    void testRun()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);

        assertEquals(ObjectLifecycleExample.VOLLEY * ObjectLifecycleExample.LIFETIME_STEPS,
                     ObjectLifecycleExample.run(log));
    }
}
