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
package com.b3dgs.lionengine;

/**
 * Loop driver that only counts and records, for the tests of a platform or a module that drives a loop (ADR-0040,
 * ADR-0045).
 *
 * <p>
 * Each {@link #iterate()} counts one iteration; the control requests are recorded and applied like the game loop does
 * (a stop ends the loop at the end of the next iteration). Published in the {@code core} test JAR. Not thread-safe.
 * </p>
 */
public final class ManualLoopDriver implements LoopDriver
{
    /** Lowest accepted time scale. */
    private static final double MIN_TIME_SCALE = 0.1;
    /** Highest accepted time scale. */
    private static final double MAX_TIME_SCALE = 4.0;

    /** Number of iterations. */
    private int iterations;
    /** Whether the first iteration ran and no stop ended the loop. */
    private boolean running;
    /** Whether the loop stopped. */
    private boolean stopped;
    /** Whether a pause is requested. */
    private boolean paused;
    /** Whether a stop is requested. */
    private boolean stopRequested;
    /** Last requested time scale. */
    private double timeScale = 1.0;

    /**
     * Creates a driver never iterated.
     */
    public ManualLoopDriver()
    {
        super();
    }

    @Override
    public void iterate()
    {
        if (stopped)
        {
            throw new IllegalStateException("The loop is stopped");
        }
        iterations++;
        running = true;
        if (stopRequested)
        {
            running = false;
            stopped = true;
        }
    }

    @Override
    public boolean isRunning()
    {
        return running;
    }

    @Override
    public void pause()
    {
        paused = true;
    }

    @Override
    public void resume()
    {
        paused = false;
    }

    @Override
    public void stop()
    {
        stopRequested = true;
    }

    @Override
    public void setTimeScale(double scale)
    {
        if (!(scale >= MIN_TIME_SCALE && scale <= MAX_TIME_SCALE))
        {
            throw new IllegalArgumentException("Time scale outside [0.1, 4.0]: " + scale);
        }
        timeScale = scale;
    }

    /**
     * Returns the number of iterations.
     *
     * @return The number of calls to {@link #iterate()} that ran.
     */
    public int getIterationCount()
    {
        return iterations;
    }

    /**
     * Tells whether a pause is requested.
     *
     * @return {@code true} after {@link #pause()} and before {@link #resume()}.
     */
    public boolean isPauseRequested()
    {
        return paused;
    }

    /**
     * Tells whether a stop is requested.
     *
     * @return {@code true} after {@link #stop()}.
     */
    public boolean isStopRequested()
    {
        return stopRequested;
    }

    /**
     * Returns the last requested time scale.
     *
     * @return The time scale, {@code 1.0} if never set.
     */
    public double getTimeScale()
    {
        return timeScale;
    }
}
