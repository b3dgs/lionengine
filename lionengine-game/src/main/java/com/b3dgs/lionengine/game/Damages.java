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

import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.util.UtilMath;
import com.b3dgs.lionengine.util.UtilRandom;

/**
 * Represents a container designed to return a random value between a range.
 */
public class Damages
{
    /** Minimum value. */
    private int min;
    /** Maximum value. */
    private int max;
    /** Last damages. */
    private int last;

    /**
     * Create a damages handler with zero as default.
     */
    public Damages()
    {
        this(0, 0);
    }

    /**
     * Create a damages handler with initial range. Max set to min value if over.
     * 
     * @param min The minimum damages value (must be positive).
     * @param max The maximum damages value (must be positive and superior or equal to min).
     */
    public Damages(int min, int max)
    {
        super();

        this.min = UtilMath.clamp(min, 0, Integer.MAX_VALUE);
        this.max = UtilMath.clamp(max, this.min, Integer.MAX_VALUE);
    }

    /**
     * Set the minimum damage value. Max set to min value if over.
     * 
     * @param min The minimum damage value (must be positive).
     */
    public void setMin(int min)
    {
        this.min = UtilMath.clamp(min, 0, Integer.MAX_VALUE);
        max = UtilMath.clamp(max, this.min, Integer.MAX_VALUE);
    }

    /**
     * Set the maximum damage value.
     * 
     * @param max The maximum damage value (must be positive and superior or equal to min).
     */
    public void setMax(int max)
    {
        this.max = UtilMath.clamp(max, min, Integer.MAX_VALUE);
    }

    /**
     * Set the maximum damage value. Max set to min value if over.
     * 
     * @param min The minimum damages value (must be positive).
     * @param max The maximum damages value (must be positive and superior or equal to min).
     */
    public void setDamages(int min, int max)
    {
        this.min = UtilMath.clamp(min, 0, Integer.MAX_VALUE);
        this.max = UtilMath.clamp(max, this.min, Integer.MAX_VALUE);
    }

    /**
     * Get random damages between min-max.
     * 
     * @return The randomized damages.
     */
    public int getRandom()
    {
        last = UtilRandom.getRandomInteger(min, max);
        return last;
    }

    /**
     * Get last damages value.
     * 
     * @return The last damages value.
     */
    public int getLast()
    {
        return last;
    }

    /**
     * Get minimum damages value.
     * 
     * @return The minimum damages value.
     */
    public int getMin()
    {
        return min;
    }

    /**
     * Get maximum damages value.
     * 
     * @return The maximum damages value.
     */
    public int getMax()
    {
        return max;
    }

    /**
     * Get the damages range.
     * 
     * @return The damages range.
     */
    public Range getDamages()
    {
        return new Range(min, max);
    }
}
