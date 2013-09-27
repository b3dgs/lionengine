package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
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
     * @param level The level reference.
     */
    public BeeLittle(Level level)
    {
        super(level, EntityType.BEE_LITTLE);
    }

    /*
     * EntityMonsterBee
     */

    @Override
    public boolean getMirror()
    {
        return !super.getMirror();
    }
}
