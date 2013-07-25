package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.FactoryProduction;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourcesLoader;
import com.b3dgs.lionengine.game.TimedMessage;
import com.b3dgs.lionengine.game.rts.skill.SetupSkillRts;

/**
 * Setup skill implementation.
 * 
 * @author Pierre-Alexandre
 */
public final class SetupSkill
        extends SetupSkillRts
{
    /** Skill icon. */
    public final SpriteTiled icon;
    /** Skill background. */
    public final SpriteTiled background;
    /** Production factory. */
    protected final FactoryProduction factoryProduction;
    /** The timed message reference. */
    protected final TimedMessage message;

    /**
     * Constructor.
     * 
     * @param config The config media.
     * @param background The skill background.
     * @param factoryProduction The production factory.
     * @param message The timed message reference.
     */
    public SetupSkill(Media config, SpriteTiled background, FactoryProduction factoryProduction, TimedMessage message)
    {
        super(config);
        this.background = background;
        this.factoryProduction = factoryProduction;
        this.message = message;
        icon = Drawable.loadSpriteTiled(Media.get(ResourcesLoader.SKILLS_DIR, configurable.getDataString("icon")), 27,
                19);
        icon.load(false);
    }
}
