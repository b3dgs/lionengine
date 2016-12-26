/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

/**
 * Handle tick measure, in updated frames number.
 */
public final class Tick implements Updatable
{
    /** One second to milli. */
    private static final double ONE_SECOND_IN_MILLI = Constant.ONE_SECOND_IN_MILLI;
    /** No update. */
    private static final Updatable PAUSE = new Updatable()
    {
        @Override
        public void update(double extrp)
        {
            // Nothing to do
        }
    };

    /** Update. */
    private final Updatable updating = new Updatable()
    {
        @Override
        public void update(double extrp)
        {
            currentTicks++;
        }
    };
    /** Update loop. */
    private Updatable updater = PAUSE;
    /** Current tick. */
    private int currentTicks;
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
     * Start tick. Can be started only if not already started.
     */
    public void start()
    {
        started = true;
        updater = updating;
    }

    /**
     * Stop and reset tick.
     */
    public void stop()
    {
        currentTicks = 0;
        started = false;
        updater = PAUSE;
    }

    /**
     * Stop and start the tick.
     */
    public void restart()
    {
        currentTicks = 0;
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
    public void unpause()
    {
        updater = updating;
    }

    /**
     * Check if specific ticks has been elapsed.
     * 
     * @param tick The ticks to check.
     * @return <code>true</code> if ticks elapsed, <code>false</code> else.
     */
    public boolean elapsed(int tick)
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
     * @param context The context reference.
     * @param milli The milliseconds to check (based on frame time).
     * @return <code>true</code> if time elapsed, <code>false</code> else.
     */
    public boolean elapsedTime(Context context, long milli)
    {
        if (started)
        {
            final double frameTime = ONE_SECOND_IN_MILLI / context.getConfig().getSource().getRate();
            // Equivalent to milli <= result
            return !(milli > StrictMath.floor(currentTicks * frameTime));
        }
        return false;
    }

    /**
     * Get number of ticks elapsed since start call.
     * 
     * @return The number of ticks elapsed since start call.
     */
    public int elapsed()
    {
        return currentTicks;
    }

    /**
     * Set the tick value.
     * 
     * @param value The tick to set.
     */
    public void set(int value)
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
    }
}
