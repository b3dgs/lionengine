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

import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of weapon types.
 */
public enum WeaponType implements ObjectType
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

    /*
     * ObjectType
     */

    @Override
    public String asPathName()
    {
        return ObjectTypeUtility.asPathName(this);
    }

    @Override
    public String asClassName()
    {
        return ObjectTypeUtility.asClassName(this);
    }

    @Override
    public String toString()
    {
        return ObjectTypeUtility.toString(this);
    }
}
