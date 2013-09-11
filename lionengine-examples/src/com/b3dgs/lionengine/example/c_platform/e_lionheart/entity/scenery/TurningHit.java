package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;

/**
 * Turning hit scenery implementation.
 */
public final class TurningHit
        extends EntityScenerySheet
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public TurningHit(Context context)
    {
        super(context, TypeEntity.TURNING_HIT);
    }
}
