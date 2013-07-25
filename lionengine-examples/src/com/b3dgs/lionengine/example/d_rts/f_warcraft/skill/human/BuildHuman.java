package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.human;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SetupSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SkillBuild;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Buildings standard human skill implementation.
 */
final class BuildHuman
        extends SkillBuild
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    BuildHuman(SetupSkill setup)
    {
        super(TypeSkill.building_standard_human, setup);
    }
}
