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

/**
 * The benchmark scene of REQ-ECS-009 (ADR-0030, ADR-0034, ADR-0079): a world of objects carrying four game-defined
 * stand-in traits shaped like the standard ones of {@code features/standard-traits.md}, processed by stand-in systems
 * at the standard slots, with a spawner keeping the population constant.
 *
 * <p>
 * The standard traits and systems do not exist yet (module {@code traits}); by the owner's answer of 2026-11-27 the
 * benchmark runs on these stand-ins and is measured again once {@code traits} exists. Each stand-in holds the fields
 * of its standard trait, in the same units, and each system does the work its standard system describes:
 * </p>
 * <ul>
 * <li>{@link Transformable}: center position, full size and previous position (m);</li>
 * <li>{@link Movable}: velocity and maximum speed (m/s); the movement system at slot 3000 captures the previous
 * position, clamps the speed and integrates the velocity with the step;</li>
 * <li>{@link Gravity}: acceleration (m/s<sup>2</sup>, negative Y pulls down) and terminal velocity (m/s, a magnitude);
 * the gravity system at slot 2000 accelerates the velocity, never beyond the terminal velocity;</li>
 * <li>{@link Lifetime}: remaining duration (s); the lifetime system at slot 5000 counts it down and requests the
 * removal of the object on expiry;</li>
 * <li>a spawner at slot 6000 (late bookkeeping, ADR-0034) creates one object with the four traits for each removal of
 * the step, so the world holds the same population at every step.</li>
 * </ul>
 *
 * <p>
 * Each step is three queries iterated by three systems, about one removal and one creation per hundred objects (a lifetime of 100 steps, counted down in floating point, may last one more step), and
 * one flush applying them. Durations depend on the creation rank only, never on random draws or iteration order, so a
 * world of {@code 10 * n} objects does exactly ten times the work of a world of {@code n} objects (the scaling check).
 * Every value here is benchmark data, not an engine default.
 * </p>
 */
final class BenchmarkWorld
{
    /** The step, in seconds (60 updates per second, REQ-PERF-001). */
    static final double STEP = 1.0 / 60.0;
    /** The stand-in traits per object. */
    static final int TRAITS = 4;
    /** Lifetimes are 1 to this number of steps, so about one object in this number expires per step. */
    static final int LIFETIME_STEPS = 100;
    /** Gravity system slot, the standard slot of {@code Gravity} (ADR-0034). */
    private static final int GRAVITY_SLOT = 2000;
    /** Movement system slot, the standard slot of {@code Movement} (ADR-0034). */
    private static final int MOVEMENT_SLOT = 3000;
    /** Lifetime system slot, the standard slot of {@code Lifetime} (ADR-0034). */
    private static final int LIFETIME_SLOT = 5000;
    /** Spawner slot, in the late bookkeeping range after 5000 (ADR-0034). */
    private static final int SPAWNER_SLOT = 6000;
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
    /** Transformable storage. */
    private final TraitStorage<Transformable> transformable;
    /** Movable storage. */
    private final TraitStorage<Movable> movable;
    /** Gravity storage. */
    private final TraitStorage<Gravity> gravity;
    /** Lifetime storage. */
    private final TraitStorage<Lifetime> lifetime;
    /** Objects with a velocity and a gravity. */
    private final Query falling;
    /** Objects with a position and a velocity. */
    private final Query moving;
    /** Objects with a lifetime. */
    private final Query expiring;
    /** The trait types, to count their pool creations and resets. */
    private final CountingType<?>[] types;
    /** The population. */
    private final int population;
    /** Objects removed in the current step, created again by the spawner. */
    private int expired;
    /** Creation rank of the next object. */
    private long rank;
    /** Objects visited by the systems. */
    private long visits;
    /** Objects created by the spawner. */
    private long spawned;

    /**
     * Creates the scene: the world with the default configuration, the four storages, the three queries and the four
     * systems; no object yet.
     *
     * @param population The objects kept alive at every step.
     */
    BenchmarkWorld(int population)
    {
        this.population = population;
        world = new World();
        final CountingType<Transformable> transformableType = new CountingType<>()
        {
            @Override
            Transformable newInstance()
            {
                return new Transformable();
            }

            @Override
            void clear(Transformable trait)
            {
                trait.x = 0.0;
                trait.y = 0.0;
                trait.width = 0.0;
                trait.height = 0.0;
                trait.previousX = 0.0;
                trait.previousY = 0.0;
            }
        };
        final CountingType<Movable> movableType = new CountingType<>()
        {
            @Override
            Movable newInstance()
            {
                return new Movable();
            }

            @Override
            void clear(Movable trait)
            {
                trait.velocityX = 0.0;
                trait.velocityY = 0.0;
                trait.maxSpeed = 0.0;
            }
        };
        final CountingType<Gravity> gravityType = new CountingType<>()
        {
            @Override
            Gravity newInstance()
            {
                return new Gravity();
            }

            @Override
            void clear(Gravity trait)
            {
                trait.acceleration = 0.0;
                trait.terminalVelocity = 0.0;
            }
        };
        final CountingType<Lifetime> lifetimeType = new CountingType<>()
        {
            @Override
            Lifetime newInstance()
            {
                return new Lifetime();
            }

            @Override
            void clear(Lifetime trait)
            {
                trait.duration = 0.0;
            }
        };
        types = new CountingType<?>[]
        {
            transformableType, movableType, gravityType, lifetimeType
        };
        transformable = world.register(transformableType);
        movable = world.register(movableType);
        gravity = world.register(gravityType);
        lifetime = world.register(lifetimeType);
        falling = world.createQuery(movable, gravity);
        moving = world.createQuery(transformable, movable);
        expiring = world.createQuery(lifetime);
        world.addSystem(this::fall, GRAVITY_SLOT);
        world.addSystem(this::move, MOVEMENT_SLOT);
        world.addSystem(this::expire, LIFETIME_SLOT);
        world.addSystem(step -> spawn(), SPAWNER_SLOT);
    }

    /**
     * Creates the population with its four traits and flushes; the first lifetimes span 1 to
     * {@link #LIFETIME_STEPS} steps by creation rank.
     */
    void populate()
    {
        for (int i = 0; i < population; i++)
        {
            spawn(1 + i % LIFETIME_STEPS);
        }
        world.flush();
    }

    /**
     * Runs steps: each one the three systems, the spawner and one flush.
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
     * Runs one step.
     */
    void step()
    {
        world.update(STEP);
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
     * Returns the queries.
     *
     * @return The three queries, in system order.
     */
    Query[] getQueries()
    {
        return new Query[]
        {
            falling, moving, expiring
        };
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
     * Returns the objects visited by the systems since the creation.
     *
     * @return The sum of the query counts iterated by the three systems.
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
        final int count = moving.getCount();
        for (int i = 0; i < count; i++)
        {
            sum += transformable.get(moving.getId(i)).y;
        }
        return sum;
    }

    /**
     * The gravity system: accelerates each velocity, never beyond the terminal velocity.
     *
     * @param step The step, in seconds.
     */
    private void fall(double step)
    {
        final int count = falling.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = falling.getId(i);
            final Movable m = movable.get(id);
            final Gravity g = gravity.get(id);
            m.velocityY = Math.max(m.velocityY + g.acceleration * step, -g.terminalVelocity);
        }
        visits += count;
    }

    /**
     * The movement system: captures the previous position, clamps the speed and integrates the velocity.
     *
     * @param step The step, in seconds.
     */
    private void move(double step)
    {
        final int count = moving.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = moving.getId(i);
            final Transformable t = transformable.get(id);
            final Movable m = movable.get(id);
            t.previousX = t.x;
            t.previousY = t.y;
            final double speed = Math.sqrt(m.velocityX * m.velocityX + m.velocityY * m.velocityY);
            if (speed > m.maxSpeed)
            {
                final double factor = m.maxSpeed / speed;
                m.velocityX *= factor;
                m.velocityY *= factor;
            }
            t.x += m.velocityX * step;
            t.y += m.velocityY * step;
        }
        visits += count;
    }

    /**
     * The lifetime system: counts down each duration and requests the removal on expiry, from inside the iteration.
     *
     * @param step The step, in seconds.
     */
    private void expire(double step)
    {
        final int count = expiring.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = expiring.getId(i);
            final Lifetime l = lifetime.get(id);
            l.duration -= step;
            if (l.duration <= 0.0)
            {
                world.remove(id);
                expired++;
            }
        }
        visits += count;
    }

    /**
     * The spawner: one new object for each removal of the step, with the longest lifetime.
     */
    private void spawn()
    {
        for (int i = 0; i < expired; i++)
        {
            spawn(LIFETIME_STEPS);
        }
        expired = 0;
    }

    /**
     * Requests one object with its four traits.
     *
     * @param lifetimeSteps The lifetime, in steps.
     */
    private void spawn(int lifetimeSteps)
    {
        final long id = world.create();
        final Transformable t = transformable.add(id);
        t.x = (double) (rank % ROW);
        t.y = (double) (rank / ROW % ROW);
        t.previousX = t.x;
        t.previousY = t.y;
        t.width = SIZE;
        t.height = SIZE;
        final Movable m = movable.add(id);
        m.velocityX = rank % 2 == 0 ? 1.0 : -1.0;
        m.maxSpeed = MAX_SPEED;
        final Gravity g = gravity.add(id);
        g.acceleration = ACCELERATION;
        g.terminalVelocity = TERMINAL_VELOCITY;
        lifetime.add(id).duration = lifetimeSteps * STEP;
        rank++;
        spawned++;
    }

    /**
     * Stand-in of the standard {@code Transformable}: center position, full size and previous position, in meters.
     */
    static final class Transformable
    {
        /** Center horizontal position. */
        double x;
        /** Center vertical position. */
        double y;
        /** Full width. */
        double width;
        /** Full height. */
        double height;
        /** Horizontal position at the step start. */
        double previousX;
        /** Vertical position at the step start. */
        double previousY;

        /**
         * Creates a trait in its reset state.
         */
        Transformable()
        {
            super();
        }
    }

    /**
     * Stand-in of the standard {@code Movable}: velocity and maximum speed, in m/s.
     */
    static final class Movable
    {
        /** Horizontal velocity. */
        double velocityX;
        /** Vertical velocity. */
        double velocityY;
        /** Maximum speed. */
        double maxSpeed;

        /**
         * Creates a trait in its reset state.
         */
        Movable()
        {
            super();
        }
    }

    /**
     * Stand-in of the standard {@code Gravity}: acceleration (m/s<sup>2</sup>) and terminal velocity (m/s).
     */
    static final class Gravity
    {
        /** Vertical acceleration, negative pulls down. */
        double acceleration;
        /** Terminal velocity, a magnitude. */
        double terminalVelocity;

        /**
         * Creates a trait in its reset state.
         */
        Gravity()
        {
            super();
        }
    }

    /**
     * Stand-in of the standard {@code Lifetime}: remaining duration, in seconds.
     */
    static final class Lifetime
    {
        /** Remaining duration. */
        double duration;

        /**
         * Creates a trait in its reset state.
         */
        Lifetime()
        {
            super();
        }
    }
}
