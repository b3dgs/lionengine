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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
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

        assertFalse(action.get());

        tick.start();

        assertFalse(action.get());

        tick.update(1.0);

        assertFalse(action.get());

        tick.update(1.0);

        assertTrue(action.get());
    }

    /**
     * Test add <code>null</code> action.
     */
    @Test
    public void testAddActionNull()
    {
        assertThrows(() -> tick.addAction(null, 0L), Check.ERROR_NULL);
    }

    /**
     * Test start.
     */
    @Test
    public void testStart()
    {
        assertFalse(tick.isStarted());
        assertFalse(tick.elapsed(0L));
        assertFalse(tick.elapsedTime(CONTEXT, 0L));
        assertEquals(0L, tick.elapsed());

        tick.start();

        assertTrue(tick.isStarted());
        assertTrue(tick.elapsed(0L));
        assertTrue(tick.elapsedTime(CONTEXT, 0L));
        assertEquals(0L, tick.elapsed());

        tick.start();

        assertTrue(tick.isStarted());
        assertFalse(tick.elapsed(1L));
        assertFalse(tick.elapsedTime(CONTEXT, 1L));
        assertEquals(0L, tick.elapsed());
    }

    /**
     * Test stop.
     */
    @Test
    public void testStop()
    {
        assertFalse(tick.isStarted());

        tick.start();

        assertTrue(tick.isStarted());

        tick.stop();

        assertFalse(tick.isStarted());
        assertFalse(tick.elapsed(0L));
        assertFalse(tick.elapsedTime(CONTEXT, 0L));
        assertEquals(0L, tick.elapsed());
    }

    /**
     * Test pause.
     */
    @Test
    public void testPause()
    {
        assertEquals(0L, tick.elapsed());
        assertFalse(tick.elapsed(1L));
        assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.start();

        assertEquals(0L, tick.elapsed());
        assertFalse(tick.elapsed(1L));
        assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.update(1.0);

        assertEquals(1L, tick.elapsed());
        assertTrue(tick.elapsed(1L));
        assertTrue(tick.elapsedTime(CONTEXT, 15L));

        tick.pause();

        assertEquals(1L, tick.elapsed());
        assertTrue(tick.elapsed(1L));
        assertTrue(tick.elapsedTime(CONTEXT, 15L));

        tick.update(1.0);

        assertEquals(1L, tick.elapsed());
        assertTrue(tick.elapsed(1L));
        assertTrue(tick.elapsedTime(CONTEXT, 15L));
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

        assertEquals(0L, tick.elapsed());
        assertFalse(tick.elapsed(1L));
        assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.resume();

        assertEquals(0L, tick.elapsed());
        assertFalse(tick.elapsed(1L));
        assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.update(1.0);

        assertEquals(1L, tick.elapsed());
        assertTrue(tick.elapsed(1L));
        assertTrue(tick.elapsedTime(CONTEXT, 15L));
    }

    /**
     * Test restart.
     */
    @Test
    public void testRestart()
    {
        assertFalse(tick.isStarted());

        tick.restart();

        assertTrue(tick.isStarted());
        assertEquals(0L, tick.elapsed());
        assertFalse(tick.elapsed(1L));
        assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.restart();

        assertTrue(tick.isStarted());
        assertEquals(0L, tick.elapsed());
        assertFalse(tick.elapsed(1L));
        assertFalse(tick.elapsedTime(CONTEXT, 15L));
    }

    /**
     * Test set.
     */
    @Test
    public void testSet()
    {
        assertFalse(tick.isStarted());
        assertEquals(0L, tick.elapsed());
        assertFalse(tick.elapsed(1L));
        assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.set(1);

        assertFalse(tick.isStarted());
        assertEquals(1L, tick.elapsed());
        assertFalse(tick.elapsed(1L));
        assertFalse(tick.elapsedTime(CONTEXT, 15L));

        tick.start();

        assertTrue(tick.isStarted());
        assertEquals(1L, tick.elapsed());
        assertTrue(tick.elapsed(1L));
        assertTrue(tick.elapsedTime(CONTEXT, 15L));
    }

    /**
     * Test elapsed with <code>null</code> context.
     */
    @Test
    public void testElapsedNullContext()
    {
        assertThrows(() -> tick.elapsedTime(null, 0L), Check.ERROR_NULL);
    }
}
