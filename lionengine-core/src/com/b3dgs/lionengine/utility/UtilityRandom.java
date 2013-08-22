package com.b3dgs.lionengine.utility;

/**
 * Random utility class implementation.
 */
public final class UtilityRandom
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
        UtilityRandom.RANDOM.setSeed(seed);
    }

    /**
     * Get a random integer value from 0 and 2^32.
     * 
     * @return A value between 0 and 2^32.
     */
    public static int getRandomInteger()
    {
        return UtilityRandom.RANDOM.nextInt();
    }

    /**
     * Get a random value from 0 and a maximum.
     * 
     * @param max The maximum randomized value.
     * @return A value between 0 inclusive and max inclusive.
     */
    public static int getRandomInteger(int max)
    {
        return UtilityRandom.RANDOM.nextInt(max + 1);
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
        return min + UtilityRandom.RANDOM.nextInt(max + 1 - min);
    }

    /**
     * Get a random boolean value.
     * 
     * @return The next randomized boolean value.
     */
    public static boolean getRandomBoolean()
    {
        return UtilityRandom.RANDOM.nextBoolean();
    }

    /**
     * Get a random double value.
     * 
     * @return The next randomized double value (between 0.0 inclusive and 1.0 exclusive).
     */
    public static double getRandomDouble()
    {
        return UtilityRandom.RANDOM.nextDouble();
    }

    /**
     * Private constructor.
     */
    private UtilityRandom()
    {
        throw new RuntimeException();
    }
}
