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
package com.b3dgs.lionengine.game.map.circuit;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.map.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGroupType;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Default circuit extractor implementation.
 */
final class CircuitsExtractorImpl implements CircuitsExtractor
{
    /**
     * Get map tile circuits.
     *
     * @param map The map reference.
     * @return The circuits found with their associated tiles.
     */
    private static Map<Circuit, Collection<TileRef>> getCircuits(MapTile map)
    {
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final Map<Circuit, Collection<TileRef>> circuits = new HashMap<Circuit, Collection<TileRef>>();
        final MapCircuitExtractor extractor = new MapCircuitExtractor(map);
        for (int ty = 1; ty < map.getInTileHeight() - 1; ty++)
        {
            for (int tx = 1; tx < map.getInTileWidth() - 1; tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null && TileGroupType.CIRCUIT == mapGroup.getType(tile))
                {
                    checkCircuit(circuits, extractor, map, tile);
                }
            }
        }
        return circuits;
    }

    /**
     * Check the tile circuit and add it to circuits collection if valid.
     * 
     * @param circuits The circuits collection.
     * @param extractor The circuit extractor.
     * @param map The map reference.
     * @param tile The tile to check.
     */
    private static void checkCircuit(Map<Circuit, Collection<TileRef>> circuits,
                                     MapCircuitExtractor extractor,
                                     MapTile map,
                                     Tile tile)
    {
        final Circuit circuit = extractor.getCircuit(tile);
        if (circuit != null)
        {
            final TileRef ref = new TileRef(tile);
            getTiles(circuits, circuit).add(ref);
            checkTransitionGroups(circuits, circuit, map, ref);
        }
    }

    /**
     * Check transitions groups, and create compatible circuit on the fly.
     * 
     * @param circuits The circuits collection.
     * @param circuit The circuit found.
     * @param map The map reference.
     * @param ref The tile ref to add.
     */
    private static void checkTransitionGroups(Map<Circuit, Collection<TileRef>> circuits,
                                              Circuit circuit,
                                              MapTile map,
                                              TileRef ref)
    {
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final MapTileTransition mapTransition = map.getFeature(MapTileTransition.class);
        final String group = mapGroup.getGroup(ref);
        for (final String groupTransition : mapGroup.getGroups())
        {
            if (mapTransition.getTransitives(group, groupTransition).size() == 1)
            {
                final Circuit transitiveCircuit = new Circuit(circuit.getType(), group, groupTransition);
                getTiles(circuits, transitiveCircuit).add(ref);
            }
        }
    }

    /**
     * Get the tiles for the circuit. Create empty list if new circuit.
     * 
     * @param circuits The circuits data.
     * @param circuit The circuit type.
     * @return The associated tiles.
     */
    private static Collection<TileRef> getTiles(Map<Circuit, Collection<TileRef>> circuits, Circuit circuit)
    {
        if (!circuits.containsKey(circuit))
        {
            circuits.put(circuit, new HashSet<TileRef>());
        }
        return circuits.get(circuit);
    }

    /**
     * Create the extractor.
     */
    CircuitsExtractorImpl()
    {
        super();
    }

    /*
     * CircuitsExtractor
     */

    @Override
    public Map<Circuit, Collection<TileRef>> getCircuits(Collection<Media> levels,
                                                         Media sheetsConfig,
                                                         Media groupsConfig)
    {
        final Collection<MapTile> mapsSet = new HashSet<MapTile>();
        for (final Media level : levels)
        {
            final Services services = new Services();
            final MapTile map = new MapTileGame();
            services.add(map);
            map.create(level, sheetsConfig);

            final MapTileGroup mapGroup = new MapTileGroupModel();
            mapGroup.loadGroups(groupsConfig);
            map.addFeature(mapGroup);

            final MapTileTransition mapTransition = new MapTileTransitionModel(services);
            map.addFeature(mapTransition);
            mapTransition.loadTransitions(levels, sheetsConfig, groupsConfig);

            mapsSet.add(map);
        }
        final MapTile[] maps = mapsSet.toArray(new MapTile[mapsSet.size()]);
        return getCircuits(maps);
    }

    @Override
    public Map<Circuit, Collection<TileRef>> getCircuits(MapTile... maps)
    {
        final Map<Circuit, Collection<TileRef>> circuits = new HashMap<Circuit, Collection<TileRef>>();
        for (final MapTile map : maps)
        {
            final Map<Circuit, Collection<TileRef>> currents = getCircuits(map);
            for (final Entry<Circuit, Collection<TileRef>> entry : currents.entrySet())
            {
                final Circuit circuit = entry.getKey();
                final Collection<TileRef> tiles = entry.getValue();
                if (circuits.containsKey(circuit))
                {
                    circuits.get(circuit).addAll(tiles);
                }
                else
                {
                    circuits.put(circuit, tiles);
                }
            }
        }
        return circuits;
    }
}
