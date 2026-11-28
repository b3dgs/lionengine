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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.RandomSource;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.physics.Box;
import com.b3dgs.lionengine.physics.BroadPhase;

/**
 * Tests of {@link CollisionSystem}: pairs, layers and masks, enter and exit events and their order, removals, triggers,
 * excluded types, listeners, moving objects at several step durations and a random scene against a brute-force
 * reference (REQ-STD-005, REQ-STD-008, REQ-STD-010, REQ-STD-011, ADR-0030, ADR-0034, ADR-0082).
 */
final class CollisionSystemTest
{
    /** The step of the event tests, in seconds. */
    private static final double STEP = 1.0 / 60.0;
    /** Event prefix. */
    private static final String ENTER = "enter ";
    /** Event prefix. */
    private static final String EXIT = "exit ";

    /** The world. */
    private final World world = new World();
    /** The transformables. */
    private final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
    /** The movables. */
    private final TraitStorage<Movable> movables = world.register(Movable.TYPE);
    /** The collidables. */
    private final TraitStorage<Collidable> collidables = world.register(Collidable.TYPE);
    /** The recorded events. */
    private final RecordingCollisionListener listener = new RecordingCollisionListener();

    /**
     * The slot, the default cell size and the query: objects with both traits and no excluded trait (REQ-STD-011).
     */
    @Test
    void testQueryAndDefaults()
    {
        final TraitStorage<Object> proxies = world.register(new MarkerType());
        final CollisionSystem collision = new CollisionSystem(world, transformables, collidables, new TraitStorage<?>[]
        {
            proxies
        });
        world.addSystem(collision, CollisionSystem.SLOT);
        collision.addListener(listener);
        final long a = create(0.0, 0.0, 1.0);
        final long b = create(0.5, 0.0, 1.0);
        final long proxy = create(0.25, 0.0, 1.0);
        proxies.add(proxy);
        transformables.add(world.create());
        collidables.add(world.create()).setSize(1.0, 1.0);
        world.flush();

        world.update(STEP);

        assertEquals(4000, CollisionSystem.SLOT);
        assertEquals(BroadPhase.DEFAULT_CELL_SIZE, collision.getCellSize());
        assertEquals(2.0, new CollisionSystem(world, transformables, collidables).getCellSize());
        assertEquals(2, collision.getQuery().getCount());
        assertEquals(List.of(ENTER + a + " " + b), listener.take());
        assertEquals(1, collision.getPairCount());
        assertEquals(a, collision.getObjectA(0));
        assertEquals(b, collision.getObjectB(0));
    }

    /**
     * A pair is accepted only if each mask includes the other's layer, the same whatever the creation order.
     *
     * @param layerA The first layer.
     * @param maskA The first mask.
     * @param layerB The second layer.
     * @param maskB The second mask.
     * @param expected The expected pair.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "1, -1, 1, -1, true", "1, 2, 2, 1, true", "1, 2, 2, 2, false", "2, 2, 1, 2, false", "1, 1, 2, 1, false",
        "0, -1, 1, -1, false", "1, -1, 1, 0, false", "6, 1, 1, 4, true"
    })
    void testLayersAndMasks(int layerA, int maskA, int layerB, int maskB, boolean expected)
    {
        final CollisionSystem collision = addCollision(BroadPhase.DEFAULT_CELL_SIZE);
        final long a = create(0.0, 0.0, 1.0);
        final long b = create(0.0, 0.0, 1.0);
        collidables.get(a).setLayer(layerA);
        collidables.get(a).setMask(maskA);
        collidables.get(b).setLayer(layerB);
        collidables.get(b).setMask(maskB);

        world.update(STEP);

        assertEquals(expected ? 1 : 0, collision.getPairCount());
        if (expected)
        {
            assertEquals(List.of(ENTER + a + " " + b), listener.take());
        }
        else
        {
            assertTrue(listener.take().isEmpty());
        }
    }

    /**
     * In one step every exit is reported before every enter, each in ascending order of the pair, with
     * {@code objectA < objectB} whatever the order of the query.
     */
    @Test
    void testEventOrder()
    {
        addCollision(BroadPhase.DEFAULT_CELL_SIZE);
        final long[] ids = new long[5];
        for (int i = 0; i < ids.length; i++)
        {
            ids[i] = world.create();
        }
        for (int i = ids.length - 1; i >= 0; i--)
        {
            transformables.add(ids[i]);
            collidables.add(ids[i]).setSize(1.0, 1.0);
        }
        world.flush();
        transformables.get(ids[0]).setPosition(0.0, 0.0);
        transformables.get(ids[1]).setPosition(0.5, 0.0);
        transformables.get(ids[2]).setPosition(10.0, 0.0);
        transformables.get(ids[3]).setPosition(0.25, 0.0);
        transformables.get(ids[4]).setPosition(20.0, 0.0);

        world.update(STEP);

        assertEquals(List.of(pair(ENTER, ids[0], ids[1]), pair(ENTER, ids[0], ids[3]), pair(ENTER, ids[1], ids[3])),
                     listener.take());

        transformables.get(ids[3]).setPosition(10.25, 0.0);
        transformables.get(ids[4]).setPosition(0.75, 0.0);

        world.update(STEP);

        assertEquals(List.of(pair(EXIT, ids[0], ids[3]),
                             pair(EXIT, ids[1], ids[3]),
                             pair(ENTER, ids[0], ids[4]),
                             pair(ENTER, ids[1], ids[4]),
                             pair(ENTER, ids[2], ids[3])),
                     listener.take());

        world.update(STEP);

        assertTrue(listener.take().isEmpty());
    }

    /**
     * Boxes that only touch by an edge are not a pair; overlapping by any amount is.
     */
    @Test
    void testTouching()
    {
        final CollisionSystem collision = addCollision(BroadPhase.DEFAULT_CELL_SIZE);
        final long a = create(0.0, 0.0, 1.0);
        final long b = create(1.0, 0.0, 1.0);
        final long c = create(0.0, 1.0, 1.0);

        world.update(STEP);

        assertEquals(0, collision.getPairCount());

        transformables.get(c).setPosition(0.0, 0.999);
        world.update(STEP);

        assertEquals(List.of(ENTER + a + " " + c), listener.take());
        assertFalse(listener.take().contains(ENTER + a + " " + b));
    }

    /**
     * Removing an object, or its collidable, or adding an excluded trait, ends its pairs with an exit at the next step,
     * the removed object being stale then.
     */
    @Test
    void testExitOnRemoval()
    {
        final TraitStorage<Object> proxies = world.register(new MarkerType());
        final CollisionSystem collision = new CollisionSystem(world,
                                                              transformables,
                                                              collidables,
                                                              1.0,
                                                              new TraitStorage<?>[]
                                                              {
                                                                  proxies
                                                              });
        world.addSystem(collision, CollisionSystem.SLOT);
        final List<Boolean> alive = new ArrayList<>();
        collision.addListener(new CollisionListener()
        {
            @Override
            public void onExit(long objectA, long objectB)
            {
                alive.add(Boolean.valueOf(world.isAlive(objectA) && world.isAlive(objectB)));
            }
        });
        collision.addListener(listener);
        final long a = create(0.0, 0.0, 1.0);
        final long b = create(0.5, 0.0, 1.0);
        final long c = create(0.0, 0.5, 1.0);
        final long d = create(0.5, 0.5, 1.0);
        world.update(STEP);
        assertEquals(6, listener.take().size());

        world.remove(b);
        world.flush();
        world.update(STEP);

        assertEquals(List.of(pair(EXIT, a, b), pair(EXIT, b, c), pair(EXIT, b, d)), listener.take());
        assertEquals(List.of(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE), alive);

        collidables.remove(c);
        world.flush();
        world.update(STEP);

        assertEquals(List.of(pair(EXIT, a, c), pair(EXIT, c, d)), listener.take());

        proxies.add(d);
        world.flush();
        world.update(STEP);

        assertEquals(List.of(pair(EXIT, a, d)), listener.take());
        assertEquals(0, collision.getPairCount());
        assertEquals(1.0, collision.getCellSize());
    }

    /**
     * A trigger reports what enters and leaves it like any pair, and nothing is moved or stopped (detection only).
     */
    @Test
    void testTrigger()
    {
        addCollision(BroadPhase.DEFAULT_CELL_SIZE);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        final long zone = create(0.0, 0.0, 2.0);
        collidables.get(zone).setTrigger(true);
        final long runner = create(-3.0, 0.0, 1.0);
        movables.add(runner).setVelocity(60.0, 0.0);
        world.flush();

        world.update(STEP);
        world.update(STEP);
        world.update(STEP);

        assertEquals(List.of(ENTER + zone + " " + runner), listener.take());
        assertEquals(0.0, transformables.get(runner).getX(), 1e-12);
        assertEquals(0.0, transformables.get(zone).getX());

        world.update(STEP);
        world.update(STEP);

        assertEquals(List.of(EXIT + zone + " " + runner), listener.take());
        assertEquals(60.0, movables.get(runner).getVelocityX());
    }

    /**
     * Two objects moving toward each other enter at the first step their boxes overlap and exit at the first step
     * they no longer do, whatever the step duration (movement before collision); the steps are those of the same
     * {@code double} arithmetic.
     *
     * @param updatesPerSecond The updates per second: steps of 1/30, 1/60 and 1/120 s.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testMovingObjects(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        addCollision(BroadPhase.DEFAULT_CELL_SIZE);
        final long left = create(-5.0, 0.0, 1.0);
        movables.add(left).setVelocity(2.0, 0.0);
        final long right = create(5.0, 0.25, 0.5);
        movables.add(right).setVelocity(-1.0, 0.0);
        world.flush();
        double xLeft = -5.0;
        double xRight = 5.0;
        boolean overlapping = false;
        final List<String> expected = new ArrayList<>();
        final int steps = 6 * updatesPerSecond;
        for (int i = 1; i <= steps; i++)
        {
            xLeft += 2.0 * step;
            xRight += -1.0 * step;
            final boolean now = new Box(xLeft, 0.0, 1.0, 1.0).overlaps(new Box(xRight, 0.25, 0.5, 0.5));
            if (now != overlapping)
            {
                expected.add((now ? ENTER : EXIT) + i);
            }
            overlapping = now;
        }
        final List<String> events = new ArrayList<>();
        for (int i = 1; i <= steps; i++)
        {
            world.update(step);
            for (final String event : listener.take())
            {
                events.add(event.substring(0, event.indexOf(' ') + 1) + i);
                assertTrue(event.endsWith(left + " " + right), event);
            }
        }

        assertEquals(2, expected.size());
        assertEquals(expected, events);
        assertTrue(expected.get(0).startsWith(ENTER));
    }

    /**
     * A random moving scene of objects on random layers and masks, at several cell sizes: at each step the pairs are
     * those of the brute-force comparison, sorted, and the events are the differences with the previous step, exits
     * first.
     *
     * @param cellSize The cell size, in meters.
     */
    @ParameterizedTest
    @ValueSource(doubles =
    {
        0.5, 2.0, 8.0
    })
    void testAgainstBruteForce(double cellSize)
    {
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        final CollisionSystem collision = addCollision(cellSize);
        final RandomSource random = new RandomSource(0x4C49_4F4EL);
        final long[] ids = new long[150];
        for (int i = 0; i < ids.length; i++)
        {
            ids[i] = create(random.nextDouble() * 20.0, random.nextDouble() * 20.0, 0.25 + random.nextDouble());
            movables.add(ids[i]).setVelocity(random.nextDouble() * 4.0 - 2.0, random.nextDouble() * 4.0 - 2.0);
            collidables.get(ids[i]).setLayer(1 << random.nextInt(3));
            collidables.get(ids[i]).setMask(random.nextInt(8));
            collidables.get(ids[i]).setOffset(random.nextDouble() - 0.5, 0.0);
        }
        world.flush();
        List<String> previous = new ArrayList<>();
        for (int s = 0; s < 120; s++)
        {
            world.update(STEP);
            final List<String> expectedPairs = bruteForce(ids);
            final List<String> pairs = new ArrayList<>();
            for (int p = 0; p < collision.getPairCount(); p++)
            {
                pairs.add(collision.getObjectA(p) + " " + collision.getObjectB(p));
            }
            assertEquals(expectedPairs, pairs, "Step " + s);
            final List<String> expectedEvents = new ArrayList<>();
            for (final String old : previous)
            {
                if (!pairs.contains(old))
                {
                    expectedEvents.add(EXIT + old);
                }
            }
            for (final String now : pairs)
            {
                if (!previous.contains(now))
                {
                    expectedEvents.add(ENTER + now);
                }
            }
            assertEquals(expectedEvents, listener.take(), "Step " + s);
            previous = pairs;
        }
    }

    /**
     * A listener may record structural changes, applied at the flush; a listener added or removed during a delivery
     * takes effect from the next event.
     */
    @Test
    void testListenersDuringDelivery()
    {
        final CollisionSystem collision = addCollision(BroadPhase.DEFAULT_CELL_SIZE);
        final RecordingCollisionListener late = new RecordingCollisionListener();
        final long a = create(0.0, 0.0, 1.0);
        final long b = create(0.5, 0.0, 1.0);
        final long c = create(0.0, 0.5, 1.0);
        collision.addListener(new CollisionListener()
        {
            @Override
            public void onEnter(long objectA, long objectB)
            {
                if (objectB == b)
                {
                    collision.addListener(late);
                    world.remove(b);
                    assertTrue(world.isAlive(b));
                }
                else if (objectA == a)
                {
                    collision.removeListener(late);
                }
            }
        });
        world.flush();

        world.update(STEP);

        assertFalse(world.isAlive(b));
        assertEquals(List.of(pair(ENTER, a, b), pair(ENTER, a, c), pair(ENTER, b, c)), listener.take());
        assertEquals(List.of(pair(ENTER, a, c)), late.take());
    }

    /**
     * Listener registration and pair accessors check their arguments; invalid constructions are refused.
     */
    @Test
    void testInvalidArguments()
    {
        final CollisionSystem collision = addCollision(BroadPhase.DEFAULT_CELL_SIZE);
        final TraitStorage<?>[] none = new TraitStorage<?>[0];
        final World other = new World();
        final TraitStorage<Collidable> foreign = other.register(Collidable.TYPE);
        final RecordingCollisionListener unknown = new RecordingCollisionListener();

        assertThrows(IllegalArgumentException.class, () -> collision.addListener(null));
        assertThrows(IllegalArgumentException.class, () -> collision.addListener(listener));
        assertThrows(IllegalArgumentException.class, () -> collision.removeListener(unknown));
        assertThrows(IllegalArgumentException.class, () -> collision.getObjectA(0));
        assertThrows(IllegalArgumentException.class, () -> collision.getObjectB(-1));
        assertThrows(IllegalArgumentException.class, () -> new CollisionSystem(null, transformables, collidables));
        assertThrows(IllegalArgumentException.class, () -> new CollisionSystem(world, null, collidables));
        assertThrows(IllegalArgumentException.class, () -> new CollisionSystem(world, transformables, null));
        assertThrows(IllegalArgumentException.class, () -> new CollisionSystem(world, transformables, collidables, null));
        assertThrows(IllegalArgumentException.class, () -> new CollisionSystem(world, transformables, foreign, none));
        assertThrows(IllegalArgumentException.class,
                     () -> new CollisionSystem(world, transformables, collidables, 0.0, none));
        assertThrows(IllegalArgumentException.class,
                     () -> new CollisionSystem(world, transformables, collidables, Double.NaN, none));
        assertThrows(IllegalArgumentException.class,
                     () -> new CollisionSystem(world, transformables, collidables, new TraitStorage<?>[]
                     {
                         collidables
                     }));
        collision.removeListener(listener);
        final long a = create(0.0, 0.0, 1.0);
        create(0.0, 0.0, 1.0);
        world.update(STEP);
        assertTrue(listener.take().isEmpty());
        assertEquals(a, collision.getObjectA(0));
    }

    /**
     * Adds the collision system with the recording listener.
     *
     * @param cellSize The cell size.
     * @return The system.
     */
    private CollisionSystem addCollision(double cellSize)
    {
        final CollisionSystem collision = new CollisionSystem(world,
                                                              transformables,
                                                              collidables,
                                                              cellSize,
                                                              new TraitStorage<?>[0]);
        world.addSystem(collision, CollisionSystem.SLOT);
        collision.addListener(listener);
        return collision;
    }

    /**
     * Creates an object with a square collidable box, and flushes.
     *
     * @param x The center X, in meters.
     * @param y The center Y, in meters.
     * @param size The box size, in meters.
     * @return The object.
     */
    private long create(double x, double y, double size)
    {
        final long id = world.create();
        transformables.add(id).setPosition(x, y);
        collidables.add(id).setSize(size, size);
        world.flush();
        return id;
    }

    /**
     * Formats an event.
     *
     * @param kind The kind prefix.
     * @param a The lower ID.
     * @param b The higher ID.
     * @return The event text.
     */
    private static String pair(String kind, long a, long b)
    {
        return kind + a + " " + b;
    }

    /**
     * Compares every pair of objects, by their boxes and their layers and masks.
     *
     * @param ids The objects.
     * @return The accepted overlapping pairs, {@code "a b"} with {@code a < b}, sorted by {@code a} then {@code b}.
     */
    private List<String> bruteForce(long[] ids)
    {
        final List<long[]> pairs = new ArrayList<>();
        final Box first = new Box();
        final Box second = new Box();
        for (int i = 0; i < ids.length; i++)
        {
            collidables.get(ids[i]).toBox(transformables.get(ids[i]), first);
            for (int j = i + 1; j < ids.length; j++)
            {
                collidables.get(ids[j]).toBox(transformables.get(ids[j]), second);
                if (first.overlaps(second) && collidables.get(ids[i]).accepts(collidables.get(ids[j])))
                {
                    pairs.add(new long[]
                    {
                        Math.min(ids[i], ids[j]), Math.max(ids[i], ids[j])
                    });
                }
            }
        }
        pairs.sort((p, q) -> p[0] != q[0] ? Long.compare(p[0], q[0]) : Long.compare(p[1], q[1]));
        final List<String> texts = new ArrayList<>();
        for (final long[] pair : pairs)
        {
            texts.add(pair[0] + " " + pair[1]);
        }
        return texts;
    }
}
