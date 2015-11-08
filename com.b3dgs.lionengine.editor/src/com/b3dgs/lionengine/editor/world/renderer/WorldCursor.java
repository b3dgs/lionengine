/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.editor.utility.UtilWorld;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.updater.WorldUpdater;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.geom.Point;

/**
 * Handle the world cursor rendering.
 */
public class WorldCursor implements WorldRenderListener
{
    /** Color of the selection area. */
    private static final ColorRgba COLOR_CURSOR_SELECTION = new ColorRgba(240, 240, 240, 96);
    /** Color of the pixel area. */
    private static final ColorRgba COLOR_CURSOR_PIXEL = new ColorRgba(240, 128, 128, 255);

    /** Map reference. */
    private final MapTile map;
    /** Camera reference. */
    private final Camera camera;
    /** Updater reference. */
    private final WorldUpdater world;

    /**
     * Create the renderer.
     * 
     * @param services The services reference.
     */
    public WorldCursor(Services services)
    {
        map = services.get(MapTile.class);
        camera = services.get(Camera.class);
        world = services.get(WorldUpdater.class);
    }

    /**
     * Draw the pointed tile.
     * 
     * @param g The graphic output.
     * @param scale The current world scaling.
     * @param tile The current selected tile.
     * @param tw The current tile width.
     * @param th The current tile height.
     */
    private void drawCursorTile(Graphic g, double scale, Tile tile, int tw, int th)
    {
        g.setColor(COLOR_CURSOR_SELECTION);

        final int x = (int) (camera.getViewpointX(tile.getX()) * scale);
        final int y = (int) (camera.getViewpointY(tile.getY()) * scale) - th;

        g.drawRect(x, y, tw, th, true);
    }

    /**
     * Draw the pointed cursor pixel.
     * 
     * @param g The graphic output.
     * @param scale The current world scaling.
     */
    private void drawCursorPixel(Graphic g, double scale)
    {
        g.setColor(COLOR_CURSOR_PIXEL);

        final int size = (int) Math.round(scale);
        final Point mouse = UtilWorld.getPoint(camera, world.getMouseX(), world.getMouseY());
        final int mx = (int) (camera.getViewpointX(mouse.getX()) * scale);
        final int my = (int) (camera.getViewpointY(mouse.getY()) * scale) - size;

        g.drawRect(mx, my, size, size, true);
    }

    /*
     * WorldRenderListener
     */

    @Override
    public void onRender(Graphic g, int width, int height, double scale, int tw, int th)
    {
        if (WorldModel.INSTANCE.isPalette(PaletteType.POINTER_TILE))
        {
            final Tile tile = UtilWorld.getTile(map, camera, world.getMouseX(), world.getMouseY());
            if (tile != null)
            {
                drawCursorTile(g, scale, tile, tw, th);
                drawCursorPixel(g, scale);
            }
        }
    }
}
