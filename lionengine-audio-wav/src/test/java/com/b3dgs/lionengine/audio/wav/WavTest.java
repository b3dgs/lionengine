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
package com.b3dgs.lionengine.audio.wav;

import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;

/**
 * Test {@link WavFormat} and {@link WavImpl}.
 */
public final class WavTest
{
    /**
     * Prepare the test.
     */
    @BeforeAll
    public static void prepareTest()
    {
        Medias.setLoadFromJar(WavTest.class);
        WavFormat.setMixer(null);
        AudioFactory.addFormat(new WavFormat());
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void cleanUp()
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
        assertThrows(() -> AudioFactory.loadAudio(null), "Unexpected null argument !");
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
        final Audio wav = AudioFactory.loadAudio(Medias.create("invalid.wav"));
        try
        {
            wav.play();
            Thread.sleep(Constant.HUNDRED);
        }
        finally
        {
            wav.stop();
        }
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test with negative volume.
     */
    @Test
    public void testNegativeVolume()
    {
        final Wav wav = AudioFactory.loadAudio(Medias.create("sound.wav"), Wav.class);
        try
        {
            assertThrows(() -> wav.setVolume(-1), "Invalid argument: -1 is not superior or equal to 0");
        }
        finally
        {
            wav.stop();
        }
    }

    /**
     * Test with out of range volume.
     */
    @Test
    public void testOutOfRangeVolume()
    {
        final Wav wav = AudioFactory.loadAudio(Medias.create("sound.wav"), Wav.class);
        try
        {
            assertThrows(() -> wav.setVolume(101), "Invalid argument: 101 is not inferior or equal to 100");
        }
        finally
        {
            wav.stop();
        }
    }

    /**
     * Test functions.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testWav() throws InterruptedException
    {
        final Wav wav = AudioFactory.loadAudio(Medias.create("sound.wav"), Wav.class);
        try
        {
            wav.setVolume(50);
            wav.play();
            Thread.sleep(Constant.HUNDRED);

            wav.play(Align.LEFT);
            Thread.sleep(Constant.HUNDRED);

            wav.play(Align.CENTER);
            Thread.sleep(Constant.HUNDRED);

            wav.play(Align.RIGHT);
            Thread.sleep(Constant.HUNDRED);

            wav.play();
            Thread.sleep(Constant.THOUSAND);

            wav.play();
            Thread.sleep(Constant.DECADE);

            wav.play();
            wav.play(Align.CENTER);
            Thread.sleep(Constant.HUNDRED);
        }
        finally
        {
            wav.stop();
        }
    }
}
