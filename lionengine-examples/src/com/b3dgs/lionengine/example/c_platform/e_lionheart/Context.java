package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.FactoryEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.HandlerEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.HandlerEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * Context container.
 */
public class Context
{
    /** Camera reference. */
    public final CameraPlatform camera;
    /** Map reference. */
    public final Map map;
    /** Desired fps value. */
    public final int desiredFps;
    /** Factory entity reference. */
    public final FactoryEntity factoryEntity;
    /** Factory effect reference. */
    public final FactoryEffect factoryEffect;
    /** Handler entity reference. */
    public final HandlerEntity handlerEntity;
    /** Handler effect reference. */
    public final HandlerEffect handlerEffect;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     * @param map The map reference.
     * @param desiredFps The desired fps value.
     */
    public Context(CameraPlatform camera, Map map, int desiredFps)
    {
        this.camera = camera;
        this.map = map;
        this.desiredFps = desiredFps;
        factoryEntity = new FactoryEntity();
        factoryEffect = new FactoryEffect();
        handlerEntity = new HandlerEntity(camera);
        handlerEffect = new HandlerEffect();
        factoryEntity.setContext(this);
    }
}
