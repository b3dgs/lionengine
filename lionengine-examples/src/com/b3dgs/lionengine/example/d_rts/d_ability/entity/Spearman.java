package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.EntityType;
import com.b3dgs.lionengine.example.d_rts.d_ability.WeaponType;

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
