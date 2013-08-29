package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.FactoryEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.HandlerEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.TypeEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;

/**
 * Talisment item.
 */
public class Talisment
        extends EntityItem
{
    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     * @param factoryEffect The effect factory.
     * @param handlerEffect The effect handler.
     */
    public Talisment(SetupEntityGame setup, Map map, int desiredFps, FactoryEffect factoryEffect,
            HandlerEffect handlerEffect)
    {
        super(TypeEntity.TALISMENT, setup, map, desiredFps, factoryEffect, handlerEffect, TypeEffect.TAKEN);
    }
}
