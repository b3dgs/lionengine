/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.AudioFactory;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test the AdPlug player.
 */
public class AdPlugTest
{
    /** Binding. */
    private static AdPlug adplug;
    /** Media music. */
    private static Media music;

    /**
     * Prepare tests.
     */
    @BeforeClass
    public static void prepare()
    {
        try
        {
            AudioFactory.addFormat(new AdPlugFormat());
            Medias.setLoadFromJar(AdPlugTest.class);
            music = Medias.create("music.lds");
            adplug = AudioFactory.loadAudio(music, AdPlug.class);
        }
        catch (final LionEngineException exception)
        {
            final String message = exception.getMessage();
            Assert.assertTrue(message, message.contains(AdPlugFormat.ERROR_LOAD_LIBRARY));
            Assume.assumeFalse("AdPlug not supported on test machine - Test skipped",
                               message.contains(AdPlugFormat.ERROR_LOAD_LIBRARY));
        }
    }

    /**
     * Clean up tests.
     */
    @AfterClass
    public static void cleanUp()
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
}
