/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.core.Media;

/**
 * Test the utility file class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityFileTest
{
    /**
     * Get a media.
     * 
     * @param file The media file.
     * @return The media path.
     */
    private static Media get(String file)
    {
        return Media.create(Media.getPath("src", "test", "resources", file));
    }

    /**
     * Get a media.
     * 
     * @param file The media file.
     * @param file2 The media file last.
     * @return The media path.
     */
    private static Media get(String file, String file2)
    {
        return Media.create(Media.getPath("src", "test", "resources", file, file2));
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
        final Media media = UtilityFileTest.get(file);
        final String path = media.getPath();
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

        final File fileDir = new File(Media.getPath("src", "test", "resources", "directory"));
        if (fileDir.mkdir())
        {
            try
            {
                final String[] dirs = UtilityFile.getDirsList(UtilityFileTest.get("").getPath());
                Assert.assertEquals(1, dirs.length);
                Assert.assertEquals("directory", dirs[0]);
                Assert.assertEquals(0, UtilityFile.getDirsList(UtilityFileTest.get("null").getPath()).length);

                final String[] files = UtilityFile.getFilesList(Media.getPath("src", "test", "resources"));
                Assert.assertEquals(8, files.length);
                Assert.assertEquals(0, UtilityFile.getFilesList(UtilityFileTest.get("null").getPath()).length);
                Assert.assertEquals(0, UtilityFile.getFilesList(UtilityFileTest.get("null").getPath(), "txt").length);
                Assert.assertEquals(1, UtilityFile.getFilesList(UtilityFileTest.get("").getPath(), "wav").length);
            }
            finally
            {
                UtilityFile.deleteDirectory(fileDir);
            }
        }

        final Media dir = UtilityFileTest.get("temp");
        final Media test = UtilityFileTest.get("temp", "test");
        final File tempDir = new File(dir.getPath());
        final File testFile = new File(test.getPath());
        final boolean created = tempDir.mkdirs();
        try
        {
            Assert.assertTrue(created);
            try
            {
                Assert.assertTrue(testFile.createNewFile());
            }
            catch (final IOException exception)
            {
                Assert.fail();
            }
        }
        finally
        {
            UtilityFile.deleteDirectory(tempDir);
        }

        final File fileTest = new File(UtilityFileTest.get("file").getPath());
        if (fileTest.createNewFile())
        {
            UtilityFile.deleteFile(fileTest);
        }

        Assert.assertEquals("path" + File.separator + "test", Media.getPath("path", "test"));
    }
}
