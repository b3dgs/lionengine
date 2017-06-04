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
import com.b3dgs.lionengine.audio.AudioFormat;
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
    /** Binding. */
    private AdPlug adplug;
    /** Media music. */
    private Media music;

    /**
     * Prepare tests.
     */
    @Before
    public void prepare()
    {
        try
        {
            final AudioFormat<?> format = AdPlugFormat.getFailsafe();
            AudioFactory.addFormat(format);
            Medias.setLoadFromJar(AdPlugTest.class);
            music = Medias.create("music.lds");
            adplug = AudioFactory.loadAudio(music, AdPlug.class);
            format.close();
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
        }
    }

    /**
     * Clean up tests.
     */
    @After
    public void cleanUp()
    {
        Medias.setLoadFromJar(null);
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
        adplug.setVolume(-1);
        Assert.fail();
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        adplug.setVolume(101);
        Assert.fail();
    }

    /**
     * Test AdPlug sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testAdPlug() throws InterruptedException
    {
        adplug.setVolume(40);
        adplug.play();
        Thread.sleep(500);
        adplug.pause();
        Thread.sleep(500);
        adplug.setVolume(60);
        adplug.resume();
        Thread.sleep(500);
        adplug.stop();
    }

    /**
     * Test AdPlug stress.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testStress() throws InterruptedException
    {
        adplug.play();
        adplug.stop();
        adplug.play();
        Thread.sleep(Constant.HUNDRED);
        adplug.stop();
        adplug.play();
        adplug.pause();
        adplug.resume();
        for (int i = 0; i < 5; i++)
        {
            adplug.play();
            Thread.sleep(Constant.HUNDRED);
        }
        Thread.sleep(250);
        adplug.stop();
        adplug.play();
        adplug.stop();
    }

    /**
     * Test Sc68 with outside media.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testOutsideMedia() throws IOException
    {
        InputStream input = null;
        OutputStream output = null;
        try
        {
            input = music.getInputStream();

            Medias.setLoadFromJar(null);
            Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));

            final Media media = Medias.create("music.lds");
            output = media.getOutputStream();
            UtilStream.copy(input, output);

            final Audio audio = AudioFactory.loadAudio(media);
            audio.setVolume(50);
            audio.play();
            UtilTests.pause(100L);
            audio.stop();

            UtilStream.safeClose(output);
            UtilFile.deleteFile(media.getFile());
        }
        finally
        {
            Medias.setResourcesDirectory(Constant.EMPTY_STRING);
            UtilStream.safeClose(input);
            UtilStream.safeClose(output);
        }
    }
}
