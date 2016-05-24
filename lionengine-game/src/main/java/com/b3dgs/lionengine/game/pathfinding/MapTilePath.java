/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.Feature;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.Tiled;

/**
 * Represents the pathfinding feature of a map tile. It works by using {@link TilePath} feature.
 */
public interface MapTilePath extends Feature
{
    /**
     * Load map pathfinding from an external file.
     * 
     * @param pathfindingConfig The pathfinding descriptor.
     */
    void loadPathfinding(Media pathfindingConfig);

    /**
     * Add object ID at this location.
     * 
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @param id The ID to store.
     */
    void addObjectId(int tx, int ty, Integer id);

    /**
     * Remove object ID from this location.
     * 
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @param id The ID to remove.
     */
    void removeObjectId(int tx, int ty, Integer id);

    /**
     * Get objects ID at this location.
     * 
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @return The objects ID found.
     */
    Collection<Integer> getObjectsId(int tx, int ty);

    /**
     * Get tile from specified map location (in tile index). If the returned tile is equal to <code>null</code>, this
     * means that there is not tile at this location. It is not an error, just a way to avoid useless tile storage.
     * 
     * @param tiled The location.
     * @return The tile reference.
     */
    Tile getTile(Tiled tiled);

    /**
     * Get the closest unused location around the area. The returned tile is not blocking, nor used by an object.
     * 
     * @param mover The object moving on map.
     * @param to The tiled destination.
     * @param radius The search radius.
     * @return The closest tile found.
     */
    CoordTile getClosestAvailableTile(Pathfindable mover, Tiled to, int radius);

    /**
     * Get the closest unused location around the area. The returned tile is not blocking, nor used by an object.
     * 
     * @param mover The object moving on map.
     * @param stx The horizontal starting tile index.
     * @param sty The vertical starting tile index.
     * @param dtx The horizontal destination tile index.
     * @param dty The vertical destination tile index.
     * @param radius The search radius.
     * @return The closest tile found.
     */
    CoordTile getClosestAvailableTile(Pathfindable mover, int stx, int sty, int dtx, int dty, int radius);

    /**
     * Search a free area from this location.
     * 
     * @param mover The object moving on map.
     * @param tiled The object to search around.
     * @param radius The search radius.
     * @return The free tile found (<code>null</code> if none).
     */
    CoordTile getFreeTileAround(Pathfindable mover, Tiled tiled, int radius);

    /**
     * Search a free area from this location.
     * 
     * @param mover The object moving on map.
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @param tw The width in tile.
     * @param th The height in tile.
     * @param radius The search radius.
     * @return The free tile found (<code>null</code> if none).
     */
    CoordTile getFreeTileAround(Pathfindable mover, int tx, int ty, int tw, int th, int radius);

    /**
     * Get the cost of the complete path, from start to end.
     * 
     * @param mover The object moving on map.
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @return The total path cost.
     */
    double getCost(Pathfindable mover, int tx, int ty);

    /**
     * Return the categories.
     * 
     * @return The categories.
     */
    Collection<String> getCategories();

    /**
     * Check if area if unused.
     * 
     * @param mover The object moving on map.
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @param tw The width in tile.
     * @param th The height in tile.
     * @param ignoreObjectId The object ID to ignore.
     * @return <code>true</code> if area is free, <code>false</code> else.
     */
    boolean isAreaAvailable(Pathfindable mover, int tx, int ty, int tw, int th, Integer ignoreObjectId);

    /**
     * Check if current location is blocking or not.
     * 
     * @param mover The object moving on map.
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @param ignoreObjectsId The ignore map objects ID checking (objects ID on tile).
     * @return <code>true</code> if blocking, <code>false</code> else.
     */
    boolean isBlocked(Pathfindable mover, int tx, int ty, boolean ignoreObjectsId);
}
