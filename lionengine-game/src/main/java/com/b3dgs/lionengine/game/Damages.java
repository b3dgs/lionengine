/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.UtilRandom;

/**
 * Represents a container designed to return a random value between a range.
 */
public class Damages
{
    /** Damages range. */
    private Range range;
    /** Last damages. */
    private int last;

    /**
     * Create a damages handler with zero as default.
     */
    public Damages()
    {
        range = new Range(0, 0);
        last = 0;
    }

    /**
     * Create a damages handler with initial range.
     * 
     * @param min The minimum damages value.
     * @param max The maximum damages value.
     */
    public Damages(int min, int max)
    {
        range = new Range(min, max);
        last = 0;
    }

    /**
     * Set the minimum damage value.
     * 
     * @param min The minimum damage value.
     */
    public void setMin(int min)
    {
        range = new Range(min, range.getMax());
    }

    /**
     * Set the maximum damage value.
     * 
     * @param max The maximum damage value.
     */
    public void setMax(int max)
    {
        range = new Range(range.getMin(), max);
    }

    /**
     * Set the maximum damage value.
     * 
     * @param min The minimum damage value.
     * @param max The maximum damage value.
     */
    public void setDamages(int min, int max)
    {
        range = new Range(min, max);
    }

    /**
     * Get random damages between min-max.
     * 
     * @return The randomized damages.
     */
    public int getRandom()
    {
        last = UtilRandom.getRandomInteger(range);
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
        return range.getMin();
    }

    /**
     * Get maximum damages value.
     * 
     * @return The maximum damages value.
     */
    public int getMax()
    {
        return range.getMax();
    }

    /**
     * Get the damages range.
     * 
     * @return The damages range.
     */
    public Range getDamages()
    {
        return new Range(range.getMin(), range.getMax());
    }
}
