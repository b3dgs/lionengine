/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.background;

import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Describe a standard interface for a scrolling background, depending of a speed and a vertical location.
 */
public interface Background
{
    /**
     * Background updates.
     * 
     * @param extrp The extrapolation value.
     * @param speed The scrolling speed.
     * @param x The background x.
     * @param y The background y.
     */
    void update(double extrp, double speed, double x, double y);

    /**
     * Background renderings.
     * 
     * @param g The graphic output.
     */
    void render(Graphic g);

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    default void setScreenSize(int width, int height)
    {
        // Nothing by default
    }
}
