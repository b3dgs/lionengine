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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.test.mock.InputStreamMock;
import com.b3dgs.lionengine.test.mock.OutputStreamMock;
import com.b3dgs.lionengine.test.util.UtilTests;

/**
 * Test the utility file class.
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
     * Test the remove extension.
     */
    @Test
    public void testRemoveExtension()
    {
        Assert.assertEquals("temp", UtilFile.removeExtension("temp.tmp"));
        Assert.assertEquals("temp", UtilFile.removeExtension("temp."));
        Assert.assertEquals("temp", UtilFile.removeExtension("temp"));
    }

    /**
     * Test the remove extension.
     */
    @Test(expected = LionEngineException.class)
    public void testRemoveExtensionNull()
    {
        Assert.assertNull(UtilFile.removeExtension(null));
    }

    /**
     * Test the remove extension.
     */
    @Test
    public void testNormalizeExtension()
    {
        Assert.assertEquals("temp.tmp", UtilFile.normalizeExtension("temp", "tmp"));
        Assert.assertEquals("temp.tmp", UtilFile.normalizeExtension("temp", ".tmp"));
        Assert.assertEquals("temp.tmp", UtilFile.normalizeExtension("temp", "..tmp"));
        Assert.assertEquals("temp.tmp", UtilFile.normalizeExtension("temp.tmp", ".tmp"));
        Assert.assertEquals("temp.tmp", UtilFile.normalizeExtension("temp.txt", ".tmp"));
    }

    /**
     * Test the normalized extension with <code>null</code> file.
     */
    @Test(expected = LionEngineException.class)
    public void testNormalizeExtensionNullFile()
    {
        Assert.assertNull(UtilFile.normalizeExtension(null, ""));
    }

    /**
     * Test the normalized extension with <code>null</code> extension.
     */
    @Test(expected = LionEngineException.class)
    public void testNormalizeExtensionNullExtension()
    {
        Assert.assertNull(UtilFile.normalizeExtension("", null));
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
            Assert.assertNull(UtilFile.getCopy(null, input));
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
        Assert.assertNull(UtilFile.getCopy("temp", null));
    }

    /**
     * Test the get path.
     */
    @Test
    public void testGetPath()
    {
        Assert.assertEquals("a" + File.separator + "b" + File.separator + "c", UtilFile.getPath("a", "b", "c"));
    }

    /**
     * Test the get path separator.
     */
    @Test
    public void testGetPathSeparator()
    {
        Assert.assertEquals("this%path%next", UtilFile.getPathSeparator("%", "this", "path", "next"));
        Assert.assertEquals("this%path%next", UtilFile.getPathSeparator("%", "this%", "path%", "next"));
        Assert.assertEquals("this%path%", UtilFile.getPathSeparator("%", "this%", "path%", null, ""));
    }

    /**
     * Test the get extension.
     */
    @Test
    public void testGetExtension()
    {
        Assert.assertEquals("", UtilFile.getExtension("tmp"));
        Assert.assertEquals("tmp", UtilFile.getExtension(".tmp"));
        Assert.assertEquals("tmp", UtilFile.getExtension("temp.tmp"));
        Assert.assertEquals("t", UtilFile.getExtension("temp.t"));
        Assert.assertEquals(Constant.EMPTY_STRING, UtilFile.getExtension("temp."));
        Assert.assertEquals(Constant.EMPTY_STRING, UtilFile.getExtension("temp"));
    }

    /**
     * Test the get extension null.
     */
    @Test(expected = LionEngineException.class)
    public void testGetExtensionNull()
    {
        Assert.assertNull(UtilFile.getExtension((String) null));
    }

    /**
     * Test the get extension on file.
     */
    @Test
    public void testGetExtensionFile()
    {
        Assert.assertEquals("tmp", UtilFile.getExtension(new File("temp.tmp")));
        Assert.assertEquals("t", UtilFile.getExtension(new File("toto", "temp.t")));
        Assert.assertEquals(Constant.EMPTY_STRING, UtilFile.getExtension(new File("temp.")));
        Assert.assertEquals(Constant.EMPTY_STRING, UtilFile.getExtension(new File("temp")));
    }

    /**
     * Test the get extension file null.
     */
    @Test(expected = LionEngineException.class)
    public void testGetExtensionFileNull()
    {
        Assert.assertNull(UtilFile.getExtension((File) null));
    }

    /**
     * Test the get directories from path.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetDirectories() throws IOException
    {
        final Path dir = Files.createTempDirectory("temp");
        final File file = Files.createTempFile(dir, "temp", ".tmp").toFile();
        final List<File> expected = new ArrayList<>();
        for (int i = 0; i < 5; i++)
        {
            expected.add(Files.createTempDirectory(dir, "temp").toFile());
        }
        Collections.sort(expected);

        final List<File> found = UtilFile.getDirectories(dir.toFile());
        Collections.sort(found);

        Assert.assertEquals(expected, found);

        Assert.assertTrue(file.delete());
        for (final File current : expected)
        {
            Assert.assertTrue(current.delete());
        }
        Assert.assertTrue(dir.toFile().delete());
    }

    /**
     * Test the get directories from wrong path.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetDirectoriesError() throws IOException
    {
        final Path dir = Files.createTempDirectory("temp");
        try
        {
            Assert.assertTrue(UtilFile.getDirectories(new File("null")).isEmpty());
        }
        finally
        {
            Assert.assertTrue(dir.toFile().delete());
        }
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

    /**
     * Test the get files by extension.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFilesByExtension() throws IOException
    {
        final Path folder = Files.createTempDirectory("temp");
        final File file1 = Files.createTempFile(folder, "temp", ".tmp").toFile();
        final File file2 = Files.createTempFile(folder, "temp", ".txt").toFile();
        final File folder2 = Files.createTempDirectory(folder, "temp").toFile();
        final File file3 = Files.createTempFile(folder2.toPath(), "temp", ".txt").toFile();
        final File dir = folder.toFile();

        final List<File> expected = Arrays.asList(file2, file3);
        final List<File> result = UtilFile.getFilesByExtension(dir, "txt");
        Collections.sort(expected);
        Collections.sort(result);

        Assert.assertEquals(expected, result);

        Assert.assertTrue(file1.delete());
        Assert.assertTrue(file2.delete());
        Assert.assertTrue(file3.delete());
        Assert.assertTrue(folder2.delete());
        Assert.assertTrue(dir.delete());
    }

    /**
     * Test the get files by name.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFilesByName() throws IOException
    {
        final Path folder = Files.createTempDirectory("temp");
        final File file1 = Files.createTempFile(folder, "temp", ".tmp").toFile();
        final File file2 = Files.createTempFile(folder, "temp", ".tmp").toFile();
        final File folder2 = Files.createTempDirectory(folder, "temp").toFile();
        final File file3 = Files.createTempFile(folder2.toPath(), "temp", ".tmp").toFile();
        final File dir = folder.toFile();

        Assert.assertEquals(Arrays.asList(file2), UtilFile.getFilesByName(dir, file2.getName()));

        Assert.assertTrue(file1.delete());
        Assert.assertTrue(file2.delete());
        Assert.assertTrue(file3.delete());
        Assert.assertTrue(folder2.delete());
        Assert.assertTrue(dir.delete());
    }

    /**
     * Test the delete directory.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testDeleteDirectory() throws IOException
    {
        final Path directory = Files.createTempDirectory("temp");
        final File file = Files.createTempFile(directory, "temp", ".tmp").toFile();
        final File dir1 = Files.createTempFile(directory, "temp", ".tmp").toFile();

        Assert.assertTrue(directory.toFile().exists());
        Assert.assertTrue(file.exists());
        Assert.assertTrue(dir1.exists());

        UtilFile.deleteDirectory(directory.toFile());

        Assert.assertFalse(directory.toFile().exists());
        Assert.assertFalse(file.exists());
        Assert.assertFalse(dir1.exists());
    }

    /**
     * Test the delete directory warning.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testDeleteDirectoryWarning() throws IOException
    {
        UtilFile.deleteDirectory(new File("void"));
    }

    /**
     * Test the delete file.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testDeleteFile() throws IOException
    {
        final File file = Files.createTempFile("temp", ".tmp").toFile();
        Assert.assertTrue(file.exists());
        UtilFile.deleteFile(file);
        Assert.assertFalse(file.exists());
    }

    /**
     * Test the delete file error.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testDeleteFileError() throws IOException
    {
        UtilFile.deleteFile(new File("void"));
    }

    /**
     * Test the exists path.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testExists() throws IOException
    {
        Assert.assertFalse(UtilFile.exists(null));

        final Path folder = Files.createTempDirectory("temp");
        final File file = Files.createTempFile(folder, "temp", ".tmp").toFile();
        final File dir = folder.toFile();

        Assert.assertTrue(UtilFile.exists(dir.getPath()));
        Assert.assertTrue(UtilFile.exists(file.getPath()));

        Assert.assertTrue(file.delete());
        Assert.assertTrue(dir.delete());

        Assert.assertFalse(UtilFile.exists(dir.getPath()));
        Assert.assertFalse(UtilFile.exists(file.getPath()));
    }

    /**
     * Test the is directory.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testIsDirectory() throws IOException
    {
        Assert.assertFalse(UtilFile.isDirectory(null));

        final File dir = Files.createTempDirectory("temp").toFile();

        Assert.assertTrue(UtilFile.isDirectory(dir.getPath()));
        Assert.assertTrue(dir.delete());
        Assert.assertFalse(UtilFile.isDirectory(dir.getPath()));
    }

    /**
     * Test the is file.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testIsFile() throws IOException
    {
        Assert.assertFalse(UtilFile.isFile(null));

        final File file = Files.createTempFile("temp", ".tmp").toFile();

        Assert.assertTrue(UtilFile.isFile(file.getPath()));
        Assert.assertTrue(file.delete());
        Assert.assertFalse(UtilFile.isFile(file.getPath()));
    }

    /**
     * Test the is type.
     */
    @Test
    public void testIsType()
    {
        Assert.assertFalse(UtilFile.isType(new File("null"), Constant.EMPTY_STRING));
        Assert.assertTrue(UtilFile.isType(Medias.create("file").getFile(), Constant.EMPTY_STRING));
        Assert.assertTrue(UtilFile.isType(Medias.create("file1.txt").getFile(), "txt"));
    }
}
