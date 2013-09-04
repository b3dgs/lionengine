package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;

/**
 * Represents the factory of scenery entity.
 */
public final class FactoryEntityScenery
{
    /**
     * Create a scenery from its type.
     * 
     * @param context The context reference.
     * @param type The item type.
     * @return The item instance.
     */
    public static Entity createScenery(Context context, TypeEntity type)
    {
        switch (type)
        {
            case SHEET:
                return new Sheet(context);
            default:
                throw new LionEngineException(FactoryEntity.UNKNOWN_ENTITY_ERROR + type);
        }
    }

    /**
     * Private constructor.
     */
    private FactoryEntityScenery()
    {
        throw new RuntimeException();
    }
}
