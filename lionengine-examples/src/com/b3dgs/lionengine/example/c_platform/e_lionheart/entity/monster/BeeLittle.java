package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;

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
        super(context, TypeEntity.BEE_LITTLE);
    }
}
