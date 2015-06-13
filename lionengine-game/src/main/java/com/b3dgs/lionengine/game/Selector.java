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
package com.b3dgs.lionengine.game;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * This class allows to perform a selection inside a dedicated area, and retrieve the {@link Rectangle} representing the
 * selection. Usage example:
 * <ul>
 * <li>{@link #setClickableArea(Viewer)} - Required to define the area where selection is allowed</li>
 * <li>{@link #setClickSelection(int)} - Recommended to set which mouse click should be used to start selection</li>
 * <li>{@link #setSelectionColor(ColorRgba)} - Optional for a custom color selection</li>
 * </ul>
 * Result can be retrieved by using {@link #addListener(SelectorListener)} to notify them the computed selection.
 * It will be then easy to check if objects are inside this area, and set them as <i>selected</i>.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see SelectorListener
 * @see Cursor
 * @see Viewer
 */
public class Selector
        implements Updatable, Renderable
{
    /** List of listeners. */
    private final Collection<SelectorListener> listeners;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Current selection area. */
    private final Rectangle selectionArea;
    /** Clickable area. */
    private final Rectangle clickableArea;
    /** Mouse click number to start a selection. */
    private int selectionClick;
    /** Currently selecting flag. */
    private boolean selecting;
    /** Mouse location x when started click selection. */
    private double startX;
    /** Mouse location y when started click selection. */
    private double startY;
    /** Raw selection y (used on rendering side). */
    private double selectRawY;
    /** Raw selection height (used on rendering side). */
    private double selectRawH;
    /** Cursor selection color. */
    private ColorRgba colorSelection;

    /**
     * Create a selector.
     * 
     * @param viewer The viewer reference.
     * @param cursor The cursor reference.
     */
    public Selector(Viewer viewer, Cursor cursor)
    {
        this.viewer = viewer;
        this.cursor = cursor;
        listeners = new HashSet<>(1);
        selectionArea = Geom.createRectangle();
        clickableArea = Geom.createRectangle();
        colorSelection = ColorRgba.GRAY;
    }

    /**
     * Add a selector listener.
     * 
     * @param listener The selector listener reference.
     */
    public void addListener(SelectorListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a selector listener.
     * 
     * @param listener The selector listener reference.
     */
    public void removeListener(SelectorListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Set the mouse click selection value.
     * 
     * @param click The click number.
     * @see InputDevicePointer
     */
    public void setClickSelection(int click)
    {
        selectionClick = click;
    }

    /**
     * Set clickable area (where selection can be performed on screen).
     * 
     * @param area The representation of the clickable area.
     */
    public void setClickableArea(Rectangle area)
    {
        clickableArea.set(area.getX(), area.getY(), area.getWidth(), area.getHeight());
    }

    /**
     * Set clickable area (where selection can be performed on screen).
     * 
     * @param viewer The viewer reference.
     */
    public void setClickableArea(Viewer viewer)
    {
        clickableArea.set(viewer.getViewX(), viewer.getViewY(), viewer.getWidth(), viewer.getHeight());
    }

    /**
     * Set the selection color.
     * 
     * @param color The selection color.
     */
    public void setSelectionColor(ColorRgba color)
    {
        colorSelection = color;
    }

    /**
     * Check if is in selection mode.
     * 
     * @return <code>true</code> if selecting, <code>false</code> else.
     */
    public boolean isSelecting()
    {
        return selecting;
    }

    /**
     * Check if can begin selection. Notify listeners if started.
     */
    private void checkBeginSelection()
    {
        final boolean canClick = !clickableArea.contains(cursor.getScreenX(), cursor.getScreenY());
        if (!selecting && !canClick)
        {
            selecting = true;
            startX = cursor.getX();
            startY = cursor.getY();

            for (final SelectorListener listener : listeners)
            {
                listener.notifySelectionStarted(Geom.createRectangle(selectionArea));
            }
        }
    }

    /**
     * Compute the selection from cursor location.
     */
    private void computeSelection()
    {
        final double viewX = viewer.getViewX() + viewer.getX();
        final double viewY = viewer.getY() - viewer.getViewY();
        final double currentX = UtilMath.fixBetween(cursor.getX(), viewX, viewX + viewer.getWidth());
        final double currentY = UtilMath.fixBetween(cursor.getY(), viewY, viewY + viewer.getHeight());

        double selectX = startX;
        double selectY = startY;
        double selectW = currentX - startX;
        double selectH = currentY - startY;

        // Viewer Y axis is inverted compared to screen axis
        selectRawY = selectY;
        selectRawH = startY - currentY;

        if (selectW < 0)
        {
            selectX += selectW;
            selectW = -selectW;
        }
        if (selectH < 0)
        {
            selectY += selectH;
            selectH = -selectH;
        }
        selectionArea.set(selectX, selectY, selectW, selectH);
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        if (selectionClick == cursor.getClick())
        {
            checkBeginSelection();
            if (selecting)
            {
                computeSelection();
            }
        }
        else if (selecting)
        {
            for (final SelectorListener listener : listeners)
            {
                listener.notifySelectionDone(Geom.createRectangle(selectionArea));
            }
            selectionArea.set(-1, -1, 0, 0);
            selecting = false;
        }
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        if (selecting)
        {
            final int x = (int) (selectionArea.getX() - viewer.getX());
            final int w = (int) selectionArea.getWidth();
            int y = (int) (viewer.getY() + viewer.getHeight() - selectRawY);
            int h = (int) selectRawH;
            if (h < 0)
            {
                y += h;
                h = -h;
            }
            if (y < 0)
            {
                h += y;
                y = 0;
            }
            g.setColor(colorSelection);
            g.drawRect(x, y, w, h, false);
        }
    }
}
