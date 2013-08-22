package com.b3dgs.lionengine.game.shmup;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.maptile.MapTileGame;
import com.b3dgs.lionengine.game.maptile.TileGame;

/**
 * Map tile shmup implementation.
 * 
 * @param <C> Collision type used.
 * @param <T> Tile type used.
 */
public abstract class MapTileShmup<C extends Enum<C>, T extends TileGame<C>>
        extends MapTileGame<C, T>
{
    /**
     * Create a new tile map.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     */
    public MapTileShmup(int tileWidth, int tileHeight)
    {
        super(tileWidth, tileHeight);
    }

    /*
     * MapTileGame
     */

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        render(g, camera.getViewHeight(), camera.getLocationIntX(), camera.getLocationIntY(),
                (int) Math.floor(camera.getViewWidth() / (double) tileWidth),
                (int) Math.floor(camera.getViewHeight() / (double) tileHeight),
                -camera.getViewX() + camera.getLocationIntX(), camera.getViewY() + camera.getLocationIntY());
    }
}
