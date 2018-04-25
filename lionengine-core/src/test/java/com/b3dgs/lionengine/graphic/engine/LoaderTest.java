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
package com.b3dgs.lionengine.graphic.engine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenMock;
import com.b3dgs.lionengine.graphic.filter.FilterBilinear;
import com.b3dgs.lionengine.graphic.filter.FilterBlur;
import com.b3dgs.lionengine.graphic.filter.FilterHq2x;
import com.b3dgs.lionengine.graphic.filter.FilterHq3x;

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
    @BeforeAll
    public static void prepareTest()
    {
        Medias.setLoadFromJar(LoaderTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        icon = Medias.create("image.png");
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Prepare test.
     */
    @BeforeEach
    public void before()
    {
        Engine.start(new EngineMock("LoaderTest", Version.DEFAULT));
    }

    /**
     * Terminate test.
     */
    @AfterEach
    public void after()
    {
        Engine.terminate();
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        assertPrivateConstructor(Loader.class);
    }

    /**
     * Test with no config.
     */
    @Test
    public void testNullConfig()
    {
        assertThrows(() -> Loader.start(null, SequenceSingleMock.class).await(), "Unexpected null argument !");
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
    @Test
    public void testSequenceTimeout()
    {
        ScreenMock.setScreenWait(true);
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");

        try
        {
            assertThrows(() -> Loader.start(CONFIG, SequenceSingleMock.class).await(), "Unable to get screen ready !");
        }
        finally
        {
            Verbose.info("****************************************************************************************");
            ScreenMock.setScreenWait(false);
        }
    }

    /**
     * Test with screen not ready on rendering.
     */
    @Test
    public void testSequenceRenderScreenUnready()
    {
        final CountDownLatch waitUpdate = new CountDownLatch(1);
        final CountDownLatch waitScreenUnready = new CountDownLatch(1);
        final TaskFuture task = Loader.start(CONFIG, SequenceScreenNotReady.class, waitUpdate, waitScreenUnready);

        assertTimeout(1000L, waitUpdate::await);
        ScreenMock.setScreenWait(true);
        waitScreenUnready.countDown();

        final AtomicReference<Throwable> throwable = new AtomicReference<>();
        final Thread thread = new Thread(() -> task.await());
        thread.setUncaughtExceptionHandler((e, t) -> throwable.set(t));
        thread.start();

        try
        {
            assertTimeout(1000L, () -> thread.join(250L));
            assertNull(throwable.get());
        }
        finally
        {
            ScreenMock.setScreenWait(false);
        }
    }

    /**
     * Test with no sequence.
     */
    @Test
    public void testSequenceNull()
    {
        assertThrows(() -> Loader.start(CONFIG, null).await(), "Unexpected null argument !");
    }

    /**
     * Test with fail sequence.
     */
    @Test
    public void testSequenceFail()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");

        assertThrows(() -> Loader.start(CONFIG, SequenceFailMock.class).await(), "expected failure");

        Verbose.info("****************************************************************************************");
    }

    /**
     * Test with fail next sequence.
     */
    @Test
    public void testSequenceFailNext()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");

        assertThrows(() -> Loader.start(CONFIG, SequenceNextFailMock.class).await(), "expected failure");

        Verbose.info("****************************************************************************************");
    }

    /**
     * Test with malformed sequence.
     */
    @Test
    public void testSequenceMalformed()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");

        final String message = NoSuchMethodException.class.getName()
                               + ": No compatible constructor found for "
                               + SequenceMalformedMock.class.getName()
                               + " with: "
                               + Arrays.asList(ContextWrapper.class);
        assertThrows(() -> Loader.start(CONFIG, SequenceMalformedMock.class).await(), message);

        Verbose.info("****************************************************************************************");
    }

    /**
     * Test interrupted.
     */
    @Test
    public void testInterrupted()
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

        assertTimeout(SequenceInterruptMock.PAUSE_MILLI * 2L, semaphore::acquire);
        assertEquals("com.b3dgs.lionengine.LionEngineException: Task stopped before ended !",
                     exception.get().toString());
    }

    /**
     * Test interrupted unchecked exception.
     */
    @Test
    public void testInterruptedUnchecked()
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

        assertTimeout(SequenceInterruptMock.PAUSE_MILLI * 2L, semaphore::acquire);
        assertTrue(exception.get().getCause() instanceof NullPointerException);

        Verbose.info("****************************************************************************************");
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
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

        assertTrue(Engine.isStarted());
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
     */
    @Test
    public void testScreenInterrupted()
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

        try
        {
            assertTimeout(ScreenMock.READY_TIMEOUT * 2L, semaphore::acquire);
            assertEquals("com.b3dgs.lionengine.LionEngineException: Unable to get screen ready !",
                         exception.get().toString());
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

        final Thread thread = new Thread(() -> Loader.start(config, SequenceEngineTerminateMock.class).await());
        thread.start();

        assertTimeout(1000L, () -> thread.join(500L));
        assertTrue(thread.isAlive());

        SequenceEngineTerminateMock.CLOSE.set(true);

        assertTimeout(1000L, () -> thread.join(500L));
        assertFalse(thread.isAlive());
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
