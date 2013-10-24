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
import com.b3dgs.lionengine.example.warcraft.skill.human.AttackBow;
import com.b3dgs.lionengine.example.warcraft.skill.human.AttackSword;
import com.b3dgs.lionengine.example.warcraft.skill.human.BuildBarracksHuman;
import com.b3dgs.lionengine.example.warcraft.skill.human.BuildFarmHuman;
import com.b3dgs.lionengine.example.warcraft.skill.human.BuildingStandardHuman;
import com.b3dgs.lionengine.example.warcraft.skill.human.CancelHuman;
import com.b3dgs.lionengine.example.warcraft.skill.human.MoveHuman;
import com.b3dgs.lionengine.example.warcraft.skill.human.ProduceArcher;
import com.b3dgs.lionengine.example.warcraft.skill.human.ProduceFootman;
import com.b3dgs.lionengine.example.warcraft.skill.human.ProducePeasant;
import com.b3dgs.lionengine.example.warcraft.skill.human.StopHuman;
import com.b3dgs.lionengine.example.warcraft.skill.orc.AttackAxe;
import com.b3dgs.lionengine.example.warcraft.skill.orc.AttackSpear;
import com.b3dgs.lionengine.example.warcraft.skill.orc.BuildBarracksOrc;
import com.b3dgs.lionengine.example.warcraft.skill.orc.BuildFarmOrc;
import com.b3dgs.lionengine.example.warcraft.skill.orc.BuildingStandardOrc;
import com.b3dgs.lionengine.example.warcraft.skill.orc.CancelOrc;
import com.b3dgs.lionengine.example.warcraft.skill.orc.MoveOrc;
import com.b3dgs.lionengine.example.warcraft.skill.orc.ProduceGrunt;
import com.b3dgs.lionengine.example.warcraft.skill.orc.ProducePeon;
import com.b3dgs.lionengine.example.warcraft.skill.orc.ProduceSpearman;
import com.b3dgs.lionengine.example.warcraft.skill.orc.StopOrc;
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
    MOVE_HUMAN(MoveHuman.class, RaceType.HUMAN),
    /** Build human skill. */
    BUILDING_STANDARD_HUMAN(BuildingStandardHuman.class, RaceType.HUMAN),
    /** Attack melee human skill. */
    ATTACK_SWORD(AttackSword.class, RaceType.HUMAN),
    /** Attack bow human skill. */
    ATTACK_BOW(AttackBow.class, RaceType.HUMAN),
    /** Cancel human skill. */
    CANCEL_HUMAN(CancelHuman.class, RaceType.HUMAN),
    /** Stop human skill. */
    STOP_HUMAN(StopHuman.class, RaceType.HUMAN),
    /** Build barracks human skill. */
    BUILD_BARRACKS_HUMAN(BuildBarracksHuman.class, RaceType.HUMAN),
    /** Build farm human skill. */
    BUILD_FARM_HUMAN(BuildFarmHuman.class, RaceType.HUMAN),
    /** Produce peasant skill. */
    PRODUCE_PEASANT(ProducePeasant.class, RaceType.HUMAN),
    /** Produce footman skill. */
    PRODUCE_FOOTMAN(ProduceFootman.class, RaceType.HUMAN),
    /** Produce archer skill. */
    PRODUCE_ARCHER(ProduceArcher.class, RaceType.HUMAN),

    /*
     * Orc skills
     */

    /** Move orc skill. */
    MOVE_ORC(MoveOrc.class, RaceType.ORC),
    /** Build orc skill. */
    BUILDING_STANDARD_ORC(BuildingStandardOrc.class, RaceType.ORC),
    /** Attack melee orc skill. */
    ATTACK_AXE(AttackAxe.class, RaceType.ORC),
    /** Attack spear orc skill. */
    ATTACK_SPEAR(AttackSpear.class, RaceType.ORC),
    /** Stop orc skill. */
    STOP_ORC(StopOrc.class, RaceType.ORC),
    /** Cancel orc skill. */
    CANCEL_ORC(CancelOrc.class, RaceType.ORC),
    /** Build barracks orc skill. */
    BUILD_BARRACKS_ORC(BuildBarracksOrc.class, RaceType.ORC),
    /** Build farm orc skill. */
    BUILD_FARM_ORC(BuildFarmOrc.class, RaceType.ORC),
    /** Produce peon skill. */
    PRODUCE_PEON(ProducePeon.class, RaceType.ORC),
    /** Produce grunt skill. */
    PRODUCE_GRUNT(ProduceGrunt.class, RaceType.ORC),
    /** Produce spearman skill. */
    PRODUCE_SPEARMAN(ProduceSpearman.class, RaceType.ORC);

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
    private SkillType(Class<?> target, RaceType race)
    {
        this.target = target;
        this.race = race;
        path = Media.getPath(ObjectTypeUtility.asPathName(race), ObjectTypeUtility.asPathName(this));
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
