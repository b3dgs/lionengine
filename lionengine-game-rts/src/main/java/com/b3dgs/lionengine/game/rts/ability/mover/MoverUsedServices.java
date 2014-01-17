/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.rts.ability.mover;

import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Used services interface.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
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
