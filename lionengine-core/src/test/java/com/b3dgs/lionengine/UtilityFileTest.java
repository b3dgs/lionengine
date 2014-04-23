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

import com.b3dgs.lionengine.core.FactoryMediaMock;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityFile;

/**
 * Test the utility file class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityFileTest
{
    /** Resources path. */
    private static String PATH;

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Media.setMediaFactory(new FactoryMediaMock());
        Media.setSeparator(java.io.File.separator);
        UtilityFileTest.PATH = Media.getPath("src", "test", "resources", "utilityfile");
        UtilityFile.setTempDirectory("temp");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Media.setMediaFactory(null);
        UtilityFile.setTempDirectory(null);
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
     * Test the utility file check.
     */
    @Test
    public void testUtilityFileCheck()
    {
        final String file = "file1.txt";
        final String path = Media.getPath(UtilityFileTest.PATH, file);
        final File descriptor = new File(path);

        Assert.assertTrue(UtilityFile.getTempDir().contains("temp"));
        Assert.assertTrue(UtilityFile.exists(path));
        Assert.assertFalse(UtilityFile.exists(null));
        Assert.assertEquals("txt", UtilityFile.getExtension(path));
        Assert.assertEquals("txt", UtilityFile.getExtension(descriptor));
        Assert.assertEquals("", UtilityFile.getExtension("noextension"));
        Assert.assertEquals("", UtilityFile.getExtension("noextension."));
        Assert.assertEquals(file, UtilityFile.getFilenameFromPath(path));
        Assert.assertTrue(UtilityFile.isFile(path));
        Assert.assertFalse(UtilityFile.isFile(null));
        Assert.assertFalse(UtilityFile.isDir(path));
        Assert.assertFalse(UtilityFile.isDir(null));
    }

    /**
     * Test the utility file directory manipulation.
     */
    @Test
    public void testUtilityFileDirectory()
    {
        final File fileDir = new File(Media.getPath(UtilityFileTest.PATH, "directory"));
        if (fileDir.mkdir())
        {
            try
            {
                final String[] dirs = UtilityFile.getDirsList(UtilityFileTest.PATH);
                Assert.assertEquals(1, dirs.length);
                Assert.assertEquals("directory", dirs[0]);
                Assert.assertEquals(0, UtilityFile.getDirsList(Media.getPath("null")).length);

                final String[] files = UtilityFile.getFilesList(UtilityFileTest.PATH);
                Assert.assertEquals(3, files.length);
                Assert.assertEquals(0, UtilityFile.getFilesList(Media.getPath("null")).length);
                Assert.assertEquals(0, UtilityFile.getFilesList(Media.getPath("null"), "txt").length);
                Assert.assertEquals(2, UtilityFile.getFilesList(UtilityFileTest.PATH, "txt").length);
            }
            catch (final LionEngineException exception)
            {
                Assert.fail(exception.getMessage());
            }
            finally
            {
                UtilityFile.deleteDirectory(fileDir);
            }
        }
        else
        {
            Assert.fail();
        }
    }

    /**
     * Test the utility file creation.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testUtilityFileCreation() throws IOException
    {
        final String dir = Media.getPath("temp");
        final String test = Media.getPath("temp", "test");
        final File tempDir = new File(dir);
        final File testFile = new File(test);
        Assert.assertTrue(tempDir.mkdirs());
        try
        {
            Assert.assertTrue(testFile.createNewFile());
        }
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }
        UtilityFile.deleteDirectory(tempDir);

        final File fileTest = new File(Media.getPath(UtilityFileTest.PATH, "create"));
        Assert.assertTrue(fileTest.createNewFile());
        Assert.assertTrue(fileTest.delete());

        Assert.assertEquals("path" + File.separator + "test", Media.getPath("path", "test"));
    }
}
