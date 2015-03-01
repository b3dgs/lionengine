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
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * This class represents the control panel (HUD), which will contain selected objects, actions, and many other
 * informations.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see ControlPanelListener
 */
public class ControlPanel
        implements Updatable
{
    /** List of listeners. */
    private final Collection<ControlPanelListener> listeners;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Selection area. */
    private final Rectangle selectionArea;
    /** Area outside panel (where the map is displayed). */
    private Rectangle outsidePanel;
    /** Mouse click number to start a selection. */
    private int clickSelection;
    /** Handler clicked state. */
    private boolean clicked;
    /** Handler clicked flag. */
    private boolean clickedFlag;
    /** Handler selecting flag. */
    private boolean selecting;
    /** Handler selected flag. */
    private boolean selected;
    /** Handler ordered flag. */
    private boolean ordered;
    /** Mouse location x when started click selection. */
    private double sy;
    /** Mouse location y when started click selection. */
    private double sx;
    /** Current selection x (stored in selectionArea when selection is done). */
    private double selectX;
    /** Current selection y (stored in selectionArea when selection is done). */
    private double selectY;
    /** Current selection width (stored in selectionArea when selection is done). */
    private double selectW;
    /** Current selection height (stored in selectionArea when selection is done). */
    private double selectH;
    /** Cursor selection color. */
    private ColorRgba colorSelection;

    /**
     * Create a control panel.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * <li>{@link Cursor}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @throws LionEngineException If missing {@link Services}.
     */
    public ControlPanel(Services services) throws LionEngineException
    {
        listeners = new HashSet<>(1);
        selectionArea = Geom.createRectangle();
        viewer = services.get(Viewer.class);
        cursor = services.get(Cursor.class);
        outsidePanel = null;
        clicked = false;
        clickedFlag = false;
        selecting = false;
        selected = false;
        ordered = false;
        sx = 0;
        sy = 0;
        selectX = 0;
        selectY = 0;
        selectW = 0;
        selectH = 0;
        colorSelection = ColorRgba.GRAY;
    }

    /**
     * Add a control panel listener.
     * 
     * @param listener The listener.
     */
    public void addListener(ControlPanelListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a control panel listener.
     * 
     * @param listener The listener.
     */
    public void removeListener(ControlPanelListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Render cursor selection routine. This function will draw the current active selection on screen, depending of its
     * localization, and using the camera point of view (location on map).
     * 
     * @param g The graphic output.
     * @param viewer The viewer reference.
     */
    public void renderCursorSelection(Graphic g, Viewer viewer)
    {
        if (selecting)
        {
            final int x = (int) viewer.getViewpointX(selectionArea.getX());
            final int y = (int) viewer.getViewpointY(selectionArea.getY() + selectionArea.getHeight());
            final int w = (int) selectionArea.getWidth();
            final int h = (int) selectionArea.getHeight();
            g.setColor(colorSelection);
            g.drawRect(x, y, w, h, false);
        }
    }

    /**
     * Reset order state (order failed).
     */
    public void resetOrder()
    {
        ordered = false;
        clicked = false;
    }

    /**
     * Set the mouse click selection value.
     * 
     * @param click The click id.
     */
    public void setClickSelection(int click)
    {
        clickSelection = click;
    }

    /**
     * Set clickable area on map (not on panel).
     * 
     * @param area The area representing the clickable area.
     */
    public void setClickableArea(Rectangle area)
    {
        outsidePanel = area;
    }

    /**
     * Set clickable area on map (not on panel), depending of the camera view.
     * 
     * @param viewer The viewer reference.
     */
    public void setClickableArea(Viewer viewer)
    {
        outsidePanel = Geom.createRectangle();
        outsidePanel.set(viewer.getViewX(), viewer.getViewY(), viewer.getWidth(), viewer.getHeight());
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
     * Get clickable area on map (out panel).
     * 
     * @return The clickable map area from panel.
     */
    public Rectangle getArea()
    {
        return outsidePanel;
    }

    /**
     * Check if cursor can click on panel.
     * 
     * @param cursor The cursor reference.
     * @return <code>true</code> if can click on panel, <code>false</code> else.
     */
    public boolean canClick(Cursor cursor)
    {
        return !outsidePanel.contains(cursor.getX(), cursor.getY());
    }

    /**
     * Set the ordered state (when an action skill is chosen).
     */
    public void ordered()
    {
        ordered = true;
        clicked = true;
        for (final ControlPanelListener listener : listeners)
        {
            listener.notifyStartOrder();
        }
    }

    /**
     * Check if panel is in ordered mode (waiting for a second click).
     * 
     * @return <code>true</code> if ordering, <code>false</code> else.
     */
    public boolean isOrdered()
    {
        return ordered;
    }

    /**
     * Check if panel is in selection mode.
     * 
     * @return <code>true</code> if selecting, <code>false</code> else.
     */
    public boolean isSelecting()
    {
        return selecting;
    }

    /**
     * Function handling cursor selection (preparing area transposed on the map).
     */
    protected void updateCursorSelection()
    {
        // Start selection on click, and reset last selection
        if (clickSelection == cursor.getClick())
        {
            final boolean canClick = canClick(cursor);
            clickedFlag = true;
            if (!selecting && !canClick && !ordered && !clicked)
            {
                selecting = true;
                sx = cursor.getX();
                sy = cursor.getY();
                computeSelection();
                for (final ControlPanelListener listener : listeners)
                {
                    listener.notifySelectionStarted(selectionArea);
                }
            }
        }
        else
        {
            if (selecting)
            {
                selected = true;
            }
            selecting = false;
        }

        // Update selection while selecting (mouse pressed, stop on releasing)
        if (selecting)
        {
            computeSelection();
        }
    }

    /**
     * Compute the selection from cursor location.
     */
    private void computeSelection()
    {
        selectX = sx;
        selectY = sy;
        selectW = UtilMath.fixBetween(cursor.getX() - sx, Double.MIN_VALUE, viewer.getViewX() + viewer.getX() - sx
                + viewer.getWidth());
        selectH = UtilMath.fixBetween(cursor.getY() - sy, Double.MIN_VALUE,
                viewer.getViewY() + viewer.getY() + viewer.getHeight() + sy);

        // This will avoid negative size
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
        if (selectX < 0)
        {
            selectW += selectX;
            selectX = 0;
        }
        if (selectY < 0)
        {
            selectH += selectY;
            selectY = 0;
        }
        selectionArea.set(selectX, selectY, selectW, selectH);
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        // Restore clicked state
        if (cursor.getClick() == 0)
        {
            clicked = false;
        }

        // Cursor selection
        if (!ordered)
        {
            updateCursorSelection();

            // Clear selection if done
            if (selected)
            {
                for (final ControlPanelListener listener : listeners)
                {
                    listener.notifySelectionDone(selectionArea);
                }
                selectionArea.set(-1, -1, 0, 0);
                selected = false;
            }
        }
        else
        {
            if (!clicked && cursor.getClick() > 0)
            {
                clicked = true;
                ordered = false;
                for (final ControlPanelListener listener : listeners)
                {
                    listener.notifyTerminateOrder();
                }
            }
        }

        // Apply clicked state if necessary
        if (clickedFlag)
        {
            clicked = true;
            clickedFlag = false;
        }
    }
}
