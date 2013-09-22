package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;

/**
 * BeeLittle monster implementation.
 */
public final class BeeLittle
        extends EntityMonsterBee
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public BeeLittle(Context context)
    {
        super(context, EntityType.BEE_LITTLE);
    }
}
