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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    @Before
    public void prepare()
    {
        oldDir = Medias.getResourcesDirectory();
        oldLoader = Medias.getResourcesLoader();
        Medias.setFactoryMedia(new FactoryMediaDefault());
    }

    /**
     * Clean test.
     */
    @After
    public void cleanUp()
    {
        Medias.setResourcesDirectory(oldDir);
        Medias.setLoadFromJar(oldLoader.orElse(null));
    }

    /**
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(Medias.class);
    }

    /**
     * Test create media with <code>null</code> argument.
     */
    @Test
    public void testCreateMediaNull()
    {
        Medias.setResourcesDirectory("rsc");

        Assert.assertEquals("null", Medias.create((String) null).getPath());
    }

    /**
     * Test create media from resources directory.
     */
    @Test
    public void testCreateMediaResources()
    {
        Medias.setResourcesDirectory("rsc");

        Assert.assertEquals("rsc" + Medias.getSeparator(), Medias.getResourcesDirectory());

        final Media media = Medias.create("test.txt");

        Assert.assertEquals("", media.getParentPath());
        Assert.assertEquals("test.txt", media.getPath());
        Assert.assertEquals("rsc" + File.separator + "test.txt", media.getFile().getPath());

        final Media media2 = Medias.create("test", "toto.txt");

        Assert.assertEquals("test", media2.getParentPath());
        Assert.assertEquals("test" + File.separator + "toto.txt", media2.getPath());
        Assert.assertEquals("rsc" + File.separator + "test" + File.separator + "toto.txt", media2.getFile().getPath());
    }

    /**
     * Test create media from loader.
     */
    @Test
    public void testCreateMediaLoader()
    {
        Medias.setLoadFromJar(MediasTest.class);

        Assert.assertEquals(MediasTest.class, Medias.getResourcesLoader().get());

        final Media media = Medias.create("test.txt");

        Assert.assertEquals("", media.getParentPath());
        Assert.assertEquals("test.txt", media.getPath());
        Assert.assertTrue(media.getFile().getPath().endsWith("test.txt"));

        final Media media2 = Medias.create("test", "toto.txt");

        Assert.assertEquals("test", media2.getParentPath());
        Assert.assertEquals("test/toto.txt", media2.getPath());
        Assert.assertTrue(media2.getFile().getPath().endsWith("test" + File.separator + "toto.txt"));
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

        Assert.assertEquals(Medias.create("folder", "foo_suffix"), Medias.getWithSuffix(folder, "suffix"));
        Assert.assertEquals(Medias.create("folder", "file_suffix.txt"), Medias.getWithSuffix(file, "suffix"));
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

        Assert.assertEquals(file, media.getFile());
        Assert.assertEquals("test.txt", media.getPath());
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

        Assert.assertEquals(new File(jarPath.substring(0, jarSeparatorIndex)), jar);
    }

    /**
     * Test get JAR resources file without loader enabled.
     */
    @Test(expected = LionEngineException.class)
    public void testGetJarResourcesNoLoader()
    {
        Medias.setLoadFromJar(null);

        Assert.assertNull(Medias.getJarResources());
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

        Assert.assertEquals(jarPath.substring(jarSeparatorIndex), resourcesPrefix);
    }

    /**
     * Test get JAR resources prefix file without loader enabled.
     */
    @Test(expected = LionEngineException.class)
    public void testGetJarResourcesPrefixNoLoader()
    {
        Medias.setLoadFromJar(null);

        Assert.assertNull(Medias.getJarResourcesPrefix());
    }

    /**
     * Test get medias by extension.
     */
    @Test
    public void testGetByExtension()
    {
        Medias.setLoadFromJar(MediasTest.class);
        final Collection<Media> medias = Medias.getByExtension("png", Medias.create(""));

        Assert.assertEquals("image.png", medias.iterator().next().getPath());
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

        Assert.assertEquals(file.toFile().getName(), medias.iterator().next().getPath());

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

        Assert.assertEquals("image.png", medias.iterator().next().getPath());
    }

    /**
     * Test get medias from JAR.
     */
    @Test
    public void testGetMediasJar()
    {
        Medias.setLoadFromJar(MediasTest.class);

        Assert.assertTrue(Medias.create("").getMedias().contains(Medias.create("image.png")));
    }

    /**
     * Test get medias.
     */
    @Test
    public void testGetMedias()
    {
        Medias.setResourcesDirectory(null);

        Assert.assertFalse(Medias.create("").getMedias().isEmpty());
    }

    /**
     * Test get medias on file
     */
    @Test(expected = LionEngineException.class)
    public void testGetMediasOnFile()
    {
        Assert.assertNull(Medias.create("image.png").getMedias());
    }

    /**
     * Test separator.
     */
    @Test
    public void testSeparator()
    {
        final String old = Medias.getSeparator();
        Medias.setSeparator("%");

        Assert.assertEquals("%", Medias.getSeparator());

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

        Assert.assertEquals("path" + Medias.getSeparator() + "file.ext", media.toString());
    }
}
