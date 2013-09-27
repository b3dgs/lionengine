package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;

/**
 * Represents the factory of scenery entity.
 */
public final class FactoryEntityScenery
{
    /**
     * Create a scenery from its type.
     * 
     * @param level The level reference.
     * @param type The item type.
     * @return The item instance.
     */
    public static Entity createScenery(Level level, EntityType type)
    {
        return FactoryEntity.createEntity(level, type, FactoryEntityScenery.class);
    }

    /**
     * Private constructor.
     */
    private FactoryEntityScenery()
    {
        throw new RuntimeException();
    }
}
