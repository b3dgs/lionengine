/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.strategy.skills.skill;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.strategy.skills.Cursor;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.FactoryProduction;
import com.b3dgs.lionengine.game.FactoryObjectGame;

/**
 * Skill factory implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactorySkill
        extends FactoryObjectGame<SetupSkill, Skill>
{
    /** Directory name from our resources directory containing our skills. */
    public static final String SKILL_PATH = "skills";
    /** Production factory. */
    private final FactoryProduction factoryProduction;
    /** Cursor cursor. */
    private final Cursor cursor;

    /**
     * Create a new entity factory.
     * 
     * @param factoryProduction The production factory.
     * @param cursor The cursor reference.
     */
    public FactorySkill(FactoryProduction factoryProduction, Cursor cursor)
    {
        super(FactorySkill.SKILL_PATH);
        this.factoryProduction = factoryProduction;
        this.cursor = cursor;
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupSkill createSetup(Class<? extends Skill> type, Media config)
    {
        return new SetupSkill(config, factoryProduction, cursor);
    }
}
