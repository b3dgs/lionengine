package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityState;

/**
 * Turning hit scenery implementation.
 */
public final class TurningHit
        extends EntityTurning
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public TurningHit(Context context)
    {
        super(context, TypeEntity.TURNING_HIT);
        shakeCounter = 5;
    }

    /*
     * EntityTurning
     */

    @Override
    public void hitBy(Entity entity)
    {
        super.hitBy(entity);
        if (shakeCounter == 6)
        {
            shakeCounter = 7;
        }
    }

    @Override
    protected void updateStates()
    {
        super.updateStates();
        // Turning
        if (shakeCounter == 5)
        {
            status.setState(TypeEntityState.TURN);
            shakeCounter = 6;
            effectStart = false;
        }
        // Detect end turning
        if (shakeCounter == 7)
        {
            effectStart = false;
            if (getFrameAnim() == 1 || getFrameAnim() == 16)
            {
                shakeCounter = 0;
                shake = false;
                status.setState(TypeEntityState.IDLE);
                effectSide = 1;
                timerShake.start();
            }
        }
        if (shake)
        {
            // Start turning
            if (shakeCounter == 4 && timerShake.elapsed(EntityTurning.TIME_BEFORE_TURNING))
            {
                shakeCounter = 5;
                timerShake.stop();
                status.setState(TypeEntityState.TURN);
            }
        }
    }
}
