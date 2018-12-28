/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.Updatable;

/**
 * Represents a frame instant, which has to be updated and rendered.
 */
public interface Frame extends Updatable
{
    /**
     * Checking when not updating nor rendering. Does nothing by default.
     */
    default void check()
    {
        // Does nothing by default
    }

    /**
     * Render updated frame.
     */
    void render();

    /**
     * Compute the frame rate depending of the game loop speed.
     * 
     * @param lastTime The last time value before game loop in nano.
     * @param currentTime The current time after game loop in nano (must be superior or equal to lastTime).
     */
    void computeFrameRate(long lastTime, long currentTime);
}
