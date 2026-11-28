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

import com.b3dgs.lionengine.ecs.CountingType;
import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * The benchmark scene of REQ-ECS-009 (ADR-0030, ADR-0034, ADR-0079, ADR-0081): a world of objects carrying the four
 * standard traits {@link Transformable}, {@link Movable}, {@link Gravity} and {@link Lifetime}, processed by the
 * standard systems at their slots, with a spawner keeping the population constant.
 *
 * <p>
 * It replaces the stand-in scene of the {@code ecs} tests (ADR-0079, decision 1; owner answer of 2026-11-28 recorded by
 * ADR-0081): each step runs five systems, as a game using these traits would register them:
 * </p>
 * <ul>
 * <li>{@link TransformCaptureSystem} (slot {@link Integer#MIN_VALUE}) captures the previous positions;</li>
 * <li>{@link GravitySystem} (2000) accelerates the velocities, never beyond the terminal velocity;</li>
 * <li>{@link MovementSystem} (3000) clamps the speeds and integrates the velocities;</li>
 * <li>{@link LifetimeSystem} (5000) counts the durations down and requests the removal of the expired objects;</li>
 * <li>a spawner at slot 6000 (late bookkeeping, ADR-0034) creates one object with the four traits for each removal of
 * the step, so the world holds the same population at every step.</li>
 * </ul>
 *
 * <p>
 * The spawner knows how many objects expire in each step without walking them: every duration is a whole number of
 * steps counted down by the same subtraction as {@link LifetimeSystem}, so the step at which each object expires is
 * computed when it is created and counted in a ring of due steps. Durations depend on the creation rank only (1 to
 * 100 steps for the first objects, 100 steps for the respawned ones), never on random draws or iteration order, so
 * about one object in a hundred is removed and created again each step and a world of {@code 10 * n} objects does
 * exactly ten times the work of a world of {@code n} objects (the scaling check). Every value here is benchmark data,
 * not an engine default.
 * </p>
 */
final class BenchmarkWorld
{
    /** The step, in seconds (60 updates per second, REQ-PERF-001). */
    static final double STEP = 1.0 / 60.0;
    /** The standard traits per object. */
    static final int TRAITS = 4;
    /** Lifetimes are 1 to this number of steps, so about one object in this number expires per step. */
    static final int LIFETIME_STEPS = 100;
    /** Spawner slot, in the late bookkeeping range after 5000 (ADR-0034). */
    private static final int SPAWNER_SLOT = 6000;
    /** Due steps kept in the ring, more than the longest lifetime in steps. */
    private static final int RING = 128;
    /** Gravity acceleration, in m/s<sup>2</sup>. */
    private static final double ACCELERATION = -9.81;
    /** Terminal velocity, in m/s. */
    private static final double TERMINAL_VELOCITY = 20.0;
    /** Maximum speed, in m/s. */
    private static final double MAX_SPEED = 15.0;
    /** Object size, in meters. */
    private static final double SIZE = 1.0;
    /** Positions spread on rows of this number of objects. */
    private static final int ROW = 100;

    /** The world. */
    private final World world;
    /** The counting trait types, in registration order. */
    private final CountingType<?>[] types;
    /** Transformable storage. */
    private final TraitStorage<Transformable> transformables;
    /** Movable storage. */
    private final TraitStorage<Movable> movables;
    /** Gravity storage. */
    private final TraitStorage<Gravity> gravities;
    /** Lifetime storage. */
    private final TraitStorage<Lifetime> lifetimes;
    /** The queries of the four standard systems, in slot order. */
    private final Query[] queries;
    /** Objects expiring at each step, by step modulo {@link #RING}. */
    private final int[] due = new int[RING];
    /** Steps after which a respawned object expires. */
    private final int respawnSteps;
    /** The population. */
    private final int population;
    /** Steps run. */
    private long step;
    /** Creation rank of the next object. */
    private long rank;
    /** Objects visited by the standard systems. */
    private long visits;
    /** Objects created by the spawner, the population included. */
    private long spawned;

    /**
     * Creates the scene: the world with the default configuration, the four storages and the five systems; no object
     * yet.
     *
     * @param population The objects kept alive at every step.
     */
    BenchmarkWorld(int population)
    {
        this.population = population;
        world = new World();
        final CountingType<Transformable> transformableType = new CountingType<>(Transformable.TYPE);
        final CountingType<Movable> movableType = new CountingType<>(Movable.TYPE);
        final CountingType<Gravity> gravityType = new CountingType<>(Gravity.TYPE);
        final CountingType<Lifetime> lifetimeType = new CountingType<>(Lifetime.TYPE);
        types = new CountingType<?>[]
        {
            transformableType, movableType, gravityType, lifetimeType
        };
        transformables = world.register(transformableType);
        movables = world.register(movableType);
        gravities = world.register(gravityType);
        lifetimes = world.register(lifetimeType);
        final TransformCaptureSystem capture = new TransformCaptureSystem(world, transformables);
        final GravitySystem gravity = new GravitySystem(world, movables, gravities);
        final MovementSystem movement = new MovementSystem(world, transformables, movables);
        final LifetimeSystem lifetime = new LifetimeSystem(world, lifetimes);
        queries = new Query[]
        {
            capture.getQuery(), gravity.getQuery(), movement.getQuery(), lifetime.getQuery()
        };
        world.addSystem(capture, TransformCaptureSystem.SLOT);
        world.addSystem(gravity, GravitySystem.SLOT);
        world.addSystem(movement, MovementSystem.SLOT);
        world.addSystem(lifetime, LifetimeSystem.SLOT);
        world.addSystem(s -> spawn(), SPAWNER_SLOT);
        respawnSteps = expirySteps(LIFETIME_STEPS);
    }

    /**
     * Creates the population with its four traits and flushes; the first lifetimes span 1 to
     * {@link #LIFETIME_STEPS} steps by creation rank.
     */
    void populate()
    {
        for (int i = 0; i < population; i++)
        {
            final int steps = 1 + i % LIFETIME_STEPS;
            spawn(steps, expirySteps(steps));
        }
        world.flush();
    }

    /**
     * Runs steps: each one the five systems and one flush.
     *
     * @param steps The number of {@link World#update(double)} calls.
     */
    void run(int steps)
    {
        for (int i = 0; i < steps; i++)
        {
            world.update(STEP);
        }
    }

    /**
     * Returns the world.
     *
     * @return The world of the scene.
     */
    World getWorld()
    {
        return world;
    }

    /**
     * Returns the queries of the standard systems.
     *
     * @return The four queries, in slot order.
     */
    Query[] getQueries()
    {
        return queries.clone();
    }

    /**
     * Returns the trait types.
     *
     * @return The four counting types, in registration order.
     */
    CountingType<?>[] getTypes()
    {
        return types.clone();
    }

    /**
     * Returns the objects visited by the standard systems since the creation.
     *
     * @return The sum of the query counts iterated by the four standard systems.
     */
    long getVisits()
    {
        return visits;
    }

    /**
     * Returns the objects created by the spawner since the creation, the population included.
     *
     * @return The creations requested.
     */
    long getSpawned()
    {
        return spawned;
    }

    /**
     * Returns a checksum of the positions, so that the work is not optimized away.
     *
     * @return The sum of the vertical positions.
     */
    double getChecksum()
    {
        double sum = 0.0;
        final Query moved = queries[2];
        final int count = moved.getCount();
        for (int i = 0; i < count; i++)
        {
            sum += transformables.get(moved.getId(i)).getY();
        }
        return sum;
    }

    /**
     * Returns the steps after which a lifetime of a whole number of steps expires, by the subtraction of
     * {@link LifetimeSystem}.
     *
     * @param steps The lifetime, in steps.
     * @return The number of the expiring step, counted from the first step after the creation.
     */
    private static int expirySteps(int steps)
    {
        double remaining = steps * STEP;
        int count = 0;
        do
        {
            remaining -= STEP;
            count++;
        }
        while (remaining > 0.0);
        return count;
    }

    /**
     * The spawner: counts the objects visited by the standard systems in this step, then creates one new object, with
     * the longest lifetime, for each object expired in this step.
     */
    private void spawn()
    {
        for (final Query query : queries)
        {
            visits += query.getCount();
        }
        step++;
        final int slot = (int) (step % RING);
        final int expired = due[slot];
        due[slot] = 0;
        for (int i = 0; i < expired; i++)
        {
            spawn(LIFETIME_STEPS, respawnSteps);
        }
    }

    /**
     * Requests one object with its four traits.
     *
     * @param lifetimeSteps The lifetime, in steps.
     * @param expiry The steps after which it expires, counted from the next step.
     */
    private void spawn(int lifetimeSteps, int expiry)
    {
        final long id = world.create();
        final Transformable transformable = transformables.add(id);
        final double x = (double) (rank % ROW);
        final double y = (double) (rank / ROW % ROW);
        transformable.setPosition(x, y);
        transformable.setPreviousPosition(x, y);
        transformable.setSize(SIZE, SIZE);
        final Movable movable = movables.add(id);
        movable.setVelocity(rank % 2 == 0 ? 1.0 : -1.0, 0.0);
        movable.setMaxSpeed(MAX_SPEED);
        final Gravity gravity = gravities.add(id);
        gravity.setAcceleration(ACCELERATION);
        gravity.setTerminalVelocity(TERMINAL_VELOCITY);
        lifetimes.add(id).setDuration(lifetimeSteps * STEP);
        due[(int) ((step + expiry) % RING)]++;
        rank++;
        spawned++;
    }
}
