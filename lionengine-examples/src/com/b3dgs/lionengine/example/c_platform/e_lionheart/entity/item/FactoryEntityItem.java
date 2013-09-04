package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;

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
    public static Entity createItem(Context context, TypeEntity type)
    {
        switch (type)
        {
            case TALISMENT:
                return new Talisment(context);
            case POTION_LITTLE:
                return new PotionLittle(context);
            case POTION_BIG:
                return new PotionBig(context);
            case LIFE:
                return new Life(context);
            case SWORD1:
                return new Sword1(context);
            case SWORD2:
                return new Sword2(context);
            case SWORD3:
                return new Sword3(context);
            default:
                throw new LionEngineException(FactoryEntity.UNKNOWN_ENTITY_ERROR + type);
        }
    }

    /**
     * Private constructor.
     */
    private FactoryEntityItem()
    {
        throw new RuntimeException();
    }
}
