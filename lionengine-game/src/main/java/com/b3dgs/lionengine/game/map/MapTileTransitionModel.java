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

import java.util.Collection;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.configurer.ConfigTileTransitions;

/**
 * Map tile transition model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MapTileTransitionModel implements MapTileTransition
{
    /** Map reference. */
    private final MapTile map;
    /** Transitions. */
    private Map<TileTransition, Collection<TileRef>> transitions;

    /**
     * Create the model.
     * 
     * @param map The map reference.
     * @throws LionEngineException If <code>null</code> map.
     */
    public MapTileTransitionModel(MapTile map)
    {
        Check.notNull(map);
        this.map = map;
    }

    /**
     * Update the tile depending of its transition.
     * 
     * @param tile The tile reference.
     */
    private void updateTile(Tile tile)
    {
        final TileTransition transition = TransitionsExtractor.getTransition(map, tile);
        final Collection<TileRef> tilesRef = transitions.get(transition);
        if (tilesRef != null && tilesRef.size() > 0)
        {
            for (final TileRef tileRef : tilesRef)
            {
                if (map.getGroup(tileRef.getSheet(), tileRef.getNumber()).getName().equals(tile.getGroup()))
                {
                    tile.setSheet(tileRef.getSheet());
                    tile.setNumber(tileRef.getNumber());
                }
            }
        }
    }

    /*
     * MapTileTransition
     */

    @Override
    public void loadTransitions()
    {
        loadTransitions(Medias.create(map.getSheetsConfig().getParentPath(), ConfigTileTransitions.FILENAME));
    }

    @Override
    public void loadTransitions(Media configTransitions)
    {
        transitions = ConfigTileTransitions.imports(configTransitions);
    }

    @Override
    public void resolve(Tile tile)
    {
        updateTile(tile);

        final int tx = tile.getX() / tile.getWidth();
        final int ty = tile.getY() / tile.getHeight();
        for (int v = ty + 1; v >= ty - 1; v--)
        {
            for (int h = tx - 1; h <= tx + 1; h++)
            {
                final Tile neighbor = map.getTile(h, v);
                if (neighbor != null && tile.getGroup().equals(tile.getGroup()))
                {
                    updateTile(neighbor);
                }
            }
        }
    }

    @Override
    public MapTile getMap()
    {
        return map;
    }
}
