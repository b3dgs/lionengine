package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.d_rts.e_skills.Cursor;
import com.b3dgs.lionengine.example.d_rts.e_skills.FactoryProduction;
import com.b3dgs.lionengine.example.d_rts.e_skills.HandlerEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeSkill;
import com.b3dgs.lionengine.game.rts.skill.FactorySkillRts;

/**
 * Skill factory implementation.
 */
public final class FactorySkill
        extends FactorySkillRts<TypeSkill, SetupSkill, Skill>
{
    /** Directory name from our resources directory containing our skills. */
    public static final String SKILL_PATH = "skills";
    /** Background set. */
    private final SpriteTiled background;
    /** Entity handler. */
    private final HandlerEntity handler;
    /** Production factory. */
    private final FactoryProduction factoryProduction;
    /** Cursor. */
    private final Cursor cursor;

    /**
     * Create a new entity factory.
     * 
     * @param handler The handler reference.
     * @param factoryProduction The production factory.
     * @param cursor The cursor reference.
     */
    public FactorySkill(HandlerEntity handler, FactoryProduction factoryProduction, Cursor cursor)
    {
        super(TypeSkill.class);
        this.handler = handler;
        this.factoryProduction = factoryProduction;
        this.cursor = cursor;
        background = Drawable.loadSpriteTiled(Media.get("skill_background.png"), 27, 19);
        background.load(false);
        loadAll(TypeSkill.values());
    }

    /*
     * FactorySkillRts
     */

    @Override
    public Skill createSkill(TypeSkill id)
    {
        switch (id)
        {
            case move_orc:
                return new Move(getSetup(id));
            case attack_axe:
                return new AttackAxe(getSetup(id), handler);
            case building_standard_orc:
                return new Build(getSetup(id));
            case stop_orc:
                return new Stop(getSetup(id));
            case build_barracks_orc:
                return new BuildBarracks(getSetup(id), cursor);
            case produce_grunt:
                return new ProduceGrunt(getSetup(id));
            case cancel_orc:
                return new Cancel(getSetup(id));
            default:
                throw new LionEngineException("Skill not found: ", id.name());
        }
    }

    @Override
    protected SetupSkill createSetup(TypeSkill id)
    {
        return new SetupSkill(Media.get(FactorySkill.SKILL_PATH, id.name() + ".xml"), background, factoryProduction);
    }
}
