package com.b3dgs.lionengine.game.platform;

import java.util.List;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.maptile.MapTile;
import com.b3dgs.lionengine.game.platform.map.TilePlatform;

/**
 * Rastered version of an abstractPlatformEntity.
 * 
 * @param <C> Collision type used.
 * @param <T> Tile type used.
 */
public abstract class EntityPlatformRastered<C extends Enum<C>, T extends TilePlatform<C>>
        extends EntityPlatform<C, T>
{
    /** List of rastered frames. */
    protected final List<SpriteAnimated> rastersAnim;
    /** Rastered flag. */
    protected final boolean rastered;
    /** Smooth raster flag. */
    protected final boolean smooth;

    /**
     * Create a new rastered platform entity from an existing, sharing the same surface.
     * 
     * @param setup The setup reference.
     * @param map The map reference.
     */
    public EntityPlatformRastered(SetupEntityPlatformRastered setup, MapTile<C, T> map)
    {
        super(setup, map);
        rastersAnim = setup.rastersAnim;
        rastered = setup.rasterFile != null;
        smooth = setup.smoothRaster;
    }

    /**
     * Get the raster offset (can be overrided).
     * 
     * @return The raster offset
     */
    protected int getRasterOffset()
    {
        return 0;
    }

    /**
     * Get raster index from location.
     * 
     * @return The raster index based on vertical location.
     */
    private int getRasterIndex()
    {
        final double value = (getLocationY() + getRasterOffset()) / map.getTileHeight();
        final int i = (int) value % SetupEntityPlatformRastered.MAX_RASTERS_R;
        int index = i;

        if (!this.smooth && index > SetupEntityPlatformRastered.MAX_RASTERS_M)
        {
            index = SetupEntityPlatformRastered.MAX_RASTERS_M - (index - SetupEntityPlatformRastered.MAX_RASTERS);
        }
        return index;
    }

    /**
     * Get raster animation from raster index.
     * 
     * @param rasterIndex The raster index.
     * @return The raster animated sprite.
     */
    private SpriteAnimated getRasterAnim(int rasterIndex)
    {
        return rastersAnim.get(rasterIndex);
    }

    /*
     * EntityPlatform
     */

    @Override
    public void render(Graphic g, CameraPlatform camera)
    {
        final SpriteAnimated anim;
        if (rastered)
        {
            anim = getRasterAnim(getRasterIndex());
        }
        else
        {
            anim = sprite;
        }
        final int x = camera.getViewpointX(getLocationIntX());
        final int y = camera.getViewpointY(getLocationIntY());
        anim.render(g, x, y);
    }
}
