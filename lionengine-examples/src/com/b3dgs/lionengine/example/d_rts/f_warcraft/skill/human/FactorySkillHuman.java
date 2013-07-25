package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.human;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Cursor;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.HandlerEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Map;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.Cancel;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.FactorySkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.Move;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.Skill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.Stop;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Skill factory implementation.
 */
public final class FactorySkillHuman
{
    /** Factory skill. */
    private final FactorySkill factory;
    /** Handler reference. */
    private final HandlerEntity handler;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Map reference. */
    private final Map map;

    /**
     * Create a new human skill factory.
     * 
     * @param factory The factory reference.
     * @param handler The handler reference.
     * @param cursor The cursor reference.
     * @param map The map reference.
     */
    public FactorySkillHuman(FactorySkill factory, HandlerEntity handler, Cursor cursor, Map map)
    {
        this.factory = factory;
        this.handler = handler;
        this.cursor = cursor;
        this.map = map;
    }

    /**
     * Create a skill from its id.
     * 
     * @param id The skill id.
     * @return The skill instance.
     */
    public Skill createSkill(TypeSkill id)
    {
        switch (id)
        {
            case move_human:
                return new Move(factory.getSetup(id));
            case building_standard_human:
                return new BuildHuman(factory.getSetup(id));
            case attack_sword:
                return new AttackSword(factory.getSetup(id), handler);
            case attack_bow:
                return new AttackBow(factory.getSetup(id), handler);
            case stop_human:
                return new Stop(factory.getSetup(id));
            case cancel_human:
                return new Cancel(factory.getSetup(id));
            case build_farm_human:
                return new BuildFarmHuman(factory.getSetup(id), cursor, map);
            case build_barracks_human:
                return new BuildBarracksHuman(factory.getSetup(id), cursor, map);
            case produce_peasant:
                return new ProducePeasant(factory.getSetup(id));
            case produce_footman:
                return new ProduceFootman(factory.getSetup(id));
            case produce_archer:
                return new ProduceArcher(factory.getSetup(id));
            default:
                throw new LionEngineException("Skill not found: ", id.name());
        }
    }
}
