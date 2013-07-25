package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SetupSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SkillBuild;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Buildings standard human skill implementation.
 */
final class BuildOrc
        extends SkillBuild
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    BuildOrc(SetupSkill setup)
    {
        super(TypeSkill.building_standard_orc, setup);
    }
}
