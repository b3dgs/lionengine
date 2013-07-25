package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.human;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SetupSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SkillProduceEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Produce grunt implementation.
 */
final class ProduceArcher
        extends SkillProduceEntity
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    ProduceArcher(SetupSkill setup)
    {
        super(TypeSkill.produce_archer, setup, TypeEntity.archer);
    }
}
