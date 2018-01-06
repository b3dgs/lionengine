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
package com.b3dgs.lionengine.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Medias;

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

    /** Temp folder. */
    @Rule public final TemporaryFolder TEMP = new TemporaryFolder();

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilFile.class);
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
     * Test the get files.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFiles() throws IOException
    {
        final File folder = TEMP.newFolder("temp");
        final File file1 = File.createTempFile("temp", ".tmp", folder);
        final File file2 = File.createTempFile("temp", ".tmp", folder);
        final File dir = folder;

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
     * Test the get files with <code>null</code> returned.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testGetFilesNull() throws IOException
    {
        Assert.assertNull(UtilFile.getFiles(new File("void")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isDirectory()
            {
                return true;
            }

            @Override
            public File[] listFiles()
            {
                return null;
            }
        }));
    }

    /**
     * Test the get files by extension.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFilesByExtension() throws IOException
    {
        Assert.assertTrue(UtilFile.getFilesByExtension(new File("void"), "txt").isEmpty());

        final File folder = TEMP.newFolder("temp");
        final File file1 = File.createTempFile("temp", ".tmp", folder);
        final File file2 = File.createTempFile("temp", ".txt", folder);

        final TemporaryFolder tempFolder = new TemporaryFolder(folder);
        tempFolder.create();
        final File folder2 = tempFolder.newFolder("temp");
        final File file3 = File.createTempFile("temp", ".txt", folder2);

        final List<File> expected = Arrays.asList(file2, file3);
        final List<File> result = UtilFile.getFilesByExtension(folder, "txt");
        Collections.sort(expected);
        Collections.sort(result);

        Assert.assertEquals(expected, result);

        Assert.assertTrue(file1.delete());
        Assert.assertTrue(file2.delete());
        Assert.assertTrue(file3.delete());
        Assert.assertTrue(folder2.delete());
    }

    /**
     * Test the get files by name.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFilesByName() throws IOException
    {
        Assert.assertTrue(UtilFile.getFilesByName(new File("void"), "name").isEmpty());

        final File folder = TEMP.newFolder("temp");
        final File file1 = File.createTempFile("temp", ".tmp", folder);
        final File file2 = File.createTempFile("temp", ".tmp", folder);

        final TemporaryFolder tempFolder = new TemporaryFolder(folder);
        tempFolder.create();
        final File folder2 = tempFolder.newFolder("temp");
        final File file3 = File.createTempFile("temp", ".tmp", folder2);

        Assert.assertEquals(Arrays.asList(file2), UtilFile.getFilesByName(folder, file2.getName()));

        Assert.assertTrue(file1.delete());
        Assert.assertTrue(file2.delete());
        Assert.assertTrue(file3.delete());
        Assert.assertTrue(folder2.delete());
    }

    /**
     * Test the delete file.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testDeleteFile() throws IOException
    {
        final File file = File.createTempFile("temp", ".tmp");
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

        final File folder = TEMP.newFolder("temp");
        final File file = File.createTempFile("temp", ".tmp", folder);
        final File dir = folder;

        Assert.assertTrue(UtilFile.exists(dir.getPath()));
        Assert.assertTrue(UtilFile.exists(file.getPath()));

        Assert.assertTrue(file.delete());
        Assert.assertTrue(dir.delete());

        Assert.assertFalse(UtilFile.exists(dir.getPath()));
        Assert.assertFalse(UtilFile.exists(file.getPath()));
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

        final File file = File.createTempFile("temp", ".tmp");

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
