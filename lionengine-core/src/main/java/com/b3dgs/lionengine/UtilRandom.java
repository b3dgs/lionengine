/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Random;

/**
 * Random utility class implementation.
 */
public final class UtilRandom
{
    /** The random utility instance. */
    private static final Random RANDOM = new java.security.SecureRandom();

    /**
     * Set the seed.
     * 
     * @param seed The seed value.
     */
    public static void setSeed(long seed)
    {
        RANDOM.setSeed(seed);
    }

    /**
     * Get a random integer value from 0 and 2^32.
     * 
     * @return A value between 0 and 2^32.
     */
    public static int getRandomInteger()
    {
        return RANDOM.nextInt();
    }

    /**
     * Get a random value from 0 and a maximum.
     * 
     * @param max The maximum randomized value.
     * @return A value between 0 inclusive and max inclusive.
     */
    public static int getRandomInteger(int max)
    {
        return getRandomInteger(0, max);
    }

    /**
     * Get a random value from range.
     * 
     * @param range The range reference (must not be <code>null</code>).
     * @return A value between min inclusive and max inclusive.
     * @throws LionEngineException If invalid argument.
     */
    public static int getRandomInteger(Range range)
    {
        Check.notNull(range);

        return getRandomInteger(range.getMin(), range.getMax());
    }

    /**
     * Get a random value from an interval.
     * 
     * @param min The minimum value.
     * @param max The maximum value (positive and superior or equal to min).
     * @return A value between min inclusive and max inclusive.
     */
    public static int getRandomInteger(int min, int max)
    {
        Check.inferiorOrEqual(min, max);

        return min + RANDOM.nextInt(max + 1 - min);
    }

    /**
     * Get a random boolean value.
     * 
     * @return The next randomized boolean value.
     */
    public static boolean getRandomBoolean()
    {
        return RANDOM.nextBoolean();
    }

    /**
     * Get a random double value.
     * 
     * @return The next randomized double value (between 0.0 inclusive and 1.0 exclusive).
     */
    public static double getRandomDouble()
    {
        return RANDOM.nextDouble();
    }

    /**
     * Private constructor.
     */
    private UtilRandom()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
