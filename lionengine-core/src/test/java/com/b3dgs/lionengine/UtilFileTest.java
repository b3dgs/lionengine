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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertIterableEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test {@link UtilFile}.
 */
public final class UtilFileTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(UtilFileTest.class);
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
     * Test constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilFile.class);
    }

    /**
     * Test remove extension.
     */
    @Test
    public void testRemoveExtension()
    {
        assertEquals("temp", UtilFile.removeExtension("temp.tmp"));
        assertEquals("temp", UtilFile.removeExtension("temp."));
        assertEquals("temp", UtilFile.removeExtension("temp"));
    }

    /**
     * Test remove extension <code>null</code>.
     */
    @Test
    public void testRemoveExtensionNull()
    {
        assertThrows(() -> UtilFile.removeExtension(null), Check.ERROR_NULL);
    }

    /**
     * Test normalize extension.
     */
    @Test
    public void testNormalizeExtension()
    {
        assertEquals("temp.tmp", UtilFile.normalizeExtension("temp", "tmp"));
        assertEquals("temp.tmp", UtilFile.normalizeExtension("temp", ".tmp"));
        assertEquals("temp.tmp", UtilFile.normalizeExtension("temp", "..tmp"));
        assertEquals("temp.tmp", UtilFile.normalizeExtension("temp.tmp", ".tmp"));
        assertEquals("temp.tmp", UtilFile.normalizeExtension("temp.txt", ".tmp"));
    }

    /**
     * Test normalized extension with <code>null</code> file.
     */
    @Test
    public void testNormalizeExtensionNullFile()
    {
        assertThrows(() -> UtilFile.normalizeExtension(null, ""), Check.ERROR_NULL);
    }

    /**
     * Test normalized extension with <code>null</code> extension.
     */
    @Test
    public void testNormalizeExtensionNullExtension()
    {
        assertThrows(() -> UtilFile.normalizeExtension("", null), Check.ERROR_NULL);
    }

    /**
     * Test get extension.
     */
    @Test
    public void testGetExtension()
    {
        assertEquals("", UtilFile.getExtension("tmp"));
        assertEquals("tmp", UtilFile.getExtension(".tmp"));
        assertEquals("tmp", UtilFile.getExtension("temp.tmp"));
        assertEquals("t", UtilFile.getExtension("temp.t"));
        assertEquals(Constant.EMPTY_STRING, UtilFile.getExtension("temp."));
        assertEquals(Constant.EMPTY_STRING, UtilFile.getExtension("temp"));
    }

    /**
     * Test get extension <code>null</code>.
     */
    @Test
    public void testGetExtensionNull()
    {
        assertThrows(() -> UtilFile.getExtension((String) null), Check.ERROR_NULL);
    }

    /**
     * Test get extension on file.
     */
    @Test
    public void testGetExtensionFile()
    {
        assertEquals("tmp", UtilFile.getExtension(new File("temp.tmp")));
        assertEquals("t", UtilFile.getExtension(new File("toto", "temp.t")));
        assertEquals(Constant.EMPTY_STRING, UtilFile.getExtension(new File("temp.")));
        assertEquals(Constant.EMPTY_STRING, UtilFile.getExtension(new File("temp")));
    }

    /**
     * Test get extension file <code>null</code>.
     */
    @Test
    public void testGetExtensionFileNull()
    {
        assertThrows(() -> UtilFile.getExtension((File) null), Check.ERROR_NULL);
    }

    /**
     * Test get files.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFiles() throws IOException
    {
        final Path folder = Files.createTempDirectory("temp");
        final Path file1 = Files.createTempFile(folder, "temp", ".tmp");
        final Path file2 = Files.createTempFile(folder, "temp", ".tmp");
        final File dir = folder.toFile();

        final List<File> found = new ArrayList<>(UtilFile.getFiles(dir));
        final List<File> expected = Arrays.asList(file1.toFile(), file2.toFile());
        Collections.sort(found);
        Collections.sort(expected);

        assertEquals(expected, found);

        Files.delete(file1);
        Files.delete(file2);
        Files.delete(folder);
    }

    /**
     * Test get files with wrong path.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFilesError() throws IOException
    {
        assertThrows(() -> UtilFile.getFiles(new File("void")), UtilFile.ERROR_DIRECTORY + "void");
    }

    /**
     * Test get files with <code>null</code> returned.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFilesNull() throws IOException
    {
        assertThrows(() -> UtilFile.getFiles(new File("void")
        {
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
        }), UtilFile.ERROR_DIRECTORY + "void");
    }

    /**
     * Test get files by extension.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFilesByExtension() throws IOException
    {
        assertTrue(UtilFile.getFilesByExtension(new File("void"), "txt").isEmpty());

        final Path folder = Files.createTempDirectory("temp");
        final Path file1 = Files.createTempFile(folder, "temp", ".tmp");
        final Path file2 = Files.createTempFile(folder, "temp", ".txt");

        final Path folder2 = Files.createTempDirectory(folder, "temp");
        final Path file3 = Files.createTempFile(folder2, "temp", ".txt");

        final List<File> expected = Arrays.asList(file2.toFile(), file3.toFile());
        final List<File> result = UtilFile.getFilesByExtension(folder.toFile(), "txt");
        Collections.sort(expected);
        Collections.sort(result);

        assertIterableEquals(expected, result);

        Files.delete(file1);
        Files.delete(file2);
        Files.delete(file3);
        Files.delete(folder2);
        Files.delete(folder);
    }

    /**
     * Test get files by name.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetFilesByName() throws IOException
    {
        assertTrue(UtilFile.getFilesByName(new File("void"), "name").isEmpty());

        final Path folder = Files.createTempDirectory("temp");
        final Path file1 = Files.createTempFile(folder, "temp", ".tmp");
        final Path file2 = Files.createTempFile(folder, "temp", ".txt");

        final Path folder2 = Files.createTempDirectory(folder, "temp");
        final Path file3 = Files.createTempFile(folder2, "temp", ".tmp");

        assertIterableEquals(Arrays.asList(file2.toFile()),
                             UtilFile.getFilesByName(folder.toFile(), file2.toFile().getName()));

        Files.delete(file1);
        Files.delete(file2);
        Files.delete(file3);
        Files.delete(folder2);
        Files.delete(folder);
    }

    /**
     * Test delete file.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testDeleteFile() throws IOException
    {
        final File file = Files.createTempFile("temp", ".tmp").toFile();

        assertTrue(file.exists());

        UtilFile.deleteFile(file);

        assertFalse(file.exists());
    }

    /**
     * Test delete file error.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testDeleteFileError() throws IOException
    {
        assertThrows(() -> UtilFile.deleteFile(new File("void")),
                     UtilFile.ERROR_DELETE_FILE + new File("void").getAbsolutePath());
    }

    /**
     * Test exists path.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testExists() throws IOException
    {
        assertFalse(UtilFile.exists(null));

        final Path folder = Files.createTempDirectory("temp");
        final Path file = Files.createTempFile(folder, "temp", ".tmp");

        assertTrue(UtilFile.exists(folder.toFile().getPath()));
        assertTrue(UtilFile.exists(file.toFile().getPath()));

        Files.delete(file);
        Files.delete(folder);

        assertFalse(UtilFile.exists(folder.toFile().getPath()));
        assertFalse(UtilFile.exists(file.toFile().getPath()));
    }

    /**
     * Test is file.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testIsFile() throws IOException
    {
        assertFalse(UtilFile.isFile(null));

        final File file = Files.createTempFile("temp", ".tmp").toFile();

        assertTrue(UtilFile.isFile(file.getPath()));
        assertTrue(file.delete());
        assertFalse(UtilFile.isFile(file.getPath()));
    }

    /**
     * Test is type.
     */
    @Test
    public void testIsType()
    {
        assertFalse(UtilFile.isType(null, Constant.EMPTY_STRING));
        assertFalse(UtilFile.isType(new File("null"), Constant.EMPTY_STRING));
        assertFalse(UtilFile.isType(new File(System.getProperty("java.io.tmpdir")), Constant.EMPTY_STRING));
        assertTrue(UtilFile.isType(Medias.create("file").getFile(), Constant.EMPTY_STRING));
        assertTrue(UtilFile.isType(Medias.create("file1.txt").getFile(), "txt"));
    }
}
