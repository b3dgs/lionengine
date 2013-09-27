package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.Patrol;

/**
 * Beetle vertical implementation.
 */
public final class BeetleVertical
        extends EntityBeetle
{
    /**
     * Constructor.
     * 
     * @param level The level reference.
     */
    public BeetleVertical(Level level)
    {
        super(level, EntityType.BEETLE_VERTICAL);
        enableMovement(Patrol.VERTICAL);
    }

    /*
     * EntityBeetle
     */

    @Override
    protected void handleActions(double extrp)
    {
        if (status.getState() == EntityState.WALK)
        {
            final int y = getLocationIntY();
            if (y > getPositionMax())
            {
                teleportY(getPositionMax());
            }
            if (y < getPositionMin())
            {
                teleportY(getPositionMin());
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
        if (hasPatrol() && (y == getPositionMin() || y == getPositionMax()))
        {
            status.setState(EntityState.TURN);
        }
        else if (diffVertical != 0.0)
        {
            status.setState(EntityState.WALK);
        }
        else
        {
            status.setState(EntityState.IDLE);
        }
    }
}
