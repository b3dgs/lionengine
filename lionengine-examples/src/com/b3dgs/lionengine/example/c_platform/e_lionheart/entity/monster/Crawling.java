package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.TypePatrol;

/**
 * Crawling monster implementation.
 */
public final class Crawling
        extends EntityMonster
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Crawling(Context context)
    {
        super(context, TypeEntity.CRAWLING);
        enableMovement(TypePatrol.HORIZONTAL);
    }
}
