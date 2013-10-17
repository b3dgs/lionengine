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

import java.lang.reflect.InvocationTargetException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.game.factory.Type;
import com.b3dgs.lionengine.example.game.factory.TypeBase;
import com.b3dgs.lionengine.example.tyrian.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.tyrian.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.tyrian.weapon.front.FactoryWeaponFront;
import com.b3dgs.lionengine.example.tyrian.weapon.rear.FactoryWeaponRear;
import com.b3dgs.lionengine.game.projectile.FactoryLauncherGame;

/**
 * Weapon factory.
 */
public final class FactoryWeapon
        extends FactoryLauncherGame<WeaponType, Weapon>
{
    /**
     * Create a type instance from its enum, using a generic way.
     * 
     * @param factory The factory class.
     * @param type The enum type.
     * @param param The instance parameter.
     * @return The instance.
     */
    public static TypeBase createGeneric(Class<?> factory, Type type, Object param)
    {
        try
        {
            final StringBuilder clazz = new StringBuilder(factory.getPackage().getName());
            clazz.append('.').append(type.asClassName());
            return (TypeBase) Class.forName(clazz.toString()).getConstructor(Object.class).newInstance(param);
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

    /** Factory reference. */
    private final FactoryProjectile factory;
    /** Handler reference. */
    private final HandlerProjectile handler;

    /**
     * Constructor.
     * 
     * @param factory The factory reference.
     * @param handler The handler reference.
     */
    public FactoryWeapon(FactoryProjectile factory, HandlerProjectile handler)
    {
        super();
        this.factory = factory;
        this.handler = handler;
    }

    /*
     * FactoryLauncherGame
     */

    @Override
    public Weapon createLauncher(WeaponType type)
    {
        switch (type.getCategory())
        {
            case FRONT:
                return FactoryWeaponFront.createWeapon(type, factory, handler);
            case REAR:
                return FactoryWeaponRear.createWeapon(type, factory, handler);
            default:
                throw new LionEngineException("Unknown type: " + type);
        }
    }
}
