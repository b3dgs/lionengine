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

import com.b3dgs.lionengine.example.warcraft.RaceType;

/**
 * List of skill types.
 */
public enum SkillType
{
    /*
     * Human skills
     */

    /** Move human skill. */
    move_human(RaceType.HUMAN),
    /** Build human skill. */
    building_standard_human(RaceType.HUMAN),
    /** Attack melee human skill. */
    attack_sword(RaceType.HUMAN),
    /** Attack bow human skill. */
    attack_bow(RaceType.HUMAN),
    /** Cancel human skill. */
    cancel_human(RaceType.HUMAN),
    /** Stop human skill. */
    stop_human(RaceType.HUMAN),
    /** Build barracks human skill. */
    build_barracks_human(RaceType.HUMAN),
    /** Build farm human skill. */
    build_farm_human(RaceType.HUMAN),
    /** Produce peasant skill. */
    produce_peasant(RaceType.HUMAN),
    /** Produce footman skill. */
    produce_footman(RaceType.HUMAN),
    /** Produce archer skill. */
    produce_archer(RaceType.HUMAN),

    /*
     * Orc skills
     */

    /** Move orc skill. */
    move_orc(RaceType.ORC),
    /** Build orc skill. */
    building_standard_orc(RaceType.ORC),
    /** Attack melee orc skill. */
    attack_axe(RaceType.ORC),
    /** Attack spear orc skill. */
    attack_spear(RaceType.ORC),
    /** Stop orc skill. */
    stop_orc(RaceType.ORC),
    /** Cancel orc skill. */
    cancel_orc(RaceType.ORC),
    /** Build barracks orc skill. */
    build_barracks_orc(RaceType.ORC),
    /** Build farm orc skill. */
    build_farm_orc(RaceType.ORC),
    /** Produce peon skill. */
    produce_peon(RaceType.ORC),
    /** Produce grunt skill. */
    produce_grunt(RaceType.ORC),
    /** Produce spearman skill. */
    produce_spearman(RaceType.ORC);

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
}
