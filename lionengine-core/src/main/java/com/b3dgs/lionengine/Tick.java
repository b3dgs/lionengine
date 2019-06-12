/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Collection;

/**
 * Handle tick measure, in updated frames number.
 */
public final class Tick implements Updatable
{
    /** One second to milli. */
    private static final double ONE_SECOND_IN_MILLI = Constant.ONE_SECOND_IN_MILLI;
    /** No update. */
    private static final Updatable PAUSE = extrp ->
    {
        // Nothing to do
    };

    /** Actions. */
    private final Collection<TickActionDelayed> actions = new ArrayList<>();
    /** Actions executed. */
    private final Collection<TickActionDelayed> toRemove = new ArrayList<>();
    /** Current tick. */
    private long currentTicks;
    /** Update. */
    private final Updatable updating = extrp -> currentTicks++;
    /** Update loop. */
    private Updatable updater = PAUSE;
    /** Started flag. */
    private boolean started;

    /**
     * Create a tick.
     */
    public Tick()
    {
        super();
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
        actions.add(new TickActionDelayed(action, tickDelay));
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
        currentTicks = 0L;
        started = false;
        updater = PAUSE;
    }

    /**
     * Stop and start the tick.
     */
    public void restart()
    {
        currentTicks = 0L;
        started = true;
        updater = updating;
    }

    /**
     * Pause tick.
     */
    public void pause()
    {
        updater = PAUSE;
    }

    /**
     * Continue tick from last pause.
     */
    public void resume()
    {
        updater = updating;
    }

    /**
     * Check if specific ticks has been elapsed.
     * 
     * @param tick The ticks to check.
     * @return <code>true</code> if ticks elapsed, <code>false</code> else.
     */
    public boolean elapsed(long tick)
    {
        if (started)
        {
            return currentTicks >= tick;
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
            return Double.compare(milli, StrictMath.floor(currentTicks * frameTime)) <= 0;
        }
        return false;
    }

    /**
     * Get number of ticks elapsed since start call.
     * 
     * @return The number of ticks elapsed since start call.
     */
    public long elapsed()
    {
        return currentTicks;
    }

    /**
     * Set the tick value.
     * 
     * @param value The tick to set.
     */
    public void set(long value)
    {
        currentTicks = value;
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

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);

        for (final TickActionDelayed action : actions)
        {
            if (elapsed(action.getDelay()))
            {
                action.getAction().execute();
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
    private static final class TickActionDelayed
    {
        /** Action reference. */
        private final TickAction action;
        /** Tick delay trigger. */
        private final long delay;

        /**
         * Create delayed action data.
         * 
         * @param action The action reference (must not be <code>null</code>).
         * @param delay The tick delay.
         * @throws LionEngineException If invalid argument.
         */
        private TickActionDelayed(TickAction action, long delay)
        {
            Check.notNull(action);

            this.action = action;
            this.delay = delay;
        }

        /**
         * Get action reference.
         * 
         * @return The action reference.
         */
        public TickAction getAction()
        {
            return action;
        }

        /**
         * Get the tick delay.
         * 
         * @return The tick delay.
         */
        public long getDelay()
        {
            return delay;
        }
    }
}
