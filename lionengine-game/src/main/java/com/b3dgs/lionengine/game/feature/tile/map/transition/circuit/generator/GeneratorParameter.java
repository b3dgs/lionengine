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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Handle the map generation parameters.
 */
public class GeneratorParameter
{
    /** Preferences defined. */
    private final List<Preference> preferences = new ArrayList<Preference>();

    /**
     * Create the generator parameters.
     */
    public GeneratorParameter()
    {
        super();
    }

    /**
     * Add a preference.
     * 
     * @param preference The preference to add.
     * @return The generator parameter itself.
     */
    public GeneratorParameter add(Preference preference)
    {
        preferences.add(preference);
        Collections.sort(preferences);
        return this;
    }

    /**
     * Apply all preferences defined.
     * 
     * @param map The map reference.
     */
    public void apply(MapTile map)
    {
        for (final Preference preference : preferences)
        {
            preference.apply(map);
        }
    }
}
