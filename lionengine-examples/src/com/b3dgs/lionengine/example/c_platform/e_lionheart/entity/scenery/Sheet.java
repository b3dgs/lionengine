package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;

/**
 * Sheet scenery implementation.
 */
public final class Sheet
        extends EntityScenerySheet
{
    /**
     * Constructor.
     * 
     * @param level The level reference.
     */
    public Sheet(Level level)
    {
        super(level, EntityType.SHEET);
    }
}
