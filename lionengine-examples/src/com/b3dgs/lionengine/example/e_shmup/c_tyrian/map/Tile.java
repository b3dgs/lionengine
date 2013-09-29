package com.b3dgs.lionengine.example.e_shmup.c_tyrian.map;

import com.b3dgs.lionengine.game.map.TileGame;

/**
 * Tile implementation.
 */
public final class Tile
        extends TileGame<TileCollision>
{
    /**
     * @see TileGame#TileGame(int, int, Integer, int, Enum)
     */
    public Tile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }
}
