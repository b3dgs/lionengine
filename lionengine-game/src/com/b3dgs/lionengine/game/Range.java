package com.b3dgs.lionengine.game;

/**
 * Standard range description, with a minimum and a maximum.
 */
public class Range
{
    /** Minimum value. */
    private int min;
    /** Maximum value. */
    private int max;

    /**
     * Create a new blank range.
     */
    public Range()
    {
        min = 0;
        max = 0;
    }

    /**
     * Create a new range.
     * 
     * @param min The minimum value.
     * @param max The maximum value.
     */
    public Range(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    /**
     * Set minimum value.
     * 
     * @param min The minimum value.
     */
    public void setMin(int min)
    {
        this.min = min;
    }

    /**
     * Set maximum value.
     * 
     * @param max The maximum value.
     */
    public void setMax(int max)
    {
        this.max = max;
    }

    /**
     * Get minimum value.
     * 
     * @return The minimum value.
     */
    public int getMin()
    {
        return min;
    }

    /**
     * Get maximum value.
     * 
     * @return The maximum value.
     */
    public int getMax()
    {
        return max;
    }
}
