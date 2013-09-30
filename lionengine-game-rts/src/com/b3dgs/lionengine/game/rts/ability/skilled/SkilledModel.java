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
package com.b3dgs.lionengine.game.rts.ability.skilled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.game.rts.skill.SkillRts;

/**
 * Skilled model implementation.
 * 
 * @param <T> Skill enum type used.
 * @param <S> Skill type used.
 */
public class SkilledModel<T extends Enum<T>, S extends SkillRts<T>>
        implements SkilledServices<T, S>
{
    /** Skills list. */
    private final Map<Integer, Map<T, S>> skills;
    /** Active skill panel. */
    private int currentSkillPanel;
    /** Next skill panel. */
    private int nextSkillPanel;

    /**
     * Constructor.
     */
    public SkilledModel()
    {
        skills = new HashMap<>(1);
        nextSkillPanel = -1;
    }

    /*
     * SkilledServices
     */

    @Override
    public void update(double extrp)
    {
        if (nextSkillPanel > -1)
        {
            setSkillPanel(nextSkillPanel);
        }
    }

    @Override
    public void addSkill(S skill, int panel)
    {
        Check.argument(panel >= 0, "The skill panel id must be positive !");

        final Integer key = Integer.valueOf(panel);

        Map<T, S> list = skills.get(key);
        if (list == null)
        {
            list = new HashMap<>(1);
            skills.put(key, list);
        }
        list.put(skill.getId(), skill);
    }

    @Override
    public S getSkill(int panel, T id)
    {
        Check.argument(panel >= 0, "The skill panel id must be positive !");

        final Integer key = Integer.valueOf(panel);
        final Map<T, S> list = skills.get(key);

        if (list == null)
        {
            return null;
        }

        return list.get(id);
    }

    @Override
    public void removeSkill(int panel, T id)
    {
        Check.argument(panel >= 0, "The skill panel id must be positive !");

        final Integer key = Integer.valueOf(panel);
        final Map<T, S> list = skills.get(key);

        if (list != null)
        {
            list.remove(id);
        }
    }

    @Override
    public Collection<S> getSkills(int panel)
    {
        Check.argument(panel >= 0, "The skill panel id must be positive !");

        final Integer key = Integer.valueOf(panel);
        final Map<T, S> list = skills.get(key);

        if (list == null)
        {
            return new ArrayList<>(0);
        }

        return list.values();
    }

    @Override
    public Collection<S> getSkills()
    {
        final Collection<S> list = new ArrayList<>(4);
        final Set<Integer> panels = skills.keySet();

        for (final Integer panel : panels)
        {
            list.addAll(skills.get(panel).values());
        }

        return list;
    }

    @Override
    public void setSkillPanel(int currentSkillPanel)
    {
        Check.argument(currentSkillPanel >= 0, "The skill panel id must be positive !");
        this.currentSkillPanel = currentSkillPanel;
        nextSkillPanel = -1;
    }

    @Override
    public void setSkillPanelNext(int nextSkillPanel)
    {
        Check.argument(currentSkillPanel >= 0, "The skill panel id must be positive !");
        this.nextSkillPanel = nextSkillPanel;
    }

    @Override
    public int getSkillPanel()
    {
        return currentSkillPanel;
    }
}
