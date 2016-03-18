/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.Closeable;
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

import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.mock.InputStreamMock;
import com.b3dgs.lionengine.mock.OutputStreamMock;
import com.b3dgs.lionengine.util.UtilTests;

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
    @Rule
    public final TemporaryFolder TEMP = new TemporaryFolder();

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

        InputStream input = null;
        OutputStream output = null;
        try
        {
            input = new FileInputStream(temp1);
            output = new FileOutputStream(temp2);
            output.write(1);
            output.flush();
            UtilStream.copy(input, output);
        }
        finally
        {
            UtilStream.close(input);
            UtilStream.close(output);
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
        OutputStream output = null;
        try
        {
            output = new OutputStreamMock();
            UtilStream.copy(null, output);
        }
        finally
        {
            UtilStream.close(output);
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
        InputStream input = null;
        try
        {
            input = new InputStreamMock();
            UtilStream.copy(input, null);
        }
        finally
        {
            UtilStream.close(input);
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
        InputStream input = null;
        try
        {
            input = new InputStreamMock();
            Assert.assertTrue(UtilStream.getCopy("te", input).delete());
            Assert.assertTrue(UtilStream.getCopy("temp", input).delete());
            Assert.assertTrue(UtilStream.getCopy("temp.tmp", input).delete());
        }
        finally
        {
            UtilStream.close(input);
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
        InputStream input = null;
        try
        {
            input = new InputStreamMock();
            Assert.assertNull(UtilStream.getCopy(null, input));
        }
        finally
        {
            UtilStream.close(input);
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
     * Test the safe close error.
     */
    @Test
    public void testSafeCloseError()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        UtilStream.safeClose(new Closeable()
        {
            @Override
            public void close() throws IOException
            {
                throw new IOException();
            }
        });
        Verbose.info("****************************************************************************************");
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
