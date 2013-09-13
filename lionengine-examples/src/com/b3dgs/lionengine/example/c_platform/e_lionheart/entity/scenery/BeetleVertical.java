package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.TypePatrol;

/**
 * Beetle vertical implementation.
 */
public final class BeetleVertical
        extends EntityBeetle
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    BeetleVertical(Context context)
    {
        super(context, TypeEntity.BEETLE_VERTICAL);
        enableMovement(TypePatrol.VERTICAL);
    }
}
