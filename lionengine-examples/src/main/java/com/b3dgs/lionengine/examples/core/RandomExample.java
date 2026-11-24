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
package com.b3dgs.lionengine.examples.core;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.RandomSource;

/**
 * Draws deterministic random numbers with the seeded random source (REQ-DET-003).
 *
 * <p>
 * A {@link RandomSource} created with the same seed gives the same values on every platform, which keeps a replay or
 * a test reproducible. Simulation code uses the world's generator only. Headless; {@link #main(String[])} logs the
 * rolls.
 * </p>
 *
 * @since 11.0.0
 */
public final class RandomExample
{
    /** Seed of the example. */
    public static final long SEED = 2026L;
    /** Number of faces of a die. */
    public static final int DIE_FACES = 6;

    /** Number of rolls logged by {@link #main(String[])}. */
    private static final int ROLLS = 5;

    /**
     * Private constructor.
     */
    private RandomExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Rolls six-sided dice.
     *
     * @param seed The seed: the same seed gives the same rolls.
     * @param count The number of rolls, not negative.
     * @return The rolls, each in {@code [1, 6]}.
     */
    public static int[] rollDice(long seed, int count)
    {
        final RandomSource random = new RandomSource(seed);
        final int[] rolls = new int[count];
        for (int i = 0; i < count; i++)
        {
            rolls[i] = 1 + random.nextInt(DIE_FACES);
        }
        return rolls;
    }

    /**
     * Runs the example: logs five rolls.
     *
     * @param args Unused.
     */
    // CHECKSTYLE IGNORE LINE: UncommentedMain
    public static void main(String[] args)
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);

        final int[] rolls = rollDice(SEED, ROLLS);
        for (int i = 0; i < rolls.length; i++)
        {
            log.log(LogLevel.INFO, "Roll:", rolls[i]);
        }
    }
}
