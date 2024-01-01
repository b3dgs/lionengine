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
package com.b3dgs.lionengine.editor.world.renderer;

import com.b3dgs.lionengine.editor.world.updater.WorldUpdater;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Handle the grid rendering.
 */
public class WorldGridRenderer implements WorldRenderListener
{
    /** Color of the grid. */
    private static final ColorRgba COLOR_GRID = new ColorRgba(96, 96, 96);

    /** Map. */
    private final MapTile map;
    /** Camera. */
    private final Camera camera;
    /** Updater. */
    private final WorldUpdater world;

    /**
     * Create a world renderer with grid enabled.
     * 
     * @param services The services reference.
     */
    public WorldGridRenderer(Services services)
    {
        map = services.get(MapTile.class);
        camera = services.get(Camera.class);
        world = services.get(WorldUpdater.class);
    }

    @Override
    public void onRender(Graphic g, int width, int height, double scale, int ctw, int cth)
    {
        if (world.isGridEnabled() && map.isCreated())
        {
            final Tile tile = findTileInView();
            if (tile != null)
            {
                final double widthScaled = camera.getWidth() * scale;
                final double heightScaled = camera.getHeight() * scale;

                g.setColor(COLOR_GRID);
                renderHorizontalLines(g, tile, scale, widthScaled, heightScaled);
                renderVerticalLines(g, tile, scale, widthScaled, heightScaled);
            }
        }
    }

    /**
     * Render horizontal lines.
     * 
     * @param g The graphic output.
     * @param tile The tile to render.
     * @param scale The scale value.
     * @param widthScaled The width scaled.
     * @param heightScaled The height scaled.
     */
    private void renderHorizontalLines(Graphic g, Tile tile, double scale, double widthScaled, double heightScaled)
    {
        final double sx = camera.getViewpointX(tile.getX()) * scale;
        final double tw = map.getTileWidth() * scale;

        for (double x = sx; x < widthScaled; x += tw)
        {
            g.drawLine((int) Math.floor(x), 0, (int) Math.floor(x), (int) Math.floor(heightScaled));
        }
        for (double x = sx; x > 0; x -= tw)
        {
            g.drawLine((int) Math.floor(x), 0, (int) Math.floor(x), (int) Math.floor(heightScaled));
        }
    }

    /**
     * Render verticals lines.
     * 
     * @param g The graphic output.
     * @param tile The tile to render.
     * @param scale The scale value.
     * @param widthScaled The width scaled.
     * @param heightScaled The height scaled.
     */
    private void renderVerticalLines(Graphic g, Tile tile, double scale, double widthScaled, double heightScaled)
    {
        final double sy = camera.getViewpointY(tile.getY()) * scale;
        final double th = map.getTileHeight() * scale;

        for (double y = sy; y < heightScaled; y += th)
        {
            g.drawLine(0, (int) Math.floor(y), (int) Math.floor(widthScaled), (int) Math.floor(y));
        }
        for (double y = sy; y > 0; y -= th)
        {
            g.drawLine(0, (int) Math.floor(y), (int) Math.floor(widthScaled), (int) Math.floor(y));
        }
    }

    /**
     * Find a tile in view area.
     * 
     * @return The tile found, <code>null</code> if none.
     */
    private Tile findTileInView()
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int sx = (int) Math.floor(camera.getX()) / th;
        final int sy = (int) Math.floor(camera.getY()) / tw;

        for (int ty = 0; ty <= camera.getHeight() / th + 1; ty++)
        {
            for (int tx = 0; tx <= camera.getWidth() / tw + 1; tx++)
            {
                final Tile tile = map.getTile(sx + tx, sy + ty);
                if (tile != null)
                {
                    return tile;
                }
            }
        }
        return null;
    }
}
