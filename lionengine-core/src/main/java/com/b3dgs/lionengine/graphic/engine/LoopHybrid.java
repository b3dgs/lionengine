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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.graphic.Screen;

/**
 * Hybrid loop with extrapolation when frame rate if higher than reference, and frame skipping when lower.
 */
public final class LoopHybrid implements Loop
{
    /** One second in nano. */
    private static final double ONE_SECOND_IN_NANO = 1_000_000_000.0;
    /** Maximum expected frame rate. */
    private static final int MAX_FRAME_RATE = 1000;
    /** Nano margin. */
    private static final int NANO_MARGIN = 60;

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
        return Constant.ONE_SECOND_IN_MILLI / expectedRate * Constant.NANO_TO_MILLI - NANO_MARGIN;
    }

    /** Current rate in frame time nano. */
    private final double frameNano;
    /** Min frame time in nano. */
    private final double minFrameTimeNano;
    /** Max frame time in nano. */
    private double maxFrameTimeNano;
    /** Running flag. */
    private boolean isRunning = true;

    /**
     * Create loop.
     * 
     * @param rateOriginal The original rate.
     * @param rateDesired The desired rate.
     */
    public LoopHybrid(int rateOriginal, int rateDesired)
    {
        super();

        frameNano = rateOriginal / ONE_SECOND_IN_NANO;
        minFrameTimeNano = computeFrameTime(rateOriginal);
        maxFrameTimeNano = computeFrameTime(rateDesired);
    }

    @Override
    public void start(Screen screen, Frame frame)
    {
        Check.notNull(screen);
        Check.notNull(frame);

        double extrp = Constant.EXTRP;

        frame.update(extrp);
        if (screen.isReady())
        {
            screen.preUpdate();
            frame.render();
            screen.update();
        }

        long lastTimeNano = System.nanoTime() - Math.round(maxFrameTimeNano);
        double acc = maxFrameTimeNano;

        while (isRunning)
        {
            if (screen.isReady())
            {
                final long firstTimeNano = System.nanoTime();
                frame.computeFrameRate(lastTimeNano, firstTimeNano);

                final long elapsed = firstTimeNano - lastTimeNano;
                if (elapsed > minFrameTimeNano)
                {
                    extrp = Constant.EXTRP;
                }
                else
                {
                    extrp = frameNano * elapsed;

                    // CHECKSTYLE IGNORE LINE: NestedIfDepth
                    if (extrp > Constant.EXTRP)
                    {
                        extrp = Constant.EXTRP;
                    }
                }
                acc += elapsed;
                do
                {
                    frame.update(extrp);
                    acc -= minFrameTimeNano;
                }
                while (acc > minFrameTimeNano);

                screen.preUpdate();
                frame.render();
                screen.update();

                while (System.nanoTime() - firstTimeNano < maxFrameTimeNano)
                {
                    continue;
                }
                lastTimeNano = firstTimeNano;
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
