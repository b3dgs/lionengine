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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link Timing}.
 */
public final class TimingTest
{
    /** Pause time value. */
    private static final long PAUSE = 50L;

    /** Timing instance. */
    private final Timing timing = new Timing();

    /**
     * Test start.
     */
    @Test
    public void testStart()
    {
        Assert.assertFalse(timing.isStarted());
        Assert.assertEquals(0L, timing.elapsed());
        Assert.assertFalse(timing.elapsed(-1L));
        Assert.assertFalse(timing.elapsed(0L));
        Assert.assertFalse(timing.elapsed(1L));
        Assert.assertEquals(0L, timing.get());

        timing.start();
        final long time = timing.get();

        Assert.assertTrue(timing.isStarted());

        UtilTests.pause(PAUSE);

        Assert.assertFalse(String.valueOf(timing.elapsed()), timing.elapsed(PAUSE * Constant.HUNDRED));
        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed() >= PAUSE);
        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed(PAUSE));
        Assert.assertEquals(time + " " + timing.elapsed(), time, timing.get());

        timing.start();
        UtilTests.pause(PAUSE);

        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed() >= PAUSE);
        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed(PAUSE));
        Assert.assertEquals(time + " " + timing.elapsed(), time, timing.get());
    }

    /**
     * Test stop.
     */
    @Test
    public void testStop()
    {
        timing.start();
        UtilTests.pause(PAUSE);
        timing.stop();

        Assert.assertFalse(timing.isStarted());
        Assert.assertEquals(0L, timing.get());
        Assert.assertFalse(String.valueOf(timing.elapsed()), timing.elapsed(PAUSE));
    }

    /**
     * Test pause.
     */
    @Test
    public void testPause()
    {
        timing.start();
        UtilTests.pause(PAUSE);
        timing.pause();

        final long old = timing.get();
        final long elapsed = timing.elapsed();

        UtilTests.pause(PAUSE);

        Assert.assertEquals(old + " " + timing.elapsed(), old, timing.get());
        Assert.assertEquals(elapsed + " " + timing.elapsed(), elapsed, timing.elapsed());
    }

    /**
     * Test unpause.
     */
    @Test
    public void testUnpause()
    {
        timing.start();
        timing.pause();
        UtilTests.pause(PAUSE);

        final long old = timing.get();
        final long elapsed = timing.elapsed();
        timing.unpause();
        UtilTests.pause(PAUSE);

        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed(PAUSE));
        Assert.assertTrue(timing + " " + timing.elapsed(), timing.get() > old);
        Assert.assertTrue(timing + " " + timing.elapsed(), timing.elapsed() > elapsed);
    }

    /**
     * Test restart.
     */
    @Test
    public void testRestart()
    {
        timing.start();

        UtilTests.pause(PAUSE);

        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed() >= PAUSE);

        timing.restart();

        UtilTests.pause(PAUSE);

        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed() >= PAUSE);
    }

    /**
     * Test set.
     */
    @Test
    public void testSet()
    {
        timing.set(PAUSE);

        Assert.assertTrue(timing.isStarted());
        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed(PAUSE));
    }
}
