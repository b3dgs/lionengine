/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents a gradient color.
 * 
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @param x1 The first horizontal location.
 * @param y1 The first vertical location.
 * @param color1 The first color.
 * @param x2 The last horizontal location.
 * @param y2 The last vertical location.
 * @param color2 The last color.
 */
public record ColorGradient(int x1, int y1, ColorRgba color1, int x2, int y2, ColorRgba color2)
{
    /**
     * Create a gradient color.
     * 
     * @param x1 The first horizontal location.
     * @param y1 The first vertical location.
     * @param color1 The first color.
     * @param x2 The last horizontal location.
     * @param y2 The last vertical location.
     * @param color2 The last color.
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public ColorGradient
    {
        Check.notNull(color1);
        Check.notNull(color2);
    }
}
