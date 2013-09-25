package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.Patrol;

/**
 * Bee monster base implementation.
 */
public abstract class EntityMonsterBee
        extends EntityMonster
{
    /**
     * @see Entity#Entity(Context, EntityType)
     */
    public EntityMonsterBee(Context context, EntityType type)
    {
        super(context, type);
        enableMovement(Patrol.HORIZONTAL);
        enableMovement(Patrol.VERTICAL);
    }

    /*
     * EntityMonster
     */

    @Override
    protected void updateStates()
    {
        super.updateStates();
        mirror(false);
        if (status.getState() == EntityState.IDLE)
        {
            status.setState(EntityState.WALK);
        }
    }

    @Override
    protected void updateCollisions()
    {
        // Nothing to do
    }
}
