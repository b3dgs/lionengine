/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilEnum;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.UtilStream;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;

/**
 * Test {@link AdPlugFormat} and {@link AdPlugPlayer}.
 */
public final class AdPlugTest
{
    /**
     * Create player.
     * 
     * @return The created player.
     */
    private static AdPlug createAdPlug()
    {
        try
        {
            final Media music = Medias.create("music.lds");
            return AudioFactory.loadAudio(music, AdPlug.class);
        }
        catch (final LionEngineException exception)
        {
            final String message = exception.getMessage();

            assertTrue(message.contains(AdPlugFormat.ERROR_LOAD_LIBRARY) || message.contains(AudioFactory.ERROR_FORMAT),
                       message);

            final boolean skip = message.contains(AdPlugFormat.ERROR_LOAD_LIBRARY)
                                 || message.contains(AudioFactory.ERROR_FORMAT);

            Assumptions.assumeFalse(skip, "AdPlug not supported on test machine - Test skipped");

            return null;
        }
    }

    /**
     * Prepare test.
     */
    @BeforeEach
    public void beforeTest()
    {
        AudioFactory.addFormat(AdPlugFormat.getFailsafe());
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
    public void testNullArgument()
    {
        assertThrows(() -> AudioFactory.loadAudio(null, AdPlug.class), "Unexpected null argument !");
    }

    /**
     * Test with missing library.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testMissingLibrary() throws Exception
    {
        final Field field = AdPlugFormat.class.getDeclaredField("LIBRARY_NAME");
        final String back = UtilReflection.getField(AdPlugFormat.class, "LIBRARY_NAME");
        try
        {
            UtilEnum.setStaticFinal(field, "void");
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");

            assertEquals(AudioVoidFormat.class, AdPlugFormat.getFailsafe().getClass());

            Verbose.info("****************************************************************************************");
        }
        finally
        {
            UtilEnum.setStaticFinal(field, back);
        }
    }

    /**
     * Test with negative volume.
     */
    @Test
    public void testNegativeVolume()
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
    public void testOutOfRangeVolume()
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
    public void testCreateFailsafe()
    {
        assertNotNull(AdPlugFormat.getFailsafe());
    }

    /**
     * Test play sequence.
     */
    @Test
    public void testPlay()
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
    public void testPlayTwice()
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
    public void testPause()
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
    public void testMissingMedia() throws IOException
    {
        final Media media = new Media()
        {
            @Override
            public String getPath()
            {
                return "void.lds";
            }

            @Override
            public String getParentPath()
            {
                return null;
            }

            @Override
            public OutputStream getOutputStream()
            {
                return null;
            }

            @Override
            public String getName()
            {
                return null;
            }

            @Override
            public Collection<Media> getMedias()
            {
                return null;
            }

            @Override
            public InputStream getInputStream()
            {
                return new InputStream()
                {
                    @Override
                    public int read() throws IOException
                    {
                        return -1;
                    }

                    @Override
                    public void close() throws IOException
                    {
                        throw new IOException();
                    }
                };
            }

            @Override
            public File getFile()
            {
                return new File(getPath());
            }

            @Override
            public boolean exists()
            {
                return false;
            }
        };
        final Audio sc68 = AudioFactory.loadAudio(media);
        try
        {
            assertCause(() -> sc68.play(), IOException.class);
        }
        finally
        {
            sc68.stop();
        }
    }

    /**
     * Test with outside media.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testOutsideMedia() throws IOException
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

            final Audio adplug = AudioFactory.loadAudio(media);
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

            UtilFile.deleteFile(media.getFile());
        }
        finally
        {
            Medias.setResourcesDirectory(null);
        }
    }
}
