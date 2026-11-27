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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests of {@link World}: creation and removal, pending and stale IDs, liveness, capacities (REQ-ECS-001,
 * REQ-ECS-002, REQ-ECS-010, REQ-ECS-011, REQ-ECS-013, REQ-WORLD-001).
 */
final class WorldTest
{
    /** Number of create and remove cycles on one index. */
    private static final int CYCLES = 10_000;

    /**
     * A new world is empty, with the default capacity.
     */
    @Test
    void testDefault()
    {
        final World world = new World();

        assertEquals(0, world.getObjectCount());
        assertEquals(WorldConfig.DEFAULT_OBJECT_CAPACITY, world.getObjectCapacity());
        assertEquals(WorldConfig.DEFAULT_OBJECT_CAPACITY, world.getCommandCapacity());
        assertFalse(world.isAlive(World.NO_OBJECT));
    }

    /**
     * A {@code null} configuration is refused.
     */
    @Test
    void testNullConfig()
    {
        assertThrows(IllegalArgumentException.class, () -> new World(null));
    }

    /**
     * A created object is pending until the flush: not alive, accessors fail fast, but its removal can be recorded.
     */
    @Test
    void testPendingUntilFlush()
    {
        final World world = new World();
        final long id = world.create();

        assertTrue(id > 0L);
        assertFalse(world.isAlive(id));
        assertEquals(0, world.getObjectCount());
        assertThrows(IllegalStateException.class, () -> world.checkAlive(id));

        world.flush();

        assertTrue(world.isAlive(id));
        assertEquals(1, world.getObjectCount());
        assertEquals(0, world.checkAlive(id));
    }

    /**
     * A removed object stays alive until the flush; its ID is stale afterwards: liveness is false, accessors and a new
     * removal fail fast.
     */
    @Test
    void testRemoveAtFlush()
    {
        final World world = new World();
        final long id = world.create();
        world.flush();

        world.remove(id);

        assertTrue(world.isAlive(id));
        assertEquals(1, world.getObjectCount());

        world.flush();

        assertFalse(world.isAlive(id));
        assertEquals(0, world.getObjectCount());
        assertThrows(IllegalArgumentException.class, () -> world.checkAlive(id));
        assertThrows(IllegalArgumentException.class, () -> world.remove(id));
    }

    /**
     * A recycled index has a new generation: the old ID stays stale while the new one is alive.
     */
    @Test
    void testStaleAfterReuse()
    {
        final World world = new World(new WorldConfig(1));
        final long old = world.create();
        world.flush();
        world.remove(old);
        world.flush();

        final long reused = world.create();
        world.flush();

        assertNotEquals(old, reused);
        assertEquals(ObjectTable.index(old), ObjectTable.index(reused));
        assertTrue(world.isAlive(reused));
        assertFalse(world.isAlive(old));
        assertThrows(IllegalArgumentException.class, () -> world.remove(old));
        assertEquals(1, world.getObjectCapacity());
    }

    /**
     * Many create and remove cycles on one index never give an ID twice, and every old ID stays stale.
     */
    @Test
    void testManyCycles()
    {
        final World world = new World(new WorldConfig(1));
        long previous = World.NO_OBJECT;
        for (int i = 0; i < CYCLES; i++)
        {
            final long id = world.create();
            world.flush();
            assertTrue(id > previous);
            assertFalse(world.isAlive(previous));
            world.remove(id);
            world.flush();
            previous = id;
        }
        assertEquals(CYCLES + 1, ObjectTable.generation(world.create()));
        assertEquals(1, world.getObjectCapacity());
    }

    /**
     * An index at the highest generation is retired: the world takes a new index instead, and no ID repeats.
     */
    @Test
    void testRetiredIndex()
    {
        final World world = new World(new WorldConfig(1), 1);
        final long first = world.create();
        world.flush();
        world.remove(first);
        world.flush();

        final long second = world.create();
        world.flush();

        assertEquals(1, ObjectTable.index(second));
        assertFalse(world.isAlive(first));
        assertTrue(world.isAlive(second));
        assertEquals(2, world.getObjectCapacity());
    }

    /**
     * Values that never named an object are not alive, and refused by removal and accessors.
     *
     * @param id The ID.
     */
    @ParameterizedTest
    @ValueSource(longs = {World.NO_OBJECT, -1L, 1L, 4_294_967_296L, 8_589_934_592L, Long.MIN_VALUE, Long.MAX_VALUE})
    void testNeverValid(long id)
    {
        final World world = new World();

        assertFalse(world.isAlive(id));
        assertThrows(IllegalArgumentException.class, () -> world.remove(id));
        assertThrows(IllegalArgumentException.class, () -> world.checkAlive(id));
    }

    /**
     * Creating more objects than the capacity is allowed; the table doubles at the flush, and never shrinks.
     */
    @Test
    void testCapacityDoublesAtFlush()
    {
        final World world = new World(new WorldConfig(2));
        final long[] ids = new long[5];
        for (int i = 0; i < ids.length; i++)
        {
            ids[i] = world.create();
        }
        world.remove(ids[4]);

        assertEquals(2, world.getObjectCapacity());

        world.flush();

        assertEquals(8, world.getObjectCapacity());
        assertEquals(4, world.getObjectCount());
        for (int i = 0; i < 4; i++)
        {
            assertTrue(world.isAlive(ids[i]));
        }
        assertFalse(world.isAlive(ids[4]));

        for (int i = 0; i < 4; i++)
        {
            world.remove(ids[i]);
        }
        world.flush();

        assertEquals(8, world.getObjectCapacity());
        assertEquals(0, world.getObjectCount());
    }

    /**
     * The command log doubles when a step records more commands than its capacity.
     */
    @Test
    void testCommandLogGrows()
    {
        final World world = new World(new WorldConfig(2));
        world.create();
        world.create();
        world.create();

        assertEquals(4, world.getCommandCapacity());

        world.flush();

        assertEquals(3, world.getObjectCount());
        assertEquals(4, world.getCommandCapacity());
    }

    /**
     * {@code reserve} grows the table ahead and never shrinks it; pending objects created beyond the old capacity stay
     * pending until the flush.
     */
    @Test
    void testReserve()
    {
        final World world = new World(new WorldConfig(1));
        world.create();
        final long beyond = world.create();

        world.reserve(10);

        assertEquals(10, world.getObjectCapacity());
        assertFalse(world.isAlive(beyond));
        assertThrows(IllegalStateException.class, () -> world.checkAlive(beyond));

        world.reserve(4);
        world.flush();

        assertEquals(10, world.getObjectCapacity());
        assertTrue(world.isAlive(beyond));
    }

    /**
     * {@code reserve} refuses a capacity outside its range.
     *
     * @param capacity The capacity.
     */
    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, 0, WorldConfig.MAX_OBJECT_CAPACITY + 1})
    void testReserveInvalid(int capacity)
    {
        final World world = new World();

        assertThrows(IllegalArgumentException.class, () -> world.reserve(capacity));
    }

    /**
     * Two worlds are independent: their IDs, counts and capacities do not interfere (REQ-WORLD-001, REQ-ECS-010).
     */
    @Test
    void testTwoWorlds()
    {
        final World first = new World(new WorldConfig(1));
        final World second = new World();
        final long id = first.create();
        first.flush();
        second.create();
        second.create();

        assertTrue(first.isAlive(id));
        assertFalse(second.isAlive(id));
        assertEquals(1, first.getObjectCount());
        assertEquals(0, second.getObjectCount());

        first.remove(id);
        second.flush();

        assertTrue(first.isAlive(id));
        assertTrue(second.isAlive(id));
        assertEquals(2, second.getObjectCount());
        assertEquals(1, first.getObjectCapacity());
    }

    /**
     * A flush without command changes nothing.
     */
    @Test
    void testEmptyFlush()
    {
        final World world = new World();
        final long id = world.create();
        world.flush();

        world.flush();
        world.flush();

        assertTrue(world.isAlive(id));
        assertEquals(1, world.getObjectCount());
    }
}
