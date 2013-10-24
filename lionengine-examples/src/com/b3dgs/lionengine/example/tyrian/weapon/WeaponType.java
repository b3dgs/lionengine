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

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.tyrian.weapon.front.HyperPulse;
import com.b3dgs.lionengine.example.tyrian.weapon.front.MachineGun;
import com.b3dgs.lionengine.example.tyrian.weapon.front.MissileLauncherFront;
import com.b3dgs.lionengine.example.tyrian.weapon.front.PulseCannon;
import com.b3dgs.lionengine.example.tyrian.weapon.rear.MissileLauncherRear;
import com.b3dgs.lionengine.example.tyrian.weapon.rear.WaveCannonRear;
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
    PULSE_CANNON(PulseCannon.class, WeaponCategory.FRONT),
    /** Missile rear launcher. */
    MISSILE_LAUNCHER_FRONT(MissileLauncherFront.class, WeaponCategory.FRONT),
    /** Machine gun. */
    MACHINE_GUN(MachineGun.class, WeaponCategory.FRONT),
    /** Hyper Pulse. */
    HYPER_PULSE(HyperPulse.class, WeaponCategory.FRONT),

    /*
     * Rear
     */

    /** Missile rear launcher. */
    MISSILE_LAUNCHER_REAR(MissileLauncherRear.class, WeaponCategory.REAR),
    /** Wave cannon. */
    WAVE_CANNON_REAR(WaveCannonRear.class, WeaponCategory.REAR);

    /** Class target. */
    private final Class<?> target;
    /** Path name. */
    private final String pathName;
    /** Weapon category. */
    private final WeaponCategory category;

    /**
     * Constructor.
     * 
     * @param target The target class.
     * @param category The weapon category.
     */
    private WeaponType(Class<?> target, WeaponCategory category)
    {
        this.target = target;
        this.category = category;
        pathName = Media.getPath(ObjectTypeUtility.asPathName(category), ObjectTypeUtility.asPathName(this));
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
    public Class<?> getTargetClass()
    {
        return target;
    }

    @Override
    public String getPathName()
    {
        return pathName;
    }
}
