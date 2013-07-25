package com.b3dgs.lionengine.game.platform.map;

import com.b3dgs.lionengine.game.maptile.MapTileGame;
import com.b3dgs.lionengine.game.maptile.TileGame;

/**
 * Default platform map implementation.
 * 
 * @param <C> Collision type used.
 * @param <T> Tile type used.
 */
public abstract class MapTilePlatform<C extends Enum<C>, T extends TileGame<C>>
        extends MapTileGame<C, T>
{
    /**
     * Create a new tile map.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     */
    public MapTilePlatform(int tileWidth, int tileHeight)
    {
        super(tileWidth, tileHeight);
    }
}
