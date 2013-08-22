package com.b3dgs.lionengine.game.rts;

import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.game.WorldGame;

/**
 * Default Rts world model, designed to contain game elements (map, player, entities...).
 */
public abstract class WorldRts
        extends WorldGame
{
    /**
     * Create a new world.
     * 
     * @param sequence The sequence reference.
     */
    public WorldRts(Sequence sequence)
    {
        super(sequence);
    }
}
