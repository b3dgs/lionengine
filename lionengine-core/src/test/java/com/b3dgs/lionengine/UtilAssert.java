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

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.opentest4j.AssertionFailedError;

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
     * Asserts that {@code executable} throws a {@link LionEngineException} with a specific <code>cause</code>.
     * 
     * @param from The exception type source.
     * @param executable The executable to test.
     * @param cause The expected exception cause.
     */
    public static void assertCause(Class<? extends Throwable> from, Executable executable, Class<?> cause)
    {
        Assertions.assertEquals(cause, Assertions.assertThrows(from, executable).getCause().getClass());
    }

    /**
     * Asserts that {@code executable} throws a {@link LionEngineException} with a specific <code>cause</code>.
     * 
     * @param executable The executable to test.
     * @param cause The expected exception cause.
     */
    public static void assertCause(Executable executable, Class<?> cause)
    {
        Assertions.assertEquals(cause,
                                Assertions.assertThrows(LionEngineException.class, executable).getCause().getClass());
    }

    /**
     * Asserts that {@code executable} throws a {@link LionEngineException} with a specific <code>cause</code>.
     * 
     * @param executable The executable to test.
     * @param expected The expected exception message.
     */
    public static void assertCause(Executable executable, String expected)
    {
        Assertions.assertEquals(expected,
                                Assertions.assertThrows(LionEngineException.class, executable).getCause().getMessage());
    }

    /**
     * Asserts that {@code executable} throws a {@link LionEngineException}.
     * 
     * @param executable The executable to test.
     * @param prefix The expected exception message prefix.
     */
    public static void assertThrowsPrefix(Executable executable, String prefix)
    {
        Assertions.assertTrue(Assertions.assertThrows(LionEngineException.class, executable)
                                        .getMessage()
                                        .startsWith(prefix));
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
     * Asserts that {@code executable} throws a {@link LionEngineException}.
     * 
     * @param type The exception type.
     * @param executable The executable to test.
     * @param expected The expected exception message.
     */
    public static void assertThrows(Class<? extends Throwable> type, Executable executable, String expected)
    {
        Assertions.assertEquals(expected, Assertions.assertThrows(type, executable).getMessage());
    }

    /**
     * Asserts that {@code executable} throws a {@link NullPointerException}.
     * 
     * @param executable The executable to test.
     */
    public static void assertThrowsNpe(Executable executable)
    {
        Assertions.assertThrows(NullPointerException.class, executable);
    }

    /**
     * Asserts that {@code executable} throws a {@link IOException}.
     * 
     * @param executable The executable to test.
     * @param startWith The start exception message.
     */
    public static void assertThrowsIo(Executable executable, String startWith)
    {
        final String message = Assertions.assertThrows(IOException.class, executable).getMessage();
        Assertions.assertTrue(message.startsWith(startWith));
    }

    /**
     * Asserts that execution of the supplied {@code executable} completes before the given {@code timeout} is exceeded.
     * 
     * @param timeout The timeout in milliseconds.
     * @param executable The executable to test.
     */
    public static void assertTimeout(long timeout, Executable executable)
    {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(timeout), executable);
    }

    /**
     * Asserts that execution of the supplied {@code executable} throws a {@link LionEngineException} and completes
     * before the given {@code timeout} is exceeded.
     * 
     * @param timeout The timeout in milliseconds.
     * @param executable The executable to test.
     * @param expected The expected exception message.
     */
    public static void assertThrowsTimeout(long timeout, Executable executable, String expected)
    {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(timeout), () -> assertThrows(executable, expected));
    }

    /**
     * Asserts that {@code object} is <code>null</code>.
     * 
     * @param object The object to test.
     */
    public static void assertNull(Object object)
    {
        Assertions.assertNull(object);
    }

    /**
     * Asserts that {@code object} is not <code>null</code>.
     * 
     * @param object The object to test.
     */
    public static void assertNotNull(Object object)
    {
        Assertions.assertNotNull(object);
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
     * Asserts that {@code condition} is <code>true</code>.
     * 
     * @param condition The excepted condition.
     * @param message The failure message.
     */
    public static void assertTrue(boolean condition, String message)
    {
        Assertions.assertTrue(condition, message);
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
     * Asserts that {@code condition} is <code>false</code>.
     * 
     * @param condition The excepted condition.
     * @param message The failure message.
     */
    public static void assertFalse(boolean condition, String message)
    {
        Assertions.assertFalse(condition, message);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are equal.
     * 
     * @param expected The excepted boolean.
     * @param actual The actual boolean.
     */
    public static void assertEquals(boolean expected, boolean actual)
    {
        Assertions.assertEquals(Boolean.valueOf(expected), Boolean.valueOf(actual));
    }

    /**
     * Asserts that {@code expected} and {@code actual} are equal.
     * 
     * @param expected The excepted integer.
     * @param actual The actual integer.
     */
    public static void assertEquals(int expected, int actual)
    {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are equal.
     * 
     * @param expected The excepted float.
     * @param actual The actual float.
     */
    public static void assertEquals(long expected, long actual)
    {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are equal.
     * 
     * @param expected The excepted float.
     * @param actual The actual float.
     */
    public static void assertEquals(float expected, float actual)
    {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are equal using {@link UtilTests#PRECISION} as delta.
     * 
     * @param expected The excepted double.
     * @param actual The actual double.
     */
    public static void assertEquals(double expected, double actual)
    {
        Assertions.assertEquals(expected, actual, UtilTests.PRECISION);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are equal.
     * 
     * @param expected The excepted long.
     * @param actual The actual long.
     * @param message The failure message.
     */
    public static void assertEquals(long expected, long actual, String message)
    {
        Assertions.assertEquals(expected, actual, message);
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
     * Asserts that {@code expected} and {@code actual} are equal. If both are {@code null}, they are considered equal.
     * 
     * @param expected The excepted array.
     * @param actual The actual array.
     */
    public static void assertEquals(int[] expected, int[] actual)
    {
        Assertions.assertArrayEquals(expected, actual);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are equal. If both are {@code null}, they are considered equal.
     * 
     * @param expected The excepted iterable.
     * @param actual The actual iterable.
     */
    public static void assertIterableEquals(Iterable<?> expected, Iterable<?> actual)
    {
        Assertions.assertIterableEquals(expected, actual);
    }

    /**
     * Asserts that {@code expected} and {@code actual} are not equal. Fails if both are <code>null</code>.
     * 
     * @param expected The excepted integer.
     * @param actual The actual integer.
     */
    public static void assertNotEquals(int expected, int actual)
    {
        if (expected == actual)
        {
            throw new AssertionFailedError("expected: not equal but was: <" + actual + ">");
        }
    }

    /**
     * Asserts that {@code expected} and {@code actual} are not equal. Fails if both are <code>null</code>.
     * 
     * @param expected The excepted object.
     * @param actual The actual object.
     */
    public static void assertNotEquals(Object expected, Object actual)
    {
        if (expected == null && actual == null || expected != null && expected.equals(actual))
        {
            throw new AssertionFailedError("expected: not equal but was: <" + actual + ">");
        }
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
     * Asserts that {@code expected} and {@code actual} boolean arrays are equal. If both are <code>null</code>, they
     * are considered equal.
     * 
     * @param expected The excepted boolean array.
     * @param actual The actual boolean array.
     */
    public static void assertArrayEquals(boolean[] expected, boolean[] actual)
    {
        Assertions.assertArrayEquals(expected, actual);
    }

    /**
     * Asserts that {@code expected} and {@code actual} int arrays are equal. If both are <code>null</code>, they
     * are considered equal.
     * 
     * @param expected The excepted int array.
     * @param actual The actual int array.
     */
    public static void assertArrayEquals(int[] expected, int[] actual)
    {
        Assertions.assertArrayEquals(expected, actual);
    }

    /**
     * Asserts that {@code expected} and {@code actual} float arrays are equal. If both are <code>null</code>, they
     * are considered equal.
     * 
     * @param expected The excepted int array.
     * @param actual The actual int array.
     */
    public static void assertArrayEquals(float[] expected, float[] actual)
    {
        Assertions.assertArrayEquals(expected, actual);
    }

    /**
     * Asserts that {@code expected} and {@code actual} objects arrays are equal. If both are <code>null</code>, they
     * are considered equal.
     * 
     * @param expected The excepted objects array.
     * @param actual The actual objects array.
     */
    public static void assertArrayEquals(Object[] expected, Object[] actual)
    {
        Assertions.assertArrayEquals(expected, actual);
    }

    /**
     * Private constructor.
     */
    private UtilAssert()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
