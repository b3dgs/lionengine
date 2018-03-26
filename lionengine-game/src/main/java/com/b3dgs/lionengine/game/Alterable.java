/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

/**
 * Represents a value which can change between 0 and a maximum. It is mainly used in complement with an Attribute, to
 * represent life, mana...
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
    private double cur;
    /** Alterable max value. */
    private int max;

    /**
     * Create an alterable with a maximum value.
     * 
     * @param max The maximum reachable value.
     */
    public Alterable(int max)
    {
        this(max, false);
    }

    /**
     * Create an alterable.
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
     * @param extrp The extrapolation value.
     * @param increase The increase step.
     * @return The increased value (equal to increase in normal case, lower in other case).
     */
    public int increase(double extrp, int increase)
    {
        final int current = getCurrent();
        final double increased = current + increase * extrp;
        set(increased);
        if (increased > max)
        {
            return max - current;
        }
        return increase;
    }

    /**
     * Increase current value. The current value will not exceed the maximum.
     * 
     * @param increase The increase step.
     * @return The increased value (equal to increase in normal case, lower in other case).
     */
    public int increase(int increase)
    {
        return increase(1.0, increase);
    }

    /**
     * Decrease current value. The current value will not be lower than 0.
     * 
     * @param extrp The extrapolation value.
     * @param decrease The decrease step.
     * @return The decreased value (equal to decrease in normal case, lower in other case).
     */
    public int decrease(double extrp, int decrease)
    {
        final int remain = getCurrent();
        final double decreased = remain - decrease * extrp;
        set(decreased);
        if (decreased < 0)
        {
            return remain;
        }
        return decrease;
    }

    /**
     * Decrease current value. The current value will not be lower than 0.
     * 
     * @param decrease The decrease step.
     * @return The decreased value (equal to decrease in normal case, lower in other case).
     */
    public int decrease(int decrease)
    {
        return decrease(1.0, decrease);
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
        set((double) value);
    }

    /**
     * Set current value. The current value will be fixed between 0 and the maximum.
     * 
     * @param value The current value.
     */
    private void set(double value)
    {
        cur = value;
        if (!overMax && cur > max)
        {
            cur = max;
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
        return (int) Math.floor(cur);
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
        return value - getCurrent();
    }

    /**
     * Check if current value equal max or not (current == max).
     * 
     * @return <code>true</code> if current equal max, <code>false</code> else.
     */
    public boolean isFull()
    {
        return getCurrent() >= max;
    }

    /**
     * Check if current value is equal to zero (current = 0).
     * 
     * @return <code>true</code> if 0, <code>false</code> else.
     */
    public boolean isEmpty()
    {
        return getCurrent() == Alterable.MIN;
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
        return getCurrent() - value >= 0;
    }
}
