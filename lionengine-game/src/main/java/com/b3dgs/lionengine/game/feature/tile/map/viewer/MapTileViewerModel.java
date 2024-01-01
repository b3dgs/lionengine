/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Map tile renderer default implementation.
 */
public class MapTileViewerModel extends FeatureAbstract implements MapTileViewer
{
    /** Map tiles renderers. */
    private final List<MapTileRenderer> renderers = new ArrayList<>();
    /** Viewer reference. */
    private final Viewer viewer;

    /** Map tile surface. */
    private MapTileSurface map;

    /**
     * Create feature.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link MapTileSurface}</li>
     * </ul>
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public MapTileViewerModel(Services services)
    {
        super();

        viewer = services.get(Viewer.class);
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
            final int x = (int) Math.round(tile.getX() - viewX);
            final int y = (int) Math.round(-tile.getY() + viewY - tile.getHeight());

            for (int i = 0; i < renderers.size(); i++)
            {
                renderers.get(i).renderTile(g, tile, x, y);
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
            if (tx >= 0 && tx < map.getInTileWidth())
            {
                renderTile(g, tx, ty, viewX, viewY);
            }
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        map = provider.getFeature(MapTileSurface.class);
        renderers.add(this);
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
        if (map.isCreated())
        {
            final int inTileHeight = (int) Math.ceil(viewer.getHeight() / (double) map.getTileHeight());
            final int sy = (int) Math.floor((viewer.getY() - viewer.getViewY()) / map.getTileHeight());
            final double viewY = viewer.getY() + viewer.getScreenHeight();

            for (int v = 0; v <= inTileHeight; v++)
            {
                final int ty = v + sy;
                if (ty >= 0 && ty < map.getInTileHeight())
                {
                    renderHorizontal(g, ty, viewY);
                }
            }
        }
    }

    @Override
    public void renderTile(Graphic g, Tile tile, int x, int y)
    {
        final SpriteTiled sprite = map.getSheet(tile.getSheet());
        sprite.setLocation(x, y);
        sprite.setTile(tile.getNumber());
        sprite.render(g);
    }
}
