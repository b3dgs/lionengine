package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.TypePatrol;

/**
 * Bee monster base implementation.
 */
public abstract class EntityMonsterBee
        extends EntityMonster
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param type The entity type.
     */
    public EntityMonsterBee(Context context, TypeEntity type)
    {
        super(context, type);
        enableMovement(TypePatrol.HORIZONTAL);
        enableMovement(TypePatrol.VERTICAL);
    }
    
    /*
     * EntityMonster
     */
    
    @Override
    protected void updateStates()
    {
        super.updateStates();
        mirror(false);
        if (status.getState() == TypeEntityState.IDLE)
        {
            status.setState(TypeEntityState.WALK);
        }
    }
    
    @Override
    protected void updateCollisions()
    {
        // Nothing to do
    }
}
