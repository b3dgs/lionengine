package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Sfx;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Turning scenery implementation base.
 */
public abstract class EntityTurning
        extends EntityScenerySheet
{
    /** Shake amplitude. */
    protected static final int SHAKE_AMPLITUDE = 5;
    /** Time before start shake. */
    protected static final int TIME_BEFORE_START_SHAKE = 2000;
    /** Time before start turning. */
    protected static final int TIME_BEFORE_TURNING = 1000;
    /** Shake speed. */
    protected static final int SHAKE_SPEED = 30;
    /** Shake timer. */
    protected final Timing timerShake;
    /** Shake counter. */
    protected int shakeCounter;
    /** Shake started. */
    protected boolean shake;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param type The entity type.
     */
    public EntityTurning(Context context, EntityType type)
    {
        super(context, type);
        timerShake = new Timing();
    }

    /*
     * EntityScenerySheet
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        if (!shake && timerShake.elapsed(EntityTurning.TIME_BEFORE_START_SHAKE))
        {
            shake = true;
            timerShake.stop();
            shakeCounter = 0;
            Sfx.BIPBIPBIP.play();
        }
        if (shake)
        {
            if (shakeCounter < 3)
            {
                effectCounter += EntityTurning.SHAKE_SPEED;
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
            setLocationY(initialY - UtilityMath.sin(effectCounter) * EntityTurning.SHAKE_AMPLITUDE);
        }
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (shakeCounter < 5)
        {
            super.hitThat(entity);
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
                    status.setState(EntityState.IDLE);
                    effectSide = 1;
                    timerShake.start();
                }
            }
            // Start turning
            if (shakeCounter == 4 && timerShake.elapsed(EntityTurning.TIME_BEFORE_TURNING))
            {
                shakeCounter = 5;
                timerShake.stop();
                status.setState(EntityState.TURN);
            }
        }
    }
}
