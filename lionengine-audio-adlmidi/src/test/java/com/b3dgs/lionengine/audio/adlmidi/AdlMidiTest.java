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
package com.b3dgs.lionengine.audio.adlmidi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

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
            Assert.assertTrue(message,
                              message.contains(AdlMidiFormat.ERROR_LOAD_LIBRARY)
                                       || message.contains(AudioFactory.ERROR_FORMAT));
            final boolean skip = message.contains(AdlMidiFormat.ERROR_LOAD_LIBRARY)
                                 || message.contains(AudioFactory.ERROR_FORMAT);
            Assume.assumeFalse("AdlMidi not supported on test machine - Test skipped", skip);
            return null;
        }
    }

    /**
     * Prepare tests.
     */
    @Before
    public void prepare()
    {
        AudioFactory.addFormat(AdlMidiFormat.getFailsafe());
        Medias.setLoadFromJar(AdlMidiTest.class);
    }

    /**
     * Clean up tests.
     */
    @After
    public void cleanUp()
    {
        Medias.setLoadFromJar(null);
        AudioFactory.clearFormats();
    }

    /**
     * Test with <code>null</code> argument.
     */
    @Test(expected = LionEngineException.class)
    public void testNullArgument()
    {
        Assert.assertNotNull(AudioFactory.loadAudio(null, AdlMidi.class));
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
            Assert.assertEquals(AudioVoidFormat.class, AdlMidiFormat.getFailsafe().getClass());
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
    @Test(expected = LionEngineException.class)
    public void testNegativeVolume()
    {
        final AdlMidi adlmidi = createAdlMidi();
        try
        {
            adlmidi.setVolume(-1);
            Assert.fail();
        }
        finally
        {
            adlmidi.stop();
        }
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        final AdlMidi adlmidi = createAdlMidi();
        try
        {
            adlmidi.setVolume(101);
            Assert.fail();
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
        Assert.assertNotNull(AdlMidiFormat.getFailsafe());
    }

    /**
     * Test play sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 10000L)
    public void testPlay() throws InterruptedException
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

            Assert.assertTrue(String.valueOf(adlmidi.getTicks()), adlmidi.getTicks() > -1L);
        }
        finally
        {
            adlmidi.stop();
        }

        adlmidi = createAdlMidi();

        adlmidi.setVolume(30);
        adlmidi.play();

        UtilTests.pause(Constant.HUNDRED);

        adlmidi.pause();

        UtilTests.pause(Constant.HUNDRED);
        adlmidi.resume();
        UtilTests.pause(Constant.HUNDRED);
    }

    /**
     * Test play sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 10000L)
    public void testPlayTwice() throws InterruptedException
    {
        final AdlMidi adlmidi = createAdlMidi();
        try
        {
            adlmidi.play();

            UtilTests.pause(Constant.HUNDRED);

            adlmidi.play();

            UtilTests.pause(Constant.HUNDRED);

            Assert.assertTrue(String.valueOf(adlmidi.getTicks()), adlmidi.getTicks() > -1L);
        }
        finally
        {
            adlmidi.stop();
        }
    }

    /**
     * Test pause sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 10000L)
    public void testPause() throws InterruptedException
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
     * 
     * @throws IOException If error.
     */
    @Test(timeout = 10000, expected = LionEngineException.class)
    public void testMissingMedia() throws IOException
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
            adlmidi.play();
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
    @Test(timeout = 10000L)
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
