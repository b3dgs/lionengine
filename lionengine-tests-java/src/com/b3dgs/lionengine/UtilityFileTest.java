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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityFile;
import com.b3dgs.lionengine.core.UtilityMedia;

/**
 * Test the utility file class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityFileTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Engine.start("UtilityFileTest", Version.create(1, 0, 0), "resources");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Test the utility file class.
     * 
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     * @throws InstantiationException If error.
     * @throws SecurityException If error.
     * @throws NoSuchMethodException If error.
     */
    @Test
    public void testUtilityFileClass() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            NoSuchMethodException, SecurityException
    {
        final Constructor<UtilityFile> utilityFile = UtilityFile.class.getDeclaredConstructor();
        utilityFile.setAccessible(true);
        try
        {
            final UtilityFile clazz = utilityFile.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Test the utility file.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testUtilityFile() throws IOException
    {
        final String file = "dot.png";
        final Media media = UtilityMedia.get(file);
        final String path = Media.getPath("resources", media.getPath());
        final File descriptor = new File(path);

        Assert.assertTrue(UtilityFile.exists(path));
        Assert.assertFalse(UtilityFile.exists(null));
        Assert.assertEquals("png", UtilityFile.getExtension(path));
        Assert.assertEquals("png", UtilityFile.getExtension(descriptor));
        Assert.assertEquals("", UtilityFile.getExtension("noextension"));
        Assert.assertEquals("", UtilityFile.getExtension("noextension."));
        Assert.assertEquals(file, UtilityFile.getFilenameFromPath(path));
        Assert.assertTrue(UtilityFile.isFile(path));
        Assert.assertFalse(UtilityFile.isFile(null));
        Assert.assertFalse(UtilityFile.isDir(path));
        Assert.assertFalse(UtilityFile.isDir(null));

        final File fileDir = new File(Media.getPath("resources", "directory"));
        if (fileDir.mkdir())
        {
            try
            {
                final String[] dirs = UtilityFile.getDirsList(UtilityMedia.get("resources").getPath());
                Assert.assertEquals(1, dirs.length);
                Assert.assertEquals("directory", dirs[0]);
                Assert.assertEquals(0, UtilityFile.getDirsList(UtilityMedia.get("null").getPath()).length);

                final String[] files = UtilityFile.getFilesList("resources");
                Assert.assertEquals(17, files.length);
                Assert.assertEquals(0, UtilityFile.getFilesList(UtilityMedia.get("null").getPath()).length);
                Assert.assertEquals(0, UtilityFile.getFilesList(UtilityMedia.get("null").getPath(), "txt").length);
                Assert.assertEquals(2, UtilityFile.getFilesList(UtilityMedia.get("resources").getPath(), "wav").length);
            }
            finally
            {
                UtilityFile.deleteDirectory(fileDir);
            }
        }

        final Media dir = UtilityMedia.get("temp");
        final Media test = UtilityMedia.get("temp", "test");
        final File tempDir = dir.getFile();
        final File testFile = test.getFile();
        Assert.assertTrue(tempDir.mkdirs());
        try
        {
            Assert.assertTrue(testFile.createNewFile());
        }
        catch (final IOException exception)
        {
            Assert.fail();
        }
        UtilityFile.deleteDirectory(tempDir);

        final File fileTest = new File(UtilityMedia.get("resources", "file").getPath());
        Assert.assertTrue(fileTest.createNewFile());
        Assert.assertTrue(fileTest.delete());

        Assert.assertEquals("path" + File.separator + "test", Media.getPath("path", "test"));
    }
}
