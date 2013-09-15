package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;

/**
 * BumbleBee monster implementation.
 */
public final class BumbleBee
        extends EntityMonsterBee
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public BumbleBee(Context context)
    {
        super(context, TypeEntity.BUMBLE_BEE);
    }
}
