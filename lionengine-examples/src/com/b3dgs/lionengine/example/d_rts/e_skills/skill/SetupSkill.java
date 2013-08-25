package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.d_rts.e_skills.FactoryProduction;
import com.b3dgs.lionengine.game.rts.skill.SetupSkillRts;

/**
 * Setup skill implementation.
 * 
 * @author Pierre-Alexandre
 */
final class SetupSkill
        extends SetupSkillRts
{
    /** Skill icon. */
    public final SpriteTiled icon;
    /** Skill background. */
    public final SpriteTiled background;
    /** Production factory. */
    final FactoryProduction factoryProduction;

    /**
     * Constructor.
     * 
     * @param config The config media.
     * @param background The skill background.
     * @param factoryProduction The production factory.
     */
    SetupSkill(Media config, SpriteTiled background, FactoryProduction factoryProduction)
    {
        super(config);
        this.background = background;
        this.factoryProduction = factoryProduction;
        icon = Drawable.loadSpriteTiled(Media.get(FactorySkill.SKILL_PATH, configurable.getDataString("icon")), 27, 19);
        icon.load(false);
    }
}
