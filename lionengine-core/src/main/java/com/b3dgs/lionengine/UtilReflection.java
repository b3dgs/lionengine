/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Utility class related to java reflection.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilReflection
{
    /** Constructor error. */
    static final String ERROR_CONSTRUCTOR = "Unable to create the following type: ";
    /** Constructor compatibility error. */
    static final String ERROR_NO_CONSTRUCTOR_COMPATIBLE = "No compatible constructor found for ";
    /** Constructor compatibility error. */
    static final String ERROR_WITH = " with: ";
    /** Field error. */
    static final String ERROR_FIELD = "Unable to access to the following field: ";
    /** Method error. */
    static final String ERROR_METHOD = "Unable to access to the following method: ";

    /**
     * Create a class instance with its parameters.
     * 
     * @param <T> The element type used.
     * @param type The class type to instantiate (must not be <code>null</code>).
     * @param paramTypes The class base type for each parameter (must not be <code>null</code>).
     * @param params The constructor parameters (must not be <code>null</code>).
     * @return The class instance.
     * @throws NoSuchMethodException If no constructor found.
     * @throws LionEngineException If invalid parameters or unable to create the instance.
     */
    public static <T> T create(Class<T> type, Class<?>[] paramTypes, Object... params) throws NoSuchMethodException
    {
        Check.notNull(type);
        Check.notNull(paramTypes);
        Check.notNull(params);

        final Constructor<T> constructor = getCompatibleConstructor(type, paramTypes);
        return create(type, constructor, params);
    }

    /**
     * Create a class instance with its parameters. Use a compatible constructor with the following parameters, reducing
     * parameter types array as a queue until empty in order to find a constructor.
     * 
     * @param <T> The element type used.
     * @param type The class type (must not be <code>null</code>).
     * @param params The maximum parameters in sequential order (must not be <code>null</code>).
     * @return The constructor found.
     * @throws NoSuchMethodException If no constructor found.
     * @throws LionEngineException If invalid parameters.
     */
    @SuppressWarnings("unchecked")
    public static <T> T createReduce(Class<T> type, Object... params) throws NoSuchMethodException
    {
        Check.notNull(type);
        Check.notNull(params);

        final Class<?>[] paramTypes = getParamTypes(params);
        final Queue<Class<?>> typesQueue = new ArrayDeque<>(Arrays.asList(paramTypes));
        final Queue<Object> paramsQueue = new ArrayDeque<>(Arrays.asList(params));
        boolean stop = false;
        while (!stop)
        {
            final int typesLength = typesQueue.size();
            final Class<?>[] typesArray = typesQueue.toArray(new Class<?>[typesLength]);
            for (final Constructor<?> constructor : type.getConstructors())
            {
                final Class<?>[] constructorTypes = constructor.getParameterTypes();
                if (constructorTypes.length == typesLength
                    && (typesLength == 0 || hasCompatibleConstructor(typesArray, constructorTypes)))
                {
                    return create(type, (Constructor<T>) constructor, paramsQueue.toArray());
                }
            }

            stop = paramsQueue.isEmpty();
            typesQueue.poll();
            paramsQueue.poll();
        }
        throw new NoSuchMethodException(ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                        + type.getName()
                                        + ERROR_WITH
                                        + Arrays.asList(paramTypes));
    }

    /**
     * Get the parameter types as array.
     * 
     * @param arguments The arguments list (must not be <code>null</code>).
     * @return The arguments type array.
     * @throws LionEngineException If invalid parameters.
     */
    public static Class<?>[] getParamTypes(Object... arguments)
    {
        Check.notNull(arguments);

        final Collection<Object> types = new ArrayList<>(arguments.length);
        for (final Object argument : arguments)
        {
            if (argument.getClass() == Class.class)
            {
                types.add(argument);
            }
            else
            {
                types.add(argument.getClass());
            }
        }
        final Class<?>[] typesArray = new Class<?>[types.size()];
        return types.toArray(typesArray);
    }

    /**
     * Get a compatible constructor with the following parameters.
     * 
     * @param <T> The element type used.
     * @param type The class type (must not be <code>null</code>).
     * @param paramTypes The parameters types (must not be <code>null</code>).
     * @return The constructor found.
     * @throws NoSuchMethodException If no constructor found.
     * @throws LionEngineException If invalid parameters.
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getCompatibleConstructor(Class<T> type, Class<?>... paramTypes)
            throws NoSuchMethodException
    {
        Check.notNull(type);
        Check.notNull(paramTypes);

        for (final Constructor<?> current : type.getConstructors())
        {
            final Class<?>[] constructorTypes = current.getParameterTypes();
            if (constructorTypes.length == paramTypes.length
                && (paramTypes.length == 0 || hasCompatibleConstructor(paramTypes, constructorTypes)))
            {
                return (Constructor<T>) current;
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
     * @param <T> The element type.
     * @param type The class type (must not be <code>null</code>).
     * @param paramTypes The parameters types (must not be <code>null</code>).
     * @return The constructor found.
     * @throws NoSuchMethodException If no constructor found.
     * @throws LionEngineException If invalid parameters.
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getCompatibleConstructorParent(Class<T> type, Class<?>[] paramTypes)
            throws NoSuchMethodException
    {
        Check.notNull(type);
        Check.notNull(paramTypes);

        for (final Constructor<?> current : type.getConstructors())
        {
            final Class<?>[] constructorTypes = current.getParameterTypes();
            if (constructorTypes.length == paramTypes.length
                && hasCompatibleConstructorParent(paramTypes, constructorTypes))
            {
                return (Constructor<T>) current;
            }
        }
        throw new NoSuchMethodException(ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                        + type.getName()
                                        + ERROR_WITH
                                        + Arrays.asList(paramTypes));
    }

    /**
     * Get all declared interfaces from object.
     * 
     * @param object The object reference (must not be <code>null</code>).
     * @param base The minimum base interface (must not be <code>null</code>).
     * @return The declared interfaces.
     * @throws LionEngineException If invalid parameters.
     */
    public static Collection<Class<?>> getInterfaces(Class<?> object, Class<?> base)
    {
        Check.notNull(object);
        Check.notNull(base);

        final Collection<Class<?>> interfaces = new ArrayList<>();
        Class<?> current = object;
        while (current != null)
        {
            final Deque<Class<?>> currents = new ArrayDeque<>(filterInterfaces(current, base));
            final Deque<Class<?>> nexts = new ArrayDeque<>();
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
     * Create a class instance with its parameters.
     * 
     * @param <T> The element type used.
     * @param type The class type to instantiate (must not be <code>null</code>).
     * @param constructor The constructor to use (must not be <code>null</code>).
     * @param params The constructor parameters (must not be <code>null</code>).
     * @return The class instance.
     * @throws LionEngineException If invalid parameters or unable to create the instance.
     */
    private static <T> T create(Class<T> type, Constructor<T> constructor, Object... params)
    {
        Check.notNull(type);
        Check.notNull(constructor);
        Check.notNull(params);

        try
        {
            return constructor.newInstance(params);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception,
                                          ERROR_CONSTRUCTOR
                                                     + type
                                                     + " "
                                                     + Arrays.asList(constructor.getParameterTypes())
                                                     + ERROR_WITH
                                                     + Arrays.asList(params));
        }
        catch (final InstantiationException | IllegalAccessException | InvocationTargetException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR + type);
        }
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
        currents.stream()
                .map(Class::getInterfaces)
                .forEach(types -> Arrays.asList(types)
                                        .stream()
                                        .filter(type -> base.isAssignableFrom(type) && !type.equals(base))
                                        .forEach(nexts::add));
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
        return Arrays.asList(object.getInterfaces())
                     .stream()
                     .filter(current -> base.isAssignableFrom(current) && !current.equals(base))
                     .collect(Collectors.toList());
    }

    /**
     * Get the object class.
     * 
     * @param object The object reference.
     * @return The object class (or object itself if already a class).
     */
    static Class<?> getClass(Object object)
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
            if (constructorTypes[i].isAssignableFrom(paramTypes[i]))
            {
                return true;
            }
        }
        return false;
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
