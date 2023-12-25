/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * Handle tick measure, in updated frames number.
 */
public final class Tick implements Updatable
{
    /** One second in milli. */
    private static final double ONE_SECOND_IN_MILLI = 1_000;

    /** Actions to add. */
    private final List<TickActionDelayed> toAdd = new ArrayList<>();
    /** Actions executed to remove. */
    private final List<TickActionDelayed> toRemove = new ArrayList<>();
    /** Actions to check. */
    private final List<TickActionDelayed> actions = new ArrayList<>();
    /** Update tick loop. */
    private final Updatable updating;

    /** Current update loop. */
    private Updatable updater = UpdatableVoid.getInstance();
    /** Current tick. */
    private double ticks;
    /** Started flag. */
    private boolean started;

    /**
     * Create a tick.
     */
    public Tick()
    {
        super();

        updating = extrp -> ticks += extrp;
    }

    /**
     * Add an action to execute once tick delay elapsed.
     * 
     * @param action The action to execute (must not be <code>null</code>).
     * @param tickDelay The tick delay used as trigger.
     * @throws LionEngineException If invalid argument.
     */
    public void addAction(TickAction action, long tickDelay)
    {
        toAdd.add(new TickActionDelayed(action, ticks + tickDelay));
    }

    /**
     * Add an action to execute once delay elapsed.
     * 
     * @param action The action to execute (must not be <code>null</code>).
     * @param rate The rate reference.
     * @param delayMs The delay in milli used as trigger.
     * @throws LionEngineException If invalid argument.
     */
    public void addAction(TickAction action, int rate, long delayMs)
    {
        final double frameTime = ONE_SECOND_IN_MILLI / rate;
        toAdd.add(new TickActionDelayed(action, ticks + delayMs / frameTime));
    }

    /**
     * Start tick. Can be started only if not already started.
     */
    public void start()
    {
        if (!started)
        {
            started = true;
            updater = updating;
        }
    }

    /**
     * Stop and reset tick.
     */
    public void stop()
    {
        ticks = 0.0;
        started = false;
        updater = UpdatableVoid.getInstance();
    }

    /**
     * Stop and start the tick.
     */
    public void restart()
    {
        ticks = 0.0;
        started = true;
        updater = updating;
    }

    /**
     * Pause tick. Does nothing if already paused.
     */
    public void pause()
    {
        updater = UpdatableVoid.getInstance();
    }

    /**
     * Continue tick from last pause. Does nothing if already running.
     */
    public void resume()
    {
        updater = updating;
    }

    /**
     * Check if specific tick has been elapsed.
     * 
     * @param tick The tick to check.
     * @return <code>true</code> if tick elapsed, <code>false</code> else.
     */
    public boolean elapsed(double tick)
    {
        if (started)
        {
            return Double.compare(ticks, tick) >= 0;
        }
        return false;
    }

    /**
     * Check if specific time has been elapsed (in tick referential).
     * 
     * @param rate The rate reference.
     * @param milli The milliseconds to check (based on frame time).
     * @return <code>true</code> if time elapsed, <code>false</code> else.
     * @throws LionEngineException If invalid argument.
     */
    public boolean elapsedTime(int rate, long milli)
    {
        if (started && rate > 0)
        {
            final double frameTime = ONE_SECOND_IN_MILLI / rate;
            return Double.compare(milli, StrictMath.floor(ticks * frameTime)) <= 0;
        }
        return false;
    }

    /**
     * Get number of ticks elapsed since start call.
     * 
     * @return The number of ticks elapsed since start call.
     */
    public double elapsed()
    {
        return ticks;
    }

    /**
     * Get number of ticks elapsed since start call.
     * 
     * @param rate The rate reference.
     * @return The number of ticks elapsed since start call.
     */
    public int elapsedTime(int rate)
    {
        final double frameTime = ONE_SECOND_IN_MILLI / rate;
        return (int) StrictMath.floor(ticks * frameTime);
    }

    /**
     * Set the tick value.
     * 
     * @param value The tick to set.
     */
    public void set(double value)
    {
        ticks = value;
    }

    /**
     * Check if tick started.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    public boolean isStarted()
    {
        return started;
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);

        if (!toAdd.isEmpty())
        {
            actions.addAll(toAdd);
            toAdd.clear();
        }

        final int n = actions.size();
        for (int i = 0; i < n; i++)
        {
            final TickActionDelayed action = actions.get(i);
            if (elapsed(action.getDelay()))
            {
                action.execute();
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
     * Delayed tick action data.
     */
    private static final class TickActionDelayed implements TickAction
    {
        /** Action reference. */
        private final TickAction action;
        /** Tick delay trigger. */
        private final double delay;

        /**
         * Create delayed action data.
         * 
         * @param action The action reference (must not be <code>null</code>).
         * @param delay The tick delay.
         * @throws LionEngineException If invalid argument.
         */
        private TickActionDelayed(TickAction action, double delay)
        {
            Check.notNull(action);

            this.action = action;
            this.delay = delay;
        }

        /**
         * Get the tick delay.
         * 
         * @return The tick delay.
         */
        public double getDelay()
        {
            return delay;
        }

        @Override
        public void execute()
        {
            action.execute();
        }
    }
}
