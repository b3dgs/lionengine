package com.b3dgs.lionengine;

/**
 * Handle timer operation, in milli seconds.
 */
public final class Timing
{
    /** Nano millisecond. */
    private static final long NANO_TO_MILLI = 1000000L;
    /** Current time. */
    private long cur;
    /** Time when pause. */
    private long back;
    /** Started flag. */
    private boolean started;

    /**
     * Create a new timer.
     */
    public Timing()
    {
        cur = 0L;
        started = false;
    }

    /**
     * Start timer.
     */
    public void start()
    {
        cur = Timing.systemTime();
        started = true;
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
     * Pause timer.
     */
    public void pause()
    {
        back = Timing.systemTime();
    }

    /**
     * Continue timer from last pause.
     */
    public void unpause()
    {
        cur += Timing.systemTime() - back;
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
            return Timing.systemTime() - cur >= time;
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
            return Timing.systemTime() - cur;
        }
        return 0;
    }

    /**
     * Get timer value.
     * 
     * @return The timer value.
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
     * Get the system time.
     * 
     * @return The system time.
     */
    private static long systemTime()
    {
        return System.nanoTime() / Timing.NANO_TO_MILLI;
    }
}
