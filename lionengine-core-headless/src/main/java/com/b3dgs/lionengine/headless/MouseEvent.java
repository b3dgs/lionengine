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
package com.b3dgs.lionengine.headless;

import com.b3dgs.lionengine.Check;

/**
 * Represents a key event with name and code.
 */
public final class MouseEvent
{
    /** Horizontal location. */
    private final int x;
    /** Vertical location. */
    private final int y;
    /** Associated click. */
    private final int click;

    /**
     * Create event.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param click The associated click (must be strictly inferior to {@link MouseHeadless#MAX_BUTTONS}).
     */
    public MouseEvent(int x, int y, int click)
    {
        super();

        Check.inferiorStrict(click, MouseHeadless.MAX_BUTTONS);

        this.x = x;
        this.y = y;
        this.click = click;
    }

    /**
     * Get the horizontal location.
     * 
     * @return The horizontal location.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Get the associated click.
     * 
     * @return The associated click
     */
    public int getClick()
    {
        return click;
    }
}
