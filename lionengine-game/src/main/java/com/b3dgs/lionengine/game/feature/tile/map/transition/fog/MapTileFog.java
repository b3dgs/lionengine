/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition.fog;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

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
    private final Collection<Tile> revealed = new HashSet<>();
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
        super();

        final Services services = new Services();
        map = services.create(MapTileGame.class);
        mapGroup = map.addFeatureAndGet(new MapTileGroupModel());
        transition = map.addFeatureAndGet(new MapTileTransitionModel(services));
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
            mapGroup.changeGroup(new TileGame(i, 0, 0, map.getTileWidth(), map.getTileHeight()), group);
        }
        mapGroup.changeGroup(new TileGame(NO_FOG, 0, 0, map.getTileWidth(), map.getTileHeight()),
                             MapTileGroupModel.NO_GROUP_NAME);
        transition.loadTransitions(config);

        for (int tx = 0; tx < map.getInTileWidth(); tx++)
        {
            for (int ty = 0; ty < map.getInTileHeight(); ty++)
            {
                this.map.setTile(tx, ty, FOG);
            }
        }
    }

    /**
     * Reset the revealed tiles to fogged.
     */
    public void reset()
    {
        for (final Tile tile : revealed)
        {
            map.setTile(tile.getInTileX(), tile.getInTileY(), FOG);
        }
        revealed.clear();
    }

    /**
     * Get the tile.
     * 
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @return The tile reference (<code>null</code> if none).
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
    public void updateFov(Fovable fovable)
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
                map.setTile(x, y, NO_FOG);
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
