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
package com.b3dgs.lionengine.core.sequence;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.EngineMock;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.filter.FilterBilinear;
import com.b3dgs.lionengine.core.filter.FilterBlur;
import com.b3dgs.lionengine.core.filter.FilterHq2x;
import com.b3dgs.lionengine.core.filter.FilterHq3x;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenMock;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link Loader}.
 */
public final class LoaderTest
{
    /** Output. */
    static final Resolution OUTPUT = new Resolution(640, 480, 60);
    /** Config. */
    static final Config CONFIG = new Config(OUTPUT, 16, true);
    /** Icon. */
    private static Media icon;

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Medias.setLoadFromJar(LoaderTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        icon = Medias.create("image.png");
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
     * Prepare test.
     */
    @Before
    public void before()
    {
        Engine.start(new EngineMock("LoaderTest", Version.DEFAULT));
    }

    /**
     * Terminate test.
     */
    @After
    public void after()
    {
        Engine.terminate();
    }

    /**
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(Loader.class);
    }

    /**
     * Test with no config.
     */
    @Test(expected = LionEngineException.class)
    public void testNullConfig()
    {
        Loader.start(null, SequenceSingleMock.class).await();
    }

    /**
     * Test with a single sequence.
     */
    @Test
    public void testSequenceSingle()
    {
        Loader.start(CONFIG, SequenceSingleMock.class).await();
    }

    /**
     * Test with a sequence that have arguments.
     */
    @Test
    public void testSequenceArgument()
    {
        Loader.start(CONFIG, SequenceArgumentsMock.class, new Object()).await();
    }

    /**
     * Test with timed out screen.
     */
    @Test(expected = LionEngineException.class)
    public void testSequenceTimeout()
    {
        try
        {
            ScreenMock.setScreenWait(true);
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
            Loader.start(CONFIG, SequenceSingleMock.class).await();
        }
        finally
        {
            Verbose.info("****************************************************************************************");
            ScreenMock.setScreenWait(false);
        }
    }

    /**
     * Test with screen not ready on rendering.
     * 
     * @throws Throwable If error.
     */
    @Test(timeout = 1000L)
    public void testSequenceRenderScreenUnready() throws Throwable
    {
        final CountDownLatch waitUpdate = new CountDownLatch(1);
        final CountDownLatch waitScreenUnready = new CountDownLatch(1);
        try
        {
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
            final TaskFuture task = Loader.start(CONFIG, SequenceScreenNotReady.class, waitUpdate, waitScreenUnready);

            waitUpdate.await();
            ScreenMock.setScreenWait(true);
            waitScreenUnready.countDown();

            final AtomicReference<Throwable> throwable = new AtomicReference<>();
            final Thread thread = new Thread(() -> task.await());
            thread.setUncaughtExceptionHandler((e, t) -> throwable.set(t));
            thread.start();
            thread.join(250);
            if (throwable.get() != null)
            {
                throw throwable.get();
            }
        }
        finally
        {
            Verbose.info("****************************************************************************************");
            ScreenMock.setScreenWait(false);
        }
    }

    /**
     * Test with no sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testSequenceNull()
    {
        Loader.start(CONFIG, null).await();
    }

    /**
     * Test with fail sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testSequenceFail()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        try
        {
            Loader.start(CONFIG, SequenceFailMock.class).await();
        }
        finally
        {
            Verbose.info("****************************************************************************************");
        }
    }

    /**
     * Test with fail next sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testSequenceFailNext()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        try
        {
            Loader.start(CONFIG, SequenceNextFailMock.class).await();
        }
        finally
        {
            Verbose.info("****************************************************************************************");
        }
    }

    /**
     * Test with malformed sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testSequenceMalformed()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        try
        {
            Loader.start(CONFIG, SequenceMalformedMock.class).await();
        }
        finally
        {
            Verbose.info("****************************************************************************************");
        }
    }

    /**
     * Test interrupted.
     * 
     * @throws Throwable If error.
     */
    @Test(timeout = SequenceInterruptMock.PAUSE_MILLI * 2L, expected = LionEngineException.class)
    public void testInterrupted() throws Throwable
    {
        final AtomicReference<Throwable> exception = new AtomicReference<>();
        final Semaphore semaphore = new Semaphore(0);

        final Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                final TaskFuture future = Loader.start(CONFIG, SequenceInterruptMock.class);
                future.await();
            }
        };
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
     * Test interrupted unchecked exception.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = SequenceInterruptMock.PAUSE_MILLI * 2L)
    public void testInterruptedUnchecked() throws InterruptedException
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
            final Thread thread = new Thread()
            {
                @Override
                public void run()
                {
                    final TaskFuture future = Loader.start(CONFIG, SequenceInterruptMock.class);
                    future.await();
                }
            };
            thread.setUncaughtExceptionHandler((t, throwable) ->
            {
                exception.set(throwable);
                semaphore.release();
            });
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
            thread.start();
            semaphore.acquire();
            Assert.assertTrue(exception.get().getCause() instanceof NullPointerException);
        }
        finally
        {
            Verbose.info("****************************************************************************************");
            Graphics.setFactoryGraphic(new FactoryGraphicMock());
        }
    }

    /**
     * Test already started.
     */
    @Test
    public void testStarted()
    {
        final TaskFuture future = Loader.start(CONFIG, SequenceSingleMock.class);
        try
        {
            Loader.start(CONFIG, SequenceSingleMock.class).await();
        }
        finally
        {
            future.await();
        }
    }

    /**
     * Test engine started.
     */
    @Test
    public void testEngineStarted()
    {
        Loader.start(CONFIG, SequenceSingleMock.class).await();
        Assert.assertTrue(Engine.isStarted());
    }

    /**
     * Test with no icon in windowed mode.
     */
    @Test
    public void testNoIconWindowed()
    {
        final Config config = new Config(OUTPUT, 16, true, Medias.create("void"));
        Loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test with an icon in windowed mode.
     */
    @Test
    public void testIconWindowed()
    {
        final Config config = new Config(OUTPUT, 16, true, icon);
        Loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test with an icon in full screen mode.
     */
    @Test
    public void testIconFullScreen()
    {
        final Config config = new Config(OUTPUT, 16, false, icon);
        Loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test with slow sequence.
     */
    @Test
    public void testSlowSequence()
    {
        Loader.start(CONFIG, SequenceSlowMock.class).await();
    }

    /**
     * Test interrupted.
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
            final Thread thread = new Thread()
            {
                @Override
                public void run()
                {
                    final Screen screen = Graphics.createScreen(CONFIG);
                    screen.start();
                    screen.awaitReady();
                }
            };
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
     * Test with screen direct.
     */
    @Test
    public void testDirect()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = new Config(output, 16, true);
        Loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test with sequence terminate engine.
     */
    @Test
    public void testEngineTerminate()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = new Config(output, 16, true);
        Loader.start(config, SequenceEngineTerminateMock.class).await();
    }

    /**
     * Test with screen scaled.
     */
    @Test
    public void testScaled()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final Config config = new Config(output, 16, true);
        Loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test with bilinear filter.
     */
    @Test
    public void testBilinear()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final Config config = new Config(output, 16, true);
        Loader.start(config, SequenceFilterMock.class, new FilterBilinear()).await();
    }

    /**
     * Test with blur filter.
     */
    @Test
    public void testBlur()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final FilterBlur blur = new FilterBlur();
        blur.setEdgeMode(FilterBlur.CLAMP_EDGES);
        blur.setAlpha(true);
        final Config config = new Config(output, 16, true);

        Loader.start(config, SequenceFilterMock.class, blur).await();

        blur.setEdgeMode(FilterBlur.WRAP_EDGES);
        Loader.start(config, SequenceFilterMock.class, blur).await();

        blur.setAlpha(false);
        Loader.start(config, SequenceFilterMock.class, blur).await();

        blur.setAlpha(true);
        blur.setRadius(0.5f);
        Loader.start(config, SequenceFilterMock.class, blur).await();
    }

    /**
     * Test with a hq2x filter.
     */
    @Test
    public void testFilterHq2x()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final Config config = new Config(output, 16, false);
        Loader.start(config, SequenceFilterMock.class, new FilterHq2x()).await();
    }

    /**
     * Test with a hq3x filter.
     */
    @Test
    public void testFilterHq3x()
    {
        final Resolution output = new Resolution(960, 720, 60);
        final Config config = new Config(output, 16, false);
        Loader.start(config, SequenceFilterMock.class, new FilterHq3x()).await();
    }
}
