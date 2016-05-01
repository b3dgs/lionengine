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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.map.GroupTransition;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.map.transition.Transition;
import com.b3dgs.lionengine.game.map.transition.TransitionType;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGroupType;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Map tile circuit model implementation.
 * 
 * <p>
 * The map must have the {@link MapTileGroup} feature.
 * </p>
 */
public class MapTileCircuitModel implements MapTileCircuit
{
    /** Map reference. */
    private final MapTile map;
    /** Map tile group. */
    private final MapTileGroup mapGroup;
    /** Map tile transition. */
    private final MapTileTransition mapTransition;
    /** Circuits as key. */
    private final Map<Circuit, Collection<TileRef>> circuits;
    /** Map circuit extractor. */
    private final MapCircuitExtractor extractor;

    /**
     * Create a map tile circuit.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * <li>{@link MapTileGroup}</li>
     * <li>{@link MapTileTransition}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @throws LionEngineException If services not found.
     */
    public MapTileCircuitModel(Services services)
    {
        Check.notNull(services);
        circuits = new HashMap<Circuit, Collection<TileRef>>();
        map = services.get(MapTile.class);
        mapGroup = map.getFeature(MapTileGroup.class);
        mapTransition = map.getFeature(MapTileTransition.class);
        extractor = new MapCircuitExtractor(map);
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
                final TileRef ref = mapTransition.getTiles(new Transition(TransitionType.CENTER, group, group))
                                                 .iterator()
                                                 .next();
                final Tile newTile = map.createTile(ref.getSheet(), ref.getNumber(), tile.getX(), tile.getY());
                map.setTile(newTile);
                mapTransition.resolve(newTile);
                map.setTile(tile);
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
        final Iterator<TileRef> iterator = getTiles(circuit).iterator();
        while (iterator.hasNext())
        {
            final TileRef newTile = iterator.next();
            if (mapGroup.getGroup(newTile).equals(mapGroup.getGroup(tile)))
            {
                map.setTile(map.createTile(newTile.getSheet(), newTile.getNumber(), neighbor.getX(), neighbor.getY()));
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
        final Collection<String> groups = new HashSet<String>();
        final String groupIn = mapGroup.getGroup(tile);
        for (final Circuit circuit : circuits.keySet())
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
                    if (transitive.isEmpty())
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
    public void loadCircuits()
    {
        loadCircuits(Medias.create(map.getSheetsConfig().getParentPath(), CircuitsConfig.FILENAME));
    }

    @Override
    public void loadCircuits(Media config)
    {
        circuits.clear();
        circuits.putAll(CircuitsConfig.imports(config));
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
    public Collection<TileRef> getTiles(Circuit circuit)
    {
        if (!circuits.containsKey(circuit))
        {
            return Collections.emptySet();
        }
        return circuits.get(circuit);
    }
}
