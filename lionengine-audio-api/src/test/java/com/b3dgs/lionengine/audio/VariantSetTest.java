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
package com.b3dgs.lionengine.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Tests the seeded selection of {@link VariantSet}: no immediate repeat, weights, exclusion, a single variant, the
 * same sequence for the same seed, and the composed sets (REQ-AUDIO-005, REQ-DET-001, ADR-0027, ADR-0033, ADR-0067).
 */
final class VariantSetTest
{
    /** Seed of the tests. */
    private static final long SEED = 2026L;
    /** Selections of the long runs. */
    private static final int DRAWS = 10_000;
    /** Format of the sounds. */
    private static final PcmFormat FORMAT = new PcmFormat(1_000, PcmFormat.MONO);
    /** Keys of the sounds. */
    private static final ResourceKey[] KEYS =
    {
        new ResourceKey("sounds/step1.wav"), new ResourceKey("sounds/step2.wav"), new ResourceKey("sounds/step3.wav")
    };

    /** The sounds, one per key. */
    private ShortSound[] sounds;

    /**
     * Loads the sounds.
     */
    @BeforeEach
    void prepare()
    {
        final FakeResourceProvider provider = new FakeResourceProvider(false);
        for (final ResourceKey key : KEYS)
        {
            provider.put(key, WavWriter.write(new PcmBuffer(FORMAT, new short[10]), 16));
        }
        final ShortSoundCache cache = new ShortSoundCache(new Resources(provider));
        sounds = new ShortSound[KEYS.length];
        for (int i = 0; i < KEYS.length; i++)
        {
            sounds[i] = cache.load(KEYS[i]);
        }
    }

    /**
     * Selects many times and counts each variant, checking the no-repeat rule on the way.
     *
     * @param set The random set.
     * @return The number of selections of each variant.
     */
    private static int[] count(VariantSet set)
    {
        final int[] counts = new int[set.size()];
        int previous = VariantSet.NO_INDEX;
        for (int i = 0; i < DRAWS; i++)
        {
            final int chosen = set.select();
            assertNotEquals(previous, chosen, "Immediate repeat at selection " + i);
            assertEquals(chosen, set.getLastIndex());
            counts[chosen]++;
            previous = chosen;
        }
        return counts;
    }

    /**
     * Equal weights: never the same variant twice in a row, each variant chosen about as often.
     */
    @Test
    void testNoImmediateRepeat()
    {
        final VariantSet set = VariantSet.random(SEED, sounds);

        assertFalse(set.isComposed());
        assertEquals(VariantSet.NO_INDEX, set.getLastIndex());
        assertEquals(1, set.getVoicesPerPlay());
        final int[] counts = count(set);

        for (final int value : counts)
        {
            assertTrue(value > DRAWS * 0.30 && value < DRAWS * 0.37, () -> "Count: " + value);
        }
    }

    /**
     * Weights {6, 1, 1}: with no repeat, the heavy variant is chosen 12 times in 26 and each light one 7 times in 26,
     * the stationary frequencies of the choice.
     */
    @Test
    void testWeights()
    {
        final VariantSet set = VariantSet.random(SEED, sounds, new double[]
        {
            6.0, 1.0, 1.0
        });

        final int[] counts = count(set);

        assertEquals(6.0, set.getWeight(0));
        assertTrue(Math.abs(counts[0] / (double) DRAWS - 12.0 / 26.0) < 0.02, () -> "Heavy: " + counts[0]);
        assertTrue(Math.abs(counts[1] / (double) DRAWS - 7.0 / 26.0) < 0.02, () -> "Light: " + counts[1]);
        assertTrue(Math.abs(counts[2] / (double) DRAWS - 7.0 / 26.0) < 0.02, () -> "Light: " + counts[2]);
    }

    /**
     * A weight of {@code 0} excludes its variant; the two others then alternate.
     */
    @Test
    void testZeroExcludes()
    {
        final VariantSet set = VariantSet.random(SEED, sounds, new double[]
        {
            1.0, 0.0, 3.0
        });

        final int[] counts = count(set);

        assertEquals(0, counts[1]);
        assertEquals(DRAWS / 2, counts[0]);
        assertEquals(DRAWS / 2, counts[2]);
        assertEquals(0.0, set.getWeight(1));
    }

    /**
     * A single variant, or a single variant of positive weight, is chosen every time: the no-repeat rule needs two.
     */
    @Test
    void testSingleVariant()
    {
        final VariantSet single = VariantSet.random(SEED, sounds[0]);
        final VariantSet weighted = VariantSet.random(SEED, sounds, new double[]
        {
            0.0, 2.0, 0.0
        });

        for (int i = 0; i < 10; i++)
        {
            assertEquals(0, single.select());
            assertEquals(1, weighted.select());
        }
        assertEquals(1, single.size());
        assertSame(sounds[0], single.getSound(0));
        assertEquals(VariantSet.DEFAULT_WEIGHT, single.getWeight(0));
    }

    /**
     * The same seed gives the same variants, another seed other variants: the set owns its generator.
     */
    @Test
    void testSeeded()
    {
        final int[] first = sequence(VariantSet.random(SEED, sounds));
        final int[] second = sequence(VariantSet.random(SEED, sounds));
        final int[] other = sequence(VariantSet.random(SEED + 1L, sounds));

        assertArrayEquals(first, second);
        assertFalse(Arrays.equals(first, other));
    }

    /**
     * Reference vectors: the first variants for the seed 2026, equal weights then weights {6, 1, 1}. The same values
     * are checked on Android API 21 by the device test, so that a seed gives the same variants on every platform.
     */
    @Test
    void testReferenceVector()
    {
        final int[] equal = new int[16];
        final int[] weighted = new int[16];
        final VariantSet uniform = VariantSet.random(SEED, sounds);
        final VariantSet heavy = VariantSet.random(SEED, sounds, new double[]
        {
            6.0, 1.0, 1.0
        });
        for (int i = 0; i < equal.length; i++)
        {
            equal[i] = uniform.select();
            weighted[i] = heavy.select();
        }

        assertArrayEquals(new int[]
        {
            1, 0, 2, 1, 2, 1, 2, 1, 2, 0, 2, 0, 2, 1, 0, 2
        }, equal);
        assertArrayEquals(new int[]
        {
            0, 1, 0, 2, 0, 2, 0, 2, 1, 0, 2, 0, 2, 0, 1, 0
        }, weighted);
    }

    /**
     * Two sets with the same seed choose the same variants whatever the other sets do: each set has its own
     * generator, shared with nobody.
     */
    @Test
    void testIndependentGenerators()
    {
        final VariantSet alone = VariantSet.random(SEED, sounds);
        final VariantSet interleaved = VariantSet.random(SEED, sounds);
        final VariantSet noise = VariantSet.random(SEED, sounds);

        for (int i = 0; i < 100; i++)
        {
            noise.select();
            noise.select();
            assertEquals(alone.select(), interleaved.select());
        }
    }

    /**
     * Selects a short sequence.
     *
     * @param set The random set.
     * @return The indices of 64 selections.
     */
    private static int[] sequence(VariantSet set)
    {
        final int[] indices = new int[64];
        for (int i = 0; i < indices.length; i++)
        {
            indices[i] = set.select();
        }
        return indices;
    }

    /**
     * A composed set holds its layers, asks one voice per layer and chooses nothing.
     */
    @Test
    void testComposed()
    {
        final VariantSet set = VariantSet.composed(sounds);

        assertTrue(set.isComposed());
        assertEquals(KEYS.length, set.size());
        assertEquals(KEYS.length, set.getVoicesPerPlay());
        assertEquals(VariantSet.NO_INDEX, set.getLastIndex());
        assertSame(sounds[2], set.getSound(2));
        assertEquals(VariantSet.DEFAULT_WEIGHT, set.getWeight(1));
        assertThrows(IndexOutOfBoundsException.class, () -> set.getWeight(KEYS.length));
        assertThrows(IndexOutOfBoundsException.class, () -> set.getWeight(-1));
    }

    /**
     * The set copies its arrays: changing them afterwards changes nothing.
     */
    @Test
    void testCopies()
    {
        final ShortSound[] variants = sounds.clone();
        final double[] weights =
        {
            1.0, 2.0, 3.0
        };
        final VariantSet set = VariantSet.random(SEED, variants, weights);
        final VariantSet layers = VariantSet.composed(variants);

        variants[0] = sounds[1];
        weights[0] = 0.0;

        assertSame(sounds[0], set.getSound(0));
        assertSame(sounds[0], layers.getSound(0));
        assertEquals(1.0, set.getWeight(0));
    }

    /**
     * Invalid arguments are refused.
     */
    @Test
    void testArguments()
    {
        final ShortSound[] none = new ShortSound[0];
        final ShortSound[] holed =
        {
            sounds[0], null
        };

        assertThrows(IllegalArgumentException.class, () -> VariantSet.random(SEED, (ShortSound[]) null));
        assertThrows(IllegalArgumentException.class, () -> VariantSet.random(SEED, none));
        assertThrows(IllegalArgumentException.class, () -> VariantSet.random(SEED, holed));
        assertThrows(IllegalArgumentException.class, () -> VariantSet.composed((ShortSound[]) null));
        assertThrows(IllegalArgumentException.class, () -> VariantSet.composed(none));
        assertThrows(IllegalArgumentException.class, () -> VariantSet.composed(holed));
        assertThrows(IllegalArgumentException.class, () -> VariantSet.random(SEED, sounds, null));
        assertWeightsRefused(1.0, 1.0);
        assertWeightsRefused(1.0, -1.0, 1.0);
        assertWeightsRefused(1.0, Double.NaN, 1.0);
        assertWeightsRefused(1.0, Double.POSITIVE_INFINITY, 1.0);
        assertWeightsRefused(0.0, 0.0, 0.0);
        assertWeightsRefused(Double.MAX_VALUE, Double.MAX_VALUE, 1.0);
    }

    /**
     * Checks that weights are refused for the three sounds.
     *
     * @param weights The weights.
     */
    private void assertWeightsRefused(double... weights)
    {
        assertThrows(IllegalArgumentException.class, () -> VariantSet.random(SEED, sounds, weights));
    }
}
