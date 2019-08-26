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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileRenderer;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

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
@FeatureInterface
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
     * Create feature.
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
     * Update fog of war.
     * 
     * @param fovable The fovable to update with.
     */
    public void update(Fovable fovable)
    {
        mapHidden.updateFov(fovable);
        mapFogged.reset(fovable);
        mapFogged.updateFov(fovable);
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
     * Check if the area has been visited.
     * 
     * @param area The tiled to check.
     * @return <code>true</code> if hidden, <code>false</code> else.
     */
    public boolean isVisited(Area area)
    {
        final int tx = (int) Math.floor(area.getX() / hideTiles.getTileWidth());
        final int ty = (int) Math.floor(area.getY() / hideTiles.getTileHeight());
        final int tw = area.getWidth() / hideTiles.getTileWidth();
        final int th = area.getHeight() / hideTiles.getTileHeight();

        for (int ctx = tx; ctx < tx + tw; ctx++)
        {
            for (int cty = ty; cty < ty + th; cty++)
            {
                if (!isVisited(ctx, cty))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the tile is currently visible.
     * 
     * @param tiled The tiled to check.
     * @return <code>true</code> if hidden, <code>false</code> else.
     */
    public boolean isVisible(Tiled tiled)
    {
        final int tx = tiled.getInTileX();
        final int ty = tiled.getInTileY();
        final int tw = tiled.getInTileWidth();
        final int th = tiled.getInTileHeight();

        for (int ctx = tx; ctx < tx + tw; ctx++)
        {
            for (int cty = ty; cty < ty + th; cty++)
            {
                if (isVisited(ctx, cty) && !isFogged(ctx, cty))
                {
                    return true;
                }
            }
        }
        return false;
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
        final Tile tile = mapHidden.getTile(tx, ty);
        return !hideMap || tile != null && tile.getNumber() == MapTileFog.NO_FOG;
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
        final Tile tile = mapHidden.getTile(tx, ty);
        return fogMap && tile != null && mapFogged.getTile(tx, ty).getNumber() < MapTileFog.FOG;
    }

    /*
     * MapTileRenderer
     */

    @Override
    public void renderTile(Graphic g, MapTile map, Tile tile, int x, int y)
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
