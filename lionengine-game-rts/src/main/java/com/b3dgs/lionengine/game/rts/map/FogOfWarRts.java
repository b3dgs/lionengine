/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.rts.map;

import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.entity.EntityRts;

/**
 * Designed to handle a fog of war (discovering tile and hiding tile).
 * 
 * @param <T> Tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class FogOfWarRts<T extends TileGame<?>>
{
    /** Fog map. */
    private final Border20Map border20Map;
    /** Map reference. */
    private MapTile<?, T> map;
    /** Fog black tile. */
    private SpriteTiled hideTiles;
    /** Fog gray tiles. */
    private SpriteTiled fogTiles;
    /** Fog visited border state. */
    private Border20[][] visited;
    /** Fog border state. */
    private Border20[][] fog;
    /** Uses of hiding fog. */
    private boolean hideMap;
    /** Uses of fog. */
    private boolean fogMap;
    /** Fog width in tile. */
    private int widthInTile;
    /** Fog height in tile. */
    private int heightInTile;
    /** Fog tile width. */
    private int tileWidth;
    /** Fog tile height. */
    private int tileHeight;
    /** Fog owner id. */
    private int playerId;

    /**
     * Constructor.
     */
    public FogOfWarRts()
    {
        border20Map = new Border20Map(false);
        hideMap = false;
        fogMap = false;
        playerId = -1;
    }

    /**
     * Constructor.
     * 
     * @param map The map reference.
     */
    public void create(MapTile<?, T> map)
    {
        this.map = map;
        widthInTile = map.getWidthInTile();
        heightInTile = map.getHeightInTile();
        tileWidth = map.getTileWidth();
        tileHeight = map.getTileHeight();
        visited = new Border20[heightInTile][widthInTile];
        fog = new Border20[heightInTile][widthInTile];
        border20Map.create(map);

        for (int y = 0; y < heightInTile; y++)
        {
            for (int x = 0; x < widthInTile; x++)
            {
                if (hideMap)
                {
                    visited[y][x] = Border20.NONE;
                }
                else
                {
                    visited[y][x] = Border20.CENTER;
                }
                if (fogMap)
                {
                    fog[y][x] = Border20.NONE;
                }
                else
                {
                    fog[y][x] = Border20.CENTER;
                }
            }
        }
    }

    /**
     * Set player id, to know which player has to be fogged.
     * 
     * @param id The player id
     */
    public void setPlayerId(int id)
    {
        playerId = id;
    }

    /**
     * Update entities field of view (fog of war).
     * 
     * @param entities The entities reference.
     * @param <E> The entity type.
     */
    public <E extends EntityRts> void update(Collection<E> entities)
    {
        if (fogMap)
        {
            for (int y = 0; y < heightInTile; y++)
            {
                Arrays.fill(fog[y], Border20.NONE);
            }
        }
        for (final E entity : entities)
        {
            if (playerId != entity.getPlayerId())
            {
                continue;
            }
            updateEntityFov(entity);
        }
    }

    /**
     * Render fog map from camera viewpoint, showing a specified area.
     * 
     * @param g The graphic output.
     * @param camera The camera viewpoint.
     */
    public void render(Graphic g, CameraRts camera)
    {
        render(g, camera.getViewHeight(), camera.getLocationIntX(), camera.getLocationIntY(),
                (int) Math.floor(camera.getViewWidth() / (double) tileWidth),
                (int) Math.floor(camera.getViewHeight() / (double) tileHeight), -camera.getViewX(), camera.getViewY());
    }

    /**
     * Set fog tilesheet reference.
     * 
     * @param hide The hide tilesheet.
     * @param fog The fog tilesheet.
     */
    public void setFogTiles(SpriteTiled hide, SpriteTiled fog)
    {
        hideTiles = hide;
        fogTiles = fog;
    }

    /**
     * Set fog of war state.
     * 
     * @param hide <code>true</code> to enable map hiding, <code>false</code> else.
     * @param fog <code>true</code> to enable fog map, <code>false</code> else.
     */
    public void setFogOfWar(boolean hide, boolean fog)
    {
        hideMap = hide;
        fogMap = fog;
    }

    /**
     * Check if fog of war is enabled.
     * 
     * @return <code>true</code> if fog of war is enabled, <code>false</code> else.
     */
    public boolean hasFogOfWar()
    {
        return hideMap || fogMap;
    }

    /**
     * Check if the entity is current hidden by the fog of war.
     * 
     * @param entity The entity to check.
     * @return <code>true</code> if hidden, <code>false</code> else.
     */
    public boolean isFogged(EntityRts entity)
    {
        final int tx = entity.getLocationInTileX();
        final int ty = entity.getLocationInTileY();
        final int tw = entity.getWidthInTile() - 1;
        final int th = entity.getHeightInTile() - 1;

        for (int x = tx; x <= tx + tw; x++)
        {
            for (int y = ty; y <= ty + th; y++)
            {
                if (isFogged(x, y) && isVisited(x, y))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * In case of active fog of war, check if tile has been discovered.
     * 
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @return <code>true</code> if already discovered, <code>false</code> else.
     */
    public boolean isVisited(int tx, int ty)
    {
        return Border20.NONE != border20Map.get(visited, tx, ty);
    }

    /**
     * In case of active fog of war, check if tile is hidden by fog.
     * 
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @return <code>true</code> if hidden by fog, <code>false</code> else.
     */
    public boolean isFogged(int tx, int ty)
    {
        return Border20.NONE != border20Map.get(fog, tx, ty);
    }

    /**
     * Update entity field of view (fog of war).
     * 
     * @param entity The entity reference.
     */
    private void updateEntityFov(EntityRts entity)
    {
        final int tx = entity.getLocationInTileX();
        final int ty = entity.getLocationInTileY();
        final int tw = entity.getWidthInTile() - 1;
        final int th = entity.getHeightInTile() - 1;
        final int ray = entity.getFov();

        if (hideMap)
        {
            border20Map.updateInclude(visited, tx, ty, tw, th, ray + 1);
        }

        if (fogMap)
        {
            border20Map.updateInclude(fog, tx, ty, tw, th, ray);
        }
    }

    /**
     * Render map from starting position, showing a specified area, including a specific offset.
     * 
     * @param g The graphic output.
     * @param screenHeight The view height (rendering start from bottom).
     * @param sx The starting x (view real location x).
     * @param sy The starting y (view real location y).
     * @param inTileWidth The number of rendered tiles in width.
     * @param inTileHeight The number of rendered tiles in height.
     * @param offsetX The horizontal map offset (usually used as safe area to avoid negative tiles).
     * @param offsetY The vertical map offset (usually used as safe area to avoid negative tiles).
     */
    private void render(Graphic g, int screenHeight, int sx, int sy, int inTileWidth, int inTileHeight, int offsetX,
            int offsetY)
    {
        // Each vertical tiles
        for (int v = 0; v <= inTileHeight; v++)
        {
            final int ty = v + (sy - offsetY) / tileHeight;
            if (!(ty < 0 || ty >= heightInTile))
            {
                // Each horizontal tiles
                for (int h = 0; h <= inTileWidth; h++)
                {
                    final int tx = h + (sx - offsetX) / tileWidth;
                    if (!(tx < 0 || tx >= widthInTile))
                    {
                        final T tile = map.getTile(tx, ty);

                        if (tile != null)
                        {
                            renderFogTile(g, screenHeight, sx, sy, tx, ty, tile);
                        }
                    }
                }
            }
        }
    }

    /**
     * Render a specific fog tile.
     * 
     * @param g The graphic output.
     * @param screenHeight The view height (rendering start from bottom).
     * @param sx The starting x (view real location x).
     * @param sy The starting y (view real location y).
     * @param tx The tile location x.
     * @param ty The tile location y.
     * @param tile The tile to render.
     */
    private void renderFogTile(Graphic g, int screenHeight, int sx, int sy, int tx, int ty, T tile)
    {
        final int x = tile.getX() - sx;
        final int y = -tile.getY() - tile.getHeight() + sy + screenHeight;

        if (!(tx < 0 || ty < 0))
        {
            final Border20 vid = border20Map.get(visited, tx, ty);
            final Border20 fid = border20Map.get(fog, tx, ty);

            if (fogMap && Border20.NONE != vid)
            {
                fogTiles.render(g, fid.ordinal(), x, y);
            }

            if (hideMap)
            {
                hideTiles.render(g, vid.ordinal(), x, y);
            }
        }
    }
}
