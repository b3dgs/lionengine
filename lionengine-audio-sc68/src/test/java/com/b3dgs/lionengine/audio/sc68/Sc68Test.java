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
            Verbose.exception(exception);
            final String message = exception.getMessage();
            Assert.assertTrue(message, message.contains(Sc68Format.ERROR_LOAD_LIBRARY));
            Assume.assumeFalse("Sc68 not supported on test machine - Test skipped",
                               message.contains(Sc68Format.ERROR_LOAD_LIBRARY));
            return null;
        }
    }

    /**
     * Prepare tests.
     */
    @Before
    public void prepare()
    {
        AudioFactory.addFormat(new Sc68Format());
        Medias.setLoadFromJar(Sc68Test.class);
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
        Assert.assertNotNull(AudioFactory.loadAudio(null, Sc68.class));
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
            Assert.assertEquals(AudioVoidFormat.class, Sc68Format.getFailsafe().getClass());
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
        final Sc68 sc68 = createSc68();
        try
        {
            sc68.setVolume(-1);
            Assert.fail();
        }
        finally
        {
            sc68.stop();
        }
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        final Sc68 sc68 = createSc68();
        try
        {
            sc68.setVolume(101);
            Assert.fail();
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
        Assert.assertNotNull(Sc68Format.getFailsafe());
    }

    /**
     * Test play sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 10000L)
    public void testPlay() throws InterruptedException
    {
        final Sc68 sc68 = createSc68();
        try
        {
            Assert.assertTrue(String.valueOf(sc68.getTicks()), sc68.getTicks() >= -1);
            sc68.setVolume(30);
            sc68.play();

            UtilTests.pause(Constant.THOUSAND);

            Assert.assertTrue(String.valueOf(sc68.getTicks()), sc68.getTicks() > -1L);
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
    @Test(timeout = 10000L)
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
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 10000L)
    public void testStart() throws InterruptedException
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

        sc68.setVolume(30);
        sc68.play();

        UtilTests.pause(Constant.HUNDRED);

        sc68.pause();

        UtilTests.pause(Constant.HUNDRED);
        sc68.resume();
        UtilTests.pause(Constant.HUNDRED);
    }

    /**
     * Test loop sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 10000L)
    public void testLoop() throws InterruptedException
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
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 10000L)
    public void testPause() throws InterruptedException
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
    @Test(timeout = 10000L)
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
    @Test(timeout = 10000L, expected = LionEngineException.class)
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
            sc68.play();
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
    @Test(timeout = 10000L)
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
