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
package com.b3dgs.lionengine.game.map.feature.viewer;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileRenderer;
import com.b3dgs.lionengine.game.map.MapTileRendererModel;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Viewer;

/**
 * Map tile renderer default implementation.
 */
public class MapTileViewerModel extends FeatureModel implements MapTileViewer
{
    /** Map tiles renderers. */
    private final Collection<MapTileRenderer> renderers = new ArrayList<MapTileRenderer>();
    /** Map reference. */
    private MapTile map;
    /** Viewer reference. */
    private Viewer viewer;

    /**
     * Create the viewer. It is shipped with a default renderer if no one defined: {@link MapTileRendererModel}.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * <li>{@link Viewer}</li>
     * </ul>
     */
    public MapTileViewerModel()
    {
        super();
    }

    /**
     * Render the tile from location.
     * 
     * @param g The graphic output.
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     * @param viewX The horizontal view offset.
     * @param viewY The vertical view offset.
     */
    private void renderTile(Graphic g, int tx, int ty, double viewX, double viewY)
    {
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final int x = (int) (tile.getX() - viewX);
            final int y = (int) (-tile.getY() + viewY - tile.getHeight());

            for (final MapTileRenderer renderer : renderers)
            {
                renderer.renderTile(g, map, tile, x, y);
            }
        }
    }

    /**
     * Render horizontal tiles.
     * 
     * @param g The graphic output.
     * @param ty The current vertical tile location.
     * @param viewY The vertical view offset.
     */
    private void renderHorizontal(Graphic g, int ty, double viewY)
    {
        final int inTileWidth = (int) Math.ceil(viewer.getWidth() / (double) map.getTileWidth());
        final int sx = (int) Math.floor((viewer.getX() + viewer.getViewX()) / map.getTileWidth());
        final double viewX = viewer.getX();

        for (int h = 0; h <= inTileWidth; h++)
        {
            final int tx = h + sx;
            if (!(tx < 0 || tx >= map.getInTileWidth()))
            {
                renderTile(g, tx, ty, viewX, viewY);
            }
        }
    }

    /*
     * MapTileViewer
     */

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        map = services.get(MapTile.class);
        viewer = services.get(Viewer.class);
        if (renderers.isEmpty())
        {
            renderers.add(new MapTileRendererModel());
        }
    }

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
    public void clear()
    {
        renderers.clear();
    }

    @Override
    public void render(Graphic g)
    {
        final int inTileHeight = (int) Math.ceil(viewer.getHeight() / (double) map.getTileHeight());
        final int sy = (int) Math.floor((viewer.getY() - viewer.getViewY()) / map.getTileHeight());
        final double viewY = viewer.getY() - viewer.getViewY() + viewer.getScreenHeight() - viewer.getViewY();

        for (int v = 0; v <= inTileHeight; v++)
        {
            final int ty = v + sy;
            if (!(ty < 0 || ty >= map.getInTileHeight()))
            {
                renderHorizontal(g, ty, viewY);
            }
        }
    }
}
