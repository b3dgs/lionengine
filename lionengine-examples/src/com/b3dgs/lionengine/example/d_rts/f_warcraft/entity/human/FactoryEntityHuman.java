package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.human;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;

/**
 * Factory entity implementation.
 */
public final class FactoryEntityHuman
{
    /**
     * Create an entity from its type.
     * 
     * @param type The entity type.
     * @param context The context reference.
     * @return The entity instance.
     */
    public static Entity createEntity(TypeEntity type, Context context)
    {
        switch (type)
        {
            case peasant:
                return new Peasant(context);
            case footman:
                return new Footman(context);
            case archer:
                return new Archer(context);
            case townhall_human:
                return new TownhallHuman(context);
            case farm_human:
                return new FarmHuman(context);
            case barracks_human:
                return new BarracksHuman(context);
            case lumbermill_human:
                return new LumbermillHuman(context);
            default:
                throw new LionEngineException("Entity not found: ", type.name());
        }
    }
}
