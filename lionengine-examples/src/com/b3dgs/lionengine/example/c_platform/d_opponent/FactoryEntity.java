package com.b3dgs.lionengine.example.c_platform.d_opponent;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;

/**
 * Factory entity implementation. Any entity instantiation has to be made using a factory instance.
 */
class FactoryEntity
        extends FactoryEntityGame<TypeEntity, SetupEntityGame, Entity>
{
    /** Main entity directory name. */
    private static final String ENTITY_DIR = "entities";
    /** Map reference. */
    private final Map map;
    /** Entity desired fps. */
    private final int desiredFps;

    /**
     * Constructor.
     * 
     * @param desiredFps The desired fps.
     * @param map The map reference.
     */
    FactoryEntity(Map map, int desiredFps)
    {
        super(TypeEntity.class);
        this.map = map;
        this.desiredFps = desiredFps;
        loadAll(TypeEntity.values());
    }

    /**
     * Create a new mario.
     * 
     * @return The instance of mario.
     */
    Mario createMario()
    {
        return new Mario(getSetup(TypeEntity.mario), map, desiredFps);
    }

    /**
     * Create a new goomba.
     * 
     * @return The instance of goomba.
     */
    Goomba createGoomba()
    {
        return new Goomba(getSetup(TypeEntity.goomba), map, desiredFps);
    }

    /*
     * FactoryEntityGame
     */

    @Override
    protected SetupEntityGame createSetup(TypeEntity id)
    {
        return new SetupEntityGame(Media.get(FactoryEntity.ENTITY_DIR, id + ".xml"));
    }

    @Override
    public Entity createEntity(TypeEntity type)
    {
        switch (type)
        {
            case mario:
                return createMario();
            case goomba:
                return createGoomba();
            default:
                throw new LionEngineException("Unknown entity type: " + type);
        }
    }
}
