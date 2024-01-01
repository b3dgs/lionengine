/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.background;

import com.b3dgs.lionengine.game.background.Background;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Foreground interface.
 */
public interface Foreground extends Background
{
    /**
     * Update routine.
     * 
     * @param extrp The extrapolation value.
     */
    default void update(double extrp)
    {
        // Nothing by default
    }

    /**
     * Render the front part of the water.
     * 
     * @param g The graphic output.
     */
    default void renderBack(Graphic g)
    {
        // Nothing by default
    }

    /**
     * Render the back part of the water.
     * 
     * @param g The graphic output.
     */
    default void renderFront(Graphic g)
    {
        // Nothing by default
    }

    /**
     * Set enabled flag.
     * 
     * @param enabled <code>true</code> to enable, <code>false</code> to disable.
     */
    void setEnabled(boolean enabled);

    /**
     * Reset foreground. Does nothing by default.
     */
    default void reset()
    {
        // Nothing by default
    }

    @Override
    default void update(double extrp, double speed, double x, double y)
    {
        // Nothing by default
    }

    @Override
    default void render(Graphic g)
    {
        // Nothing by default
    }
}
