package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeWeapon;

/**
 * Spearman implementation.
 */
final class Spearman
        extends UnitAttacker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Spearman(Context context)
    {
        super(TypeEntity.spearman, context);
        addWeapon(context.factoryWeapon.createWeapon(TypeWeapon.spear, this), 0);
    }
}
