/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.adplug;

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
 * Test {@link AdPlugFormat} and {@link AdPlugPlayer}.
 */
final class AdPlugTest
{
    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(AdPlugTest.class.getSimpleName(), new Version(1, 0, 0)));
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
     * Create player.
     * 
     * @return The created player.
     */
    private static AdPlug createAdPlug()
    {
        return createAdPlug(Medias.create("music.lds"));
    }

    /**
     * Create player.
     * 
     * @param media The media reference.
     * @return The created player.
     */
    private static AdPlug createAdPlug(Media media)
    {
        return AudioFactory.loadAudio(media, AdPlug.class);
    }

    /**
     * Prepare test.
     */
    @BeforeEach
    public void beforeTest()
    {
        try
        {
            AudioFactory.addFormat(new AdPlugFormat());
        }
        catch (final LionEngineException exception)
        {
            final String message = exception.getMessage();

            assertTrue(message.contains(AdPlugFormat.ERROR_LOAD_LIBRARY) || message.contains(AudioFactory.ERROR_FORMAT),
                       message);

            final boolean skip = message.contains(AdPlugFormat.ERROR_LOAD_LIBRARY)
                                 || message.contains(AudioFactory.ERROR_FORMAT);

            Assumptions.assumeFalse(skip, "AdPlug not supported on test machine - Test skipped");
        }
        Medias.setLoadFromJar(AdPlugTest.class);
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
        assertThrows(() -> AudioFactory.loadAudio(null, AdPlug.class), "Unexpected null argument !");
    }

    /**
     * Test with negative volume.
     */
    @Test
    void testNegativeVolume()
    {
        final AdPlug adplug = createAdPlug();
        try
        {
            assertThrows(() -> adplug.setVolume(-1), "Invalid argument: -1 is not superior or equal to 0");
        }
        finally
        {
            adplug.stop();
        }
    }

    /**
     * Test with out of range volume.
     */
    @Test
    void testOutOfRangeVolume()
    {
        final AdPlug adplug = createAdPlug();
        try
        {
            assertThrows(() -> adplug.setVolume(101), "Invalid argument: 101 is not inferior or equal to 100");
        }
        finally
        {
            adplug.stop();
        }
    }

    /**
     * Test create fail safe.
     */
    @Test
    void testCreateFailsafe()
    {
        assertNotNull(AdPlugFormat.getFailsafe());
    }

    /**
     * Test play sequence.
     */
    @Test
    void testPlay()
    {
        final AdPlug adplug = createAdPlug();
        try
        {
            adplug.setVolume(30);

            assertTimeout(5000L, () ->
            {
                adplug.play();
                UtilTests.pause(Constant.HUNDRED);

                adplug.pause();
                UtilTests.pause(Constant.BYTE_4);

                adplug.resume();
                UtilTests.pause(Constant.BYTE_4);
            });
        }
        finally
        {
            adplug.stop();
        }

        final AdPlug adplug2 = createAdPlug();
        try
        {
            adplug2.setVolume(30);

            assertTimeout(5000L, () ->
            {
                adplug2.play();
                UtilTests.pause(Constant.HUNDRED);

                adplug2.pause();
                UtilTests.pause(Constant.BYTE_4);
                adplug2.resume();

                UtilTests.pause(Constant.BYTE_4);
            });
        }
        finally
        {
            adplug2.stop();
        }
    }

    /**
     * Test play sequence.
     */
    @Test
    void testPlayTwice()
    {
        final AdPlug adplug = createAdPlug();
        try
        {
            assertTimeout(5000L, () ->
            {
                adplug.play();
                UtilTests.pause(Constant.HUNDRED);

                adplug.play();
                UtilTests.pause(Constant.BYTE_4);
            });
        }
        finally
        {
            adplug.stop();
        }
    }

    /**
     * Test pause sequence.
     */
    @Test
    void testPause()
    {
        final AdPlug adplug = createAdPlug();
        try
        {
            adplug.setVolume(30);

            assertTimeout(5000L, () ->
            {
                adplug.play();
                UtilTests.pause(Constant.HUNDRED);

                adplug.pause();
                UtilTests.pause(Constant.BYTE_4);

                adplug.resume();
                UtilTests.pause(Constant.BYTE_4);
            });
        }
        finally
        {
            adplug.stop();
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
                return "void.lds";
            }

            @Override
            public File getFile()
            {
                return new File(getPath());
            }
        };
        final Audio audio = createAdPlug(media);
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
        final Media music = Medias.create("music.lds");
        try (InputStream input = music.getInputStream())
        {
            Medias.setLoadFromJar(null);
            Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));

            final Media media = Medias.create("music.lds");
            try (OutputStream output = media.getOutputStream())
            {
                UtilStream.copy(input, output);
            }
            final Audio adplug = createAdPlug(media);
            try
            {
                adplug.setVolume(50);

                assertTimeout(5000L, () ->
                {
                    adplug.play();
                    UtilTests.pause(Constant.HUNDRED);
                });
            }
            finally
            {
                adplug.stop();
            }
        }
        finally
        {
            Medias.setResourcesDirectory(null);
        }
        if (music.getFile().exists())
        {
            UtilFile.deleteFile(music.getFile());
        }
    }
}
