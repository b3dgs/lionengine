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
package com.b3dgs.lionengine.runtime;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LoopDriver;

/**
 * Fixed-step game loop (ADR-0017, ADR-0018, ADR-0031, ADR-0040, REQ-LOOP-001 to REQ-LOOP-012, REQ-RT-001, REQ-RT-002,
 * REQ-RT-009).
 *
 * <p>
 * The platform calls {@link #iterate()} on its runtime thread while {@link #isRunning()}; the engine starts no thread.
 * One iteration, in this exact order:
 * </p>
 * <ol>
 * <li>read the control requests: pause, resume (the accumulator is cleared, so there is no catch-up) and time
 * scale;</li>
 * <li>deliver the inbox: every accepted event, in arrival order;</li>
 * <li>if paused, discard the elapsed time and go to step 6;</li>
 * <li>add the elapsed time: snapped to 1 to 4 steps when within the tolerance, capped at the lag cap (the excess is
 * counted and given to the {@link LagListener}), multiplied by the time scale and rounded to nanoseconds, the
 * remainder being carried;</li>
 * <li>while at least one step is accumulated, call {@link UpdateCallback#update(double)} with the constant step in
 * seconds;</li>
 * <li>call {@link RenderCallback#render(double, double)} once, with {@code alpha} in {@code [0, 1)} (frozen while
 * paused) and the real elapsed time in seconds, capped, whatever the pause and the time scale;</li>
 * <li>if a stop was requested, stop.</li>
 * </ol>
 *
 * <p>
 * The first iteration uses an elapsed time of {@code 0}. Fail-fast: an exception thrown by an update, a render, an
 * inbox event or the lag listener puts the loop in {@link LoopState#FAILED} and reaches the caller of
 * {@link #iterate()}; no render follows a failed update.
 * </p>
 *
 * <p>
 * Threads: {@link #iterate()} and the other methods on the runtime thread; the control requests
 * ({@link #pause()}, {@link #resume()}, {@link #stop()}, {@link #setTimeScale(double)}) and posting to the
 * {@link #getInbox() inbox} from any thread. An iteration allocates nothing in steady state.
 * </p>
 *
 * @since 11.0.0
 */
public final class GameLoop implements LoopDriver
{
    /**
     * Checks that a constructor argument is not {@code null}.
     *
     * @param argument The argument.
     * @param name The argument name, for the message.
     * @throws IllegalArgumentException If the argument is {@code null}.
     */
    private static void checkArgument(Object argument, String name)
    {
        if (argument == null)
        {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }

    /** Time source. */
    private final Clock clock;
    /** Simulation step. */
    private final UpdateCallback update;
    /** Presentation of a frame. */
    private final RenderCallback render;
    /** Duration of the fixed step given to the updates, in seconds. */
    private final double stepSeconds;
    /** Accumulated simulation time. */
    private final FixedStepAccumulator time;
    /** Events from other threads. */
    private final BoundedInbox inbox;
    /** Requested pause, read at the next frame boundary. */
    private volatile boolean pauseRequested;
    /** Requested stop, read at the end of the iteration. */
    private volatile boolean stopRequested;
    /** Requested time scale, read at the next frame boundary. */
    private volatile double requestedTimeScale;
    /** Current state. */
    private LoopState state = LoopState.CREATED;
    /** Listener of the dropped lag, {@code null} if none. */
    private LagListener lagListener;
    /** Whether the clock was read once. */
    private boolean measured;
    /** Clock time of the previous iteration, in nanoseconds. */
    private long lastNanos;
    /** Presentation factor given to the last render. */
    private double alpha;
    /** Number of iterations that dropped time. */
    private long droppedLagCount;
    /** Total dropped time, in seconds. */
    private double droppedLagSeconds;

    /**
     * Creates a loop measuring time with {@code System.nanoTime}.
     *
     * @param config The configuration (never {@code null}).
     * @param log The log of the inbox drop reports (never {@code null}).
     * @param update The simulation step (never {@code null}).
     * @param render The presentation of a frame (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}.
     */
    public GameLoop(LoopConfig config, Log log, UpdateCallback update, RenderCallback render)
    {
        this(config, System::nanoTime, log, update, render);
    }

    /**
     * Creates a loop with its time source, for example a controllable clock in tests.
     *
     * @param config The configuration (never {@code null}).
     * @param clock The monotonic time source (never {@code null}).
     * @param log The log of the inbox drop reports (never {@code null}).
     * @param update The simulation step (never {@code null}).
     * @param render The presentation of a frame (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}.
     */
    public GameLoop(LoopConfig config, Clock clock, Log log, UpdateCallback update, RenderCallback render)
    {
        checkArgument(config, "Configuration");
        checkArgument(clock, "Clock");
        checkArgument(log, "Log");
        checkArgument(update, "Update callback");
        checkArgument(render, "Render callback");

        this.clock = clock;
        this.update = update;
        this.render = render;
        stepSeconds = config.stepSeconds();
        time = new FixedStepAccumulator(config);
        inbox = new BoundedInbox(config.inboxCapacity(), log);
        requestedTimeScale = config.timeScale();
    }

    /**
     * Returns the inbox of the loop, to hand to the event producers (platform, input adapters, loaders).
     *
     * @return The inbox (never {@code null}); delivered at the start of each iteration.
     */
    public Inbox getInbox()
    {
        return inbox;
    }

    /**
     * Returns the state.
     *
     * @return The state (never {@code null}).
     */
    public LoopState getState()
    {
        return state;
    }

    /**
     * Returns the time scale applied by the last iteration.
     *
     * @return The time scale, the configured one before the first iteration.
     */
    public double getTimeScale()
    {
        return time.getTimeScale();
    }

    /**
     * Sets the listener of the time dropped by the lag cap.
     *
     * @param listener The listener, {@code null} for none.
     */
    public void setLagListener(LagListener listener)
    {
        lagListener = listener;
    }

    /**
     * Returns the number of iterations that dropped time beyond the lag cap.
     *
     * @return The number of drops since creation.
     */
    public long getDroppedLagCount()
    {
        return droppedLagCount;
    }

    /**
     * Returns the total time dropped beyond the lag cap.
     *
     * @return The dropped time since creation, in seconds.
     */
    public double getDroppedLagSeconds()
    {
        return droppedLagSeconds;
    }

    @Override
    public void iterate()
    {
        if (state == LoopState.STOPPED || state == LoopState.FAILED)
        {
            throw new IllegalStateException("The loop is " + state);
        }
        boolean completed = false;
        try
        {
            final boolean resumed = applyRequests();
            inbox.deliverAll();
            final long elapsedNanos = measure();
            if (state == LoopState.RUNNING)
            {
                advance(elapsedNanos, resumed);
            }
            render.render(alpha, time.cap(elapsedNanos) / LoopConfig.NANOS_PER_SECOND);
            if (stopRequested)
            {
                state = LoopState.STOPPED;
            }
            completed = true;
        }
        finally
        {
            if (!completed)
            {
                state = LoopState.FAILED;
            }
        }
    }

    @Override
    public boolean isRunning()
    {
        return state == LoopState.RUNNING || state == LoopState.PAUSED;
    }

    @Override
    public void pause()
    {
        pauseRequested = true;
    }

    @Override
    public void resume()
    {
        pauseRequested = false;
    }

    @Override
    public void stop()
    {
        stopRequested = true;
    }

    @Override
    public void setTimeScale(double scale)
    {
        LoopConfig.checkTimeScale(scale);
        requestedTimeScale = scale;
    }

    /**
     * Applies the control requests at the frame boundary.
     *
     * @return {@code true} if the loop resumes in this iteration.
     */
    private boolean applyRequests()
    {
        time.setTimeScale(requestedTimeScale);
        final boolean pause = pauseRequested;
        boolean resumed = false;
        if (state == LoopState.CREATED)
        {
            state = LoopState.RUNNING;
        }
        if (pause && state == LoopState.RUNNING)
        {
            state = LoopState.PAUSED;
        }
        else if (!pause && state == LoopState.PAUSED)
        {
            state = LoopState.RUNNING;
            time.clear();
            resumed = true;
        }
        return resumed;
    }

    /**
     * Reads the clock.
     *
     * @return The real time elapsed since the previous iteration, in nanoseconds, {@code 0} for the first one.
     */
    private long measure()
    {
        final long now = clock.nanoTime();
        long elapsedNanos = 0L;
        if (measured)
        {
            elapsedNanos = Math.max(0L, now - lastNanos);
        }
        measured = true;
        lastNanos = now;
        return elapsedNanos;
    }

    /**
     * Accumulates the elapsed time and runs the steps.
     *
     * @param elapsedNanos The real time elapsed since the previous iteration, in nanoseconds.
     * @param resumed {@code true} if the loop resumes in this iteration: no time is accumulated.
     */
    private void advance(long elapsedNanos, boolean resumed)
    {
        if (!resumed)
        {
            final long droppedNanos = time.add(elapsedNanos);
            if (droppedNanos > 0L)
            {
                reportLag(droppedNanos);
            }
        }
        while (time.takeStep())
        {
            update.update(stepSeconds);
        }
        alpha = time.getAlpha();
    }

    /**
     * Counts and reports dropped time.
     *
     * @param droppedNanos The dropped time, in nanoseconds, positive.
     */
    private void reportLag(long droppedNanos)
    {
        final double seconds = droppedNanos / LoopConfig.NANOS_PER_SECOND;
        droppedLagCount++;
        droppedLagSeconds += seconds;
        if (lagListener != null)
        {
            lagListener.onLag(seconds);
        }
    }
}
