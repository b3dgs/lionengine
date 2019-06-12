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
package com.b3dgs.lionengine.audio.adlmidi;

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
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
 * Test {@link AdlMidiFormat} and {@link AdlMidiPlayer}.
 */
public final class AdlMidiTest
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
    public void testNullArgument()
    {
        assertThrows(() -> AudioFactory.loadAudio(null, AdlMidi.class), "Unexpected null argument !");
    }

    /**
     * Test with missing library.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testMissingLibrary() throws Exception
    {
        final Field field = AdlMidiFormat.class.getDeclaredField("LIBRARY_NAME");
        final String back = UtilReflection.getField(AdlMidiFormat.class, "LIBRARY_NAME");
        try
        {
            UtilEnum.setStaticFinal(field, "void");
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");

            assertEquals(AudioVoidFormat.class, AdlMidiFormat.getFailsafe().getClass());

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
    public void testOutOfRangeVolume()
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
    public void testCreateFailsafe()
    {
        assertNotNull(AdlMidiFormat.getFailsafe());
    }

    /**
     * Test play sequence.
     */
    @Test
    public void testPlay()
    {
        AdlMidi adlmidi = createAdlMidi();
        try
        {
            adlmidi.setVolume(30);
            adlmidi.setBank(43);
            adlmidi.play();

            UtilTests.pause(Constant.HUNDRED);

            adlmidi.pause();

            UtilTests.pause(Constant.HUNDRED);
            adlmidi.resume();
            UtilTests.pause(Constant.HUNDRED);

            assertTrue(adlmidi.getTicks() > -1L, String.valueOf(adlmidi.getTicks()));
        }
        finally
        {
            adlmidi.stop();
        }

        adlmidi = createAdlMidi();
        try
        {
            adlmidi.setVolume(30);
            adlmidi.play();

            UtilTests.pause(Constant.HUNDRED);

            adlmidi.pause();

            UtilTests.pause(Constant.HUNDRED);
            adlmidi.resume();
            UtilTests.pause(Constant.HUNDRED);
        }
        finally
        {
            adlmidi.stop();
        }
    }

    /**
     * Test play sequence.
     */
    @Test
    public void testPlayTwice()
    {
        final AdlMidi adlmidi = createAdlMidi();
        try
        {
            adlmidi.play();
            UtilTests.pause(Constant.HUNDRED);

            adlmidi.play();
            UtilTests.pause(Constant.HUNDRED);

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
    public void testPause()
    {
        final AdlMidi adlmidi = createAdlMidi();
        try
        {
            adlmidi.setVolume(30);

            adlmidi.play();
            UtilTests.pause(Constant.HUNDRED);

            adlmidi.pause();
            UtilTests.pause(Constant.BYTE_4);

            adlmidi.resume();
            UtilTests.pause(Constant.HUNDRED);
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
    public void testMissingMedia()
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
    public void testOutsideMedia() throws IOException
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
                adlmidi.play();
                UtilTests.pause(Constant.HUNDRED);
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
