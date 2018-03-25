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
package com.b3dgs.lionengine.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.util.UtilFolder;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Test {@link Media}.
 */
public final class MediaTest
{
    /** Old resources directory. */
    private String oldDir;
    /** Old loader. */
    private Optional<Class<?>> oldLoader;

    /**
     * Prepare test.
     */
    @Before
    public void prepareTest()
    {
        oldDir = Medias.getResourcesDirectory();
        oldLoader = Medias.getResourcesLoader();
    }

    /**
     * Clean test.
     */
    @After
    public void cleanTest()
    {
        Medias.setResourcesDirectory(oldDir);
        Medias.setLoadFromJar(oldLoader.orElse(null));
    }

    /**
     * Test path getter.
     */
    @Test
    public void testPath()
    {
        final String path = "path";

        Assert.assertEquals(path, Medias.create(path).getPath());
    }

    /**
     * Test file path getter.
     */
    @Test
    public void testFile()
    {
        Medias.setLoadFromJar(MediaTest.class);
        final String path = "image.png";

        Assert.assertEquals(new File(MediaTest.class.getResource(path).getFile()), Medias.create(path).getFile());
    }

    /**
     * Test parent path getter.
     */
    @Test
    public void testParentPath()
    {
        final String path = "path";

        Assert.assertEquals(Constant.EMPTY_STRING, Medias.create(path).getParentPath());
    }

    /**
     * Test input stream.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testInputStream() throws IOException
    {
        Medias.setLoadFromJar(MediaTest.class);
        final Media media = Medias.create("image.png");

        Assert.assertTrue(media.exists());

        try (InputStream input = media.getInputStream())
        {
            Assert.assertNotNull(input);
        }
    }

    /**
     * Test input stream with no existing file.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testInputStreamNotExists() throws IOException
    {
        Medias.setResourcesDirectory(null);
        final Media media = Medias.create("void");

        Assert.assertFalse(media.exists());

        try (InputStream input = media.getInputStream())
        {
            Assert.assertNotNull(input);
        }
    }

    /**
     * Test input stream with no existing in JAR.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testInputStreamNotExistsInJar() throws IOException
    {
        Medias.setLoadFromJar(MediaTest.class);
        final File file = File.createTempFile("void", "");
        final File dir = new File(file.getParentFile(), MediaTest.class.getSimpleName());
        dir.mkdir();
        final File file2 = File.createTempFile("void", "", dir);
        final Media media = Medias.create(file2.getName());

        try (InputStream input = media.getInputStream())
        {
            Assert.assertNotNull(input);
        }
        finally
        {
            Assert.assertTrue(file.delete());
            Assert.assertTrue(file2.delete());

            UtilFolder.deleteDirectory(dir);
        }
    }

    /**
     * Test output stream.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testOutputStream() throws IOException
    {
        Medias.setLoadFromJar(MediaTest.class);
        final Media media = Medias.create(String.valueOf(System.nanoTime()), "test");

        try (OutputStream output = media.getOutputStream())
        {
            Assert.assertNotNull(output);
        }
        finally
        {
            Assert.assertTrue(media.getFile().exists());
            Assert.assertTrue(media.getFile().delete());
            Assert.assertFalse(media.getFile().exists());
        }
    }

    /**
     * Test media existence.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testExists() throws IOException
    {
        Medias.setLoadFromJar(MediasTest.class);

        Assert.assertFalse(Medias.create("void").exists());
        Assert.assertTrue(Medias.create("image.png").exists());

        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));

        final File file = File.createTempFile("test", ".txt", new File(Medias.getResourcesDirectory()));
        final Media media = Medias.get(file);

        Assert.assertTrue(file.exists());
        Assert.assertTrue(media.exists());

        Assert.assertTrue(file.delete());
        Assert.assertFalse(media.exists());
    }

    /**
     * Test temp path creation unable.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testUnableCreateTempDir() throws IOException
    {
        final String tempFolder = UtilReflection.getField(MediaDefault.class, "TEMP");
        final File folder = new File(tempFolder, MediasTest.class.getClass().getSimpleName());
        folder.mkdirs();

        Assert.assertTrue(folder.delete());
        Assert.assertTrue(folder.createNewFile());

        try
        {
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
            final String path = UtilReflection.getMethod(MediaDefault.class, "getTempDir", MediasTest.class.getClass());

            Assert.assertEquals(folder.getPath(), path);

            Verbose.info("****************************************************************************************");
        }
        finally
        {
            Assert.assertTrue(folder.delete());
        }
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final Media media = Medias.create("media");
        final Media media1 = Medias.create("media");
        final Media media2 = Medias.create("media2");

        Assert.assertEquals(media.hashCode(), media.hashCode());
        Assert.assertEquals(media.hashCode(), media1.hashCode());
        Assert.assertNotEquals(media.hashCode(), media2.hashCode());
        Assert.assertNotEquals(media.hashCode(), new Object().hashCode());
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final Media media = Medias.create("media");
        final Media media1 = Medias.create("media");
        final Media media2 = Medias.create("media2");

        Assert.assertEquals(media, media);
        Assert.assertEquals(media, media1);
        Assert.assertNotEquals(media, media2);
        Assert.assertNotEquals(media, null);
        Assert.assertNotEquals(media, new Object());
    }

    /**
     * Test get name.
     */
    @Test
    public void testGetName()
    {
        final Media media = Medias.create("test", "media.ext");

        Assert.assertEquals("media.ext", media.getName());
    }
}
