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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests of the object and trait listeners: content and order of the events of the flush, several listeners,
 * registration inside a callback, refusal of structural changes by a listener, failure (REQ-ECS-007, REQ-ECS-012,
 * ADR-0018, ADR-0030, ADR-0034, ADR-0077).
 */
final class WorldEventTest
{
    /** The world. */
    private final World world = new World();
    /** The first trait type, type ID 0. */
    private final TraitStorage<Health> health = world.register(new HealthType());
    /** The second trait type, type ID 1. */
    private final TraitStorage<Health> armor = world.register(new HealthType());
    /** The third trait type, type ID 2. */
    private final TraitStorage<Health> shield = world.register(new HealthType());
    /** The recorder. */
    private final RecordingListener recorder = new RecordingListener();

    /**
     * Creates the test, the recorder listening.
     */
    WorldEventTest()
    {
        recorder.listen(world);
    }

    /**
     * A creation with traits fires the creation, then each addition in recorded order; the trait is readable and the
     * object alive during the calls.
     */
    @Test
    void testCreateAndAdd()
    {
        final List<String> seen = new ArrayList<>();
        world.addTraitListener(new TraitListener()
        {
            @Override
            public void onTraitAdded(long objectId, int traitTypeId)
            {
                final boolean has = health.has(objectId);
                seen.add(world.isAlive(objectId) + " " + has + " " + (has ? health.get(objectId).points : -1));
            }
        });
        final long id = world.create();
        armor.add(id);
        health.add(id).points = 7;
        world.flush();

        assertEquals(List.of("created " + id, "added " + id + " 1", "added " + id + " 0"), recorder.take());
        assertEquals(List.of("true false -1", "true true 7"), seen);
    }

    /**
     * A trait removal fires once, before the instance is reset, the trait still readable.
     */
    @Test
    void testRemoveTrait()
    {
        final long id = world.create();
        health.add(id).points = 5;
        world.flush();
        recorder.take();
        final List<String> seen = new ArrayList<>();
        world.addTraitListener(new TraitListener()
        {
            @Override
            public void onTraitRemoved(long objectId, int traitTypeId)
            {
                seen.add(health.has(objectId) + " " + health.get(objectId).points);
            }
        });
        health.remove(id);
        world.flush();

        assertEquals(List.of("removed " + id + " 0"), recorder.take());
        assertEquals(List.of("true 5"), seen);
        assertFalse(health.has(id));
    }

    /**
     * A replacement fires the removal of the old trait, then the addition of the new one; during the removal the old
     * instance is readable, during the addition the new one.
     */
    @Test
    void testReplacement()
    {
        final long id = world.create();
        health.add(id).points = 1;
        world.flush();
        recorder.take();
        final List<Integer> seen = new ArrayList<>();
        world.addTraitListener(new TraitListener()
        {
            @Override
            public void onTraitAdded(long objectId, int traitTypeId)
            {
                seen.add(Integer.valueOf(health.get(objectId).points));
            }

            @Override
            public void onTraitRemoved(long objectId, int traitTypeId)
            {
                seen.add(Integer.valueOf(health.get(objectId).points));
            }
        });
        health.add(id).points = 2;
        world.flush();

        assertEquals(List.of("removed " + id + " 0", "added " + id + " 0"), recorder.take());
        assertEquals(List.of(Integer.valueOf(1), Integer.valueOf(2)), seen);
    }

    /**
     * An addition then a removal recorded in one step both apply and both fire; so do a creation and a removal.
     */
    @Test
    void testAddThenRemoveInOneStep()
    {
        final long alive = world.create();
        world.flush();
        recorder.take();
        armor.add(alive);
        armor.remove(alive);
        final long brief = world.create();
        world.remove(brief);
        world.flush();

        assertEquals(List.of("added " + alive + " 1",
                             "removed " + alive + " 1",
                             "created " + brief,
                             "destroyed " + brief),
                     recorder.take());
        assertFalse(armor.has(alive));
    }

    /**
     * The removal of an object fires the removal of its traits by ascending type ID, then its destruction, the object
     * stale during the destruction call.
     */
    @Test
    void testObjectRemoval()
    {
        final long id = world.create();
        shield.add(id);
        health.add(id);
        armor.add(id);
        world.flush();
        recorder.take();
        final List<Boolean> alive = new ArrayList<>();
        world.addObjectListener(new ObjectListener()
        {
            @Override
            public void onDestroyed(long objectId)
            {
                alive.add(Boolean.valueOf(world.isAlive(objectId)));
            }
        });
        world.remove(id);
        world.flush();

        assertEquals(List.of("removed " + id + " 0", "removed " + id + " 1", "removed " + id + " 2", "destroyed " + id),
                     recorder.take());
        assertEquals(List.of(Boolean.FALSE), alive);
    }

    /**
     * Commands that change nothing fire nothing: a removal of an absent trait, a second removal of an object, an
     * addition and a removal on an object removed by an earlier command, and an empty flush.
     */
    @Test
    void testNoChangeFiresNothing()
    {
        final long id = world.create();
        world.flush();
        recorder.take();
        health.remove(id);
        world.remove(id);
        world.remove(id);
        armor.add(id);
        armor.remove(id);
        world.flush();
        world.flush();

        assertEquals(List.of("destroyed " + id), recorder.take());
    }

    /**
     * Several listeners are called in registration order, object and trait listeners alike, and a removed listener
     * receives nothing more.
     */
    @Test
    void testSeveralListenersInOrder()
    {
        final List<String> seen = new ArrayList<>();
        final ObjectListener first = new NamedListener("first", seen);
        final NamedListener second = new NamedListener("second", seen);
        world.addObjectListener(first);
        world.addObjectListener(second);
        world.addTraitListener(second);
        final long id = world.create();
        health.add(id);
        world.flush();
        world.removeObjectListener(first);
        world.remove(id);
        world.flush();

        assertEquals(List.of("first created", "second created", "second added", "second removed", "second destroyed"),
                     seen);
    }

    /**
     * A listener added during a delivery receives the events from the next one; a listener removed during a delivery
     * still receives the current event and nothing after it.
     */
    @Test
    void testRegistrationInsideCallback()
    {
        final List<String> seen = new ArrayList<>();
        final NamedListener late = new NamedListener("late", seen);
        final NamedListener leaving = new NamedListener("leaving", seen);
        world.addObjectListener(new ObjectListener()
        {
            @Override
            public void onCreated(long objectId)
            {
                if (seen.isEmpty())
                {
                    world.addObjectListener(late);
                    world.removeObjectListener(leaving);
                }
                seen.add("first created");
            }
        });
        world.addObjectListener(leaving);
        world.create();
        world.create();
        world.flush();

        assertEquals(List.of("first created", "leaving created", "first created", "late created"), seen);
    }

    /**
     * A listener removing itself during its call receives nothing after it.
     */
    @Test
    void testSelfRemoval()
    {
        final List<String> seen = new ArrayList<>();
        world.addTraitListener(new TraitListener()
        {
            @Override
            public void onTraitAdded(long objectId, int traitTypeId)
            {
                seen.add("added " + traitTypeId);
                world.removeTraitListener(this);
            }
        });
        final long id = world.create();
        health.add(id);
        armor.add(id);
        world.flush();

        assertEquals(List.of("added 0"), seen);
    }

    /**
     * Listener registration refuses {@code null}, a listener registered twice and the removal of an absent one.
     */
    @Test
    void testListenerValidation()
    {
        final ObjectListener object = new ObjectListener()
        {
            // Nothing.
        };
        final TraitListener trait = new TraitListener()
        {
            // Nothing.
        };
        world.addObjectListener(object);
        world.addTraitListener(trait);

        assertThrows(IllegalArgumentException.class, () -> world.addObjectListener(null));
        assertThrows(IllegalArgumentException.class, () -> world.addTraitListener(null));
        assertThrows(IllegalArgumentException.class, () -> world.addObjectListener(object));
        assertThrows(IllegalArgumentException.class, () -> world.addTraitListener(trait));
        world.removeObjectListener(object);
        world.removeTraitListener(trait);
        assertThrows(IllegalArgumentException.class, () -> world.removeObjectListener(object));
        assertThrows(IllegalArgumentException.class, () -> world.removeTraitListener(trait));
        assertThrows(IllegalArgumentException.class, () -> world.removeObjectListener(null));
        assertThrows(IllegalArgumentException.class, () -> world.removeTraitListener(null));
    }

    /**
     * Every structural change attempted by a listener during the flush throws {@link IllegalStateException}; reading
     * and changing trait data are allowed.
     */
    @Test
    void testListenerMustNotChangeWorld()
    {
        final long other = world.create();
        world.flush();
        final List<Class<?>> refused = new ArrayList<>();
        final List<Runnable> attempts = List.of(world::create,
                                                () -> world.remove(other),
                                                () -> health.add(other),
                                                () -> health.remove(other),
                                                world::flush,
                                                () -> world.update(1.0),
                                                () -> world.register(new HealthType()),
                                                () -> world.createQuery(health),
                                                () -> world.reserve(1),
                                                () -> health.reserve(1),
                                                () -> world.addSystem(step -> refused.add(Object.class), 0));
        world.addTraitListener(new TraitListener()
        {
            @Override
            public void onTraitAdded(long objectId, int traitTypeId)
            {
                health.get(objectId).points = 9;
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
            }
        });
        final long id = world.create();
        health.add(id);
        world.flush();

        assertEquals(attempts.size(), refused.size());
        assertEquals(9, health.get(id).points);
        assertEquals(0, world.getSystemCount());
        assertEquals(0, world.getQueryCount());
        world.remove(id);
        world.flush();
        assertFalse(world.isAlive(id));
        assertTrue(world.isAlive(other));
    }

    /**
     * An exception of a listener propagates out of the flush, the rest of the flush is not applied, and the world
     * refuses every later structural change, flush and update.
     */
    @Test
    void testListenerExceptionFailsWorld()
    {
        world.addObjectListener(new ObjectListener()
        {
            @Override
            public void onCreated(long objectId)
            {
                throw new IllegalStateException("listener failure");
            }
        });
        final long first = world.create();
        final long second = world.create();

        final IllegalStateException exception = assertThrows(IllegalStateException.class, world::flush);
        assertEquals("listener failure", exception.getMessage());
        assertTrue(world.isAlive(first));
        assertFalse(world.isAlive(second));
        assertThrows(IllegalStateException.class, world::flush);
        assertThrows(IllegalStateException.class, () -> world.update(1.0));
        assertThrows(IllegalStateException.class, world::create);
        assertThrows(IllegalStateException.class, () -> world.remove(first));
        assertThrows(IllegalStateException.class, () -> health.add(first));
    }

    /**
     * Two identical runs give the same events in the same order.
     */
    @Test
    void testReproducible()
    {
        assertEquals(run(), run());
    }

    /**
     * Runs a mixed sequence of steps in a new world and records its events.
     *
     * @return The events.
     */
    private static List<String> run()
    {
        final World run = new World(new WorldConfig(4, 4));
        final TraitStorage<Health> a = run.register(new HealthType());
        final TraitStorage<Health> b = run.register(new HealthType());
        final RecordingListener events = new RecordingListener();
        events.listen(run);
        final long[] ids = new long[8];
        for (int step = 0; step < 50; step++)
        {
            final int slot = step % ids.length;
            if (ids[slot] != World.NO_OBJECT && run.isAlive(ids[slot]))
            {
                if (step % 3 == 0)
                {
                    run.remove(ids[slot]);
                }
                else
                {
                    b.add(ids[slot]);
                    a.remove(ids[slot]);
                }
            }
            else
            {
                ids[slot] = run.create();
                a.add(ids[slot]);
            }
            run.update(1.0);
        }
        return events.take();
    }

    /**
     * A listener naming its events, recording them in a shared list.
     */
    private static final class NamedListener implements ObjectListener, TraitListener
    {
        /** The name. */
        private final String name;
        /** The events. */
        private final List<String> seen;

        /**
         * Creates the listener.
         *
         * @param name The name.
         * @param seen The events.
         */
        NamedListener(String name, List<String> seen)
        {
            this.name = name;
            this.seen = seen;
        }

        @Override
        public void onCreated(long objectId)
        {
            seen.add(name + " created");
        }

        @Override
        public void onDestroyed(long objectId)
        {
            seen.add(name + " destroyed");
        }

        @Override
        public void onTraitAdded(long objectId, int traitTypeId)
        {
            seen.add(name + " added");
        }

        @Override
        public void onTraitRemoved(long objectId, int traitTypeId)
        {
            seen.add(name + " removed");
        }
    }
}
