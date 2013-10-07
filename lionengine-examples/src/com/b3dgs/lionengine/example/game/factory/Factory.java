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
package com.b3dgs.lionengine.example.game.factory;

import java.lang.reflect.InvocationTargetException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.SetupGame;

/**
 * Factory implementation example.
 * This factory will allow to create instances of type.
 * Each type should have one configuration file, in XML.
 * According to {@link Type} and {@link Type#asPathName()}, the configuration files should be as follow:
 * <p>
 * In 'factory' folder:
 * <ul>
 * <li>fly_machine.xml</li>
 * <li>ground_truck.xml</li>
 * </ul>
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Factory
        extends FactoryGame<Type, SetupGame>
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

    /** Parameter. Can be replaced by another type if needed. */
    private final Object param;

    /**
     * Constructor.
     */
    public Factory()
    {
        super(Type.class);
        param = new Object();
        loadAll(Type.values());
    }

    /**
     * Create a type from its enum, using a simple form. If a new type is added, this method should be updated.
     * 
     * @param type The enum type.
     * @return The type instance.
     */
    public TypeBase createTypeMethod1(Type type)
    {
        switch (type)
        {
            case FLY_MACHINE:
                return new FlyMachine(param);
            case GROUND_TRUCK:
                return new GroundTruck(param);
            default:
                throw new LionEngineException("Unknown type: " + type);
        }
    }

    /**
     * Create a type from its enum. No need to update this method in case of new type.
     * 
     * @param type The enum type.
     * @return The type instance.
     */
    public TypeBase createTypeMethod2(Type type)
    {
        return Factory.createGeneric(getClass(), type, new Object());
    }

    /*
     * FactoryGame
     */

    /**
     * Setup for each type are created here. Done when call to {@link #loadAll(Type[])} is performed. This allows to not
     * create the same data for each object, and then share them (less memory used).
     * <p>
     * {@inheritDoc}
     * </p>
     */
    @Override
    protected SetupGame createSetup(Type type)
    {
        return new SetupGame(Media.get("factory", type.asPathName() + ".xml"));
    }
}
