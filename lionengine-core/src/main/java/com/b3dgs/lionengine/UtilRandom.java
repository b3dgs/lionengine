/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine;

/**
 * Random utility class implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilRandom
{
    /** The random utility instance. */
    private static final java.util.Random RANDOM = new java.util.Random();

    /**
     * Set the seed.
     * 
     * @param seed The seed value.
     */
    public static void setSeed(long seed)
    {
        UtilRandom.RANDOM.setSeed(seed);
    }

    /**
     * Get a random integer value from 0 and 2^32.
     * 
     * @return A value between 0 and 2^32.
     */
    public static int getRandomInteger()
    {
        return UtilRandom.RANDOM.nextInt();
    }

    /**
     * Get a random value from 0 and a maximum.
     * 
     * @param max The maximum randomized value.
     * @return A value between 0 inclusive and max inclusive.
     */
    public static int getRandomInteger(int max)
    {
        return UtilRandom.RANDOM.nextInt(max + 1);
    }

    /**
     * Get a random value from an interval.
     * 
     * @param min The minimum value (>= 0).
     * @param max The maximum value (>= 0 && >= min).
     * @return A value between min inclusive and max inclusive.
     */
    public static int getRandomInteger(int min, int max)
    {
        return min + UtilRandom.RANDOM.nextInt(max + 1 - min);
    }

    /**
     * Get a random boolean value.
     * 
     * @return The next randomized boolean value.
     */
    public static boolean getRandomBoolean()
    {
        return UtilRandom.RANDOM.nextBoolean();
    }

    /**
     * Get a random double value.
     * 
     * @return The next randomized double value (between 0.0 inclusive and 1.0 exclusive).
     */
    public static double getRandomDouble()
    {
        return UtilRandom.RANDOM.nextDouble();
    }

    /**
     * Private constructor.
     */
    private UtilRandom()
    {
        throw new RuntimeException();
    }
}
