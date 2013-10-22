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

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.warcraft.RaceType;
import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of entity types.
 */
public enum EntityType implements ObjectType
{
    /*
     * Human
     */

    /** Peasant unit. */
    PEASANT(RaceType.HUMAN),
    /** Footman unit. */
    FOOTMAN(RaceType.HUMAN),
    /** Archer unit. */
    ARCHER(RaceType.HUMAN),
    /** TownHall building. */
    TOWNHALL_HUMAN(RaceType.HUMAN),
    /** Farm building. */
    FARM_HUMAN(RaceType.HUMAN),
    /** Barracks building. */
    BARRACKS_HUMAN(RaceType.HUMAN),
    /** Lumber mill building. */
    LUMBERMILL_HUMAN(RaceType.HUMAN),

    /*
     * Orc
     */

    /** Peon unit. */
    PEON(RaceType.ORC),
    /** Grunt unit. */
    GRUNT(RaceType.ORC),
    /** Spearman unit. */
    SPEARMAN(RaceType.ORC),
    /** TownHall building. */
    TOWNHALL_ORC(RaceType.ORC),
    /** Farm building. */
    FARM_ORC(RaceType.ORC),
    /** Barracks building. */
    BARRACKS_ORC(RaceType.ORC),
    /** Lumber mill building. */
    LUMBERMILL_ORC(RaceType.ORC),

    /*
     * Neutral
     */

    /** Gold mine building. */
    GOLD_MINE(RaceType.NEUTRAL);

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

    /*
     * ObjectType
     */

    @Override
    public String asPathName()
    {
        return Media.getPath(race.asPathName(), ObjectTypeUtility.asPathName(this));
    }

    @Override
    public String asClassName()
    {
        return race.asPathName() + "." + ObjectTypeUtility.asClassName(this);
    }

    @Override
    public String toString()
    {
        return ObjectTypeUtility.toString(this);
    }
}
