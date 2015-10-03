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
package com.b3dgs.lionengine.core.swt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the utility media class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityMediaTest
{
    /**
     * Prepare test.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @BeforeClass
    public static void setUp() throws ReflectiveOperationException
    {
        Medias.setFactoryMedia(new FactoryMediaSwt());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setFactoryMedia(null);
    }

    /**
     * Test the constructor.
     * 
     * @throws Throwable If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Throwable
    {
        UtilTests.testPrivateConstructor(UtilityMedia.class);
    }

    /**
     * Test the utility media.
     */
    @Test
    public void testUtility()
    {
        UtilityMedia.setLoadFromJar(UtilityMediaTest.class);

        UtilityMedia.setResourcesDirectory(null);
        Assert.assertEquals(Constant.EMPTY_STRING, UtilityMedia.getRessourcesDir());

        UtilityMedia.setResourcesDirectory("test");
        Assert.assertEquals("test/", UtilityMedia.getRessourcesDir());

        UtilityMedia.setLoadFromJar(null);
    }

    /**
     * Test the input stream.
     * 
     * @throws IOException If error.
     * @throws LionEngineException If error.
     */
    @Test
    public void testInputStream() throws LionEngineException, IOException
    {
        UtilityMedia.setResourcesDirectory(Constant.EMPTY_STRING);
        UtilityMedia.setLoadFromJar(UtilityMediaTest.class);

        final File file = new File("raster.xml");
        final Media media = UtilityMedia.get(file);
        try (InputStream input = UtilityMedia.getInputStream(media))
        {
            Assert.assertNotNull(input);
        }
    }

    /**
     * Test the input stream.
     * 
     * @throws IOException If error.
     * @throws LionEngineException If error.
     */
    @Test
    public void testInputStream2() throws LionEngineException, IOException
    {
        UtilityMedia.setLoadFromJar(null);
        UtilityMedia.setResourcesDirectory("META-INF");

        final File file = new File("MANIFEST.MF");
        final Media media = new MediaSwt(file.getPath());
        try (InputStream input = UtilityMedia.getInputStream(media))
        {
            Assert.assertNotNull(input);
        }
    }

    /**
     * Test the output stream.
     * 
     * @throws IOException If error.
     * @throws LionEngineException If error.
     */
    @Test
    public void testOutputStream() throws LionEngineException, IOException
    {
        UtilityMedia.setLoadFromJar(null);
        UtilityMedia.setResourcesDirectory(Constant.EMPTY_STRING);

        final File file = new File("test.xml");
        final Media media = UtilityMedia.get(file);
        try (OutputStream output = UtilityMedia.getOutputStream(media))
        {
            Assert.assertNotNull(output);
        }
        finally
        {
            UtilFile.deleteFile(media.getFile());
        }
    }

    /**
     * Test the input stream.
     * 
     * @throws IOException If error.
     * @throws LionEngineException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testInputStreamFail() throws LionEngineException, IOException
    {
        UtilityMedia.setLoadFromJar(UtilityMediaTest.class);

        final File file = new File("none");
        final Media media = UtilityMedia.get(file);
        try (InputStream input = UtilityMedia.getInputStream(media))
        {
            Assert.assertNotNull(input);
        }
    }

    /**
     * Test the output stream.
     * 
     * @throws IOException If error.
     * @throws LionEngineException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testOutputStreamFail() throws LionEngineException, IOException
    {
        UtilityMedia.setResourcesDirectory(null);
        UtilityMedia.setLoadFromJar(null);

        final File file = new File("test.xml");
        final Media media = UtilityMedia.get(file);
        try (OutputStream output = UtilityMedia.getOutputStream(media))
        {
            Assert.assertNotNull(output);
        }
        finally
        {
            UtilFile.deleteFile(media.getFile());
        }
    }

    /**
     * Test the input stream.
     * 
     * @throws IOException If error.
     * @throws LionEngineException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testInputStreamJarFail() throws LionEngineException, IOException
    {
        UtilityMedia.setLoadFromJar(null);

        final File file = new File("none");
        final Media media = UtilityMedia.get(file);
        try (InputStream input = UtilityMedia.getInputStream(media))
        {
            Assert.assertNotNull(input);
        }
    }

    /**
     * Test is exists.
     */
    @Test
    public void testExists()
    {
        UtilityMedia.setLoadFromJar(null);
        UtilityMedia.setResourcesDirectory(Constant.EMPTY_STRING);

        final Media media = new MediaSwt(MediaSwtTest.class.getResource("raster.xml").getFile());
        Assert.assertTrue(media.exists());
    }

    /**
     * Test is exists from jar.
     */
    @Test
    public void testExistsFromJar()
    {
        UtilityMedia.setResourcesDirectory(null);
        UtilityMedia.setLoadFromJar(UtilityMediaTest.class);

        final Media media = new MediaSwt("raster.xml");
        Assert.assertTrue(media.exists());
    }

    /**
     * Test if not exists.
     */
    @Test
    public void testNotExists()
    {
        UtilityMedia.setLoadFromJar(null);
        UtilityMedia.setResourcesDirectory(Constant.EMPTY_STRING);

        final Media media = new MediaSwt("void");
        Assert.assertFalse(UtilityMedia.exists(media));
    }

    /**
     * Test if not exists.
     */
    @Test
    public void testNotExistsFromJar()
    {
        UtilityMedia.setLoadFromJar(UtilityMediaTest.class);

        final Media media = new MediaSwt("void");
        Assert.assertFalse(UtilityMedia.exists(media));
    }
}
