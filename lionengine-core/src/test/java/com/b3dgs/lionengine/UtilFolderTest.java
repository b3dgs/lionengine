/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test {@link UtilFolder}.
 */
final class UtilFolderTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(UtilFolderTest.class);
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test the constructor.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilFolder.class);
    }

    /**
     * Test the get path.
     */
    @Test
    void testGetPath()
    {
        assertEquals("a" + Constant.SLASH + "b" + Constant.SLASH + "c", UtilFolder.getPath("a", "b", "c"));
    }

    /**
     * Test the get path separator.
     */
    @Test
    void testGetPathSeparator()
    {
        assertEquals("this%path%next", UtilFolder.getPathSeparator("%", "this", "path", "next"));
        assertEquals("this%path%next", UtilFolder.getPathSeparator("%", "this%", "path%", "next"));
        assertEquals("this%path%", UtilFolder.getPathSeparator("%", "this%", "path%", null, ""));
    }

    /**
     * Test the get directories from path.
     * 
     * @throws IOException If error.
     */
    @Test
    void testGetDirectories() throws IOException
    {
        final Path dir = Files.createTempDirectory("temp");
        final Path file = Files.createTempFile(dir, "temp", ".tmp");
        final List<File> expected = new ArrayList<>();
        for (int i = 0; i < 5; i++)
        {
            expected.add(Files.createTempDirectory(dir, "temp").toFile());
        }
        Collections.sort(expected);

        final List<File> found = UtilFolder.getDirectories(dir.toFile());
        Collections.sort(found);

        assertEquals(expected, found);

        Files.delete(file);

        for (final File current : expected)
        {
            assertTrue(current.delete());
        }

        Files.delete(dir);
    }

    /**
     * Test the get directories from wrong path.
     */
    @Test
    void testGetDirectoriesError()
    {
        assertTrue(UtilFolder.getDirectories(new File("null")).isEmpty());
    }

    /**
     * Test the delete directory.
     * 
     * @throws IOException If error.
     */
    @Test
    void testDeleteDirectory() throws IOException
    {
        final Path directory = Files.createTempDirectory("temp");
        final Path file = Files.createTempFile(directory, "temp", ".tmp");
        final Path dir1 = Files.createTempFile(directory, "temp", ".tmp");

        assertTrue(Files.exists(directory));
        assertTrue(Files.exists(file));
        assertTrue(Files.exists(dir1));

        UtilFolder.deleteDirectory(directory.toFile());

        assertTrue(Files.notExists(directory));
        assertTrue(Files.notExists(file));
        assertTrue(Files.notExists(dir1));
    }

    /**
     * Test the delete directory warning.
     * 
     * @throws IOException If error.
     */
    @Test
    void testDeleteDirectoryWarning() throws IOException
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        UtilFolder.deleteDirectory(new File("void"));
        UtilFolder.deleteDirectory(new File("warn")
        {
            @Override
            public boolean isDirectory()
            {
                return true;
            }

            @Override
            public boolean delete()
            {
                return false;
            }

            @Override
            public File[] listFiles()
            {
                return new File[]
                {
                    new File("warn")
                    {
                        @Override
                        public boolean isFile()
                        {
                            return true;
                        }

                        @Override
                        public boolean delete()
                        {
                            return false;
                        }
                    }
                };
            }
        });
        UtilFolder.deleteDirectory(new File("null")
        {
            @Override
            public boolean isDirectory()
            {
                return true;
            }

            @Override
            public boolean delete()
            {
                return false;
            }

            @Override
            public File[] listFiles()
            {
                return null;
            }
        });
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test the is directory.
     * 
     * @throws IOException If error.
     */
    @Test
    void testIsDirectory() throws IOException
    {
        assertFalse(UtilFolder.isDirectory(null));

        final Path dir = Files.createTempDirectory("temp");

        assertTrue(UtilFolder.isDirectory(dir.toFile().getPath()));

        Files.delete(dir);

        assertFalse(UtilFolder.isDirectory(dir.toFile().getPath()));
    }
}
