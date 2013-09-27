package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
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
     * @param level The level reference.
     */
    public TurningAuto(Level level)
    {
        super(level, EntityType.TURNING_AUTO);
        timerShake.start();
    }
}
