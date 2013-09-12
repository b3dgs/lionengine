package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityState;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Turning auto scenery implementation.
 */
public final class TurningAuto
        extends EntityScenerySheet
{
    /** Shake amplitude. */
    private static final int SHAKE_AMPLITUDE = 5;
    /** Time before start shake. */
    private static final int TIME_BEFORE_START_SHAKE = 2000;
    /** Time before start turning. */
    private static final int TIME_BEFORE_TURNING = 1000;
    /** Shake speed. */
    private static final int SHAKE_SPEED = 30;
    /** Shake timer. */
    private final Timing timerShake;
    /** Shake counter. */
    private int shakeCounter;
    /** Shake started. */
    private boolean shake;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public TurningAuto(Context context)
    {
        super(context, TypeEntity.TURNING_AUTO);
        timerShake = new Timing();
        timerShake.start();
    }

    /*
     * EntityScenerySheet
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        if (!shake && timerShake.elapsed(TurningAuto.TIME_BEFORE_START_SHAKE))
        {
            shake = true;
            timerShake.stop();
            shakeCounter = 0;
            // TODO: Play start turn sound
        }
        if (shake)
        {
            if (shakeCounter < 3)
            {
                effectCounter += TurningAuto.SHAKE_SPEED;
                if (effectCounter > EntityScenerySheet.HALF_CIRCLE)
                {
                    shakeCounter++;
                    effectCounter = 0;
                }
                effectStart = false;
            }
            // Prepare turning
            if (shakeCounter == 3)
            {
                shakeCounter = 4;
                timerShake.start();
            }
            setLocationY(initialY - UtilityMath.sin(effectCounter) * TurningAuto.SHAKE_AMPLITUDE);
        }
    }
    
    @Override
    public void hitBy(Entity entity)
    {
        if (shakeCounter < 5)
        {
            super.hitBy(entity);
        }
    }

    @Override
    protected void updateStates()
    {
        super.updateStates();
        if (shake)
        {
            // Turning, detect end turning
            if (shakeCounter == 5)
            {
                effectStart = false;
                if (getAnimState() == AnimState.FINISHED)
                {
                    shakeCounter = 0;
                    shake = false;
                    status.setState(TypeEntityState.IDLE);
                    effectSide = 1;
                    timerShake.start();
                }
            }
            // Start turning
            if (shakeCounter == 4 && timerShake.elapsed(TurningAuto.TIME_BEFORE_TURNING))
            {
                shakeCounter = 5;
                timerShake.stop();
                status.setState(TypeEntityState.TURN);
            }
        }
    }
}
