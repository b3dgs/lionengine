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
package com.b3dgs.lionengine.example.game.rts.skills.entity;

import java.util.Collection;

import com.b3dgs.lionengine.example.game.rts.skills.Context;
import com.b3dgs.lionengine.example.game.rts.skills.map.Map;
import com.b3dgs.lionengine.example.game.rts.skills.skill.FactorySkill;
import com.b3dgs.lionengine.example.game.rts.skills.skill.Skill;
import com.b3dgs.lionengine.example.game.rts.skills.skill.SkillType;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.rts.ability.skilled.SkilledModel;
import com.b3dgs.lionengine.game.rts.ability.skilled.SkilledServices;
import com.b3dgs.lionengine.game.rts.entity.EntityRts;

/**
 * Abstract entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Entity
        extends EntityRts
        implements SkilledServices<SkillType, Skill>
{
    /** Entity life. */
    public final Alterable life;
    /** Map reference. */
    protected final Map map;
    /** Entity name. */
    private final String name;
    /** Factory skill. */
    private final FactorySkill factorySkill;
    /** Skilled model. */
    private final SkilledModel<SkillType, Skill> skilled;

    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected Entity(EntityType id, Context context)
    {
        super(context.factoryEntity.getSetup(id), context.map);
        map = context.map;
        factorySkill = context.factorySkill;
        skilled = new SkilledModel<>();
        life = new Alterable(getDataInteger("life", "attributes"));
        name = getDataString("name");
    }

    /**
     * Add a skill.
     * 
     * @param factory The factory reference.
     * @param panel The skill panel.
     * @param type The skill type.
     * @param priority The position number.
     */
    public void addSkill(FactoryEntity factory, int panel, SkillType type, int priority)
    {
        final Skill skill = factorySkill.create(type);
        skill.setOwner(this);
        skill.setPriority(priority);
        skill.prepare();
        addSkill(skill, panel);
    }

    /**
     * Get the current life.
     * 
     * @return The current life.
     */
    public int getLife()
    {
        return life.getCurrent();
    }

    /**
     * Get the entity name.
     * 
     * @return The entity name.
     */
    public String getName()
    {
        return name;
    }

    /*
     * Skill
     */

    @Override
    public void addSkill(Skill skill, int panel)
    {
        skilled.addSkill(skill, panel);
    }

    @Override
    public Skill getSkill(int panel, SkillType id)
    {
        return skilled.getSkill(panel, id);
    }

    @Override
    public void removeSkill(int panel, SkillType id)
    {
        skilled.removeSkill(panel, id);
    }

    @Override
    public Collection<Skill> getSkills(int panel)
    {
        return skilled.getSkills(panel);
    }

    @Override
    public Collection<Skill> getSkills()
    {
        return skilled.getSkills();
    }

    @Override
    public void setSkillPanel(int currentSkillPanel)
    {
        skilled.setSkillPanel(currentSkillPanel);
    }

    @Override
    public void setSkillPanelNext(int nextSkillPanel)
    {
        skilled.setSkillPanelNext(nextSkillPanel);
    }

    @Override
    public int getSkillPanel()
    {
        return skilled.getSkillPanel();
    }
}
