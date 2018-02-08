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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

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
 * Test the AdPlug player.
 */
public class AdPlugTest
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
    @Test(timeout = 1000, expected = LionEngineException.class)
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
    @Test(timeout = 1000, expected = LionEngineException.class)
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
    @Test(timeout = 1000, expected = LionEngineException.class)
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
     * Test play sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 1000)
    public void testPlay() throws InterruptedException
    {
        final AdPlug adplug = createAdPlug();
        try
        {
            adplug.setVolume(30);
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
    @Test(timeout = 1000)
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
     * Test AdPlug with outside media.
     * 
     * @throws IOException If error.
     */
    @Test(timeout = 1000)
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
