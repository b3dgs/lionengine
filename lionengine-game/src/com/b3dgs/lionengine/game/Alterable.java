package com.b3dgs.lionengine.game;

/**
 * Represents a value which can change between 0 and a maximum. It is mainly used in complement with an Attribute, to
 * represent life, mana...
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Alterable life = new Alterable(100);
 * life.decrease(25); // life = 75
 * life.fill(); // life = 100
 * life.isFull(); // returns true
 * life.getNeeded(150); // returns 50
 * life.setMax(150);
 * life.fill(); // life = 150
 * life.increase(25); // life = 150
 * life.set(0); // life = 0
 * life.isEmpty(); // returns true
 * </pre>
 * 
 * @see Attribute
 */
public class Alterable
{
    /** Minimum value. */
    private static final int MIN = 0;
    /** Over max. */
    private final boolean overMax;
    /** Alterable current value. */
    private int cur;
    /** Alterable max value. */
    private int max;

    /**
     * Create a new alterable.
     * 
     * @param max The maximum reachable value.
     */
    public Alterable(int max)
    {
        this(max, false);
    }

    /**
     * Create a new alterable.
     * 
     * @param max The maximum reachable value.
     * @param overMax <code>true</code> to allow over value.
     */
    public Alterable(int max, boolean overMax)
    {
        this.overMax = overMax;
        this.max = max;
        if (this.max < Alterable.MIN)
        {
            this.max = Alterable.MIN;
        }
        cur = 0;
    }

    /**
     * Increase current value. The current value will not exceed the maximum.
     * 
     * @param increase The increase step.
     * @return The increased value (equal to increase in normal case, lower in other case).
     */
    public int increase(int increase)
    {
        final int current = cur;
        final int increased = cur + increase;
        set(increased);
        if (increased > max)
        {
            return max - current;
        }
        return increase;
    }

    /**
     * Decrease current value. The current value will not be lower than 0.
     * 
     * @param decrease The decrease step.
     * @return The decreased value (equal to decrease in normal case, lower in other case).
     */
    public int decrease(int decrease)
    {
        final int remain = cur;
        final int decreased = cur - decrease;
        set(decreased);
        if (decreased < 0)
        {
            return remain;
        }
        return decrease;
    }

    /**
     * Fill until max value (set current value to max).
     */
    public void fill()
    {
        cur = max;
    }

    /**
     * Reset value to minimum.
     */
    public void reset()
    {
        cur = Alterable.MIN;
    }

    /**
     * Set current value. The current value will be fixed between 0 and the maximum.
     * 
     * @param value The current value.
     */
    public void set(int value)
    {
        cur = value;
        if (!overMax)
        {
            if (cur > max)
            {
                cur = max;
            }
        }
        if (cur < Alterable.MIN)
        {
            cur = Alterable.MIN;
        }
    }

    /**
     * Set maximum reachable value. The maximum value can not be lower than 0.
     * 
     * @param max The maximum reachable value.
     */
    public void setMax(int max)
    {
        this.max = max;
        if (this.max < Alterable.MIN)
        {
            this.max = Alterable.MIN;
        }
    }

    /**
     * Get maximum reachable value.
     * 
     * @return The maximum reachable value.
     */
    public int getMax()
    {
        return max;
    }

    /**
     * Get current value.
     * 
     * @return The current value.
     */
    public int getCurrent()
    {
        return cur;
    }

    /**
     * Get percent value (depending of current/max). The percent value is between 0 and 100 (0 = min, 100 = max).
     * 
     * @return The percent value.
     */
    public int getPercent()
    {
        if (max == 0)
        {
            return 0;
        }
        return (int) (cur * 100.0 / max);
    }

    /**
     * Get the needed value from an input to a specified value. Basically, it will return the input value - current.
     * <p>
     * Example: if current = 50; getNeeded(60) will return 10 (60 - 50).
     * </p>
     * 
     * @param value The quantity desired.
     * @return 0 if nothing is needed, positive value else.
     */
    public int getNeeded(int value)
    {
        if (isEnough(value))
        {
            return 0;
        }
        return value - cur;
    }

    /**
     * Check if current value equal max or not (current == max).
     * 
     * @return <code>true</code> if current equal max, <code>false</code> else.
     */
    public boolean isFull()
    {
        return cur >= max;
    }

    /**
     * Check if current value is equal to zero (current = 0).
     * 
     * @return <code>true</code> if 0, <code>false</code> else.
     */
    public boolean isEmpty()
    {
        return cur == Alterable.MIN;
    }

    /**
     * Check if current value less the specified value is still positive. Basically, it will return current - input
     * value. Example: if current = 50; <code>isEnough(40)</code> will return <code>true</code>;
     * <code>isEnough(60)</code> will return <code>false</code>.
     * 
     * @param value The check value.
     * @return <code>true</code> if subtract is positive, <code>false</code> else.
     */
    public boolean isEnough(int value)
    {
        return cur - value >= 0;
    }
}
