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
package com.b3dgs.lionengine.audio;

import java.lang.management.ManagementFactory;

import com.sun.management.ThreadMXBean;

/**
 * Backend that plays nothing and allocates nothing, for the allocation tests of the audio system.
 */
final class SilentAudioBackend implements AudioBackend
{
    /** Measured runs of a workload: the lowest is kept. */
    private static final int MEASURES = 3;

    /**
     * Measures the bytes a workload allocates in steady state on the current thread: one warm-up run, then the lowest
     * of {@value #MEASURES} measured runs. A one-time event of the JVM landing in one run (a compilation, for example)
     * does not count; an allocation made by the workload itself is in every run.
     *
     * @param workload The workload, created before the call.
     * @return The lowest number of bytes allocated by a measured run.
     */
    static long steadyAllocation(Runnable workload)
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        workload.run();
        long lowest = Long.MAX_VALUE;
        for (int i = 0; i < MEASURES; i++)
        {
            final long before = bean.getCurrentThreadAllocatedBytes();
            workload.run();
            lowest = Math.min(lowest, bean.getCurrentThreadAllocatedBytes() - before);
        }
        return lowest;
    }

    /** The listener. */
    private VoiceListener listener;
    /** Next voice. */
    private int next = 1;

    /**
     * Creates the backend.
     */
    SilentAudioBackend()
    {
        super();
    }

    /**
     * Ends a voice by itself, as a delivered end event does.
     *
     * @param voice The voice.
     */
    void end(int voice)
    {
        listener.onVoiceEnded(voice);
    }

    @Override
    public int getMaxVoices()
    {
        return 8;
    }

    @Override
    public void setVoiceListener(VoiceListener listener)
    {
        this.listener = listener;
    }

    @Override
    public int play(PcmBuffer buffer)
    {
        return next++;
    }

    @Override
    public int play(PcmStream stream)
    {
        return next++;
    }

    @Override
    public void stop(int voice)
    {
        // Plays nothing, so nothing to stop.
    }

    @Override
    public boolean isPlaying(int voice)
    {
        return false;
    }

    @Override
    public void close()
    {
        // Holds nothing to release.
    }
}
