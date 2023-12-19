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
package com.b3dgs.lionengine.editor.world;

import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Represents the selection area.
 */
public class Selection
{
    /** Last selection area. */
    private final Rectangle selectionArea;
    /** Selection starting horizontal location. */
    private int startX;
    /** Selection starting vertical location. */
    private int startY;
    /** Selection ending horizontal location. */
    private int endX;
    /** Selection ending vertical location. */
    private int endY;
    /** Selection started. */
    private boolean started;
    /** Selecting flag. */
    private boolean selecting;
    /** Selected flag. */
    private boolean selected;

    /**
     * Create the selection updater.
     */
    public Selection()
    {
        selectionArea = new Rectangle();
    }

    /**
     * Start the selection.
     * 
     * @param mx The mouse x.
     * @param my The mouse y.
     */
    public void start(int mx, int my)
    {
        if (!isStarted())
        {
            startX = mx;
            startY = my;
            endX = mx;
            endY = my;
            started = true;
            selecting = false;
            selected = false;
        }
    }

    /**
     * Update the active selection.
     * 
     * @param mx The mouse x.
     * @param my The mouse y.
     */
    public void update(int mx, int my)
    {
        if (isStarted())
        {
            endX = mx;
            endY = my;
            started = true;
            selecting = true;
            selected = false;
        }
    }

    /**
     * Terminate current selection.
     * 
     * @param mx The mouse x.
     * @param my The mouse y.
     */
    public void end(int mx, int my)
    {
        if (isSelecting())
        {
            int sx = startX;
            int sy = startY;
            int ex = mx;
            int ey = my;

            // Ensure selection is positive
            if (ex < sx)
            {
                final int tmp = sx;
                sx = ex;
                ex = tmp;
            }
            if (ey < sy)
            {
                final int tmp = sy;
                sy = ey;
                ey = tmp;
            }
            startX = sx;
            startY = sy;
            endX = ex;
            endY = ey;
            selectionArea.set(startX, startY, endX - (double) startX, endY - (double) startY);
            started = false;
            selecting = false;
            selected = true;
        }
    }

    /**
     * Reset the selection.
     */
    public void reset()
    {
        startX = -1;
        startY = -1;
        endX = -1;
        endY = -1;
        selectionArea.set(startX, startY, 0, 0);
        started = false;
        selecting = false;
        selected = false;
    }

    /**
     * Draw the current selection.
     * 
     * @param g The graphic output.
     * @param color The selection color.
     */
    public void render(Graphic g, ColorRgba color)
    {
        if (isSelecting())
        {
            final int sx = startX;
            final int sy = startY;
            final int w = endX - sx;
            final int h = endY - sy;
            g.setColor(color);
            g.drawRect(sx, sy, w, h, true);
        }
    }

    /**
     * Get the selection area.
     * 
     * @return The selection area.
     */
    public Rectangle getArea()
    {
        return selectionArea;
    }

    /**
     * Check if selection has been started.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    public boolean isStarted()
    {
        return started;
    }

    /**
     * Check if currently selecting.
     * 
     * @return <code>true</code> if selecting, <code>false</code> else.
     */
    public boolean isSelecting()
    {
        return selecting;
    }

    /**
     * Check if selection is done.
     * 
     * @return <code>true</code> if selected, <code>false</code> else.
     */
    public boolean isSelected()
    {
        return selected;
    }
}
