/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.List;

/**
 * Handle timer operation, in milliseconds, system clock independent.
 */
public final class Timing implements Updatable
{
    /** Nano millisecond. */
    private static final long NANO_TO_MILLI = 1_000_000L;

    /**
     * Get the system time.
     * 
     * @return The system time in milliseconds.
     */
    private static long systemTime()
    {
        return System.nanoTime() / NANO_TO_MILLI;
    }

    /** Actions to add. */
    private final List<ActionDelayed> toAdd = new ArrayList<>();
    /** Actions. */
    private final List<ActionDelayed> actions = new ArrayList<>();
    /** Actions executed. */
    private final List<ActionDelayed> toRemove = new ArrayList<>();
    /** Current time. */
    private long cur;
    /** Time when pause. */
    private long back;
    /** Started flag. */
    private boolean started;

    /**
     * Create a timing.
     */
    public Timing()
    {
        super();
    }

    /**
     * Add an action to execute once delay elapsed.
     * 
     * @param action The action to execute (must not be <code>null</code>).
     * @param delayMs The delay used as trigger.
     * @throws LionEngineException If invalid argument.
     */
    public void addAction(TickAction action, long delayMs)
    {
        toAdd.add(new ActionDelayed(action, delayMs));
    }

    /**
     * Start timer. Can be started only if not already started.
     */
    public void start()
    {
        if (!started)
        {
            cur = systemTime();
            started = true;
        }
    }

    /**
     * Start timer with initial timing value in milli seconds. Can be started only if not already started.
     * 
     * @param value The value to set milli seconds.
     */
    public void start(long value)
    {
        if (!started)
        {
            cur = systemTime() - value;
            started = true;
        }
    }

    /**
     * Stop and reset timer.
     */
    public void stop()
    {
        cur = 0L;
        started = false;
    }

    /**
     * Stop and start the timer.
     */
    public void restart()
    {
        stop();
        start();
    }

    /**
     * Pause timer.
     */
    public void pause()
    {
        back = systemTime();
    }

    /**
     * Continue timer from last pause.
     */
    public void unpause()
    {
        cur += systemTime() - back;
        back = 0L;
    }

    /**
     * Check if specific time has been elapsed.
     * 
     * @param time The time to check in milliseconds.
     * @return <code>true</code> if timer elapsed this time, <code>false</code> else.
     */
    public boolean elapsed(long time)
    {
        if (started)
        {
            return getRelativeTime() - cur >= time;
        }
        return false;
    }

    /**
     * Get number of milli seconds elapsed since start call.
     * 
     * @return The number of milli seconds elapsed since start call.
     */
    public long elapsed()
    {
        if (started)
        {
            return getRelativeTime() - cur;
        }
        return 0L;
    }

    /**
     * Set the timing value in milli seconds.
     * 
     * @param value The value to set milli seconds.
     */
    public void set(long value)
    {
        cur = value;
        started = true;
    }

    /**
     * Get timer value milli seconds.
     * 
     * @return The timer value milli seconds.
     */
    public long get()
    {
        return cur;
    }

    /**
     * Check if timer started.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    public boolean isStarted()
    {
        return started;
    }

    /**
     * Get the relative time.
     * 
     * @return The current time, the old time if paused.
     */
    private long getRelativeTime()
    {
        if (back > 0L)
        {
            return back;
        }
        return systemTime();
    }

    @Override
    public void update(double extrp)
    {
        if (!toAdd.isEmpty())
        {
            actions.addAll(toAdd);
            toAdd.clear();
        }

        final int n = actions.size();
        for (int i = 0; i < n; i++)
        {
            final ActionDelayed action = actions.get(i);
            if (elapsed(action.delayMs()))
            {
                action.action().execute();
                toRemove.add(action);
            }
        }

        if (!toRemove.isEmpty())
        {
            actions.removeAll(toRemove);
            toRemove.clear();
        }
    }

    /**
     * Delayed action data.
     * 
     * @param action The action reference.
     * @param delayMs The delay.
     */
    private record ActionDelayed(TickAction action, long delayMs)
    {
        private ActionDelayed
        {
            Check.notNull(action);
        }
    }
}
