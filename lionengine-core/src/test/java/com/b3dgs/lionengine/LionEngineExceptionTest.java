/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.FactoryMediaProvider;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

/**
 * Test the exception.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LionEngineExceptionTest
{
    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());
        Verbose.info("*** TEST EXPECTED EXCEPTION ***");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Verbose.info("*************************");
        FactoryMediaProvider.setFactoryMedia(null);
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
        final String message = "Exception test";
        try
        {
            Check.notNull(null, message);
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(message, exception.getMessage());
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
            throw new LionEngineException(Core.MEDIA.create("media"), message);
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Test the exception with a throwable as argument.
     */
    @Test
    public void testLionEngineExceptionWithThrowable()
    {
        try
        {
            throw new LionEngineException(new IOException("error"));
        }
        catch (final LionEngineException exception)
        {
            exception.printStackTrace();
        }
    }
}
