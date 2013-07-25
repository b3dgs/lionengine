package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SetupSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SkillProduceEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Produce grunt implementation.
 */
final class ProducePeon
        extends SkillProduceEntity
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    ProducePeon(SetupSkill setup)
    {
        super(TypeSkill.produce_peon, setup, TypeEntity.peon);
    }
}
