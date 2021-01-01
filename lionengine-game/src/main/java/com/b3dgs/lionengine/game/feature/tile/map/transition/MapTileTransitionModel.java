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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;

/**
 * Map tile transition model implementation.
 */
public class MapTileTransitionModel extends FeatureAbstract implements MapTileTransition
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

    /** Tile as key. */
    private final Map<Integer, Collection<Transition>> tiles = new HashMap<>();
    /** Transitions as key. */
    private final Map<Transition, Collection<Integer>> transitions = new HashMap<>();
    /** Existing group links. */
    private final Collection<GroupTransition> groupLinks = new HashSet<>();
    /** Transitive group handler. */
    private TransitiveGroup transitiveGroup;

    /** Map tile surface. */
    private MapTileSurface map;
    /** Map tile group. */
    private MapTileGroup mapGroup;

    /**
     * Create feature.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link MapTileSurface}</li>
     * <li>{@link MapTileGroup}</li>
     * </ul>
     */
    public MapTileTransitionModel()
    {
        super();
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
        final Iterator<Integer> iterator = getTiles(transition).iterator();
        if (iterator.hasNext())
        {
            final Integer ref = iterator.next();
            map.setTile(neighbor.getInTileX(), neighbor.getInTileY(), ref.intValue());
            resolved.add(new TileGame(ref.intValue(),
                                      neighbor.getInTileX(),
                                      neighbor.getInTileY(),
                                      map.getTileWidth(),
                                      map.getTileHeight()));
        }
        else
        {
            final Tile newTile = new TileGame(tile.getNumber(),
                                              neighbor.getInTileX(),
                                              neighbor.getInTileY(),
                                              tile.getWidth(),
                                              tile.getHeight());
            final String groupA = mapGroup.getGroup(tile);
            final String groupB = mapGroup.getGroup(neighbor);

            // Used to fix transitions not found
            if (!neighbor.equals(newTile)
                && (!isCenter(neighbor)
                    || groupA.equals(groupB)
                    || groupLinks.contains(new GroupTransition(groupA, groupB))))
            {
                map.setTile(newTile.getInTileX(), newTile.getInTileY(), newTile.getNumber());
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
        final Integer old = tile.getKey();
        for (final Tile neighbor : map.getNeighbors(tile))
        {
            final String group = mapGroup.getGroup(old);
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
            map.setTile(tile.getInTileX(), tile.getInTileY(), old.intValue());
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
        final String transitiveOut = transitive.getOut();
        final Transition transition = new Transition(TransitionType.CENTER, transitiveOut, transitiveOut);
        final Collection<Integer> refs = getTiles(transition);
        if (!refs.isEmpty())
        {
            final Integer ref = refs.iterator().next();

            // Replace user tile with the needed tile to solve transition (restored later)
            map.setTile(tile.getInTileX(), tile.getInTileY(), ref.intValue());

            // Replace neighbor with the needed tile to solve transition
            final Tile newTile2 = new TileGame(ref.intValue(),
                                               neighbor.getInTileX(),
                                               neighbor.getInTileY(),
                                               neighbor.getWidth(),
                                               neighbor.getHeight());
            map.setTile(newTile2.getInTileX(), newTile2.getInTileY(), newTile2.getNumber());
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
        for (final Transition transition : tiles.get(tile.getKey()))
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
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        map = provider.getFeature(MapTileSurface.class);
        mapGroup = provider.getFeature(MapTileGroup.class);
    }

    @Override
    public void loadTransitions(Media transitionsConfig)
    {
        loadTransitions(TransitionsConfig.imports(transitionsConfig));
    }

    @Override
    public void loadTransitions(Collection<Media> levels, Media sheetsConfig, Media groupsConfig)
    {
        final TransitionsExtractor transitionsExtractor = new TransitionsExtractorImpl();
        loadTransitions(transitionsExtractor.getTransitions(levels, sheetsConfig, groupsConfig));
    }

    @Override
    public void loadTransitions(Map<Transition, Collection<Integer>> transitions)
    {
        this.transitions.clear();
        this.transitions.putAll(transitions);

        tiles.clear();
        for (final Entry<Transition, Collection<Integer>> entry : this.transitions.entrySet())
        {
            final Transition transition = entry.getKey();
            for (final Integer tileRef : entry.getValue())
            {
                if (!tiles.containsKey(tileRef))
                {
                    tiles.put(tileRef, new HashSet<>());
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
        final Collection<Tile> resolved = new HashSet<>();
        checkTransitives(resolved, tile);

        final Collection<Tile> toResolve = new ArrayList<>();
        resolve(resolved, toResolve, tile);

        final Collection<Tile> toResolveAfter = new ArrayList<>();
        for (final Tile next : toResolve)
        {
            resolve(resolved, toResolveAfter, next);
        }

        toResolve.clear();
        toResolveAfter.clear();

        return resolved;
    }

    @Override
    public Transition getTransition(Integer tile, String groupOut)
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
        return getTransition(tile.getKey(), group);
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
    public Collection<Integer> getTiles(Transition transition)
    {
        if (!transitions.containsKey(transition))
        {
            return Collections.emptySet();
        }
        return transitions.get(transition);
    }
}
