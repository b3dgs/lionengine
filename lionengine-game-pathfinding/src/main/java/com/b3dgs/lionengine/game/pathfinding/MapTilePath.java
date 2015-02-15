/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.pathfinding;

import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.map.MapTileFeature;
import com.b3dgs.lionengine.game.map.Tile;

/**
 * Describe a tile based map which supports pathfinding. The setRef/getRef functions allows to store special id. Theses
 * id can represent an entity which is over the map. This way, it is really easy and fast to search an entity at
 * specified location.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface MapTilePath
        extends MapTileFeature
{
    /**
     * Create and prepare map memory area. Must be called before assigning tiles.
     * 
     * @param widthInTile The map width in tile (must be strictly positive).
     * @param heightInTile The map height in tile (must be strictly positive).
     */
    void create(int widthInTile, int heightInTile);

    /**
     * Get tile from specified map location (in tile index). If the returned tile is equal to <code>null</code>, this
     * means that there is not tile at this location. It is not an error, just a way to avoid useless tile storage.
     * 
     * @param tiled The location.
     * @return The tile reference.
     */
    Tile getTile(Tiled tiled);

    /**
     * Get the closest unused location around the area. The returned tile is not blocking, nor used by an entity.
     * 
     * @param from The tiled from.
     * @param radius The search size.
     * @param to The tiled destination.
     * @return The closest location found.
     */
    CoordTile getClosestAvailableTile(Tiled from, int radius, Tiled to);

    /**
     * Get the closest unused location around the area. The returned tile is not blocking, nor used by an entity.
     * 
     * @param sx The horizontal location.
     * @param sy The vertical location.
     * @param radius The search size.
     * @param dx The horizontal destination location.
     * @param dy The vertical destination location.
     * @return The closest location found.
     */
    CoordTile getClosestAvailableTile(int sx, int sy, int radius, int dx, int dy);

    /**
     * Search a free area from this area.
     * 
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @param radius The search size.
     * @return The free place found.
     */
    CoordTile getFreeTileAround(int tx, int ty, int radius);

    /**
     * Check if area if unused.
     * 
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @param w The width in tile.
     * @param h The height in tile.
     * @param ignoreRef The id to ignore.
     * @return <code>true</code> if area is free (area id = 0), <code>false</code> else.
     */
    boolean isAreaAvailable(int tx, int ty, int w, int h, int ignoreRef);

    /**
     * Check if current location is blocking or not.
     * 
     * @param mover The object moving on map.
     * @param dx The horizontal destination location.
     * @param dy The vertical destination location.
     * @param ignoreRef The ignore map references array checking (entities id).
     * @return <code>true</code> if blocking, <code>false</code> else.
     */
    boolean isBlocked(Pathfindable mover, int dx, int dy, boolean ignoreRef);

    /**
     * Get the cost of the complete path, from start till end.
     * 
     * @param mover The object moving on map.
     * @param sx The starting location x.
     * @param sy The starting location y.
     * @param dx The ending location x.
     * @param dy The ending location y.
     * @return The total cost.
     */
    double getCost(Pathfindable mover, int sx, int sy, int dx, int dy);

    /**
     * Set reference id.
     * 
     * @param tx The horizontal index.
     * @param ty The vertical index.
     * @param id The id to store.
     */
    void setRef(int tx, int ty, Integer id);

    /**
     * Get reference id.
     * 
     * @param tx The horizontal index.
     * @param ty The vertical index.
     * @return The id found.
     */
    Integer getRef(int tx, int ty);
}
