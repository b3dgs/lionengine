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
package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.TraitType;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;

/**
 * Runs {@code ecs} on Android API 21 (ADR-0044): object creation and removal by ID, pending and stale IDs, the flush
 * and the capacity growth; trait registration, deferred trait addition, replacement and removal, pooling and the trait
 * capacity growth; queries with include and exclude sets, their update at the flush, changes requested inside an
 * iteration and the sorted ID copy. Expected values are the ones of the JVM tests of {@code lionengine-ecs} (WorldTest,
 * WorldFlushOrderTest, TraitStorageTest, QueryTest).
 */
@RunWith(AndroidJUnit4.class)
public final class EcsTest
{
    /** A trait type of the test, a one-element {@code int} array as data. */
    private static final TraitType<int[]> COUNTER = new TraitType<>()
    {
        @Override
        public int[] create()
        {
            return new int[1];
        }

        @Override
        public void reset(int[] trait)
        {
            trait[0] = 0;
        }
    };

    /**
     * Creates the test.
     */
    public EcsTest()
    {
        super();
    }

    /**
     * A created object is pending until the flush; a removed one is stale after the flush; a reused index has a new
     * generation.
     */
    @Test
    public void testLifecycle()
    {
        final World world = new World(new WorldConfig(1, WorldConfig.DEFAULT_TRAIT_CAPACITY));
        final long id = world.create();

        assertFalse(world.isAlive(id));
        world.flush();
        assertTrue(world.isAlive(id));

        world.remove(id);
        assertTrue(world.isAlive(id));
        world.flush();
        assertFalse(world.isAlive(id));
        assertThrows(IllegalArgumentException.class, () -> world.remove(id));

        final long reused = world.create();
        world.flush();
        assertTrue(world.isAlive(reused));
        assertTrue(reused > id);
        assertEquals(1, world.getObjectCount());
        assertFalse(world.isAlive(World.NO_OBJECT));
    }

    /**
     * Creating more objects than the capacity doubles the table at the flush.
     */
    @Test
    public void testGrowth()
    {
        final World world = new World(new WorldConfig(2, WorldConfig.DEFAULT_TRAIT_CAPACITY));
        for (int i = 0; i < 5; i++)
        {
            world.create();
        }
        assertEquals(2, world.getObjectCapacity());
        world.flush();
        assertEquals(8, world.getObjectCapacity());
        assertEquals(5, world.getObjectCount());
    }

    /**
     * A trait is registered once, added at the flush, replaced by a second addition, removed at the flush and reset into
     * the pool; accessors refuse a pending object; removing the object removes its traits.
     */
    @Test
    public void testTraits()
    {
        final World world = new World(new WorldConfig(2, 1));
        final TraitStorage<int[]> counters = world.register(COUNTER);
        assertEquals(0, counters.getTypeId());
        assertThrows(IllegalArgumentException.class, () -> world.register(COUNTER));

        final long id = world.create();
        final int[] first = counters.add(id);
        first[0] = 5;
        assertThrows(IllegalStateException.class, () -> counters.has(id));
        world.flush();
        assertSame(first, counters.get(id));
        assertEquals(5, counters.get(id)[0]);

        final int[] second = counters.add(id);
        second[0] = 7;
        world.flush();
        assertSame(second, counters.get(id));
        assertEquals(0, first[0]);

        counters.remove(id);
        world.flush();
        assertFalse(counters.has(id));
        assertSame(second, counters.add(id));
        world.flush();

        world.remove(id);
        world.flush();
        assertEquals(0, counters.getCount());
        assertThrows(IllegalArgumentException.class, () -> counters.has(id));
    }

    /**
     * Adding more traits than the trait capacity doubles it at the flush.
     */
    @Test
    public void testTraitGrowth()
    {
        final World world = new World(new WorldConfig(8, 1));
        final TraitStorage<int[]> counters = world.register(COUNTER);
        for (int i = 0; i < 3; i++)
        {
            counters.add(world.create())[0] = i;
        }
        assertEquals(1, counters.getCapacity());
        world.flush();
        assertEquals(4, counters.getCapacity());
        assertEquals(3, counters.getCount());
    }

    /**
     * A query lists the objects with the included trait and without the excluded one, follows the flush, stays valid
     * while removals are requested inside its iteration, and copies its IDs in ascending order.
     */
    @Test
    public void testQueries()
    {
        final World world = new World(new WorldConfig(4, 4));
        final TraitStorage<int[]> counters = world.register(COUNTER);
        final TraitStorage<int[]> frozen = world.register(new TraitType<int[]>()
        {
            @Override
            public int[] create()
            {
                return new int[1];
            }

            @Override
            public void reset(int[] trait)
            {
                trait[0] = 0;
            }
        });
        final long[] ids = new long[5];
        for (int i = 0; i < ids.length; i++)
        {
            ids[i] = world.create();
            counters.add(ids[i]);
        }
        frozen.add(ids[1]);
        world.flush();
        final Query active = world.createQuery(new TraitStorage<?>[] {counters}, new TraitStorage<?>[] {frozen});
        final Query all = world.createQuery(counters);
        final long[] sorted = new long[ids.length];

        assertEquals(4, active.copySortedIds(sorted));
        assertArrayEquals(new long[] {ids[0], ids[2], ids[3], ids[4]}, Arrays.copyOf(sorted, 4));

        for (int i = 0; i < all.getCount(); i++)
        {
            world.remove(all.getId(i));
        }
        frozen.remove(ids[1]);
        assertEquals(5, all.getCount());
        world.flush();
        assertEquals(0, all.getCount());
        assertEquals(0, active.getCount());
        assertThrows(IllegalArgumentException.class, () -> all.copySortedIds(null));
    }
}
