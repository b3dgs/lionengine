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

import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.feature.renderer.MapTileRenderer;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.Tiled;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Designed to handle a fog of war (discovering tile and hiding tile).
 * <p>
 * Usage example:
 * </p>
 * <ul>
 * <li>{@link #setTilesheet(SpriteTiled, SpriteTiled)}</li>
 * <li>{@link #setEnabled(boolean, boolean)}</li>
 * <li>{@link #create(MapTile, Media)}</li>
 * </ul>
 */
public class FogOfWar extends FeatureModel implements MapTileRenderer
{
    /** Hidden map. */
    private final MapTileFog mapHidden = new MapTileFog();
    /** Fogged map. */
    private final MapTileFog mapFogged = new MapTileFog();
    /** Fog black tile. */
    private SpriteTiled hideTiles;
    /** Fog gray tiles. */
    private SpriteTiled fogTiles;
    /** Uses of hiding fog. */
    private boolean hideMap;
    /** Uses of fog. */
    private boolean fogMap;

    /**
     * Create a fog of war.
     */
    public FogOfWar()
    {
        super();
    }

    /**
     * Create a fog of war from a map.
     * 
     * @param map The map reference.
     * @param config The fog configuration.
     */
    public void create(MapTile map, Media config)
    {
        mapHidden.create(map, config, hideTiles);
        mapFogged.create(map, config, fogTiles);
    }

    /**
     * Update fovable field of view (fog of war).
     * 
     * @param fovables The entities reference.
     */
    public void update(Collection<Fovable> fovables)
    {
        mapHidden.update(fovables);
        mapFogged.reset();
        mapFogged.update(fovables);
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
        return mapHidden.getTile(tx, ty).getNumber() == MapTileFog.NO_FOG;
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
        return mapFogged.getTile(tx, ty).getNumber() < MapTileFog.FOG;
    }

    /*
     * MapTileRenderer
     */

    @Override
    public void renderTile(Graphic g, Tile tile, int x, int y)
    {
        final int tx = tile.getInTileX();
        final int ty = tile.getInTileY();

        final Tile fogTile = mapFogged.getTile(tx, ty);
        if (fogMap && fogTile != null && fogTile.getNumber() != MapTileFog.NO_FOG)
        {
            fogTiles.setLocation(x, y);
            fogTiles.setTile(fogTile.getNumber());
            fogTiles.render(g);
        }

        final Tile hideTile = mapHidden.getTile(tx, ty);
        if (hideMap && hideTile != null && hideTile.getNumber() != MapTileFog.NO_FOG)
        {
            hideTiles.setTile(hideTile.getNumber());
            hideTiles.setLocation(x, y);
            hideTiles.render(g);
        }
    }
}
