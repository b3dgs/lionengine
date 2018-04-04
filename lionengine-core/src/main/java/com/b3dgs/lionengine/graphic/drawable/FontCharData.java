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
package com.b3dgs.lionengine.graphic.drawable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Character data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
final class FontCharData
{
    /** Character id. */
    private final int id;
    /** Character width. */
    private final int width;
    /** Character height. */
    private final int height;

    /**
     * Internal constructor.
     * 
     * @param id The character id (must be superior or equal to 0).
     * @param width The character width (must be superior or equal to 0).
     * @param height The character height (must be superior or equal to 0).
     * @throws LionEngineException If invalid arguments.
     */
    FontCharData(int id, int width, int height)
    {
        Check.superiorOrEqual(id, 0);
        Check.superiorOrEqual(width, 0);
        Check.superiorOrEqual(height, 0);

        this.id = id;
        this.width = width;
        this.height = height;
    }

    /**
     * Get the character id.
     * 
     * @return The character id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Get the character width.
     * 
     * @return The character width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the character height.
     * 
     * @return The character height.
     */
    public int getHeight()
    {
        return height;
    }
}
