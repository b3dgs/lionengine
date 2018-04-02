/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map;

import java.util.Collection;

import com.b3dgs.lionengine.game.feature.Feature;

/**
 * Handle map tile resizing.
 */
public interface MapTileAppender extends Feature
{
    /**
     * Append an existing map, starting at the specified offsets. Offsets start at the beginning of the map (0, 0).
     * A call to {@link #append(MapTile, int, int)} at ({@link MapTile#getInTileWidth()},
     * {@link MapTile#getInTileHeight()}) will add the new map at the top-right.
     * 
     * @param map The map to append.
     * @param offsetX The horizontal offset in tile (positive).
     * @param offsetY The vertical offset in tile (positive).
     */
    void append(MapTile map, int offsetX, int offsetY);

    /**
     * Append existing maps.
     * 
     * @param maps The maps to append.
     * @param offsetX The horizontal offset factor in tile (positive).
     * @param offsetY The vertical offset factor in tile (positive).
     * @param randX The horizontal random offset in tile.
     * @param randY The vertical random offset in tile.
     */
    void append(Collection<MapTile> maps, int offsetX, int offsetY, int randX, int randY);
}
