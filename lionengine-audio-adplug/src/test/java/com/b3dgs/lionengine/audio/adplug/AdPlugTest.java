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
package com.b3dgs.lionengine.audio.adplug;

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
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.util.UtilEnum;
import com.b3dgs.lionengine.util.UtilFile;
import com.b3dgs.lionengine.util.UtilReflection;
import com.b3dgs.lionengine.util.UtilStream;
import com.b3dgs.lionengine.util.UtilTests;

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
            Assert.assertTrue(message,
                              message.contains(AdPlugFormat.ERROR_LOAD_LIBRARY)
                                       || message.contains(AudioFactory.ERROR_FORMAT));
            final boolean skip = message.contains(AdPlugFormat.ERROR_LOAD_LIBRARY)
                                 || message.contains(AudioFactory.ERROR_FORMAT);
            Assume.assumeFalse("AdPlug not supported on test machine - Test skipped", skip);
            return null;
        }
    }

    /**
     * Prepare tests.
     */
    @Before
    public void prepare()
    {
        AudioFactory.addFormat(AdPlugFormat.getFailsafe());
        Medias.setLoadFromJar(AdPlugTest.class);
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
        Assert.assertNotNull(AudioFactory.loadAudio(null, AdPlug.class));
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
            Assert.assertEquals(AudioVoidFormat.class, AdPlugFormat.getFailsafe().getClass());
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
        final AdPlug adplug = createAdPlug();
        try
        {
            adplug.setVolume(-1);
            Assert.fail();
        }
        finally
        {
            adplug.stop();
        }
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        final AdPlug adplug = createAdPlug();
        try
        {
            adplug.setVolume(101);
            Assert.fail();
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
        Assert.assertNotNull(AdPlugFormat.getFailsafe());
    }

    /**
     * Test play sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 10000)
    public void testPlay() throws InterruptedException
    {
        AdPlug adplug = createAdPlug();
        try
        {
            adplug.setVolume(30);
            adplug.play();

            UtilTests.pause(Constant.HUNDRED);

            adplug.pause();

            UtilTests.pause(Constant.HUNDRED);
            adplug.resume();
            UtilTests.pause(Constant.HUNDRED);
        }
        finally
        {
            adplug.stop();
        }

        adplug = createAdPlug();

        adplug.setVolume(30);
        adplug.play();

        UtilTests.pause(Constant.HUNDRED);

        adplug.pause();

        UtilTests.pause(Constant.HUNDRED);
        adplug.resume();
        UtilTests.pause(Constant.HUNDRED);
    }

    /**
     * Test play sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 10000)
    public void testPlayTwice() throws InterruptedException
    {
        final AdPlug adplug = createAdPlug();
        try
        {
            adplug.play();

            UtilTests.pause(Constant.HUNDRED);

            adplug.play();

            UtilTests.pause(Constant.HUNDRED);
        }
        finally
        {
            adplug.stop();
        }
    }

    /**
     * Test pause sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 10000)
    public void testPause() throws InterruptedException
    {
        final AdPlug adplug = createAdPlug();
        try
        {
            adplug.setVolume(30);
            adplug.play();

            UtilTests.pause(Constant.HUNDRED);

            adplug.pause();
            UtilTests.pause(Constant.BYTE_4);
            adplug.resume();

            UtilTests.pause(Constant.HUNDRED);
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
    @Test(timeout = 10000, expected = LionEngineException.class)
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
    @Test(timeout = 10000)
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
                adplug.play();
                UtilTests.pause(Constant.HUNDRED);
            }
            finally
            {
                adplug.stop();
            }

            UtilFile.deleteFile(media.getFile());
        }
        finally
        {
            Medias.setResourcesDirectory(Constant.EMPTY_STRING);
        }
    }
}
