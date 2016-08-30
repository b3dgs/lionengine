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
package com.b3dgs.lionengine.core;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the medias factory class.
 */
public class MediasTest
{
    /** Old resources directory. */
    private static String oldDir;
    /** Old loader. */
    private static Class<?> oldLoader;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void prepare()
    {
        oldDir = Medias.getResourcesDirectory();
        oldLoader = Medias.getResourcesLoader();
        Medias.setFactoryMedia(new FactoryMediaDefault());
    }

    /**
     * Clean test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(oldDir);
        Medias.setLoadFromJar(oldLoader);
    }

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(Medias.class);
    }

    /**
     * Test the create media with <code>null</code> argument.
     */
    @Test
    public void testCreateMediaNull()
    {
        Medias.setResourcesDirectory("rsc");
        Assert.assertEquals("null", Medias.create((String) null).getPath());
    }

    /**
     * Test the create media from resources directory.
     */
    @Test
    public void testCreateMediaResources()
    {
        Medias.setResourcesDirectory("rsc");
        Assert.assertEquals("rsc" + Medias.getSeparator(), Medias.getResourcesDirectory());

        final Media media = Medias.create("test.txt");
        Assert.assertEquals("", media.getParentPath());
        Assert.assertEquals("test.txt", media.getPath());
        Assert.assertEquals("rsc" + File.separator + "test.txt", media.getFile().getPath());

        final Media media2 = Medias.create("test", "toto.txt");
        Assert.assertEquals("test", media2.getParentPath());
        Assert.assertEquals("test" + File.separator + "toto.txt", media2.getPath());
        Assert.assertEquals("rsc" + File.separator + "test" + File.separator + "toto.txt", media2.getFile().getPath());
    }

    /**
     * Test the create media from loader.
     */
    @Test
    public void testCreateMediaLoader()
    {
        Medias.setLoadFromJar(MediasTest.class);
        Assert.assertEquals(MediasTest.class, Medias.getResourcesLoader());

        final Media media = Medias.create("test.txt");
        Assert.assertEquals("", media.getParentPath());
        Assert.assertEquals("test.txt", media.getPath());
        Assert.assertEquals("test.txt", media.getFile().getPath());

        final Media media2 = Medias.create("test", "toto.txt");
        Assert.assertEquals("test", media2.getParentPath());
        Assert.assertEquals("test/toto.txt", media2.getPath());
        Assert.assertEquals("test" + File.separator + "toto.txt", media2.getFile().getPath());
    }

    /**
     * Test the create media from resources directory.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateMediaError()
    {
        Medias.setResourcesDirectory(null);
        Medias.setLoadFromJar(null);

        Assert.assertNull(Medias.create("test.txt"));
    }

    /**
     * Test the get with suffix.
     */
    @Test
    public void testGetWithSuffix()
    {
        Medias.setResourcesDirectory(oldDir);
        final Media folder = Medias.create("folder", "foo");
        final Media file = Medias.create("folder", "file.txt");

        Assert.assertEquals(Medias.create("folder", "foo_suffix"), Medias.getWithSuffix(folder, "suffix"));
        Assert.assertEquals(Medias.create("folder", "file_suffix.txt"), Medias.getWithSuffix(file, "suffix"));
    }

    /**
     * Test the get file.
     */
    @Test
    public void testGetFile()
    {
        Medias.setResourcesDirectory("rsc");
        final File file = new File("rsc" + File.separator + "test.txt");
        final Media media = Medias.get(file);
        Assert.assertEquals(file, media.getFile());
        Assert.assertEquals("test.txt", media.getPath());
    }

    /**
     * Test the get file no resources.
     */
    @Test(expected = LionEngineException.class)
    public void testGetFileNoResources()
    {
        Medias.setResourcesDirectory(null);
        final File file = new File("test.txt");
        Assert.assertNull(Medias.get(file));
    }

    /**
     * Test the separator.
     */
    @Test
    public void testSeparator()
    {
        final String old = Medias.getSeparator();
        Medias.setSeparator("%");
        Assert.assertEquals("%", Medias.getSeparator());
        Medias.setSeparator(old);
    }
}
