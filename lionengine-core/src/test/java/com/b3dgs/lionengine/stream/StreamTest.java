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
package com.b3dgs.lionengine.stream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.mock.MediaMock;

/**
 * Test the stream package.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class StreamTest
{
    /**
     * Test the failure cases.
     * 
     * @throws IOException If error.
     */
    private static void testFailures() throws IOException
    {
        try
        {
            Stream.createFileWriting(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            Stream.createFileReading(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            Stream.createXmlNode(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the constructor.
     * 
     * @throws Throwable If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Throwable
    {
        UtilTests.testPrivateConstructor(Stream.class);
    }

    /**
     * Test the file factory.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testFactory() throws IOException
    {
        testFailures();
        Assert.assertNotNull(Stream.createXmlNode("test"));
        try (FileReading reading = Stream.createFileReading(new MediaMock("malformed.xml")))
        {
            Assert.assertNotNull(reading);
        }
        final File file = Files.createTempFile("test", null).toFile();
        file.deleteOnExit();
        final Media media = new MediaMock(file.getAbsolutePath(), true);
        try (FileWriting writing = Stream.createFileWriting(media))
        {
            Assert.assertNotNull(writing);
        }
    }
}
