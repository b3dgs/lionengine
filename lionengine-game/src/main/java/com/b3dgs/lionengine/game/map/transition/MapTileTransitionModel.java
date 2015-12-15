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
package com.b3dgs.lionengine.game.map.transition;

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
import com.b3dgs.lionengine.core.Medias;
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
    /** Map reference. */
    private final MapTile map;
    /** Map tile group. */
    private final MapTileGroup mapGroup;
    /** Tile as key. */
    private final Map<TileRef, Collection<Transition>> tiles;
    /** Transitions as key. */
    private Map<Transition, Collection<TileRef>> transitions;
    /** Transitive group handler. */
    private TransitiveGroup transitive;

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
        tiles = new HashMap<TileRef, Collection<Transition>>();
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
        final Transition transition = getTransition(tile, group);
        if (TransitionType.CENTER.equals(transition.getType()))
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
        // checkTransitives(tile);
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
            && !TransitionType.CENTER.equals(getTransition(neighbor, group).getType()))
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
        for (final Transition transition : TransitionsExtractor.getTransition(map, tile))
        {
            final String group = mapGroup.getGroup(tile);
            if (group.equals(transition.getOut()))
            {
                final Collection<TileRef> tilesRef = getTransitionTiles(transition);
                if (updateTile(tile, transition, tilesRef))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the tiles associated to the transition.
     * 
     * @param transition The transition reference.
     * @return The associated tiles.
     */
    private Collection<TileRef> getTransitionTiles(Transition transition)
    {
        final Collection<TileRef> tilesRef;
        if (transitions.containsKey(transition))
        {
            tilesRef = transitions.get(transition);
        }
        else
        {
            tilesRef = transitive.getDirectTransitiveTiles(transition);
        }
        return tilesRef;
    }

    /**
     * Update the tile depending of its transition. Replace the tile with its right transition representation.
     * 
     * @param tile The tile reference.
     * @param transition The tile transition.
     * @param tilesRef The associated tiles.
     * @return <code>true</code> if updated, <code>false</code> if unchanged.
     */
    private boolean updateTile(Tile tile, Transition transition, Collection<TileRef> tilesRef)
    {
        if (!tilesRef.contains(new TileRef(tile)))
        {
            for (final TileRef tileRef : tilesRef)
            {
                final TransitionType type = getTransition(tileRef, transition.getOut()).getType();
                if (!TransitionType.CENTER.equals(type))
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
        for (int v = ty + 1; v >= ty - 1; v--)
        {
            for (int h = tx - 1; h <= tx + 1; h++)
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
        for (final Transition transition : TransitionsExtractor.getTransition(map, ref))
        {
            final String group = mapGroup.getGroup(tile);
            if (group.equals(transition.getIn())
                && TransitionType.CENTER.equals(transition.getType())
                && !transition.getType().equals(getTransition(ref, transition.getOut()).getType()))
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
        final Transition neighborTransition = TransitionsExtractor.getTransition(map, tile, false);
        final Iterator<TileRef> iterator = getTiles(neighborTransition).iterator();
        return iterator.hasNext() && TransitionType.CENTER.equals(getTransition(iterator.next(), group).getType());
    }

    /*
     * MapTileTransition
     */

    @Override
    public void loadTransitions()
    {
        loadTransitions(Medias.create(map.getSheetsConfig().getParentPath(), TransitionsConfig.FILENAME));
    }

    @Override
    public void loadTransitions(Media configTransitions)
    {
        transitions = TransitionsConfig.imports(configTransitions);
        tiles.clear();

        for (final Entry<Transition, Collection<TileRef>> entry : transitions.entrySet())
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
        }

        transitive = new TransitiveGroup(map, mapGroup, this);
        transitive.load();
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
        fixNeigbor(tile, tile, fixed, 2);
        for (final Tile ref : checked)
        {
            fix(tile, ref, fixed);
        }
        fixed.clear();

        checked.clear();
    }

    @Override
    public Transition getTransition(TileRef tile, String group)
    {
        final String groupIn = mapGroup.getGroup(tile);
        if (tiles.containsKey(tile))
        {
            for (final Transition transition : tiles.get(tile))
            {
                if (transition.getIn().equals(groupIn) && transition.getOut().equals(group))
                {
                    return transition;
                }
            }
        }
        return new Transition(TransitionType.NONE, groupIn, groupIn);
    }

    @Override
    public Transition getTransition(Tile tile, String group)
    {
        return getTransition(new TileRef(tile), group);
    }

    @Override
    public Collection<GroupTransition> getTransitives(String groupIn, String groupOut)
    {
        return transitive.getTransitives(groupIn, groupOut);
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
