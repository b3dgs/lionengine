package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
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
     * @param context The context reference.
     * @param type The item type.
     * @return The item instance.
     */
    public static Entity createItem(Context context, EntityType type)
    {
        return FactoryEntity.createEntity(context, type, FactoryEntityItem.class);
    }

    /**
     * Private constructor.
     */
    private FactoryEntityItem()
    {
        throw new RuntimeException();
    }
}
