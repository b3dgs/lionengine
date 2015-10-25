/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Damages dmg = new Damages(1, 3);
 * dmg.getMax(); // returns 3
 * dmg.getRandom(); // returns a value between 1 and 3 included
 * dmg.getLast(); // returns the last value return by getRandom()
 * dmg.setMin(5);
 * dmg.setMax(10);
 * dmg.getMin(); // returns 5
 * dmg.getRandom(); // returns a value between 5 and 10 included
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Damages extends Range
{
    /** Last damages. */
    private int last;

    /**
     * Create a damage handler.
     */
    public Damages()
    {
        super();
        last = 0;
    }

    /**
     * Create a damages handler.
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
        last = UtilRandom.getRandomInteger(getMin(), getMax());
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
