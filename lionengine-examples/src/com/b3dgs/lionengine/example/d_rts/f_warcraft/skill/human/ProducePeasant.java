package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.human;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SetupSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SkillProduceEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Produce grunt implementation.
 */
final class ProducePeasant
        extends SkillProduceEntity
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    ProducePeasant(SetupSkill setup)
    {
        super(TypeSkill.produce_peasant, setup, TypeEntity.peasant);
    }
}
