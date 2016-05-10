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
package com.b3dgs.lionengine.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the utility folder class.
 */
public class UtilFolderTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(UtilFolderTest.class);
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
        UtilTests.testPrivateConstructor(UtilFolder.class);
    }

    /**
     * Test the get path.
     */
    @Test
    public void testGetPath()
    {
        Assert.assertEquals("a" + File.separator + "b" + File.separator + "c", UtilFolder.getPath("a", "b", "c"));
    }

    /**
     * Test the get path separator.
     */
    @Test
    public void testGetPathSeparator()
    {
        Assert.assertEquals("this%path%next", UtilFolder.getPathSeparator("%", "this", "path", "next"));
        Assert.assertEquals("this%path%next", UtilFolder.getPathSeparator("%", "this%", "path%", "next"));
        Assert.assertEquals("this%path%", UtilFolder.getPathSeparator("%", "this%", "path%", null, ""));
    }

    /**
     * Test the get directories from path.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetDirectories() throws IOException
    {
        final File dir = TEMP.newFolder("temp");
        final File file = File.createTempFile("temp", ".tmp", dir);
        final List<File> expected = new ArrayList<File>();
        for (int i = 0; i < 5; i++)
        {
            final TemporaryFolder folder = new TemporaryFolder(dir);
            folder.create();
            expected.add(folder.getRoot());
        }
        Collections.sort(expected);

        final List<File> found = UtilFolder.getDirectories(dir);
        Collections.sort(found);

        Assert.assertEquals(expected, found);

        Assert.assertTrue(file.delete());
        for (final File current : expected)
        {
            Assert.assertTrue(current.delete());
        }
        Assert.assertTrue(dir.delete());
    }

    /**
     * Test the get directories from wrong path.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetDirectoriesError() throws IOException
    {
        final File dir = TEMP.newFolder("temp");
        try
        {
            Assert.assertTrue(UtilFolder.getDirectories(new File("null")).isEmpty());
        }
        finally
        {
            Assert.assertTrue(dir.delete());
        }
    }

    /**
     * Test the delete directory.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testDeleteDirectory() throws IOException
    {
        final File directory = TEMP.newFolder("temp");
        final File file = File.createTempFile("temp", ".tmp", directory);
        final File dir1 = File.createTempFile("temp", ".tmp", directory);

        Assert.assertTrue(directory.exists());
        Assert.assertTrue(file.exists());
        Assert.assertTrue(dir1.exists());

        UtilFolder.deleteDirectory(directory);

        Assert.assertFalse(directory.exists());
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
        UtilFolder.deleteDirectory(new File("void"));
    }

    /**
     * Test the is directory.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testIsDirectory() throws IOException
    {
        Assert.assertFalse(UtilFolder.isDirectory(null));

        final File dir = TEMP.newFolder("temp");

        Assert.assertTrue(UtilFolder.isDirectory(dir.getPath()));
        Assert.assertTrue(dir.delete());
        Assert.assertFalse(UtilFolder.isDirectory(dir.getPath()));
    }
}
