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
package com.b3dgs.lionengine.example.warcraft.skill;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.warcraft.RaceType;
import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of skill types.
 */
public enum SkillType implements ObjectType
{
    /*
     * Human skills
     */

    /** Move human skill. */
    MOVE_HUMAN(RaceType.HUMAN),
    /** Build human skill. */
    BUILDING_STANDARD_HUMAN(RaceType.HUMAN),
    /** Attack melee human skill. */
    ATTACK_SWORD(RaceType.HUMAN),
    /** Attack bow human skill. */
    ATTACK_BOW(RaceType.HUMAN),
    /** Cancel human skill. */
    CANCEL_HUMAN(RaceType.HUMAN),
    /** Stop human skill. */
    STOP_HUMAN(RaceType.HUMAN),
    /** Build barracks human skill. */
    BUILD_BARRACKS_HUMAN(RaceType.HUMAN),
    /** Build farm human skill. */
    BUILD_FARM_HUMAN(RaceType.HUMAN),
    /** Produce peasant skill. */
    PRODUCE_PEASANT(RaceType.HUMAN),
    /** Produce footman skill. */
    PRODUCE_FOOTMAN(RaceType.HUMAN),
    /** Produce archer skill. */
    PRODUCE_ARCHER(RaceType.HUMAN),

    /*
     * Orc skills
     */

    /** Move orc skill. */
    MOVE_ORC(RaceType.ORC),
    /** Build orc skill. */
    BUILDING_STANDARD_ORC(RaceType.ORC),
    /** Attack melee orc skill. */
    ATTACK_AXE(RaceType.ORC),
    /** Attack spear orc skill. */
    ATTACK_SPEAR(RaceType.ORC),
    /** Stop orc skill. */
    STOP_ORC(RaceType.ORC),
    /** Cancel orc skill. */
    CANCEL_ORC(RaceType.ORC),
    /** Build barracks orc skill. */
    BUILD_BARRACKS_ORC(RaceType.ORC),
    /** Build farm orc skill. */
    BUILD_FARM_ORC(RaceType.ORC),
    /** Produce peon skill. */
    PRODUCE_PEON(RaceType.ORC),
    /** Produce grunt skill. */
    PRODUCE_GRUNT(RaceType.ORC),
    /** Produce spearman skill. */
    PRODUCE_SPEARMAN(RaceType.ORC);

    /** The race. */
    public final RaceType race;

    /**
     * Create a new type entity.
     * 
     * @param race The entity race.
     */
    private SkillType(RaceType race)
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
