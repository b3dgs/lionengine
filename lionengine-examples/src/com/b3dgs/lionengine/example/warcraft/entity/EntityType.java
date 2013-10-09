/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.warcraft.entity;

import com.b3dgs.lionengine.example.warcraft.RaceType;

/**
 * List of entity types.
 */
public enum EntityType
{
    /*
     * Human units
     */

    /** Peasant unit. */
    peasant(RaceType.HUMAN),
    /** Footman unit. */
    footman(RaceType.HUMAN),
    /** Archer unit. */
    archer(RaceType.HUMAN),
    /** TownHall building. */
    townhall_human(RaceType.HUMAN),
    /** Farm building. */
    farm_human(RaceType.HUMAN),
    /** Barracks building. */
    barracks_human(RaceType.HUMAN),
    /** Lumber mill building. */
    lumbermill_human(RaceType.HUMAN),

    /*
     * Orc units
     */

    /** Peon unit. */
    peon(RaceType.ORC),
    /** Grunt unit. */
    grunt(RaceType.ORC),
    /** Spearman unit. */
    spearman(RaceType.ORC),
    /** TownHall building. */
    townhall_orc(RaceType.ORC),
    /** Farm building. */
    farm_orc(RaceType.ORC),
    /** Barracks building. */
    barracks_orc(RaceType.ORC),
    /** Lumber mill building. */
    lumbermill_orc(RaceType.ORC),

    /*
     * Neutral units
     */

    /** Gold mine building. */
    gold_mine(RaceType.NEUTRAL);

    /** The race. */
    public final RaceType race;

    /**
     * Create a new type entity.
     * 
     * @param race The entity race.
     */
    private EntityType(RaceType race)
    {
        this.race = race;
    }
}
