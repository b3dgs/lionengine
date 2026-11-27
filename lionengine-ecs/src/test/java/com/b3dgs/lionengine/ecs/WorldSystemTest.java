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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests of the systems and of {@link World#update(double)}: slot order and ties, the standard slots with game systems
 * between them, the enabled flag, one flush per update after every system, the step, misuse and failure (REQ-ECS-006,
 * REQ-ECS-008, REQ-ECS-010, REQ-ECS-012, REQ-ECS-013, REQ-LOOP-010, ADR-0017, ADR-0030, ADR-0034, ADR-0077).
 */
final class WorldSystemTest
{
    /** The step of the tests, in seconds. */
    private static final double STEP = 0.01;

    /** The world. */
    private final World world = new World();
    /** The systems run, in order. */
    private final List<String> runs = new ArrayList<>();

    /**
     * Creates a system recording its runs under a name.
     *
     * @param name The name.
     * @return The system.
     */
    private GameSystem named(String name)
    {
        return step -> runs.add(name);
    }

    /**
     * Systems run from the lowest slot to the highest, systems of equal slots in registration order, whatever the
     * registration order of the slots; negative slots are allowed.
     */
    @Test
    void testSlotOrderAndTies()
    {
        world.addSystem(named("b1"), 20);
        world.addSystem(named("a"), 10);
        world.addSystem(named("c"), 30);
        world.addSystem(named("b2"), 20);
        world.addSystem(named("first"), -5);
        world.addSystem(named("b3"), 20);
        world.update(STEP);

        assertEquals(List.of("first", "a", "b1", "b2", "b3", "c"), runs);
        assertEquals(6, world.getSystemCount());
    }

    /**
     * Game systems in the recommended ranges run between the standard slots of ADR-0034 (REQ-ECS-013).
     */
    @Test
    void testGameSystemsBetweenStandardSlots()
    {
        world.addSystem(named("Lifetime"), 5000);
        world.addSystem(named("Collision"), 4000);
        world.addSystem(named("Movement"), 3000);
        world.addSystem(named("Gravity"), 2000);
        world.addSystem(named("Jump"), 1000);
        world.addSystem(named("bookkeeping"), 6000);
        world.addSystem(named("reaction"), 4500);
        world.addSystem(named("ai"), 1500);
        world.addSystem(named("input"), 500);
        world.update(STEP);

        assertEquals(List.of("input",
                             "Jump",
                             "ai",
                             "Gravity",
                             "Movement",
                             "Collision",
                             "reaction",
                             "Lifetime",
                             "bookkeeping"),
                     runs);
    }

    /**
     * Each system receives the constant step given to the update.
     */
    @Test
    void testStepGiven()
    {
        final double[] received = new double[1];
        world.addSystem(step -> received[0] = step, 0);
        world.update(1.0 / 60.0);

        assertEquals(1.0 / 60.0, received[0]);
    }

    /**
     * The enabled flag applies at the start of the next step: a system disabled by an earlier system still runs in the
     * current step; a disabled system is skipped until enabled again.
     */
    @Test
    void testEnabledFlagAtNextStep()
    {
        final GameSystem late = named("late");
        world.addSystem(step ->
        {
            runs.add("switch");
            world.setSystemEnabled(late, !world.isSystemEnabled(late));
        }, 0);
        world.addSystem(late, 1);
        assertTrue(world.isSystemEnabled(late));

        world.update(STEP);
        assertFalse(world.isSystemEnabled(late));
        world.update(STEP);
        world.update(STEP);

        assertEquals(List.of("switch", "late", "switch", "switch", "late"), runs);
    }

    /**
     * A flag set between two steps applies to the next one.
     */
    @Test
    void testEnabledFlagOutsideStep()
    {
        final GameSystem system = named("system");
        world.addSystem(system, 0);
        world.setSystemEnabled(system, false);
        world.update(STEP);
        world.setSystemEnabled(system, true);
        world.update(STEP);

        assertEquals(List.of("system"), runs);
    }

    /**
     * One flush per update, after every system: an object created and a trait added by a system are not visible to the
     * later systems of the same step, and are from the next step; the events fire after the last system.
     */
    @Test
    void testOneFlushAfterAllSystems()
    {
        final TraitStorage<Health> health = world.register(new HealthType());
        final Query query = world.createQuery(health);
        final long[] created = new long[1];
        world.addObjectListener(new ObjectListener()
        {
            @Override
            public void onCreated(long objectId)
            {
                runs.add("created");
            }
        });
        world.addSystem(step ->
        {
            runs.add("spawner " + query.getCount());
            if (created[0] == World.NO_OBJECT)
            {
                created[0] = world.create();
                health.add(created[0]);
            }
        }, 0);
        world.addSystem(step -> runs.add("reader " + query.getCount() + " " + world.isAlive(created[0])), 1);

        world.update(STEP);
        world.update(STEP);

        assertEquals(List.of("spawner 0", "reader 0 false", "created", "spawner 1", "reader 1 true"), runs);
    }

    /**
     * Removals requested by a system inside a query iteration apply at the end of the update; the iteration visits
     * every object (REQ-ECS-006).
     */
    @Test
    void testRemovalInsideIteration()
    {
        final TraitStorage<Health> health = world.register(new HealthType());
        final Query query = world.createQuery(health);
        for (int i = 0; i < 5; i++)
        {
            health.add(world.create());
        }
        world.flush();
        final int[] visited = new int[1];
        world.addSystem(step ->
        {
            for (int i = 0; i < query.getCount(); i++)
            {
                world.remove(query.getId(i));
                visited[0]++;
            }
        }, 0);
        world.update(STEP);

        assertEquals(5, visited[0]);
        assertEquals(0, query.getCount());
        assertEquals(0, world.getObjectCount());
    }

    /**
     * Commands recorded between two updates (inbox events, or a misbehaving renderer) are not applied before the next
     * update, so the state read between two updates is stable (REQ-ECS-010, REQ-LOOP-006).
     */
    @Test
    void testStateStableBetweenUpdates()
    {
        final TraitStorage<Health> health = world.register(new HealthType());
        final long id = world.create();
        health.add(id);
        world.update(STEP);
        world.remove(id);
        final long other = world.create();

        assertTrue(world.isAlive(id));
        assertFalse(world.isAlive(other));
        assertTrue(health.has(id));
        world.update(STEP);
        assertFalse(world.isAlive(id));
        assertTrue(world.isAlive(other));
    }

    /**
     * A world without systems only flushes.
     */
    @Test
    void testUpdateWithoutSystems()
    {
        final long id = world.create();
        world.update(STEP);

        assertTrue(world.isAlive(id));
    }

    /**
     * A step that is zero, negative, infinite or NaN is refused and nothing runs.
     */
    @Test
    void testInvalidStep()
    {
        world.addSystem(named("system"), 0);
        final long id = world.create();

        assertThrows(IllegalArgumentException.class, () -> world.update(0.0));
        assertThrows(IllegalArgumentException.class, () -> world.update(-0.0));
        assertThrows(IllegalArgumentException.class, () -> world.update(-STEP));
        assertThrows(IllegalArgumentException.class, () -> world.update(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> world.update(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> world.update(Double.NEGATIVE_INFINITY));
        assertTrue(runs.isEmpty());
        assertFalse(world.isAlive(id));
        world.update(Double.MIN_VALUE);
        assertEquals(List.of("system"), runs);
    }

    /**
     * Registration validation: {@code null}, a system registered twice, an unknown system.
     */
    @Test
    void testSystemValidation()
    {
        final GameSystem system = named("system");
        world.addSystem(system, 0);

        assertThrows(IllegalArgumentException.class, () -> world.addSystem(null, 0));
        assertThrows(IllegalArgumentException.class, () -> world.addSystem(system, 1));
        world.removeSystem(system);
        assertEquals(0, world.getSystemCount());
        assertThrows(IllegalArgumentException.class, () -> world.removeSystem(system));
        assertThrows(IllegalArgumentException.class, () -> world.removeSystem(null));
        assertThrows(IllegalArgumentException.class, () -> world.setSystemEnabled(system, true));
        assertThrows(IllegalArgumentException.class, () -> world.isSystemEnabled(null));
    }

    /**
     * A removed system no longer runs, the others keep their order; registered again, it goes after its ties.
     */
    @Test
    void testRemoveAndAddAgain()
    {
        final GameSystem a = named("a");
        world.addSystem(a, 0);
        world.addSystem(named("b"), 0);
        world.addSystem(named("c"), 1);
        world.removeSystem(a);
        world.update(STEP);
        world.addSystem(a, 0);
        world.update(STEP);

        assertEquals(List.of("b", "c", "b", "a", "c"), runs);
    }

    /**
     * Many systems grow the schedule and keep the order.
     */
    @Test
    void testGrowth()
    {
        for (int i = 40; i > 0; i--)
        {
            world.addSystem(named(Integer.toString(i)), i);
        }
        world.update(STEP);

        assertEquals(40, runs.size());
        assertEquals("1", runs.get(0));
        assertEquals("40", runs.get(39));
    }

    /**
     * A system must not flush, update, add or remove a system during the update; it may record changes and switch
     * flags.
     */
    @Test
    void testMisuseInsideUpdate()
    {
        final GameSystem other = named("other");
        world.addSystem(other, 10);
        final List<Class<?>> refused = new ArrayList<>();
        final List<Runnable> attempts = List.of(world::flush,
                                                () -> world.update(STEP),
                                                () -> world.addSystem(named("late"), 0),
                                                () -> world.removeSystem(other));
        world.addSystem(step ->
        {
            for (final Runnable attempt : attempts)
            {
                try
                {
                    attempt.run();
                }
                catch (final IllegalStateException exception)
                {
                    refused.add(exception.getClass());
                }
            }
        }, 0);
        world.update(STEP);

        assertEquals(attempts.size(), refused.size());
        assertEquals(2, world.getSystemCount());
        assertEquals(List.of("other"), runs);
    }

    /**
     * An exception of a system propagates; the later systems and the flush do not run; the world refuses every later
     * structural change, flush and update, while reads still work (REQ-LOOP-010).
     */
    @Test
    void testSystemExceptionPropagates()
    {
        final IllegalStateException failure = new IllegalStateException("system failure");
        final long id = world.create();
        world.addSystem(step ->
        {
            throw failure;
        }, 0);
        world.addSystem(named("later"), 1);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> world.update(STEP)));
        assertTrue(runs.isEmpty());
        assertFalse(world.isAlive(id));
        assertThrows(IllegalStateException.class, () -> world.update(STEP));
        assertThrows(IllegalStateException.class, world::flush);
        assertThrows(IllegalStateException.class, world::create);
        final GameSystem added = named("new");
        assertThrows(IllegalStateException.class, () -> world.addSystem(added, 0));
        assertEquals(0, world.getObjectCount());
    }

    /**
     * The same system may be registered in two worlds, each running it in its own updates.
     */
    @Test
    void testSystemInTwoWorlds()
    {
        final GameSystem system = named("system");
        final World second = new World();
        world.addSystem(system, 0);
        second.addSystem(system, 0);
        world.setSystemEnabled(system, false);
        world.update(STEP);
        second.update(STEP);

        assertEquals(List.of("system"), runs);
        assertTrue(second.isSystemEnabled(system));
    }
}
