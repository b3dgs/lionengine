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
package com.b3dgs.lionengine.example.tyrian.entity.dynamic;

import java.util.Locale;

/**
 * List of dynamic entity types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityDynamicType
{
    /** Meteor little 1. */
    METEOR_LITTLE_1,
    /** Meteor little 2. */
    METEOR_LITTLE_2,
    /** Meteor medium 1. */
    METEOR_MEDIUM_1,
    /** Meteor medium 2. */
    METEOR_MEDIUM_2,
    /** Meteor big. */
    METEOR_BIG;

    /**
     * Get the name as a path (lower case).
     * 
     * @return The name.
     */
    public String asPathName()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Get the class name equivalence.
     * 
     * @return The class name equivalence.
     */
    public String asClassName()
    {
        final char[] name = toString().toCharArray();
        for (int i = 0; i < name.length; i++)
        {
            if (name[i] == '_')
            {
                name[i + 1] = Character.toUpperCase(name[i + 1]);
            }
        }
        return String.valueOf(name).replace("_", "");
    }

    /**
     * Get the title name (first letter as upper).
     * 
     * @return The title name.
     */
    @Override
    public String toString()
    {
        final String string = asPathName();
        return Character.toString(string.charAt(0)).toUpperCase(Locale.ENGLISH) + string.substring(1);
    }
}
