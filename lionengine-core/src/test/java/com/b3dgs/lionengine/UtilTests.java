/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;

/**
 * Set of utility function related to unit test.
 */
public final class UtilTests
{
    /** Double precision. */
    public static final double PRECISION = 0.000000001;
    /** Standard resolution. */
    public static final Resolution RESOLUTION_320_240 = new Resolution(320, 240, 60);
    /** Standard resolution. */
    public static final Resolution RESOLUTION_640_480 = new Resolution(640, 480, 60);
    /** Enum.valueOf method. */
    private static final String ENUM_VALUEOF = "valueOf";

    /**
     * Test private constructor.
     * 
     * @param clazz The class to test.
     * @param args Optional arguments.
     * @throws LionEngineException Expected exception.
     * @throws Exception If error.
     */
    public static void testPrivateConstructor(Class<?> clazz, Object... args) throws Exception
    {
        final Class<?>[] params = UtilReflection.getParamTypes(args);
        final Constructor<?> constructor = clazz.getDeclaredConstructor(params);
        final boolean accessible = constructor.isAccessible();
        try
        {
            UtilReflection.setAccessible(constructor, true);
            Assertions.assertNull(constructor.newInstance(args));
        }
        catch (final InvocationTargetException exception)
        {
            final Throwable cause = exception.getCause();
            if (cause instanceof LionEngineException)
            {
                throw (LionEngineException) cause;
            }
            throw exception;
        }
        finally
        {
            if (constructor.isAccessible() != accessible)
            {
                UtilReflection.setAccessible(constructor, accessible);
            }
        }
        Assertions.fail("Constructor is not private !");
    }

    /**
     * Test object functions call.
     * 
     * @param signature The object functions to call.
     * @param object The object to call with.
     */
    public static void testCalls(Class<?> signature, Object object)
    {
        for (final Method method : signature.getMethods())
        {
            try
            {
                final List<Object> arguments = new ArrayList<>();
                for (final Class<?> type : method.getParameterTypes())
                {
                    if (type.isPrimitive())
                    {
                        arguments.add(getPrimitive(type));
                    }
                    else
                    {
                        arguments.add(null);
                    }
                }
                method.invoke(object, arguments.toArray());
            }
            catch (final InvocationTargetException | IllegalAccessException exception)
            {
                Assertions.fail(exception);
            }
        }
    }

    /**
     * Get primitive instance.
     * 
     * @param type The primitive type.
     * @return The primitive instance;
     */
    private static Object getPrimitive(Class<?> type)
    {
        if (type == Integer.TYPE)
        {
            return Integer.valueOf(0);
        }
        else if (type == Double.TYPE)
        {
            return Double.valueOf(0.0);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Test all enum values.
     * 
     * @param <T> The enum type.
     * @param type The enum type.
     * @throws Exception If error.
     */
    public static <T extends Enum<T>> void testEnum(Class<T> type) throws Exception
    {
        final Method method = type.getDeclaredMethod(ENUM_VALUEOF, String.class);
        UtilReflection.setAccessible(method, true);
        for (final T value : type.getEnumConstants())
        {
            Assertions.assertNotNull(value);
            Assertions.assertEquals(value, method.invoke(type, value.name()));
        }
    }

    /**
     * Pause time with accurate precision.
     * 
     * @param milli Time to wait in millisecond.
     */
    public static void pause(long milli)
    {
        final double old = System.nanoTime() / Constant.NANO_TO_MILLI;
        while (System.nanoTime() / Constant.NANO_TO_MILLI - old < milli)
        {
            Thread.yield();
        }
    }

    /**
     * Get nano to milli.
     * 
     * @param oldNano The old nano time.
     * @param nano The current nano time.
     * @return The elapsed milli time.
     */
    public static double getElapsedMilli(long oldNano, long nano)
    {
        return (nano - oldNano) / Constant.NANO_TO_MILLI;
    }

    /**
     * Private constructor.
     */
    private UtilTests()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
