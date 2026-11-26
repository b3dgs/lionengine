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

import java.awt.Frame;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.LockSupport;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.LogSink;
import com.b3dgs.lionengine.LoopDriver;
import com.b3dgs.lionengine.WorkerPool;
import com.b3dgs.lionengine.platform.CloseListener;
import com.b3dgs.lionengine.platform.DisplayListener;
import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.Platform;
import com.b3dgs.lionengine.platform.PlatformEvent;
import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.RenderBackend;

/**
 * Desktop platform with the JDK AWT: a window on the default monitor, the loop on a dedicated thread, AWT events
 * converted into platform events (ADR-0008, ADR-0018, ADR-0022, ADR-0031, ADR-0040, ADR-0043, ADR-0046,
 * REQ-PLATFORM-001, REQ-PLATFORM-002, REQ-FE-002, REQ-RT-008).
 *
 * <p>
 * The application creates it with the logical frame size, the initial display request and the log sink, attaches the
 * render backend ({@code render-awt}), then calls {@link #start(LoopDriver, Inbox)}, which opens the window, runs the
 * loop on a thread named {@code lionengine-runtime}, and returns when the loop stops.
 * </p>
 *
 * <p>
 * Window: a resizable window on the default monitor, without title; its canvas, given to the AWT modules by the
 * {@link AwtSurface} capability, starts at the logical frame size in device pixels. Sizes are reported in device
 * pixels: AWT sizes are multiplied by the scale of the canvas' graphics configuration (ADR-0043). Rendering is active
 * (repaint events are ignored), and the loop is paced at the refresh rate of the default monitor, or
 * 60 Hz when the monitor does not report it, because an AWT window has no vertical
 * synchronization.
 * </p>
 *
 * <p>
 * Events: closing the window is a close request, which stops the loop unless a close listener vetoes it (listeners are
 * asked in the order they were added, until the first veto); iconifying the window goes to the background, which
 * pauses the loop and slows its frames to 10 Hz, and restoring it comes back to the foreground, which resumes it; the
 * focus of the window gives the focus events; a resize of the canvas changes the surface, never the logical frame.
 * </p>
 *
 * <p>
 * Display: until the display-mode batches, the window is always {@code WINDOWED} and the display mode is the current
 * one of the default monitor; another request is applied as far as possible and reported as degraded. Requests are
 * applied at the next frame boundary. The worker pool has 2 daemon workers and is closed with the loop.
 * </p>
 *
 * <p>
 * Threads: the constructor and {@link #start(LoopDriver, Inbox)} are called on the application thread, never on the
 * AWT event thread; the window is created there through {@code EventQueue.invokeAndWait}. Every other
 * method is called on the runtime thread.
 * </p>
 *
 * @since 11.0.0
 */
public final class AwtPlatform implements Platform
{
    /** Name of the thread running the loop. */
    private static final String RUNTIME_THREAD = "lionengine-runtime";
    /** Refresh rate used to pace the frames when the default monitor does not report its own, in Hz. */
    private static final int DEFAULT_REFRESH_RATE = 60;
    /** Frame rate while the window is in the background, in Hz (ADR-0052). */
    private static final int BACKGROUND_RATE = 10;
    /** Number of loading workers, the desktop default (ADR-0031). */
    private static final int WORKERS = 2;
    /** Nanoseconds per second. */
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    /** Error message of a second start, or an attachment after the start. */
    private static final String ERROR_STARTED = "AWT platform already started";

    /**
     * Computes the deadline of the next frame. A frame late by more than one period starts a new schedule instead of
     * catching up with shorter frames.
     *
     * @param deadline The deadline of the current frame, in nanoseconds.
     * @param period The frame period, in nanoseconds.
     * @param now The current time, in nanoseconds.
     * @return The deadline of the next frame, in nanoseconds.
     */
    static long nextDeadline(long deadline, long period, long now)
    {
        final long next = deadline + period;
        if (now - next > period)
        {
            return now;
        }
        return next;
    }

    /** Close listeners; iterated over a snapshot, so that a change during a delivery takes effect after it. */
    private final List<CloseListener> listeners = new CopyOnWriteArrayList<>();
    /** Worker pool. */
    private final AwtWorkerPool workerPool = new AwtWorkerPool(WORKERS);
    /** Surface capability. */
    private final AwtSurface surface;
    /** Log sink. */
    private final LogSink logSink;
    /** Logical frame width in pixels. */
    private final int logicalWidth;
    /** Logical frame height in pixels. */
    private final int logicalHeight;
    /** Window and canvas. */
    private final AwtWindow window;
    /** Display state. */
    private final AwtDisplay display;
    /** {@code true} if the frames are run by {@link #iterate()} instead of the runtime thread. */
    private final boolean manual;
    /** {@code true} while the window is in the background: the loop is paused and paced slowly. */
    private boolean background;
    /** Request applied at the next frame boundary, {@code null} if none. */
    private DisplayRequest pending;
    /** Loop driver, {@code null} until started. */
    private LoopDriver driver;
    /** Inbox of the loop, {@code null} until started. */
    private Inbox inbox;
    /**
     * Failure of the loop, {@code null} if none: written by the handler of uncaught exceptions of the runtime thread,
     * which runs on that thread before it ends, and read after joining it.
     */
    private Throwable failure;

    /**
     * Creates a desktop platform; the window is opened by {@link #start(LoopDriver, Inbox)}.
     *
     * @param logicalWidth The logical frame width in pixels (positive).
     * @param logicalHeight The logical frame height in pixels (positive).
     * @param request The initial display request, applied at once (never {@code null}).
     * @param logSink The log sink of the platform (never {@code null}).
     * @throws IllegalArgumentException If a size is not positive, or {@code request} or {@code logSink} is
     *             {@code null}.
     * @throws IllegalStateException If the environment has no display.
     */
    public AwtPlatform(int logicalWidth, int logicalHeight, DisplayRequest request, LogSink logSink)
    {
        this(logicalWidth, logicalHeight, request, logSink, false);
    }

    /**
     * Creates a desktop platform.
     *
     * @param logicalWidth The logical frame width in pixels (positive).
     * @param logicalHeight The logical frame height in pixels (positive).
     * @param request The initial display request, applied at once (never {@code null}).
     * @param logSink The log sink of the platform (never {@code null}).
     * @param manual {@code true} if the start only opens the window and the frames are run by {@link #iterate()}, for
     *            the platform tests.
     * @throws IllegalArgumentException If a size is not positive, or {@code request} or {@code logSink} is
     *             {@code null}.
     * @throws IllegalStateException If the environment has no display.
     */
    AwtPlatform(int logicalWidth, int logicalHeight, DisplayRequest request, LogSink logSink, boolean manual)
    {
        if (logicalWidth <= 0 || logicalHeight <= 0 || request == null || logSink == null)
        {
            throw new IllegalArgumentException("Invalid AWT platform: " + logicalWidth + 'x' + logicalHeight);
        }
        AwtWindow.checkDisplay();
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        this.logSink = logSink;
        this.manual = manual;
        display = AwtDisplay.create(logicalWidth, logicalHeight, request);
        window = AwtWindow.create(logicalWidth, logicalHeight);
        surface = window::getCanvas;
    }

    /**
     * Posts an event to the inbox, as the AWT event thread does.
     *
     * @param kind The kind of event.
     * @param surfaceWidth The surface width after the event, in device pixels.
     * @param surfaceHeight The surface height after the event, in device pixels.
     */
    void post(PlatformEvent.Kind kind, int surfaceWidth, int surfaceHeight)
    {
        inbox.post(new AwtEvent(this, kind, surfaceWidth, surfaceHeight));
    }

    /**
     * Applies a delivered event, on the runtime thread.
     *
     * @param event The event.
     */
    void deliver(AwtEvent event)
    {
        switch (event.getKind())
        {
            case SURFACE_RESIZED ->
            {
                if (display.resize(event.getSurfaceWidth(), event.getSurfaceHeight()))
                {
                    display.post(inbox);
                }
            }
            case BACKGROUND ->
            {
                background = true;
                driver.pause();
            }
            case FOREGROUND ->
            {
                background = false;
                driver.resume();
            }
            case CLOSE_REQUESTED -> requestClose();
            default ->
            {
                // Focus and context events: nothing to apply on the platform.
            }
        }
    }

    /**
     * Runs one frame on the calling thread, for the platform tests: the frame boundary, then one iteration of the loop.
     *
     * @throws IllegalStateException If the platform is not started, or the loop is stopped or failed.
     */
    void iterate()
    {
        if (driver == null)
        {
            throw new IllegalStateException("AWT platform not started");
        }
        applyPending();
        driver.iterate();
    }

    /**
     * Closes the window and the worker pool. The window is disposed on the AWT event thread, after the events already
     * queued.
     */
    void close()
    {
        workerPool.close();
        window.dispose();
    }

    /**
     * Returns the window, for the platform tests.
     *
     * @return The window, {@code null} before the start.
     */
    Frame getWindow()
    {
        return window.getFrame();
    }

    /**
     * Runs the loop on the runtime thread and waits for its end. A failure of the loop is caught by the handler of
     * uncaught exceptions of the runtime thread, then rethrown here.
     *
     * @throws RuntimeException The failure of the loop.
     * @throws Error The failure of the loop.
     */
    private void run()
    {
        final Thread runtime = new Thread(this::loop, RUNTIME_THREAD);
        runtime.setUncaughtExceptionHandler((thread, error) -> failure = error);
        runtime.start();
        try
        {
            runtime.join();
        }
        catch (InterruptedException _)
        {
            driver.stop();
            Thread.currentThread().interrupt();
            return;
        }
        if (failure instanceof final RuntimeException exception)
        {
            throw exception;
        }
        if (failure instanceof final Error error)
        {
            throw error;
        }
    }

    /**
     * Body of the runtime thread: frames paced at the refresh rate until the loop stops or fails, then the window and
     * the worker pool are closed; a failure leaves the thread to its handler of uncaught exceptions.
     */
    private void loop()
    {
        final DisplayMode mode = display.getDisplayMode();
        final int rate = mode.isRefreshRateKnown() ? mode.refreshRate() : DEFAULT_REFRESH_RATE;
        final long period = NANOS_PER_SECOND / rate;
        final long backgroundPeriod = NANOS_PER_SECOND / Math.min(rate, BACKGROUND_RATE);
        long deadline = System.nanoTime();
        try
        {
            do
            {
                applyPending();
                driver.iterate();
                deadline = nextDeadline(deadline, background ? backgroundPeriod : period, System.nanoTime());
                long remaining = deadline - System.nanoTime();
                while (remaining > 0L)
                {
                    LockSupport.parkNanos(remaining);
                    remaining = deadline - System.nanoTime();
                }
            }
            while (driver.isRunning());
        }
        finally
        {
            close();
        }
    }

    /**
     * Applies the pending request, at the frame boundary.
     */
    private void applyPending()
    {
        if (pending != null)
        {
            final DisplayRequest request = pending;
            pending = null;
            display.apply(request);
            window.setScreenMode(display.getScreenMode());
            display.post(inbox);
        }
    }

    /**
     * Asks the close listeners, in the order they were added, until the first veto; stops the loop without veto.
     */
    private void requestClose()
    {
        for (final CloseListener listener : listeners)
        {
            if (!listener.onCloseRequested())
            {
                return;
            }
        }
        driver.stop();
    }

    @Override
    public <T> T getCapability(Class<T> type)
    {
        if (AwtSurface.class.equals(type))
        {
            return type.cast(surface);
        }
        return null;
    }

    @Override
    public boolean hasCapability(Class<?> type)
    {
        return AwtSurface.class.equals(type);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Opens the window, runs the loop on the thread {@code lionengine-runtime}, and returns when the loop stops, after
     * closing the window and the worker pool. If the waiting thread is interrupted, the loop is asked to stop and this
     * method returns at once, with the interrupt flag set.
     * </p>
     *
     * @throws IllegalStateException If the platform is already started, or this method is called on the AWT event
     *             thread.
     * @throws RuntimeException The failure of the loop (ADR-0017), rethrown as it was thrown on the runtime thread.
     */
    @Override
    public void start(LoopDriver driver, Inbox inbox)
    {
        if (driver == null || inbox == null)
        {
            throw new IllegalArgumentException("Driver and inbox must not be null");
        }
        if (this.driver != null)
        {
            throw new IllegalStateException(ERROR_STARTED);
        }
        if (AwtWindow.isEventThread())
        {
            throw new IllegalStateException("The AWT platform is not started on the AWT event thread");
        }
        this.driver = driver;
        this.inbox = inbox;
        window.open(this::post, display.getScreenMode());
        if (!manual)
        {
            run();
        }
    }

    @Override
    public void attach(RenderBackend backend)
    {
        if (backend == null)
        {
            throw new IllegalArgumentException("Render backend must not be null");
        }
        if (driver != null)
        {
            throw new IllegalStateException(ERROR_STARTED);
        }
        display.attach(backend);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The pool has 2 daemon workers, started at the first submissions, and is closed when the loop ends.
     * </p>
     */
    @Override
    public WorkerPool getWorkerPool()
    {
        return workerPool;
    }

    @Override
    public LogSink getLogSink()
    {
        return logSink;
    }

    @Override
    public void addCloseListener(CloseListener listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("Close listener must not be null");
        }
        listeners.add(listener);
    }

    @Override
    public void removeCloseListener(CloseListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void addDisplayListener(DisplayListener listener)
    {
        display.addListener(listener);
    }

    @Override
    public void removeDisplayListener(DisplayListener listener)
    {
        display.removeListener(listener);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * {@code WINDOWED} and {@code FULL_WINDOW} are always supported; {@code FULL_SCREEN} when the default monitor
     * supports exclusive full screen (ADR-0060).
     * </p>
     */
    @Override
    public boolean isSupported(ScreenMode mode)
    {
        return display.isSupported(mode);
    }

    @Override
    public DisplayRequest apply(DisplayRequest request)
    {
        if (request == null)
        {
            throw new IllegalArgumentException("Display request must not be null");
        }
        pending = request;
        return request;
    }

    @Override
    public EffectiveResult getEffectiveResult()
    {
        return display.getResult();
    }

    @Override
    public PresentationGeometry getPresentationGeometry()
    {
        return display.getGeometry();
    }

    @Override
    public int getLogicalWidth()
    {
        return logicalWidth;
    }

    @Override
    public int getLogicalHeight()
    {
        return logicalHeight;
    }

    @Override
    public int getSurfaceWidth()
    {
        return display.getSurfaceWidth();
    }

    @Override
    public int getSurfaceHeight()
    {
        return display.getSurfaceHeight();
    }
}
