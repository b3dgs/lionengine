package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Sheet base scenery implementation.
 */
public class EntityScenerySheet
        extends EntityScenery
{
    /** Effect speed. */
    private static final int EFFECT_SPEED = 9;
    /** Effect amplitude. */
    private static final int AMPLITUDE = 12;
    /** Half circle. */
    private static final int HALF_CIRCLE = 180;
    /** Initial vertical location, default sheet location y. */
    private int initialY = Integer.MIN_VALUE;
    /** Effect side, -1 to decrease, 1 to increase. */
    private int effectSide;
    /** Effect start flag, <code>true</code> when effect is occurring, <code>false</code> else. */
    private boolean effectStart;
    /** Effect counter, represent the value used to calculate the effect. */
    private int effectCounter;
    /** First hit flag, when sheet is hit for the first time. */
    private boolean firstHit;
    
    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param type The entity type.
     */
    public EntityScenerySheet(Context context, TypeEntity type)
    {
        super(context, type);
    }

    /*
     * EntityScenery
     */
    
    @Override
    public void update(double extrp)
    {
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
        // Keep original location y
        if (initialY == Integer.MIN_VALUE)
        {
            initialY = getLocationIntY();
        }
        if (!effectStart)
        {
            effectCounter = 0;
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
