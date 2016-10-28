/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Utility class related to java reflection.
 */
public final class UtilReflection
{
    /** Constructor error. */
    private static final String ERROR_CONSTRUCTOR = "Unable to create the following type: ";
    /** Constructor compatibility error. */
    private static final String ERROR_NO_CONSTRUCTOR_COMPATIBLE = "No compatible constructor found for ";
    /** Constructor compatibility error. */
    private static final String ERROR_WITH = " with: ";
    /** Field error. */
    private static final String ERROR_FIELD = "Unable to access to the following field: ";
    /** Method error. */
    private static final String ERROR_METHOD = "Unable to access to the following method: ";
    /** Accessibility. */
    private static final AtomicBoolean ACCESSIBLE = new AtomicBoolean(true);

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
    public static <T> T create(Class<?> type, Class<?>[] paramTypes, Object... params) throws NoSuchMethodException
    {
        Check.notNull(type);
        try
        {
            final Constructor<?> constructor = getCompatibleConstructor(type, paramTypes);
            final boolean accessible = constructor.isAccessible();
            setAccessible(constructor, ACCESSIBLE.get());
            @SuppressWarnings("unchecked")
            final T object = (T) constructor.newInstance(params);
            if (constructor.isAccessible() != accessible)
            {
                setAccessible(constructor, accessible);
            }
            return object;
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
     * Get a compatible constructor with the following parameters.
     * 
     * @param type The class type.
     * @param paramTypes The parameters types.
     * @return The constructor found.
     * @throws NoSuchMethodException If no constructor found.
     */
    public static Constructor<?> getCompatibleConstructor(Class<?> type, Class<?>[] paramTypes)
            throws NoSuchMethodException
    {
        for (final Constructor<?> current : type.getDeclaredConstructors())
        {
            final Class<?>[] constructorTypes = current.getParameterTypes();
            if (constructorTypes.length == paramTypes.length && hasCompatibleConstructor(paramTypes, constructorTypes))
            {
                return current;
            }
        }
        throw new NoSuchMethodException(ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                        + type.getName()
                                        + ERROR_WITH
                                        + Arrays.asList(paramTypes));
    }

    /**
     * Get a compatible constructor with the following parameters considering parent side.
     * 
     * @param type The class type.
     * @param paramTypes The parameters types.
     * @return The constructor found.
     * @throws NoSuchMethodException If no constructor found.
     */
    public static Constructor<?> getCompatibleConstructorParent(Class<?> type, Class<?>[] paramTypes)
            throws NoSuchMethodException
    {
        for (final Constructor<?> current : type.getDeclaredConstructors())
        {
            final Class<?>[] constructorTypes = current.getParameterTypes();
            if (constructorTypes.length == paramTypes.length
                && hasCompatibleConstructorParent(paramTypes, constructorTypes))
            {
                return current;
            }
        }
        throw new NoSuchMethodException(ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                        + type.getName()
                                        + ERROR_WITH
                                        + Arrays.asList(paramTypes));
    }

    /**
     * Get method and call its return value with parameters.
     * 
     * @param <T> The object type.
     * @param object The object caller.
     * @param name The method name.
     * @param params The method parameters.
     * @return The value returned.
     */
    public static <T> T getMethod(Object object, String name, Object... params)
    {
        Check.notNull(object);
        Check.notNull(name);
        try
        {
            final Class<?> clazz = getClass(object);
            final Method method = clazz.getDeclaredMethod(name, getParamTypes(params));
            final boolean accessible = method.isAccessible();
            if (!accessible)
            {
                setAccessible(method, ACCESSIBLE.get());
            }
            @SuppressWarnings("unchecked")
            final T value = (T) method.invoke(object, params);
            if (method.isAccessible() != accessible)
            {
                setAccessible(method, accessible);
            }
            return value;
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_METHOD, name);
        }
        catch (final IllegalAccessException exception)
        {
            throw new LionEngineException(exception, ERROR_METHOD, name);
        }
        catch (final InvocationTargetException exception)
        {
            throw new LionEngineException(exception, ERROR_METHOD, name);
        }
    }

    /**
     * Get the field by reflection.
     * 
     * @param <T> The field type.
     * @param object The object to use.
     * @param name The field name.
     * @return The field found.
     * @throws LionEngineException If field not found.
     */
    public static <T> T getField(Object object, String name)
    {
        Check.notNull(object);
        Check.notNull(name);
        try
        {
            final Class<?> clazz = getClass(object);
            final Field field = clazz.getDeclaredField(name);
            final boolean accessible = field.isAccessible();
            if (!accessible)
            {
                setAccessible(field, ACCESSIBLE.get());
            }
            @SuppressWarnings("unchecked")
            final T value = (T) field.get(object);
            if (field.isAccessible() != accessible)
            {
                setAccessible(field, accessible);
            }
            return value;
        }
        catch (final NoSuchFieldException exception)
        {
            throw new LionEngineException(exception, ERROR_FIELD, name);
        }
        catch (final IllegalAccessException exception)
        {
            throw new LionEngineException(exception, ERROR_FIELD, name);
        }
    }

    /**
     * Set the object accessibility with an access controller.
     * 
     * @param object The accessible object.
     * @param accessible <code>true</code> if accessible, <code>false</code> else.
     */
    public static void setAccessible(final AccessibleObject object, final boolean accessible)
    {
        java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Void>()
        {
            @Override
            public Void run()
            {
                object.setAccessible(accessible);
                return null;
            }
        });
    }

    /**
     * Get all declared interfaces from object.
     * 
     * @param object The object reference.
     * @param base The minimum base interface.
     * @return The declared interfaces.
     */
    public static Collection<Class<?>> getInterfaces(Class<?> object, Class<?> base)
    {
        final Collection<Class<?>> interfaces = new ArrayList<Class<?>>();
        Class<?> current = object;
        while (current != null)
        {
            final Deque<Class<?>> currents = new ArrayDeque<Class<?>>(filterInterfaces(current, base));
            final Deque<Class<?>> nexts = new ArrayDeque<Class<?>>();
            while (!currents.isEmpty())
            {
                nexts.clear();
                interfaces.addAll(currents);
                checkInterfaces(base, currents, nexts);
                currents.clear();
                currents.addAll(nexts);
                nexts.clear();
            }
            current = current.getSuperclass();
        }
        return interfaces;
    }

    /**
     * Store all declared valid interfaces into next.
     * 
     * @param base The minimum base interface.
     * @param currents The current interfaces found.
     * @param nexts The next interface to check.
     */
    private static void checkInterfaces(Class<?> base, Deque<Class<?>> currents, Deque<Class<?>> nexts)
    {
        for (final Class<?> current : currents)
        {
            for (final Class<?> type : current.getInterfaces())
            {
                if (base.isAssignableFrom(type) && !type.equals(base))
                {
                    nexts.add(type);
                }
            }
        }
    }

    /**
     * Get all direct interfaces from object filtered by base interface (excluded).
     * 
     * @param object The object reference.
     * @param base The minimum base interface.
     * @return The declared interfaces.
     */
    private static Collection<Class<?>> filterInterfaces(Class<?> object, Class<?> base)
    {
        final Collection<Class<?>> interfaces = new ArrayList<Class<?>>();
        for (final Class<?> current : object.getInterfaces())
        {
            if (base.isAssignableFrom(current) && !current.equals(base))
            {
                interfaces.add(current);
            }
        }
        return interfaces;
    }

    /**
     * Get the object class.
     * 
     * @param object The object reference.
     * @return The object class (or object itself if already a class).
     */
    private static Class<?> getClass(Object object)
    {
        if (object instanceof Class)
        {
            return (Class<?>) object;
        }
        return object.getClass();
    }

    /**
     * Check if there is a compatible constructor for the types.
     * 
     * @param paramTypes The types as input.
     * @param constructorTypes The constructors to check.
     * @return <code>true</code> if at least one constructor is compatible, <code>false</code> else.
     */
    private static boolean hasCompatibleConstructor(Class<?>[] paramTypes, Class<?>[] constructorTypes)
    {
        for (int i = 0; i < paramTypes.length; i++)
        {
            if (!constructorTypes[i].isAssignableFrom(paramTypes[i]))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if there is a compatible constructor for the types.
     * 
     * @param paramTypes The types as input.
     * @param constructorTypes The constructors to check.
     * @return <code>true</code> if at least one constructor is compatible, <code>false</code> else.
     */
    private static boolean hasCompatibleConstructorParent(Class<?>[] paramTypes, Class<?>[] constructorTypes)
    {
        for (int i = 0; i < paramTypes.length; i++)
        {
            if (!paramTypes[i].isAssignableFrom(constructorTypes[i]))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Private constructor.
     */
    private UtilReflection()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
