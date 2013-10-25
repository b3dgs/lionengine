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
import com.b3dgs.lionengine.example.warcraft.entity.human.Archer;
import com.b3dgs.lionengine.example.warcraft.entity.human.BarracksHuman;
import com.b3dgs.lionengine.example.warcraft.entity.human.FarmHuman;
import com.b3dgs.lionengine.example.warcraft.entity.human.Footman;
import com.b3dgs.lionengine.example.warcraft.entity.human.LumbermillHuman;
import com.b3dgs.lionengine.example.warcraft.entity.human.Peasant;
import com.b3dgs.lionengine.example.warcraft.entity.human.TownhallHuman;
import com.b3dgs.lionengine.example.warcraft.entity.neutral.GoldMine;
import com.b3dgs.lionengine.example.warcraft.entity.orc.BarracksOrc;
import com.b3dgs.lionengine.example.warcraft.entity.orc.FarmOrc;
import com.b3dgs.lionengine.example.warcraft.entity.orc.Grunt;
import com.b3dgs.lionengine.example.warcraft.entity.orc.LumbermillOrc;
import com.b3dgs.lionengine.example.warcraft.entity.orc.Peon;
import com.b3dgs.lionengine.example.warcraft.entity.orc.Spearman;
import com.b3dgs.lionengine.example.warcraft.entity.orc.TownhallOrc;
import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of entity types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityType implements ObjectType
{
    /*
     * Human
     */

    /** Peasant unit. */
    PEASANT(Peasant.class, RaceType.HUMAN),
    /** Footman unit. */
    FOOTMAN(Footman.class, RaceType.HUMAN),
    /** Archer unit. */
    ARCHER(Archer.class, RaceType.HUMAN),
    /** TownHall building. */
    TOWNHALL_HUMAN(TownhallHuman.class, RaceType.HUMAN),
    /** Farm building. */
    FARM_HUMAN(FarmHuman.class, RaceType.HUMAN),
    /** Barracks building. */
    BARRACKS_HUMAN(BarracksHuman.class, RaceType.HUMAN),
    /** Lumber mill building. */
    LUMBERMILL_HUMAN(LumbermillHuman.class, RaceType.HUMAN),

    /*
     * Orc
     */

    /** Peon unit. */
    PEON(Peon.class, RaceType.ORC),
    /** Grunt unit. */
    GRUNT(Grunt.class, RaceType.ORC),
    /** Spearman unit. */
    SPEARMAN(Spearman.class, RaceType.ORC),
    /** TownHall building. */
    TOWNHALL_ORC(TownhallOrc.class, RaceType.ORC),
    /** Farm building. */
    FARM_ORC(FarmOrc.class, RaceType.ORC),
    /** Barracks building. */
    BARRACKS_ORC(BarracksOrc.class, RaceType.ORC),
    /** Lumber mill building. */
    LUMBERMILL_ORC(LumbermillOrc.class, RaceType.ORC),

    /*
     * Neutral
     */

    /** Gold mine building. */
    GOLD_MINE(GoldMine.class, RaceType.NEUTRAL);

    /** Class target. */
    private final Class<?> target;
    /** Path name. */
    private final String path;
    /** The race. */
    public final RaceType race;

    /**
     * The class target.
     * 
     * @param target The target class.
     * @param race The entity race.
     */
    private EntityType(Class<?> target, RaceType race)
    {
        this.target = target;
        path = Media.getPath(ObjectTypeUtility.getPathName(race), ObjectTypeUtility.getPathName(this));
        this.race = race;
    }

    /*
     * ObjectType
     */

    @Override
    public Class<?> getTargetClass()
    {
        return target;
    }

    @Override
    public String getPathName()
    {
        return path;
    }
}
