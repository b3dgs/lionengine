package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;

/**
 * Turning auto scenery implementation.
 */
public final class TurningAuto
        extends EntityScenerySheet
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public TurningAuto(Context context)
    {
        super(context, TypeEntity.TURNING_AUTO);
    }
}
