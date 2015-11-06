/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
        WavTest.mediaSound = Medias.create("sound.wav");
        WavTest.sound = AudioWav.loadWav(WavTest.mediaSound);
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
        AudioWav.loadWav(null);
        Assert.fail();
    }

    /**
     * Test with negative volume.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeVolume()
    {
        WavTest.sound.setVolume(-1);
        Assert.fail();
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        WavTest.sound.setVolume(101);
        Assert.fail();
    }

    /**
     * Test Wav functions.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testWav() throws InterruptedException
    {
        WavTest.sound.setVolume(50);
        WavTest.sound.setAlignment(Align.LEFT);
        WavTest.sound.play();
        Thread.sleep(200);

        WavTest.sound.setAlignment(Align.CENTER);
        WavTest.sound.play();
        Thread.sleep(200);

        WavTest.sound.setAlignment(Align.RIGHT);
        WavTest.sound.play();
        Thread.sleep(200);
        WavTest.sound.stop();

        final Wav soundSim = AudioWav.loadWav(WavTest.mediaSound, 2);
        soundSim.play();
        soundSim.play(20);
        soundSim.play();
        Thread.sleep(200);
        soundSim.play(100);
        soundSim.play(100);
        soundSim.terminate();

        final Wav soundSim2 = AudioWav.loadWav(WavTest.mediaSound, 2);
        soundSim2.setVolume(50);
        soundSim2.play();
        soundSim2.play();
        Thread.sleep(500);
        soundSim2.terminate();
    }
}
