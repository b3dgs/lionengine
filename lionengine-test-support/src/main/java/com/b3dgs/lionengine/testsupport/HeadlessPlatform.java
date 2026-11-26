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
package com.b3dgs.lionengine.testsupport;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.FakeWorkerPool;
import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.LogSink;
import com.b3dgs.lionengine.LoopDriver;
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
 * Platform without window, driven by the test, for headless tests of the engine and of games (ADR-0045,
 * REQ-TEST-001, REQ-FE-001, REQ-RT-008, REQ-PLATFORM-002).
 *
 * <p>
 * The test creates it, starts it with the loop (usually a {@code GameLoop} on a controllable clock), then runs one
 * frame at a time with {@link #iterate()}: the frame boundary of the platform (a pending display request is applied),
 * then one iteration of the loop, which delivers the events posted with {@link #post(PlatformEvent.Kind)} and
 * {@link #resize(int, int)} as a real platform's threads would post them. Capabilities are set by the test with
 * {@link #setCapability(Class, Object)}.
 * </p>
 *
 * <p>
 * Behavior: going to the background pauses the loop and coming back resumes it; a close request stops it unless a close
 * listener vetoes it (listeners are asked in the order they were added, and the first veto ends the request); a resize
 * changes the surface, never the logical frame. The surface starts at the logical frame size, and so does the display
 * mode of its imaginary monitor. Every screen mode is supported and the monitor offers that one display mode, until the
 * test sets fake capabilities with {@link #setScreenModeSupported(ScreenMode, boolean)} and
 * {@link #setDisplayModes(List)}; an unsupported screen mode falls back and a display mode that is not offered is
 * replaced by the nearest one (ADR-0060, ADR-0061). The output filters are those the attached render backend
 * implements, and the others are reported as skipped in the effective result. The worker pool
 * is a {@link FakeWorkerPool} of 2 workers that runs tasks only when the test asks it to. Not thread-safe: the test
 * drives it from one thread, its runtime thread.
 * </p>
 *
 * @since 11.0.0
 */
public final class HeadlessPlatform implements Platform
{
    /** Number of workers of the pool, as the desktop default. */
    private static final int WORKERS = 2;
    /** Error message of a second start, or an attachment after the start. */
    private static final String ERROR_STARTED = "Headless platform already started";
    /** No listener. */
    private static final CloseListener[] NO_LISTENER = new CloseListener[0];

    /** Capabilities by type. */
    private final Map<Class<?>, Object> capabilities = new HashMap<>();
    /** Reused events by kind. */
    private final Map<PlatformEvent.Kind, HeadlessEvent> events = new EnumMap<>(PlatformEvent.Kind.class);
    /** Worker pool. */
    private final FakeWorkerPool workerPool = new FakeWorkerPool(WORKERS);
    /** Log sink. */
    private final LogSink logSink;
    /** Logical frame width in pixels. */
    private final int logicalWidth;
    /** Logical frame height in pixels. */
    private final int logicalHeight;
    /** Close listeners; replaced, never modified, so that a change during a delivery takes effect after it. */
    private CloseListener[] listeners = NO_LISTENER;
    /** Display state. */
    private final HeadlessDisplay display;
    /** Request applied at the next frame boundary, {@code null} if none. */
    private DisplayRequest pending;
    /** Loop driver, {@code null} until started. */
    private LoopDriver driver;
    /** Inbox of the loop, {@code null} until started. */
    private Inbox inbox;

    /**
     * Creates a headless platform; its surface and its display mode have the logical frame size.
     *
     * @param logicalWidth The logical frame width in pixels (positive).
     * @param logicalHeight The logical frame height in pixels (positive).
     * @param request The initial display request, applied at once (never {@code null}).
     * @param logSink The log sink of the platform (never {@code null}).
     * @throws IllegalArgumentException If a size is not positive, or {@code request} or {@code logSink} is
     *             {@code null}.
     */
    public HeadlessPlatform(int logicalWidth, int logicalHeight, DisplayRequest request, LogSink logSink)
    {
        if (logicalWidth <= 0 || logicalHeight <= 0 || request == null || logSink == null)
        {
            throw new IllegalArgumentException("Invalid headless platform: " + logicalWidth + 'x' + logicalHeight);
        }
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        this.logSink = logSink;
        display = new HeadlessDisplay(logicalWidth, logicalHeight, request);
        for (final PlatformEvent.Kind kind : PlatformEvent.Kind.values())
        {
            events.put(kind, new HeadlessEvent(this, kind));
        }
    }

    /**
     * Sets a capability of this platform, as the platform modules provide theirs (ADR-0020).
     *
     * @param <T> The capability type.
     * @param type The capability type (never {@code null}).
     * @param capability The capability, {@code null} to remove it.
     * @throws IllegalArgumentException If {@code type} is {@code null}.
     */
    public <T> void setCapability(Class<T> type, T capability)
    {
        if (type == null)
        {
            throw new IllegalArgumentException("Capability type must not be null");
        }
        if (capability == null)
        {
            capabilities.remove(type);
        }
        else
        {
            capabilities.put(type, capability);
        }
    }

    /**
     * Sets whether a screen mode is supported, as a fake capability (ADR-0060): every mode is supported by default. A
     * request for an unsupported mode falls back at its frame boundary; the request already applied is not resolved
     * again.
     *
     * @param mode The screen mode (never {@code null}).
     * @param supported {@code true} if the platform supports it.
     * @throws IllegalArgumentException If {@code mode} is {@code null}.
     * @throws IllegalStateException If it would leave no supported mode.
     */
    public void setScreenModeSupported(ScreenMode mode, boolean supported)
    {
        if (mode == null)
        {
            throw new IllegalArgumentException("Screen mode must not be null");
        }
        final Set<ScreenMode> modes = EnumSet.noneOf(ScreenMode.class);
        for (final ScreenMode other : ScreenMode.values())
        {
            if (other == mode ? supported : display.isSupported(other))
            {
                modes.add(other);
            }
        }
        if (modes.isEmpty())
        {
            throw new IllegalStateException("At least one screen mode must stay supported: " + mode);
        }
        display.setSupported(modes);
    }

    /**
     * Sets the display modes of the fake monitor (ADR-0061): by default, one mode of the logical frame size with an
     * unknown refresh rate and bit depth. The current display mode is kept if it is listed, otherwise it becomes the
     * first listed mode; a requested mode is selected among them at its frame boundary, the request already applied is
     * not resolved again.
     *
     * @param modes The display modes (never {@code null}, not empty, no {@code null} element); duplicates are removed.
     * @throws IllegalArgumentException If {@code modes} is {@code null}, empty or holds {@code null}.
     */
    public void setDisplayModes(List<DisplayMode> modes)
    {
        display.setDisplayModes(modes);
    }

    /**
     * Posts an event to the inbox, as a thread of a real platform would; it is applied when the loop delivers it.
     *
     * @param kind The kind of event, not {@link PlatformEvent.Kind#SURFACE_RESIZED} (use {@link #resize(int, int)}).
     * @return {@code true} if the inbox accepted the event.
     * @throws IllegalArgumentException If {@code kind} is {@code null} or a resize.
     * @throws IllegalStateException If the platform is not started.
     */
    public boolean post(PlatformEvent.Kind kind)
    {
        if (kind == null || kind == PlatformEvent.Kind.SURFACE_RESIZED)
        {
            throw new IllegalArgumentException("Event kind not posted directly: " + kind);
        }
        checkStarted();
        return inbox.post(events.get(kind));
    }

    /**
     * Posts a resize of the surface. The event is reused: when two resizes are posted before a delivery, both
     * deliveries apply the last size.
     *
     * @param width The new surface width in device pixels (not negative).
     * @param height The new surface height in device pixels (not negative).
     * @return {@code true} if the inbox accepted the event.
     * @throws IllegalArgumentException If a size is negative.
     * @throws IllegalStateException If the platform is not started.
     */
    public boolean resize(int width, int height)
    {
        if (width < 0 || height < 0)
        {
            throw new IllegalArgumentException("Negative surface size: " + width + 'x' + height);
        }
        checkStarted();
        final HeadlessEvent event = events.get(PlatformEvent.Kind.SURFACE_RESIZED);
        event.setSurface(width, height);
        return inbox.post(event);
    }

    /**
     * Runs one frame: applies a pending display request at the frame boundary, then runs one iteration of the loop.
     *
     * @throws IllegalStateException If the platform is not started, or the loop is stopped or failed.
     */
    public void iterate()
    {
        checkStarted();
        if (pending != null)
        {
            final DisplayRequest request = pending;
            pending = null;
            display.apply(request);
            display.post(inbox);
        }
        driver.iterate();
    }

    /**
     * Returns the worker pool, which runs tasks only when the test asks it to.
     *
     * @return The worker pool of 2 workers.
     */
    @Override
    public FakeWorkerPool getWorkerPool()
    {
        return workerPool;
    }

    /**
     * Applies a delivered event, on the runtime thread.
     *
     * @param event The event.
     */
    void deliver(HeadlessEvent event)
    {
        switch (event.getKind())
        {
            case SURFACE_RESIZED:
                if (display.resize(event.getSurfaceWidth(), event.getSurfaceHeight()))
                {
                    display.post(inbox);
                }
                break;
            case BACKGROUND:
                driver.pause();
                break;
            case FOREGROUND:
                driver.resume();
                break;
            case CLOSE_REQUESTED:
                if (isCloseAccepted())
                {
                    driver.stop();
                }
                break;
            default:
                // Focus and context events: no effect on a headless platform.
                break;
        }
    }

    /**
     * Asks the close listeners, in the order they were added, until the first veto.
     *
     * @return {@code true} if no listener vetoed the close.
     */
    private boolean isCloseAccepted()
    {
        final CloseListener[] current = listeners;
        for (final CloseListener listener : current)
        {
            if (!listener.onCloseRequested())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks that the platform is started.
     *
     * @throws IllegalStateException If the platform is not started.
     */
    private void checkStarted()
    {
        if (driver == null)
        {
            throw new IllegalStateException("Headless platform not started");
        }
    }

    @Override
    public <T> T getCapability(Class<T> type)
    {
        return type.cast(capabilities.get(type));
    }

    @Override
    public boolean hasCapability(Class<?> type)
    {
        return capabilities.containsKey(type);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Returns at once: the test runs the frames with {@link #iterate()}.
     * </p>
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
        this.driver = driver;
        this.inbox = inbox;
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
        final CloseListener[] added = new CloseListener[listeners.length + 1];
        System.arraycopy(listeners, 0, added, 0, listeners.length);
        added[listeners.length] = listener;
        listeners = added;
    }

    @Override
    public void removeCloseListener(CloseListener listener)
    {
        for (int i = 0; i < listeners.length; i++)
        {
            if (listeners[i] == listener)
            {
                final CloseListener[] removed = new CloseListener[listeners.length - 1];
                System.arraycopy(listeners, 0, removed, 0, i);
                System.arraycopy(listeners, i + 1, removed, i, listeners.length - i - 1);
                listeners = removed;
                return;
            }
        }
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

    @Override
    public List<DisplayMode> getDisplayModes()
    {
        return display.getDisplayModes();
    }

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
        return display.getEffectiveResult();
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
