/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGame;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;
import com.b3dgs.lionengine.game.feature.tile.map.transition.GroupTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.Transition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.TransitionType;

/**
 * Map tile circuit model implementation.
 */
public class MapTileCircuitModel extends FeatureAbstract implements MapTileCircuit
{
    /** Circuits as key. */
    private final Map<Circuit, Collection<Integer>> circuits = new HashMap<>();
    /** Map circuit extractor. */
    private MapCircuitExtractor extractor;

    /** Map tile surface. */
    private MapTileSurface map;
    /** Map tile group. */
    private MapTileGroup mapGroup;
    /** Map tile transition. */
    private MapTileTransition mapTransition;

    /**
     * Create feature.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link MapTileSurface}</li>
     * <li>{@link MapTileGroup}</li>
     * <li>{@link MapTileTransition}</li>
     * </ul>
     */
    public MapTileCircuitModel()
    {
        super();
    }

    /**
     * Update tile.
     * 
     * @param tile The tile reference.
     */
    private void updateTransitiveTile(Tile tile)
    {
        final Circuit circuit = extractor.getCircuit(tile);
        if (circuit == null || !circuits.containsKey(circuit))
        {
            final String group = getTransitiveGroup(circuit, tile);
            if (group != null)
            {
                final int old = tile.getNumber();
                final Integer ref = mapTransition.getTiles(new Transition(TransitionType.CENTER, group, group))
                                                 .iterator()
                                                 .next();
                final Tile newTile = new TileGame(ref.intValue(),
                                                  tile.getInTileX(),
                                                  tile.getInTileY(),
                                                  tile.getWidth(),
                                                  tile.getHeight());
                map.setTile(newTile.getInTileX(), newTile.getInTileY(), newTile.getNumber());
                mapTransition.resolve(newTile);
                map.setTile(newTile.getInTileX(), newTile.getInTileY(), old);
            }
        }
    }

    /**
     * Update tile.
     * 
     * @param tile The tile reference.
     * @param ox The horizontal offset to update.
     * @param oy The vertical offset to update.
     */
    private void updateTile(Tile tile, int ox, int oy)
    {
        final int tx = tile.getInTileX();
        final int ty = tile.getInTileY();

        final Tile neighbor = map.getTile(tx + ox, ty + oy);
        if (neighbor != null)
        {
            updateNeigbor(tile, neighbor);
        }
    }

    /**
     * Update neighbor.
     * 
     * @param tile The tile reference.
     * @param neighbor The neighbor reference.
     */
    private void updateNeigbor(Tile tile, Tile neighbor)
    {
        final String group = mapGroup.getGroup(tile);
        final String neighborGroup = mapGroup.getGroup(neighbor);
        final Circuit circuit = getCircuitOverTransition(extractor.getCircuit(neighbor), neighbor);
        if (group.equals(neighborGroup))
        {
            updateTile(tile, neighbor, circuit);
        }
        else if (neighborGroup.equals(circuit.getIn()))
        {
            updateTile(neighbor, neighbor, circuit);
        }
    }

    /**
     * Update tile with new representation.
     * 
     * @param tile The tile placed.
     * @param neighbor The tile to update.
     * @param circuit The circuit to set.
     */
    private void updateTile(Tile tile, Tile neighbor, Circuit circuit)
    {
        final Iterator<Integer> iterator = getTiles(circuit).iterator();
        while (iterator.hasNext())
        {
            final Integer newTile = iterator.next();
            if (mapGroup.getGroup(newTile).equals(mapGroup.getGroup(tile)))
            {
                map.setTile(neighbor.getInTileX(), neighbor.getInTileY(), newTile.intValue());
                break;
            }
        }
    }

    /**
     * Get the circuit, supporting over existing transition.
     * 
     * @param circuit The initial circuit.
     * @param neighbor The neighbor tile which can be a transition.
     * @return The new circuit or original one.
     */
    private Circuit getCircuitOverTransition(Circuit circuit, Tile neighbor)
    {
        final String group = getTransitiveGroup(circuit, neighbor);
        final Circuit newCircuit;
        if (TileGroupType.TRANSITION == mapGroup.getType(circuit.getIn()))
        {
            newCircuit = new Circuit(circuit.getType(), group, circuit.getOut());
        }
        else if (TileGroupType.TRANSITION == mapGroup.getType(circuit.getOut()))
        {
            newCircuit = new Circuit(circuit.getType(), circuit.getIn(), group);
        }
        else
        {
            newCircuit = circuit;
        }
        return newCircuit;
    }

    /**
     * Get the transitive group by replacing the transition group name with the plain one.
     * 
     * @param initialCircuit The initial circuit.
     * @param tile The tile reference.
     * @return The plain group name.
     */
    private String getTransitiveGroup(Circuit initialCircuit, Tile tile)
    {
        final Set<Circuit> circuitSet = circuits.keySet();
        final Collection<String> groups = new HashSet<>(circuitSet.size());
        final String groupIn = mapGroup.getGroup(tile);
        for (final Circuit circuit : circuitSet)
        {
            final String groupOut = circuit.getOut();
            for (final Tile neighbor : map.getNeighbors(tile))
            {
                final String groupNeighbor = mapGroup.getGroup(neighbor);
                if (groupNeighbor.equals(groupOut) && !groupNeighbor.equals(groupIn))
                {
                    return groupOut;
                }
            }
            groups.add(groupOut);
        }
        return getShortestTransitiveGroup(groups, initialCircuit);
    }

    /**
     * Get the shortest transitive group.
     * 
     * @param groups The groups list.
     * @param initialCircuit The initial circuit.
     * @return The transitive group from shortest.
     */
    private String getShortestTransitiveGroup(Collection<String> groups, Circuit initialCircuit)
    {
        int min = Integer.MAX_VALUE;
        String groupOut = null;
        final String group = initialCircuit.getIn();

        for (final String group2 : groups)
        {
            if (!group2.equals(group)
                && !group2.equals(initialCircuit.getIn())
                && !group2.equals(initialCircuit.getOut())
                && TileGroupType.TRANSITION != mapGroup.getType(group2))
            {
                final Collection<GroupTransition> transitive = mapTransition.getTransitives(group, group2);
                final int size = transitive.size();
                if (size < min)
                {
                    if (transitive.isEmpty()) // CHECKSTYLE IGNORE LINE: TrailingComment|NestedIfDepth
                    {
                        groupOut = group2;
                    }
                    else
                    {
                        groupOut = transitive.iterator().next().getOut();
                    }
                    min = size;
                }
            }

        }
        return groupOut;
    }

    /*
     * MapTileCircuit
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        map = provider.getFeature(MapTileSurface.class);
        mapGroup = provider.getFeature(MapTileGroup.class);
        mapTransition = provider.getFeature(MapTileTransition.class);
        extractor = new MapCircuitExtractor(map);
    }

    @Override
    public void loadCircuits(Media circuitsConfig)
    {
        loadCircuits(CircuitsConfig.imports(circuitsConfig));
    }

    @Override
    public void loadCircuits(Collection<Media> levels, Media sheetsConfig, Media groupsConfig)
    {
        final CircuitsExtractor circuitsExtractor = new CircuitsExtractorImpl();
        loadCircuits(circuitsExtractor.getCircuits(levels, sheetsConfig, groupsConfig));
    }

    @Override
    public void loadCircuits(Map<Circuit, Collection<Integer>> circuits)
    {
        this.circuits.clear();
        this.circuits.putAll(circuits);
    }

    @Override
    public void resolve(Tile tile)
    {
        updateTransitiveTile(tile);
        updateTile(tile, 0, 0);
        updateTile(tile, 0, 1);
        updateTile(tile, -1, 0);
        updateTile(tile, 0, -1);
        updateTile(tile, 1, 0);
    }

    @Override
    public Collection<Integer> getTiles(Circuit circuit)
    {
        if (!circuits.containsKey(circuit))
        {
            return Collections.emptySet();
        }
        return circuits.get(circuit);
    }
}
