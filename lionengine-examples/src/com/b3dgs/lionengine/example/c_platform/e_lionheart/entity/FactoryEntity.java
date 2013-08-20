package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * Factory entity implementation.
 */
public final class FactoryEntity
        extends FactoryEntityGame<TypeEntity, SetupEntityGame, Entity>
{
    /** Main entity directory name. */
    private static final String ENTITY_DIR = "entity";
    /** Map reference. */
    private final Map map;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Entity desired fps. */
    private final int desiredFps;

    /**
     * Standard constructor.
     * 
     * @param map The map reference.
     * @param camera The camera reference.
     * @param desiredFps The desired fps.
     */
    public FactoryEntity(Map map, CameraPlatform camera, int desiredFps)
    {
        super(TypeEntity.class);
        this.map = map;
        this.camera = camera;
        this.desiredFps = desiredFps;
        loadAll(TypeEntity.values());
    }

    /**
     * Create a new valdyn.
     * 
     * @return The instance of valdyn.
     */
    public Valdyn createValdyn()
    {
        return new Valdyn(getSetup(TypeEntity.valdyn), map, camera, desiredFps);
    }

    /*
     * FactoryEntityGame
     */

    @Override
    public Entity createEntity(TypeEntity type)
    {
        switch (type)
        {
            case valdyn:
                return createValdyn();
            default:
                throw new LionEngineException("Unknown entity: " + type);
        }
    }

    @Override
    protected SetupEntityGame createSetup(TypeEntity id)
    {
        return new SetupEntityGame(Media.get(FactoryEntity.ENTITY_DIR, id + ".xml"));
    }
}
