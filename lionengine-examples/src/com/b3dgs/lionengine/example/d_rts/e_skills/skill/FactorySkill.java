package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.d_rts.e_skills.Cursor;
import com.b3dgs.lionengine.example.d_rts.e_skills.FactoryProduction;
import com.b3dgs.lionengine.example.d_rts.e_skills.HandlerEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.SkillType;
import com.b3dgs.lionengine.game.rts.skill.FactorySkillRts;

/**
 * Skill factory implementation.
 */
public final class FactorySkill
        extends FactorySkillRts<SkillType, SetupSkill, Skill>
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
        super(SkillType.class);
        this.handler = handler;
        this.factoryProduction = factoryProduction;
        this.cursor = cursor;
        background = Drawable.loadSpriteTiled(Media.get("skill_background.png"), 27, 19);
        background.load(false);
        loadAll(SkillType.values());
    }

    /*
     * FactorySkillRts
     */

    @Override
    public Skill createSkill(SkillType id)
    {
        switch (id)
        {
            case MOVE_ORC:
                return new Move(getSetup(id));
            case ATTACK_AXE:
                return new AttackAxe(getSetup(id), handler);
            case BUILDING_STANDARD_ORC:
                return new Build(getSetup(id));
            case STOP_ORC:
                return new Stop(getSetup(id));
            case BUILD_BARRACKS_ORC:
                return new BuildBarracks(getSetup(id), cursor);
            case PRODUCE_GRUNT:
                return new ProduceGrunt(getSetup(id));
            case CANCEL_ORC:
                return new Cancel(getSetup(id));
            default:
                throw new LionEngineException("Skill not found: " + id);
        }
    }

    @Override
    protected SetupSkill createSetup(SkillType id)
    {
        return new SetupSkill(Media.get(FactorySkill.SKILL_PATH, id + ".xml"), background, factoryProduction);
    }
}
