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
package com.b3dgs.lionengine;

/**
 * Standard range description, with a minimum and a maximum.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public class Range
{
    /** Positive integer numbers including 0. */
    public static final Range INT_POSITIVE = new Range(0, Integer.MAX_VALUE);
    /** Positive integer numbers excluding 0. */
    public static final Range INT_POSITIVE_STRICT = new Range(1, Integer.MAX_VALUE);

    /** Minimum value. */
    private final int min;
    /** Maximum value. */
    private final int max;

    /**
     * Create a blank range where min and max are equals to <code>0</code>.
     */
    public Range()
    {
        this(0, 0);
    }

    /**
     * Create a range.
     * 
     * @param min The minimum value (must be inferior or equal to max).
     * @param max The maximum value (must be superior or equal to min).
     * 
     * @throws LionEngineException If invalid arguments.
     */
    public Range(int min, int max)
    {
        Check.inferiorOrEqual(min, max);

        this.min = min;
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

    /**
     * Check if value is inside range, min and max included.
     * 
     * @param value The value to check.
     * @return <code>true</code> if value is inside range, <code>false</code> else.
     */
    public boolean includes(int value)
    {
        return value >= min && value <= max;
    }

    /**
     * Check if value is inside range, min and max included.
     * 
     * @param value The value to check.
     * @return <code>true</code> if value is inside range, <code>false</code> else.
     */
    public boolean includes(double value)
    {
        return Double.compare(value, min) >= 0 && Double.compare(value, max) <= 0;
    }
}
