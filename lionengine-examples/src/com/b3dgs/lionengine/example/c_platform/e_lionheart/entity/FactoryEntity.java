package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.util.Locale;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.FactoryEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.HandlerEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item.Talisment;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * Handle the entity creation by containing all necessary object for their instantiation.
 */
public final class FactoryEntity
        extends FactoryEntityGame<TypeEntity, SetupEntityGame, Entity>
{
    /** Unknown entity error message. */
    public static final String UNKNOWN_ENTITY_ERROR = "Unknown entity: ";
    /** Map reference. */
    private final Map map;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Desired fps. */
    private final int desiredFps;
    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;

    /**
     * Standard constructor.
     * 
     * @param camera The camera reference.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     * @param handlerEffect The handler effect.
     */
    public FactoryEntity(CameraPlatform camera, Map map, int desiredFps, HandlerEffect handlerEffect)
    {
        super(TypeEntity.class);
        this.camera = camera;
        this.map = map;
        this.desiredFps = desiredFps;
        this.factoryEffect = new FactoryEffect();
        this.handlerEffect = handlerEffect;
        loadAll(TypeEntity.values());
    }

    /**
     * Create a talisment.
     * 
     * @return The instance of talisment.
     */
    public Talisment createTalisment()
    {
        return new Talisment(getSetup(TypeEntity.TALISMENT), map, desiredFps, factoryEffect, handlerEffect);
    }

    /**
     * Create a new valdyn.
     * 
     * @return The instance of valdyn.
     */
    public Valdyn createValdyn()
    {
        return new Valdyn(getSetup(TypeEntity.VALDYN), camera, map, desiredFps);
    }

    /*
     * FactoryEntityGame
     */

    @Override
    public Entity createEntity(TypeEntity type)
    {
        switch (type)
        {
            case TALISMENT:
                return createTalisment();
            case VALDYN:
                return createValdyn();
            default:
                throw new LionEngineException(FactoryEntity.UNKNOWN_ENTITY_ERROR + type);
        }
    }

    @Override
    protected SetupEntityGame createSetup(TypeEntity id)
    {
        final Media media = Media.get(AppLionheart.ENTITIES_DIR, id.getCategory().getFolder(),
                id.name().toLowerCase(Locale.ENGLISH) + AppLionheart.CONFIG_FILE_EXTENSION);
        return new SetupEntityGame(media);
    }
}
