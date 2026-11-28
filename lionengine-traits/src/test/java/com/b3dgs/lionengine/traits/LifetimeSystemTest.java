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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.ecs.ObjectListener;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * Tests of {@link LifetimeSystem}: expiry with the step and the removal deferred to the flush, at several step
 * durations (REQ-STD-006, REQ-STD-008, REQ-STD-011, ADR-0017, ADR-0034, ADR-0081).
 */
final class LifetimeSystemTest
{
    /** The world. */
    private final World world = new World();
    /** The lifetimes. */
    private final TraitStorage<Lifetime> lifetimes = world.register(Lifetime.TYPE);

    /**
     * An object of half a second lives until the step at which the count-down reaches zero, that is half the updates
     * per second (one more if the rounding leaves a remainder); a system after the slot still sees it in that step;
     * the flush removes it with its lifetime, reported once; an object without limit stays.
     *
     * @param updatesPerSecond The updates per second: steps of 1/30, 1/60 and 1/120 s.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testExpiry(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        final int[] destroyed = new int[1];
        world.addObjectListener(new ObjectListener()
        {
            @Override
            public void onDestroyed(long objectId)
            {
                destroyed[0]++;
            }
        });
        world.addSystem(new LifetimeSystem(world, lifetimes), LifetimeSystem.SLOT);
        final long id = world.create();
        lifetimes.add(id).setDuration(0.5);
        final long forever = world.create();
        lifetimes.add(forever);
        world.flush();
        final int expected = expiryStep(0.5, step);
        final boolean[] seenAfter = new boolean[1];
        world.addSystem(s -> seenAfter[0] = world.isAlive(id), LifetimeSystem.SLOT + 1);

        assertTrue(expected == updatesPerSecond / 2 || expected == updatesPerSecond / 2 + 1, () -> "" + expected);
        for (int i = 1; i < expected; i++)
        {
            world.update(step);
            assertTrue(world.isAlive(id), "Step " + i);
            assertTrue(lifetimes.get(id).getDuration() > 0.0);
        }
        world.update(step);

        assertTrue(seenAfter[0]);
        assertFalse(world.isAlive(id));
        assertTrue(world.isAlive(forever));
        assertEquals(1, destroyed[0]);
        assertEquals(1, world.getObjectCount());

        for (int i = 0; i < updatesPerSecond; i++)
        {
            world.update(step);
        }
        assertEquals(1, destroyed[0]);
        assertTrue(world.isAlive(forever));
        assertEquals(Double.POSITIVE_INFINITY, lifetimes.get(forever).getDuration());
    }

    /**
     * A zero duration expires at the first step; several objects expiring in the same step are all removed, the
     * removal requests made while the query is walked.
     */
    @Test
    void testSimultaneousExpiry()
    {
        final LifetimeSystem system = new LifetimeSystem(world, lifetimes);
        world.addSystem(system, LifetimeSystem.SLOT);
        final long[] ids = new long[5];
        for (int i = 0; i < ids.length; i++)
        {
            ids[i] = world.create();
            lifetimes.add(ids[i]).setDuration(i % 2 == 0 ? 0.0 : 0.1);
        }
        world.flush();

        world.update(0.05);
        assertEquals(2, world.getObjectCount());
        assertEquals(2, system.getQuery().getCount());
        assertTrue(world.isAlive(ids[1]));
        assertTrue(world.isAlive(ids[3]));

        world.update(0.05);
        assertEquals(0, world.getObjectCount());
        assertEquals(0, system.getQuery().getCount());
    }

    /**
     * An object carrying an excluded trait never expires (REQ-STD-011); its duration is not counted down.
     */
    @Test
    void testExcluded()
    {
        final TraitStorage<Object> proxies = world.register(new MarkerType());
        final LifetimeSystem system = new LifetimeSystem(world, lifetimes, new TraitStorage<?>[]
        {
            proxies
        });
        world.addSystem(system, LifetimeSystem.SLOT);
        final long proxy = world.create();
        lifetimes.add(proxy).setDuration(0.0);
        proxies.add(proxy);
        final long local = world.create();
        lifetimes.add(local).setDuration(0.0);
        world.flush();

        world.update(1.0 / 60.0);

        assertEquals(0, system.getQuery().getCount());
        assertTrue(world.isAlive(proxy));
        assertEquals(0.0, lifetimes.get(proxy).getDuration());
        assertFalse(world.isAlive(local));
    }

    /**
     * Invalid arguments are refused.
     */
    @Test
    void testInvalidArguments()
    {
        final TraitStorage<?>[] none = new TraitStorage<?>[0];
        final World other = new World();
        final TraitStorage<Lifetime> foreign = other.register(Lifetime.TYPE);

        assertThrows(IllegalArgumentException.class, () -> new LifetimeSystem(null, lifetimes));
        assertThrows(IllegalArgumentException.class, () -> new LifetimeSystem(world, null));
        assertThrows(IllegalArgumentException.class, () -> new LifetimeSystem(world, lifetimes, null));
        assertThrows(IllegalArgumentException.class, () -> new LifetimeSystem(world, foreign, none));
        assertThrows(IllegalArgumentException.class, () -> new LifetimeSystem(world, lifetimes, new TraitStorage<?>[]
        {
            lifetimes
        }));
    }

    /**
     * Returns the step at which a duration expires, by the subtraction the system does.
     *
     * @param duration The duration, in seconds.
     * @param step The step, in seconds.
     * @return The number of the expiring step, from 1.
     */
    static int expiryStep(double duration, double step)
    {
        double remaining = duration;
        int steps = 0;
        do
        {
            remaining -= step;
            steps++;
        }
        while (remaining > 0.0);
        return steps;
    }
}
