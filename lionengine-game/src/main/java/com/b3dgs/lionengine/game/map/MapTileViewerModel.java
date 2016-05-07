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
package com.b3dgs.lionengine.game.map;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Viewer;

/**
 * Map tile renderer default implementation.
 * <p>
 * The {@link Services} must provide the following services:
 * </p>
 * <ul>
 * <li>{@link MapTile}</li>
 * <li>{@link Viewer}</li>
 * </ul>
 */
public class MapTileViewerModel implements MapTileViewer
{
    /** Map tiles renderers. */
    private final Collection<MapTileRenderer> renderers = new ArrayList<MapTileRenderer>();
    /** Map reference. */
    private final MapTile map;
    /** Viewer reference. */
    private final Viewer viewer;

    /**
     * Create the renderer.
     * 
     * @param services The services reference.
     */
    public MapTileViewerModel(Services services)
    {
        map = services.get(MapTile.class);
        viewer = services.get(Viewer.class);
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
     * @param offsetX The horizontal map offset.
     * @param offsetY The vertical map offset.
     */
    protected void render(Graphic g,
                          int screenHeight,
                          int sx,
                          int sy,
                          int inTileWidth,
                          int inTileHeight,
                          int offsetX,
                          int offsetY)
    {
        for (int v = 0; v <= inTileHeight; v++)
        {
            final int ty = v + (sy - offsetY) / map.getTileHeight();
            if (!(ty < 0 || ty >= map.getInTileHeight()))
            {
                renderHorizontal(g, screenHeight, sx, sy, inTileWidth, offsetX, ty);
            }
        }
    }

    /**
     * Render horizontal tiles.
     * 
     * @param g The graphic output.
     * @param screenHeight The view height (rendering start from bottom).
     * @param sx The starting x (view real location x).
     * @param sy The starting y (view real location y).
     * @param inTileWidth The number of rendered tiles in width.
     * @param ty The current vertical tile.
     * @param offsetX The horizontal map offset.
     */
    private void renderHorizontal(Graphic g, int screenHeight, int sx, int sy, int inTileWidth, int offsetX, int ty)
    {
        for (int h = 0; h <= inTileWidth; h++)
        {
            final int tx = h + (sx - offsetX) / map.getTileWidth();
            if (!(tx < 0 || tx >= map.getInTileWidth()))
            {
                renderTile(g, tx, ty, sx, sy, screenHeight);
            }
        }
    }

    /**
     * Get the tile at location and render it.
     * 
     * @param g The graphic output.
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     * @param sx The starting horizontal location.
     * @param sy The starting vertical location.
     * @param screenHeight The view height (rendering start from bottom).
     */
    private void renderTile(Graphic g, int tx, int ty, int sx, int sy, int screenHeight)
    {
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final int x = (int) tile.getX() - sx;
            final int y = -(int) tile.getY() - tile.getHeight() + sy + screenHeight;
            for (final MapTileRenderer renderer : renderers)
            {
                renderer.renderTile(g, tile, x, y);
            }
        }
    }

    /*
     * MapTileViewer
     */

    @Override
    public void addRenderer(MapTileRenderer renderer)
    {
        renderers.add(renderer);
    }

    @Override
    public void removeRenderer(MapTileRenderer renderer)
    {
        renderers.remove(renderer);
    }

    @Override
    public void render(Graphic g)
    {
        render(g,
               viewer.getHeight(),
               (int) Math.ceil(viewer.getX()),
               (int) Math.ceil(viewer.getY()),
               (int) Math.ceil(viewer.getWidth() / (double) map.getTileWidth()),
               (int) Math.ceil(viewer.getHeight() / (double) map.getTileHeight()),
               -viewer.getViewX(),
               viewer.getViewY());
    }
}
