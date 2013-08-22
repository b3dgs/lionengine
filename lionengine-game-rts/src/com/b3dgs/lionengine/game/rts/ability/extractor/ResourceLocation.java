package com.b3dgs.lionengine.game.rts.ability.extractor;

import com.b3dgs.lionengine.game.Tiled;

/**
 * Represents the resource location.
 */
class ResourceLocation
        implements Tiled
{
    /** The horizontal location. */
    private int tx;
    /** The vertical location. */
    private int ty;
    /** The width in tile. */
    private int tw;
    /** The height in tile. */
    private int th;

    /**
     * Constructor.
     */
    ResourceLocation()
    {
        tx = 0;
        ty = 0;
        tw = 0;
        th = 0;
    }

    /**
     * Set the location.
     * 
     * @param tx The horizontal location.
     * @param ty The vertical location.
     */
    void setCoordinate(int tx, int ty)
    {
        this.tx = tx;
        this.ty = ty;
    }

    /**
     * Set the size.
     * 
     * @param tw The width in tile.
     * @param th The height in tile.
     */
    void setSize(int tw, int th)
    {
        this.tw = tw;
        this.th = th;
    }

    /*
     * Tiled
     */

    @Override
    public int getLocationInTileX()
    {
        return tx;
    }

    @Override
    public int getLocationInTileY()
    {
        return ty;
    }

    @Override
    public int getWidthInTile()
    {
        return tw;
    }

    @Override
    public int getHeightInTile()
    {
        return th;
    }

}
