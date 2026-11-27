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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.RandomSource;
import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * Tests of {@link RandomSpawnExample}, {@link SpawnSystem} and {@link Spot}.
 */
final class RandomSpawnExampleTest
{
    /**
     * Plays one round in a new world.
     *
     * @param seed The seed.
     * @return The places.
     */
    private static double[] round(long seed)
    {
        final World world = RandomSpawnExample.createWorld(seed);
        final TraitStorage<Spot> spots = world.register(RandomSpawnExample.SPOT);
        final SpawnSystem spawn = new SpawnSystem(world,
                                                  spots,
                                                  RandomSpawnExample.SPAWNS,
                                                  RandomSpawnExample.ARENA_WIDTH,
                                                  RandomSpawnExample.ARENA_HEIGHT);
        world.addSystem(spawn, RandomSpawnExample.SPAWN_SLOT);
        final Query all = world.createQuery(spots);
        final double[] places = RandomSpawnExample.playRound(world, spots, all, spawn);
        assertEquals(RandomSpawnExample.SPAWNS, spawn.getSpawned());
        assertEquals(0, world.getObjectCount());
        return places;
    }

    /**
     * The places are drawn from the world's generator, a {@link RandomSource} of the configured seed, inside the
     * arena.
     */
    @Test
    void testPlacesFromTheWorldSeed()
    {
        final double[] places = round(RandomSpawnExample.SEED);
        final RandomSource expected = new RandomSource(RandomSpawnExample.SEED);

        assertEquals(2 * RandomSpawnExample.SPAWNS, places.length);
        for (int i = 0; i < places.length; i += 2)
        {
            assertEquals(expected.nextDouble() * RandomSpawnExample.ARENA_WIDTH, places[i]);
            assertEquals(expected.nextDouble() * RandomSpawnExample.ARENA_HEIGHT, places[i + 1]);
            assertTrue(places[i] >= 0.0 && places[i] < RandomSpawnExample.ARENA_WIDTH);
            assertTrue(places[i + 1] >= 0.0 && places[i + 1] < RandomSpawnExample.ARENA_HEIGHT);
        }
    }

    /**
     * Two worlds with the same seed spawn at the same places; another seed elsewhere.
     */
    @Test
    void testSameSeedSamePlaces()
    {
        final double[] first = round(RandomSpawnExample.SEED);

        assertArrayEquals(first, round(RandomSpawnExample.SEED));
        assertFalse(Arrays.equals(first, round(RandomSpawnExample.SEED + 1L)));
    }

    /**
     * The example runs: the round played after reseeding with the same seed repeats the first one.
     */
    @Test
    void testRun()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);

        assertArrayEquals(round(RandomSpawnExample.SEED), RandomSpawnExample.run(log, RandomSpawnExample.SEED));
    }
}
