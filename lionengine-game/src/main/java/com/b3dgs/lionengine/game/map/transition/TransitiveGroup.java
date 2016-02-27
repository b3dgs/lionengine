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
package com.b3dgs.lionengine.game.map.transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Handle the transitivity between groups for transitions.
 */
public class TransitiveGroup
{
    /** Map reference. */
    private final MapTile map;
    /** Map tile group. */
    private final MapTileGroup mapGroup;
    /** Map transition. */
    private final MapTileTransition mapTransition;
    /** Groups transitive transitions. */
    private final Map<GroupTransition, Collection<GroupTransition>> transitives;

    /**
     * Create the transitive group handler.
     * 
     * @param map The map reference.
     * @param mapGroup The map group reference.
     * @param mapTransition The map transition reference.
     */
    public TransitiveGroup(MapTile map, MapTileGroup mapGroup, MapTileTransition mapTransition)
    {
        this.map = map;
        this.mapGroup = mapGroup;
        this.mapTransition = mapTransition;
        transitives = new HashMap<GroupTransition, Collection<GroupTransition>>();
    }

    /**
     * Load transitive data.
     */
    public void load()
    {
        transitives.clear();
        for (final String groupIn : mapGroup.getGroups())
        {
            for (final String groupOut : mapGroup.getGroups())
            {
                if (!groupIn.equals(groupOut))
                {
                    findTransitives(groupIn, groupOut);
                }
            }
        }
    }

    /**
     * Check transitive tiles.
     * 
     * @param tile The tile reference.
     */
    public void checkTransitives(Tile tile)
    {
        final int tx = tile.getInTileX();
        final int ty = tile.getInTileY();
        for (int v = ty + 2; v >= ty - 2; v--)
        {
            checkTransitives(tile, tx - 2, v);
            checkTransitives(tile, tx + 2, v);
        }
        for (int h = tx - 2; h <= tx + 2; h++)
        {
            checkTransitives(tile, h, ty - 2);
            checkTransitives(tile, h, ty + 2);
        }
    }

    /**
     * Get the transitive groups list to reach a group from another.
     * 
     * @param groupIn The first group.
     * @param groupOut The last group.
     * @return The transitive groups.
     */
    public Collection<GroupTransition> getTransitives(String groupIn, String groupOut)
    {
        final GroupTransition transition = new GroupTransition(groupIn, groupOut);
        if (!transitives.containsKey(transition))
        {
            return Collections.emptyList();
        }
        return transitives.get(transition);
    }

    /**
     * Get the associated tiles with the direct transitive transition.
     * 
     * @param transition The transition reference.
     * @return The associated tiles from direct transitive transition.
     */
    public Collection<TileRef> getDirectTransitiveTiles(Transition transition)
    {
        final String groupOut = transition.getOut();
        final Collection<GroupTransition> transitives = getTransitives(transition.getIn(), groupOut);
        if (transitives.size() != 2)
        {
            return Collections.emptySet();
        }
        final GroupTransition groupTransition = transitives.iterator().next();
        return mapTransition.getTiles(new Transition(transition.getType(), groupTransition.getOut(), groupOut));
    }

    /**
     * Find transitive groups.
     * 
     * @param groupIn The first group.
     * @param groupOut The last group.
     */
    private void findTransitives(String groupIn, String groupOut)
    {
        final GroupTransition groupTransition = new GroupTransition(groupIn, groupOut);
        if (!transitives.containsKey(groupTransition))
        {
            transitives.put(groupTransition, new ArrayList<GroupTransition>());
        }

        final Collection<GroupTransition> localChecked = new HashSet<GroupTransition>();
        final Collection<GroupTransition> found = transitives.get(groupTransition);

        final Deque<GroupTransition> collect = new LinkedList<GroupTransition>();
        checkTransitive(groupIn, groupIn, groupOut, localChecked, collect, true);

        found.addAll(collect);

        localChecked.clear();
    }

    /**
     * Check transitive groups.
     * 
     * @param groupStart The first group.
     * @param groupIn The local group in.
     * @param groupEnd The last group.
     * @param localChecked Current checked transitions.
     * @param found Transitions found.
     * @param first <code>true</code> if first pass, <code>false</code> else.
     * @return <code>true</code> if transitive valid, <code>false</code> else.
     */
    private boolean checkTransitive(String groupStart,
                                    String groupIn,
                                    String groupEnd,
                                    Collection<GroupTransition> localChecked,
                                    Deque<GroupTransition> found,
                                    boolean first)
    {
        boolean ok = false;
        for (final String groupOut : mapGroup.getGroups())
        {
            final GroupTransition transitive = new GroupTransition(groupIn, groupOut);
            final boolean firstOrNext = first && groupIn.equals(groupStart) || !first && !groupIn.equals(groupOut);
            if (firstOrNext && !localChecked.contains(transitive))
            {
                localChecked.add(transitive);
                final boolean nextFirst = countTransitions(groupStart, transitive, groupEnd, found, first);

                if (groupOut.equals(groupEnd))
                {
                    ok = true;
                }
                else
                {
                    checkTransitive(groupStart, groupOut, groupEnd, localChecked, found, nextFirst);
                }
            }
        }
        return ok;
    }

    /**
     * Count the number of area transitions and check if pass is still first or not. Add valid transitive to list.
     * 
     * @param groupStart The first group.
     * @param transitive The current group transition.
     * @param groupEnd The last group.
     * @param found Transitions found.
     * @param first <code>true</code> if first pass, <code>false</code> else.
     * @return <code>true</code> if still first pass, <code>false</code> else.
     */
    private boolean countTransitions(String groupStart,
                                     GroupTransition transitive,
                                     String groupEnd,
                                     Deque<GroupTransition> found,
                                     boolean first)
    {
        final String groupIn = transitive.getIn();
        final String groupOut = transitive.getOut();
        int count = 0;
        for (final Transition transition : mapTransition.getTransitions())
        {
            final boolean valid = groupIn.equals(transition.getIn()) && groupOut.equals(transition.getOut());
            if (transition.getType().isArea() && !groupOut.equals(groupStart) && !groupIn.equals(groupEnd) && valid)
            {
                count++;
            }
        }
        if (count >= TransitionType.AREA_TRANSITIONS && (found.isEmpty() || found.getLast().getOut().equals(groupIn)))
        {
            found.add(transitive);
            return false;
        }
        return first;
    }

    /**
     * Check transitive tiles.
     * 
     * @param tile The tile reference.
     * @param tx The horizontal index neighbor.
     * @param ty The vertical index neighbor.
     */
    private void checkTransitives(Tile tile, int tx, int ty)
    {
        final Tile neighbor = map.getTile(tx, ty);
        final String group = mapGroup.getGroup(neighbor);
        if (neighbor != null)
        {
            for (final GroupTransition current : getTransitives(mapGroup.getGroup(tile), mapGroup.getGroup(neighbor)))
            {
                for (final TileRef ref : mapGroup.getGroup(current.getOut()))
                {
                    if (!group.equals(current.getOut())
                        && TransitionType.CENTER == mapTransition.getTransition(ref, current.getOut()).getType())
                    {
                        map.setTile(map.createTile(ref.getSheet(), ref.getNumber(), neighbor.getX(), neighbor.getY()));
                    }
                }
                break;
            }
        }
    }
}
