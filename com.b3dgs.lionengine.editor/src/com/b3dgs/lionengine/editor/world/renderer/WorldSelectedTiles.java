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
package com.b3dgs.lionengine.editor.world.renderer;

import java.util.Map;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.editor.world.TileSelectionListener;
import com.b3dgs.lionengine.editor.world.updater.Marker;
import com.b3dgs.lionengine.editor.world.updater.WorldInteractionTile;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.geom.Line;

/**
 * Handle the tiles selection rendering.
 */
public class WorldSelectedTiles implements WorldRenderListener, TileSelectionListener
{
    /** Color of the selected tile. */
    private static final ColorRgba COLOR_TILE_SELECTED = new ColorRgba(192, 192, 192, 96);
    /** Color of the selection area. */
    private static final ColorRgba COLOR_GROUP_SELECTION = new ColorRgba(240, 240, 240, 96);

    /** Map reference. */
    private final MapTile map;
    /** Map group. */
    private final MapTileGroup mapGroup;
    /** Camera reference. */
    private final Camera camera;
    /** Tile interaction. */
    private final WorldInteractionTile interactionTile;
    /** Selected tile. */
    private Tile tile;
    /** Selected tile group. */
    private String tileGroup;

    /**
     * Create the renderer.
     * 
     * @param services The services reference.
     */
    public WorldSelectedTiles(Services services)
    {
        map = services.get(MapTile.class);
        mapGroup = services.get(MapTileGroup.class);
        camera = services.get(Camera.class);
        interactionTile = services.get(WorldInteractionTile.class);
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

    /**
     * Render the assigning collision line.
     * 
     * @param g The graphic output.
     * @param line The current line.
     * @param scale The scale factor.
     * @param tw The current tile width.
     * @param th The current tile height.
     */
    private void renderAssigningCollision(Graphic g, Line line, double scale, int tw, int th)
    {
        final int x1 = (int) (camera.getViewpointX(line.getX1()) * scale);
        final int y1 = (int) (camera.getViewpointY(line.getY1()) * scale);
        final int x2 = (int) (camera.getViewpointX(line.getX2()) * scale);
        final int y2 = (int) (camera.getViewpointY(line.getY2()) * scale);

        g.drawLine(x1, y1, x2, y2);

        final Map<Integer, Marker> markers = interactionTile.getMarkers();
        for (final Marker marker : markers.values())
        {
            for (final Tile tile : marker.getTiles())
            {
                renderSelectedTile(g, tile, scale, tw, th);
            }
        }
    }

    /*
     * WorldRenderListener
     */

    @Override
    public void onRender(Graphic g, int width, int height, double scale, int tw, int th)
    {
        if (tileGroup != null)
        {
            g.setColor(COLOR_GROUP_SELECTION);
            renderSelectedGroup(g, tileGroup, scale, tw, th);
        }
        if (tile != null)
        {
            g.setColor(COLOR_TILE_SELECTED);
            renderSelectedTile(g, tile, scale, tw, th);
        }
        final Line currentLine = interactionTile.getCollisionLine();
        if (currentLine != null)
        {
            renderAssigningCollision(g, currentLine, scale, tw, th);
        }
    }

    /*
     * TileSelectionListener
     */

    @Override
    public void notifyTileSelected(Tile tile)
    {
        this.tile = tile;
    }

    @Override
    public void notifyTileGroupSelected(String group)
    {
        tileGroup = group;
    }
}
