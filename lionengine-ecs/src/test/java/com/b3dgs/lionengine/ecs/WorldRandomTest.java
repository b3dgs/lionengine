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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.RandomSource;

/**
 * Tests of the random source of {@link World} (REQ-WORLD-001, REQ-DET-001, REQ-DET-003, ADR-0024, ADR-0031,
 * ADR-0078).
 */
final class WorldRandomTest
{
    /** Increment of SplitMix64, the same constant as the one of {@link RandomSource}. */
    private static final long GOLDEN_GAMMA = 0x9E37_79B9_7F4A_7C15L;
    /**
     * Seed whose SplitMix64 expansion is the xoshiro256** reference state of {@code RandomSourceTest}: the seed of its
     * SplitMix64 reference outputs plus 24 increments.
     */
    private static final long REFERENCE_SEED = 0x1a2b3c4d5e6f7531L + 24 * GOLDEN_GAMMA;
    /** The xoshiro256** reference outputs of {@code RandomSourceTest}, first ten. */
    private static final long[] REFERENCE_OUTPUTS = {
            0x462c422df780c48eL, 0xa82f1f6031c183e6L, 0x8a113820e8d2ca8dL, 0x1ac7023a26534958L,
            0xac8e41d0101e109cL, 0x46e34bc13edd63c4L, 0x3a26776adcd665c3L, 0x9ac6c9bea8fc518cL,
            0x1cef0aa07cc738c4L, 0x5136a5f070244b1dL,
    };
    /** Draws compared. */
    private static final int DRAWS = 100;
    /** The step, in seconds. */
    private static final double STEP = 1.0 / 60.0;

    /**
     * Draws values.
     *
     * @param random The generator.
     * @param count The number of values.
     * @return The values.
     */
    private static long[] draw(RandomSource random, int count)
    {
        final long[] values = new long[count];
        for (int i = 0; i < count; i++)
        {
            values[i] = random.nextLong();
        }
        return values;
    }

    /**
     * Creates a world of a seed.
     *
     * @param seed The seed.
     * @return The world.
     */
    private static World world(long seed)
    {
        return new World(new WorldConfig(WorldConfig.DEFAULT_OBJECT_CAPACITY,
                                         WorldConfig.DEFAULT_TRAIT_CAPACITY,
                                         seed));
    }

    /**
     * The default world draws the sequence of {@link WorldConfig#DEFAULT_RANDOM_SEED}, {@code 0x4C494F4E}; the
     * two-capacity configuration too.
     */
    @Test
    void testDefaultSeed()
    {
        final long[] expected = draw(new RandomSource(0x4C49_4F4EL), DRAWS);

        assertArrayEquals(expected, draw(new World().getRandomSource(), DRAWS));
        assertArrayEquals(expected, draw(new World(new WorldConfig(16, 16)).getRandomSource(), DRAWS));
    }

    /**
     * A custom seed gives the sequence of a generator of that seed.
     */
    @Test
    void testCustomSeed()
    {
        assertArrayEquals(draw(new RandomSource(-7L), DRAWS), draw(world(-7L).getRandomSource(), DRAWS));
        assertArrayEquals(draw(new RandomSource(0L), DRAWS), draw(world(0L).getRandomSource(), DRAWS));
    }

    /**
     * The world's generator follows the reference vectors of {@code RandomSourceTest} for the same seed.
     */
    @Test
    void testReferenceVectors()
    {
        assertArrayEquals(REFERENCE_OUTPUTS, draw(world(REFERENCE_SEED).getRandomSource(), REFERENCE_OUTPUTS.length));
    }

    /**
     * Two worlds of the same seed give the same sequence, of different seeds different ones; they never share a
     * generator, and drawing from one leaves the other unchanged.
     */
    @Test
    void testWorldsIndependent()
    {
        final World first = world(42L);
        final World second = world(42L);
        final World other = world(43L);

        assertNotSame(first.getRandomSource(), second.getRandomSource());
        assertSame(first.getRandomSource(), first.getRandomSource());
        final long[] values = draw(first.getRandomSource(), DRAWS);
        assertArrayEquals(values, draw(second.getRandomSource(), DRAWS));
        assertFalse(Arrays.equals(values, draw(other.getRandomSource(), DRAWS)));

        final World untouched = world(42L);
        draw(first.getRandomSource(), DRAWS);
        assertArrayEquals(values, draw(untouched.getRandomSource(), DRAWS));
    }

    /**
     * Reseeding at setup and between two updates restarts the sequence at once, on the same instance; the next update
     * draws from the new sequence.
     */
    @Test
    void testReseedBetweenUpdates()
    {
        final World world = world(1L);
        final RandomSource random = world.getRandomSource();
        final long[] drawn = new long[1];
        world.addSystem(step -> drawn[0] = random.nextLong(), 0);

        world.reseed(5L);
        assertArrayEquals(draw(new RandomSource(5L), 3), draw(random, 3));

        world.update(STEP);
        world.reseed(9L);
        world.update(STEP);
        assertEquals(new RandomSource(9L).nextLong(), drawn[0]);
        assertSame(random, world.getRandomSource());

        world.flush();
        world.reseed(9L);
        assertEquals(new RandomSource(9L).nextLong(), random.nextLong());
    }

    /**
     * Reseeding is refused during an update, during a flush and after a failure; the sequence is unchanged.
     */
    @Test
    void testReseedRefused()
    {
        final World world = world(3L);
        final RandomSource random = world.getRandomSource();
        final RandomSource expected = new RandomSource(3L);
        final GameSystem reseeding = step -> world.reseed(4L);
        world.addSystem(reseeding, 0);

        assertThrows(IllegalStateException.class, () -> world.update(STEP));
        assertThrows(IllegalStateException.class, () -> world.reseed(4L));
        assertEquals(expected.nextLong(), random.nextLong());

        final World flushing = world(3L);
        final RandomSource other = flushing.getRandomSource();
        final IllegalStateException[] refused = new IllegalStateException[1];
        flushing.addObjectListener(new ObjectListener()
        {
            @Override
            public void onCreated(long objectId)
            {
                refused[0] = assertThrows(IllegalStateException.class, () -> flushing.reseed(4L));
            }
        });
        flushing.create();
        flushing.flush();
        assertEquals(IllegalStateException.class, refused[0].getClass());
        assertEquals(new RandomSource(3L).nextLong(), other.nextLong());
    }

    /**
     * A listener may draw during the flush: its draws follow the flush order, so two identical runs give the same
     * values.
     */
    @Test
    void testListenerDraws()
    {
        assertArrayEquals(listenerRun(), listenerRun());
    }

    /**
     * Runs a world whose listener draws one value per creation.
     *
     * @return The values drawn.
     */
    private static long[] listenerRun()
    {
        final World world = world(11L);
        final long[] values = new long[8];
        final int[] count = new int[1];
        world.addObjectListener(new ObjectListener()
        {
            @Override
            public void onCreated(long objectId)
            {
                values[count[0]++] = world.getRandomSource().nextLong() ^ objectId;
            }
        });
        for (int i = 0; i < values.length; i++)
        {
            world.create();
        }
        world.flush();
        assertEquals(values.length, count[0]);
        return values;
    }

    /**
     * A system drawing numbers during {@link World#update(double)} gives the same world in two identical runs: same
     * objects, same traits, same trait values (REQ-DET-001).
     */
    @Test
    void testSystemDrawsReproducible()
    {
        final long[] first = systemRun(77L);

        assertArrayEquals(first, systemRun(77L));
        assertFalse(Arrays.equals(first, systemRun(78L)));
    }

    /**
     * Runs a world whose system creates, damages and removes objects at random.
     *
     * @param seed The seed.
     * @return The sorted IDs of the alive objects, each followed by its health points.
     */
    private static long[] systemRun(long seed)
    {
        final World world = new World(new WorldConfig(16, 16, seed));
        final TraitStorage<Health> health = world.register(new HealthType());
        final Query all = world.createQuery(health);
        final RandomSource random = world.getRandomSource();
        world.addSystem(step ->
        {
            final int count = all.getCount();
            for (int i = 0; i < count; i++)
            {
                final long id = all.getId(i);
                health.get(id).points -= random.nextInt(10);
                if (random.nextInt(4) == 0)
                {
                    world.remove(id);
                }
            }
            final int spawns = random.nextInt(4);
            for (int i = 0; i < spawns; i++)
            {
                health.add(world.create()).points = 50 + random.nextInt(50);
            }
        }, 0);
        for (int step = 0; step < 200; step++)
        {
            world.update(STEP);
        }
        final long[] ids = new long[world.getObjectCapacity()];
        final int count = all.copySortedIds(ids);
        final long[] state = new long[2 * count];
        for (int i = 0; i < count; i++)
        {
            state[2 * i] = ids[i];
            state[2 * i + 1] = health.get(ids[i]).points;
        }
        return state;
    }
}
