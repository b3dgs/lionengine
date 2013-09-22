package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;

/**
 * Turning auto scenery implementation.
 */
public final class TurningAuto
        extends EntityTurning
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public TurningAuto(Context context)
    {
        super(context, EntityType.TURNING_AUTO);
        timerShake.start();
    }
}
