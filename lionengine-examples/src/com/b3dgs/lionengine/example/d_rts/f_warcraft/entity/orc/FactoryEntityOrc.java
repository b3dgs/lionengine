package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.orc;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;

/**
 * Factory entity implementation.
 */
public final class FactoryEntityOrc
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
            case peon:
                return new Peon(context);
            case grunt:
                return new Grunt(context);
            case spearman:
                return new Spearman(context);
            case townhall_orc:
                return new TownhallOrc(context);
            case farm_orc:
                return new FarmOrc(context);
            case barracks_orc:
                return new BarracksOrc(context);
            case lumbermill_orc:
                return new LumbermillOrc(context);
            default:
                throw new LionEngineException("Entity not found: ", type.name());
        }
    }

    /**
     * Constructor.
     */
    private FactoryEntityOrc()
    {
        throw new RuntimeException();
    }
}
