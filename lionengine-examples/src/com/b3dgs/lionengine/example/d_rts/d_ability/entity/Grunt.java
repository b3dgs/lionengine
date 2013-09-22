package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.EntityType;
import com.b3dgs.lionengine.example.d_rts.d_ability.WeaponType;

/**
 * Grunt implementation.
 */
final class Grunt
        extends UnitAttacker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Grunt(Context context)
    {
        super(EntityType.GRUNT, context);
        addWeapon(context.factoryWeapon.createWeapon(WeaponType.AXE, this), 0);
    }
}
