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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.ecs.CountingType;
import com.b3dgs.lionengine.ecs.ObjectListener;
import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitListener;
import com.b3dgs.lionengine.ecs.World;

/**
 * Linear scaling of the benchmark step (REQ-ECS-009, ADR-0030, decision 3; ADR-0079; ADR-0081): from 1,000 to 10,000
 * objects, every operation count of the step grows exactly ten times. Counts, not time, so the check cannot flake.
 *
 * <p>
 * The counts are those the world produces for the scene: the objects visited through the four queries of the standard
 * systems, the creations and removals requested, the events delivered by the flush to an object listener and a trait
 * listener (one per effective change, so each command is applied once), and the creations and resets of the trait
 * pools (a steady step creates no instance and resets one instance per removed trait).
 * </p>
 */
final class BenchmarkScalingTest
{
    /** The smaller population. */
    private static final int SMALL = 1000;
    /** Ten times the smaller population. */
    private static final int FACTOR = 10;
    /** Warm-up steps, more than one full lifetime cycle. */
    private static final int WARMUP = 200;
    /** Measured steps. */
    private static final int STEPS = 500;

    /**
     * Every operation count at 10,000 objects is ten times the count at 1,000 objects, and the capacities stay under
     * twice the population (doubling growth).
     */
    @Test
    void testOperationCountsScaleLinearly()
    {
        final long[] small = count(SMALL);
        final long[] large = count(SMALL * FACTOR);

        for (int i = 0; i < small.length; i++)
        {
            final int index = i;
            assertEquals(small[i] * FACTOR, large[i], () -> "Count " + index);
        }
        assertTrue(small[0] > 0L);
        assertTrue(small[1] > 0L);
        assertTrue(small[2] > 0L);
        assertEquals(0L, small[small.length - 1]);
    }

    /**
     * Runs the scene and returns its operation counts over the measured steps.
     *
     * @param objects The population.
     * @return The visits, the creations requested, the created, destroyed, trait added and trait removed events, the
     *         resets and, last, the pool creations.
     */
    private static long[] count(int objects)
    {
        final BenchmarkWorld scene = new BenchmarkWorld(objects);
        final long[] events = new long[4];
        final World world = scene.getWorld();
        world.addObjectListener(new ObjectListener()
        {
            @Override
            public void onCreated(long objectId)
            {
                events[0]++;
            }

            @Override
            public void onDestroyed(long objectId)
            {
                events[1]++;
            }
        });
        world.addTraitListener(new TraitListener()
        {
            @Override
            public void onTraitAdded(long objectId, int traitTypeId)
            {
                events[2]++;
            }

            @Override
            public void onTraitRemoved(long objectId, int traitTypeId)
            {
                events[3]++;
            }
        });
        scene.populate();
        scene.run(WARMUP);
        final long[] start = snapshot(scene, events);
        scene.run(STEPS);
        final long[] end = snapshot(scene, events);

        assertEquals(objects, world.getObjectCount());
        assertTrue(world.getObjectCapacity() < 2 * (objects + objects / BenchmarkWorld.LIFETIME_STEPS));
        for (final Query query : scene.getQueries())
        {
            assertEquals(objects, query.getCount());
        }
        final long[] counts = new long[start.length];
        for (int i = 0; i < counts.length; i++)
        {
            counts[i] = end[i] - start[i];
        }
        return counts;
    }

    /**
     * Returns the current counters of the scene.
     *
     * @param scene The scene.
     * @param events The four event counters.
     * @return The counters, in the order of {@link #count(int)}.
     */
    private static long[] snapshot(BenchmarkWorld scene, long[] events)
    {
        long resets = 0L;
        long created = 0L;
        for (final CountingType<?> type : scene.getTypes())
        {
            resets += type.getResets();
            created += type.getCreated();
        }
        return new long[]
        {
            scene.getVisits(), scene.getSpawned(), events[0], events[1], events[2], events[3], resets, created
        };
    }
}
