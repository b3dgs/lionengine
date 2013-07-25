package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.utility.UtilityRandom;

/**
 * Represents a container designed to return a random value between a range.
 * 
 * <pre>
 * Damages dmg = new Damages(1, 3);
 * dmg.getMax(); // returns 3
 * dmg.getRandom(); // returns a value between 1 and 3 included
 * dmg.getLast(); // returns the last value return by getRandom()
 * dmg.set(5, 10);
 * dmg.getMin(); // returns 5
 * dmg.getRandom(); // returns a value between 5 and 10 included
 * </pre>
 */
public final class Damages
        extends Range
{
    /** Last damages. */
    private int last;

    /**
     * Create new damage handler.
     */
    public Damages()
    {
        super();
        last = 0;
    }

    /**
     * Create new damage handler.
     * 
     * @param min The minimum damages value.
     * @param max The maximum damages value.
     */
    public Damages(int min, int max)
    {
        super(min, max);
        last = 0;
    }

    /**
     * Get random damages between min-max.
     * 
     * @return The randomized damages.
     */
    public int getRandom()
    {
        last = UtilityRandom.getRandomInteger(getMin(), getMax());
        return last;
    }

    /**
     * Get last damages.
     * 
     * @return The last damages.
     */
    public int getLast()
    {
        return last;
    }
}
