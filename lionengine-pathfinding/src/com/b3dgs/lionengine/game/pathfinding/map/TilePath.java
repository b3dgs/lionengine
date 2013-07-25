package com.b3dgs.lionengine.game.pathfinding.map;

import com.b3dgs.lionengine.game.maptile.TileGame;

/**
 * Representation of a default tile, used for pathfinding.
 * 
 * @param <C> collision type used.
 */
public abstract class TilePath<C extends Enum<C>>
        extends TileGame<C>
{
    /** Blocked flag. */
    private boolean blocking;

    /**
     * Create a new blank path tile.
     * 
     * @param width The tile width.
     * @param height The tile height.
     */
    public TilePath(int width, int height)
    {
        super(width, height);
        blocking = false;
    }

    /**
     * Check if this collision is blocking.
     * 
     * @param collision The tile collision.
     * @return <code>true</code> if blocking, <code>false</code> else.
     */
    public abstract boolean checkBlocking(C collision);

    /**
     * Check if current tile is blocking or not.
     * 
     * @return <code>true</code> if blocking, <code>false</code> else.
     */
    public boolean isBlocking()
    {
        return blocking;
    }

    /**
     * Set blocking state.
     * 
     * @param blocking The blocking state.
     */
    public void setBlocking(boolean blocking)
    {
        this.blocking = blocking;
    }
}
