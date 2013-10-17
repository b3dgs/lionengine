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
package com.b3dgs.lionengine.example.tyrian.projectile.rear;

import java.lang.reflect.InvocationTargetException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.tyrian.effect.FactoryEffect;
import com.b3dgs.lionengine.example.tyrian.effect.HandlerEffect;
import com.b3dgs.lionengine.example.tyrian.projectile.Projectile;
import com.b3dgs.lionengine.example.tyrian.projectile.ProjectileType;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Projectile rear factory.
 */
public final class FactoryProjectileRear
{
    /**
     * Create a projectile instance from its type.
     * 
     * @param type The projectile type.
     * @param factoryEffect The factory effect reference.
     * @param handlerEffect The handler effect reference.
     * @param setup The setup reference.
     * @return The instance.
     */
    public static Projectile createProjectile(ProjectileType type, SetupSurfaceGame setup, FactoryEffect factoryEffect,
            HandlerEffect handlerEffect)
    {
        try
        {
            final StringBuilder clazz = new StringBuilder(FactoryProjectileRear.class.getPackage().getName());
            clazz.append('.').append(type.asClassName());
            return (Projectile) Class.forName(clazz.toString())
                    .getConstructor(SetupSurfaceGame.class, FactoryEffect.class, HandlerEffect.class)
                    .newInstance(setup, factoryEffect, handlerEffect);
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
