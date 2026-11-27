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
package com.b3dgs.lionengine.examples.ecs;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.TraitType;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;

/**
 * Spawns objects at random places with the world's random source (REQ-WORLD-001, REQ-DET-001, REQ-DET-003, ADR-0024,
 * ADR-0031, ADR-0078).
 *
 * <p>
 * The seed is part of the world configuration ({@link WorldConfig#randomSeed()}); the world creates its own generator
 * from it, and {@link SpawnSystem}, a game-defined system, draws from {@link World#getRandomSource()} during
 * {@link World#update(double)}. Two worlds with the same seed spawn the same objects at the same places; another seed
 * gives other places. {@link World#reseed(long)}, between two updates, restarts the sequence for a new round. Headless;
 * {@link #run(Log, long)} logs the spawns.
 * </p>
 *
 * @since 11.0.0
 */
public final class RandomSpawnExample
{
    /** The seed of the example. */
    public static final long SEED = 2026L;
    /** Objects spawned per round, one per step. */
    public static final int SPAWNS = 5;
    /** Width of the arena, in meters. */
    public static final double ARENA_WIDTH = 20.0;
    /** Height of the arena, in meters. */
    public static final double ARENA_HEIGHT = 10.0;
    /** The constant step, in seconds. */
    public static final double STEP_SECONDS = 1.0 / 60.0;
    /** Slot of {@link SpawnSystem}: game logic range. */
    public static final int SPAWN_SLOT = 500;
    /** The spot trait type: a new spot is at the origin, a removed spot is reset to it. */
    public static final TraitType<Spot> SPOT = new TraitType<>()
    {
        @Override
        public Spot create()
        {
            return new Spot();
        }

        @Override
        public void reset(Spot trait)
        {
            trait.set(0.0, 0.0);
        }
    };

    /**
     * Private constructor.
     */
    private RandomSpawnExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates the world of the example: room for one round, and the seed of its random source.
     *
     * @param seed The seed of the world's random source.
     * @return The world.
     */
    public static World createWorld(long seed)
    {
        return new World(new WorldConfig(SPAWNS, SPAWNS, seed));
    }

    /**
     * Plays one round: {@link #SPAWNS} steps of the spawn system, then reads the places in ascending ID order and
     * removes the objects. Called between two updates.
     *
     * @param world The world.
     * @param spots The spot storage of the world.
     * @param all The query of the spots of the world.
     * @param spawn The spawn system, registered in the world.
     * @return The places, x then y for each object, in meters.
     */
    public static double[] playRound(World world, TraitStorage<Spot> spots, Query all, SpawnSystem spawn)
    {
        spawn.restart();
        final long[] ids = new long[SPAWNS];
        for (int step = 0; step < SPAWNS; step++)
        {
            world.update(STEP_SECONDS);
        }
        final int count = all.copySortedIds(ids);
        final double[] places = new double[2 * count];
        for (int i = 0; i < count; i++)
        {
            final Spot spot = spots.get(ids[i]);
            places[2 * i] = spot.getX();
            places[2 * i + 1] = spot.getY();
            world.remove(ids[i]);
        }
        world.flush();
        return places;
    }

    /**
     * Runs the example: plays a round, reseeds the world with the same seed between two updates and plays the same
     * round again, logging the places.
     *
     * @param log The log, at level {@link LogLevel#INFO} or finer to see the spawns.
     * @param seed The seed of the world's random source.
     * @return The places of the second round, x then y for each object, in meters: equal to the first.
     */
    public static double[] run(Log log, long seed)
    {
        final World world = createWorld(seed);
        final TraitStorage<Spot> spots = world.register(SPOT);
        final SpawnSystem spawn = new SpawnSystem(world, spots, SPAWNS, ARENA_WIDTH, ARENA_HEIGHT);
        world.addSystem(spawn, SPAWN_SLOT);
        final Query all = world.createQuery(spots);

        final double[] first = playRound(world, spots, all, spawn);
        log(log, first);
        world.reseed(seed);
        final double[] second = playRound(world, spots, all, spawn);
        log(log, second);
        return second;
    }

    /**
     * Logs the places of a round.
     *
     * @param log The log.
     * @param places The places, x then y for each object.
     */
    private static void log(Log log, double[] places)
    {
        for (int i = 0; i < places.length; i += 2)
        {
            log.log(LogLevel.INFO, "Spawn at x:", places[i]);
            log.log(LogLevel.INFO, "Spawn at y:", places[i + 1]);
        }
    }
}
