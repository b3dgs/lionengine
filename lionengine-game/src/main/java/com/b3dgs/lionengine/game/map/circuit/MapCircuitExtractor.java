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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGroupType;

/**
 * Map circuit extractor.
 */
public class MapCircuitExtractor
{
    /**
     * Get the tile circuit between two groups.
     * 
     * @param group The initial group.
     * @param neighborGroups The neighbor groups.
     * @return The tile circuit.
     */
    private static Circuit getCircuitGroups(String group, Collection<String> neighborGroups)
    {
        final Iterator<String> iterator = new HashSet<String>(neighborGroups).iterator();
        final String groupIn = iterator.next();
        final String groupOut = iterator.next();
        final CircuitType type = getCircuitType(group, neighborGroups);

        if (groupIn.equals(group))
        {
            return new Circuit(type, group, groupOut);
        }
        return new Circuit(type, group, groupIn);
    }

    /**
     * Get the circuit type from one group to another.
     * 
     * @param groupIn The group in.
     * @param neighborGroups The neighbor groups.
     * @return The tile circuit.
     */
    private static CircuitType getCircuitType(String groupIn, Collection<String> neighborGroups)
    {
        final boolean[] bits = new boolean[CircuitType.BITS];
        int i = CircuitType.BITS - 1;
        for (final String neighborGroup : neighborGroups)
        {
            bits[i] = groupIn.equals(neighborGroup);
            i--;
        }
        return CircuitType.from(bits);
    }

    /** The map reference. */
    private final MapTile map;
    /** The map group reference. */
    private final MapTileGroup mapGroup;

    /**
     * Create the extractor.
     * 
     * @param map The map reference.
     */
    public MapCircuitExtractor(MapTile map)
    {
        this.map = map;
        mapGroup = map.getFeature(MapTileGroup.class);
    }

    /**
     * Get the tile circuit.
     * 
     * @param tile The current tile.
     * @return The tile circuit, <code>null</code> if none.
     */
    public Circuit getCircuit(Tile tile)
    {
        final Collection<String> neighborGroups = getNeighborGroups(tile);
        final Collection<String> groups = new HashSet<String>(neighborGroups);
        final String group = mapGroup.getGroup(tile);
        final Circuit circuit;

        if (groups.size() == 1)
        {
            if (group.equals(groups.iterator().next()))
            {
                circuit = new Circuit(CircuitType.MIDDLE, group, group);
            }
            else
            {
                circuit = new Circuit(CircuitType.BLOCK, group, groups.iterator().next());
            }
        }
        else if (groups.size() > 1 && neighborGroups.size() == CircuitType.BITS && isCircuit(tile))
        {
            circuit = getCircuitGroups(group, neighborGroups);
        }
        else
        {
            circuit = null;
        }

        return circuit;
    }

    /**
     * Get the direct neighbor groups.
     * 
     * @param tile The current tile.
     * @return The 4 neighbor groups.
     */
    private Collection<String> getNeighborGroups(Tile tile)
    {
        final Collection<String> neighborGroups = new ArrayList<String>(CircuitType.BITS);

        addNeighborGroup(neighborGroups, tile, 0, 1);
        addNeighborGroup(neighborGroups, tile, -1, 0);
        addNeighborGroup(neighborGroups, tile, 0, -1);
        addNeighborGroup(neighborGroups, tile, 1, 0);

        return neighborGroups;
    }

    /**
     * Add the neighbor group if exists.
     * 
     * @param neighborGroups The current neighbor groups.
     * @param tile The current tile reference.
     * @param ox The horizontal offset in tile.
     * @param oy The vertical offset in tile.
     */
    private void addNeighborGroup(Collection<String> neighborGroups, Tile tile, int ox, int oy)
    {
        final Tile neighbor = map.getTile(tile.getInTileX() + ox, tile.getInTileY() + oy);
        if (neighbor != null)
        {
            final String neighborGroup = mapGroup.getGroup(neighbor);
            if (neighborGroup != null)
            {
                neighborGroups.add(neighborGroup);
            }
        }
    }

    /**
     * Check if the tile is a tile circuit between two groups.
     * 
     * @param tile The tile to check.
     * @return <code>true</code> if is between two groups, <code>false</code> else.
     */
    private boolean isCircuit(Tile tile)
    {
        return TileGroupType.CIRCUIT == mapGroup.getType(tile);
    }
}
