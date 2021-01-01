/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNull;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Test {@link LionEngineException}.
 */
final class LionEngineExceptionTest
{
    /**
     * Test exception with a media as argument with <code>null</code> path.
     */
    @Test
    void testLionEngineExceptionWithMediaNullPath()
    {
        final LionEngineException exception = new LionEngineException(new MediaMock(), "message");

        assertEquals("[null] message", exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test exception with a media as argument.
     */
    @Test
    void testLionEngineExceptionWithMedia()
    {
        final LionEngineException exception = new LionEngineException(Medias.create("media"));

        assertEquals("[media] ", exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test exception with a media and message as argument.
     */
    @Test
    void testLionEngineExceptionWithMediaMessage()
    {
        final LionEngineException exception = new LionEngineException(Medias.create("media"), "message");

        assertEquals("[media] message", exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test exception with a media as argument.
     */
    @Test
    void testLionEngineExceptionWithMediaException()
    {
        final LionEngineException exception = new LionEngineException(new RuntimeException(), Medias.create("media"));

        assertEquals("[media] ", exception.getMessage());
        assertEquals(RuntimeException.class, exception.getCause().getClass());
    }

    /**
     * Test exception with a media as argument plus exception and message.
     */
    @Test
    void testLionEngineExceptionWithMediaExceptionMessage()
    {
        final LionEngineException exception = new LionEngineException(new RuntimeException(),
                                                                      Medias.create("media"),
                                                                      "message");

        assertEquals("[media] message", exception.getMessage());
        assertEquals(RuntimeException.class, exception.getCause().getClass());
    }

    /**
     * Test exception with a <code>null</code> enum as argument.
     */
    @Test
    void testLionEngineExceptionWithEnumNull()
    {
        final LionEngineException exception = new LionEngineException((Enum<?>) null);

        assertEquals(LionEngineException.ERROR_UNKNOWN_ENUM + LionEngineException.NULL_ENUM, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test exception with an enum as argument.
     */
    @Test
    void testLionEngineExceptionWithEnum()
    {
        final LionEngineException exception = new LionEngineException(Origin.MIDDLE);

        assertEquals(LionEngineException.ERROR_UNKNOWN_ENUM + Origin.MIDDLE, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test exception with a exception as argument.
     * 
     * @throws FileNotFoundException If error.
     */
    @Test
    void testLionEngineExceptionWithException() throws FileNotFoundException
    {
        final LionEngineException exception = new LionEngineException(new IOException("error"));

        assertEquals("java.io.IOException: error", exception.getMessage());
        assertEquals(IOException.class, exception.getCause().getClass());
    }

    /**
     * Test exception with a {@link LionEngineException} as argument.
     * 
     * @throws FileNotFoundException If error.
     */
    @Test
    void testLionEngineExceptionWithLionEngineException() throws FileNotFoundException
    {
        final LionEngineException exception = new LionEngineException(new LionEngineException("reason"));

        assertEquals("com.b3dgs.lionengine.LionEngineException: reason", exception.getMessage());
        assertEquals(LionEngineException.class, exception.getCause().getClass());
        assertNotEquals(exception, exception.getCause());
    }

    /**
     * Test exception with a runtime exception as argument.
     * 
     * @throws FileNotFoundException If error.
     */
    @Test
    void testLionEngineExceptionWithRuntimeException() throws FileNotFoundException
    {
        final LionEngineException exception = new LionEngineException(new LionEngineException(new RuntimeException("sub")));

        assertEquals("com.b3dgs.lionengine.LionEngineException: java.lang.RuntimeException: sub",
                     exception.getMessage());
        assertEquals(LionEngineException.class, exception.getCause().getClass());
        assertEquals(RuntimeException.class, exception.getCause().getCause().getClass());
        assertNotEquals(exception, exception.getCause());
    }

    /**
     * Test exception with a <code>null</code> reason.
     * 
     * @throws FileNotFoundException If error.
     */
    @Test
    void testLionEngineExceptionWithNullReason() throws FileNotFoundException
    {
        final LionEngineException exception = new LionEngineException((Throwable) null);

        assertEquals(null, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test exception with an exception as argument without message.
     */
    @Test
    void testLionEngineExceptionWithExceptionNoMessage()
    {
        final LionEngineException exception = new LionEngineException(new IOException());

        assertEquals("java.io.IOException", exception.getMessage());
        assertEquals(IOException.class, exception.getCause().getClass());
    }

    /**
     * Test exception with a exception as argument and message.
     */
    @Test
    void testLionEngineExceptionWithThrowableMessage()
    {
        final LionEngineException exception = new LionEngineException(new IOException(), "message");

        assertEquals("message", exception.getMessage());
        assertEquals(IOException.class, exception.getCause().getClass());
    }
}
