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

import com.b3dgs.lionengine.RandomSource;
import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * The collision benchmark scene (REQ-STD-005, REQ-ECS-009, ADR-0079, ADR-0082): a world of moving objects carrying
 * {@link Transformable}, {@link Movable} and {@link Collidable}, bouncing in a square area, processed by the standard
 * systems at their slots.
 *
 * <p>
 * Kept apart from the scene of REQ-ECS-009 ({@link BenchmarkWorld}, 1,000 objects × 4 traits), which stays comparable
 * with its reference (owner answer of 2026-11-28 recorded by ADR-0082). Each step runs four systems:
 * </p>
 * <ul>
 * <li>{@link TransformCaptureSystem} (slot {@link Integer#MIN_VALUE}) captures the previous positions;</li>
 * <li>{@link MovementSystem} (3000) integrates the velocities;</li>
 * <li>a bounce at slot 3500 (a game system between the standard ones, ADR-0034) reverses a velocity leaving the
 * area;</li>
 * <li>{@link CollisionSystem} (4000), with the default cell size of 2 m and one listener counting the events.</li>
 * </ul>
 *
 * <p>
 * Objects are squares of 0.5 m at random places and velocities from a seeded {@link RandomSource}, in an area whose
 * side grows with the square root of the population, about 4 m² per object, so the density, and the pairs per object,
 * stay the same at every population. Every value here is benchmark data, not an engine default.
 * </p>
 */
final class CollisionBenchmarkWorld
{
    /** The step, in seconds (60 updates per second, REQ-PERF-001). */
    static final double STEP = 1.0 / 60.0;
    /** Bounce slot, between {@code Movement} and {@code Collision} (ADR-0034). */
    private static final int BOUNCE_SLOT = 3500;
    /** Area per object, in square meters. */
    private static final double AREA_PER_OBJECT = 4.0;
    /** Object size, in meters. */
    private static final double SIZE = 0.5;
    /** Highest speed on each axis, in m/s. */
    private static final double SPEED = 2.0;
    /** Seed of the scene. */
    private static final long SEED = 0x4C49_4F4EL;

    /** The world. */
    private final World world;
    /** Transformable storage. */
    private final TraitStorage<Transformable> transformables;
    /** Movable storage. */
    private final TraitStorage<Movable> movables;
    /** Collidable storage. */
    private final TraitStorage<Collidable> collidables;
    /** The bounced objects. */
    private final Query bounced;
    /** The collision system. */
    private final CollisionSystem collision;
    /** The side of the area, in meters. */
    private final double side;
    /** The population. */
    private final int population;
    /** Events received. */
    private long events;

    /**
     * Creates the scene: the world with the default configuration, the storages and the systems; no object yet.
     *
     * @param population The objects.
     */
    CollisionBenchmarkWorld(int population)
    {
        this.population = population;
        side = Math.sqrt(population * AREA_PER_OBJECT);
        world = new World();
        transformables = world.register(Transformable.TYPE);
        movables = world.register(Movable.TYPE);
        collidables = world.register(Collidable.TYPE);
        final MovementSystem movement = new MovementSystem(world, transformables, movables);
        bounced = movement.getQuery();
        collision = new CollisionSystem(world, transformables, collidables);
        collision.addListener(new CollisionListener()
        {
            @Override
            public void onEnter(long objectA, long objectB)
            {
                events++;
            }

            @Override
            public void onExit(long objectA, long objectB)
            {
                events++;
            }
        });
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(movement, MovementSystem.SLOT);
        world.addSystem(s -> bounce(), BOUNCE_SLOT);
        world.addSystem(collision, CollisionSystem.SLOT);
    }

    /**
     * Creates the population and flushes.
     */
    void populate()
    {
        final RandomSource random = new RandomSource(SEED);
        for (int i = 0; i < population; i++)
        {
            final long id = world.create();
            transformables.add(id).setPosition(random.nextDouble() * side, random.nextDouble() * side);
            movables.add(id).setVelocity(speed(random), speed(random));
            collidables.add(id).setSize(SIZE, SIZE);
        }
        world.flush();
    }

    /**
     * Returns the world.
     *
     * @return The world.
     */
    World getWorld()
    {
        return world;
    }

    /**
     * Returns the collision system.
     *
     * @return The system.
     */
    CollisionSystem getCollision()
    {
        return collision;
    }

    /**
     * Returns the events received.
     *
     * @return The enters and exits since the creation.
     */
    long getEvents()
    {
        return events;
    }

    /**
     * Draws a speed on one axis.
     *
     * @param random The random source.
     * @return A speed in [-{@link #SPEED}, {@link #SPEED}), in m/s.
     */
    private static double speed(RandomSource random)
    {
        return (random.nextDouble() * 2.0 - 1.0) * SPEED;
    }

    /**
     * Reverses the velocity of the objects leaving the area.
     */
    private void bounce()
    {
        final int count = bounced.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = bounced.getId(i);
            final Transformable transformable = transformables.get(id);
            final Movable movable = movables.get(id);
            double vx = movable.getVelocityX();
            double vy = movable.getVelocityY();
            if (transformable.getX() < 0.0 && vx < 0.0 || transformable.getX() > side && vx > 0.0)
            {
                vx = -vx;
            }
            if (transformable.getY() < 0.0 && vy < 0.0 || transformable.getY() > side && vy > 0.0)
            {
                vy = -vy;
            }
            movable.setVelocity(vx, vy);
        }
    }
}
