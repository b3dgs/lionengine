/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * Test wav player.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WavTest
{
    /**
     * Test Wav functions.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testWav() throws InterruptedException
    {
        try
        {
            AudioWav.loadWav(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Wav sound = AudioWav.loadWav(Media.create(Media.getPath("resources", "sound.wav")));
        try
        {
            sound.setVolume(-1);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            sound.setVolume(101);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        sound.setVolume(50);
        sound.setAlignment(Align.LEFT);
        sound.play();
        Thread.sleep(200);

        sound.setAlignment(Align.CENTER);
        sound.play();
        Thread.sleep(200);

        sound.setAlignment(Align.RIGHT);
        sound.play();
        Thread.sleep(200);
        sound.stop();

        final Wav soundSim = AudioWav.loadWav(Media.create(Media.getPath("resources", "sound.wav")), 2);
        soundSim.play();
        soundSim.play(20);
        soundSim.play();
        Thread.sleep(200);
        soundSim.play(100);
        soundSim.play(100);
        soundSim.terminate();

        final Wav soundSim2 = AudioWav.loadWav(Media.create(Media.getPath("resources", "sound.wav")), 2);
        soundSim2.setVolume(50);
        soundSim2.play();
        soundSim2.play();
        Thread.sleep(500);
        soundSim2.terminate();
    }
}
