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

import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.trait.fovable.Fovable;

/**
 * Designed to handle a fog of war (discovering tile and hiding tile).
 * <p>
 * Usage example:
 * </p>
 * <ul>
 * <li>{@link #setTilesheet(SpriteTiled, SpriteTiled)}</li>
 * <li>{@link #setEnabled(boolean, boolean)}</li>
 * <li>{@link #create(MapTile, MapTileRenderer)} - Default or external {@link MapTileRenderer} must be set here (could
 * be the {@link MapTile} or another one).</li>
 * <li>{@link MapTile#setTileRenderer(MapTileRenderer)} - Fog of war is a {@link MapTileRenderer} which will not
 * override the existing map renderer, but decorate it by rendering fog after the old one.</li>
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FogOfWar
        implements MapTileRenderer
{
    /** Fog map. */
    private final Border20Map border20Map;
    /** Map tile renderer. */
    private MapTileRenderer renderer;
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

    /**
     * Create a fog of war.
     */
    public FogOfWar()
    {
        border20Map = new Border20Map(false);
        hideMap = false;
        fogMap = false;
    }

    /**
     * Create a fog of war from a map.
     * 
     * @param map The map reference.
     * @param renderer The renderer reference.
     */
    public void create(MapTile map, MapTileRenderer renderer)
    {
        this.renderer = renderer;
        widthInTile = map.getInTileWidth();
        heightInTile = map.getInTileHeight();
        visited = new Border20[heightInTile][widthInTile];
        fog = new Border20[heightInTile][widthInTile];
        border20Map.create(map);

        for (int ty = 0; ty < heightInTile; ty++)
        {
            for (int tx = 0; tx < widthInTile; tx++)
            {
                if (hideMap)
                {
                    visited[ty][tx] = Border20.NONE;
                }
                else
                {
                    visited[ty][tx] = Border20.CENTER;
                }
                if (fogMap)
                {
                    fog[ty][tx] = Border20.NONE;
                }
                else
                {
                    fog[ty][tx] = Border20.CENTER;
                }
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
        if (fogMap)
        {
            for (int y = 0; y < heightInTile; y++)
            {
                Arrays.fill(fog[y], Border20.NONE);
            }
        }
        for (final Fovable fovable : fovables)
        {
            updateFov(fovable);
        }
    }

    /**
     * Set fog tilesheet reference.
     * 
     * @param hide The hide tilesheet.
     * @param fog The fog tilesheet.
     */
    public void setTilesheet(SpriteTiled hide, SpriteTiled fog)
    {
        hideTiles = hide;
        fogTiles = fog;
    }

    /**
     * Set fog of war enabled state.
     * 
     * @param hide <code>true</code> to enable map hiding, <code>false</code> else.
     * @param fog <code>true</code> to enable fog map, <code>false</code> else.
     */
    public void setEnabled(boolean hide, boolean fog)
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
     * Check if the tile is currently hidden by the fog of war.
     * 
     * @param tiled The tiled to check.
     * @return <code>true</code> if hidden, <code>false</code> else.
     */
    public boolean isFogged(Tiled tiled)
    {
        final int tx = tiled.getInTileX();
        final int ty = tiled.getInTileY();
        final int tw = tiled.getInTileWidth() - 1;
        final int th = tiled.getInTileHeight() - 1;

        for (int ctx = tx; ctx <= tx + tw; ctx++)
        {
            for (int cty = ty; cty <= ty + th; cty++)
            {
                if (isFogged(ctx, cty) && isVisited(ctx, cty))
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
        return Border20.NONE != Border20Map.get(visited, tx, ty);
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
        return Border20.NONE != Border20Map.get(fog, tx, ty);
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

        if (hideMap)
        {
            border20Map.updateInclude(visited, tx, ty, tw, th, ray + 1);
        }

        if (fogMap)
        {
            border20Map.updateInclude(fog, tx, ty, tw, th, ray);
        }
    }

    /*
     * MapTileRenderer
     */

    @Override
    public void renderTile(Graphic g, Tile tile, int x, int y)
    {
        renderer.renderTile(g, tile, x, y);

        final int tx = tile.getX() / tile.getWidth();
        final int ty = tile.getY() / tile.getHeight();
        final Border20 vid = Border20Map.get(visited, tx, ty);
        final Border20 fid = Border20Map.get(fog, tx, ty);

        if (fogMap && Border20.NONE != vid)
        {
            fogTiles.setLocation(x, y);
            fogTiles.setTile(fid.ordinal());
            fogTiles.render(g);
        }
        if (hideMap)
        {
            hideTiles.setLocation(x, y);
            hideTiles.setTile(vid.ordinal());
            hideTiles.render(g);
        }
    }
}
