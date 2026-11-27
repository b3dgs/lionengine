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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Query} and {@link World#createQuery(TraitStorage[], TraitStorage[])}: include and exclude matching,
 * incremental update at the flush, creation after objects exist, changes requested inside an iteration, order and
 * sorted copy (REQ-ECS-005, REQ-ECS-006, REQ-ECS-012, REQ-ECS-013, ADR-0011, ADR-0030, ADR-0034, ADR-0076).
 */
final class QueryTest
{
    /**
     * Returns the IDs of a query in its iteration order.
     *
     * @param query The query.
     * @return The IDs.
     */
    private static long[] ids(Query query)
    {
        final long[] ids = new long[query.getCount()];
        for (int i = 0; i < ids.length; i++)
        {
            ids[i] = query.getId(i);
        }
        return ids;
    }

    /**
     * Returns the IDs of a query in ascending order.
     *
     * @param query The query.
     * @return The sorted IDs.
     */
    private static long[] sorted(Query query)
    {
        final long[] ids = new long[query.getCount()];
        query.copySortedIds(ids);
        return ids;
    }

    /**
     * Tells whether a query lists an object.
     *
     * @param query The query.
     * @param id The object ID.
     * @return {@code true} if listed.
     */
    private static boolean lists(Query query, long id)
    {
        for (int i = 0; i < query.getCount(); i++)
        {
            if (query.getId(i) == id)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * A query lists the objects having all included types and none of the excluded ones.
     */
    @Test
    void testIncludeExcludeMatching()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final TraitStorage<Health> b = world.register(new HealthType());
        final TraitStorage<Health> c = world.register(new HealthType());
        final long none = world.create();
        final long onlyA = world.create();
        a.add(onlyA);
        final long ab = world.create();
        a.add(ab);
        b.add(ab);
        final long abc = world.create();
        a.add(abc);
        b.add(abc);
        c.add(abc);
        final long ac = world.create();
        a.add(ac);
        c.add(ac);
        world.flush();

        final Query withA = world.createQuery(a);
        final Query withAb = world.createQuery(a, b);
        final Query withANotC = world.createQuery(new TraitStorage<?>[] {a}, new TraitStorage<?>[] {c});
        final Query withAbNotC = world.createQuery(new TraitStorage<?>[] {a, b, a}, new TraitStorage<?>[] {c, c});

        assertArrayEquals(new long[] {onlyA, ab, abc, ac}, sorted(withA));
        assertArrayEquals(new long[] {ab, abc}, sorted(withAb));
        assertArrayEquals(new long[] {onlyA, ab}, sorted(withANotC));
        assertArrayEquals(new long[] {ab}, sorted(withAbNotC));
        assertFalse(lists(withA, none));
        assertEquals(4, world.getQueryCount());
    }

    /**
     * Include and exclude sets work beyond 64 trait types, across mask words.
     */
    @Test
    void testMoreThan64Types()
    {
        final World world = new World();
        final int types = TraitMasks.TYPES_PER_WORD * 2 + 3;
        final List<TraitStorage<Health>> storages = new ArrayList<>();
        for (int t = 0; t < types; t++)
        {
            storages.add(world.register(new HealthType()));
        }
        final TraitStorage<Health> low = storages.get(1);
        final TraitStorage<Health> high = storages.get(TraitMasks.TYPES_PER_WORD + 1);
        final TraitStorage<Health> highest = storages.get(types - 1);
        final TraitStorage<Health> word0Exclude = storages.get(TraitMasks.TYPES_PER_WORD - 1);
        final Query query = world.createQuery(new TraitStorage<?>[] {low, high}, new TraitStorage<?>[] {highest});
        final Query lowOnly = world.createQuery(new TraitStorage<?>[] {low}, new TraitStorage<?>[] {word0Exclude});

        final long both = world.create();
        low.add(both);
        high.add(both);
        final long excluded = world.create();
        low.add(excluded);
        high.add(excluded);
        highest.add(excluded);
        final long lowObject = world.create();
        low.add(lowObject);
        final long highObject = world.create();
        high.add(highObject);
        final long lowExcluded = world.create();
        low.add(lowExcluded);
        word0Exclude.add(lowExcluded);
        world.flush();

        assertArrayEquals(new long[] {both}, ids(query));
        assertArrayEquals(new long[] {both, excluded, lowObject}, sorted(lowOnly));

        highest.remove(excluded);
        world.flush();

        assertArrayEquals(new long[] {both, excluded}, sorted(query));
    }

    /**
     * The flush updates a query when an included trait is added or removed.
     */
    @Test
    void testUpdateOnAddAndRemove()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final TraitStorage<Health> b = world.register(new HealthType());
        final Query query = world.createQuery(a, b);
        final long id = world.create();
        a.add(id);
        world.flush();
        assertEquals(0, query.getCount());

        b.add(id);
        assertEquals(0, query.getCount());
        world.flush();
        assertArrayEquals(new long[] {id}, ids(query));

        a.remove(id);
        assertEquals(1, query.getCount());
        world.flush();
        assertEquals(0, query.getCount());

        a.add(id);
        a.remove(id);
        world.flush();
        assertEquals(0, query.getCount());

        a.remove(id);
        a.add(id);
        world.flush();
        assertArrayEquals(new long[] {id}, ids(query));
    }

    /**
     * The flush updates a query when an excluded trait is added or removed.
     */
    @Test
    void testUpdateOnExclude()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final TraitStorage<Health> dead = world.register(new HealthType());
        final Query query = world.createQuery(new TraitStorage<?>[] {a}, new TraitStorage<?>[] {dead});
        final long id = world.create();
        a.add(id);
        world.flush();
        assertArrayEquals(new long[] {id}, ids(query));

        dead.add(id);
        world.flush();
        assertEquals(0, query.getCount());

        dead.remove(id);
        world.flush();
        assertArrayEquals(new long[] {id}, ids(query));
    }

    /**
     * Replacing an included trait keeps the object in place in the query.
     */
    @Test
    void testReplaceKeepsMembership()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final Query query = world.createQuery(a);
        final long first = world.create();
        a.add(first);
        final long second = world.create();
        a.add(second);
        world.flush();
        final long[] before = ids(query);

        a.add(first).points = 3;
        world.flush();

        assertArrayEquals(before, ids(query));
        assertEquals(3, a.get(first).points);
    }

    /**
     * Removing an object removes it from every query; its reused index starts outside them.
     */
    @Test
    void testObjectRemoval()
    {
        final World world = new World(new WorldConfig(1, 1));
        final TraitStorage<Health> a = world.register(new HealthType());
        final TraitStorage<Health> dead = world.register(new HealthType());
        final Query query = world.createQuery(a);
        final Query alive = world.createQuery(new TraitStorage<?>[] {a}, new TraitStorage<?>[] {dead});
        final long id = world.create();
        a.add(id);
        world.flush();

        world.remove(id);
        world.flush();

        assertEquals(0, query.getCount());
        assertEquals(0, alive.getCount());
        final long reused = world.create();
        dead.add(reused);
        world.flush();
        assertEquals(0, query.getCount());
        assertEquals(0, alive.getCount());

        a.add(reused);
        world.flush();
        assertArrayEquals(new long[] {reused}, ids(query));
        assertEquals(0, alive.getCount());
    }

    /**
     * A query never lists pending objects, nor an object created and removed in the same step.
     */
    @Test
    void testPendingObjects()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final Query query = world.createQuery(a);
        final long pending = world.create();
        a.add(pending);
        assertEquals(0, query.getCount());

        final long transient0 = world.create();
        a.add(transient0);
        world.remove(transient0);
        world.flush();

        assertArrayEquals(new long[] {pending}, ids(query));
    }

    /**
     * A query created after objects exist lists the matching ones of the last flush, not the pending changes, and is
     * updated by the next flush.
     */
    @Test
    void testCreatedAfterObjects()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final TraitStorage<Health> b = world.register(new HealthType());
        final long[] objects = new long[10];
        for (int i = 0; i < objects.length; i++)
        {
            objects[i] = world.create();
            a.add(objects[i]);
            if (i % 2 == 0)
            {
                b.add(objects[i]);
            }
        }
        world.flush();
        final long late = world.create();
        a.add(late);
        b.add(late);
        b.remove(objects[0]);

        final Query query = world.createQuery(a, b);
        final Query notB = world.createQuery(new TraitStorage<?>[] {a}, new TraitStorage<?>[] {b});

        assertArrayEquals(new long[] {objects[0], objects[2], objects[4], objects[6], objects[8]}, sorted(query));
        assertArrayEquals(new long[] {objects[1], objects[3], objects[5], objects[7], objects[9]}, sorted(notB));
        world.flush();
        assertArrayEquals(new long[] {objects[2], objects[4], objects[6], objects[8], late}, sorted(query));
        assertEquals(6, notB.getCount());
    }

    /**
     * A query created after objects exist follows the dense order of its smallest include storage.
     */
    @Test
    void testCreatedAfterObjectsOrder()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final TraitStorage<Health> b = world.register(new HealthType());
        final long first = world.create();
        final long second = world.create();
        final long third = world.create();
        a.add(first);
        a.add(second);
        a.add(third);
        b.add(third);
        b.add(first);
        world.flush();

        assertArrayEquals(new long[] {third, first}, ids(world.createQuery(a, b)));
        assertArrayEquals(new long[] {third, first}, ids(world.createQuery(b, a)));
    }

    /**
     * Removals requested inside an iteration are deferred: every listed object is visited once, then the flush
     * applies them.
     */
    @Test
    void testRemovalDuringIteration()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final Query query = world.createQuery(a);
        for (int i = 0; i < 20; i++)
        {
            a.add(world.create()).points = i;
        }
        world.flush();
        final long[] before = ids(query);

        final List<Long> visited = new ArrayList<>();
        for (int i = 0; i < query.getCount(); i++)
        {
            final long id = query.getId(i);
            visited.add(Long.valueOf(id));
            if (a.get(id).points % 2 == 0)
            {
                world.remove(id);
            }
            else
            {
                a.remove(id);
            }
        }

        assertEquals(before.length, visited.size());
        assertArrayEquals(before, ids(query));
        world.flush();
        assertEquals(0, query.getCount());
    }

    /**
     * Additions requested inside an iteration are deferred: the iteration does not see them, the next one does.
     */
    @Test
    void testAdditionDuringIteration()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final TraitStorage<Health> b = world.register(new HealthType());
        final Query query = world.createQuery(a);
        final Query withB = world.createQuery(a, b);
        for (int i = 0; i < 5; i++)
        {
            a.add(world.create());
        }
        world.flush();

        int visited = 0;
        for (int i = 0; i < query.getCount(); i++)
        {
            final long child = world.create();
            a.add(child);
            b.add(query.getId(i));
            visited++;
        }

        assertEquals(5, visited);
        assertEquals(5, query.getCount());
        assertEquals(0, withB.getCount());
        world.flush();
        assertEquals(10, query.getCount());
        assertEquals(5, withB.getCount());
    }

    /**
     * The order is the same for two identical operation sequences, and is not the creation order once objects are
     * removed.
     */
    @Test
    void testDeterministicOrder()
    {
        final long[] first = run();
        final long[] second = run();

        assertArrayEquals(first, second);
        final long[] ascending = first.clone();
        Arrays.sort(ascending);
        assertNotEquals(Arrays.toString(ascending), Arrays.toString(first));
    }

    /**
     * Runs a fixed operation sequence with removals.
     *
     * @return The iteration order of the query at the end.
     */
    private static long[] run()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final TraitStorage<Health> b = world.register(new HealthType());
        final Query query = world.createQuery(new TraitStorage<?>[] {a}, new TraitStorage<?>[] {b});
        final long[] objects = new long[50];
        for (int step = 0; step < 5; step++)
        {
            for (int i = 0; i < objects.length; i++)
            {
                if (objects[i] != World.NO_OBJECT && (i + step) % 3 == 0)
                {
                    world.remove(objects[i]);
                    objects[i] = World.NO_OBJECT;
                }
                else if (objects[i] == World.NO_OBJECT)
                {
                    objects[i] = world.create();
                    a.add(objects[i]);
                }
                else if ((i + step) % 5 == 0)
                {
                    b.add(objects[i]);
                }
            }
            world.flush();
        }
        return ids(query);
    }

    /**
     * The sorted copy fills an exact or a larger buffer in ascending order and leaves the rest of it unchanged.
     */
    @Test
    void testCopySortedIds()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final Query query = world.createQuery(a);
        final long[] objects = new long[8];
        for (int i = 0; i < objects.length; i++)
        {
            objects[i] = world.create();
            a.add(objects[i]);
        }
        world.flush();
        world.remove(objects[0]);
        world.remove(objects[3]);
        world.flush();
        final long reused = world.create();
        a.add(reused);
        world.flush();
        final long[] expected = {objects[1], objects[2], objects[4], objects[5], objects[6], objects[7], reused};
        Arrays.sort(expected);

        final long[] exact = new long[expected.length];
        assertEquals(expected.length, query.copySortedIds(exact));
        assertArrayEquals(expected, exact);

        final long[] larger = new long[expected.length + 2];
        Arrays.fill(larger, -7L);
        assertEquals(expected.length, query.copySortedIds(larger));
        assertArrayEquals(expected, Arrays.copyOf(larger, expected.length));
        assertEquals(-7L, larger[expected.length]);
        assertEquals(-7L, larger[expected.length + 1]);
    }

    /**
     * An undersized or {@code null} buffer is refused and left unchanged; an empty query copies nothing.
     */
    @Test
    void testCopySortedIdsUndersized()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final Query query = world.createQuery(a);
        assertEquals(0, query.copySortedIds(new long[0]));
        a.add(world.create());
        a.add(world.create());
        world.flush();

        final long[] small = {5L};
        assertThrows(IllegalArgumentException.class, () -> query.copySortedIds(small));
        assertArrayEquals(new long[] {5L}, small);
        assertThrows(IllegalArgumentException.class, () -> query.copySortedIds(null));
    }

    /**
     * The in-place sort orders arrays of every small length and random content like {@link Arrays#sort(long[])}.
     */
    @Test
    void testSort()
    {
        long seed = 0x9E3779B97F4A7C15L;
        for (int length = 0; length < 70; length++)
        {
            final long[] values = new long[length + 3];
            for (int i = 0; i < values.length; i++)
            {
                seed = seed * 6364136223846793005L + 1442695040888963407L;
                values[i] = (seed >>> 33) % 17;
            }
            final long[] expected = values.clone();
            Arrays.sort(expected, 0, length);

            Query.sort(values, length);

            assertArrayEquals(expected, values, () -> "Length " + values.length);
        }
    }

    /**
     * A query grows with the object table.
     */
    @Test
    void testGrowth()
    {
        final World world = new World(new WorldConfig(2, 2));
        final TraitStorage<Health> a = world.register(new HealthType());
        final Query query = world.createQuery(a);
        final long[] objects = new long[9];
        for (int i = 0; i < objects.length; i++)
        {
            objects[i] = world.create();
            a.add(objects[i]);
        }
        world.flush();
        assertEquals(objects.length, query.getCount());
        assertArrayEquals(objects, sorted(query));

        world.reserve(64);
        assertArrayEquals(objects, sorted(query));
        for (int i = 0; i < 40; i++)
        {
            a.add(world.create());
        }
        world.flush();
        assertEquals(objects.length + 40, query.getCount());
    }

    /**
     * Invalid sets and positions are refused.
     */
    @Test
    void testInvalidArguments()
    {
        final World world = new World();
        final World other = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final TraitStorage<Health> b = world.register(new HealthType());
        final TraitStorage<Health> foreign = other.register(new HealthType());
        final TraitStorage<?>[] empty = new TraitStorage<?>[0];
        final TraitStorage<?>[] onlyA = {a};
        final TraitStorage<?>[] withNull = {a, null};
        final TraitStorage<?>[] withForeign = {foreign};

        assertThrows(IllegalArgumentException.class, () -> world.createQuery((TraitStorage<?>[]) null));
        assertThrows(IllegalArgumentException.class, () -> world.createQuery(empty));
        assertThrows(IllegalArgumentException.class, () -> world.createQuery(onlyA, null));
        assertThrows(IllegalArgumentException.class, () -> world.createQuery(withNull));
        assertThrows(IllegalArgumentException.class, () -> world.createQuery(withForeign));
        assertThrows(IllegalArgumentException.class, () -> world.createQuery(onlyA, withForeign));
        assertThrows(IllegalArgumentException.class, () -> world.createQuery(onlyA, withNull));
        assertThrows(IllegalArgumentException.class, () -> world.createQuery(new TraitStorage<?>[] {a, b}, onlyA));
        assertEquals(0, world.getQueryCount());

        final Query query = world.createQuery(onlyA, empty);
        assertThrows(IllegalArgumentException.class, () -> query.getId(0));
        a.add(world.create());
        world.flush();
        assertThrows(IllegalArgumentException.class, () -> query.getId(-1));
        assertThrows(IllegalArgumentException.class, () -> query.getId(1));
        assertTrue(query.getId(0) > 0L);
    }

    /**
     * During a call of the change hook, the queries agree with the trait masks: an added trait is already listed, a
     * removed trait still is.
     */
    @Test
    void testQueriesAgreeWithHook()
    {
        final World world = new World();
        final TraitStorage<Health> a = world.register(new HealthType());
        final Query query = world.createQuery(a);
        final List<String> seen = new ArrayList<>();
        world.setHook(new ChangeHook()
        {
            @Override
            public void onTraitAdded(long objectId, int traitTypeId)
            {
                seen.add("added " + lists(query, objectId));
            }

            @Override
            public void onTraitRemoved(long objectId, int traitTypeId)
            {
                seen.add("removed " + lists(query, objectId));
            }

            @Override
            public void onDestroyed(long objectId)
            {
                seen.add("destroyed " + lists(query, objectId));
            }
        });
        final long id = world.create();
        a.add(id);
        world.flush();
        a.add(id);
        world.flush();
        world.remove(id);
        world.flush();

        assertEquals(List.of("added true", "removed true", "added true", "removed true", "destroyed false"), seen);
    }
}
