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
package com.b3dgs.lionengine.game.map;

import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.trait.Pathfindable;

/**
 * Represents the pathfinding feature of a map tile. It works by using {@link TilePath} feature.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.game.map.astar
 */
public interface MapTilePath
        extends MapTileFeature
{
    /**
     * Load map pathfinding from an external file.
     * Map must have the {@link MapTileCollision} feature and collisions loaded with
     * {@link MapTileCollision#loadCollisions(Media, Media)}.
     * 
     * @param pathfindingConfig The pathfinding descriptor.
     * @throws LionEngineException If error when reading pathfinding or {@link MapTileCollision} feature missing.
     */
    void loadPathfinding(Media pathfindingConfig) throws LionEngineException;

    /**
     * Add object id at this location.
     * 
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @param id The id to store.
     */
    void addObjectId(int tx, int ty, Integer id);

    /**
     * Get objects id at this location.
     * 
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @return The objects id found.
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
     * Check if area if unused.
     * 
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @param w The width in tile.
     * @param h The height in tile.
     * @param ignoreObjectId The object id to ignore.
     * @return <code>true</code> if area is free (area id = 0), <code>false</code> else.
     */
    boolean isAreaAvailable(int tx, int ty, int w, int h, Integer ignoreObjectId);

    /**
     * Check if current location is blocking or not.
     * 
     * @param mover The object moving on map.
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     * @param ignoreObjectsId The ignore map objects id checking (objects id on tile).
     * @return <code>true</code> if blocking, <code>false</code> else.
     */
    boolean isBlocked(Pathfindable mover, int tx, int ty, boolean ignoreObjectsId);
}
