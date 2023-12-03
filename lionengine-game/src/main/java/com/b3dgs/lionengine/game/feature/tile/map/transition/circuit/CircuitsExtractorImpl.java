/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransitionModel;

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
    private static Map<Circuit, Collection<Integer>> getCircuits(MapTile map)
    {
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final Map<Circuit, Collection<Integer>> circuits = new HashMap<>();
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
    private static void checkCircuit(Map<Circuit, Collection<Integer>> circuits,
                                     MapCircuitExtractor extractor,
                                     MapTile map,
                                     Tile tile)
    {
        final Circuit circuit = extractor.getCircuit(tile);
        if (circuit != null)
        {
            final Integer number = tile.getKey();
            getTiles(circuits, circuit).add(number);
            checkTransitionGroups(circuits, circuit, map, number);
        }
    }

    /**
     * Check transitions groups, and create compatible circuit on the fly.
     * 
     * @param circuits The circuits collection.
     * @param circuit The circuit found.
     * @param map The map reference.
     * @param number The tile number to add.
     */
    private static void checkTransitionGroups(Map<Circuit, Collection<Integer>> circuits,
                                              Circuit circuit,
                                              MapTile map,
                                              Integer number)
    {
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final MapTileTransition mapTransition = map.getFeature(MapTileTransition.class);
        final String group = mapGroup.getGroup(number);
        for (final String groupTransition : mapGroup.getGroups())
        {
            if (mapTransition.getTransitives(group, groupTransition).size() == 1)
            {
                final Circuit transitiveCircuit = new Circuit(circuit.getType(), group, groupTransition);
                getTiles(circuits, transitiveCircuit).add(number);
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
    private static Collection<Integer> getTiles(Map<Circuit, Collection<Integer>> circuits, Circuit circuit)
    {
        Collection<Integer> set = circuits.get(circuit);
        if (set == null)
        {
            set = new HashSet<>();
            circuits.put(circuit, set);
        }
        return set;
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
    public Map<Circuit, Collection<Integer>> getCircuits(Collection<Media> levels,
                                                         Media sheetsConfig,
                                                         Media groupsConfig)
    {
        final Collection<MapTile> mapsSet = new HashSet<>(levels.size());
        for (final Media level : levels)
        {
            final MapTileGame map = new MapTileGame();
            map.create(level, sheetsConfig);

            final MapTileGroup mapGroup = map.addFeature(new MapTileGroupModel());
            final MapTileTransition mapTransition = map.addFeature(new MapTileTransitionModel());

            mapGroup.loadGroups(groupsConfig);
            mapTransition.loadTransitions(levels, sheetsConfig, groupsConfig);

            mapsSet.add(map);
        }
        return getCircuits(mapsSet);
    }

    @Override
    public Map<Circuit, Collection<Integer>> getCircuits(Collection<MapTile> maps)
    {
        final Map<Circuit, Collection<Integer>> circuits = new HashMap<>();
        for (final MapTile map : maps)
        {
            final Map<Circuit, Collection<Integer>> currents = getCircuits(map);
            for (final Entry<Circuit, Collection<Integer>> entry : currents.entrySet())
            {
                final Circuit circuit = entry.getKey();
                final Collection<Integer> tiles = entry.getValue();
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
