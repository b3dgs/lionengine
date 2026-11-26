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
package com.b3dgs.lionengine.platform;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.LogSink;
import com.b3dgs.lionengine.LoopDriver;
import com.b3dgs.lionengine.WorkerPool;
import com.b3dgs.lionengine.render.RenderBackend;

/**
 * The platform object: window or surface, loop thread, lifecycle events, capabilities and display state (ADR-0008,
 * ADR-0018, ADR-0020, ADR-0031, ADR-0040, ADR-0046, REQ-FE-001, REQ-RT-008, REQ-PLATFORM-002).
 *
 * <p>
 * The application creates the platform with its configuration (logical frame size, initial display request, log
 * sink), wires its modules explicitly (nothing is discovered), then calls {@link #start(LoopDriver, Inbox)}.
 * Implementations: {@code platform-awt} on desktop, {@code platform-android}, and the headless platform of
 * {@code test-support}.
 * </p>
 *
 * <p>
 * Threads: every method is called on the runtime thread, except {@link #start(LoopDriver, Inbox)}, called on the
 * application thread. The platform's own threads only post {@link PlatformEvent}s to the inbox and call
 * {@link com.b3dgs.lionengine.LoopControl} requests.
 * </p>
 *
 * @since 11.0.0
 */
public interface Platform
{
    /**
     * Returns a capability of this platform, a typed contract defined in an {@code *-api} module or in the
     * implementation module (for example {@code AwtSurface}). Comparing class literals is not reflection.
     *
     * @param <T> The capability type.
     * @param type The capability type (never {@code null}).
     * @return The capability, {@code null} if this platform does not support it.
     */
    <T> T getCapability(Class<T> type);

    /**
     * Tells whether this platform supports a capability.
     *
     * @param type The capability type (never {@code null}).
     * @return {@code true} if {@link #getCapability(Class)} returns a capability for this type.
     */
    boolean hasCapability(Class<?> type);

    /**
     * Creates the window or surface and runs the loop on the thread this platform requires, by calling
     * {@link LoopDriver#iterate()} while the loop runs. Whether this call returns at once or blocks until the loop
     * stops is documented by each implementation.
     *
     * @param driver The game loop (never {@code null}).
     * @param inbox The inbox of the loop, to which platform events are posted (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}.
     * @throws IllegalStateException If the platform is already started.
     */
    void start(LoopDriver driver, Inbox inbox);

    /**
     * Attaches the render backend, before the start (ADR-0020, ADR-0046): the platform asks it which output filters it
     * implements, and skips and reports the others in its effective result. Attaching another backend replaces the
     * previous one. Without a backend, no filter is applied and this is reported.
     *
     * @param backend The render backend (never {@code null}).
     * @throws IllegalArgumentException If {@code backend} is {@code null}.
     * @throws IllegalStateException If the platform is started.
     */
    void attach(RenderBackend backend);

    /**
     * Returns the loading worker pool owned by this platform: 2 workers on desktop and 1 on Android by default,
     * {@code 0} meaning synchronous.
     *
     * @return The worker pool (never {@code null}).
     */
    WorkerPool getWorkerPool();

    /**
     * Returns the log sink of this platform.
     *
     * @return The log sink (never {@code null}).
     */
    LogSink getLogSink();

    /**
     * Adds a close listener; one listener returning {@code false} vetoes a close request. A listener added during a
     * delivery takes effect after it.
     *
     * @param listener The listener (never {@code null}).
     * @throws IllegalArgumentException If {@code listener} is {@code null}.
     */
    void addCloseListener(CloseListener listener);

    /**
     * Removes a close listener; no effect if it was not added.
     *
     * @param listener The listener.
     */
    void removeCloseListener(CloseListener listener);

    /**
     * Adds a display listener, called on the runtime thread at the inbox delivery that follows each applied display
     * request, and each surface resize that changes the effective result (ADR-0060). Listeners are called in the order
     * they were added; a listener added or removed during a delivery takes effect after it. Nothing is delivered before
     * the start.
     *
     * @param listener The listener (never {@code null}).
     * @throws IllegalArgumentException If {@code listener} is {@code null}.
     */
    void addDisplayListener(DisplayListener listener);

    /**
     * Removes a display listener; no effect if it was not added.
     *
     * @param listener The listener.
     */
    void removeDisplayListener(DisplayListener listener);

    /**
     * Tells whether the platform can apply a screen mode (ADR-0060). The answer does not change while the platform
     * lives, and at least one mode is supported. A request for another mode falls back following the order of
     * ADR-0006, reported in the effective result.
     *
     * @param mode The screen mode (never {@code null}).
     * @return {@code true} if the platform applies this mode.
     * @throws IllegalArgumentException If {@code mode} is {@code null}.
     */
    boolean isSupported(ScreenMode mode);

    /**
     * Requests display settings. Never blocks the update step and never throws for a missing capability: the change
     * is applied at the next frame boundary, best effort, and its {@link EffectiveResult} is delivered through the
     * inbox to the display listeners; an unsupported screen mode falls back (ADR-0006, ADR-0060).
     *
     * @param request The requested settings (never {@code null}).
     * @return The accepted request, as given.
     * @throws IllegalArgumentException If {@code request} is {@code null}.
     */
    DisplayRequest apply(DisplayRequest request);

    /**
     * Returns the effective result of the last applied request.
     *
     * @return The current effective result (never {@code null}).
     */
    EffectiveResult getEffectiveResult();

    /**
     * Returns where the logical frame is currently presented on the output surface.
     *
     * @return The current presentation geometry (never {@code null}).
     */
    PresentationGeometry getPresentationGeometry();

    /**
     * Returns the width of the logical frame, fixed at creation.
     *
     * @return The logical frame width in pixels (positive).
     */
    int getLogicalWidth();

    /**
     * Returns the height of the logical frame, fixed at creation.
     *
     * @return The logical frame height in pixels (positive).
     */
    int getLogicalHeight();

    /**
     * Returns the width of the output surface.
     *
     * @return The surface width in device pixels (not negative).
     */
    int getSurfaceWidth();

    /**
     * Returns the height of the output surface.
     *
     * @return The surface height in device pixels (not negative).
     */
    int getSurfaceHeight();
}
