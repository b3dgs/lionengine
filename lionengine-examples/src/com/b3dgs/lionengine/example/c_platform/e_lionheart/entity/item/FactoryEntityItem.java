package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;

/**
 * Represents the factory of item entity.
 */
public final class FactoryEntityItem
{
    /**
     * Create an item from its type.
     * 
     * @param level The level reference.
     * @param type The item type.
     * @return The item instance.
     */
    public static Entity createItem(Level level, EntityType type)
    {
        return FactoryEntity.createEntity(level, type, FactoryEntityItem.class);
    }

    /**
     * Private constructor.
     */
    private FactoryEntityItem()
    {
        throw new RuntimeException();
    }
}
