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
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.warcraft.AppWarcraft;
import com.b3dgs.lionengine.example.warcraft.Cursor;
import com.b3dgs.lionengine.example.warcraft.entity.FactoryProduction;
import com.b3dgs.lionengine.example.warcraft.entity.HandlerEntity;
import com.b3dgs.lionengine.example.warcraft.map.Map;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.TimedMessage;

/**
 * Skill factory implementation.
 */
public final class FactorySkill
        extends FactoryObjectGame<SkillType, SetupSkill, Skill>
{
    /** Map. */
    public final Map map;
    /** Cursor. */
    public final Cursor cursor;
    /** Handler entity. */
    public final HandlerEntity handlerEntity;
    /** Production factory. */
    public final FactoryProduction factoryProduction;
    /** Background set. */
    private final SpriteTiled background;
    /** The timed message reference. */
    private final TimedMessage message;

    /**
     * Create a new entity factory.
     * 
     * @param map The map reference.
     * @param cursor The cursor reference.
     * @param handlerEntity The handler entity reference.
     * @param factoryProduction The production factory.
     * @param message The timed message reference.
     */
    public FactorySkill(Map map, Cursor cursor, HandlerEntity handlerEntity, FactoryProduction factoryProduction,
            TimedMessage message)
    {
        super(SkillType.class, AppWarcraft.SKILLS_DIR);
        this.map = map;
        this.cursor = cursor;
        this.handlerEntity = handlerEntity;
        this.factoryProduction = factoryProduction;
        this.message = message;
        background = Drawable.loadSpriteTiled(Media.get("skill_background.png"), 31, 23);
        background.load(false);
        load();
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupSkill createSetup(SkillType type, Media config)
    {
        return new SetupSkill(config, type, background, map, cursor, handlerEntity, factoryProduction, message);
    }
}
