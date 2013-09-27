package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
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
     * @see Entity#Entity(Level, EntityType)
     */
    public EntityMonsterBee(Level level, EntityType type)
    {
        super(level, type);
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
