package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.human;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.UnitWorker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Peon implementation.
 */
final class Peasant
        extends UnitWorker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Peasant(Context context)
    {
        super(TypeEntity.peasant, context);
        addSkill(context, 0, TypeSkill.move_human, 0);
        addSkill(context, 0, TypeSkill.stop_human, 1);
        addSkill(context, 0, TypeSkill.building_standard_human, 2);
        addSkill(context, 1, TypeSkill.build_farm_human, 0);
        addSkill(context, 1, TypeSkill.build_barracks_human, 1);
        addSkill(context, 1, TypeSkill.cancel_human, 2);
    }
}
