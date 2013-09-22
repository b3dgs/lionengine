package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.EntityType;
import com.b3dgs.lionengine.example.d_rts.e_skills.WeaponType;

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
        super(EntityType.SPEARMAN, context);
        addWeapon(context.factoryWeapon.createWeapon(WeaponType.SPEAR, this), 0);
    }
}
