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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Screen;

/**
 * Frame skipping loop, prioritizing update over render in order to respect the expected frame rate.
 * <p>
 * Speed is always the same, but frames may be skipped during rendering in order to not be late on update.
 * </p>
 * <p>
 * If update takes more time than expected frame rate, rendering is performed at a minimum rate.
 * </p>
 */
public final class LoopFrameSkipping implements Loop
{
    /** Nano to milli. */
    static final long NANO_TO_MILLI = 1_000_000L;
    /** Maximum frame time in nano. */
    static final long MAX_FRAME_TIME_NANO = 250 * NANO_TO_MILLI;
    /** Maximum expected frame rate. */
    private static final int MAX_FRAME_RATE = 1000;

    private static double computeFrameTime(int rate)
    {
        final double expectedRate;
        if (rate == 0)
        {
            expectedRate = MAX_FRAME_RATE;
        }
        else
        {
            expectedRate = rate;
        }
        return Constant.ONE_SECOND_IN_MILLI / expectedRate * Constant.NANO_TO_MILLI;
    }

    /**
     * Check if screen has sync locked.
     * 
     * @param screen The screen reference.
     * @return <code>true</code> if sync enabled, <code>false</code> else.
     */
    private static boolean hasSync(Screen screen)
    {
        final Config config = screen.getConfig();
        final Resolution output = config.getOutput();
        return config.isWindowed() && output.getRate() > 0;
    }

    /** Extrapolation base. */
    private final double extrp;
    /** Running flag. */
    private boolean isRunning;
    /** Max frame time in nano. */
    private double maxFrameTimeNano = -1.0;

    /**
     * Create loop.
     */
    public LoopFrameSkipping()
    {
        super();

        extrp = Constant.EXTRP;
    }

    /**
     * Create loop.
     * 
     * @param rateOriginal The original rate.
     * @param rateDesired The desired rate.
     */
    public LoopFrameSkipping(int rateOriginal, int rateDesired)
    {
        super();

        if (rateOriginal == rateDesired)
        {
            extrp = Constant.EXTRP;
        }
        else
        {
            extrp = rateOriginal / (double) rateDesired;
        }
        maxFrameTimeNano = computeFrameTime(rateDesired);
    }

    @Override
    public void start(Screen screen, Frame frame)
    {
        Check.notNull(screen);
        Check.notNull(frame);

        final boolean sync = hasSync(screen);
        if (maxFrameTimeNano < 0)
        {
            notifyRateChanged(screen.getConfig().getOutput().getRate());
        }
        long currentTimeNano = System.nanoTime();
        double acc = 0.0;
        isRunning = true;

        while (isRunning)
        {
            if (screen.isReady())
            {
                final long firstTimeNano = System.nanoTime();
                final long frameTimeNano = UtilMath.clamp(firstTimeNano - currentTimeNano, 0L, MAX_FRAME_TIME_NANO);
                currentTimeNano = firstTimeNano;
                acc += frameTimeNano;

                do
                {
                    frame.update(extrp);
                    acc -= maxFrameTimeNano;
                }
                while (acc > maxFrameTimeNano);

                screen.preUpdate();
                frame.render();
                screen.update();

                while (sync && System.nanoTime() - firstTimeNano < maxFrameTimeNano)
                {
                    Thread.yield();
                }

                frame.computeFrameRate(firstTimeNano, Math.max(firstTimeNano + 1L, System.nanoTime()));
            }
            else
            {
                frame.check();
                UtilSequence.pause(Constant.DECADE);
            }
        }
    }

    @Override
    public void stop()
    {
        isRunning = false;
    }

    @Override
    public void notifyRateChanged(int rate)
    {
        maxFrameTimeNano = computeFrameTime(rate);
    }
}
