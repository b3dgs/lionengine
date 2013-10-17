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
package com.b3dgs.lionengine.example.tyrian.weapon;

import java.util.Locale;

/**
 * List of weapon types.
 */
public enum WeaponType
{
    /*
     * Front
     */

    /** Pulse cannon. */
    PULSE_CANNON(WeaponCategory.FRONT),
    /** Missile rear launcher. */
    MISSILE_FRONT_LAUNCHER(WeaponCategory.FRONT),
    /** Machine gun. */
    MACHINE_GUN(WeaponCategory.FRONT),
    /** Hyper Pulse. */
    HYPER_PULSE(WeaponCategory.FRONT),

    /*
     * Rear
     */

    /** Missile rear launcher. */
    MISSILE_REAR_LAUNCHER(WeaponCategory.REAR),
    /** Wave cannon. */
    WAVE_CANNON(WeaponCategory.REAR);

    /** Weapon category. */
    private final WeaponCategory category;

    /**
     * Constructor.
     * 
     * @param category The weapon category.
     */
    private WeaponType(WeaponCategory category)
    {
        this.category = category;
    }

    /**
     * Get the weapon category.
     * 
     * @return The weapon category.
     */
    public WeaponCategory getCategory()
    {
        return category;
    }

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
     * Get the type name (first letter as upper).
     * 
     * @return The type name.
     */
    @Override
    public String toString()
    {
        final String string = asPathName();
        return Character.toString(string.charAt(0)).toUpperCase(Locale.ENGLISH) + string.substring(1);
    }
}
