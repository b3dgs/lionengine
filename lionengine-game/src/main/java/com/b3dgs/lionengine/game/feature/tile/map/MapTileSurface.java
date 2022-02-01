/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile.map;

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Describe a map using tile for its representation. This is the lower level interface to describe a 2D map using tiles.
 * Each tiles are stored vertically and then horizontally.
 * <p>
 * A sheet id represents a tilesheet number (surface number containing tiles). A map can have one or more
 * sheets. The map picks its resources from a sheets folder, which must contains the files images.
 * </p>
 */
@FeatureInterface
public interface MapTileSurface extends Feature, MapTile, Listenable<TileSetListener>
{
    /**
     * Resize map with new size.
     * 
     * @param newWidth The new width in tile.
     * @param newHeight The new height in tile.
     */
    void resize(int newWidth, int newHeight);

    /**
     * Get the associated media.
     * 
     * @return The associated media, <code>null</code> if none.
     */
    Media getMedia();
}
