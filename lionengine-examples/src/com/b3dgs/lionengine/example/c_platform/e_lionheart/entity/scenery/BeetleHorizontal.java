package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.Patrol;

/**
 * Beetle horizontal implementation.
 */
public final class BeetleHorizontal
        extends EntityBeetle
{
    /**
     * Constructor.
     * 
     * @param level The level reference.
     */
    public BeetleHorizontal(Level level)
    {
        super(level, EntityType.BEETLE_HORIZONTAL);
        setFrameOffsets(0, -6);
        enableMovement(Patrol.HORIZONTAL);
    }

    /*
     * EntityBeetle
     */

    @Override
    protected void handleActions(double extrp)
    {
        if (status.getState() == EntityState.WALK)
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
            status.setState(EntityState.TURN);
        }
        else if (diffHorizontal != 0.0)
        {
            status.setState(EntityState.WALK);
        }
        else
        {
            status.setState(EntityState.IDLE);
        }
    }

    @Override
    protected void onCollide(Entity entity)
    {
        super.onCollide(entity);
        entity.teleportX(entity.getLocationX() + getDiffHorizontal());
    }
}
