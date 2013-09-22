package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;

/**
 * Bee monster implementation.
 */
public final class Bee
        extends EntityMonsterBee
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Bee(Context context)
    {
        super(context, EntityType.BEE);
    }
}
