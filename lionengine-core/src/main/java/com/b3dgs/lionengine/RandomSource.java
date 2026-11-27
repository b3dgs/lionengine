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

/**
 * Seeded random generator of the simulation: xoshiro256** seeded by SplitMix64 (ADR-0024, ADR-0031, ADR-0078,
 * REQ-DET-003).
 *
 * <p>
 * The seed is expanded by SplitMix64 into the 256-bit state of xoshiro256** 1.0 (Blackman and Vigna), which produces
 * the sequence. Integer operations only, so a seed gives the same sequence on every JVM and on Android; the
 * {@code double} values are exact conversions of integers. Neither {@code java.util.Random} nor
 * {@code SplittableRandom} is used. Not meant for security.
 * </p>
 *
 * <p>
 * Owned by one thread, usually the world on the runtime thread; not thread-safe. Allocates nothing.
 * </p>
 *
 * @since 11.0.0
 */
public final class RandomSource
{
    /** Increment of SplitMix64: the odd integer closest to 2^64 divided by the golden ratio. */
    static final long GOLDEN_GAMMA = 0x9E37_79B9_7F4A_7C15L;

    /** First multiplier of the SplitMix64 output function. */
    private static final long MIX_MULTIPLIER_1 = 0xBF58_476D_1CE4_E5B9L;
    /** Second multiplier of the SplitMix64 output function. */
    private static final long MIX_MULTIPLIER_2 = 0x94D0_49BB_1331_11EBL;
    /** First shift of the SplitMix64 output function. */
    private static final int MIX_SHIFT_1 = 30;
    /** Second shift of the SplitMix64 output function. */
    private static final int MIX_SHIFT_2 = 27;
    /** Third shift of the SplitMix64 output function. */
    private static final int MIX_SHIFT_3 = 31;
    /** Multiplier before the rotation of the xoshiro256** scrambler. */
    private static final long SCRAMBLE_MULTIPLIER_1 = 5L;
    /** Rotation of the xoshiro256** scrambler. */
    private static final int SCRAMBLE_ROTATION = 7;
    /** Multiplier after the rotation of the xoshiro256** scrambler. */
    private static final long SCRAMBLE_MULTIPLIER_2 = 9L;
    /** Shift of the xoshiro256** state update. */
    private static final int STATE_SHIFT = 17;
    /** Rotation of the xoshiro256** state update. */
    private static final int STATE_ROTATION = 45;
    /** Bits dropped from a {@code long} to get the 53 bits of a {@code double} mantissa. */
    private static final int DOUBLE_SHIFT = 11;
    /** Weight of the lowest of the 53 bits of a {@code double} in {@code [0, 1)}: 2^-53. */
    private static final double DOUBLE_UNIT = 0x1.0p-53;
    /** Number of bits of an {@code int}. */
    private static final int INT_BITS = 32;
    /** Mask of the low 32 bits of a {@code long}. */
    private static final long LOW_BITS = 0xFFFF_FFFFL;
    /** Number of 32-bit values: 2^32. */
    private static final long INT_VALUES = 1L << INT_BITS;

    /**
     * Returns the output of SplitMix64 for a state (Steele, Lea and Flood; reference code by Vigna). The generator
     * adds {@link #GOLDEN_GAMMA} to its state, then returns this function of the new state.
     *
     * @param state The state, already incremented.
     * @return The output.
     */
    static long mix64(long state)
    {
        long z = state;
        z = (z ^ z >>> MIX_SHIFT_1) * MIX_MULTIPLIER_1;
        z = (z ^ z >>> MIX_SHIFT_2) * MIX_MULTIPLIER_2;
        return z ^ z >>> MIX_SHIFT_3;
    }

    /** First word of the xoshiro256** state. */
    private long s0;
    /** Second word of the xoshiro256** state. */
    private long s1;
    /** Third word of the xoshiro256** state. */
    private long s2;
    /** Fourth word of the xoshiro256** state. */
    private long s3;

    /**
     * Creates a generator from a seed: the state is the first four outputs of SplitMix64 started at the seed, never
     * all zero.
     *
     * @param seed The seed; any value, {@code 0} included.
     */
    public RandomSource(long seed)
    {
        reseed(seed);
    }

    /**
     * Creates a generator from an explicit state, for the reference vectors of the algorithm.
     *
     * @param s0 The first state word.
     * @param s1 The second state word.
     * @param s2 The third state word.
     * @param s3 The fourth state word.
     * @throws IllegalArgumentException If the state is all zero.
     */
    RandomSource(long s0, long s1, long s2, long s3)
    {
        if ((s0 | s1 | s2 | s3) == 0L)
        {
            throw new IllegalArgumentException("The state must not be all zero");
        }
        this.s0 = s0;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
    }

    /**
     * Restarts the sequence from a seed, in place: the state becomes the one {@link #RandomSource(long)} gives for this
     * seed, so the next values are those of a new generator created with it. Allocates nothing; the references to this
     * generator stay valid (ADR-0078).
     *
     * <p>
     * The owner of the generator decides when the sequence restarts: a world reseeds its generator through
     * {@code World.reseed(long)}, which checks the phase of the world; code that borrows a generator does not reseed
     * it.
     * </p>
     *
     * @param seed The seed; any value, {@code 0} included.
     * @since 11.0.0
     */
    public void reseed(long seed)
    {
        long state = seed + GOLDEN_GAMMA;
        s0 = mix64(state);
        state += GOLDEN_GAMMA;
        s1 = mix64(state);
        state += GOLDEN_GAMMA;
        s2 = mix64(state);
        state += GOLDEN_GAMMA;
        s3 = mix64(state);
    }

    /**
     * Returns the next value of the sequence: 64 uniformly distributed bits.
     *
     * @return The value, any {@code long}.
     */
    public long nextLong()
    {
        final long result = Long.rotateLeft(s1 * SCRAMBLE_MULTIPLIER_1, SCRAMBLE_ROTATION) * SCRAMBLE_MULTIPLIER_2;
        final long t = s1 << STATE_SHIFT;
        s2 ^= s0;
        s3 ^= s1;
        s1 ^= s2;
        s0 ^= s3;
        s2 ^= t;
        s3 = Long.rotateLeft(s3, STATE_ROTATION);
        return result;
    }

    /**
     * Returns a value in {@code [0, bound)}, uniformly distributed, without modulo bias: the high 32 bits of a value
     * are multiplied by the bound, and the rare products that would favour some results are drawn again (Lemire's
     * method). Usually consumes one value of the sequence.
     *
     * @param bound The exclusive upper bound, positive.
     * @return The value, in {@code [0, bound)}.
     * @throws IllegalArgumentException If {@code bound} is not positive.
     */
    public int nextInt(int bound)
    {
        if (bound <= 0)
        {
            throw new IllegalArgumentException("Bound must be positive: " + bound);
        }
        long product = (nextLong() >>> INT_BITS) * bound;
        long low = product & LOW_BITS;
        if (low < bound)
        {
            final long threshold = (INT_VALUES - bound) % bound;
            while (low < threshold)
            {
                product = (nextLong() >>> INT_BITS) * bound;
                low = product & LOW_BITS;
            }
        }
        return (int) (product >>> INT_BITS);
    }

    /**
     * Returns a value in {@code [0, 1)}: the high 53 bits of the next value, times 2^-53.
     *
     * @return The value, in {@code [0, 1)}, a multiple of 2^-53.
     */
    public double nextDouble()
    {
        return (nextLong() >>> DOUBLE_SHIFT) * DOUBLE_UNIT;
    }

    /**
     * Returns a boolean: the highest bit of the next value.
     *
     * @return {@code true} or {@code false} with the same probability.
     */
    public boolean nextBoolean()
    {
        return nextLong() < 0L;
    }
}
