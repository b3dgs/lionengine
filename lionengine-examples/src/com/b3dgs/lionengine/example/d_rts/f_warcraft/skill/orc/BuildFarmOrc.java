package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Cursor;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Map;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SetupSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SkillProduceBuilding;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Build skill implementation.
 */
final class BuildFarmOrc
        extends SkillProduceBuilding
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     * @param cursor The cursor reference.
     * @param map The map reference.
     */
    BuildFarmOrc(SetupSkill setup, Cursor cursor, Map map)
    {
        super(TypeSkill.build_farm_orc, setup, TypeEntity.farm_orc, cursor, map);
    }
}
