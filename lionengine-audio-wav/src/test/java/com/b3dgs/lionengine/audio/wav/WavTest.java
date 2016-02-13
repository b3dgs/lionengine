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
package com.b3dgs.lionengine.audio.wav;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test wav player.
 */
public class WavTest
{
    /** Media sound. */
    private static Media mediaSound;
    /** Wav sound. */
    private static Wav sound;

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Medias.setLoadFromJar(WavTest.class);
        mediaSound = Medias.create("sound.wav");
        sound = AudioWav.loadWav(mediaSound);
    }

    /**
     * Clean up tests.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        AudioWav.terminate();
    }

    /**
     * Test with <code>null</code> argument.
     */
    @Test(expected = LionEngineException.class)
    public void testNullArgument()
    {
        AudioWav.loadWav(null);
        Assert.fail();
    }

    /**
     * Test with invalid audio.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testInvalidAudio() throws InterruptedException
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        final Wav wav = AudioWav.loadWav(Medias.create("invalid.wav"));
        wav.play();
        Thread.sleep(100);
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test with negative volume.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeVolume()
    {
        sound.play(Align.CENTER, -1);
        Assert.fail();
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        sound.play(Align.CENTER, 101);
        Assert.fail();
    }

    /**
     * Test wav functions.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testWav() throws InterruptedException
    {
        sound.play(Align.CENTER, 50, 0);
        sound.play(Align.LEFT, 50, 150);
        sound.play(Align.CENTER, 50, 300);
        sound.play(Align.RIGHT, 50, 450);
        sound.play(Align.CENTER, 50, 600);
        Thread.sleep(900);

        sound.play();
        sound.play(10);
        sound.play(Align.CENTER, 50);
        Thread.sleep(50);
        sound.stop();
    }
}
