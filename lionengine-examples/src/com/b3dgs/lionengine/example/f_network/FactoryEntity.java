package com.b3dgs.lionengine.example.f_network;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Factory entity implementation. Any entity instantiation has to be made using a factory instance.
 */
class FactoryEntity
        extends FactoryEntityGame<TypeEntity, SetupEntityGame, Entity>
{
    /** Main entity directory name. */
    private static final String ENTITY_DIR = "entity";
    /** Entity desired fps. */
    private final int desiredFps;
    /** Map reference. */
    private final Map map;

    /**
     * Standard constructor.
     * 
     * @param desiredFps The desired fps.
     * @param map The map reference.
     */
    public FactoryEntity(int desiredFps, Map map)
    {
        super(TypeEntity.class);
        this.desiredFps = desiredFps;
        this.map = map;
        loadAll(TypeEntity.values());
    }

    @Override
    protected SetupEntityGame createSetup(TypeEntity id)
    {
        return new SetupEntityGame(new ConfigurableModel(), Media.get(FactoryEntity.ENTITY_DIR, id + ".xml"), false);
    }

    @Override
    public Entity createEntity(TypeEntity type)
    {
        switch (type)
        {
            case mario:
                return createMario(true);
            case goomba:
                return createGoomba(true);
            default:
                return null;
        }
    }

    /**
     * Create a new mario.
     * 
     * @param server <code>true</code> if is server, <code>false</code> if client.
     * @return The instance of mario.
     */
    public Mario createMario(boolean server)
    {
        return new Mario(getSetup(TypeEntity.mario), map, desiredFps, server);
    }

    /**
     * Create a new goomba.
     * 
     * @param server <code>true</code> if is server, <code>false</code> if client.
     * @return The instance of goomba.
     */
    public Goomba createGoomba(boolean server)
    {
        return new Goomba(getSetup(TypeEntity.goomba), map, desiredFps, server);
    }
}
