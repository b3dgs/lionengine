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
package com.b3dgs.lionengine.audio.sc68;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * Test the sc68 player.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class Sc68Test
{
    /** Media music. */
    private static final Media MUSIC = new MediaMock("music.sc68");
    /** Binding. */
    private static final Sc68 SC68 = AudioSc68.createSc68Player();

    /**
     * Test with <code>null</code> argument.
     */
    @Test(expected = LionEngineException.class)
    public void testNullArgument()
    {
        SC68.play(null);
        Assert.fail();
    }

    /**
     * Test with negative volume.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeVolume()
    {
        SC68.setVolume(-1);
        Assert.fail();
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        SC68.setVolume(101);
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
        SC68.setVolume(15);
        SC68.setConfig(true, false);
        Sc68Test.SC68.play(MUSIC);
        Thread.sleep(500);
        SC68.setConfig(false, false);
        SC68.pause();
        Thread.sleep(500);
        SC68.setConfig(false, true);
        SC68.setVolume(30);
        SC68.resume();
        Thread.sleep(500);
        SC68.setConfig(true, true);
        Assert.assertTrue(SC68.seek() >= 0);
        SC68.stop();
    }

    /**
     * Test Sc68 stress.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testStress() throws InterruptedException
    {
        final Sc68 sc68 = AudioSc68.createSc68Player();
        sc68.play(MUSIC);
        sc68.stop();
        sc68.play(MUSIC);
        Thread.sleep(100);
        sc68.stop();
        sc68.play(MUSIC);
        sc68.pause();
        sc68.resume();
        for (int i = 0; i < 5; i++)
        {
            sc68.play(MUSIC);
            Thread.sleep(100);
        }
        Thread.sleep(250);
        sc68.stop();
        sc68.play(MUSIC);
        sc68.stop();
    }
}
