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

import com.b3dgs.lionengine.editor.world.updater.Marker;
import com.b3dgs.lionengine.editor.world.updater.WorldInteractionTileCollision;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.geom.Line;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Handle the tiles selection rendering.
 */
public class WorldSelectedTilesCollision implements WorldRenderListener
{
    /** Camera reference. */
    private final Camera camera;
    /** Tile interaction. */
    private final WorldInteractionTileCollision interactionTile;

    /**
     * Create the renderer.
     * 
     * @param services The services reference.
     */
    public WorldSelectedTilesCollision(Services services)
    {
        camera = services.get(Camera.class);
        interactionTile = services.get(WorldInteractionTileCollision.class);
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

    /*
     * WorldRenderListener
     */

    @Override
    public void onRender(Graphic g, int width, int height, double scale, int tw, int th)
    {
        final Line currentLine = interactionTile.getCollisionLine();
        if (currentLine != null)
        {
            renderAssigningCollision(g, currentLine, scale, tw, th);
        }
    }
}
