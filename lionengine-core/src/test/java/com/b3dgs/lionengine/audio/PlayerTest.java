/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio;

import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.MediaMock;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.io.InputStreamMock;

/**
 * Test {@link PlayerAbstract}.
 */
public final class PlayerTest
{
    /**
     * Clean up tests.
     */
    @AfterEach
    public void afterTests()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test player from JAR.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testPlayerFromJar() throws IOException
    {
        Medias.setLoadFromJar(PlayerTest.class);
        final Media media = Medias.create("image.png");
        final PlayerMock player = new PlayerMock(media);
        player.play();

        final String mediaPath = media.getFile().getAbsolutePath();
        final String playedPrefix = player.played.get().substring(player.played.get().lastIndexOf(File.separator) + 1,
                                                                  player.played.get().lastIndexOf(Constant.DOT));
        final String mediaPrefix = mediaPath.substring(mediaPath.lastIndexOf(File.separator) + 1,
                                                       mediaPath.lastIndexOf(Constant.DOT));

        assertTrue(playedPrefix.startsWith(mediaPrefix), playedPrefix + "!=" + mediaPrefix);

        Files.delete(new File(player.played.get()).toPath());
    }

    /**
     * Test player with cached file.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testPlayerCached() throws IOException
    {
        Medias.setLoadFromJar(PlayerTest.class);
        final Media media = Medias.create("image.png");
        final PlayerMock player = new PlayerMock(media);
        player.play();
        player.play();

        final String mediaPath = media.getFile().getAbsolutePath();
        final String playedPrefix = player.played.get().substring(player.played.get().lastIndexOf(File.separator) + 1,
                                                                  player.played.get().lastIndexOf(Constant.DOT));
        final String mediaPrefix = mediaPath.substring(mediaPath.lastIndexOf(File.separator) + 1,
                                                       mediaPath.lastIndexOf(Constant.DOT));

        assertTrue(playedPrefix.startsWith(mediaPrefix), playedPrefix + "!=" + mediaPrefix);

        Files.delete(new File(player.played.get()).toPath());
    }

    /**
     * Test player loading from disk.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testPlayerNoResourceLoader() throws IOException
    {
        final Path file = Files.createTempFile("image", ".png");
        Medias.setResourcesDirectory(file.getParent().toFile().getAbsolutePath());
        try
        {
            final Media media = Medias.get(file.toFile());
            final PlayerMock player = new PlayerMock(media);
            player.play();

            final String mediaPath = media.getFile().getAbsolutePath();
            final String playedPrefix = player.played.get()
                                                     .substring(player.played.get().lastIndexOf(File.separator) + 1,
                                                                player.played.get().lastIndexOf(Constant.DOT));
            final String mediaPrefix = mediaPath.substring(mediaPath.lastIndexOf(File.separator) + 1,
                                                           mediaPath.lastIndexOf(Constant.DOT));

            assertTrue(playedPrefix.startsWith(mediaPrefix), playedPrefix + "!=" + mediaPrefix);
        }
        finally
        {
            Files.delete(file);
        }
    }

    /**
     * Test player with invalid media.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testPlayerInvalidMedia() throws IOException
    {
        Medias.setLoadFromJar(PlayerTest.class);
        final Media media = new MediaFail();
        final PlayerMock player = new PlayerMock(media);

        assertThrows(() -> player.play(), IOException.class.getName());
    }

    /**
     * Media fail mock.
     */
    private static final class MediaFail extends MediaMock
    {
        @Override
        public File getFile()
        {
            try
            {
                final Path path = Files.createTempFile("test", "fail");
                final File file = new File(path.toFile().getParentFile(), "mediafail");
                Files.delete(path);
                return file;
            }
            catch (final IOException exception)
            {
                throw new LionEngineException(exception);
            }
        }

        @Override
        public InputStream getInputStream()
        {
            return new InputStreamMock()
            {
                @Override
                public void close() throws IOException
                {
                    throw new IOException();
                }
            };
        }
    }
}
