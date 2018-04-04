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
package com.b3dgs.lionengine;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test {@link Tick}.
 */
public final class TickTest
{
    /** Test config. */
    private static final Config CONFIG = new Config(UtilTests.RESOLUTION_320_240, 16, true);
    /** Test context. */
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

    /** Tick instance. */
    private final Tick tick = new Tick();

    /**
     * Test delayed action.
     */
    @Test
    public void testAddAction()
    {
        final AtomicBoolean action = new AtomicBoolean();
        tick.addAction(() -> action.set(true), 2L);

        Assert.assertFalse(action.get());

        tick.start();

        Assert.assertFalse(action.get());

        tick.update(1.0);

        Assert.assertFalse(action.get());

        tick.update(1.0);

        Assert.assertTrue(action.get());
    }

    /**
     * Test add <code>null</code> action.
     */
    @Test(expected = LionEngineException.class)
    public void testAddActionNull()
    {
        tick.addAction(null, 0L);
    }

    /**
     * Test start.
     */
    @Test
    public void testStart()
    {
        Assert.assertFalse(tick.isStarted());
        Assert.assertFalse(tick.elapsed(0L));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 0L));
        Assert.assertEquals(0L, tick.elapsed());

        tick.start();

        Assert.assertTrue(tick.isStarted());
        Assert.assertTrue(tick.elapsed(0L));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 0L));
        Assert.assertEquals(0L, tick.elapsed());

        tick.start();

        Assert.assertTrue(tick.isStarted());
        Assert.assertFalse(tick.elapsed(1L));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 1L));
        Assert.assertEquals(0L, tick.elapsed());
    }

    /**
     * Test stop.
     */
    @Test
    public void testStop()
    {
        Assert.assertFalse(tick.isStarted());

        tick.start();

        Assert.assertTrue(tick.isStarted());

        tick.stop();

        Assert.assertFalse(tick.isStarted());
        Assert.assertFalse(tick.elapsed(0L));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 0L));
        Assert.assertEquals(0L, tick.elapsed());
    }

    /**
     * Test pause.
     */
    @Test
    public void testPause()
    {
        Assert.assertEquals(0L, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1L));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.start();

        Assert.assertEquals(0L, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1L));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.update(1.0);

        Assert.assertEquals(1L, tick.elapsed());
        Assert.assertTrue(tick.elapsed(1L));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 15L));

        tick.pause();

        Assert.assertEquals(1L, tick.elapsed());
        Assert.assertTrue(tick.elapsed(1L));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 15L));

        tick.update(1.0);

        Assert.assertEquals(1L, tick.elapsed());
        Assert.assertTrue(tick.elapsed(1L));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 15L));
    }

    /**
     * Test resume.
     */
    @Test
    public void testResume()
    {
        tick.start();
        tick.pause();
        tick.update(1.0);

        Assert.assertEquals(0L, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1L));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.resume();

        Assert.assertEquals(0L, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1L));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.update(1.0);

        Assert.assertEquals(1L, tick.elapsed());
        Assert.assertTrue(tick.elapsed(1L));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 15L));
    }

    /**
     * Test restart.
     */
    @Test
    public void testRestart()
    {
        Assert.assertFalse(tick.isStarted());

        tick.restart();

        Assert.assertTrue(tick.isStarted());
        Assert.assertEquals(0L, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1L));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));
    }

    /**
     * Test set.
     */
    @Test
    public void testSet()
    {
        Assert.assertFalse(tick.isStarted());
        Assert.assertEquals(0L, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1L));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.set(1);

        Assert.assertFalse(tick.isStarted());
        Assert.assertEquals(1L, tick.elapsed());
        Assert.assertFalse(tick.elapsed(1L));
        Assert.assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.start();

        Assert.assertTrue(tick.isStarted());
        Assert.assertEquals(1L, tick.elapsed());
        Assert.assertTrue(tick.elapsed(1L));
        Assert.assertTrue(tick.elapsedTime(CONTEXT, 15L));
    }

    /**
     * Test elapsed with <code>null</code> context.
     */
    @Test(expected = LionEngineException.class)
    public void testElapsedNullContext()
    {
        Assert.assertTrue(tick.elapsedTime(null, 0L));
    }
}
