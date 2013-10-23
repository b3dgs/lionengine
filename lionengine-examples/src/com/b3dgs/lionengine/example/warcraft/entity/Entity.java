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
package com.b3dgs.lionengine.example.warcraft.entity;

import java.util.Collection;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.example.warcraft.AppWarcraft;
import com.b3dgs.lionengine.example.warcraft.Context;
import com.b3dgs.lionengine.example.warcraft.Player;
import com.b3dgs.lionengine.example.warcraft.map.Map;
import com.b3dgs.lionengine.example.warcraft.skill.Skill;
import com.b3dgs.lionengine.example.warcraft.skill.SkillType;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.rts.ability.skilled.SkilledModel;
import com.b3dgs.lionengine.game.rts.ability.skilled.SkilledServices;
import com.b3dgs.lionengine.game.rts.entity.EntityRts;

/**
 * Abstract entity implementation.
 */
public abstract class Entity
        extends EntityRts
        implements SkilledServices<SkillType, Skill>
{
    /** Entity type. */
    public final EntityType type;
    /** Map reference. */
    protected final Map map;
    /** Entity life. */
    private final Alterable life;
    /** Entity name. */
    private final String name;
    /** Entity icon number. */
    private final Sprite icon;
    /** Skilled model. */
    private final SkilledModel<SkillType, Skill> skilled;
    /** Dead flag. */
    private boolean dead;
    /** Player owner (null if none). */
    private Player owner;
    /** Creation progress. */
    private int progress;

    /**
     * Constructor.
     * 
     * @param type The entity type.
     * @param setup The setup reference.
     * @param context The context reference.
     */
    protected Entity(EntityType type, SetupSurfaceGame setup, Context context)
    {
        super(context.factoryEntity.getSetup(type), context.map);
        this.type = type;
        map = context.map;
        skilled = new SkilledModel<>();
        life = new Alterable(getDataInteger("life", "attributes"));
        setFov(getDataInteger("fov", "attributes"));
        name = getDataString("name");
        icon = Drawable.loadSprite(Media.get(AppWarcraft.ENTITIES_DIR, type.race.asPathName(), getDataString("icon")));
        icon.load(false);
        dead = false;
        owner = null;
        life.fill();
        progress = 100;
    }

    /**
     * Add a skill.
     * 
     * @param context The context reference.
     * @param panel The skill panel.
     * @param type The skill type.
     * @param priority The position number.
     */
    public void addSkill(Context context, int panel, SkillType type, int priority)
    {
        final Skill skill = context.factorySkill.create(type);
        skill.setOwner(this);
        skill.setPriority(priority);
        skill.prepare();
        addSkill(skill, panel);
    }

    /**
     * Kill the entity.
     */
    public void kill()
    {
        decreaseLife(life.getCurrent());
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
     * Get the life percent.
     * 
     * @return The life percent.
     */
    public int getLifePercent()
    {
        return life.getPercent();
    }

    /**
     * Decrease life value.
     * 
     * @param damages The damages.
     */
    public void decreaseLife(int damages)
    {
        decreaseLife(damages, null);
    }

    /**
     * Decrease life value.
     * 
     * @param damages The damages.
     * @param attacker The attacker reference.
     */
    public void decreaseLife(int damages, Attacker attacker)
    {
        life.decrease(damages);
        if (life.isEmpty())
        {
            dead = true;
            setSelectable(false);
            setActive(false);
            setAlive(false);
            stop();
            removeRef();
            final Player player = getPlayer();
            if (player != null)
            {
                player.changePopulation(-1);
            }
        }
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

    /**
     * Get the entity icon number.
     * 
     * @return The icon number.
     */
    public Sprite getIcon()
    {
        return icon;
    }

    /**
     * Get the owner reference.
     * 
     * @return The owner reference.
     */
    public Player getPlayer()
    {
        return owner;
    }

    /**
     * Set the owner reference.
     * 
     * @param player The player owner reference.
     */
    public void setPlayer(Player player)
    {
        owner = player;
        setPlayerId(player.id);
    }

    /**
     * Get the dead status.
     * 
     * @return <code>true</code> if dead, <code>false</code> else.
     */
    public boolean isDead()
    {
        return dead;
    }

    /**
     * Get the construction progress in percent.
     * 
     * @return The construction progress in percent.
     */
    public int getProgressPercent()
    {
        return progress;
    }

    /**
     * Set the current progress in percent.
     * 
     * @param progress The progress value in percent.
     */
    void setProgressPercent(int progress)
    {
        this.progress = progress;
    }

    /*
     * SkilledServices
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
