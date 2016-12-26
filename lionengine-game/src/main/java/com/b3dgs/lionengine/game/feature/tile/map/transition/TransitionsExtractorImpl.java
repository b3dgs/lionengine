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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Default transition extractor implementation.
 */
final class TransitionsExtractorImpl implements TransitionsExtractor
{
    /**
     * Get map tile transitions.
     *
     * @param map The map reference.
     * @return The transitions found with their associated tiles.
     */
    private static Map<Transition, Collection<TileRef>> getTransitions(MapTile map)
    {
        final Map<Transition, Collection<TileRef>> transitions = new HashMap<Transition, Collection<TileRef>>();
        final MapTransitionExtractor extractor = new MapTransitionExtractor(map);
        for (int ty = 1; ty < map.getInTileHeight() - 1; ty++)
        {
            for (int tx = 1; tx < map.getInTileWidth() - 1; tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    checkTransition(transitions, extractor, tile);
                }
            }
        }
        return transitions;
    }

    /**
     * Check the tile transition and add it to transitions collection if valid.
     * 
     * @param transitions The transitions collection.
     * @param extractor The transition extractor.
     * @param tile The tile to check.
     */
    private static void checkTransition(Map<Transition, Collection<TileRef>> transitions,
                                        MapTransitionExtractor extractor,
                                        Tile tile)
    {
        final Transition transition = extractor.getTransition(tile);
        if (transition != null)
        {
            getTiles(transitions, transition).add(new TileRef(tile));

            final Transition symetric = new Transition(transition.getType().getSymetric(),
                                                       transition.getOut(),
                                                       transition.getIn());
            getTiles(transitions, symetric).add(new TileRef(tile));
        }
    }

    /**
     * Get the tiles for the transition. Create empty list if new transition.
     * 
     * @param transitions The transitions data.
     * @param transition The transition type.
     * @return The associated tiles.
     */
    private static Collection<TileRef> getTiles(Map<Transition, Collection<TileRef>> transitions, Transition transition)
    {
        if (!transitions.containsKey(transition))
        {
            transitions.put(transition, new HashSet<TileRef>());
        }
        return transitions.get(transition);
    }

    /**
     * Create the extractor.
     */
    TransitionsExtractorImpl()
    {
        super();
    }

    /*
     * TransitionsExtractor
     */

    @Override
    public Map<Transition, Collection<TileRef>> getTransitions(Collection<Media> levels,
                                                               Media sheetsConfig,
                                                               Media groupsConfig)
    {
        final Collection<MapTile> mapsSet = new HashSet<MapTile>();
        for (final Media level : levels)
        {
            final MapTile map = new MapTileGame();
            map.create(level, sheetsConfig);

            final MapTileGroup mapGroup = new MapTileGroupModel();
            mapGroup.loadGroups(groupsConfig);
            map.addFeature(mapGroup);
            mapsSet.add(map);
        }
        final MapTile[] maps = mapsSet.toArray(new MapTile[mapsSet.size()]);
        return getTransitions(maps);
    }

    @Override
    public Map<Transition, Collection<TileRef>> getTransitions(MapTile... maps)
    {
        final Map<Transition, Collection<TileRef>> transitions = new HashMap<Transition, Collection<TileRef>>();
        for (final MapTile map : maps)
        {
            final Map<Transition, Collection<TileRef>> currents = getTransitions(map);
            for (final Entry<Transition, Collection<TileRef>> entry : currents.entrySet())
            {
                final Transition transition = entry.getKey();
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
}
