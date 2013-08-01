package com.b3dgs.lionengine.game.platform.map;

import com.b3dgs.lionengine.game.maptile.MapTileGame;
import com.b3dgs.lionengine.game.platform.EntityPlatform;

/**
 * Default platform map implementation.
 * 
 * @param <C> Collision type used.
 * @param <T> Tile type used.
 */
public abstract class MapTilePlatform<C extends Enum<C>, T extends TilePlatform<C>>
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

    /**
     * Get the tile at the entity location.
     * 
     * @param entity The entity.
     * @param offsetX The horizontal offset search.
     * @param offsetY The vertical offset search.
     * @return The tile found at the entity.
     */
    public T getTile(EntityPlatform<C, T> entity, int offsetX, int offsetY)
    {
        final int tx = (entity.getLocationIntX() + offsetX) / getTileWidth();
        final int ty = (entity.getLocationIntY() + offsetY) / getTileHeight();
        return getTile(tx, ty);
    }
}
