/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;
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
 * <li>{@link #create(Media)}</li>
 * </ul>
 */
@FeatureInterface
public class FogOfWar extends FeatureAbstract implements MapTileRenderer, Listenable<RevealedListener>
{
    /** Hidden map. */
    private final MapTileFog mapHidden = new MapTileFog(false);
    /** Fogged map. */
    private final MapTileFog mapFogged = new MapTileFog(true);
    /** Tile width. */
    private int tw = 1;
    /** Tile width. */
    private int th = 1;
    /** Fog black tile. */
    private SpriteTiled hideTiles;
    /** Fog gray tiles. */
    private SpriteTiled fogTiles;
    /** Uses of hiding fog. */
    private boolean hideMap;
    /** Uses of fog. */
    private boolean fogMap;

    /** Map surface reference. */
    private MapTileSurface map;

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
     * @param config The fog configuration.
     */
    public void create(Media config)
    {
        tw = map.getTileWidth();
        th = map.getTileHeight();
        mapHidden.create(map, config, hideTiles);
        mapFogged.create(map, config, fogTiles);
    }

    /**
     * Update fog of war.
     * 
     * @param fovable The fovable to update with.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param nx The new horizontal location.
     * @param ny The new vertical location.
     */
    public void update(Fovable fovable, int ox, int oy, int nx, int ny)
    {
        if (fovable.canUpdate())
        {
            mapHidden.updateFov(fovable, nx, ny);
            mapFogged.reset(fovable, ox, oy);
            mapFogged.updateFov(fovable, nx, ny);
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
     * Set allow angle flag.
     * 
     * @param allow <code>true</code> to allow angle, <code>false</code> else.
     */
    public void setAllowAngle(boolean allow)
    {
        mapHidden.setAllowAngle(allow);
        mapFogged.setAllowAngle(allow);
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
        final int tx = (int) Math.floor(area.getX() / tw);
        final int ty = (int) Math.floor(area.getY() / th);
        final int atw = area.getWidth() / tw;
        final int ath = area.getHeight() / th;

        for (int ctx = tx; ctx < tx + atw; ctx++)
        {
            for (int cty = ty; cty < ty + ath; cty++)
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
        final int itw = tiled.getInTileWidth();
        final int ith = tiled.getInTileHeight();

        for (int ctx = tx; ctx < tx + itw; ctx++)
        {
            for (int cty = ty; cty < ty + ith; cty++)
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
        final Tile tile = mapFogged.getTile(tx, ty);
        return fogMap && tile != null && tile.getNumber() < MapTileFog.NO_FOG;
    }

    /*
     * Feature
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        map = provider.getFeature(MapTileSurface.class);
    }

    /*
     * Listenable
     */

    @Override
    public void addListener(RevealedListener listener)
    {
        mapHidden.addListener(listener);
        mapFogged.addListener(listener);
    }

    @Override
    public void removeListener(RevealedListener listener)
    {
        mapHidden.removeListener(listener);
        mapFogged.removeListener(listener);
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
