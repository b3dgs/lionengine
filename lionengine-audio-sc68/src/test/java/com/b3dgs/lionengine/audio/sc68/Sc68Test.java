/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * Test the sc68 player.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Sc68Test
{
    /** Media music. */
    private static Media MUSIC;
    /** Binding. */
    private static Sc68 sc68;

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Sc68Test.MUSIC = new MediaMock("music.sc68");
        Sc68Test.sc68 = AudioSc68.createSc68Player();
    }

    /**
     * Test with <code>null</code> argument.
     */
    @Test(expected = LionEngineException.class)
    public void testNullArgument()
    {
        Sc68Test.sc68.play(null);
        Assert.fail();
    }

    /**
     * Test with negative volume.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeVolume()
    {
        Sc68Test.sc68.setVolume(-1);
        Assert.fail();
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        Sc68Test.sc68.setVolume(101);
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
        Sc68Test.sc68.setVolume(15);
        Sc68Test.sc68.play(Sc68Test.MUSIC);
        Thread.sleep(500);
        Sc68Test.sc68.pause();
        Thread.sleep(500);
        Sc68Test.sc68.setVolume(30);
        Sc68Test.sc68.resume();
        Thread.sleep(500);
        Assert.assertTrue(Sc68Test.sc68.seek() >= 0);
        Sc68Test.sc68.stop();
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
        sc68.play(Sc68Test.MUSIC);
        sc68.stop();
        sc68.play(Sc68Test.MUSIC);
        Thread.sleep(100);
        sc68.stop();
        sc68.play(Sc68Test.MUSIC);
        sc68.pause();
        sc68.resume();
        for (int i = 0; i < 5; i++)
        {
            sc68.play(Sc68Test.MUSIC);
            Thread.sleep(100);
        }
        Thread.sleep(250);
        sc68.stop();
        sc68.play(Sc68Test.MUSIC);
        sc68.stop();
    }
}
