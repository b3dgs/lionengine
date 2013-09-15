package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;

/**
 * Dino monster implementation.
 */
public final class Dino
        extends EntityMonster
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Dino(Context context)
    {
        super(context, TypeEntity.DINO);
        setFrameOffsets(0, -4);
    }
}
