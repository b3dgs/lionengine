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
package com.b3dgs.lionengine.game.feature.tile.map.viewer;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileRendererModel;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Map tile renderer default implementation.
 */
public class MapTileViewerModel extends FeatureAbstract implements MapTileViewer
{
    /** Map tiles renderers. */
    private final Collection<MapTileRenderer> renderers = new ArrayList<>();
    /** Map reference. */
    private final MapTile map;
    /** Viewer reference. */
    private final Viewer viewer;

    /**
     * Create feature. It uses default renderer: {@link MapTileRendererModel}.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public MapTileViewerModel(Services services)
    {
        super();

        Check.notNull(services);

        map = services.get(MapTile.class);
        viewer = services.get(Viewer.class);
        renderers.add(new MapTileRendererModel());
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
            final int x = (int) Math.floor(tile.getX() - viewX);
            final int y = (int) Math.floor(-tile.getY() + viewY - tile.getHeight());

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
        if (map.isCreated())
        {
            final int inTileHeight = (int) Math.ceil(viewer.getHeight() / (double) map.getTileHeight());
            final int sy = (int) Math.floor((viewer.getY() - viewer.getViewY()) / map.getTileHeight());
            final double viewY = viewer.getY() + viewer.getScreenHeight();

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
}
