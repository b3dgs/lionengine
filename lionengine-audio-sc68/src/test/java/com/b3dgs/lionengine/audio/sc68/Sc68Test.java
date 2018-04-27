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
package com.b3dgs.lionengine.audio.sc68;

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
 * Test {@link Sc68Format} and {@link Sc68Player}.
 */
public final class Sc68Test
{
    /**
     * Create sc68 player.
     * 
     * @return The created player.
     */
    private static Sc68 createSc68()
    {
        try
        {
            final Media music = Medias.create("music.sc68");
            return AudioFactory.loadAudio(music, Sc68.class);
        }
        catch (final LionEngineException exception)
        {
            final String message = exception.getMessage();

            assertTrue(message.contains(Sc68Format.ERROR_LOAD_LIBRARY) || message.contains(AudioFactory.ERROR_FORMAT),
                       message);

            final boolean skip = message.contains(Sc68Format.ERROR_LOAD_LIBRARY)
                                 || message.contains(AudioFactory.ERROR_FORMAT);

            Assumptions.assumeFalse(skip, "Sc68 not supported on test machine - Test skipped");

            return null;
        }
    }

    /**
     * Prepare test.
     */
    @BeforeEach
    public void beforeTest()
    {
        AudioFactory.addFormat(new Sc68Format());
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
    public void testNullArgument()
    {
        assertThrows(() -> AudioFactory.loadAudio(null, Sc68.class), "Unexpected null argument !");
    }

    /**
     * Test with missing library.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testMissingLibrary() throws Exception
    {
        final Field field = Sc68Format.class.getDeclaredField("LIBRARY_NAME");
        final String back = UtilReflection.getField(Sc68Format.class, "LIBRARY_NAME");
        try
        {
            UtilEnum.setStaticFinal(field, "void");
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");

            assertEquals(AudioVoidFormat.class, Sc68Format.getFailsafe().getClass());

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
    public void testOutOfRangeVolume()
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
    public void testCreateFailsafe()
    {
        assertNotNull(Sc68Format.getFailsafe());
    }

    /**
     * Test play sequence.
     */
    @Test
    public void testPlay()
    {
        final Sc68 sc68 = createSc68();
        try
        {
            assertTrue(sc68.getTicks() >= -1, String.valueOf(sc68.getTicks()));
            sc68.setVolume(30);
            sc68.play();

            UtilTests.pause(Constant.THOUSAND);

            assertTrue(sc68.getTicks() > -1L, String.valueOf(sc68.getTicks()));
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
    public void testPlayTwice() throws InterruptedException
    {
        final Sc68 sc68 = createSc68();
        try
        {
            sc68.play();

            UtilTests.pause(Constant.HUNDRED);

            sc68.play();

            UtilTests.pause(Constant.HUNDRED);
        }
        finally
        {
            sc68.stop();
        }
    }

    /**
     * Test start sequence.
     */
    @Test
    public void testStart()
    {
        Sc68 sc68 = createSc68();
        try
        {
            sc68.setVolume(30);
            sc68.setStart(0);
            sc68.play();

            UtilTests.pause(Constant.HUNDRED);
        }
        finally
        {
            sc68.stop();
        }

        sc68 = createSc68();
        try
        {
            sc68.setVolume(30);
            sc68.play();

            UtilTests.pause(Constant.HUNDRED);

            sc68.pause();

            UtilTests.pause(Constant.HUNDRED);
            sc68.resume();
            UtilTests.pause(Constant.HUNDRED);
        }
        finally
        {
            sc68.stop();
        }
    }

    /**
     * Test loop sequence.
     */
    @Test
    public void testLoop()
    {
        final Sc68 sc68 = createSc68();
        try
        {
            sc68.setVolume(30);
            sc68.setLoop(0, 100);
            sc68.play();

            UtilTests.pause(Constant.HUNDRED);
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
    public void testPause()
    {
        final Sc68 sc68 = createSc68();
        try
        {
            sc68.setVolume(30);
            sc68.play();

            UtilTests.pause(Constant.HUNDRED);

            sc68.pause();
            UtilTests.pause(Constant.BYTE_4);
            sc68.resume();

            UtilTests.pause(Constant.HUNDRED);
        }
        finally
        {
            sc68.stop();
        }
    }

    /**
     * Test the set configuration.
     */
    @Test
    public void testConfig()
    {
        final Sc68 sc68 = createSc68();
        try
        {
            sc68.setVolume(30);
            sc68.setConfig(false, false);

            sc68.play();
            UtilTests.pause(Constant.HUNDRED);

            sc68.setConfig(false, true);

            UtilTests.pause(Constant.HUNDRED);

            sc68.setConfig(true, false);

            UtilTests.pause(Constant.HUNDRED);

            sc68.setConfig(true, true);

            UtilTests.pause(Constant.HUNDRED);
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
    public void testMissingMedia() throws IOException
    {
        final Media media = new Media()
        {
            @Override
            public String getPath()
            {
                return "void.sc68";
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

            final Audio sc68 = AudioFactory.loadAudio(media);
            try
            {
                sc68.setVolume(50);
                sc68.play();
                UtilTests.pause(Constant.HUNDRED);
            }
            finally
            {
                sc68.stop();
            }

            UtilFile.deleteFile(media.getFile());
        }
        finally
        {
            Medias.setResourcesDirectory(null);
        }
    }
}
