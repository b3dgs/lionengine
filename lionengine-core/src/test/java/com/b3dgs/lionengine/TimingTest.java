/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

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
        assertFalse(timing.isStarted());
        assertEquals(0L, timing.elapsed());
        assertFalse(timing.elapsed(-1L));
        assertFalse(timing.elapsed(0L));
        assertFalse(timing.elapsed(1L));
        assertEquals(0L, timing.get());

        timing.start();
        final long time = timing.get();

        assertTrue(timing.isStarted());

        UtilTests.pause(PAUSE);

        assertFalse(timing.elapsed(PAUSE * Constant.HUNDRED), String.valueOf(timing.elapsed()));
        assertTrue(timing.elapsed() >= PAUSE, String.valueOf(timing.elapsed()));
        assertTrue(timing.elapsed(PAUSE), String.valueOf(timing.elapsed()));
        assertEquals(time, timing.get(), time + " " + timing.elapsed());

        timing.start();
        UtilTests.pause(PAUSE);

        assertTrue(timing.elapsed() >= PAUSE, String.valueOf(timing.elapsed()));
        assertTrue(timing.elapsed(PAUSE), String.valueOf(timing.elapsed()));
        assertEquals(time, timing.get(), time + " " + timing.elapsed());
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

        assertFalse(timing.isStarted());
        assertEquals(0L, timing.get());
        assertFalse(timing.elapsed(PAUSE), String.valueOf(timing.elapsed()));
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

        assertEquals(old, timing.get(), old + " " + timing.elapsed());
        assertEquals(elapsed, timing.elapsed(), elapsed + " " + timing.elapsed());
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

        assertTrue(timing.elapsed(PAUSE), String.valueOf(timing.elapsed()));
        assertTrue(timing.get() > old, timing + " " + timing.elapsed());
        assertTrue(timing.elapsed() > elapsed, timing + " " + timing.elapsed());
    }

    /**
     * Test restart.
     */
    @Test
    public void testRestart()
    {
        timing.start();

        UtilTests.pause(PAUSE);

        assertTrue(timing.elapsed() >= PAUSE, String.valueOf(timing.elapsed()));

        timing.restart();

        UtilTests.pause(PAUSE);

        assertTrue(timing.elapsed() >= PAUSE, String.valueOf(timing.elapsed()));
    }

    /**
     * Test set.
     */
    @Test
    public void testSet()
    {
        timing.set(PAUSE);

        assertTrue(timing.isStarted());
        assertTrue(timing.elapsed(PAUSE), String.valueOf(timing.elapsed()));
    }
}
