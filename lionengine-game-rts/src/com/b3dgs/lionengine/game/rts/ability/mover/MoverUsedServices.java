package com.b3dgs.lionengine.game.rts.ability.mover;

import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Used services interface.
 */
public interface MoverUsedServices
        extends MoverListener, Localizable
{
    /**
     * Get the id.
     * 
     * @return The id.
     */
    Integer getId();

    /**
     * Set the location in tile.
     * 
     * @param tx The location in tile x.
     * @param ty The location in tile y.
     */
    void setLocation(int tx, int ty);

    /**
     * Get horizontal location in tile (location on map).
     * 
     * @return The horizontal location in tile (location on map).
     */
    int getLocationInTileX();

    /**
     * Get vertical location in tile (location on map).
     * 
     * @return The vertical location in tile (location on map).
     */
    int getLocationInTileY();

    /**
     * Set the orientation.
     * 
     * @param orientation The orientation.
     */
    void setOrientation(Orientation orientation);

    /**
     * Get the orientation.
     * 
     * @return The orientation.
     */
    Orientation getOrientation();

    /**
     * Get the width in tile.
     * 
     * @return The width in tile.
     */
    int getWidthInTile();

    /**
     * Get the height in tile.
     * 
     * @return The height in tile.
     */
    int getHeightInTile();
}
