/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.selector;

import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Represents the selector model data.
 */
public class SelectorModel extends FeatureModel implements SelectorConfigurer
{
    /** Current selection area. */
    private final Rectangle selectionArea = new Rectangle();
    /** Clickable area. */
    private final Rectangle clickableArea = new Rectangle(0.0, 0.0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    /** Mouse click number to start a selection. */
    private int selectionClick;
    /** Currently selecting flag. */
    private boolean selecting;
    /** Raw selection y (used on rendering side). */
    private double selectRawY;
    /** Raw selection height (used on rendering side). */
    private double selectRawH;
    /** Enabled flag. */
    private boolean enabled = true;

    /**
     * Create a selector.
     */
    public SelectorModel()
    {
        super();
    }

    /**
     * Set the selection flag.
     * 
     * @param selecting The selection flag.
     */
    public void setSelecting(boolean selecting)
    {
        this.selecting = selecting;
    }

    /**
     * Get the click used to start selection.
     * 
     * @return The selection click.
     */
    public int getSelectionClick()
    {
        return selectionClick;
    }

    /**
     * Set the vertical raw selection.
     * 
     * @param selectRawY The vertical raw selection.
     */
    public void setSelectRawY(double selectRawY)
    {
        this.selectRawY = selectRawY;
    }

    /**
     * Set the height raw selection.
     * 
     * @param selectRawH The height raw selection.
     */
    public void setSelectRawH(double selectRawH)
    {
        this.selectRawH = selectRawH;
    }

    /**
     * Get the selection area.
     * 
     * @return The selection area.
     */
    public Rectangle getSelectionArea()
    {
        return selectionArea;
    }

    /**
     * Get the clickable area.
     * 
     * @return The clickable area.
     */
    public Rectangle getClickableArea()
    {
        return clickableArea;
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
     * Get the raw y.
     * 
     * @return The raw y.
     */
    public double getSelectRawY()
    {
        return selectRawY;
    }

    /**
     * Get the raw height.
     * 
     * @return The raw height.
     */
    public double getSelectRawH()
    {
        return selectRawH;
    }

    /**
     * Get the enabled flag.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /*
     * SelectorConfigurer
     */

    @Override
    public void setClickSelection(int click)
    {
        selectionClick = click;
    }

    @Override
    public void setClickableArea(Area area)
    {
        clickableArea.set(area.getX(), area.getY(), area.getWidthReal(), area.getHeightReal());
    }

    @Override
    public void setClickableArea(Viewer viewer)
    {
        clickableArea.set(viewer.getViewX(), viewer.getViewY(), viewer.getWidth(), viewer.getHeight());
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
