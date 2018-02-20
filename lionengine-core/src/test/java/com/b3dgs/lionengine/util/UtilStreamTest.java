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
package com.b3dgs.lionengine.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.io.InputStreamMock;
import com.b3dgs.lionengine.io.OutputStreamMock;

/**
 * Test the utility stream class.
 */
public class UtilStreamTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(UtilStreamTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /** Temp folder. */
    @Rule public final TemporaryFolder TEMP = new TemporaryFolder();

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilStream.class);
    }

    /**
     * Test the stream copy.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testCopy() throws IOException
    {
        final File temp1 = File.createTempFile("temp", ".tmp");
        final File temp2 = File.createTempFile("temp", ".tmp");

        try (InputStream input = new FileInputStream(temp1);
             OutputStream output = new FileOutputStream(temp2))
        {
            output.write(1);
            output.flush();
            UtilStream.copy(input, output);
        }
        finally
        {
            Assert.assertTrue(temp1.delete());
            Assert.assertTrue(temp2.delete());
        }
    }

    /**
     * Test the stream copy with null input.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testCopyNullInput() throws IOException
    {
        try (OutputStream output = new OutputStreamMock())
        {
            UtilStream.copy(null, output);
        }
    }

    /**
     * Test the stream copy with null output.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testCopyNullOutput() throws IOException
    {
        try (InputStream input = new InputStreamMock())
        {
            UtilStream.copy(input, null);
        }
    }

    /**
     * Test the input stream copy.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetCopy() throws IOException
    {
        try (InputStream input = new InputStreamMock())
        {
            Assert.assertTrue(UtilStream.getCopy("te", input).delete());
            Assert.assertTrue(UtilStream.getCopy("temp", input).delete());
            Assert.assertTrue(UtilStream.getCopy("temp.tmp", input).delete());
        }
    }

    /**
     * Test the input stream copy with null name.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testGetCopyNullName() throws IOException
    {
        try (InputStream input = new InputStreamMock())
        {
            Assert.assertNull(UtilStream.getCopy(null, input));
        }
    }

    /**
     * Test the input stream copy with null name.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testGetCopyNullStream() throws IOException
    {
        Assert.assertNull(UtilStream.getCopy("temp", null));
    }

    /**
     * Test the get copy error.
     */
    @Test(expected = LionEngineException.class)
    public void testGetCopyError()
    {
        UtilStream.getCopy("copy", new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                throw new IOException();
            }
        });
    }
}
