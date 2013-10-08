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
package com.b3dgs.lionengine.example.game.rts.skills;

import java.util.Locale;

/**
 * List of skill types.
 */
public enum SkillType
{
    /*
     * Human skills
     */

    /** Move human skill. */
    MOVE_HUMAN(TypeRace.HUMAN),
    /** Build human skill. */
    BUILDING_STANDARD_HUMAN(TypeRace.HUMAN),
    /** Attack melee human skill. */
    ATTACK_SWORD(TypeRace.HUMAN),
    /** Attack bow human skill. */
    ATTACK_BOW(TypeRace.HUMAN),
    /** Cancel human skill. */
    CANCEL_HUMAN(TypeRace.HUMAN),
    /** Stop human skill. */
    STOP_HUMAN(TypeRace.HUMAN),
    /** Build barracks human skill. */
    BUILD_BARRACKS_HUMAN(TypeRace.HUMAN),
    /** Produce peasant skill. */
    PRODUCE_PEASANT(TypeRace.HUMAN),
    /** Produce footman skill. */
    PRODUCE_FOOTMAN(TypeRace.HUMAN),
    /** Produce archer skill. */
    PRODUCE_ARCHER(TypeRace.HUMAN),

    /*
     * Orc skills
     */

    /** Move orc skill. */
    MOVE_ORC(TypeRace.ORC),
    /** Build orc skill. */
    BUILDING_STANDARD_ORC(TypeRace.ORC),
    /** Attack melee orc skill. */
    ATTACK_AXE(TypeRace.ORC),
    /** Attack spear orc skill. */
    ATTACK_SPEAR(TypeRace.ORC),
    /** Stop orc skill. */
    STOP_ORC(TypeRace.ORC),
    /** Cancel orc skill. */
    CANCEL_ORC(TypeRace.ORC),
    /** Build barracks orc skill. */
    BUILD_BARRACKS_ORC(TypeRace.ORC),
    /** Produce peon skill. */
    PRODUCE_PEON(TypeRace.ORC),
    /** Produce grunt skill. */
    PRODUCE_GRUNT(TypeRace.ORC),
    /** Produce spearman skill. */
    PRODUCE_SPEARMAN(TypeRace.ORC);

    /** The race. */
    public final TypeRace race;

    /**
     * Create a new type entity.
     * 
     * @param race The entity race.
     */
    private SkillType(TypeRace race)
    {
        this.race = race;
    }

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
