package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Sheet base scenery implementation.
 */
public class EntityScenerySheet
        extends EntityScenery
{
    /** Half circle. */
    protected static final int HALF_CIRCLE = 180;
    /** Effect speed. */
    private static final int EFFECT_SPEED = 9;
    /** Effect amplitude. */
    private static final int AMPLITUDE = 6;
    /** Initial vertical location, default sheet location y. */
    protected int initialY = Integer.MIN_VALUE;
    /** Effect start flag, <code>true</code> when effect is occurring, <code>false</code> else. */
    protected boolean effectStart;
    /** Effect counter, represent the value used to calculate the effect. */
    protected int effectCounter;
    /** Effect side, -1 to decrease, 1 to increase. */
    protected int effectSide;
    /** First hit flag, when sheet is hit for the first time. */
    protected boolean firstHit;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param type The entity type.
     */
    public EntityScenerySheet(Context context, EntityType type)
    {
        super(context, type);
    }

    /*
     * EntityScenery
     */

    @Override
    public void update(double extrp)
    {
        // Keep original location y
        if (initialY == Integer.MIN_VALUE)
        {
            initialY = getLocationIntY();
        }
        super.update(extrp);
        if (effectStart)
        {
            effectCounter += EntityScenerySheet.EFFECT_SPEED * effectSide;
            // Detect end
            if (effectCounter >= EntityScenerySheet.HALF_CIRCLE)
            {
                effectCounter = 0;
                effectSide = 0;
            }
            if (effectCounter <= 0)
            {
                effectCounter = 0;
                effectSide = 0;
            }
            setLocationY(initialY - UtilityMath.sin(effectCounter) * EntityScenerySheet.AMPLITUDE);
        }
    }

    @Override
    protected void onCollide(Entity entity)
    {
        if (!effectStart)
        {
            effectSide = 1;
            effectStart = true;
        }
        if (!firstHit)
        {
            firstHit = true;
            if (effectCounter > EntityScenerySheet.HALF_CIRCLE / 2)
            {
                effectSide = -1;
            }
            else
            {
                effectSide = 1;
            }
            effectStart = true;
        }
    }

    @Override
    protected void onLostCollision()
    {
        firstHit = false;
        if (effectStart)
        {
            // Go back to 0 as effect is lower than its half way
            if (effectCounter < EntityScenerySheet.HALF_CIRCLE / 2)
            {
                effectSide = -1;
            }
            if (effectCounter == 0)
            {
                effectStart = false;
            }
        }
    }
}
