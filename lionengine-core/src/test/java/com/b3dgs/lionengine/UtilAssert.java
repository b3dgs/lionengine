/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

/**
 * Set of utility function related to unit test assertions.
 */
public final class UtilAssert
{
    /**
     * Asserts that class constructor is private.
     * 
     * @param clazz The class to test.
     */
    public static void assertPrivateConstructor(Class<?> clazz)
    {
        Assertions.assertThrows(LionEngineException.class, () -> UtilTests.testPrivateConstructor(clazz));
    }

    /**
     * Asserts that {@code executable} throws a {@link LionEngineException}.
     * 
     * @param executable The executable to test.
     * @param expected The expected exception message.
     */
    public static void assertThrows(Executable executable, String expected)
    {
        Assertions.assertEquals(expected, Assertions.assertThrows(LionEngineException.class, executable).getMessage());
    }

    /**
     * Asserts that {@code condition} is <code>true</code>.
     * 
     * @param condition The excepted condition.
     */
    public static void assertTrue(boolean condition)
    {
        Assertions.assertTrue(condition);
    }

    /**
     * Asserts that {@code condition} is <code>false</code>.
     * 
     * @param condition The excepted condition.
     */
    public static void assertFalse(boolean condition)
    {
        Assertions.assertFalse(condition);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are equal.
     * 
     * @param expected The excepted object.
     * @param actual The actual object.
     */
    public static void assertEquals(boolean expected, boolean actual)
    {
        Assertions.assertEquals(Boolean.valueOf(expected), Boolean.valueOf(actual));
    }

    /**
     * Asserts that {@code expected} and {@code actual} are equal using {@link UtilTests#PRECISION} as delta.
     * 
     * @param expected The excepted object.
     * @param actual The actual object.
     */
    public static void assertEquals(double expected, double actual)
    {
        Assertions.assertEquals(expected, actual, UtilTests.PRECISION);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are equal. If both are {@code null}, they are considered equal.
     * 
     * @param expected The excepted object.
     * @param actual The actual object.
     */
    public static void assertEquals(Object expected, Object actual)
    {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are hash code equal.
     * 
     * @param expected The excepted object hash code.
     * @param actual The actual object hash code.
     */
    public static void assertHashEquals(Object expected, Object actual)
    {
        Assertions.assertNotNull(expected);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.hashCode(), actual.hashCode());
    }

    /**
     * Asserts that {@code expected} and {@code actual} are hash code not equals.
     * 
     * @param expected The excepted object hash code.
     * @param actual The actual object hash code.
     */
    public static void assertHashNotEquals(Object expected, Object actual)
    {
        Assertions.assertNotNull(expected);
        Assertions.assertNotNull(actual);
        assertNotEquals(expected.hashCode(), actual.hashCode());
    }

    /**
     * Asserts that {@code expected} and {@code actual} are not equal. Fails if both are <code>null</code>.
     * 
     * @param expected The excepted object.
     * @param actual The actual object.
     */
    public static void assertNotEquals(Object expected, Object actual)
    {
        Assertions.assertNotEquals(expected, actual);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are not equal. Fails if both are <code>null</code>.
     * 
     * @param expected The excepted object.
     * @param actual The actual object.
     */
    public static void assertNotEquals(int expected, int actual)
    {
        Assertions.assertNotEquals(Integer.valueOf(expected), Integer.valueOf(actual));
    }

    /**
     * Private constructor.
     */
    private UtilAssert()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
