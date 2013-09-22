package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;

/**
 * Represents the factory of monster entity.
 */
public final class FactoryEntityMonster
{
    /**
     * Create a monster from its type.
     * 
     * @param context The context reference.
     * @param type The item type.
     * @return The item instance.
     */
    public static Entity createMonster(Context context, EntityType type)
    {
        return FactoryEntity.createEntity(context, type, FactoryEntityMonster.class);
    }

    /**
     * Private constructor.
     */
    private FactoryEntityMonster()
    {
        throw new RuntimeException();
    }
}
