package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Valdyn;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Factory entity implementation.
 */
public class FactoryEntity
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
            case valdyn:
                return createValdyn();
            default:
                return null;
        }
    }

    /**
     * Create a new valdyn.
     * 
     * @return The instance of valdyn.
     */
    public Valdyn createValdyn()
    {
        return new Valdyn(getSetup(TypeEntity.valdyn), map, desiredFps);
    }
}
