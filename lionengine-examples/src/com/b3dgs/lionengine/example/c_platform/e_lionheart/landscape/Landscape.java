/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.background.BackgroundPlatform;

/**
 * Represents a landscape by containing a background and a foreground.
 */
public class Landscape
{
    /** Landscape type. */
    private final LandscapeType type;
    /** Background element. */
    private final BackgroundPlatform background;
    /** Foreground element. */
    private final Foreground foreground;

    /**
     * Constructor.
     * 
     * @param type The landscape type.
     * @param background The background element.
     * @param foreground The foreground element.
     */
    public Landscape(LandscapeType type, BackgroundPlatform background, Foreground foreground)
    {
        this.type = type;
        this.background = background;
        this.foreground = foreground;
    }

    /**
     * Update the landscape.
     * 
     * @param extrp The extrapolation value.
     * @param camera The camera reference.
     */
    public void update(double extrp, CameraPlatform camera)
    {
        background.update(extrp, camera.getMovementHorizontal(), camera.getLocationY());
        foreground.update(extrp, camera.getMovementHorizontal(), camera.getLocationY());
    }

    /**
     * Render the background.
     * 
     * @param g The graphic output.
     */
    public void renderBackground(Graphic g)
    {
        background.render(g);
        foreground.renderBack(g);
    }

    /**
     * Render the foreground.
     * 
     * @param g The graphic output.
     */
    public void renderForeground(Graphic g)
    {
        foreground.renderFront(g);
    }

    /**
     * Get the current water height.
     * 
     * @return The current water height.
     */
    public double getWaterHeight()
    {
        return foreground.getTop();
    }

    /**
     * Get the landscape type.
     * 
     * @return The landscape type.
     */
    public LandscapeType getType()
    {
        return type;
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void setScreenSize(int width, int height)
    {
        ((Swamp) background).setScreenSize(width, height);
        foreground.setScreenSize(width, height);
    }
}
