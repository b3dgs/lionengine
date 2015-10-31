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
package com.b3dgs.lionengine.test.core;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Screen;
import com.b3dgs.lionengine.core.TaskFuture;
import com.b3dgs.lionengine.test.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.test.mock.ScreenMock;
import com.b3dgs.lionengine.test.mock.SequenceArgumentsMock;
import com.b3dgs.lionengine.test.mock.SequenceFailMock;
import com.b3dgs.lionengine.test.mock.SequenceInterruptMock;
import com.b3dgs.lionengine.test.mock.SequenceMalformedMock;
import com.b3dgs.lionengine.test.mock.SequenceNextFailMock;
import com.b3dgs.lionengine.test.mock.SequenceSingleMock;
import com.b3dgs.lionengine.test.util.UtilTests;

/**
 * Test the loader class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LoaderTest
{
    /** Output. */
    private static final Resolution OUTPUT = new Resolution(640, 480, 60);
    /** Config. */
    private static final Config CONFIG = new Config(OUTPUT, 16, true);
    /** Icon. */
    private static final Media ICON = Medias.create("image.png");

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Medias.setLoadFromJar(LoaderTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test the loader with no config.
     */
    @Test(expected = LionEngineException.class)
    public void testNullConfig()
    {
        final Loader loader = new Loader();
        loader.start(null, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with no sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testNullSequence()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, null);
    }

    /**
     * Test the loader with fail sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testFailSequence()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, SequenceFailMock.class).await();
    }

    /**
     * Test the loader with fail next sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testFailNextSequence()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, SequenceNextFailMock.class).await();
    }

    /**
     * Test the loader with malformed sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testMalformedSequence()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, SequenceMalformedMock.class).await();
    }

    /**
     * Test the loader interrupted.
     * 
     * @throws Throwable If error.
     */
    @Test(timeout = SequenceInterruptMock.PAUSE_MILLI * 2L, expected = LionEngineException.class)
    public void testInterrupted() throws Throwable
    {
        final AtomicReference<Throwable> exception = new AtomicReference<>();
        final Semaphore semaphore = new Semaphore(0);
        final Thread thread = new Thread(() ->
        {
            final Loader loader = new Loader();
            final TaskFuture future = loader.start(CONFIG, SequenceInterruptMock.class);
            future.await();
        });
        thread.setUncaughtExceptionHandler((t, throwable) ->
        {
            exception.set(throwable);
            semaphore.release();
        });
        thread.start();
        UtilTests.pause(SequenceInterruptMock.PAUSE_MILLI / 2L);
        thread.interrupt();
        semaphore.acquire();
        throw exception.get();
    }

    /**
     * Test the loader interrupted unchecked exception.
     * 
     * @throws Throwable If error.
     */
    @Test(timeout = SequenceInterruptMock.PAUSE_MILLI * 2L, expected = Throwable.class)
    public void testInterruptedUnchecked() throws Throwable
    {
        try
        {
            Graphics.setFactoryGraphic(new FactoryGraphicMock()
            {
                @Override
                public Screen createScreen(Config config)
                {
                    return null;
                }
            });

            final AtomicReference<Throwable> exception = new AtomicReference<>();
            final Semaphore semaphore = new Semaphore(0);
            final Thread thread = new Thread(() ->
            {
                final Loader loader = new Loader();
                final TaskFuture future = loader.start(CONFIG, SequenceInterruptMock.class);
                future.await();
            });
            thread.setUncaughtExceptionHandler((t, throwable) ->
            {
                exception.set(throwable);
                semaphore.release();
            });
            thread.start();
            UtilTests.pause(SequenceInterruptMock.PAUSE_MILLI / 2L);
            thread.interrupt();
            semaphore.acquire();
            throw exception.get();
        }
        finally
        {
            Graphics.setFactoryGraphic(new FactoryGraphicMock());
        }
    }

    /**
     * Test the loader already started.
     */
    @Test(expected = LionEngineException.class)
    public void testStarted()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, SequenceSingleMock.class);
        loader.start(CONFIG, SequenceSingleMock.class);
    }

    /**
     * Test the loader with an icon in windowed mode.
     */
    @Test
    public void testIconWindowed()
    {
        final Config config = new Config(OUTPUT, 16, true);
        config.setIcon(ICON);

        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class);
    }

    /**
     * Test the loader with an icon in full screen mode.
     */
    @Test
    public void testIconFullScreen()
    {
        final Config config = new Config(OUTPUT, 16, false);
        config.setIcon(ICON);

        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class);
    }

    /**
     * Test the loader with a single sequence.
     */
    @Test
    public void testSequenceSingle()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with a sequence that have arguments.
     */
    @Test
    public void testSequenceArgument()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, SequenceArgumentsMock.class, new Object()).await();
    }

    /**
     * Test the loader with timed out screen.
     */
    @Test(expected = LionEngineException.class)
    public void testSequenceTimeout()
    {
        try
        {
            ScreenMock.setScreenWait(true);
            final Loader loader = new Loader();
            loader.start(CONFIG, SequenceSingleMock.class).await();
        }
        finally
        {
            ScreenMock.setScreenWait(false);
        }
    }

    /**
     * Test the loader interrupted.
     * 
     * @throws Throwable If error.
     */
    @Test(timeout = ScreenMock.READY_TIMEOUT * 2L, expected = LionEngineException.class)
    public void testScreenInterrupted() throws Throwable
    {
        try
        {
            ScreenMock.setScreenWait(true);
            final AtomicReference<Throwable> exception = new AtomicReference<>();
            final Semaphore semaphore = new Semaphore(0);
            final Thread thread = new Thread(() ->
            {
                final Screen screen = Graphics.createScreen(CONFIG);
                screen.start();
                screen.awaitReady();
            });
            thread.setUncaughtExceptionHandler((t, throwable) ->
            {
                exception.set(throwable);
                semaphore.release();
            });
            thread.start();
            UtilTests.pause(ScreenMock.READY_TIMEOUT / 2L);
            thread.interrupt();
            semaphore.acquire();
            throw exception.get();
        }
        finally
        {
            ScreenMock.setScreenWait(false);
        }
    }

    /**
     * Test the loader with a bilinear filter.
     */
    @Test
    public void testFilterBilinear()
    {
        final Resolution output = new Resolution(320, 240, 0);
        final Config config = new Config(output, 16, true, Filter.BILINEAR);
        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with a bilinear filter and screen scaled.
     */
    @Test
    public void testFilterBilinearScaled()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final Config config = new Config(output, 16, true, Filter.BILINEAR);
        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with a hq2x filter.
     */
    @Test
    public void testFilterHq2x()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final Config config = new Config(output, 16, false, Filter.HQ2X);
        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with a hq3x filter.
     */
    @Test
    public void testFilterHq3x()
    {
        final Resolution output = new Resolution(960, 720, 60);
        final Config config = new Config(output, 16, false, Filter.HQ3X);
        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class).await();
    }
}
