package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
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
     * @param level The level reference.
     */
    public Bee(Level level)
    {
        super(level, EntityType.BEE);
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
