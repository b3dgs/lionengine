package com.b3dgs.lionengine.game.entity;

import com.b3dgs.lionengine.game.HandlerGame;

/**
 * Designed to handle a type, by updating and rendering it. Mainly used to handle a lot of entity (usually for
 * opponents).
 * 
 * @param <E> Entity type used.
 */
public class HandlerEntityGame<E extends EntityGame>
        extends HandlerGame<Integer, E>
{
    /**
     * Create a new handler.
     */
    public HandlerEntityGame()
    {
        super();
    }

    @Override
    protected Integer getKey(E object)
    {
        return object.getId();
    }
}
