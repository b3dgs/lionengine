package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;

/**
 * Entity editor handler.
 */
public class Handler
        extends HandlerEntityGame<Entity>
{
    /**
     * Constructor.
     */
    public Handler()
    {
        super();
    }

    /**
     * Update the entity list.
     */
    public void update()
    {
        updateAdd();
        updateRemove();
    }
}
