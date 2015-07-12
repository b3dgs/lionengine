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
import java.lang.reflect.Method;

import org.junit.Assert;

/**
 * Set of utility function related to unit test.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilTests
{
    /** Double precision. */
    public static final double PRECISION = 0.000000001;

    /**
     * Test private constructor.
     * 
     * @param clazz The class to test.
     * @param args Optional arguments.
     * @throws Throwable If error.
     */
    public static void testPrivateConstructor(Class<?> clazz, Object... args) throws Throwable
    {
        final Constructor<?> constructor = clazz.getDeclaredConstructor(UtilReflection.getParamTypes(args));
        try
        {
            constructor.setAccessible(true);
            final Object instance = constructor.newInstance(args);
            Assert.assertNotNull(instance);
        }
        catch (final InvocationTargetException exception)
        {
            throw exception.getCause();
        }
        finally
        {
            constructor.setAccessible(false);
        }
        Assert.fail();
    }

    /**
     * Test all enum values.
     * 
     * @param type The enum type.
     * @throws ReflectiveOperationException If error.
     */
    public static <T extends Enum<T>> void testEnum(Class<T> type) throws ReflectiveOperationException
    {
        final Method method = type.getDeclaredMethod("valueOf", String.class);
        for (final T value : type.getEnumConstants())
        {
            Assert.assertNotNull(value);
            Assert.assertEquals(value, method.invoke(type, value.name()));
        }
    }

    /**
     * Private constructor.
     */
    private UtilTests()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
