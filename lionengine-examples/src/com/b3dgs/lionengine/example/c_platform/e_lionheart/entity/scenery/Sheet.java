package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
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
     * @param context The context reference.
     */
    public Sheet(Context context)
    {
        super(context, EntityType.SHEET);
    }
}
