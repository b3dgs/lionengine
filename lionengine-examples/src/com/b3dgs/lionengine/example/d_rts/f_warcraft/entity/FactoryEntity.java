package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourcesLoader;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.human.FactoryEntityHuman;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.orc.FactoryEntityOrc;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;

/**
 * Factory entity implementation.
 */
public class FactoryEntity
        extends FactoryEntityGame<TypeEntity, SetupSurfaceGame, Entity>
{
    /** Context. */
    private Context context;

    /**
     * Constructor.
     */
    public FactoryEntity()
    {
        super(TypeEntity.class);
        loadAll(TypeEntity.values());
    }

    /**
     * Set the context.
     * 
     * @param context The context
     */
    public void setContext(Context context)
    {
        this.context = context;
    }

    /*
     * FactoryEntityGame
     */

    @Override
    public Entity createEntity(TypeEntity type)
    {
        Check.notNull(type, "The type must not be null !");
        switch (type.race)
        {
            case HUMAN:
                return FactoryEntityHuman.createEntity(type, context);
            case ORC:
                return FactoryEntityOrc.createEntity(type, context);
            case NEUTRAL:
                switch (type)
                {
                    case gold_mine:
                        return new GoldMine(context);
                    default:
                        throw new LionEngineException("Entity not found: ", type.name());
                }
            default:
                throw new LionEngineException("Entity not found: ", type.name());
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(TypeEntity id)
    {
        return new SetupSurfaceGame(Media.get(ResourcesLoader.ENTITIES_DIR, id + ".xml"));
    }
}
