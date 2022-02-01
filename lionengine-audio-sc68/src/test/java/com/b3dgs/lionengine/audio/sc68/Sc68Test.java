/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.sc68;

import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.MediaMock;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilStream;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;

/**
 * Test {@link Sc68Format} and {@link Sc68Player}.
 */
final class Sc68Test
{
    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(Sc68Test.class.getSimpleName(), Version.DEFAULT));
    }

    /**
     * Terminate engine.
     */
    @AfterAll
    static void afterAll()
    {
        Engine.terminate();
    }

    /**
     * Create sc68 player.
     * 
     * @return The created player.
     */
    private static Sc68 createSc68()
    {
        return createSc68(Medias.create("music.sc68"));
    }

    /**
     * Create sc68 player.
     * 
     * @param media The media reference.
     * @return The created player.
     */
    private static Sc68 createSc68(Media media)
    {
        return AudioFactory.loadAudio(media, Sc68.class);
    }

    /**
     * Prepare test.
     */
    @BeforeEach
    public void beforeTest()
    {
        try
        {
            AudioFactory.addFormat(new Sc68Format());
        }
        catch (final LionEngineException exception)
        {
            final String message = exception.getMessage();

            assertTrue(message.contains(Sc68Format.ERROR_LOAD_LIBRARY) || message.contains(AudioFactory.ERROR_FORMAT),
                       message);

            final boolean skip = message.contains(Sc68Format.ERROR_LOAD_LIBRARY)
                                 || message.contains(AudioFactory.ERROR_FORMAT);

            Assumptions.assumeFalse(skip, "Sc68 not supported on test machine - Test skipped");
        }
        Medias.setLoadFromJar(Sc68Test.class);
    }

    /**
     * Clean up test.
     */
    @AfterEach
    public void afterTest()
    {
        Medias.setLoadFromJar(null);
        AudioFactory.clearFormats();
    }

    /**
     * Test with <code>null</code> argument.
     */
    @Test
    void testNullArgument()
    {
        assertThrows(() -> AudioFactory.loadAudio(null, Sc68.class), "Unexpected null argument !");
    }

    /**
     * Test with negative volume.
     */
    @Test
    void testNegativeVolume()
    {
        final Sc68 sc68 = createSc68();
        try
        {
            assertThrows(() -> sc68.setVolume(-1), "Invalid argument: -1 is not superior or equal to 0");
        }
        finally
        {
            sc68.stop();
        }
    }

    /**
     * Test with out of range volume.
     */
    @Test
    void testOutOfRangeVolume()
    {
        final Sc68 sc68 = createSc68();
        try
        {
            assertThrows(() -> sc68.setVolume(101), "Invalid argument: 101 is not inferior or equal to 100");
        }
        finally
        {
            sc68.stop();
        }
    }

    /**
     * Test create fail safe.
     */
    @Test
    void testCreateFailsafe()
    {
        assertNotNull(Sc68Format.getFailsafe());
    }

    /**
     * Test play sequence.
     */
    @Test
    void testPlay()
    {
        final Sc68 sc68 = createSc68();
        try
        {
            assertTrue(sc68.getTicks() >= -1, String.valueOf(sc68.getTicks()));

            sc68.setVolume(30);

            assertTimeout(5000L, () ->
            {
                sc68.play();
                UtilTests.pause(Constant.HUNDRED);
            });
        }
        finally
        {
            sc68.stop();
        }
    }

    /**
     * Test play sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    void testPlayTwice() throws InterruptedException
    {
        final Sc68 sc68 = createSc68();
        try
        {
            assertTimeout(5000L, () ->
            {
                sc68.play();
                UtilTests.pause(Constant.HUNDRED);

                sc68.play();
                UtilTests.pause(Constant.BYTE_4);
            });
        }
        finally
        {
            sc68.stop();
        }
    }

    /**
     * Test pause sequence.
     */
    @Test
    void testPause()
    {
        final Sc68 sc68 = createSc68();
        try
        {
            sc68.setVolume(30);

            assertTimeout(5000L, () ->
            {
                sc68.play();
                UtilTests.pause(Constant.HUNDRED);

                sc68.pause();
                UtilTests.pause(Constant.BYTE_4);
                sc68.resume();

                UtilTests.pause(Constant.BYTE_4);
            });
        }
        finally
        {
            sc68.stop();
        }
    }

    /**
     * Test with missing media.
     * 
     * @throws IOException If error.
     */
    @Test
    void testMissingMedia() throws IOException
    {
        final Media media = new MediaMock()
        {
            @Override
            public String getPath()
            {
                return "void.sc68";
            }

            @Override
            public File getFile()
            {
                return new File(getPath());
            }
        };
        final Audio audio = createSc68(media);
        try
        {
            audio.play();
        }
        finally
        {
            audio.stop();
            final File file = new File(new File(System.getProperty("java.io.tmpdir"), getClass().getSimpleName()),
                                       media.getPath());
            if (file.exists())
            {
                UtilFile.deleteFile(file);
            }
        }
    }

    /**
     * Test with outside media.
     * 
     * @throws IOException If error.
     */
    @Test
    void testOutsideMedia() throws IOException
    {
        final Media music = Medias.create("music.sc68");
        try (InputStream input = music.getInputStream())
        {
            Medias.setLoadFromJar(null);
            Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));

            final Media media = Medias.create("music.sc68");
            try (OutputStream output = media.getOutputStream())
            {
                UtilStream.copy(input, output);
            }

            final Audio sc68 = createSc68(media);
            try
            {
                sc68.setVolume(50);

                assertTimeout(5000L, () ->
                {
                    sc68.play();
                    UtilTests.pause(Constant.HUNDRED);
                });
            }
            finally
            {
                sc68.stop();
            }

            if (media.getFile().exists())
            {
                UtilFile.deleteFile(media.getFile());
            }
        }
        finally
        {
            Medias.setResourcesDirectory(null);
        }
    }
}
