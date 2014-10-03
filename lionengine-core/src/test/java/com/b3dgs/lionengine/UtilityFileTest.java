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

import javax.xml.bind.ValidationException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.FactoryMediaProvider;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

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
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());
        UtilityFileTest.PATH = UtilFile.getPath("src", "test", "resources", "utilityfile");
        UtilFile.setTempDirectory("temp");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryMediaProvider.setFactoryMedia(null);
        UtilFile.setTempDirectory(null);
    }

    /**
     * Test the core class.
     * 
     * @throws NoSuchMethodException If error.
     * @throws IllegalAccessException If error.
     * @throws InstantiationException If error.
     * @throws InvocationTargetException If success.
     */
    @Test(expected = InvocationTargetException.class)
    public void testUtilityFileClass() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException
    {
        final Constructor<UtilFile> utilityFile = UtilFile.class.getDeclaredConstructor();
        utilityFile.setAccessible(true);
        final UtilFile clazz = utilityFile.newInstance();
        Assert.assertNotNull(clazz);
    }

    /**
     * Test the utility file check.
     * 
     * @throws ValidationException If error.
     */
    @Test
    public void testUtilityFileCheck() throws ValidationException
    {
        final String file = "file1.txt";
        final String path = UtilFile.getPath(UtilityFileTest.PATH, file);
        final File descriptor = new File(path);

        Assert.assertTrue(UtilFile.getTempDir().contains("temp"));
        Assert.assertTrue(UtilFile.exists(path));
        Assert.assertFalse(UtilFile.exists(null));
        Assert.assertTrue(UtilFile.isFile(path));
        Assert.assertEquals("txt", UtilFile.getExtension(path));
        Assert.assertEquals("txt", UtilFile.getExtension(descriptor));
        Assert.assertEquals("", UtilFile.getExtension("noextension"));
        Assert.assertEquals("", UtilFile.getExtension("noextension."));
        Assert.assertEquals(file, UtilFile.getFilenameFromPath(path));
        Assert.assertTrue(UtilFile.isFile(path));
        Assert.assertFalse(UtilFile.isFile(null));
        Assert.assertFalse(UtilFile.isDir(path));
        Assert.assertFalse(UtilFile.isDir(null));
        Assert.assertEquals("file1", UtilFile.removeExtension(file));
    }

    /**
     * Test the utility file directory manipulation.
     */
    @Test
    public void testUtilityFileDirectory()
    {
        final File fileDir = new File(UtilFile.getPath(UtilityFileTest.PATH, "directory"));
        if (fileDir.mkdir())
        {
            try
            {
                final String[] dirs = UtilFile.getDirsList(UtilityFileTest.PATH);
                Assert.assertEquals(1, dirs.length);
                Assert.assertEquals("directory", dirs[0]);
                Assert.assertEquals(0, UtilFile.getDirsList(UtilFile.getPath("null")).length);

                final String[] files = UtilFile.getFilesList(UtilityFileTest.PATH);
                Assert.assertEquals(7, files.length);
                Assert.assertEquals(0, UtilFile.getFilesList(UtilFile.getPath("null")).length);
                Assert.assertEquals(0, UtilFile.getFilesByExtension(UtilFile.getPath("null"), "txt").size());
                Assert.assertEquals(2, UtilFile.getFilesByExtension(UtilityFileTest.PATH, "txt").size());
                Assert.assertFalse(UtilFile.getFilesByName(new File(UtilityFileTest.PATH).getParentFile(), "file")
                        .isEmpty());
            }
            catch (final LionEngineException exception)
            {
                Assert.fail(exception.getMessage());
            }
            finally
            {
                UtilFile.deleteDirectory(fileDir);
            }
        }
        else
        {
            Assert.fail();
        }
    }

    /**
     * Test the utility file path.
     */
    @Test
    public void testUtilityFilePath()
    {
        Assert.assertEquals("", UtilFile.getPathSeparator(".", "", ""));
        Assert.assertEquals("null", UtilFile.getPathSeparator(".", (String) null, (String) null));
    }

    /**
     * Test the utility file creation.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testUtilityFileCreation() throws IOException
    {
        final String dir = UtilFile.getPath("temp");
        final String test = UtilFile.getPath("temp", "test");
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
        UtilFile.deleteDirectory(tempDir);

        final File fileTest = new File(UtilFile.getPath(UtilityFileTest.PATH, "create"));
        Assert.assertTrue(fileTest.createNewFile());
        Assert.assertTrue(fileTest.delete());

        Assert.assertEquals("path" + File.separator + "test", UtilFile.getPath("path", "test"));
    }

    /**
     * Test the utility file type.
     */
    @Test
    public void testUtilityFileType()
    {
        Assert.assertTrue(UtilFile.isType(new File("temp"), ""));
        Assert.assertTrue(UtilFile.isType(new File("temp.tmp"), "tmp"));
    }
}
