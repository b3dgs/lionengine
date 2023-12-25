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
package com.b3dgs.lionengine.editor.map.world.renderer;

import com.b3dgs.lionengine.editor.map.world.updater.TileSelectionListener;
import com.b3dgs.lionengine.editor.map.world.updater.WorldInteractionTile;
import com.b3dgs.lionengine.editor.world.renderer.WorldRenderListener;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroup;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Handle the tiles selection rendering.
 */
public class WorldSelectedTiles implements WorldRenderListener
{
    /** Color of the selected tile. */
    private static final ColorRgba COLOR_TILE_SELECTED = new ColorRgba(192, 192, 192, 96);
    /** Color of the selection area. */
    private static final ColorRgba COLOR_GROUP_SELECTION = new ColorRgba(240, 240, 240, 96);

    /** Tile selection listener. */
    private final TileSelectionListenerImpl selection = new TileSelectionListenerImpl();
    /** Map reference. */
    private final MapTile map;
    /** Map group. */
    private final MapTileGroup mapGroup;
    /** Camera reference. */
    private final Camera camera;

    /**
     * Create the renderer.
     * 
     * @param services The services reference.
     */
    public WorldSelectedTiles(Services services)
    {
        map = services.get(MapTile.class);
        mapGroup = map.getFeature(MapTileGroup.class);
        camera = services.get(Camera.class);
        services.get(WorldInteractionTile.class).addListener(selection);
    }

    /**
     * Get the selection listener.
     * 
     * @return The selection listener.
     */
    public TileSelectionListener getListener()
    {
        return selection;
    }

    /**
     * Render the selected tiles group.
     * 
     * @param g The graphic output.
     * @param selectedGroup The selected group reference.
     * @param scale The scale factor.
     * @param tw The current tile width.
     * @param th The current tile height.
     */
    private void renderSelectedGroup(Graphic g, String selectedGroup, double scale, int tw, int th)
    {
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null && CollisionGroup.same(selectedGroup, mapGroup.getGroup(tile)))
                {
                    final int x = (int) (camera.getViewpointX(tile.getX()) * scale);
                    final int y = (int) (camera.getViewpointY(tile.getY()) * scale) - th;
                    g.drawRect(x, y, tw, th, true);
                }
            }
        }
    }

    /**
     * Render the selected tile.
     * 
     * @param g The graphic output.
     * @param selectedTile The selected tile reference.
     * @param scale The scale factor.
     * @param tw The current tile width.
     * @param th The current tile height.
     */
    private void renderSelectedTile(Graphic g, Tile selectedTile, double scale, int tw, int th)
    {
        final int x = (int) (camera.getViewpointX(selectedTile.getX()) * scale);
        final int y = (int) (camera.getViewpointY(selectedTile.getY()) * scale) - th;
        g.drawRect(x, y, tw, th, true);
    }

    @Override
    public void onRender(Graphic g, int width, int height, double scale, int tw, int th)
    {
        final String tileGroup = selection.getTileGroup();
        if (tileGroup != null)
        {
            g.setColor(COLOR_GROUP_SELECTION);
            renderSelectedGroup(g, tileGroup, scale, tw, th);
        }

        final Tile tile = selection.getTile();
        if (tile != null)
        {
            g.setColor(COLOR_TILE_SELECTED);
            renderSelectedTile(g, tile, scale, tw, th);
        }
    }
}
