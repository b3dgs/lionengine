/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Utility class related to java reflection.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilReflection
{
    /** Constructor error. */
    private static final String ERROR_CONSTRUCTOR = "Unable to create the following type: ";

    /**
     * Create a class instance with its parameters.
     * 
     * @param <T> The element type used.
     * @param type The class type to instantiate.
     * @param paramTypes The class base type for each parameter.
     * @param params The constructor parameters.
     * @return The class instance.
     * @throws NoSuchMethodException If no constructor found.
     * @throws LionEngineException If unable to create the instance or type is <code>null</code>.
     */
    public static <T> T create(Class<?> type, Class<?>[] paramTypes, Object... params)
            throws NoSuchMethodException, LionEngineException
    {
        Check.notNull(type);
        try
        {
            final Constructor<?> constructor = getCompatibleConstructor(type, paramTypes);
            final boolean accessible = constructor.isAccessible();
            if (!accessible)
            {
                constructor.setAccessible(true);
            }
            @SuppressWarnings("unchecked")
            final T object = (T) constructor.newInstance(params);
            if (constructor.isAccessible() != accessible)
            {
                constructor.setAccessible(accessible);
            }
            return object;
        }
        catch (final NoSuchMethodException exception)
        {
            throw exception;
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR + type);
        }
        catch (final InstantiationException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR + type);
        }
        catch (final IllegalAccessException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR + type);
        }
        catch (final InvocationTargetException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR + type);
        }
    }

    /**
     * Get a compatible constructor with the following parameters.
     * 
     * @param type The class type.
     * @param paramTypes The parameters types.
     * @return The constructor found.
     * @throws NoSuchMethodException If no constructor found.
     */
    private static Constructor<?> getCompatibleConstructor(Class<?> type, Class<?>[] paramTypes)
            throws NoSuchMethodException
    {
        for (final Constructor<?> current : type.getDeclaredConstructors())
        {
            final Class<?>[] constructorTypes = current.getParameterTypes();
            if (constructorTypes.length == paramTypes.length)
            {
                boolean found = true;
                for (int i = 0; i < paramTypes.length; i++)
                {
                    if (!constructorTypes[i].isAssignableFrom(paramTypes[i]))
                    {
                        found = false;
                        break;
                    }
                }
                if (found)
                {
                    return current;
                }
            }
        }
        throw new NoSuchMethodException("No compatible constructor found for "
                                        + type.getName()
                                        + " with: "
                                        + Arrays.asList(paramTypes));
    }

    /**
     * Get the parameter types as array.
     * 
     * @param arguments The arguments list.
     * @return The arguments type array.
     */
    public static Class<?>[] getParamTypes(Object... arguments)
    {
        final Collection<Object> types = new ArrayList<Object>();
        for (final Object argument : arguments)
        {
            types.add(argument.getClass());
        }
        final Class<?>[] typesArray = new Class<?>[types.size()];
        return types.toArray(typesArray);
    }

    /**
     * Private constructor.
     */
    private UtilReflection()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
