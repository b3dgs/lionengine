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
     * @param level The level reference.
     * @param desiredFps The desired fps value.
     */
    public Context(Level level, int desiredFps)
    {
        this.desiredFps = desiredFps;
        camera = level.handlerEntity.camera;
        map = level.map;
        factoryEntity = level.factoryEntity;
        handlerEntity = level.handlerEntity;
        handlerEffect = new HandlerEffect();
        factoryEffect = new FactoryEffect(handlerEffect);
        factoryEntity.setContext(this);
    }
}
