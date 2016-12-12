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
package com.b3dgs.lionengine;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the tick class.
 */
public class TickTest
{
    private static final Config CONFIG = new Config(UtilTests.RESOLUTION_320_240, 16, true);
    private static final Context CONTEXT = new Context()
    {
        @Override
        public int getX()
        {
            return 0;
        }

        @Override
        public int getY()
        {
            return 0;
        }

        @Override
        public <T extends InputDevice> T getInputDevice(Class<T> type)
        {
            return null;
        }

        @Override
        public Config getConfig()
        {
            return CONFIG;
        }
    };

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void prepare()
    {
        CONFIG.setSource(UtilTests.RESOLUTION_320_240);
    }

    private final Tick tick = new Tick();

    /**
     * Test the start case.
     */
    @Test
    public void testStart()
    {
        Assert.assertFalse(tick.isStarted());
        Assert.assertFalse(tick.elapsed(0));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 0L));
        Assert.assertEquals(0, tick.elapsed());

        tick.start();

        Assert.assertTrue(tick.isStarted());
        Assert.assertTrue(tick.elapsed(0));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 0L));
        Assert.assertEquals(0, tick.elapsed());

        tick.start();

        Assert.assertTrue(tick.isStarted());
        Assert.assertFalse(tick.elapsed(1));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 1L));
        Assert.assertEquals(0, tick.elapsed());
    }

    /**
     * Test the stop case.
     */
    @Test
    public void testStop()
    {
        Assert.assertFalse(tick.isStarted());

        tick.start();

        Assert.assertTrue(tick.isStarted());

        tick.stop();

        Assert.assertFalse(tick.isStarted());
        Assert.assertFalse(tick.elapsed(0));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 0L));
        Assert.assertEquals(0, tick.elapsed());
    }

    /**
     * Test the pause case.
     */
    @Test
    public void testPause()
    {
        Assert.assertEquals(0, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.start();

        Assert.assertEquals(0, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.update(1.0);

        Assert.assertEquals(1, tick.elapsed());
        Assert.assertTrue(tick.elapsed(1));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 15L));

        tick.pause();

        Assert.assertEquals(1, tick.elapsed());
        Assert.assertTrue(tick.elapsed(1));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 15L));

        tick.update(1.0);

        Assert.assertEquals(1, tick.elapsed());
        Assert.assertTrue(tick.elapsed(1));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 15L));
    }

    /**
     * Test the unpause case.
     */
    @Test
    public void testUnpause()
    {
        tick.start();
        tick.pause();
        tick.update(1.0);

        Assert.assertEquals(0, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.unpause();

        Assert.assertEquals(0, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.update(1.0);

        Assert.assertEquals(1, tick.elapsed());
        Assert.assertTrue(tick.elapsed(1));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 15L));
    }

    /**
     * Test the restart case.
     */
    @Test
    public void testRestart()
    {
        Assert.assertFalse(tick.isStarted());

        tick.restart();

        Assert.assertTrue(tick.isStarted());
        Assert.assertEquals(0, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));
    }

    /**
     * Test the set case.
     */
    @Test
    public void testSet()
    {
        Assert.assertFalse(tick.isStarted());
        Assert.assertEquals(0, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.set(1);

        Assert.assertFalse(tick.isStarted());
        Assert.assertEquals(1, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.start();

        Assert.assertTrue(tick.isStarted());
        Assert.assertEquals(1, tick.elapsed());
        Assert.assertTrue(tick.elapsed(1));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 15L));
    }
}
