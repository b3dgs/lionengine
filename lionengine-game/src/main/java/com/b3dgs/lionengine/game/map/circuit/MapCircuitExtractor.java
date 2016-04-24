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

/**
 * Map circuit extractor.
 */
public class MapCircuitExtractor
{
    /** Number of groups defining a circuit. */
    private static final int CIRCUIT_GROUPS_NUMBER = 2;

    /**
     * Get the tile circuit with one group only.
     * 
     * @param groups The groups (must contain one group).
     * @return The tile circuit.
     */
    private static Circuit getCircuitSingleGroup(Collection<String> groups)
    {
        final Iterator<String> iterator = groups.iterator();
        final String group = iterator.next();

        return new Circuit(CircuitType.MIDDLE, group, group);
    }

    /**
     * Get the tile circuit between two groups.
     * 
     * @param neighborGroups The neighbor groups (must contain two groups).
     * @return The tile circuit.
     */
    private static Circuit getCircuitTwoGroups(Collection<String> neighborGroups)
    {
        final Iterator<String> iterator = new HashSet<String>(neighborGroups).iterator();
        final String groupIn = iterator.next();
        final String groupOut = iterator.next();
        final CircuitType type = getCircuitType(groupIn, neighborGroups);

        return new Circuit(type, groupIn, groupOut);
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

    /**
     * Check if tiles are same, considering sheet and number.
     * 
     * @param tile The tile reference.
     * @param other The other tile.
     * @return <code>true</code> if same sheet and number and not <code>null</code>, <code>false</code> else.
     */
    private static boolean isTileSame(Tile tile, Tile other)
    {
        return other != null && other.getNumber() == tile.getNumber() && other.getSheet().equals(tile.getSheet());
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
        final Circuit circuit;

        if (groups.size() == 1 && mapGroup.getGroup(tile).equals(groups.iterator().next()))
        {
            circuit = getCircuitSingleGroup(groups);
        }
        else if (groups.size() == 2 && neighborGroups.size() == CircuitType.BITS && isCircuit(tile))
        {
            circuit = getCircuitTwoGroups(neighborGroups);
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
        for (int tx = 0; tx < map.getInTileWidth(); tx++)
        {
            for (int ty = 0; ty < map.getInTileHeight(); ty++)
            {
                final Tile current = map.getTile(tx, ty);
                if (isTileSame(tile, current) && isCircuitGroup(current))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if tile is a circuit by checking direct neighbors.
     * 
     * @param tile The tile to check.
     * @return <code>true</code> if circuit tile, <code>false</code> else.
     */
    private boolean isCircuitGroup(Tile tile)
    {
        return isCircuitGroup(tile, 0, 1)
               || isCircuitGroup(tile, -1, 0)
               || isCircuitGroup(tile, 0, -1)
               || isCircuitGroup(tile, 1, 0);
    }

    /**
     * Check if tile is a circuit group by looking at the mirror tile from offset.
     * 
     * @param tile The tile to check.
     * @param ox The starting horizontal offset.
     * @param oy The starting vertical offset.
     * @return <code>true</code> if 3 different groups are defined until mirror offset, <code>false</code> else.
     */
    private boolean isCircuitGroup(Tile tile, int ox, int oy)
    {
        final int tx = tile.getInTileX();
        final int ty = tile.getInTileY();
        final Tile tile1 = map.getTile(tx + ox, ty + oy);
        final Tile tile2 = map.getTile(tx - ox, ty - oy);
        if (tile1 != null && tile2 != null)
        {
            final Collection<String> groups = new HashSet<String>(CIRCUIT_GROUPS_NUMBER);
            groups.add(mapGroup.getGroup(tile));
            groups.add(mapGroup.getGroup(tile1));
            groups.add(mapGroup.getGroup(tile2));

            return groups.size() == CIRCUIT_GROUPS_NUMBER;
        }
        return false;
    }
}
