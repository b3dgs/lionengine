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
package com.b3dgs.lionengine.game.map.feature.fog;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroup;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroupModel;
import com.b3dgs.lionengine.game.map.feature.transition.MapTileTransition;
import com.b3dgs.lionengine.game.map.feature.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGame;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Designed to handle a fog of war (discovering tile and hiding tile).
 */
public class MapTileFog
{
    /** No fog tile. */
    static final int FOG = 16;
    /** Fog tile. */
    static final int NO_FOG = 17;
    /** Fog group. */
    private static final String FOG_GROUP = "fog";
    /** Transition group. */
    private static final String TRANSITION_GROUP = "transition";

    /** Revealed tiles. */
    private final Collection<Tile> revealed = new HashSet<Tile>();
    /** Hidden map. */
    private final MapTile map;
    /** Map group. */
    private final MapTileGroup mapGroup;
    /** Transitions. */
    private final MapTileTransition transition;

    /**
     * Create a fog of war.
     */
    public MapTileFog()
    {
        final Services services = new Services();
        map = services.create(MapTileGame.class);
        mapGroup = map.addFeatureAndGet(new MapTileGroupModel());
        transition = map.addFeatureAndGet(new MapTileTransitionModel());
        map.prepareFeatures(services);
    }

    /**
     * Create a fog of war from a map.
     * 
     * @param map The map reference.
     * @param config The fog configuration.
     * @param sheet The sheet used (can be <code>null</code>).
     */
    public void create(MapTile map, Media config, SpriteTiled sheet)
    {
        this.map.create(map.getTileWidth(), map.getTileHeight(), map.getInTileWidth(), map.getInTileHeight());
        if (sheet != null)
        {
            this.map.loadSheets(Arrays.asList(sheet));
        }
        for (int i = 0; i < NO_FOG; i++)
        {
            final String group;
            if (i == FOG)
            {
                group = FOG_GROUP;
            }
            else
            {
                group = TRANSITION_GROUP;
            }
            mapGroup.changeGroup(new TileGame(Integer.valueOf(0), i, 0, 0, map.getTileWidth(), map.getTileHeight()),
                                 group);
        }
        mapGroup.changeGroup(new TileGame(Integer.valueOf(0), NO_FOG, 0, 0, map.getTileWidth(), map.getTileHeight()),
                             MapTileGroupModel.NO_GROUP_NAME);
        transition.loadTransitions(config);

        for (int x = 0; x < map.getInTileWidth(); x++)
        {
            for (int y = 0; y < map.getInTileHeight(); y++)
            {
                final Tile tile = new TileGame(Integer.valueOf(0),
                                               FOG,
                                               x * map.getTileWidth(),
                                               y * map.getTileHeight(),
                                               map.getTileWidth(),
                                               map.getTileHeight());
                this.map.setTile(tile);
            }
        }
    }

    /**
     * Update fovable field of view (fog of war).
     * 
     * @param fovables The entities reference.
     */
    public void update(Collection<Fovable> fovables)
    {
        for (final Fovable fovable : fovables)
        {
            updateFov(fovable);
        }
    }

    /**
     * Reset the revealed tiles to fogged.
     */
    public void reset()
    {
        for (final Tile tile : revealed)
        {
            final Tile reset = new TileGame(tile.getSheet(),
                                            FOG,
                                            tile.getX(),
                                            tile.getY(),
                                            tile.getWidth(),
                                            tile.getHeight());
            map.setTile(reset);
        }
        revealed.clear();
    }

    /**
     * Get the tile.
     * 
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @return The tile reference.
     */
    public Tile getTile(int tx, int ty)
    {
        return map.getTile(tx, ty);
    }

    /**
     * Update fovable field of view (fog of war).
     * 
     * @param fovable The fovable reference.
     */
    private void updateFov(Fovable fovable)
    {
        final int tx = fovable.getInTileX();
        final int ty = fovable.getInTileY();
        final int tw = fovable.getInTileWidth();
        final int th = fovable.getInTileHeight();
        final int ray = fovable.getInTileFov();

        final int sx = UtilMath.clamp(tx - ray - tw / 2, 0, map.getInTileWidth() - 1);
        final int ex = UtilMath.clamp(tx + ray + tw / 2, 0, map.getInTileWidth() - 1);
        final int sy = UtilMath.clamp(ty - ray - th / 2, 0, map.getInTileHeight() - 1);
        final int ey = UtilMath.clamp(ty + ray + th / 2, 0, map.getInTileHeight() - 1);

        for (int x = sx + 1; x < ex; x++)
        {
            for (int y = sy + 1; y < ey; y++)
            {
                final Tile tile = new TileGame(Integer.valueOf(0),
                                               NO_FOG,
                                               x * map.getTileWidth(),
                                               y * map.getTileHeight(),
                                               map.getTileWidth(),
                                               map.getTileHeight());
                map.setTile(tile);
                transition.resolve(getTile(x, y));
            }
        }

        for (int x = sx; x < ex + 1; x++)
        {
            for (int y = sy; y < ey + 1; y++)
            {
                final Tile tile = map.getTile(x, y);
                revealed.add(tile);
            }
        }
    }
}
