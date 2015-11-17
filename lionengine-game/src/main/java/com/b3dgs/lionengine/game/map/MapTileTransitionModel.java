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
import java.util.HashMap;
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
    private final MapTileGroup mapTileGroup;
    /** Tile as key. */
    private final Map<TileRef, TileTransition> tiles = new HashMap<TileRef, TileTransition>();
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
        map = services.get(MapTile.class);
        mapTileGroup = map.getFeature(MapTileGroup.class);
    }

    /**
     * Update the tile depending of its transition.
     * 
     * @param tile The tile reference.
     * @return <code>true</code> if updated, <code>false</code> if unchanged.
     */
    private boolean updateTile(Tile tile)
    {
        final TileTransition transition = TransitionsExtractor.getTransition(map, tile);
        final Collection<TileRef> tilesRef = transitions.get(transition);
        final TileRef ref = new TileRef(tile);
        if (tilesRef != null && !tilesRef.isEmpty() && !tilesRef.contains(ref))
        {
            for (final TileRef tileRef : tilesRef)
            {
                if (mapTileGroup.getGroup(tileRef.getSheet(), tileRef.getNumber()).getName().equals(tile.getGroup()))
                {
                    tile.setSheet(tileRef.getSheet());
                    tile.setNumber(tileRef.getNumber());
                    return true;
                }
            }
        }
        return false;
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

        for (final Entry<TileTransition, Collection<TileRef>> entry : transitions.entrySet())
        {
            final TileTransition transition = entry.getKey();
            for (final TileRef tileRef : entry.getValue())
            {
                tiles.put(tileRef, transition);
            }
        }
    }

    @Override
    public void resolve(Tile tile)
    {
        final Collection<Tile> checked = new ArrayList<Tile>();
        checked.add(tile);

        updateTile(tile);
        resolve(checked, tile);
        checked.clear();
    }

    /**
     * Resolve unchecked tiles around.
     * 
     * @param checked The list of checked tiles.
     * @param tile The tile to check its neighbor.
     */
    private void resolve(Collection<Tile> checked, Tile tile)
    {
        final int tx = tile.getX() / tile.getWidth();
        final int ty = tile.getY() / tile.getHeight();
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
                        resolve(checked, neighbor);
                    }
                }
            }
        }
    }

    @Override
    public TileTransition getTransition(Tile tile)
    {
        final TileRef tileRef = new TileRef(tile);
        if (tiles.containsKey(tileRef))
        {
            return tiles.get(tileRef);
        }
        return new TileTransition(TileTransitionType.NONE, tile.getGroup(), tile.getGroup());
    }
}
