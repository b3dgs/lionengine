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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileRef;
import com.b3dgs.lionengine.game.tile.TileTransition;
import com.b3dgs.lionengine.game.tile.TileTransitionType;

/**
 * Find all map transition and extract them.
 */
public final class TransitionsExtractor
{
    /**
     * Check map tile transitions and concatenate data..
     *
     * @param levels The level rips used.
     * @param sheetsMedia The sheets media.
     * @param groupsMedia The groups media.
     * @return The transitions found.
     */
    public static Map<TileTransition, Collection<TileRef>> getTransitions(Media[] levels,
                                                                          Media sheetsMedia,
                                                                          Media groupsMedia)
    {
        final Collection<MapTile> mapsSet = new HashSet<MapTile>();
        for (final Media level : levels)
        {
            final MapTile map = new MapTileGame();
            map.create(level, sheetsMedia);

            final MapTileGroup mapGroup = new MapTileGroupModel();
            mapGroup.loadGroups(groupsMedia);
            map.addFeature(mapGroup);
            mapsSet.add(map);
        }
        final MapTile[] maps = mapsSet.toArray(new MapTile[mapsSet.size()]);
        return getTransitions(maps);
    }

    /**
     * Check map tile transitions and concatenate data..
     *
     * @param maps The maps reference.
     * @return The transitions found.
     */
    public static Map<TileTransition, Collection<TileRef>> getTransitions(MapTile... maps)
    {
        final Map<TileTransition, Collection<TileRef>> transitions;
        transitions = new HashMap<TileTransition, Collection<TileRef>>();

        for (final MapTile map : maps)
        {
            final Map<TileTransition, Collection<TileRef>> currents = getTransitions(map);
            for (final Entry<TileTransition, Collection<TileRef>> entry : currents.entrySet())
            {
                final TileTransition transition = entry.getKey();
                final Collection<TileRef> tiles = entry.getValue();
                if (transitions.containsKey(transition))
                {
                    transitions.get(transition).addAll(tiles);
                }
                else
                {
                    transitions.put(transition, tiles);
                }
            }
        }

        return transitions;
    }

    /**
     * Check map tile transitions.
     *
     * @param map The map reference.
     * @return The transitions found.
     */
    public static Map<TileTransition, Collection<TileRef>> getTransitions(MapTile map)
    {
        final Map<TileTransition, Collection<TileRef>> transitions;
        transitions = new HashMap<TileTransition, Collection<TileRef>>();
        for (int ty = 1; ty < map.getInTileHeight() - 1; ty++)
        {
            for (int tx = 1; tx < map.getInTileWidth() - 1; tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    checkTileTransition(transitions, map, tile);
                }
            }
        }
        return transitions;
    }

    /**
     * Get the tile transitions on both sides.
     * 
     * @param map The map reference.
     * @param tile The current tile.
     * @return The tile transition.
     */
    public static TileTransition[] getTransition(MapTile map, Tile tile)
    {
        final TileTransition[] transitions = new TileTransition[2];
        transitions[0] = getTransition(map, tile, false);
        transitions[1] = getTransition(map, tile, true);
        return transitions;
    }

    /**
     * Get the tile transition.
     * 
     * @param map The map reference.
     * @param tile The current tile.
     * @param inverted <code>true</code> to get inverted transition, <code>false</code> for normal.
     * @return The tile transition.
     */
    public static TileTransition getTransition(MapTile map, Tile tile, boolean inverted)
    {
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final String groupA = mapGroup.getGroup(tile);
        final Boolean[] bytes = new Boolean[TileTransitionType.BITS];
        final int tx = tile.getInTileX();
        final int ty = tile.getInTileY();
        String groupB = null;
        int i = 0;
        for (int v = ty + 1; v >= ty - 1; v--)
        {
            for (int h = tx - 1; h <= tx + 1; h++)
            {
                final Tile neighbor = map.getTile(h, v);
                if (neighbor != null)
                {
                    final String groupNeighbor = mapGroup.getGroup(neighbor);
                    if (groupB == null && !groupNeighbor.equals(groupA))
                    {
                        groupB = groupNeighbor;
                    }
                    if (groupB == null || groupNeighbor.equals(groupB) || groupNeighbor.equals(groupA))
                    {
                        bytes[i] = Boolean.valueOf(CollisionGroup.same(groupNeighbor, groupA));
                    }
                    else
                    {
                        bytes[i] = null;
                    }
                }
                i++;
            }
        }
        return getTransition(groupA, groupB, bytes, inverted);
    }

    /**
     * Find tile transition and add it to the collection.
     * 
     * @param transitions The transitions collection.
     * @param map The map reference.
     * @param tile The tile to check.
     */
    private static void checkTileTransition(Map<TileTransition, Collection<TileRef>> transitions,
                                            MapTile map,
                                            Tile tile)
    {
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final TileRef ref = new TileRef(tile);
        final String groupRef = mapGroup.getGroup(ref);
        for (final TileTransition transition : getTransition(map, tile))
        {
            if (!TileTransitionType.NONE.equals(transition.getType()) && groupRef.equals(transition.getGroupOut()))
            {
                final Collection<TileRef> tiles = getTiles(transitions, transition);
                tiles.add(ref);
            }
        }
    }

    /**
     * Get the transition type from its bytes array.
     * 
     * @param bytes The bytes array.
     * @param inverted <code>true</code> to get inverted transition, <code>false</code> for normal.
     * @return The transition type.
     */
    private static TileTransitionType getTransitionType(Boolean[] bytes, boolean inverted)
    {
        if (inverted)
        {
            final Boolean[] bitsInv = new Boolean[bytes.length];
            for (int j = 0; j < bitsInv.length; j++)
            {
                bitsInv[j] = bytes[bytes.length - j - 1];
            }
            return TileTransitionType.from(bitsInv);
        }
        return TileTransitionType.from(bytes);
    }

    /**
     * Get the tile transition from raw data.
     * 
     * @param groupA The first group.
     * @param groupB The second group.
     * @param bytes The bytes array.
     * @param inverted <code>true</code> to get inverted transition, <code>false</code> for normal.
     * @return The tile transition.
     */
    private static TileTransition getTransition(String groupA, String groupB, Boolean[] bytes, boolean inverted)
    {
        final TileTransitionType type = getTransitionType(bytes, inverted);
        final String group;
        if (groupB == null)
        {
            group = groupA;
        }
        else
        {
            group = groupB;
        }
        if (inverted)
        {
            return new TileTransition(type, group, groupA);
        }
        return new TileTransition(type, groupA, group);
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
     * Private constructor.
     */
    private TransitionsExtractor()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
