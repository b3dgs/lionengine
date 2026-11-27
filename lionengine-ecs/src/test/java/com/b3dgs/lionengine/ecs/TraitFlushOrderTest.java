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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Regression set of the flush order with object and trait commands mixed: one recorded order, effective changes only,
 * removal of all traits with their object, reproducibility (REQ-ECS-003, REQ-ECS-006, REQ-ECS-012, ADR-0030 decision 2,
 * ADR-0074, ADR-0075).
 */
final class TraitFlushOrderTest
{
    /** Steps of the reproducibility run. */
    private static final int STEPS = 300;
    /** Objects of the reproducibility run. */
    private static final int OBJECTS = 24;

    /**
     * Each change of a mixed log applies in recorded order and is reported once, as it happens.
     */
    @Test
    void testMixedOrder()
    {
        final World world = new World();
        final RecordingHook hook = new RecordingHook();
        world.setHook(hook);
        final TraitStorage<Health> health = world.register(new HealthType());
        final TraitStorage<Health> armor = world.register(new HealthType());
        final long a = world.create();
        health.add(a);
        armor.add(a);
        final long b = world.create();
        armor.add(b);
        health.remove(b);
        world.flush();

        assertEquals(List.of("created " + a,
                             "added " + a + " 0",
                             "added " + a + " 1",
                             "created " + b,
                             "added " + b + " 1"),
                     hook.take());

        health.add(b);
        armor.remove(a);
        world.remove(a);
        health.add(a);
        health.add(b);
        armor.remove(b);
        armor.add(b);
        world.flush();

        assertEquals(List.of("added " + b + " 0",
                             "removed " + a + " 1",
                             "removed " + a + " 0",
                             "destroyed " + a,
                             "removed " + b + " 0",
                             "added " + b + " 0",
                             "removed " + b + " 1",
                             "added " + b + " 1"),
                     hook.take());
        assertFalse(world.isAlive(a));
        assertTrue(health.has(b));
        assertTrue(armor.has(b));
    }

    /**
     * The removal of an object reports its traits by ascending type ID, then its destruction, whatever the order the
     * traits were added in.
     */
    @Test
    void testRemovalReportsTraitsByTypeId()
    {
        final World world = new World();
        final RecordingHook hook = new RecordingHook();
        world.setHook(hook);
        final int types = TraitMasks.TYPES_PER_WORD + 2;
        final List<TraitStorage<Health>> storages = new ArrayList<>();
        for (int t = 0; t < types; t++)
        {
            storages.add(world.register(new HealthType()));
        }
        final long id = world.create();
        for (int t = types - 1; t >= 0; t -= 2)
        {
            storages.get(t).add(id);
        }
        world.flush();
        hook.take();

        world.remove(id);
        world.flush();

        final List<String> expected = new ArrayList<>();
        for (int t = 1; t < types; t += 2)
        {
            expected.add("removed " + id + " " + t);
        }
        expected.add("destroyed " + id);
        assertEquals(expected, hook.take());
    }

    /**
     * Commands that change nothing report nothing: a removal of an absent trait, an addition to a removed object, a
     * second removal of an object.
     */
    @Test
    void testNoEffectNoReport()
    {
        final World world = new World();
        final RecordingHook hook = new RecordingHook();
        world.setHook(hook);
        final TraitStorage<Health> health = world.register(new HealthType());
        final long id = world.create();
        world.flush();
        hook.take();

        health.remove(id);
        world.remove(id);
        health.add(id);
        health.remove(id);
        world.remove(id);
        world.flush();

        assertEquals(List.of("destroyed " + id), hook.take());
    }

    /**
     * An addition then a removal of a trait in one step both apply and both are reported.
     */
    @Test
    void testAddThenRemoveReported()
    {
        final World world = new World();
        final RecordingHook hook = new RecordingHook();
        world.setHook(hook);
        final TraitStorage<Health> health = world.register(new HealthType());
        final long id = world.create();
        health.add(id);
        health.remove(id);
        world.flush();

        assertEquals(List.of("created " + id, "added " + id + " 0", "removed " + id + " 0"), hook.take());
        assertFalse(health.has(id));
    }

    /**
     * Two runs of the same operation sequence give the same changes, the same dense order and the same data
     * (REQ-DET-001).
     */
    @Test
    void testReproducible()
    {
        assertEquals(run(), run());
    }

    /**
     * Runs a sequence of object and trait changes over many steps.
     *
     * @return The changes reported, then the dense order and data of the last step.
     */
    private static List<String> run()
    {
        final World world = new World(new WorldConfig(4, 2));
        final RecordingHook hook = new RecordingHook();
        world.setHook(hook);
        final TraitStorage<Health> health = world.register(new HealthType());
        final TraitStorage<Health> armor = world.register(new HealthType());
        final long[] ids = new long[OBJECTS];
        final List<String> changes = new ArrayList<>();
        for (int step = 0; step < STEPS; step++)
        {
            final int slot = step * 7 % OBJECTS;
            if (ids[slot] == World.NO_OBJECT || !world.isAlive(ids[slot]))
            {
                ids[slot] = world.create();
                health.add(ids[slot]).points = step;
            }
            else if (step % 3 == 0)
            {
                world.remove(ids[slot]);
            }
            else if (armor.has(ids[slot]))
            {
                armor.remove(ids[slot]);
                health.add(ids[slot]).points = -step;
            }
            else
            {
                armor.add(ids[slot]).points = step;
            }
            world.flush();
            changes.addAll(hook.take());
        }
        for (int i = 0; i < health.getCount(); i++)
        {
            changes.add(health.getId(i) + "=" + health.getAt(i).points);
        }
        return changes;
    }
}
