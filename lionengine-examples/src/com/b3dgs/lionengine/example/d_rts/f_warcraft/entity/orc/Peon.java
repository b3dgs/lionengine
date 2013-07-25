package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.UnitWorker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Peon implementation.
 */
final class Peon
        extends UnitWorker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Peon(Context context)
    {
        super(TypeEntity.peon, context);
        addSkill(context, 0, TypeSkill.move_orc, 0);
        addSkill(context, 0, TypeSkill.stop_orc, 1);
        addSkill(context, 0, TypeSkill.building_standard_orc, 2);
        addSkill(context, 1, TypeSkill.build_farm_orc, 0);
        addSkill(context, 1, TypeSkill.build_barracks_orc, 1);
        addSkill(context, 1, TypeSkill.cancel_orc, 2);
    }
}
