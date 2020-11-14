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
package com.b3dgs.lionengine.audio.adlmidi;

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilStream;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;

/**
 * Test {@link AdlMidiFormat} and {@link AdlMidiPlayer}.
 */
final class AdlMidiTest
{
    /**
     * Create player.
     * 
     * @return The created player.
     */
    private static AdlMidi createAdlMidi()
    {
        try
        {
            final Media music = Medias.create("music.xmi");
            return AudioFactory.loadAudio(music, AdlMidi.class);
        }
        catch (final LionEngineException exception)
        {
            final String message = exception.getMessage();

            assertTrue(message.contains(AdlMidiFormat.ERROR_LOAD_LIBRARY)
                       || message.contains(AudioFactory.ERROR_FORMAT),
                       message);

            final boolean skip = message.contains(AdlMidiFormat.ERROR_LOAD_LIBRARY)
                                 || message.contains(AudioFactory.ERROR_FORMAT);

            Assumptions.assumeFalse(skip, "AdlMidi not supported on test machine - Test skipped");

            return null;
        }
    }

    /**
     * Prepare test.
     */
    @BeforeEach
    public void beforeTest()
    {
        AudioFactory.addFormat(AdlMidiFormat.getFailsafe());
        Medias.setLoadFromJar(AdlMidiTest.class);
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
        assertThrows(() -> AudioFactory.loadAudio(null, AdlMidi.class), "Unexpected null argument !");
    }

    /**
     * Test with negative volume.
     */
    @Test
    void testNegativeVolume()
    {
        final AdlMidi adlmidi = createAdlMidi();
        try
        {
            assertThrows(() -> adlmidi.setVolume(-1), "Invalid argument: -1 is not superior or equal to 0");
        }
        finally
        {
            adlmidi.stop();
        }
    }

    /**
     * Test with out of range volume.
     */
    @Test
    void testOutOfRangeVolume()
    {
        final AdlMidi adlmidi = createAdlMidi();
        try
        {
            assertThrows(() -> adlmidi.setVolume(101), "Invalid argument: 101 is not inferior or equal to 100");
        }
        finally
        {
            adlmidi.stop();
        }
    }

    /**
     * Test create fail safe.
     */
    @Test
    void testCreateFailsafe()
    {
        assertNotNull(AdlMidiFormat.getFailsafe());
    }

    /**
     * Test play sequence.
     */
    @Test
    void testPlay()
    {
        AdlMidiFormat.setDefaultBank(Integer.valueOf(43));
        final AdlMidi adlmidi = createAdlMidi();
        try
        {
            adlmidi.setVolume(30);

            assertTimeout(5000L, () ->
            {
                adlmidi.play();
                UtilTests.pause(Constant.HUNDRED);

                adlmidi.setBank(0);
                adlmidi.pause();
                UtilTests.pause(Constant.BYTE_4);

                adlmidi.resume();
                UtilTests.pause(Constant.BYTE_4);
            });

            assertTrue(adlmidi.getTicks() > -1L, String.valueOf(adlmidi.getTicks()));
        }
        finally
        {
            adlmidi.stop();
        }

        final AdlMidi adlmidi2 = createAdlMidi();
        try
        {
            adlmidi2.setVolume(30);

            assertTimeout(5000L, () ->
            {
                adlmidi2.play();
                UtilTests.pause(Constant.HUNDRED);

                adlmidi2.pause();
                UtilTests.pause(Constant.BYTE_4);

                adlmidi2.resume();
                UtilTests.pause(Constant.BYTE_4);
            });
        }
        finally
        {
            adlmidi2.stop();
        }
        AdlMidiFormat.setDefaultBank(null);
    }

    /**
     * Test play sequence.
     */
    @Test
    void testPlayTwice()
    {
        final AdlMidi adlmidi = createAdlMidi();
        try
        {
            assertTimeout(5000L, () ->
            {
                adlmidi.play();
                UtilTests.pause(Constant.HUNDRED);

                adlmidi.play();
                UtilTests.pause(Constant.BYTE_4);
            });

            assertTrue(adlmidi.getTicks() > -1L, String.valueOf(adlmidi.getTicks()));
        }
        finally
        {
            adlmidi.stop();
        }
    }

    /**
     * Test pause sequence.
     */
    @Test
    void testPause()
    {
        final AdlMidi adlmidi = createAdlMidi();
        try
        {
            adlmidi.setVolume(30);

            assertTimeout(5000L, () ->
            {
                adlmidi.play();
                UtilTests.pause(Constant.HUNDRED);

                adlmidi.pause();
                UtilTests.pause(Constant.BYTE_4);

                adlmidi.resume();
                UtilTests.pause(Constant.BYTE_4);
            });
        }
        finally
        {
            adlmidi.stop();
        }
    }

    /**
     * Test with missing media.
     */
    @Test
    void testMissingMedia()
    {
        final Media media = new Media()
        {
            @Override
            public String getPath()
            {
                return "void.xmi";
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
        final Audio adlmidi = AudioFactory.loadAudio(media);
        try
        {
            assertCause(() -> adlmidi.play(), IOException.class);
        }
        finally
        {
            adlmidi.stop();
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
        final Media music = Medias.create("music.xmi");
        try (InputStream input = music.getInputStream())
        {
            Medias.setLoadFromJar(null);
            Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));

            final Media media = Medias.create("music.xmi");
            try (OutputStream output = media.getOutputStream())
            {
                UtilStream.copy(input, output);
            }

            final Audio adlmidi = AudioFactory.loadAudio(media);
            try
            {
                adlmidi.setVolume(50);

                assertTimeout(5000L, () ->
                {
                    adlmidi.play();
                    UtilTests.pause(Constant.HUNDRED);
                });
            }
            finally
            {
                adlmidi.stop();
            }

            UtilFile.deleteFile(media.getFile());
        }
        finally
        {
            Medias.setResourcesDirectory(null);
        }
    }
}
