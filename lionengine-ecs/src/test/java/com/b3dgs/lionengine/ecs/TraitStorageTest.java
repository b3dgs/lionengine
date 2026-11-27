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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests of {@link World#register(TraitType)} and {@link TraitStorage}: registration, deferred add, remove and replace,
 * accessors, pooling and capacities (REQ-ECS-003, REQ-ECS-004, REQ-ECS-006, REQ-ECS-013, ADR-0011, ADR-0030, ADR-0034,
 * ADR-0075).
 */
final class TraitStorageTest
{
    /** Points written by the tests. */
    private static final int POINTS = 42;
    /** Other points. */
    private static final int OTHER_POINTS = 7;

    /**
     * Creates an alive object.
     *
     * @param world The world.
     * @return Its ID.
     */
    private static long alive(World world)
    {
        final long id = world.create();
        world.flush();
        return id;
    }

    /**
     * Types get consecutive IDs from 0 in registration order; the storage keeps its type; the pool is filled.
     */
    @Test
    void testRegister()
    {
        final World world = new World();
        final HealthType health = new HealthType();
        final HealthType armor = new HealthType();

        final TraitStorage<Health> first = world.register(health);
        final TraitStorage<Health> second = world.register(armor);

        assertEquals(0, first.getTypeId());
        assertEquals(1, second.getTypeId());
        assertSame(health, first.getType());
        assertEquals(2, world.getTraitTypeCount());
        assertEquals(WorldConfig.DEFAULT_TRAIT_CAPACITY, first.getCapacity());
        assertEquals(WorldConfig.DEFAULT_TRAIT_CAPACITY, health.getCreated());
        assertEquals(WorldConfig.DEFAULT_TRAIT_CAPACITY, first.getPoolSize());
        assertEquals(0, first.getCount());
    }

    /**
     * A type registered twice in a world, or {@code null}, is refused; a type is registered in two worlds
     * independently.
     */
    @Test
    void testRegisterDuplicate()
    {
        final World world = new World();
        final HealthType health = new HealthType();
        world.register(health);

        assertThrows(IllegalArgumentException.class, () -> world.register(health));
        assertThrows(IllegalArgumentException.class, () -> world.register(null));
        assertEquals(1, world.getTraitTypeCount());

        final World other = new World();
        other.register(new HealthType());
        assertEquals(1, other.register(health).getTypeId());
    }

    /**
     * A type creating a {@code null} instance is refused at registration and changes nothing.
     */
    @Test
    void testRegisterNullInstance()
    {
        final World world = new World();
        final TraitType<Health> broken = new TraitType<>()
        {
            @Override
            public Health create()
            {
                return null;
            }

            @Override
            public void reset(Health trait)
            {
                trait.points = 0;
            }
        };

        assertThrows(IllegalStateException.class, () -> world.register(broken));
        assertEquals(0, world.getTraitTypeCount());
        assertEquals(0, world.register(new HealthType()).getTypeId());
    }

    /**
     * More types than the initial storage array and than one mask word are registered.
     */
    @Test
    void testManyTypes()
    {
        final World world = new World(new WorldConfig(2, 1));
        final int types = 3 * TraitMasks.TYPES_PER_WORD + 1;
        final long id = alive(world);
        TraitStorage<Health> last = null;
        for (int t = 0; t < types; t++)
        {
            last = world.register(new HealthType());
            last.add(id);
        }
        world.flush();

        assertEquals(types, world.getTraitTypeCount());
        assertEquals(types - 1, last.getTypeId());
        assertTrue(last.has(id));
        assertEquals(4, world.getMasks().getWordCount());
        assertTrue(world.getMasks().has(ObjectTable.index(id), types - 1));

        world.remove(id);
        world.flush();
        for (int t = 0; t < types; t++)
        {
            assertEquals(0, world.getStorage(t).getCount());
        }
    }

    /**
     * An addition is invisible before the flush and visible after it, with the data written before the flush.
     */
    @Test
    void testAddAtFlush()
    {
        final World world = new World();
        final TraitStorage<Health> health = world.register(new HealthType());
        final long id = alive(world);

        final Health trait = health.add(id);
        assertEquals(0, trait.points);
        trait.points = POINTS;
        assertFalse(health.has(id));
        assertEquals(0, health.getCount());

        world.flush();

        assertTrue(health.has(id));
        assertSame(trait, health.get(id));
        assertEquals(POINTS, health.get(id).points);
        assertEquals(1, health.getCount());
        assertTrue(world.getMasks().has(ObjectTable.index(id), health.getTypeId()));
    }

    /**
     * A removal is invisible before the flush; after it the trait is gone, reset and pooled.
     */
    @Test
    void testRemoveAtFlush()
    {
        final World world = new World();
        final HealthType type = new HealthType();
        final TraitStorage<Health> health = world.register(type);
        final long id = alive(world);
        health.add(id).points = POINTS;
        world.flush();
        final int pool = health.getPoolSize();

        health.remove(id);
        assertTrue(health.has(id));

        world.flush();

        assertFalse(health.has(id));
        assertEquals(0, health.getCount());
        assertEquals(1, type.getResets());
        assertEquals(pool + 1, health.getPoolSize());
        assertFalse(world.getMasks().has(ObjectTable.index(id), health.getTypeId()));
    }

    /**
     * Removing a trait the object does not have changes nothing; removing it twice applies once.
     */
    @Test
    void testRemoveAbsent()
    {
        final World world = new World();
        final HealthType type = new HealthType();
        final TraitStorage<Health> health = world.register(type);
        final long id = alive(world);

        health.remove(id);
        world.flush();
        assertFalse(health.has(id));

        health.add(id);
        world.flush();
        health.remove(id);
        health.remove(id);
        world.flush();

        assertFalse(health.has(id));
        assertEquals(1, type.getResets());
    }

    /**
     * Adding a trait the object already has replaces it at the flush: the last addition wins, the old instance is reset
     * and pooled, the count does not change.
     */
    @Test
    void testReplace()
    {
        final World world = new World();
        final HealthType type = new HealthType();
        final TraitStorage<Health> health = world.register(type);
        final long id = alive(world);
        final Health old = health.add(id);
        old.points = POINTS;
        world.flush();

        final Health replacement = health.add(id);
        replacement.points = OTHER_POINTS;
        assertSame(old, health.get(id));
        world.flush();

        assertSame(replacement, health.get(id));
        assertEquals(OTHER_POINTS, health.get(id).points);
        assertEquals(0, old.points);
        assertEquals(1, type.getResets());
        assertEquals(1, health.getCount());
    }

    /**
     * Two additions in one step: the last wins.
     */
    @Test
    void testTwoAdditionsInOneStep()
    {
        final World world = new World();
        final TraitStorage<Health> health = world.register(new HealthType());
        final long id = alive(world);

        health.add(id).points = POINTS;
        final Health last = health.add(id);
        last.points = OTHER_POINTS;
        world.flush();

        assertSame(last, health.get(id));
        assertEquals(1, health.getCount());
    }

    /**
     * An addition followed by a removal in one step both apply: no trait after the flush, the instance back in the
     * pool, reset.
     */
    @Test
    void testAddThenRemove()
    {
        final World world = new World();
        final HealthType type = new HealthType();
        final TraitStorage<Health> health = world.register(type);
        final long id = alive(world);
        final int pool = health.getPoolSize();

        final Health trait = health.add(id);
        trait.points = POINTS;
        health.remove(id);
        world.flush();

        assertFalse(health.has(id));
        assertEquals(0, trait.points);
        assertEquals(1, type.getResets());
        assertEquals(pool, health.getPoolSize());
    }

    /**
     * A removal followed by an addition in one step: the object has the new trait.
     */
    @Test
    void testRemoveThenAdd()
    {
        final World world = new World();
        final TraitStorage<Health> health = world.register(new HealthType());
        final long id = alive(world);
        final Health old = health.add(id);
        world.flush();

        health.remove(id);
        final Health trait = health.add(id);
        world.flush();

        assertNotSame(old, trait);
        assertSame(trait, health.get(id));
        assertEquals(1, health.getCount());
    }

    /**
     * A trait added to a pending object is attached by the flush that creates it.
     */
    @Test
    void testAddToPending()
    {
        final World world = new World();
        final TraitStorage<Health> health = world.register(new HealthType());
        final long id = world.create();

        health.add(id).points = POINTS;
        world.flush();

        assertTrue(world.isAlive(id));
        assertEquals(POINTS, health.get(id).points);
    }

    /**
     * An addition to an object removed earlier in the same flush is discarded: the instance goes back to the pool,
     * reset; also for an object created and removed in the same step.
     */
    @Test
    void testAddToRemoved()
    {
        final World world = new World();
        final HealthType type = new HealthType();
        final TraitStorage<Health> health = world.register(type);
        final long id = alive(world);
        final long pending = world.create();
        final int pool = health.getPoolSize();

        world.remove(id);
        health.add(id).points = POINTS;
        world.remove(pending);
        health.add(pending).points = POINTS;
        world.flush();

        assertEquals(0, health.getCount());
        assertEquals(pool, health.getPoolSize());
        assertEquals(2, type.getResets());
    }

    /**
     * Removing an object removes all its traits, resets and pools them; the other objects keep theirs.
     */
    @Test
    void testObjectRemovalRemovesTraits()
    {
        final World world = new World();
        final HealthType healthType = new HealthType();
        final HealthType armorType = new HealthType();
        final TraitStorage<Health> health = world.register(healthType);
        final TraitStorage<Health> armor = world.register(armorType);
        final long removed = world.create();
        final long kept = world.create();
        health.add(removed);
        armor.add(removed);
        health.add(kept).points = POINTS;
        world.flush();

        world.remove(removed);
        world.flush();

        assertEquals(1, health.getCount());
        assertEquals(0, armor.getCount());
        assertEquals(1, healthType.getResets());
        assertEquals(1, armorType.getResets());
        assertEquals(POINTS, health.get(kept).points);
        assertThrows(IllegalArgumentException.class, () -> health.has(removed));

        final long reused = world.create();
        world.flush();
        assertEquals(ObjectTable.index(removed), ObjectTable.index(reused));
        assertFalse(health.has(reused));
        assertFalse(armor.has(reused));
    }

    /**
     * An object created, given a trait and removed in the same step leaves nothing.
     */
    @Test
    void testCreateAddRemoveInOneStep()
    {
        final World world = new World();
        final HealthType type = new HealthType();
        final TraitStorage<Health> health = world.register(type);
        final long id = world.create();

        health.add(id);
        world.remove(id);
        world.flush();

        assertFalse(world.isAlive(id));
        assertEquals(0, health.getCount());
        assertEquals(1, type.getResets());
    }

    /**
     * Swap-remove: removing a trait in the middle moves the last one; every object keeps its own instance.
     */
    @Test
    void testSwapRemove()
    {
        final World world = new World();
        final TraitStorage<Health> health = world.register(new HealthType());
        final int count = 5;
        final long[] ids = new long[count];
        for (int i = 0; i < count; i++)
        {
            ids[i] = world.create();
            health.add(ids[i]).points = i + 1;
        }
        world.flush();
        assertEquals(ids[0], health.getId(0));
        assertSame(health.get(ids[0]), health.getAt(0));

        health.remove(ids[1]);
        world.flush();

        assertEquals(ids[count - 1], health.getId(1));
        assertEquals(count - 1, health.getCount());
        for (int i = 0; i < count; i++)
        {
            if (i != 1)
            {
                assertEquals(i + 1, health.get(ids[i]).points);
            }
        }
        health.remove(ids[count - 1]);
        world.flush();
        assertEquals(count - 2, health.getCount());
        assertEquals(1, health.get(ids[0]).points);
    }

    /**
     * Accessors refuse a pending object with {@link IllegalStateException}, a stale or never-valid ID with
     * {@link IllegalArgumentException}; {@code get} refuses an object without the trait.
     */
    @Test
    void testAccessorErrors()
    {
        final World world = new World();
        final TraitStorage<Health> health = world.register(new HealthType());
        final long stale = alive(world);
        world.remove(stale);
        world.flush();
        final long without = alive(world);
        final long pending = world.create();

        assertThrows(IllegalStateException.class, () -> health.has(pending));
        assertThrows(IllegalStateException.class, () -> health.get(pending));
        assertThrows(IllegalArgumentException.class, () -> health.has(stale));
        assertThrows(IllegalArgumentException.class, () -> health.get(stale));
        assertThrows(IllegalArgumentException.class, () -> health.has(World.NO_OBJECT));
        assertThrows(IllegalArgumentException.class, () -> health.get(-1L));
        assertThrows(IllegalStateException.class, () -> health.get(without));
    }

    /**
     * Additions and removals refuse a stale or never-valid ID and record nothing.
     */
    @Test
    void testCommandErrors()
    {
        final World world = new World();
        final TraitStorage<Health> health = world.register(new HealthType());
        final long stale = alive(world);
        world.remove(stale);
        world.flush();
        final int pool = health.getPoolSize();

        assertThrows(IllegalArgumentException.class, () -> health.add(stale));
        assertThrows(IllegalArgumentException.class, () -> health.remove(stale));
        assertThrows(IllegalArgumentException.class, () -> health.add(World.NO_OBJECT));
        assertThrows(IllegalArgumentException.class, () -> health.remove(Long.MAX_VALUE));
        assertEquals(pool, health.getPoolSize());
        world.flush();
        assertEquals(0, health.getCount());
    }

    /**
     * Pooled instances are reused: a removed instance is handed out again, reset, and no instance is created.
     */
    @Test
    void testPooledReuse()
    {
        final World world = new World(new WorldConfig(WorldConfig.DEFAULT_OBJECT_CAPACITY, 1));
        final HealthType type = new HealthType();
        final TraitStorage<Health> health = world.register(type);
        final long id = alive(world);
        final Health first = health.add(id);
        first.points = POINTS;
        world.flush();
        health.remove(id);
        world.flush();

        final Health second = health.add(id);

        assertSame(first, second);
        assertEquals(0, second.points);
        assertEquals(1, type.getCreated());
    }

    /**
     * Beyond the trait capacity, an addition creates an instance when the pool is empty and the flush doubles the dense
     * array; the capacity never shrinks.
     */
    @Test
    void testTraitCapacityGrowth()
    {
        final World world = new World(new WorldConfig(WorldConfig.DEFAULT_OBJECT_CAPACITY, 2));
        final HealthType type = new HealthType();
        final TraitStorage<Health> health = world.register(type);
        final int count = 5;
        final long[] ids = new long[count];
        for (int i = 0; i < count; i++)
        {
            ids[i] = world.create();
            health.add(ids[i]).points = i;
        }
        assertEquals(count, type.getCreated());
        assertEquals(2, health.getCapacity());

        world.flush();

        assertEquals(8, health.getCapacity());
        assertEquals(count, health.getCount());
        for (int i = 0; i < count; i++)
        {
            assertEquals(i, health.get(ids[i]).points);
            world.remove(ids[i]);
        }
        world.flush();
        assertEquals(8, health.getCapacity());
        assertEquals(count, health.getPoolSize());
    }

    /**
     * {@code reserve} grows the dense array and fills the pool ahead, never shrinks, and refuses an invalid capacity.
     */
    @Test
    void testReserve()
    {
        final World world = new World(new WorldConfig(WorldConfig.DEFAULT_OBJECT_CAPACITY, 2));
        final HealthType type = new HealthType();
        final TraitStorage<Health> health = world.register(type);
        final int reserved = 10;

        health.reserve(reserved);

        assertEquals(reserved, health.getCapacity());
        assertEquals(reserved, type.getCreated());
        assertEquals(reserved, health.getPoolSize());

        health.reserve(1);
        assertEquals(reserved, health.getCapacity());
        assertThrows(IllegalArgumentException.class, () -> health.reserve(0));
        assertThrows(IllegalArgumentException.class, () -> health.reserve(WorldConfig.MAX_TRAIT_CAPACITY + 1));
    }

    /**
     * Traits follow the object table when it grows: at the flush and by {@code reserve}, also for a type registered
     * after the growth.
     */
    @Test
    void testObjectGrowth()
    {
        final World world = new World(new WorldConfig(1, 1));
        final TraitStorage<Health> health = world.register(new HealthType());
        final int count = 3;
        final long[] ids = new long[count];
        for (int i = 0; i < count; i++)
        {
            ids[i] = world.create();
            health.add(ids[i]).points = i;
        }
        world.flush();
        assertEquals(4, world.getObjectCapacity());
        assertEquals(2, health.get(ids[2]).points);

        world.reserve(16);
        final TraitStorage<Health> armor = world.register(new HealthType());
        final long[] more = new long[16 - count];
        for (int i = 0; i < more.length; i++)
        {
            more[i] = world.create();
            armor.add(more[i]);
            health.add(more[i]);
        }
        world.flush();
        assertEquals(16, world.getObjectCapacity());
        assertEquals(16, health.getCount());
        assertEquals(more.length, armor.getCount());
        assertTrue(armor.has(more[more.length - 1]));
        assertFalse(armor.has(ids[0]));
    }

    /**
     * The traits of two worlds are independent, also with the same type.
     */
    @Test
    void testTwoWorlds()
    {
        final HealthType type = new HealthType();
        final World first = new World();
        final World second = new World();
        final TraitStorage<Health> firstHealth = first.register(type);
        final TraitStorage<Health> secondHealth = second.register(type);
        final long a = alive(first);
        final long b = alive(second);

        firstHealth.add(a).points = POINTS;
        first.flush();
        second.flush();

        assertTrue(firstHealth.has(a));
        assertFalse(secondHealth.has(b));
    }
}
