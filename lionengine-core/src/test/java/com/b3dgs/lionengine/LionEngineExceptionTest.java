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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.mock.MediaMock;

/**
 * Test the exception.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class LionEngineExceptionTest
{
    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test the exception.
     */
    @Test
    public void testLionEngineExceptionWithCheck()
    {
        try
        {
            Check.notNull(null);
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Test the exception with a message as argument.
     */
    @Test
    public void testLionEngineExceptionWithCheckMessage()
    {
        try
        {
            Check.notNull(null);
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals("Unexpected null argument !", exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Test the exception with a media as argument with null path.
     */
    @Test
    public void testLionEngineExceptionWithMediaNullPath()
    {
        final String message = "Exception test";
        try
        {
            throw new LionEngineException(new MediaMock(null, true), message);
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Test the exception with a media as argument.
     */
    @Test
    public void testLionEngineExceptionWithMedia()
    {
        final String message = "Exception test";
        try
        {
            throw new LionEngineException(new MediaMock("media", true), message);
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Test the exception with a throwable as argument.
     * 
     * @throws FileNotFoundException If error.
     */
    @Test
    public void testLionEngineExceptionWithThrowable() throws FileNotFoundException
    {
        try
        {
            throw new LionEngineException(new IOException("error"));
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace();
        }
        try
        {
            throw new LionEngineException(new LionEngineException("reason"));
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace();
        }
        final File out = new File("out.txt");
        try (PrintWriter writer = new PrintWriter(out))
        {
            try
            {
                throw new LionEngineException(new LionEngineException(new RuntimeException("sub")));
            }
            catch (final LionEngineException exception)
            {
                exception.printStackTrace(writer);
            }
        }
        if (out.isFile())
        {
            UtilFile.deleteFile(out);
        }
    }

    /**
     * Test the exception with a <code>null</code> reason.
     * 
     * @throws FileNotFoundException If error.
     */
    @Test
    public void testLionEngineExceptionWithNullReason() throws FileNotFoundException
    {
        final File out = new File("out.txt");
        try (PrintWriter writer = new PrintWriter(out))
        {
            try
            {
                throw new LionEngineException((Throwable) null);
            }
            catch (final LionEngineException exception)
            {
                exception.printStackTrace(writer);
            }
        }
        if (out.isFile())
        {
            UtilFile.deleteFile(out);
        }
    }

    /**
     * Test the exception with a throwable as argument without message.
     */
    @Test
    public void testLionEngineExceptionWithThrowableNoMessage()
    {
        LionEngineException.setIgnoreEngineTrace(true);
        try
        {
            throw new LionEngineException(new IOException());
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace();
        }
        LionEngineException.setIgnoreEngineTrace(false);
    }
}
