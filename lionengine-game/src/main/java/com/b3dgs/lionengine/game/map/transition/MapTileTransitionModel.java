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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.map.GroupTransition;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Map tile transition model implementation.
 * 
 * <p>
 * The map must have the {@link MapTileGroup} feature.
 * </p>
 */
public class MapTileTransitionModel implements MapTileTransition
{
    /**
     * Get the new transition type from two transitions.
     * 
     * @param a The inner transition.
     * @param b The outer transition.
     * @param ox The horizontal offset to update.
     * @param oy The vertical offset to update.
     * @return The new transition type, <code>null</code> if none.
     */
    private static TransitionType getTransition(TransitionType a, TransitionType b, int ox, int oy)
    {
        final TransitionType type = getTransitionHorizontalVertical(a, b, ox, oy);
        if (type != null)
        {
            return type;
        }
        return getTransitionDiagonals(a, b, ox, oy);
    }

    /**
     * Get the new transition type from two transitions on horizontal and vertical axis.
     * 
     * @param a The inner transition.
     * @param b The outer transition.
     * @param ox The horizontal offset to update.
     * @param oy The vertical offset to update.
     * @return The new transition type, <code>null</code> if none.
     */
    private static TransitionType getTransitionHorizontalVertical(TransitionType a, TransitionType b, int ox, int oy)
    {
        final TransitionType type;
        if (ox == -1 && oy == 0)
        {
            type = TransitionType.from(!b.getDownRight(), a.getDownLeft(), !b.getUpRight(), a.getUpLeft());
        }
        else if (ox == 1 && oy == 0)
        {
            type = TransitionType.from(a.getDownRight(), !b.getDownLeft(), a.getUpRight(), !b.getUpLeft());
        }
        else if (ox == 0 && oy == 1)
        {
            type = TransitionType.from(!b.getDownRight(), !b.getDownLeft(), a.getUpRight(), a.getUpLeft());
        }
        else if (ox == 0 && oy == -1)
        {
            type = TransitionType.from(a.getDownRight(), a.getDownLeft(), !b.getUpRight(), !b.getUpLeft());
        }
        else
        {
            type = null;
        }
        return type;
    }

    /**
     * Get the new transition type from two transitions on diagonals.
     * 
     * @param a The inner transition.
     * @param b The outer transition.
     * @param ox The horizontal offset to update.
     * @param oy The vertical offset to update.
     * @return The new transition type, <code>null</code> if none.
     */
    private static TransitionType getTransitionDiagonals(TransitionType a, TransitionType b, int ox, int oy)
    {
        final TransitionType type;
        if (ox == -1 && oy == 1)
        {
            type = TransitionType.from(!b.getDownRight(), !b.getDownLeft(), !b.getUpRight(), a.getUpLeft());
        }
        else if (ox == 1 && oy == 1)
        {
            type = TransitionType.from(!b.getDownRight(), !b.getDownLeft(), a.getUpRight(), !b.getUpLeft());
        }
        else if (ox == -1 && oy == -1)
        {
            type = TransitionType.from(!b.getDownRight(), a.getDownLeft(), !b.getUpRight(), !b.getUpLeft());
        }
        else if (ox == 1 && oy == -1)
        {
            type = TransitionType.from(a.getDownRight(), !b.getDownLeft(), !b.getUpRight(), !b.getUpLeft());
        }
        else
        {
            type = null;
        }
        return type;
    }

    /** Map reference. */
    private final MapTile map;
    /** Map tile group. */
    private final MapTileGroup mapGroup;
    /** Tile as key. */
    private final Map<TileRef, Collection<Transition>> tiles;
    /** Transitions as key. */
    private final Map<Transition, Collection<TileRef>> transitions;
    /** Existing group links. */
    private final Collection<GroupTransition> groupLinks;
    /** Transitive group handler. */
    private TransitiveGroup transitiveGroup;

    /**
     * Create a map tile transition.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * <li>{@link MapTileGroup}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @throws LionEngineException If services not found.
     */
    public MapTileTransitionModel(Services services)
    {
        Check.notNull(services);

        tiles = new HashMap<TileRef, Collection<Transition>>();
        transitions = new HashMap<Transition, Collection<TileRef>>();
        groupLinks = new HashSet<GroupTransition>();
        map = services.get(MapTile.class);
        mapGroup = map.getFeature(MapTileGroup.class);
    }

    /**
     * Resolve current tile and add to resolve list extra tiles.
     * 
     * @param resolved The resolved tiles.
     * @param toResolve The next tiles to resolve.
     * @param tile The tile to resolve.
     */
    private void resolve(Collection<Tile> resolved, Collection<Tile> toResolve, Tile tile)
    {
        updateTile(resolved, toResolve, tile, -1, 0);
        updateTile(resolved, toResolve, tile, 1, 0);
        updateTile(resolved, toResolve, tile, 0, 1);
        updateTile(resolved, toResolve, tile, 0, -1);

        updateTile(resolved, toResolve, tile, -1, 1);
        updateTile(resolved, toResolve, tile, 1, 1);
        updateTile(resolved, toResolve, tile, -1, -1);
        updateTile(resolved, toResolve, tile, 1, -1);
    }

    /**
     * Update tile.
     * 
     * @param resolved The resolved tiles.
     * @param toResolve Tiles to resolve after.
     * @param tile The tile reference.
     * @param ox The horizontal offset to update.
     * @param oy The vertical offset to update.
     */
    private void updateTile(Collection<Tile> resolved, Collection<Tile> toResolve, Tile tile, int ox, int oy)
    {
        final int tx = tile.getInTileX();
        final int ty = tile.getInTileY();

        final Tile neighbor = map.getTile(tx + ox, ty + oy);
        if (neighbor != null)
        {
            updateNeigbor(resolved, toResolve, tile, neighbor, ox, oy);
        }
    }

    /**
     * Update neighbor.
     * 
     * @param resolved The resolved tiles.
     * @param toResolve Tiles to resolve after.
     * @param tile The tile reference.
     * @param neighbor The neighbor reference.
     * @param ox The horizontal offset to update.
     * @param oy The vertical offset to update.
     */
    private void updateNeigbor(Collection<Tile> resolved,
                               Collection<Tile> toResolve,
                               Tile tile,
                               Tile neighbor,
                               int ox,
                               int oy)
    {
        final String group = mapGroup.getGroup(tile);
        final String neighborGroup = mapGroup.getGroup(neighbor);

        final Transition transitionA = getTransition(tile, neighborGroup);
        final Transition transitionB = getTransition(neighbor, group);

        if (transitionA != null && transitionB != null)
        {
            final TransitionType transitionTypeA = transitionA.getType();
            final TransitionType transitionTypeB = transitionB.getType();

            final TransitionType newType = getTransition(transitionTypeA, transitionTypeB, ox, oy);
            if (newType != null)
            {
                final Transition newTransition = new Transition(newType, transitionA.getOut(), transitionB.getIn());
                updateTransition(resolved, toResolve, tile, neighbor, neighborGroup, newTransition);
            }
        }
    }

    /**
     * Update transition.
     * 
     * @param resolved The resolved tiles.
     * @param toResolve Tiles to resolve after.
     * @param tile The tile reference.
     * @param neighbor The neighbor reference.
     * @param neighborGroup The neighbor group.
     * @param newTransition The new transition.
     */
    private void updateTransition(Collection<Tile> resolved,
                                  Collection<Tile> toResolve,
                                  Tile tile,
                                  Tile neighbor,
                                  String neighborGroup,
                                  Transition newTransition)
    {
        if (!neighborGroup.equals(newTransition.getIn()))
        {
            updateTile(resolved, toResolve, tile, neighbor, newTransition);
        }
    }

    /**
     * Update tile.
     * 
     * @param resolved The resolved tiles.
     * @param toResolve Tiles to resolve after.
     * @param tile The tile placed.
     * @param neighbor The tile to update.
     * @param transition The transition to set.
     */
    private void updateTile(Collection<Tile> resolved,
                            Collection<Tile> toResolve,
                            Tile tile,
                            Tile neighbor,
                            Transition transition)
    {
        final Iterator<TileRef> iterator = getTiles(transition).iterator();
        if (iterator.hasNext())
        {
            final TileRef ref = iterator.next();
            final Tile newTile = map.createTile(ref.getSheet(), ref.getNumber(), neighbor.getX(), neighbor.getY());
            map.setTile(newTile);
            resolved.add(newTile);
        }
        else
        {
            final Tile newTile = map.createTile(tile.getSheet(), tile.getNumber(), neighbor.getX(), neighbor.getY());
            final String groupA = mapGroup.getGroup(tile);
            final String groupB = mapGroup.getGroup(neighbor);

            // Used to fix transitions not found
            if (!neighbor.equals(newTile)
                && (!isCenter(neighbor)
                    || groupA.equals(groupB)
                    || groupLinks.contains(new GroupTransition(groupA, groupB))))
            {
                map.setTile(newTile);
                toResolve.add(newTile);
            }
            resolved.add(newTile);
        }
    }

    /**
     * Check tile transitive groups.
     * 
     * @param resolved The resolved tiles.
     * @param tile The tile to check.
     */
    private void checkTransitives(Collection<Tile> resolved, Tile tile)
    {
        boolean isTransitive = false;
        for (final Tile neighbor : map.getNeighbors(tile))
        {
            final String group = mapGroup.getGroup(tile);
            final String neighborGroup = mapGroup.getGroup(neighbor);
            final Collection<GroupTransition> transitives = getTransitives(group, neighborGroup);

            if (transitives.size() > 1 && (getTransition(neighbor, group) == null || isCenter(neighbor)))
            {
                final int iterations = transitives.size() - 3;
                int i = 0;
                for (final GroupTransition transitive : transitives)
                {
                    updateTransitive(resolved, tile, neighbor, transitive);
                    isTransitive = true;
                    i++;
                    if (i > iterations)
                    {
                        break;
                    }
                }
            }
        }
        // Restore initial tile once transition solved by transitive
        if (isTransitive)
        {
            map.setTile(tile);
        }
    }

    /**
     * Update the transitive between tile and its neighbor.
     * 
     * @param resolved The resolved tiles.
     * @param tile The tile reference.
     * @param neighbor The neighbor reference.
     * @param transitive The transitive involved.
     */
    private void updateTransitive(Collection<Tile> resolved, Tile tile, Tile neighbor, GroupTransition transitive)
    {
        final String transitiveGroup = transitive.getOut();
        final Transition transition = new Transition(TransitionType.CENTER, transitiveGroup, transitiveGroup);
        final Collection<TileRef> refs = getTiles(transition);
        if (!refs.isEmpty())
        {
            final TileRef ref = refs.iterator().next();

            // Replace user tile with the needed tile to solve transition (restored later)
            final Tile newTile = map.createTile(ref.getSheet(), ref.getNumber(), tile.getX(), tile.getY());
            map.setTile(newTile);

            // Replace neighbor with the needed tile to solve transition
            final Tile newTile2 = map.createTile(ref.getSheet(), ref.getNumber(), neighbor.getX(), neighbor.getY());
            map.setTile(newTile2);
            resolved.addAll(resolve(newTile2));
        }
    }

    /**
     * Check if tile is a center.
     * 
     * @param tile The tile to check.
     * @return <code>true</code> if center, <code>false</code> else.
     */
    private boolean isCenter(Tile tile)
    {
        for (final Transition transition : tiles.get(new TileRef(tile)))
        {
            if (TransitionType.CENTER == transition.getType())
            {
                return true;
            }
        }
        return false;
    }

    /*
     * MapTileTransition
     */

    @Override
    public void loadTransitions(Media transitionsConfig)
    {
        loadTransitions(TransitionsConfig.imports(transitionsConfig));
    }

    @Override
    public void loadTransitions(Media[] levels, Media sheetsConfig, Media groupsConfig)
    {
        final TransitionsExtractor transitionsExtractor = new TransitionsExtractorImpl();
        loadTransitions(transitionsExtractor.getTransitions(levels, sheetsConfig, groupsConfig));
    }

    @Override
    public void loadTransitions(Map<Transition, Collection<TileRef>> transitions)
    {
        this.transitions.clear();
        this.transitions.putAll(transitions);

        tiles.clear();
        for (final Entry<Transition, Collection<TileRef>> entry : this.transitions.entrySet())
        {
            final Transition transition = entry.getKey();
            for (final TileRef tileRef : entry.getValue())
            {
                if (!tiles.containsKey(tileRef))
                {
                    tiles.put(tileRef, new HashSet<Transition>());
                }
                tiles.get(tileRef).add(transition);
            }
            groupLinks.add(new GroupTransition(transition.getIn(), transition.getOut()));
            groupLinks.add(new GroupTransition(transition.getOut(), transition.getIn()));
        }

        transitiveGroup = new TransitiveGroup(map);
        transitiveGroup.load();
    }

    @Override
    public Collection<Tile> resolve(Tile tile)
    {
        final Collection<Tile> resolved = new HashSet<Tile>();
        checkTransitives(resolved, tile);

        final Collection<Tile> toResolve = new ArrayList<Tile>();
        resolve(resolved, toResolve, tile);

        final Collection<Tile> toResolveAfter = new ArrayList<Tile>();
        for (final Tile next : toResolve)
        {
            resolve(resolved, toResolveAfter, next);
        }

        toResolve.clear();
        toResolveAfter.clear();

        return resolved;
    }

    @Override
    public Transition getTransition(TileRef tile, String groupOut)
    {
        final String groupIn = mapGroup.getGroup(tile);
        if (tiles.containsKey(tile))
        {
            for (final Transition transition : tiles.get(tile))
            {
                if (transition.getIn().equals(groupIn) || transition.getOut().equals(groupOut))
                {
                    return transition;
                }
            }
        }
        return null;
    }

    @Override
    public Transition getTransition(Tile tile, String group)
    {
        return getTransition(new TileRef(tile), group);
    }

    @Override
    public Collection<GroupTransition> getTransitives(String groupIn, String groupOut)
    {
        return transitiveGroup.getTransitives(groupIn, groupOut);
    }

    @Override
    public Collection<Transition> getTransitions()
    {
        return transitions.keySet();
    }

    @Override
    public Collection<TileRef> getTiles(Transition transition)
    {
        if (!transitions.containsKey(transition))
        {
            return Collections.emptySet();
        }
        return transitions.get(transition);
    }
}
