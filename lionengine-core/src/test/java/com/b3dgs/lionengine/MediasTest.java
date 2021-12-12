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
import static com.b3dgs.lionengine.UtilAssert.assertThrowsNpe;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test {@link Medias}.
 */
final class MediasTest
{
    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(MediasTest.class.getSimpleName(), Version.DEFAULT));
    }

    /**
     * Terminate engine.
     */
    @AfterAll
    static void afterAll()
    {
        Engine.terminate();
    }

    /** Old resources directory. */
    private String oldDir;
    /** Old loader. */
    private Class<?> oldLoader;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void beforeTest()
    {
        oldDir = Medias.getResourcesDirectory();
        oldLoader = Medias.getResourcesLoader();
        Medias.setFactoryMedia(new FactoryMediaDefault());
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void afterTest()
    {
        Medias.setResourcesDirectory(oldDir);
        Medias.setLoadFromJar(oldLoader);
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(Medias.class);
    }

    /**
     * Test create media with <code>null</code> argument.
     */
    @Test
    void testCreateMediaNull()
    {
        Medias.setResourcesDirectory("rsc");

        assertThrowsNpe(() -> Medias.create((String) null).getPath());
    }

    /**
     * Test create media from resources directory.
     */
    @Test
    void testCreateMediaResources()
    {
        Medias.setResourcesDirectory("rsc");

        assertEquals("rsc" + Medias.getSeparator(), Medias.getResourcesDirectory());

        final Media media = Medias.create("test.txt");

        assertEquals("", media.getParentPath());
        assertEquals("test.txt", media.getPath());
        assertEquals(System.getProperty("java.io.tmpdir")
                     + MediasTest.class.getSimpleName()
                     + java.io.File.separator
                     + "test.txt",
                     media.getFile().getPath());

        final Media media2 = Medias.create("test", "toto.txt");

        assertEquals("test", media2.getParentPath());
        assertEquals("test" + Constant.SLASH + "toto.txt", media2.getPath());
        assertEquals(System.getProperty("java.io.tmpdir")
                     + MediasTest.class.getSimpleName()
                     + java.io.File.separator
                     + "test"
                     + java.io.File.separator
                     + "toto.txt",
                     media2.getFile().getPath());
    }

    /**
     * Test create media from loader.
     */
    @Test
    void testCreateMediaLoader()
    {
        Medias.setLoadFromJar(MediasTest.class);

        assertEquals(MediasTest.class, Medias.getResourcesLoader());

        final Media media = Medias.create("test.txt");

        assertEquals("", media.getParentPath());
        assertEquals("test.txt", media.getPath());
        assertTrue(media.getFile().getPath().endsWith("test.txt"));

        final Media media2 = Medias.create("test", "toto.txt");

        assertEquals("test", media2.getParentPath());
        assertEquals("test/toto.txt", media2.getPath());
        assertTrue(media2.getFile().getPath().endsWith("test" + File.separator + "toto.txt"));
    }

    /**
     * Test get with suffix.
     */
    @Test
    void testGetWithSuffix()
    {
        Medias.setResourcesDirectory(oldDir);
        final Media folder = Medias.create("folder", "foo");
        final Media file = Medias.create("folder", "file.txt");

        assertEquals(Medias.create("folder", "foo_suffix"), Medias.getWithSuffix(folder, "suffix"));
        assertEquals(Medias.create("folder", "file_suffix.txt"), Medias.getWithSuffix(file, "suffix"));
    }

    /**
     * Test get file.
     */
    @Test
    void testGetFile()
    {
        Medias.setResourcesDirectory("rsc");
        final File file = new File("rsc" + File.separator + "test.txt");
        final Media media = Medias.get(file);

        assertEquals(new File(System.getProperty("java.io.tmpdir")
                              + MediasTest.class.getSimpleName()
                              + java.io.File.separator
                              + "test.txt"),
                     media.getFile());
        assertEquals("test.txt", media.getPath());
    }

    /**
     * Test get URL.
     * 
     * @throws MalformedURLException If error.
     */
    @Test
    void testGetUrl() throws MalformedURLException
    {
        Medias.setLoadFromJar(MediasTest.class);
        final Media media = Medias.create("file1.txt");

        assertEquals(MediasTest.class.getResource(media.getName()), media.getUrl());
    }

    /**
     * Test get medias by extension.
     * 
     * @throws IOException If error.
     */
    @Test
    void testGetByExtensionFolder() throws IOException
    {
        final Path temp = Files.createTempDirectory(MediasTest.class.getSimpleName());
        final Path file = Files.createTempFile(temp, "temp", ".png");
        Files.createTempFile(temp, "temp", ".txt");
        try
        {
            Medias.setResourcesDirectory(temp.toFile().getAbsolutePath());
            final Collection<Media> medias = Medias.getByExtension("png", Medias.create(""));

            assertEquals(file.toFile().getName(), medias.iterator().next().getPath());
        }
        finally
        {
            UtilFolder.deleteDirectory(temp.toFile());
        }
    }

    /**
     * Test get medias by extension in JAR.
     */
    @Test
    void testGetByExtensionJar()
    {
        Medias.setLoadFromJar(MediasTest.class);
        final Collection<Media> medias = Medias.getByExtension(Medias.create("resources.jar").getFile(),
                                                               "com/b3dgs/lionengine/",
                                                               21,
                                                               "png");

        assertEquals("image.png", medias.iterator().next().getPath());
    }

    /**
     * Test get medias.
     */
    @Test
    void testGetMedias()
    {
        Medias.setResourcesDirectory(null);

        assertFalse(Medias.create("").getMedias().isEmpty());
    }

    /**
     * Test get medias on file
     */
    @Test
    void testGetMediasOnFile()
    {
        assertTrue(Medias.create("image.png").getMedias().isEmpty());
    }

    /**
     * Test separator.
     */
    @Test
    void testSeparator()
    {
        assertEquals(Constant.SLASH, Medias.getSeparator());
    }

    /**
     * Test media to string.
     */
    @Test
    void testToString()
    {
        Medias.setResourcesDirectory("prefix");
        final Media media = Medias.create("path", "file.ext");

        assertEquals("path" + Medias.getSeparator() + "file.ext", media.toString());
    }
}
