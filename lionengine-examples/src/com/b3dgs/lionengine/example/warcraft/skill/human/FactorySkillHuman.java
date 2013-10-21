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
package com.b3dgs.lionengine.example.warcraft.skill.human;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.warcraft.Cursor;
import com.b3dgs.lionengine.example.warcraft.entity.HandlerEntity;
import com.b3dgs.lionengine.example.warcraft.map.Map;
import com.b3dgs.lionengine.example.warcraft.skill.Cancel;
import com.b3dgs.lionengine.example.warcraft.skill.FactorySkill;
import com.b3dgs.lionengine.example.warcraft.skill.Move;
import com.b3dgs.lionengine.example.warcraft.skill.Skill;
import com.b3dgs.lionengine.example.warcraft.skill.SkillType;
import com.b3dgs.lionengine.example.warcraft.skill.Stop;

/**
 * Skill factory implementation.
 */
public final class FactorySkillHuman
{
    /** Factory skill. */
    private final FactorySkill factory;
    /** Handler reference. */
    private final HandlerEntity handler;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Map reference. */
    private final Map map;

    /**
     * Create a new human skill factory.
     * 
     * @param factory The factory reference.
     * @param handler The handler reference.
     * @param cursor The cursor reference.
     * @param map The map reference.
     */
    public FactorySkillHuman(FactorySkill factory, HandlerEntity handler, Cursor cursor, Map map)
    {
        this.factory = factory;
        this.handler = handler;
        this.cursor = cursor;
        this.map = map;
    }

    /**
     * Create a skill from its id.
     * 
     * @param id The skill id.
     * @return The skill instance.
     */
    public Skill createSkill(SkillType id)
    {
        switch (id)
        {
            case MOVE_HUMAN:
                return new Move(factory.getSetup(id));
            case BUILDING_STANDARD_HUMAN:
                return new BuildHuman(factory.getSetup(id));
            case ATTACK_SWORD:
                return new AttackSword(factory.getSetup(id), handler);
            case ATTACK_BOW:
                return new AttackBow(factory.getSetup(id), handler);
            case STOP_HUMAN:
                return new Stop(factory.getSetup(id));
            case CANCEL_HUMAN:
                return new Cancel(factory.getSetup(id));
            case BUILD_FARM_HUMAN:
                return new BuildFarmHuman(factory.getSetup(id), cursor, map);
            case BUILD_BARRACKS_HUMAN:
                return new BuildBarracksHuman(factory.getSetup(id), cursor, map);
            case PRODUCE_PEASANT:
                return new ProducePeasant(factory.getSetup(id));
            case PRODUCE_FOOTMAN:
                return new ProduceFootman(factory.getSetup(id));
            case PRODUCE_ARCHER:
                return new ProduceArcher(factory.getSetup(id));
            default:
                throw new LionEngineException("Skill not found: ", id.name());
        }
    }
}
