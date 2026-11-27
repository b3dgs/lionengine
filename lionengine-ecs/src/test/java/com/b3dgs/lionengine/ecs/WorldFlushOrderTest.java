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
package com.b3dgs.lionengine.ecs;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Regression set of the flush order: commands apply in recorded order, a removal frees its index only at the flush,
 * two identical runs give the same IDs (REQ-ECS-011, REQ-ECS-012, ADR-0030 decision 2, ADR-0074).
 */
final class WorldFlushOrderTest
{
    /** Number of steps of the reproducibility run. */
    private static final int STEPS = 200;
    /** Objects created per step of the reproducibility run. */
    private static final int CREATED_PER_STEP = 3;

    /**
     * A creation and a removal recorded before the same flush both apply: the object is never alive and its index is
     * free after the flush.
     */
    @Test
    void testCreateThenRemove()
    {
        final World world = new World(new WorldConfig(1));
        final long id = world.create();
        world.remove(id);
        world.flush();

        assertFalse(world.isAlive(id));
        assertEquals(0, world.getObjectCount());

        final long reused = world.create();

        assertEquals(ObjectTable.index(id), ObjectTable.index(reused));
        assertEquals(ObjectTable.generation(id) + 1, ObjectTable.generation(reused));
    }

    /**
     * An index removed before a flush is not reused by a creation recorded before the same flush.
     */
    @Test
    void testRemovedIndexNotReusedBeforeFlush()
    {
        final World world = new World();
        final long removed = world.create();
        world.flush();

        world.remove(removed);
        final long created = world.create();

        assertNotEquals(ObjectTable.index(removed), ObjectTable.index(created));

        world.flush();

        assertFalse(world.isAlive(removed));
        assertTrue(world.isAlive(created));
        assertEquals(1, world.getObjectCount());
    }

    /**
     * A second removal of the same object before the flush changes nothing more, and leaves the next creations alone.
     */
    @Test
    void testRemoveTwice()
    {
        final World world = new World();
        final long id = world.create();
        final long other = world.create();
        world.flush();

        world.remove(id);
        world.remove(id);
        world.flush();

        assertFalse(world.isAlive(id));
        assertTrue(world.isAlive(other));
        assertEquals(1, world.getObjectCount());

        final long reused = world.create();
        world.flush();

        assertTrue(world.isAlive(reused));
        assertEquals(2, world.getObjectCount());
    }

    /**
     * A removal recorded on a pending object, then the same pending object removed again: one creation and one
     * effective removal, the count never goes negative.
     */
    @Test
    void testPendingRemovedTwice()
    {
        final World world = new World();
        final long id = world.create();
        world.remove(id);
        world.remove(id);
        world.flush();

        assertFalse(world.isAlive(id));
        assertEquals(0, world.getObjectCount());
    }

    /**
     * Freed indices are reused in the order of their removal in the flush, oldest first.
     */
    @Test
    void testReuseInRemovalOrder()
    {
        final World world = new World();
        final long a = world.create();
        final long b = world.create();
        final long c = world.create();
        world.flush();

        world.remove(c);
        world.remove(a);
        world.remove(b);
        world.flush();

        assertEquals(ObjectTable.index(c), ObjectTable.index(world.create()));
        assertEquals(ObjectTable.index(a), ObjectTable.index(world.create()));
        assertEquals(ObjectTable.index(b), ObjectTable.index(world.create()));
    }

    /**
     * Two runs of the same operation sequence give the same IDs and the same liveness (REQ-DET-001).
     */
    @Test
    void testReproducible()
    {
        assertArrayEquals(run(), run());
    }

    /**
     * Runs a sequence of creations and removals over many steps.
     *
     * @return Every ID created, in creation order.
     */
    private static long[] run()
    {
        final World world = new World(new WorldConfig(2));
        final long[] ids = new long[STEPS * CREATED_PER_STEP];
        int count = 0;
        for (int step = 0; step < STEPS; step++)
        {
            for (int i = 0; i < CREATED_PER_STEP; i++)
            {
                ids[count] = world.create();
                count++;
            }
            for (int i = count - CREATED_PER_STEP - 1; i >= 0 && i >= count - 2 * CREATED_PER_STEP; i -= 2)
            {
                if (world.isAlive(ids[i]))
                {
                    world.remove(ids[i]);
                }
            }
            world.flush();
        }
        return ids;
    }
}
