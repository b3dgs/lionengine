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
package com.b3dgs.lionengine.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test the exception.
 */
public class LionEngineExceptionTest
{
    /** Output result. */
    private static PrintStream stream;
    /** Output result. */
    private static PrintWriter writer;

    /**
     * Prepare the test.
     * 
     * @throws IOException If error.
     */
    @BeforeClass
    public static void prepareTest() throws IOException
    {
        final File file1 = File.createTempFile(LionEngineExceptionTest.class.getName(), ".log");
        stream = new PrintStream(file1);

        final File file2 = File.createTempFile(LionEngineExceptionTest.class.getName(), ".log");
        writer = new PrintWriter(file2);

        Verbose.info("Test results of ", LionEngineExceptionTest.class.getName(), " in ", file1.getAbsolutePath());
        Verbose.info("Test results of ", LionEngineExceptionTest.class.getName(), " in ", file2.getAbsolutePath());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        stream.flush();
        stream.close();
        writer.flush();
        writer.close();
    }

    /**
     * Test the exception.
     */
    @Test
    public void testLionEngineExceptionWithCheck()
    {
        stream.println("testLionEngineExceptionWithCheck");
        try
        {
            Check.notNull(null);
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace(stream);
        }
        stream.println();
    }

    /**
     * Test the exception with a message as argument.
     */
    @Test
    public void testLionEngineExceptionWithCheckMessage()
    {
        stream.println("testLionEngineExceptionWithCheckMessage");
        try
        {
            Check.notNull(null);
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals("Unexpected null argument !", exception.getMessage());
            exception.printStackTrace(stream);
        }
        stream.println();
    }

    /**
     * Test the exception with a media as argument with null path.
     */
    @Test
    public void testLionEngineExceptionWithMediaNullPath()
    {
        stream.println("testLionEngineExceptionWithMediaNullPath");
        final String message = "Exception test";
        try
        {
            throw new LionEngineException(Medias.create((String) null), message);
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace(stream);
        }
        stream.println();
    }

    /**
     * Test the exception with a media as argument.
     */
    @Test
    public void testLionEngineExceptionWithMedia()
    {
        stream.println("testLionEngineExceptionWithMedia");
        final String message = "Exception test";
        try
        {
            throw new LionEngineException(Medias.create("media"), message);
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace(stream);
        }
        stream.println();
    }

    /**
     * Test the exception with a throwable as argument.
     * 
     * @throws FileNotFoundException If error.
     */
    @Test
    public void testLionEngineExceptionWithThrowable() throws FileNotFoundException
    {
        stream.println("testLionEngineExceptionWithThrowable");
        try
        {
            throw new LionEngineException(new IOException("error"));
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace(stream);
        }
        try
        {
            throw new LionEngineException(new LionEngineException("reason"));
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace(stream);
        }
        stream.println();
        writer.println("testLionEngineExceptionWithThrowable");
        try
        {
            throw new LionEngineException(new LionEngineException(new RuntimeException("sub")));
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace(writer);
        }
        writer.println();
    }

    /**
     * Test the exception with a <code>null</code> reason.
     * 
     * @throws FileNotFoundException If error.
     */
    @Test
    public void testLionEngineExceptionWithNullReason() throws FileNotFoundException
    {
        writer.println("testLionEngineExceptionWithNullReason");
        try
        {
            throw new LionEngineException((Throwable) null);
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace(writer);
        }
        writer.println();
    }

    /**
     * Test the exception with a throwable as argument without message.
     */
    @Test
    public void testLionEngineExceptionWithThrowableNoMessage()
    {
        stream.println("testLionEngineExceptionWithThrowableNoMessage");
        LionEngineException.setIgnoreEngineTrace(true);
        try
        {
            throw new LionEngineException(new IOException());
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace(stream);
        }
        LionEngineException.setIgnoreEngineTrace(false);
        stream.println();
    }
}
