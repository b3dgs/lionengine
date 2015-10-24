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

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Find all tiles transitions and extract them to an XML file.
 * It will check from an existing map all transitions combination.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TileTransitionsExtractor
{
    /**
     * Check map tile transitions.
     *
     * @param map The map reference.
     * @return The transitions found.
     */
    public static Map<TileRef, TileTransition> getTransitions(MapTile map)
    {
        final Map<TileRef, TileTransition> transitions = new HashMap<TileRef, TileTransition>();
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    final TileTransition transition = getTransition(map, tile);
                    transitions.put(new TileRef(tile), transition);
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
     * Disabled constructor.
     */
    private TileTransitionsExtractor()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
