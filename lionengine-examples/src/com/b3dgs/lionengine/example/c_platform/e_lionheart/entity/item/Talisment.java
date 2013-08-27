package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.game.SetupEntityGame;

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
     */
    public Talisment(SetupEntityGame setup, Map map, int desiredFps)
    {
        super(setup, map, desiredFps);
    }
}
