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
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test {@link Media}.
 */
final class MediaTest
{
    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(MediaTest.class.getSimpleName(), Version.DEFAULT));
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
     * Test path getter.
     */
    @Test
    void testPath()
    {
        Medias.setLoadFromJar(MediaTest.class);
        final String path = "path";

        assertEquals(path, Medias.create(path).getPath());
    }

    /**
     * Test file path getter.
     */
    @Test
    void testFile()
    {
        Medias.setLoadFromJar(MediaTest.class);
        final String path = "image.png";

        assertEquals(new File(MediaTest.class.getResource(path).getFile()), Medias.create(path).getFile());
    }

    /**
     * Test URL getter.
     */
    @Test
    void testUrl()
    {
        Medias.setLoadFromJar(MediaTest.class);
        final String path = "image.png";

        assertEquals(MediaTest.class.getResource(path), Medias.create(path).getUrl());
        assertThrows(() -> Medias.create("void").getUrl(), "[void] " + MediaDefault.ERROR_OPEN_MEDIA);
    }

    /**
     * Test parent path getter.
     */
    @Test
    void testParentPath()
    {
        Medias.setLoadFromJar(MediaTest.class);
        final String path = "image.png";

        assertEquals(Constant.EMPTY_STRING, Medias.create(path).getParentPath());
    }

    /**
     * Test input stream.
     * 
     * @throws IOException If error.
     */
    @Test
    void testInputStream() throws IOException
    {
        Medias.setLoadFromJar(MediaTest.class);
        final Media media = Medias.create("image.png");

        assertTrue(media.exists());

        try (InputStream input = media.getInputStream())
        {
            assertNotNull(input);
        }
    }

    /**
     * Test input stream with no existing file.
     */
    @Test
    void testInputStreamNotExists()
    {
        Medias.setResourcesDirectory(null);
        final Media media = Medias.create("void");

        assertFalse(media.exists());
        assertThrows(() -> media.getInputStream(), "[void] " + MediaDefault.ERROR_OPEN_MEDIA);
    }

    /**
     * Test input stream with no existing in JAR.
     * 
     * @throws IOException If error.
     */
    @Test
    void testInputStreamNotExistsInJar() throws IOException
    {
        Medias.setLoadFromJar(MediaTest.class);
        final File file = File.createTempFile("void", "");
        final File dir = new File(file.getParentFile(), MediaTest.class.getSimpleName());
        dir.mkdir();
        final File file2 = File.createTempFile("void", "", dir);
        final Media media = Medias.create(file2.getName());

        try (InputStream input = media.getInputStream())
        {
            assertNotNull(input);
        }
        finally
        {
            assertTrue(file.delete());
            assertTrue(file2.delete());

            UtilFolder.deleteDirectory(dir);
        }
    }

    /**
     * Test output stream.
     * 
     * @throws IOException If error.
     */
    @Test
    void testOutputStream() throws IOException
    {
        Medias.setLoadFromJar(MediaTest.class);
        final Media media = Medias.create(String.valueOf(System.nanoTime()), "test");

        try (OutputStream output = media.getOutputStream())
        {
            assertNotNull(output);
        }
        finally
        {
            assertTrue(media.getFile().exists());
            assertTrue(media.getFile().delete());
            assertFalse(media.getFile().exists());
            assertTrue(media.getFile().getParentFile().delete());
            assertTrue(media.getFile().getParentFile().getParentFile().delete());
        }
    }

    /**
     * Test media existence.
     * 
     * @throws IOException If error.
     */
    @Test
    void testExists() throws IOException
    {
        Medias.setLoadFromJar(MediasTest.class);

        assertFalse(Medias.create("void").exists());
        assertTrue(Medias.create("image.png").exists());

        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));

        final File file = File.createTempFile("test", ".txt", new File(Medias.getResourcesDirectory()));
        final Media media = Medias.get(file);

        assertTrue(file.exists());
        assertTrue(media.exists());

        assertTrue(file.delete());
        assertFalse(media.exists());
    }

    /**
     * Test get medias.
     */
    @Test
    void testGetMedias()
    {
        assertTrue(Medias.create("null", "void").getMedias().isEmpty());
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final Media media = Medias.create("media");
        final Media media1 = Medias.create("media");
        final Media media2 = Medias.create("media2");

        assertEquals(media.hashCode(), media.hashCode());
        assertEquals(media.hashCode(), media1.hashCode());
        assertNotEquals(media.hashCode(), media2.hashCode());
        assertNotEquals(media.hashCode(), new Object().hashCode());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final Media media = Medias.create("media");
        final Media media1 = Medias.create("media");
        final Media media2 = Medias.create("media2");

        assertEquals(media, media);
        assertEquals(media, media1);
        assertNotEquals(media, media2);
        assertNotEquals(media, null);
        assertNotEquals(media, new Object());
    }

    /**
     * Test get name.
     */
    @Test
    void testGetName()
    {
        Medias.setLoadFromJar(MediaTest.class);
        final Media media = Medias.create("test", "media.ext");

        assertEquals("media.ext", media.getName());
    }
}
