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
package com.b3dgs.lionengine.game.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileConstraint;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Find all tiles constraints and extract them to an XML file.
 * It will check from an existing map all adjacent tiles combination.
 */
public final class ConstraintsExtractor
{
    /**
     * Get maps tile constraints and concatenate data from each map.
     * 
     * @param levels The level rips used.
     * @param sheetsMedia The sheets media.
     * @return The constraints found.
     */
    public static Map<TileRef, Collection<TileConstraint>> getConstraints(Media[] levels, Media sheetsMedia)
    {
        final Collection<MapTile> mapsSet = new HashSet<MapTile>();
        for (final Media level : levels)
        {
            final MapTile map = new MapTileGame();
            map.create(level, sheetsMedia);
            mapsSet.add(map);
        }
        final MapTile[] maps = mapsSet.toArray(new MapTile[mapsSet.size()]);
        return getConstraints(maps);
    }

    /**
     * Get maps tile constraints and concatenate data from each map.
     * 
     * @param maps The maps list.
     * @return The constraints found.
     */
    public static Map<TileRef, Collection<TileConstraint>> getConstraints(MapTile... maps)
    {
        final Map<TileRef, Collection<TileConstraint>> constraints = new HashMap<TileRef, Collection<TileConstraint>>();
        for (final MapTile map : maps)
        {
            checkConstraints(constraints, map);
        }
        return constraints;
    }

    /**
     * Check map tile constraints.
     * 
     * @param constraints The current constraints.
     * @param map The map reference.
     */
    private static void checkConstraints(Map<TileRef, Collection<TileConstraint>> constraints, MapTile map)
    {
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    checkNeighbor(constraints, map, tile, tx, ty);
                }
            }
        }
    }

    /**
     * Check the tile neighbors.
     * 
     * @param constraints The current constraints.
     * @param map The map reference.
     * @param tile The current tile.
     * @param tx The current horizontal tile index.
     * @param ty The current vertical tile index.
     */
    private static void checkNeighbor(Map<TileRef, Collection<TileConstraint>> constraints,
                                      MapTile map,
                                      Tile tile,
                                      int tx,
                                      int ty)
    {
        final TileRef tileRef = new TileRef(tile.getSheet(), tile.getNumber());
        for (int h = tx - 1; h <= tx + 1; h++)
        {
            for (int v = ty - 1; v <= ty + 1; v++)
            {
                final Orientation orientation = Orientation.get(tx, ty, h, v);
                checkOrientation(constraints, map, orientation, tileRef, h, v);
            }
        }
    }

    /**
     * Check the orientation.
     * 
     * @param constraints The current constraints.
     * @param map The map reference.
     * @param orientation The current orientation.
     * @param tileRef The current tile.
     * @param tx The current horizontal tile index.
     * @param ty The current vertical tile index.
     */
    private static void checkOrientation(Map<TileRef, Collection<TileConstraint>> constraints,
                                         MapTile map,
                                         Orientation orientation,
                                         TileRef tileRef,
                                         int tx,
                                         int ty)
    {
        if (orientation != null)
        {
            final Tile neighbor = map.getTile(tx, ty);
            if (neighbor != null)
            {
                final TileRef neighborRef = new TileRef(neighbor.getSheet(), neighbor.getNumber());
                final TileConstraint constraint = getConstraint(constraints, tileRef, orientation);
                constraint.add(neighborRef);
            }
        }
    }

    /**
     * Get the constraint for the specified tile. Create an empty one if key does not already exists.
     * 
     * @param constraints The current constraints.
     * @param tile The tile reference.
     * @param orientation The constraint orientation.
     * @return The associated constraint.
     */
    private static TileConstraint getConstraint(Map<TileRef, Collection<TileConstraint>> constraints,
                                                TileRef tile,
                                                Orientation orientation)
    {
        if (!constraints.containsKey(tile))
        {
            constraints.put(tile, new ArrayList<TileConstraint>());
        }

        final Collection<TileConstraint> orientationConstraints = constraints.get(tile);
        for (final TileConstraint constraint : orientationConstraints)
        {
            if (orientation.equals(constraint.getOrientation()))
            {
                return constraint;
            }
        }

        final TileConstraint constraint = new TileConstraint(orientation);
        orientationConstraints.add(constraint);

        return constraint;
    }

    /**
     * Private constructor.
     */
    private ConstraintsExtractor()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
