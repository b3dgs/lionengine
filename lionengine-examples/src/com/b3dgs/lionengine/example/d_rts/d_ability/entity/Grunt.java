package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeWeapon;

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
        super(TypeEntity.grunt, context);
        addWeapon(context.factoryWeapon.createWeapon(TypeWeapon.axe, this), 0);
    }
}
