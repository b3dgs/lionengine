package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.Patrol;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.Patroller;

/**
 * Dino monster implementation.
 */
public final class Dino
        extends EntityMonster
{
    /**
     * Constructor.
     * 
     * @param level The level reference.
     */
    public Dino(Level level)
    {
        super(level, EntityType.DINO);
        setFrameOffsets(0, -4);
        enableMovement(Patrol.HORIZONTAL);
    }

    /*
     * EntityMonster
     */

    @Override
    public boolean getMirror()
    {
        return !super.getMirror();
    }

    @Override
    protected void onHitBy(Entity entity)
    {
        super.onHitBy(entity);
        final int side;
        if (entity.getLocationX() - getLocationX() < 0)
        {
            side = Patroller.MOVE_RIGHT;
        }
        else
        {
            side = Patroller.MOVE_LEFT;
        }
        movement.getForce().setForce(3.0 * side, 0.0);
    }

    @Override
    protected void updateStates()
    {
        if (isFalling() || timerHurt.isStarted())
        {
            status.setState(EntityState.FALL);
        }
        else
        {
            super.updateStates();
        }
    }
}
