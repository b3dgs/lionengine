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
package com.b3dgs.lionengine.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Test the utility file class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilFileTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(UtilFileTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test the constructor.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws ReflectiveOperationException
    {
        UtilTests.testPrivateConstructor(UtilFile.class);
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
        final String path = Medias.create(file).getFile().getPath();
        final File descriptor = new File(path);

        Assert.assertTrue(UtilFile.exists(path));
        Assert.assertFalse(UtilFile.exists(null));
        Assert.assertTrue(UtilFile.isFile(path));
        Assert.assertEquals("txt", UtilFile.getExtension(path));
        Assert.assertEquals("txt", UtilFile.getExtension(descriptor));
        Assert.assertEquals(Constant.EMPTY_STRING, UtilFile.getExtension("noextension"));
        Assert.assertEquals(Constant.EMPTY_STRING, UtilFile.getExtension("noextension."));
        final String old = Medias.getSeparator();
        Medias.setSeparator(Constant.SLASH);
        Assert.assertTrue(UtilFile.getFilenameFromPath(path).endsWith(file));
        Medias.setSeparator(old);
        Assert.assertTrue(UtilFile.isFile(path));
        Assert.assertFalse(UtilFile.isFile(null));
        Assert.assertFalse(UtilFile.isDirectory(path));
        Assert.assertFalse(UtilFile.isDirectory(null));
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
            final String path = Medias.create("file").getFile().getParentFile().getAbsolutePath();
            final String[] dirs = UtilFile.getDirectoriesList(path);
            Assert.assertEquals(6, dirs.length);
            Assert.assertEquals("anim", dirs[0]);
            Assert.assertEquals(0, UtilFile.getDirectoriesList(UtilFile.getPath("null")).length);

            final String[] files = UtilFile.getFilesList(path);
            Assert.assertTrue("Count = " + files.length, files.length == 58);
            Assert.assertEquals(0, UtilFile.getFilesList(UtilFile.getPath("null")).length);
            Assert.assertEquals(0, UtilFile.getFilesByExtension(UtilFile.getPath("null"), "txt").size());

            final File parent = Medias.create("file").getFile().getParentFile();
            final int count = UtilFile.getFilesByExtension(path, "txt").size();
            Assert.assertEquals(2, count);
            Assert.assertEquals(UtilFile.getFilesByName(parent, "file").size(), 1);
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
        Assert.assertEquals(Constant.EMPTY_STRING,
                            UtilFile.getPathSeparator(Constant.DOT, Constant.EMPTY_STRING, Constant.EMPTY_STRING));
        Assert.assertEquals("null", UtilFile.getPathSeparator(Constant.DOT, (String) null, (String) null));
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
        try
        {
            UtilFile.deleteFile(new File("null"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test the utility file type.
     */
    @Test
    public void testType()
    {
        Assert.assertFalse(UtilFile.isType(new File("null"), Constant.EMPTY_STRING));
        Assert.assertTrue(UtilFile.isType(Medias.create("file").getFile(), Constant.EMPTY_STRING));
        Assert.assertTrue(UtilFile.isType(Medias.create("file1.txt").getFile(), "txt"));
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
            UtilFile.copy(input, output);
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
        try (final OutputStream output = new OutputStreamMock())
        {
            UtilFile.copy(null, output);
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
        try (final InputStream input = new InputStreamMock())
        {
            UtilFile.copy(input, null);
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
        try (final InputStream input = new InputStreamMock())
        {
            Assert.assertTrue(UtilFile.getCopy("te", input).delete());
            Assert.assertTrue(UtilFile.getCopy("temp", input).delete());
            Assert.assertTrue(UtilFile.getCopy("temp.tmp", input).delete());
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
        try (final InputStream input = new InputStreamMock())
        {
            UtilFile.getCopy(null, input);
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
        UtilFile.getCopy("temp", null);
    }

    /**
     * Test the get files.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFiles() throws IOException
    {
        final Path folder = Files.createTempDirectory("temp");
        final File file1 = Files.createTempFile(folder, "temp", ".tmp").toFile();
        final File file2 = Files.createTempFile(folder, "temp", ".tmp").toFile();
        final File dir = folder.toFile();

        final List<File> found = new ArrayList<>(UtilFile.getFiles(dir));
        final List<File> expected = Arrays.asList(file1, file2);
        Collections.sort(found);
        Collections.sort(expected);
        Assert.assertEquals(expected, found);

        Assert.assertTrue(file1.delete());
        Assert.assertTrue(file2.delete());
        Assert.assertTrue(dir.delete());
    }

    /**
     * Test the get files with wrong path.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testGetFilesError() throws IOException
    {
        Assert.assertNull(UtilFile.getFiles(new File("void")));
    }
}
