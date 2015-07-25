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
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.editor.world.WorldRenderListener;
import com.b3dgs.lionengine.editor.world.updater.WorldViewUpdater;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Handle the grid rendering.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldGrid implements WorldRenderListener
{
    /** Color of the grid. */
    private static final ColorRgba COLOR_GRID = new ColorRgba(128, 128, 128, 128);

    /** Updater. */
    private final WorldViewUpdater world;

    /**
     * Create a world view renderer with grid enabled.
     * 
     * @param services The services reference.
     */
    public WorldGrid(Services services)
    {
        world = services.get(WorldViewUpdater.class);
    }

    /*
     * WorldRenderListener
     */

    @Override
    public void onRender(Graphic g, int width, int height, double scale, int tw, int th, int offsetY)
    {
        if (world.isGridEnabled())
        {
            g.setColor(COLOR_GRID);

            // Render horizontal lines
            for (int v = height - offsetY; v > -offsetY; v -= th)
            {
                g.drawLine(0, v + offsetY, width - 1, v + offsetY);
            }
            // Render vertical lines
            for (int h = 0; h < width; h += tw)
            {
                g.drawLine(h, 0, h, height - 1);
            }
        }
    }
}
