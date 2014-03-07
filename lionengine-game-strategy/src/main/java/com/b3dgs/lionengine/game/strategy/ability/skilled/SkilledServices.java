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
package com.b3dgs.lionengine.game.strategy.ability.skilled;

import java.util.Collection;

import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.strategy.skill.SkillStrategy;

/**
 * Define something that can used skills.
 * 
 * @param <T> Skill enum type used.
 * @param <S> Skill type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface SkilledServices<T extends Enum<T> & ObjectType, S extends SkillStrategy<T>>
{
    /**
     * Main routine, has to be called in a game loop.
     * 
     * @param extrp The extrapolation value.
     */
    void update(double extrp);

    /**
     * Link skill to the entity.
     * 
     * @param skill The skill to add.
     * @param panel The panel id (>= 0).
     */
    void addSkill(S skill, int panel);

    /**
     * Get a skill from its id.
     * 
     * @param panel The panel (>= 0).
     * @param id The skill id.
     * @return The skill found.
     */
    S getSkill(int panel, T id);

    /**
     * Remove a skill.
     * 
     * @param panel The skill panel (>= 0).
     * @param id The skill id.
     */
    void removeSkill(int panel, T id);

    /**
     * Get all skills as collection (iterable) from a panel.
     * 
     * @param panel The skill panel (>= 0).
     * @return The all skills (empty collection if no skills for this panel).
     */
    Collection<S> getSkills(int panel);

    /**
     * Get all skills from all panels.
     * 
     * @return The list of all skills owned by the entity.
     */
    Collection<S> getSkills();

    /**
     * Set the current skill panel.
     * 
     * @param currentSkillPanel The current skill panel (>= 0).
     */
    void setSkillPanel(int currentSkillPanel);

    /**
     * Set the next skill panel (will be applied on the next update, so differed compared to {@link #setSkillPanel(int)}
     * .
     * 
     * @param nextSkillPanel The next skill panel (>= 0).
     */
    void setSkillPanelNext(int nextSkillPanel);

    /**
     * Get the current skill panel.
     * 
     * @return The current skill panel.
     */
    int getSkillPanel();
}
