package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.TypePatrol;

/**
 * Beetle horizontal implementation.
 */
public final class BeetleHorizontal
        extends EntityBeetle
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    BeetleHorizontal(Context context)
    {
        super(context, TypeEntity.BEETLE_HORIZONTAL);
        enableMovement(TypePatrol.HORIZONTAL);
    }
}
