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
package com.b3dgs.lionengine.example.d_rts.f_warcraft.type;

/**
 * List of entity types.
 */
public enum TypeEntity
{
    /*
     * Human units
     */

    /** Peasant unit. */
    peasant(TypeRace.HUMAN),
    /** Footman unit. */
    footman(TypeRace.HUMAN),
    /** Archer unit. */
    archer(TypeRace.HUMAN),
    /** TownHall building. */
    townhall_human(TypeRace.HUMAN),
    /** Farm building. */
    farm_human(TypeRace.HUMAN),
    /** Barracks building. */
    barracks_human(TypeRace.HUMAN),
    /** Lumber mill building. */
    lumbermill_human(TypeRace.HUMAN),

    /*
     * Orc units
     */

    /** Peon unit. */
    peon(TypeRace.ORC),
    /** Grunt unit. */
    grunt(TypeRace.ORC),
    /** Spearman unit. */
    spearman(TypeRace.ORC),
    /** TownHall building. */
    townhall_orc(TypeRace.ORC),
    /** Farm building. */
    farm_orc(TypeRace.ORC),
    /** Barracks building. */
    barracks_orc(TypeRace.ORC),
    /** Lumber mill building. */
    lumbermill_orc(TypeRace.ORC),

    /*
     * Neutral units
     */

    /** Gold mine building. */
    gold_mine(TypeRace.NEUTRAL);

    /** The race. */
    public final TypeRace race;

    /**
     * Create a new type entity.
     * 
     * @param race The entity race.
     */
    private TypeEntity(TypeRace race)
    {
        this.race = race;
    }
}
