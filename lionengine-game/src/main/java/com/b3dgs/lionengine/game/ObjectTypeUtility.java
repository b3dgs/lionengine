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
package com.b3dgs.lionengine.game;

import java.util.Locale;

/**
 * Object type utility implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ObjectTypeUtility
{
    /**
     * Get the name as a path (lower case).
     * 
     * @param type The object type.
     * @return The name.
     */
    public static String getPathName(Enum<?> type)
    {
        return type.name().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Get the title name (first letter as upper).
     * 
     * @param type The object type.
     * @return The title name.
     */
    public static String toString(Enum<?> type)
    {
        final String string = ObjectTypeUtility.getPathName(type);
        return Character.toString(string.charAt(0)).toUpperCase(Locale.ENGLISH) + string.substring(1);
    }

    /**
     * Private constructor.
     */
    private ObjectTypeUtility()
    {
        throw new RuntimeException();
    }
}
