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
package com.b3dgs.lionengine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;

import javax.xml.bind.ValidationException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.mock.FactoryMediaMock;
import com.b3dgs.lionengine.mock.MediaMock;

/**
 * Test the utility file class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityFileTest
{
    /** Mock media. */
    private static final FactoryMediaMock MOCK = new FactoryMediaMock();

    /**
     * Setup test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setFactoryMedia(MOCK);
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
    public void testCheck() throws ValidationException
    {
        final String file = "file1.txt";
        final String path = new MediaMock(file).getPath();
        final File descriptor = new File(path);

        Assert.assertTrue(UtilFile.exists(path));
        Assert.assertFalse(UtilFile.exists(null));
        Assert.assertTrue(UtilFile.isFile(path));
        Assert.assertEquals("txt", UtilFile.getExtension(path));
        Assert.assertEquals("txt", UtilFile.getExtension(descriptor));
        Assert.assertEquals("", UtilFile.getExtension("noextension"));
        Assert.assertEquals("", UtilFile.getExtension("noextension."));
        final String old = MOCK.getSeparator();
        MOCK.setSeparator("/");
        Assert.assertEquals(file, UtilFile.getFilenameFromPath(path));
        MOCK.setSeparator(old);
        Assert.assertTrue(UtilFile.isFile(path));
        Assert.assertFalse(UtilFile.isFile(null));
        Assert.assertFalse(UtilFile.isDir(path));
        Assert.assertFalse(UtilFile.isDir(null));
        Assert.assertEquals("file1", UtilFile.removeExtension(file));
    }

    /**
     * Test the utility file directory manipulation.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testDirectory() throws IOException
    {
        final File fileDir = Files.createTempDirectory("directory").toFile();
        fileDir.deleteOnExit();

        try
        {
            final String[] dirs = UtilFile.getDirsList(new File(new MediaMock("file").getPath()).getParentFile()
                    .getAbsolutePath());
            Assert.assertEquals(1, dirs.length);
            Assert.assertEquals("bis", dirs[0]);
            Assert.assertEquals(0, UtilFile.getDirsList(UtilFile.getPath("null")).length);

            final String[] files = UtilFile.getFilesList(new File(new MediaMock("file").getPath()).getParentFile()
                    .getParentFile().getAbsolutePath());
            Assert.assertTrue("Count = " + files.length, files.length >= 25);
            Assert.assertEquals(0, UtilFile.getFilesList(UtilFile.getPath("null")).length);
            Assert.assertEquals(0, UtilFile.getFilesByExtension(UtilFile.getPath("null"), "txt").size());

            final File parent = new File(new MediaMock("file").getPath());
            Assert.assertEquals(2, UtilFile.getFilesByExtension(parent.getParentFile().getAbsolutePath(), "txt").size());
            Assert.assertFalse(UtilFile.getFilesByName(parent.getParentFile(), "file").isEmpty());
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

    /**
     * Test the utility file path.
     */
    @Test
    public void testPath()
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
    public void testCreation() throws IOException
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

        Assert.assertEquals("path" + File.separator + "test", UtilFile.getPath("path", "test"));

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        UtilFile.deleteFile(new File("null"));
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test the utility file type.
     */
    @Test
    public void testType()
    {
        Assert.assertTrue(UtilFile.isType(new File("temp"), ""));
        Assert.assertTrue(UtilFile.isType(new File("temp.tmp"), "tmp"));
    }
}
