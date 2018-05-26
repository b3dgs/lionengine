/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;

/**
 * Map transition extractor.
 */
public class MapTransitionExtractor
{
    /**
     * Get the tile transition with one group only.
     * 
     * @param groups The groups (must contain one group).
     * @return The tile transition.
     */
    private static Transition getTransitionSingleGroup(Collection<String> groups)
    {
        final Iterator<String> iterator = groups.iterator();
        final String group = iterator.next();

        return new Transition(TransitionType.CENTER, group, group);
    }

    /**
     * Get the tile transition between two groups.
     * 
     * @param neighborGroups The neighbor groups (must contain two groups).
     * @return The tile transition.
     */
    private static Transition getTransitionTwoGroups(Collection<String> neighborGroups)
    {
        final Iterator<String> iterator = new HashSet<>(neighborGroups).iterator();
        final String groupIn = iterator.next();
        final String groupOut = iterator.next();
        final TransitionType type = getTransitionType(groupIn, neighborGroups);

        return new Transition(type, groupIn, groupOut);
    }

    /**
     * Get the transition type from one group to another.
     * 
     * @param groupIn The group in.
     * @param neighborGroups The neighbor groups.
     * @return The tile transition.
     */
    private static TransitionType getTransitionType(String groupIn, Collection<String> neighborGroups)
    {
        final boolean[] bits = new boolean[TransitionType.BITS];
        int i = 0;
        for (final String neighborGroup : neighborGroups)
        {
            bits[i] = !groupIn.equals(neighborGroup);
            i++;
        }
        return TransitionType.from(bits);
    }

    /** The map reference. */
    private final MapTile map;
    /** The map group reference. */
    private final MapTileGroup mapGroup;

    /**
     * Create the extractor.
     * <p>
     * The {@link MapTile} must provide the following features:
     * </p>
     * <ul>
     * <li>{@link MapTileGroup}</li>
     * </ul>
     * 
     * @param map The map reference.
     * @throws LionEngineException If missing feature.
     */
    public MapTransitionExtractor(MapTile map)
    {
        super();

        this.map = map;
        mapGroup = map.getFeature(MapTileGroup.class);
    }

    /**
     * Get the tile transition.
     * 
     * @param tile The current tile.
     * @return The tile transition, <code>null</code> if none.
     */
    public Transition getTransition(Tile tile)
    {
        final Collection<String> neighborGroups = getNeighborGroups(tile);
        final Collection<String> groups = new HashSet<>(neighborGroups);

        final Transition transition;
        if (groups.size() == 1 && mapGroup.getGroup(tile).equals(groups.iterator().next()))
        {
            transition = getTransitionSingleGroup(groups);
        }
        else if (groups.size() == 2 && neighborGroups.size() == TransitionType.BITS && isTransition(tile))
        {
            transition = getTransitionTwoGroups(neighborGroups);
        }
        else
        {
            transition = null;
        }

        return transition;
    }

    /**
     * Get the direct neighbor angle groups.
     * 
     * @param tile The current tile.
     * @return The 4 neighbor groups.
     */
    private Collection<String> getNeighborGroups(Tile tile)
    {
        final Collection<String> neighborGroups = new ArrayList<>(TransitionType.BITS);

        addNeighborGroup(neighborGroups, tile, 1, -1);
        addNeighborGroup(neighborGroups, tile, -1, -1);
        addNeighborGroup(neighborGroups, tile, 1, 1);
        addNeighborGroup(neighborGroups, tile, -1, 1);

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
            final String neighborGroup = getNeighborGroup(tile, neighbor);
            if (neighborGroup != null)
            {
                neighborGroups.add(neighborGroup);
            }
        }
    }

    /**
     * Get the neighbor group depending if it is a transition tile.
     * 
     * @param tile The current tile.
     * @param neighbor The neighbor tile.
     * @return The neighbor group, <code>null</code> if none.
     */
    private String getNeighborGroup(Tile tile, Tile neighbor)
    {
        final String neighborGroup;
        if (isTransition(neighbor))
        {
            if (isTransition(tile))
            {
                neighborGroup = getOtherGroup(tile, neighbor);
            }
            else
            {
                neighborGroup = null;
            }
        }
        else
        {
            neighborGroup = mapGroup.getGroup(neighbor);
        }
        return neighborGroup;
    }

    /**
     * Get the other transition group, excluding the current group and transition itself.
     * 
     * @param tile The tile reference.
     * @param neighbor The neighbor reference.
     * @return The third group, excluding transition group and tile reference group, <code>null</code> if none.
     */
    private String getOtherGroup(Tile tile, Tile neighbor)
    {
        final String group = mapGroup.getGroup(tile);
        for (final Tile shared : getSharedNeigbors(tile, neighbor))
        {
            final String sharedNeighborGroup = mapGroup.getGroup(shared);
            if (!group.equals(sharedNeighborGroup) && !isTransition(shared))
            {
                return sharedNeighborGroup;
            }
        }
        return null;
    }

    /**
     * Get the neighbors in commons between two tiles.
     * 
     * @param tile1 The first tile.
     * @param tile2 The second tile.
     * @return The neighbors in common (should be 2).
     */
    private Collection<Tile> getSharedNeigbors(Tile tile1, Tile tile2)
    {
        final Collection<Tile> neighbors1 = map.getNeighbors(tile1);
        final Collection<Tile> neighbors2 = map.getNeighbors(tile2);
        final Collection<Tile> sharedNeighbors = new HashSet<>(2);
        for (final Tile neighbor : neighbors1)
        {
            if (neighbors2.contains(neighbor))
            {
                sharedNeighbors.add(neighbor);
            }
        }
        neighbors1.clear();
        neighbors2.clear();
        return sharedNeighbors;
    }

    /**
     * Check if the tile is a tile transition between two groups.
     * 
     * @param tile The tile to check.
     * @return <code>true</code> if is between two groups, <code>false</code> else.
     */
    private boolean isTransition(Tile tile)
    {
        return TileGroupType.TRANSITION == mapGroup.getType(tile);
    }
}
