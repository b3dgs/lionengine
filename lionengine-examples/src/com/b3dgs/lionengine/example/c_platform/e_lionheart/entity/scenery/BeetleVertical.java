package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.TypePatrol;

/**
 * Beetle vertical implementation.
 */
public final class BeetleVertical
        extends EntityBeetle
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    BeetleVertical(Context context)
    {
        super(context, TypeEntity.BEETLE_VERTICAL);
        enableMovement(TypePatrol.VERTICAL);
    }
    
    /*
     * EntityBeetle
     */
    
    @Override
    protected void handleActions(double extrp)
    {
        if (status.getState() == TypeEntityState.WALK)
        {
            final int y = getLocationIntY();
            if (y > patroller.getPositionMax())
            {
                teleportY(patroller.getPositionMax());
            }
            if (y < patroller.getPositionMin())
            {
                teleportY(patroller.getPositionMin());
            }
        }
        super.handleActions(extrp);
    }
    
    @Override
    protected void updateStates()
    {
        super.updateStates();
        final double diffVertical = getDiffVertical();
        final int y = getLocationIntY();
        if (patroller.hasPatrol() && (y == patroller.getPositionMin() || y == patroller.getPositionMax()))
        {
            status.setState(TypeEntityState.TURN);
        }
        else if (diffVertical != 0.0)
        {
            status.setState(TypeEntityState.WALK);
        }
        else
        {
            status.setState(TypeEntityState.IDLE);
        }
    }
}
