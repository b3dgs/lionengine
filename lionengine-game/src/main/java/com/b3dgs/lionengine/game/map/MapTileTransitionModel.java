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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileRef;
import com.b3dgs.lionengine.game.tile.TileTransition;
import com.b3dgs.lionengine.game.tile.TileTransitionType;
import com.b3dgs.lionengine.game.tile.TileTransitionsConfig;

/**
 * Map tile transition model implementation.
 * 
 * <p>
 * The map must have the {@link MapTileGroup} feature.
 * </p>
 */
public class MapTileTransitionModel implements MapTileTransition
{
    /** Map reference. */
    private final MapTile map;
    /** Map tile group. */
    private final MapTileGroup mapGroup;
    /** Tile as key. */
    private final Map<TileRef, Collection<TileTransition>> tiles = new HashMap<TileRef, Collection<TileTransition>>();
    /** Groups transitive transitions. */
    private final Map<GroupTransition, Collection<GroupTransition>> transitives;
    /** Transitions as key. */
    private Map<TileTransition, Collection<TileRef>> transitions;

    /**
     * Create a map tile path.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * <p>
     * {@link MapTileGroup}
     * </p>
     * </ul>
     * 
     * @param services The services reference.
     * @throws LionEngineException If services not found.
     */
    public MapTileTransitionModel(Services services)
    {
        Check.notNull(services);
        transitives = new HashMap<GroupTransition, Collection<GroupTransition>>();
        map = services.get(MapTile.class);
        mapGroup = map.getFeature(MapTileGroup.class);
    }

    /**
     * Check if tile is a center tile in order to update directly its neighbor properly.
     * 
     * @param tile The tile reference.
     */
    private void checkCenterTile(Tile tile)
    {
        final String group = mapGroup.getGroup(tile);
        final TileTransition transition = getTransition(tile, group);
        if (TileTransitionType.CENTER.equals(transition.getType()))
        {
            final int tx = tile.getInTileX();
            final int ty = tile.getInTileY();
            for (int v = ty + 1; v >= ty - 1; v--)
            {
                for (int h = tx - 1; h <= tx + 1; h++)
                {
                    final Tile neighbor = map.getTile(h, v);
                    checkCenterTile(tile, neighbor);
                }
            }
        }
        checkTransitives(tile);
    }

    /**
     * Check transitive tiles.
     * 
     * @param tile The tile reference.
     */
    private void checkTransitives(Tile tile)
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
                for (final TileRef ref : mapGroup.getGroup(current.getGroupOut()))
                {
                    if (!group.equals(current.getGroupOut())
                        && TileTransitionType.CENTER.equals(getTransition(ref, current.getGroupOut()).getType()))
                    {
                        map.setTile(map.createTile(ref.getSheet(), ref.getNumber(), neighbor.getX(), neighbor.getY()));
                    }
                }
                break;
            }
        }
    }

    /**
     * Check if tile is a center tile in order to update directly its neighbor properly.
     * 
     * @param tile The tile reference.
     * @param neighbor The tile neighbor reference.
     */
    private void checkCenterTile(Tile tile, Tile neighbor)
    {
        final String group = mapGroup.getGroup(tile);
        if (neighbor != null
            && !isTransitionInverted(neighbor, group)
            && !TileTransitionType.CENTER.equals(getTransition(neighbor, group).getType()))
        {
            map.setTile(map.createTile(tile.getSheet(), tile.getNumber(), neighbor.getX(), neighbor.getY()));
        }
    }

    /**
     * Update the tile depending of its transition. Replace the tile with its right transition representation.
     * 
     * @param tile The tile reference.
     * @return <code>true</code> if updated, <code>false</code> if unchanged.
     */
    private boolean updateTile(Tile tile)
    {
        for (final TileTransition transition : TransitionsExtractor.getTransition(map, tile))
        {
            final String group = mapGroup.getGroup(tile);
            if (group.equals(transition.getGroupOut()))
            {
                final Collection<TileRef> tilesRef = transitions.get(transition);
                if (updateTile(tile, transition, tilesRef))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Update the tile depending of its transition. Replace the tile with its right transition representation.
     * 
     * @param tile The tile reference.
     * @param transition The tile transition.
     * @param tilesRef The associated tiles.
     * @return <code>true</code> if updated, <code>false</code> if unchanged.
     */
    private boolean updateTile(Tile tile, TileTransition transition, Collection<TileRef> tilesRef)
    {
        if (tilesRef != null)
        {
            for (final TileRef tileRef : tilesRef)
            {
                final TileTransitionType type = getTransition(tileRef, transition.getGroupOut()).getType();
                if (!TileTransitionType.CENTER.equals(type))
                {
                    map.setTile(map.createTile(tileRef.getSheet(), tileRef.getNumber(), tile.getX(), tile.getY()));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Resolve unchecked tiles around.
     * 
     * @param checked The list of checked tiles.
     * @param tile The tile to check its neighbor.
     */
    private void resolve(Collection<Tile> checked, Tile tile)
    {
        final Collection<Tile> todo = new HashSet<Tile>();
        final int tx = tile.getInTileX();
        final int ty = tile.getInTileY();
        for (int v = ty + 2; v >= ty - 2; v--)
        {
            for (int h = tx - 2; h <= tx + 2; h++)
            {
                final Tile neighbor = map.getTile(h, v);
                if (neighbor != null && !checked.contains(neighbor))
                {
                    checked.add(neighbor);
                    if (updateTile(neighbor))
                    {
                        todo.add(neighbor);
                    }
                }
            }
        }

        for (final Tile neighbor : todo)
        {
            resolve(checked, neighbor);
        }
        todo.clear();
    }

    /**
     * Fix tile transition image if not center where needed.
     * 
     * @param tile The initial tile reference.
     * @param ref The current tile reference to fix.
     * @param fixed The fixed tiles list.
     */
    private void fix(Tile tile, Tile ref, Collection<Tile> fixed)
    {
        fixed.add(ref);
        for (final TileTransition transition : TransitionsExtractor.getTransition(map, ref))
        {
            final String group = mapGroup.getGroup(tile);
            if (group.equals(transition.getGroupIn())
                && TileTransitionType.CENTER.equals(transition.getType())
                && !transition.getType().equals(getTransition(ref, transition.getGroupOut()).getType()))
            {
                map.setTile(map.createTile(tile.getSheet(), tile.getNumber(), ref.getX(), ref.getY()));
                fixNeigbor(tile, ref, fixed, 1);
            }
        }
    }

    /**
     * Fix tile transition image if not center where needed.
     * 
     * @param tile The initial tile reference.
     * @param ref The current tile reference to fix.
     * @param fixed The fixed tiles list.
     * @param radius The fix radius in tile.
     */
    private void fixNeigbor(Tile tile, Tile ref, Collection<Tile> fixed, int radius)
    {
        final int tx = ref.getInTileX();
        final int ty = ref.getInTileY();
        for (int v = ty + radius; v >= ty - radius; v--)
        {
            for (int h = tx - radius; h <= tx + radius; h++)
            {
                final Tile neighbor = map.getTile(h, v);
                if (neighbor != null && !fixed.contains(neighbor))
                {
                    fix(tile, neighbor, fixed);
                }
            }
        }
    }

    /**
     * Check if tile is an inverted transition compared to the group.
     * 
     * @param tile The tile to check.
     * @param group The group to check with.
     * @return <code>true</code> if transition is inverted, <code>false</code> else.
     */
    private boolean isTransitionInverted(Tile tile, String group)
    {
        final TileTransition neighborTransition = TransitionsExtractor.getTransition(map, tile, false);
        final Iterator<TileRef> iterator = getTiles(neighborTransition).iterator();
        return iterator.hasNext() && TileTransitionType.CENTER.equals(getTransition(iterator.next(), group).getType());
    }

    /*
     * MapTileTransition
     */

    @Override
    public void loadTransitions()
    {
        loadTransitions(Medias.create(map.getSheetsConfig().getParentPath(), TileTransitionsConfig.FILENAME));
    }

    @Override
    public void loadTransitions(Media configTransitions)
    {
        transitions = TileTransitionsConfig.imports(configTransitions);
        tiles.clear();
        transitives.clear();

        for (final Entry<TileTransition, Collection<TileRef>> entry : transitions.entrySet())
        {
            final TileTransition transition = entry.getKey();
            for (final TileRef tileRef : entry.getValue())
            {
                if (!tiles.containsKey(tileRef))
                {
                    tiles.put(tileRef, new HashSet<TileTransition>());
                }
                tiles.get(tileRef).add(transition);
            }
        }

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
            if (first && groupIn.equals(groupStart) || !first && !groupIn.equals(groupOut))
            {
                final GroupTransition transitive = new GroupTransition(groupIn, groupOut);
                if (!localChecked.contains(transitive))
                {
                    localChecked.add(transitive);

                    int count = 0;
                    for (final TileTransition transition : transitions.keySet())
                    {
                        if (transition.getType().isArea()
                            && !groupOut.equals(groupStart)
                            && !groupIn.equals(groupEnd)
                            && groupIn.equals(transition.getGroupIn())
                            && groupOut.equals(transition.getGroupOut()))
                        {
                            count++;
                        }
                    }
                    final boolean nextFirst;
                    if (count >= 8 && (found.size() == 0 || found.getLast().getGroupOut().equals(groupIn)))
                    {
                        found.add(transitive);
                        nextFirst = false;
                    }
                    else
                    {
                        nextFirst = first;
                    }
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
        }
        return ok;
    }

    @Override
    public void resolve(Tile tile)
    {
        final Collection<Tile> checked = new HashSet<Tile>();
        checked.add(tile);

        checkCenterTile(tile);

        updateTile(tile);
        resolve(checked, tile);

        final Collection<Tile> fixed = new HashSet<Tile>();
        fixNeigbor(tile, tile, fixed, 3);
        for (final Tile ref : checked)
        {
            fix(tile, ref, fixed);
        }
        fixed.clear();

        checked.clear();
    }

    @Override
    public TileTransition getTransition(TileRef tile, String group)
    {
        final String groupIn = mapGroup.getGroup(tile);
        if (tiles.containsKey(tile))
        {
            for (final TileTransition transition : tiles.get(tile))
            {
                if (transition.getGroupIn().equals(groupIn) && transition.getGroupOut().equals(group))
                {
                    return transition;
                }
            }
        }
        return new TileTransition(TileTransitionType.NONE, groupIn, groupIn);
    }

    @Override
    public TileTransition getTransition(Tile tile, String group)
    {
        return getTransition(new TileRef(tile), group);
    }

    @Override
    public Collection<GroupTransition> getTransitives(String groupIn, String groupOut)
    {
        final GroupTransition transition = new GroupTransition(groupIn, groupOut);
        if (!transitives.containsKey(transition))
        {
            return Collections.emptyList();
        }
        return transitives.get(transition);
    }

    @Override
    public Collection<TileRef> getTiles(TileTransition transition)
    {
        if (!transitions.containsKey(transition))
        {
            return Collections.emptySet();
        }
        return transitions.get(transition);
    }
}
