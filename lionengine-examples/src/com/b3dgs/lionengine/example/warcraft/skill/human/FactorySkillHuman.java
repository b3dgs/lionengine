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
import com.b3dgs.lionengine.example.warcraft.HandlerEntity;
import com.b3dgs.lionengine.example.warcraft.Map;
import com.b3dgs.lionengine.example.warcraft.skill.Cancel;
import com.b3dgs.lionengine.example.warcraft.skill.FactorySkill;
import com.b3dgs.lionengine.example.warcraft.skill.Move;
import com.b3dgs.lionengine.example.warcraft.skill.Skill;
import com.b3dgs.lionengine.example.warcraft.skill.Stop;
import com.b3dgs.lionengine.example.warcraft.type.TypeSkill;

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
    public Skill createSkill(TypeSkill id)
    {
        switch (id)
        {
            case move_human:
                return new Move(factory.getSetup(id));
            case building_standard_human:
                return new BuildHuman(factory.getSetup(id));
            case attack_sword:
                return new AttackSword(factory.getSetup(id), handler);
            case attack_bow:
                return new AttackBow(factory.getSetup(id), handler);
            case stop_human:
                return new Stop(factory.getSetup(id));
            case cancel_human:
                return new Cancel(factory.getSetup(id));
            case build_farm_human:
                return new BuildFarmHuman(factory.getSetup(id), cursor, map);
            case build_barracks_human:
                return new BuildBarracksHuman(factory.getSetup(id), cursor, map);
            case produce_peasant:
                return new ProducePeasant(factory.getSetup(id));
            case produce_footman:
                return new ProduceFootman(factory.getSetup(id));
            case produce_archer:
                return new ProduceArcher(factory.getSetup(id));
            default:
                throw new LionEngineException("Skill not found: ", id.name());
        }
    }
}
