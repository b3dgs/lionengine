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
package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Cursor;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.FactoryProduction;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.HandlerEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Map;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourcesLoader;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.human.FactorySkillHuman;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.orc.FactorySkillOrc;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.game.TimedMessage;
import com.b3dgs.lionengine.game.rts.skill.FactorySkillRts;

/**
 * Skill factory implementation.
 */
public final class FactorySkill
        extends FactorySkillRts<TypeSkill, SetupSkill, Skill>
{
    /** Production factory. */
    public final FactoryProduction factoryProduction;
    /** Entity handler. */
    final HandlerEntity handler;
    /** Cursor reference. */
    final Cursor cursor;
    /** Background set. */
    private final SpriteTiled background;
    /** The timed message reference. */
    private final TimedMessage message;
    /** Human skill factory. */
    private final FactorySkillHuman factorySkillHuman;
    /** Orc skill factory. */
    private final FactorySkillOrc factorySkillOrc;

    /**
     * Create a new entity factory.
     * 
     * @param handler The handler reference.
     * @param factoryProduction The production factory.
     * @param cursor The cursor reference.
     * @param map The map reference.
     * @param message The timed message reference.
     */
    public FactorySkill(HandlerEntity handler, FactoryProduction factoryProduction, Cursor cursor, Map map,
            TimedMessage message)
    {
        super(TypeSkill.class);
        this.handler = handler;
        this.factoryProduction = factoryProduction;
        this.cursor = cursor;
        this.message = message;
        background = ResourcesLoader.SKILL_BACKGROUND;
        background.load(false);
        factorySkillHuman = new FactorySkillHuman(this, handler, cursor, map);
        factorySkillOrc = new FactorySkillOrc(this, handler, cursor, map);
        loadAll(TypeSkill.values());
    }

    /*
     * FactorySkillRts
     */

    @Override
    public Skill createSkill(TypeSkill id)
    {
        switch (id.race)
        {
            case HUMAN:
                return factorySkillHuman.createSkill(id);
            case ORC:
                return factorySkillOrc.createSkill(id);
            default:
                throw new LionEngineException("Skill not found: ", id.name());
        }
    }

    @Override
    protected SetupSkill createSetup(TypeSkill id)
    {
        return new SetupSkill(Media.get(ResourcesLoader.SKILLS_DIR, id.name() + ".xml"), background, factoryProduction,
                message);
    }
}
