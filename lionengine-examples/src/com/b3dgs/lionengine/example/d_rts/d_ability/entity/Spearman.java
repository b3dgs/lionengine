package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeWeapon;

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
