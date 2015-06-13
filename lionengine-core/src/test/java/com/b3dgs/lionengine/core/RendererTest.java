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
package com.b3dgs.lionengine.core;

import java.lang.Thread.State;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.ScreenMock;
import com.b3dgs.lionengine.mock.SequenceInterruptMock;
import com.b3dgs.lionengine.mock.SequenceSingleMock;

/**
 * Test the renderer class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class RendererTest
{
    /** Output. */
    private static final Resolution OUTPUT = new Resolution(640, 480, 60);
    /** Config. */
    private static final Config CONFIG = new Config(OUTPUT, 16, true);
    /** Time out. */
    private static final int TIME_OUT = 5000;
    /** Uncaught flag. */
    static volatile boolean uncaught = false;

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Verbose.info("*********************************** SEQUENCE VERBOSE ***********************************");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Verbose.info("****************************************************************************************");
        ScreenMock.wait = false;
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test the renderer with a simple sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testStartFirstSequence() throws InterruptedException
    {
        final Loader loader = new Loader(CONFIG);
        final Renderer renderer = loader.getRenderer();
        renderer.startFirstSequence(loader, SequenceSingleMock.class);
        renderer.join();
    }

    /**
     * Test the renderer already started.
     * 
     * @throws InterruptedException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testAlreadyStarted() throws InterruptedException
    {
        final Loader loader = new Loader(CONFIG);
        final Renderer renderer = loader.getRenderer();
        final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread t, Throwable exception)
            {
                uncaught = true;
            }
        };
        renderer.setUncaughtExceptionHandler(handler);
        renderer.startFirstSequence(loader, SequenceSingleMock.class);
        Assert.assertTrue(renderer.isStarted());
        Assert.assertNull(renderer.getNextSequence());
        renderer.join();
        renderer.startFirstSequence(loader, SequenceSingleMock.class);
        renderer.join();
        Assert.assertTrue(uncaught);
    }

    /**
     * Test the sequence interrupt.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 500)
    public void testSequenceInterrupt() throws InterruptedException
    {
        final Loader loader = new Loader(CONFIG);
        final Renderer renderer = loader.getRenderer();
        final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread t, Throwable exception)
            {
                uncaught = true;
            }
        };
        renderer.setUncaughtExceptionHandler(handler);
        loader.start(SequenceInterruptMock.class);
        while (renderer.getState() != State.TIMED_WAITING)
        {
            Thread.sleep(50);
        }
        renderer.interrupt();
        while (!uncaught)
        {
            // Wait
        }
        renderer.join();
    }

    /**
     * Test the wait screen timeout.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = TIME_OUT + 1000)
    public void testWaitScreenTimeout() throws InterruptedException
    {
        final Loader loader = new Loader(CONFIG);
        final Renderer renderer = loader.getRenderer();
        ScreenMock.wait = true;
        loader.start(SequenceSingleMock.class);
        renderer.join();
        ScreenMock.wait = false;
    }

    /**
     * Test the wait screen interrupt.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 500)
    public void testWaitScreenInterrupt() throws InterruptedException
    {
        final Loader loader = new Loader(CONFIG);
        final Renderer renderer = loader.getRenderer();
        ScreenMock.wait = true;
        loader.start(SequenceSingleMock.class);

        while (renderer.getState() != State.TIMED_WAITING)
        {
            // Wait
        }
        renderer.interrupt();
        renderer.join();
        ScreenMock.wait = false;
    }
}
