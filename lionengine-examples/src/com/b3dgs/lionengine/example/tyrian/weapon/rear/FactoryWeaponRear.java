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
package com.b3dgs.lionengine.example.tyrian.weapon.rear;

import java.lang.reflect.InvocationTargetException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.tyrian.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.tyrian.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.tyrian.weapon.Weapon;
import com.b3dgs.lionengine.example.tyrian.weapon.WeaponType;

/**
 * Weapon rear factory.
 */
public final class FactoryWeaponRear
{
    /**
     * Create a weapon instance from its enum.
     * 
     * @param type The weapon type.
     * @param factoryProjectile The factory reference.
     * @param handlerProjectile The handler reference.
     * @return The instance.
     */
    public static Weapon createWeapon(WeaponType type, FactoryProjectile factoryProjectile,
            HandlerProjectile handlerProjectile)
    {
        try
        {
            final StringBuilder clazz = new StringBuilder(FactoryWeaponRear.class.getPackage().getName());
            clazz.append('.').append(type.asClassName());
            return (Weapon) Class.forName(clazz.toString())
                    .getConstructor(FactoryProjectile.class, HandlerProjectile.class)
                    .newInstance(factoryProjectile, handlerProjectile);
        }
        catch (InstantiationException
               | IllegalAccessException
               | IllegalArgumentException
               | InvocationTargetException
               | NoSuchMethodException
               | SecurityException
               | ClassCastException
               | ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, "Unknown type: " + type);
        }
    }
}
