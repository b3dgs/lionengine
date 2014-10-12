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
package com.b3dgs.lionengine.game.platform.background;

import com.b3dgs.lionengine.drawable.Renderable;

/**
 * Represents a background element (contained in a background component).
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class BackgroundElement
{
    /** Renderable reference. */
    private final Renderable renderable;
    /** Main location x referential. */
    private int mainX;
    /** Main location y referential. */
    private int mainY;
    /** Offsets location x from reference. */
    private double offsetX;
    /** Offsets location y from reference. */
    private double offsetY;

    /**
     * Constructor.
     * 
     * @param mainX The main location x.
     * @param mainY The main location y.
     * @param renderable The element renderable.
     */
    public BackgroundElement(int mainX, int mainY, Renderable renderable)
    {
        this.mainX = mainX;
        this.mainY = mainY;
        this.renderable = renderable;
        offsetX = 0.0;
        offsetY = 0.0;
    }

    /**
     * Set the main horizontal location.
     * 
     * @param mainX The main horizontal location.
     */
    public void setMainX(int mainX)
    {
        this.mainX = mainX;
    }

    /**
     * Set the main vertical location.
     * 
     * @param mainY The main vertical location.
     */
    public void setMainY(int mainY)
    {
        this.mainY = mainY;
    }

    /**
     * Set horizontal offset value.
     * 
     * @param offsetX The horizontal offset value.
     */
    public void setOffsetX(double offsetX)
    {
        this.offsetX = offsetX;
    }

    /**
     * Set vertical offset value.
     * 
     * @param offsetY The vertical offset value.
     */
    public void setOffsetY(double offsetY)
    {
        this.offsetY = offsetY;
    }

    /**
     * Get renderable reference.
     * 
     * @return The renderable reference.
     */
    public Renderable getRenderable()
    {
        return renderable;
    }

    /**
     * Get main location x.
     * 
     * @return The main location x.
     */
    public int getMainX()
    {
        return mainX;
    }

    /**
     * Get main location y.
     * 
     * @return The main location y.
     */
    public int getMainY()
    {
        return mainY;
    }

    /**
     * Get horizontal offset.
     * 
     * @return The horizontal offset.
     */
    public double getOffsetX()
    {
        return offsetX;
    }

    /**
     * Get vertical offset.
     * 
     * @return The vertical offset.
     */
    public double getOffsetY()
    {
        return offsetY;
    }
}
