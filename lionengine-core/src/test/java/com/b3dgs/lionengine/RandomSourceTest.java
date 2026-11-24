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
package com.b3dgs.lionengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test {@link RandomSource} (REQ-DET-003).
 *
 * <p>
 * Reference outputs: produced by the C reference code of the algorithm authors (https://prng.di.unimi.it,
 * splitmix64.c and xoshiro256starstar.c), as published in the test suite of Apache Commons RNG
 * (SplitMix64Test and XoShiRo256StarStarTest, commons-rng-core).
 * </p>
 */
final class RandomSourceTest
{
    /** Seed of the SplitMix64 reference outputs. */
    private static final long SPLIT_MIX_SEED = 0x1a2b3c4d5e6f7531L;
    /** SplitMix64 reference outputs for {@link #SPLIT_MIX_SEED}. */
    private static final long[] SPLIT_MIX_OUTPUTS = {
            0x4141302768c9e9d0L, 0x64df48c4eab51b1aL, 0x4e723b53dbd901b3L, 0xead8394409dd6454L,
            0x3ef60e485b412a0aL, 0xb2a23aee63aecf38L, 0x6cc3b8933c4fa332L, 0x9c9e75e031e6fccbL,
            0x0fddffb161c9f30fL, 0x2d1d75d4e75c12a3L, 0xcdcf9d2dde66da2eL, 0x278ba7d1d142cfecL,
            0x4ca423e66072e606L, 0x8f2c3c46ebc70bb7L, 0xc9def3b1eeae3e21L, 0x8e06670cd3e98bceL,
            0x2326dee7dd34747fL, 0x3c8fff64392bb3c1L, 0xfc6aa1ebe7916578L, 0x3191fb6113694e70L,
            0x3453605f6544dac6L, 0x86cf93e5cdf81801L, 0x0d764d7e59f724dfL, 0xae1dfb943ebf8659L,
            0x012de1babb3c4104L, 0xa5a818b8fc5aa503L, 0xb124ea2b701f4993L, 0x18e0374933d8c782L,
            0x2af8df668d68ad55L, 0x76e56f59daa06243L, 0xf58c016f0f01e30fL, 0x8eeafa41683dbbf4L,
            0x7bf121347c06677fL, 0x4fd0c88d25db5ccbL, 0x99af3be9ebe0a272L, 0x94f2b33b74d0bdcbL,
            0x24b5d9d7a00a3140L, 0x79d983d781a34a3cL, 0x582e4a84d595f5ecL, 0x7316fe8b0f606d20L,
    };
    /** State of the xoshiro256** reference outputs: the SplitMix64 outputs 24 to 27 above. */
    private static final long[] XOSHIRO_STATE = {
            0x012de1babb3c4104L, 0xa5a818b8fc5aa503L, 0xb124ea2b701f4993L, 0x18e0374933d8c782L,
    };
    /** xoshiro256** reference outputs for {@link #XOSHIRO_STATE}. */
    private static final long[] XOSHIRO_OUTPUTS = {
            0x462c422df780c48eL, 0xa82f1f6031c183e6L, 0x8a113820e8d2ca8dL, 0x1ac7023a26534958L,
            0xac8e41d0101e109cL, 0x46e34bc13edd63c4L, 0x3a26776adcd665c3L, 0x9ac6c9bea8fc518cL,
            0x1cef0aa07cc738c4L, 0x5136a5f070244b1dL, 0x12e2e12edee691ffL, 0x28942b20799b71b4L,
            0xbe2d5c4267af2469L, 0x9dbec53728b2b9b7L, 0x893cf86611b14a96L, 0x712c226c79f066d6L,
            0x1a8a11ef81d2ac60L, 0x28171739ef8f2f46L, 0x073baa93525f8b1dL, 0xa73c7f3cb93df678L,
            0xae5633ab977a3531L, 0x25314041ba2d047eL, 0x31e6819dea142672L, 0x9479fa694f4c2965L,
            0xde5b771a968472b7L, 0xf0501965d9eeb4a3L, 0xef25a2a8ec90b911L, 0x1f58f71a75392659L,
            0x32d9547188781f3cL, 0x2d13b036ccf65bc0L, 0x289f9cc038dd952fL, 0x6ae2d5231e50824aL,
            0x75651acfb42ab170L, 0x7369aeb4f10056cfL, 0x0297ed632a97cf75L, 0x19f534c778015b72L,
            0x5d1d111c5ff182a8L, 0x861cdfe8e8014b96L, 0x07c6071e08112c83L, 0x15601582dcf4e4feL,
    };
    /** A bound for which a plain multiply-and-shift gives the values {@code 3k + 2} a probability of 1/4. */
    private static final int BIASED_BOUND = 3 << 29;
    /** Number of draws of the bias test. */
    private static final int DRAWS = 30_000;

    /**
     * SplitMix64 matches its reference outputs.
     */
    @Test
    void testSplitMix64()
    {
        long state = SPLIT_MIX_SEED;
        for (final long expected : SPLIT_MIX_OUTPUTS)
        {
            state += RandomSource.GOLDEN_GAMMA;
            assertEquals(expected, RandomSource.mix64(state));
        }
    }

    /**
     * xoshiro256** matches its reference outputs from an explicit state.
     */
    @Test
    void testXoshiro256StarStar()
    {
        final RandomSource random = new RandomSource(XOSHIRO_STATE[0],
                                                     XOSHIRO_STATE[1],
                                                     XOSHIRO_STATE[2],
                                                     XOSHIRO_STATE[3]);

        for (final long expected : XOSHIRO_OUTPUTS)
        {
            assertEquals(expected, random.nextLong());
        }
    }

    /**
     * A seed is expanded by SplitMix64: the seed whose first four SplitMix64 outputs are the xoshiro256** reference
     * state gives the xoshiro256** reference outputs.
     */
    @Test
    void testSeedExpansion()
    {
        final RandomSource random = new RandomSource(SPLIT_MIX_SEED + 24 * RandomSource.GOLDEN_GAMMA);

        for (final long expected : XOSHIRO_OUTPUTS)
        {
            assertEquals(expected, random.nextLong());
        }
    }

    /**
     * The same seed gives the same sequence; another seed another one.
     */
    @Test
    void testDeterminism()
    {
        final RandomSource first = new RandomSource(42L);
        final RandomSource second = new RandomSource(42L);
        final RandomSource other = new RandomSource(43L);

        for (int i = 0; i < 100; i++)
        {
            final long value = first.nextLong();
            assertEquals(value, second.nextLong());
            assertNotEquals(value, other.nextLong());
        }
    }

    /**
     * Derived values are exact functions of the next value of the sequence.
     */
    @Test
    void testDerivedValues()
    {
        final RandomSource random = new RandomSource(7L);
        final RandomSource reference = new RandomSource(7L);

        for (int i = 0; i < 1000; i++)
        {
            assertEquals((reference.nextLong() >>> 11) * 0x1.0p-53, random.nextDouble());
            assertEquals(reference.nextLong() < 0L, random.nextBoolean());
            assertEquals((int) (((reference.nextLong() >>> 32) * 6L) >>> 32), random.nextInt(6));
        }
    }

    /**
     * Doubles are in [0, 1) and booleans take both values.
     */
    @Test
    void testRanges()
    {
        final RandomSource random = new RandomSource(0L);
        int trueCount = 0;
        for (int i = 0; i < DRAWS; i++)
        {
            final double value = random.nextDouble();
            assertTrue(value >= 0.0 && value < 1.0, () -> "Out of range: " + value);
            if (random.nextBoolean())
            {
                trueCount++;
            }
        }
        assertTrue(trueCount > DRAWS * 0.45 && trueCount < DRAWS * 0.55, "True count: " + trueCount);
    }

    /**
     * Bounded integers stay in [0, bound).
     *
     * @param bound The bound.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 6, 100, 1 << 30, Integer.MAX_VALUE})
    void testBound(int bound)
    {
        final RandomSource random = new RandomSource(bound);
        for (int i = 0; i < 1000; i++)
        {
            final int value = random.nextInt(bound);
            assertTrue(value >= 0 && value < bound, () -> "Out of range: " + value);
        }
    }

    /**
     * No modulo bias: with a bound of 3 * 2^29, a biased multiply-and-shift would give the values {@code 3k + 2}
     * a frequency of 1/4; the rejection keeps it at 1/3.
     */
    @Test
    void testNoBias()
    {
        final RandomSource random = new RandomSource(2026L);
        int count = 0;
        for (int i = 0; i < DRAWS; i++)
        {
            if (random.nextInt(BIASED_BOUND) % 3 == 2)
            {
                count++;
            }
        }
        final double frequency = count / (double) DRAWS;
        assertTrue(frequency > 0.32 && frequency < 0.347, "Frequency: " + frequency);
    }

    /**
     * A bound must be positive.
     */
    @Test
    void testInvalidBound()
    {
        final RandomSource random = new RandomSource(1L);

        assertThrows(IllegalArgumentException.class, () -> random.nextInt(0));
        assertThrows(IllegalArgumentException.class, () -> random.nextInt(-1));
    }

    /**
     * An explicit state must not be all zero.
     */
    @Test
    void testZeroState()
    {
        assertThrows(IllegalArgumentException.class, () -> new RandomSource(0L, 0L, 0L, 0L));
    }
}
