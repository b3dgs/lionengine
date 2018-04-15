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
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test {@link Medias}.
 */
public final class MediasTest
{
    /** Old resources directory. */
    private String oldDir;
    /** Old loader. */
    private Optional<Class<?>> oldLoader;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        oldDir = Medias.getResourcesDirectory();
        oldLoader = Medias.getResourcesLoader();
        Medias.setFactoryMedia(new FactoryMediaDefault());
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void cleanUp()
    {
        Medias.setResourcesDirectory(oldDir);
        Medias.setLoadFromJar(oldLoader.orElse(null));
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        assertPrivateConstructor(Medias.class);
    }

    /**
     * Test create media with <code>null</code> argument.
     */
    @Test
    public void testCreateMediaNull()
    {
        Medias.setResourcesDirectory("rsc");

        assertEquals("null", Medias.create((String) null).getPath());
    }

    /**
     * Test create media from resources directory.
     */
    @Test
    public void testCreateMediaResources()
    {
        Medias.setResourcesDirectory("rsc");

        assertEquals("rsc" + Medias.getSeparator(), Medias.getResourcesDirectory());

        final Media media = Medias.create("test.txt");

        assertEquals("", media.getParentPath());
        assertEquals("test.txt", media.getPath());
        assertEquals("rsc" + File.separator + "test.txt", media.getFile().getPath());

        final Media media2 = Medias.create("test", "toto.txt");

        assertEquals("test", media2.getParentPath());
        assertEquals("test" + File.separator + "toto.txt", media2.getPath());
        assertEquals("rsc" + File.separator + "test" + File.separator + "toto.txt", media2.getFile().getPath());
    }

    /**
     * Test create media from loader.
     */
    @Test
    public void testCreateMediaLoader()
    {
        Medias.setLoadFromJar(MediasTest.class);

        assertEquals(MediasTest.class, Medias.getResourcesLoader().get());

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
    public void testGetWithSuffix()
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
    public void testGetFile()
    {
        Medias.setResourcesDirectory("rsc");
        final File file = new File("rsc" + File.separator + "test.txt");
        final Media media = Medias.get(file);

        assertEquals(file, media.getFile());
        assertEquals("test.txt", media.getPath());
    }

    /**
     * Test get JAR resources file.
     */
    @Test
    public void testGetJarResources()
    {
        Medias.setLoadFromJar(MediasTest.class);

        final File folder = Medias.create(com.b3dgs.lionengine.Constant.EMPTY_STRING).getFile();
        final String prefix = Medias.getResourcesLoader().get().getPackage().getName().replace(Constant.DOT,
                                                                                               Constant.SLASH);
        final String jarPath = folder.getPath().replace(File.separator, Constant.SLASH);
        final int jarSeparatorIndex = jarPath.indexOf(prefix);
        final File jar = Medias.getJarResources();

        assertEquals(new File(jarPath.substring(0, jarSeparatorIndex)), jar);
    }

    /**
     * Test get JAR resources file without loader enabled.
     */
    @Test
    public void testGetJarResourcesNoLoader()
    {
        Medias.setLoadFromJar(null);

        assertThrows(() -> Medias.getJarResources(), Medias.JAR_LOADER_ERROR);
    }

    /**
     * Test get JAR resources prefix.
     */
    @Test
    public void testGetJarResourcesPrefix()
    {
        Medias.setLoadFromJar(MediasTest.class);

        final File folder = Medias.create(com.b3dgs.lionengine.Constant.EMPTY_STRING).getFile();
        final String prefix = Medias.getResourcesLoader().get().getPackage().getName().replace(Constant.DOT,
                                                                                               Constant.SLASH);
        final String jarPath = folder.getPath().replace(File.separator, Constant.SLASH);
        final int jarSeparatorIndex = jarPath.indexOf(prefix);
        final String resourcesPrefix = Medias.getJarResourcesPrefix();

        assertEquals(jarPath.substring(jarSeparatorIndex), resourcesPrefix);
    }

    /**
     * Test get JAR resources prefix file without loader enabled.
     */
    @Test
    public void testGetJarResourcesPrefixNoLoader()
    {
        Medias.setLoadFromJar(null);

        assertThrows(() -> Medias.getJarResourcesPrefix(), Medias.JAR_LOADER_ERROR);
    }

    /**
     * Test get medias by extension.
     */
    @Test
    public void testGetByExtension()
    {
        Medias.setLoadFromJar(MediasTest.class);
        final Collection<Media> medias = Medias.getByExtension("png", Medias.create(""));

        assertEquals("image.png", medias.iterator().next().getPath());
    }

    /**
     * Test get medias by extension.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testGetByExtensionFolder() throws IOException
    {
        final Path temp = Files.createTempDirectory(MediasTest.class.getSimpleName());
        final Path file = Files.createTempFile(temp, "temp", ".png");
        Files.createTempFile(temp, "temp", ".txt");
        Medias.setResourcesDirectory(temp.toFile().getAbsolutePath());
        final Collection<Media> medias = Medias.getByExtension("png", Medias.create(""));

        assertEquals(file.toFile().getName(), medias.iterator().next().getPath());

        UtilFolder.deleteDirectory(temp.toFile());
    }

    /**
     * Test get medias by extension in JAR.
     */
    @Test
    public void testGetByExtensionJar()
    {
        Medias.setLoadFromJar(MediasTest.class);
        final Collection<Media> medias = Medias.getByExtension(Medias.create("resources.jar").getFile(),
                                                               "com/b3dgs/lionengine/",
                                                               21,
                                                               "png");

        assertEquals("image.png", medias.iterator().next().getPath());
    }

    /**
     * Test get medias from JAR.
     */
    @Test
    public void testGetMediasJar()
    {
        Medias.setLoadFromJar(MediasTest.class);

        assertTrue(Medias.create("").getMedias().contains(Medias.create("image.png")));
    }

    /**
     * Test get medias.
     */
    @Test
    public void testGetMedias()
    {
        Medias.setResourcesDirectory(null);

        assertFalse(Medias.create("").getMedias().isEmpty());
    }

    /**
     * Test get medias on file
     */
    @Test
    public void testGetMediasOnFile()
    {
        assertThrows(() -> Medias.create("image.png").getMedias(), "[image.png] " + MediaDefault.ERROR_PATH_DIR);
    }

    /**
     * Test separator.
     */
    @Test
    public void testSeparator()
    {
        final String old = Medias.getSeparator();
        Medias.setSeparator("%");

        assertEquals("%", Medias.getSeparator());

        Medias.setSeparator(old);
    }

    /**
     * Test media to string.
     */
    @Test
    public void testToString()
    {
        Medias.setResourcesDirectory("prefix");
        final Media media = Medias.create("path", "file.ext");

        assertEquals("path" + Medias.getSeparator() + "file.ext", media.toString());
    }
}
