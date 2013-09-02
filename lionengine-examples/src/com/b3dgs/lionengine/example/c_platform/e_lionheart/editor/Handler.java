package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.HandlerEntity;

/**
 * Entity editor handler.
 */
public class Handler
        extends HandlerEntity
{
    /**
     * Constructor.
     * 
     * @param factoryEntity The factory entity reference.
     */
    public Handler(FactoryEntity factoryEntity)
    {
        super(null, factoryEntity);
    }

    /**
     * Update the entities.
     */
    public void update()
    {
        update(1.0);
    }

    /*
     * HandlerEntity
     */

    @Override
    public void update(double extrp)
    {
        updateAdd();
        updateRemove();
    }
}
