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
package com.b3dgs.lionengine.game.rts.skill;

import com.b3dgs.lionengine.game.FactoryGame;

/**
 * Abstract skill factory. It performs a list of available skills from a file considering an input enumeration. Data are
 * stored with an enumeration as key.
 * 
 * @param <T> Skill enum type used.
 * @param <S> Setup entity type used.
 * @param <K> Skill type used.
 */
public abstract class FactorySkillRts<T extends Enum<T>, S extends SetupSkillRts, K extends SkillRts<T>>
        extends FactoryGame<T, S>
{
    /**
     * Create a new skill factory.
     * 
     * @param clazz The enum class.
     */
    public FactorySkillRts(Class<T> clazz)
    {
        super(clazz);
    }

    /**
     * Create a skill from its id.
     * 
     * @param id The skill id.
     * @return The skill instance.
     */
    public abstract K createSkill(T id);
}
