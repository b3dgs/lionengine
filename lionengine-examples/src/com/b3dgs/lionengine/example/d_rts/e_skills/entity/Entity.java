package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.Map;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeSkill;
import com.b3dgs.lionengine.example.d_rts.e_skills.skill.FactorySkill;
import com.b3dgs.lionengine.example.d_rts.e_skills.skill.Skill;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.rts.EntityRts;
import com.b3dgs.lionengine.game.rts.ability.skilled.SkilledModel;
import com.b3dgs.lionengine.game.rts.ability.skilled.SkilledServices;

/**
 * Abstract entity implementation.
 */
public abstract class Entity
        extends EntityRts
        implements SkilledServices<TypeSkill, Skill>
{
    /** Entity life. */
    public final Alterable life;
    /** Map reference. */
    protected final Map map;
    /** Entity name. */
    private final String name;
    /** Entity icon number. */
    private final Sprite icon;
    /** Factory skill. */
    private final FactorySkill factorySkill;
    /** Skilled model. */
    private final SkilledModel<TypeSkill, Skill> skilled;

    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected Entity(TypeEntity id, Context context)
    {
        super(context.factoryEntity.getSetup(id), context.map);
        map = context.map;
        factorySkill = context.factorySkill;
        skilled = new SkilledModel<>();
        life = new Alterable(getDataInteger("life", "attributes"));
        name = getDataString("name");
        icon = Drawable.loadSprite(Media.get(FactoryEntity.ENTITY_PATH, getDataString("icon")));
        icon.load(false);
    }

    /**
     * Add a skill.
     * 
     * @param factory The factory reference.
     * @param panel The skill panel.
     * @param id The skill id.
     * @param priority The position number.
     */
    public void addSkill(FactoryEntity factory, int panel, TypeSkill id, int priority)
    {
        final Skill skill = factorySkill.createSkill(id);
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

    /**
     * Get the entity icon number.
     * 
     * @return The icon number.
     */
    public Sprite getIcon()
    {
        return icon;
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
    public Skill getSkill(int panel, TypeSkill id)
    {
        return skilled.getSkill(panel, id);
    }

    @Override
    public void removeSkill(int panel, TypeSkill id)
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
