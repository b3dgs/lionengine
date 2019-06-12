/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator;

import com.b3dgs.lionengine.Check;

/**
 * Describe the preference base for map generator.
 */
public abstract class PrefAbstract implements Preference
{
    /** Must be executed first. */
    private final Integer priority;

    /**
     * Create preference.
     * 
     * @param priority The priority value (must be superior or equal to 0).
     */
    public PrefAbstract(int priority)
    {
        super();

        Check.superiorOrEqual(priority, 0);

        this.priority = Integer.valueOf(priority);
    }

    /*
     * Preference
     */

    @Override
    public Integer getPrority()
    {
        return priority;
    }

    @Override
    public int compareTo(Preference other)
    {
        return priority.compareTo(other.getPrority());
    }
}
