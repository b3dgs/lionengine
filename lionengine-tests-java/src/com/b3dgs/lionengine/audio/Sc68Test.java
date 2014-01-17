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
package com.b3dgs.lionengine.audio;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Media;

/**
 * Test the sc68 player.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Sc68Test
{
    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Test Sc68 functions.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testSc68() throws InterruptedException
    {
        try
        {
            Engine.start("Sc68Test", Version.create(1, 0, 0), "resources");
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Sc68 sc68 = AudioSc68.createSc68Player();
        try
        {
            sc68.play(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            sc68.setVolume(-1);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            sc68.setVolume(101);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        sc68.setVolume(15);
        sc68.play(Media.create("music.sc68"));
        Thread.sleep(1000);
        sc68.pause();
        Thread.sleep(500);
        sc68.setVolume(30);
        sc68.resume();
        Thread.sleep(1000);
        Assert.assertTrue(sc68.seek() >= 0);
        sc68.stop();
        Engine.terminate();
    }

    /**
     * Test Sc68 stress.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testSc68Stress() throws InterruptedException
    {
        Engine.start("Sc68TestStress", Version.create(1, 0, 0), "resources");
        final Sc68 sc68 = AudioSc68.createSc68Player();
        sc68.play(Media.create("music.sc68"));
        sc68.stop();
        sc68.play(Media.create("music.sc68"));
        Thread.sleep(100);
        sc68.stop();
        sc68.play(Media.create("music.sc68"));
        sc68.pause();
        sc68.resume();
        for (int i = 0; i < 10; i++)
        {
            sc68.play(Media.create("music.sc68"));
        }
        Thread.sleep(500);
        sc68.stop();
        sc68.play(Media.create("music.sc68"));
        sc68.stop();
        Engine.terminate();
    }
}
