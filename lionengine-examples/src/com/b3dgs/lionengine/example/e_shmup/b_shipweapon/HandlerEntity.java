package com.b3dgs.lionengine.example.e_shmup.b_shipweapon;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;

/**
 * Handler entity.
 */
final class HandlerEntity
        extends HandlerEntityGame<EntityGame>
{
    /**
     * Constructor.
     */
    public HandlerEntity()
    {
        super();
    }

    /*
     * HandlerEntityGame
     */
    
    @Override
    protected void render(Graphic g, EntityGame entity)
    {
        // Nothing to do
    }
}
