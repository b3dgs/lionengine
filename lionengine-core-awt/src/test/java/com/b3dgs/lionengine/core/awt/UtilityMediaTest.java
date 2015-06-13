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
package com.b3dgs.lionengine.core.awt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test the utility media class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
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
        Medias.setFactoryMedia(new FactoryMediaAwt());
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
     * @throws ReflectiveOperationException If error.
     */
    @Test(expected = InvocationTargetException.class)
    public void testConstructor() throws ReflectiveOperationException
    {
        final Constructor<UtilityMedia> constructor = UtilityMedia.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final UtilityMedia utility = constructor.newInstance();
        Assert.assertNotNull(utility);
        Assert.fail();
    }

    /**
     * Test the utility media.
     */
    @Test
    public void testUtility()
    {
        UtilityMedia.setLoadFromJar(UtilityMediaTest.class);

        UtilityMedia.setResourcesDirectory(null);
        Assert.assertEquals("", UtilityMedia.getRessourcesDir());
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
        UtilityMedia.setResourcesDirectory("");
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
        final Media media = new MediaAwt(file.getPath());
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
        UtilityMedia.setResourcesDirectory("");
        final File file = new File("test.xml");
        file.deleteOnExit();
        final Media media = UtilityMedia.get(file);
        try (OutputStream output = UtilityMedia.getOutputStream(media))
        {
            Assert.assertNotNull(output);
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
        file.deleteOnExit();
        final Media media = UtilityMedia.get(file);
        try (OutputStream output = UtilityMedia.getOutputStream(media))
        {
            Assert.assertNotNull(output);
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
}
