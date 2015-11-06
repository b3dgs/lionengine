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
package com.b3dgs.lionengine.test.stream;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.test.util.UtilTests;

/**
 * Test the stream package.
 */
public class StreamTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void prepare()
    {
        Medias.setLoadFromJar(StreamTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

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
    }

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
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

        FileReading reading = null;
        try
        {
            reading = Stream.createFileReading(Medias.create("malformed.xml"));
            Assert.assertNotNull(reading);
        }
        finally
        {
            UtilFile.close(reading);
        }

        final File file = File.createTempFile("test", null);
        file.deleteOnExit();
        final Media media = Medias.create(file.getAbsolutePath());

        FileWriting writing = null;
        try
        {
            writing = Stream.createFileWriting(media);
            Assert.assertNotNull(writing);
        }
        finally
        {
            UtilFile.close(writing);
        }
    }
}
