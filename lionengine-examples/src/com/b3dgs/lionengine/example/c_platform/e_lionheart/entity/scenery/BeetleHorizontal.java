package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.TypePatrol;

/**
 * Beetle horizontal implementation.
 */
public final class BeetleHorizontal
        extends EntityBeetle
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    BeetleHorizontal(Context context)
    {
        super(context, TypeEntity.BEETLE_HORIZONTAL);
        enableMovement(TypePatrol.HORIZONTAL);
    }

    /*
     * EntityBeetle
     */

    @Override
    protected void handleActions(double extrp)
    {
        if (status.getState() == TypeEntityState.WALK)
        {
            final int x = getLocationIntX();
            if (x > getPositionMax())
            {
                teleportX(getPositionMax());
            }
            if (x < getPositionMin())
            {
                teleportX(getPositionMin());
            }
        }
        super.handleActions(extrp);
    }
    
    @Override
    protected void updateStates()
    {
        super.updateStates();
        final double diffHorizontal = getDiffHorizontal();
        final int x = getLocationIntX();
        if (hasPatrol() && (x == getPositionMin() || x == getPositionMax()))
        {
            status.setState(TypeEntityState.TURN);
        }
        else if (diffHorizontal != 0.0)
        {
            status.setState(TypeEntityState.WALK);
        }
        else
        {
            status.setState(TypeEntityState.IDLE);
        }
    }
    
    @Override
    protected void onCollide(Entity entity)
    {
        super.onCollide(entity);
        entity.teleportX(entity.getLocationX() + getDiffHorizontal());
    }
}
