package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;

/**
 * BumbleBee monster implementation.
 */
public final class BumbleBee
        extends EntityMonsterBee
{
    /**
     * Constructor.
     * 
     * @param level The level reference.
     */
    public BumbleBee(Level level)
    {
        super(level, EntityType.BUMBLE_BEE);
    }
}
