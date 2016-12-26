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
package com.b3dgs.lionengine.audio;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test the sc68 player.
 */
public class Sc68Test
{
    /** Binding. */
    private static Sc68 sc68;
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
            AudioFactory.addFormat(new Sc68Format());
            Medias.setLoadFromJar(Sc68Test.class);
            music = Medias.create("music.sc68");
            sc68 = AudioFactory.loadAudio(music, Sc68.class);
        }
        catch (final LionEngineException exception)
        {
            final String message = exception.getMessage();
            Assert.assertTrue(message, message.contains(Sc68Format.ERROR_LOAD_LIBRARY));
            Assume.assumeFalse("Sc68 not supported on test machine - Test skipped",
                               message.contains(Sc68Format.ERROR_LOAD_LIBRARY));
        }
    }

    /**
     * Clean up tests.
     */
    @AfterClass
    public static void cleanUp()
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
     * Test with negative volume.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeVolume()
    {
        sc68.setVolume(-1);
        Assert.fail();
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        sc68.setVolume(101);
        Assert.fail();
    }

    /**
     * Test Sc68 sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testSc68() throws InterruptedException
    {
        sc68.setVolume(15);
        sc68.setConfig(true, false);
        sc68.play();
        Thread.sleep(500);
        sc68.setConfig(false, false);
        sc68.pause();
        Thread.sleep(500);
        sc68.setConfig(false, true);
        sc68.setVolume(30);
        sc68.resume();
        Thread.sleep(500);
        sc68.setConfig(true, true);
        Assert.assertTrue(sc68.getTicks() >= 0);
        sc68.stop();
    }

    /**
     * Test Sc68 stress.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testStress() throws InterruptedException
    {
        sc68.play();
        sc68.stop();
        sc68.play();
        Thread.sleep(Constant.HUNDRED);
        sc68.stop();
        sc68.play();
        sc68.pause();
        sc68.resume();
        for (int i = 0; i < 5; i++)
        {
            sc68.play();
            Thread.sleep(Constant.HUNDRED);
        }
        Thread.sleep(250);
        sc68.stop();
        sc68.play();
        sc68.stop();
    }
}
