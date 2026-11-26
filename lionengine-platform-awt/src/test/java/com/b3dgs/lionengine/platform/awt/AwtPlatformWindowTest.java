/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.platform.awt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.LoopDriver;
import com.b3dgs.lionengine.WorkerPool;
import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.ScalingPolicy;

/**
 * Test of the window and of the runtime thread of {@link AwtPlatform}, with a real display (platform test,
 * REQ-PLATFORM-001).
 */
@Tag(AwtFixtures.PLATFORM)
final class AwtPlatformWindowTest
{
    /** Logical frame width. */
    private static final int WIDTH = 160;
    /** Logical frame height. */
    private static final int HEIGHT = 120;
    /** Windowed request. */
    private static final DisplayRequest REQUEST = new DisplayRequest(ScreenMode.WINDOWED,
                                                                     null,
                                                                     ScalingPolicy.FIT,
                                                                     Collections.emptyList());
    /** Nanoseconds per second. */
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    /**
     * Skips the tests without display.
     */
    @BeforeAll
    static void requireDisplay()
    {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    /** Platform of the test, {@code null} if none. */
    private AwtPlatform platform;

    /**
     * Closes the platform of the test and waits for its window to be disposed.
     */
    @AfterEach
    void closePlatform()
    {
        if (platform != null)
        {
            platform.close();
        }
        AwtFixtures.flush();
    }

    /**
     * The start opens a window showing the canvas of the surface capability, sized in device pixels.
     */
    @Test
    void testWindow()
    {
        platform = new AwtPlatform(WIDTH, HEIGHT, REQUEST, AwtFixtures.SINK, true);
        final Canvas canvas = platform.getCapability(AwtSurface.class).getCanvas();
        assertTrue(platform.hasCapability(AwtSurface.class));
        assertNotNull(canvas);
        final InboxSpy inbox = new InboxSpy();

        platform.start(new StoppingDriver(inbox, Integer.MAX_VALUE, null), inbox);
        AwtFixtures.flush();
        platform.iterate();

        final Frame window = platform.getWindow();
        assertTrue(window.isShowing());
        assertSame(window, canvas.getParent());
        assertEquals("", window.getTitle());
        assertTrue(window.isResizable());
        assertNotNull(canvas.getBufferStrategy());
        assertEquals(AwtWindowEvents.toDevice(canvas.getWidth(), AwtWindowEvents.getScaleX(canvas)),
                     platform.getSurfaceWidth());
        assertEquals(AwtWindowEvents.toDevice(canvas.getHeight(), AwtWindowEvents.getScaleY(canvas)),
                     platform.getSurfaceHeight());
        assertEquals(WIDTH, platform.getLogicalWidth());
        assertEquals(HEIGHT, platform.getLogicalHeight());
    }

    /**
     * {@code FULL_WINDOW} makes the window undecorated over the bounds of the default monitor, the new canvas size
     * reaches the platform as a resize, and the display listeners get the result; {@code WINDOWED} restores a decorated
     * window; {@code FULL_SCREEN} is applied when supported and falls back to {@code FULL_WINDOW} otherwise (ADR-0060).
     */
    @Test
    void testScreenModes()
    {
        platform = new AwtPlatform(WIDTH, HEIGHT, REQUEST, AwtFixtures.SINK, true);
        final Canvas canvas = platform.getCapability(AwtSurface.class).getCanvas();
        final InboxSpy inbox = new InboxSpy();
        final List<EffectiveResult> results = new ArrayList<>();
        platform.addDisplayListener(results::add);
        platform.start(new StoppingDriver(inbox, Integer.MAX_VALUE, null), inbox);
        AwtFixtures.flush();
        platform.iterate();
        final Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                    .getDefaultScreenDevice()
                                                    .getDefaultConfiguration()
                                                    .getBounds();

        platform.apply(new DisplayRequest(ScreenMode.FULL_WINDOW, null, ScalingPolicy.FIT, Collections.emptyList()));
        platform.iterate();
        AwtFixtures.flush();
        platform.iterate();

        final Frame window = platform.getWindow();
        assertTrue(window.isUndecorated());
        assertTrue(window.isShowing());
        assertEquals(bounds, window.getBounds());
        assertNotNull(canvas.getBufferStrategy());
        assertEquals(AwtWindowEvents.toDevice(canvas.getWidth(), AwtWindowEvents.getScaleX(canvas)),
                     platform.getSurfaceWidth());
        assertEquals(ScreenMode.FULL_WINDOW, results.get(0).effective().screenMode());
        assertFalse(results.get(0).degraded());

        platform.apply(REQUEST);
        platform.iterate();
        AwtFixtures.flush();
        platform.iterate();

        assertFalse(window.isUndecorated());
        assertTrue(window.isShowing());
        assertEquals(ScreenMode.WINDOWED, platform.getEffectiveResult().effective().screenMode());

        platform.apply(new DisplayRequest(ScreenMode.FULL_SCREEN, null, ScalingPolicy.FIT, Collections.emptyList()));
        platform.iterate();
        AwtFixtures.flush();

        final ScreenMode expected;
        if (platform.isSupported(ScreenMode.FULL_SCREEN))
        {
            expected = ScreenMode.FULL_SCREEN;
        }
        else
        {
            expected = ScreenMode.FULL_WINDOW;
        }
        assertEquals(expected, platform.getEffectiveResult().effective().screenMode());
        assertTrue(window.isUndecorated());
        assertTrue(platform.isSupported(ScreenMode.WINDOWED));
        assertTrue(platform.isSupported(ScreenMode.FULL_WINDOW));
    }

    /**
     * The loop runs on the runtime thread, paced at the refresh rate; the start returns when it stops, after closing
     * the window and the worker pool.
     */
    @Test
    void testRun()
    {
        platform = new AwtPlatform(WIDTH, HEIGHT, REQUEST, AwtFixtures.SINK);
        final InboxSpy inbox = new InboxSpy();
        final StoppingDriver driver = new StoppingDriver(inbox, 6, null);
        final DisplayMode mode = platform.getEffectiveResult().effective().displayMode();
        final int rate = mode.isRefreshRateKnown() ? mode.refreshRate() : 60;
        final long start = System.nanoTime();

        platform.start(driver, inbox);

        final long elapsed = System.nanoTime() - start;
        assertEquals(6, driver.getIterations());
        assertEquals("lionengine-runtime", driver.getThreadName());
        assertTrue(elapsed >= 6 * NANOS_PER_SECOND / rate, String.valueOf(elapsed));
        AwtFixtures.flush();
        assertFalse(platform.getWindow().isDisplayable());
        final WorkerPool pool = platform.getWorkerPool();
        assertThrows(IllegalStateException.class, () -> pool.submit(driver::stop));
        assertThrows(IllegalStateException.class, () -> platform.start(driver, inbox));
    }

    /**
     * A failure of the loop is rethrown by the start, after closing the window.
     */
    @Test
    void testFailure()
    {
        platform = new AwtPlatform(WIDTH, HEIGHT, REQUEST, AwtFixtures.SINK);
        final InboxSpy inbox = new InboxSpy();
        final IllegalStateException failure = new IllegalStateException("Failure of the test");
        final StoppingDriver driver = new StoppingDriver(inbox, 2, failure);

        final IllegalStateException thrown = assertThrows(IllegalStateException.class,
                                                          () -> platform.start(driver, inbox));

        assertSame(failure, thrown);
        AwtFixtures.flush();
        assertFalse(platform.getWindow().isDisplayable());
    }

    /**
     * An interrupted start asks the loop to stop and returns with the interrupt flag set.
     *
     * @throws InterruptedException If interrupted while waiting for the starting thread.
     */
    @Test
    void testInterrupted() throws InterruptedException
    {
        platform = new AwtPlatform(WIDTH, HEIGHT, REQUEST, AwtFixtures.SINK);
        final InboxSpy inbox = new InboxSpy();
        final StoppingDriver driver = new StoppingDriver(inbox, Integer.MAX_VALUE, null);
        final AtomicBoolean interrupted = new AtomicBoolean();
        final Thread starter = new Thread(() ->
        {
            platform.start(driver, inbox);
            interrupted.set(Thread.currentThread().isInterrupted());
        });

        starter.start();
        while (driver.getIterations() == 0)
        {
            Thread.onSpinWait();
        }
        starter.interrupt();
        starter.join();

        assertTrue(interrupted.get());
        assertTrue(driver.isStopRequested());
    }

    /**
     * The start is refused on the AWT event thread.
     */
    @Test
    void testStartOnEventThread()
    {
        platform = new AwtPlatform(WIDTH, HEIGHT, REQUEST, AwtFixtures.SINK, true);
        final InboxSpy inbox = new InboxSpy();
        final AtomicReference<RuntimeException> thrown = new AtomicReference<>();

        AwtFixtures.onEventThread(() ->
        {
            try
            {
                platform.start(new StoppingDriver(inbox, 1, null), inbox);
            }
            catch (final IllegalStateException exception)
            {
                thrown.set(exception);
            }
        });

        assertNotNull(thrown.get());
        assertThrows(IllegalStateException.class, platform::iterate);
    }

    /**
     * Loop driver delivering the inbox, then stopping or failing at a given iteration; thread-safe.
     */
    private static final class StoppingDriver implements LoopDriver
    {
        /** Inbox delivered at each iteration. */
        private final InboxSpy inbox;
        /** Iteration at which the loop stops or fails. */
        private final int last;
        /** Failure thrown at the last iteration, {@code null} to stop instead. */
        private final RuntimeException failure;
        /** Number of iterations. */
        private volatile int iterations;
        /** Name of the thread of the last iteration. */
        private volatile String threadName;
        /** {@code true} once a stop is requested. */
        private volatile boolean stopRequested;
        /** {@code true} while running. */
        private volatile boolean running;

        /**
         * Creates the driver.
         *
         * @param inbox The inbox delivered at each iteration.
         * @param last The iteration at which the loop stops or fails.
         * @param failure The failure thrown at the last iteration, {@code null} to stop instead.
         */
        StoppingDriver(InboxSpy inbox, int last, RuntimeException failure)
        {
            this.inbox = inbox;
            this.last = last;
            this.failure = failure;
        }

        /**
         * Returns the number of iterations.
         *
         * @return The number of iterations.
         */
        int getIterations()
        {
            return iterations;
        }

        /**
         * Returns the name of the thread of the last iteration.
         *
         * @return The thread name.
         */
        String getThreadName()
        {
            return threadName;
        }

        /**
         * Tells whether a stop was requested.
         *
         * @return {@code true} if a stop was requested.
         */
        boolean isStopRequested()
        {
            return stopRequested;
        }

        @Override
        public void iterate()
        {
            threadName = Thread.currentThread().getName();
            inbox.deliverAll();
            final int count = iterations + 1;
            iterations = count;
            if (count == last)
            {
                if (failure != null)
                {
                    throw failure;
                }
                stopRequested = true;
            }
            running = !stopRequested;
        }

        @Override
        public boolean isRunning()
        {
            return running;
        }

        @Override
        public void pause()
        {
            // Not used by these tests.
        }

        @Override
        public void resume()
        {
            // Not used by these tests.
        }

        @Override
        public void stop()
        {
            stopRequested = true;
        }

        @Override
        public void setTimeScale(double scale)
        {
            // Not used by these tests.
        }
    }
}
