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
package com.b3dgs.lionengine.headless;

/**
 * Represents a key event with name and code.
 */
public final class KeyEvent
{
    /** Key code. */
    private final int code;
    /** Key name. */
    private final char name;

    /**
     * Create event.
     * 
     * @param code The key code.
     * @param name The key name.
     */
    public KeyEvent(int code, char name)
    {
        super();

        this.code = code;
        this.name = name;
    }

    /**
     * Get the code.
     * 
     * @return The code
     */
    public int getCode()
    {
        return code;
    }

    /**
     * Get the name.
     * 
     * @return The name
     */
    public char getName()
    {
        return name;
    }
}
