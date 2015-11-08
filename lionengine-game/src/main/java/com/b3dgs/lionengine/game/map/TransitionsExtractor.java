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
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileRef;
import com.b3dgs.lionengine.game.tile.TileTransition;

/**
 * Find all map transition and extract them.
 */
public final class TransitionsExtractor
{
    /**
     * Check map tile transitions.
     *
     * @param map The map reference.
     * @return The transitions found.
     */
    public static Map<TileTransition, Collection<TileRef>> getTransitions(MapTile map)
    {
        final Map<TileTransition, Collection<TileRef>> transitions;
        transitions = new EnumMap<TileTransition, Collection<TileRef>>(TileTransition.class);
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    final TileTransition transition = getTransition(map, tile);
                    final Collection<TileRef> tiles = getTiles(transitions, transition);
                    tiles.add(new TileRef(tile));
                }
            }
        }
        return transitions;
    }

    /**
     * Get the tile transition.
     * 
     * @param map The map reference.
     * @param tile The current tile.
     * @return The tile transition.
     */
    public static TileTransition getTransition(MapTile map, Tile tile)
    {
        final Boolean[] bits = new Boolean[TileTransition.BITS];
        final int tx = tile.getX() / tile.getWidth();
        final int ty = tile.getY() / tile.getHeight();
        int i = 0;
        for (int v = ty + 1; v >= ty - 1; v--)
        {
            for (int h = tx - 1; h <= tx + 1; h++)
            {
                final Tile neighbor = map.getTile(h, v);
                if (neighbor != null)
                {
                    bits[i] = Boolean.valueOf(neighbor.getGroup().equals(tile.getGroup()));
                }
                i++;
            }
        }
        return TileTransition.from(bits);
    }

    /**
     * Get the tiles for the transition. Create empty list if new transition.
     * 
     * @param transitions The transitions data.
     * @param transition The transition type.
     * @return The associated tiles.
     */
    private static Collection<TileRef> getTiles(Map<TileTransition, Collection<TileRef>> transitions,
                                                TileTransition transition)
    {
        if (!transitions.containsKey(transition))
        {
            transitions.put(transition, new HashSet<TileRef>());
        }
        return transitions.get(transition);
    }

    /**
     * Utility class.
     */
    private TransitionsExtractor()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
