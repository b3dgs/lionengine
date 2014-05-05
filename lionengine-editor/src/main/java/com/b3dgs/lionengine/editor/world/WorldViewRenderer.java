/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.world;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.UtilityMath;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * World view paint listener, rendering the current world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class WorldViewRenderer
        implements PaintListener, MouseListener, MouseMoveListener
{
    /** Color of the grid. */
    private static final ColorRgba COLOR_GRID = new ColorRgba(128, 128, 128, 128);
    /** Color of the selection area. */
    private static final ColorRgba COLOR_MOUSE_SELECTION = new ColorRgba(240, 240, 240, 96);

    /**
     * Draw the grid.
     * 
     * @param g The graphic output.
     * @param tw Horizontal grid spacing (width).
     * @param th Vertical grid spacing (height).
     * @param areaX Horizontal global grid size.
     * @param areaY Vertical global grid size.
     * @param color Grid color.
     */
    private static void drawGrid(Graphic g, int tw, int th, int areaX, int areaY, ColorRgba color)
    {
        g.setColor(color);
        for (int v = 0; v <= areaY; v += tw)
        {
            g.drawLine(0, v, areaX, v);
        }
        for (int h = 0; h <= areaX; h += th)
        {
            g.drawLine(h, 0, h, areaY);
        }
    }

    /** The view model. */
    private final WorldViewModel model;
    /** Current horizontal mouse location. */
    private int mouseX;
    /** Current vertical mouse location. */
    private int mouseY;
    /** Selecting flag. */
    private boolean selecting;
    /** Moving entity flag. */
    private boolean moving;

    /**
     * Constructor.
     */
    public WorldViewRenderer()
    {
        model = WorldViewModel.INSTANCE;
    }

    /**
     * Render the world.
     * 
     * @param g The graphic output.
     * @param width The view width.
     * @param height The view height.
     */
    private void render(Graphic g, int width, int height)
    {
        final CameraGame camera = model.getCamera();
        final MapTile<?, ?> map = model.getMap();

        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int areaX = UtilityMath.getRounded(width, tw);
        final int areaY = UtilityMath.getRounded(height, th);

        camera.setView(0, 0, areaX - tw, areaY);

        // Background
        g.setColor(ColorRgba.GRAY_LIGHT);
        g.drawRect(0, 0, width, height, true);

        // Map area
        g.setColor(ColorRgba.BLUE);
        g.drawRect(0, 0, areaX, areaY, true);

        // Renders
        if (map.getNumberPatterns() > 0)
        {
            map.render(g, camera);
        }

        drawCursor(g, tw, th, areaX, areaY);
        WorldViewRenderer.drawGrid(g, tw, th, areaX, areaY, WorldViewRenderer.COLOR_GRID);
    }

    /**
     * Draw the cursor.
     * 
     * @param g The graphic output.
     * @param tw The tile width.
     * @param th The tile height.
     * @param areaX Maximum width.
     * @param areaY Maximum height.
     */
    private void drawCursor(Graphic g, int tw, int th, int areaX, int areaY)
    {
        if (!selecting && !moving)
        {
            if (mouseX >= 0 && mouseY >= 0 && mouseX < areaX && mouseY < areaY)
            {
                final int mx = UtilityMath.getRounded(mouseX, tw);
                final int my = UtilityMath.getRounded(mouseY, th);

                g.setColor(WorldViewRenderer.COLOR_MOUSE_SELECTION);
                g.drawRect(mx, my, tw, th, true);
            }
        }
    }

    /**
     * Update the mouse.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updateMouse(int mx, int my)
    {
        mouseX = mx;
        mouseY = my;
    }

    /*
     * PaintListener
     */

    @Override
    public void paintControl(PaintEvent paintEvent)
    {
        final GC gc = paintEvent.gc;
        final Graphic g = Core.GRAPHIC.createGraphic();
        g.setGraphic(gc);
        if (model.getMap() != null)
        {
            render(g, paintEvent.width, paintEvent.height);
        }
        else
        {
            gc.drawString("No map implementation defined, select it from the project view !", 0, 0, true);
        }
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseDoubleClick(MouseEvent mouseEvent)
    {
        // Nothing to do
    }

    @Override
    public void mouseDown(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;

        updateMouse(mx, my);
    }

    @Override
    public void mouseUp(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;

        moving = false;
        updateMouse(mx, my);
    }

    /*
     * MouseMoveListener
     */

    @Override
    public void mouseMove(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;

        updateMouse(mx, my);
    }
}
