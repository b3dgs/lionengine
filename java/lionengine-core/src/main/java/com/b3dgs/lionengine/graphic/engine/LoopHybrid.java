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
package com.b3dgs.lionengine.graphic.engine;

import java.util.concurrent.locks.LockSupport;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.graphic.Screen;

/**
 * Hybrid loop with extrapolation when frame rate is higher than reference, and frame skipping when lower.
 */
public final class LoopHybrid implements Loop
{
    /** Maximum expected frame rate used when rate is 0 (uncapped). */
    private static final int MAX_FRAME_RATE = 1000;
    /** Small margin subtracted from computed frame times to avoid drifting over budget (ns). */
    private static final int NANO_MARGIN = 100;
    /** Maximum consecutive logic updates per rendered frame. */
    private static final int MAX_UPDATES_PER_FRAME = 5;
    /** Minimum remaining time for sleep. */
    private static final long MINIMUM_REMAINING_TIME_FOR_SLEEP_NANO = 2_000_000L;
    /** Park nano time. */
    private static final long PARK_NANO = 1_000_000L;

    /**
     * Prepare with first pass update render.
     * 
     * @param screen The screen reference.
     * @param frame The frame reference.
     */
    private static void prepare(Screen screen, Frame frame)
    {
        frame.update(Constant.EXTRP);
        if (screen.isReady())
        {
            screen.preUpdate();
            frame.render();
            screen.update();
        }
    }

    /**
     * Compute the nanosecond budget for one frame at the given rate.
     *
     * @param rate The target rate in Hz (0 = uncapped, uses {@link #MAX_FRAME_RATE}).
     * @return Frame time in nanoseconds minus {@link #NANO_MARGIN}.
     */
    private static double computeFrameTime(int rate)
    {
        final double effectiveRate = rate == 0 ? MAX_FRAME_RATE : rate;
        return Constant.ONE_SECOND_IN_MILLI / effectiveRate * Constant.NANO_TO_MILLI - NANO_MARGIN;
    }

    /**
     * Duration of one logic step in nanoseconds, derived from the original/native update rate.
     * Used as the fixed timestep fed to {@code frame.update()} and as the accumulator threshold.
     */
    private final double logicStepNano;
    /**
     * Target render frame time in nanoseconds, derived from the desired display rate.
     * Acts as the FPS cap: the busy-wait spin runs until this budget is exhausted.
     */
    private double renderFrameTimeNano;
    /** Timestamp of the start of the current frame (nanoseconds). */
    private long firstTimeNano;
    /** Accumulator tracking unspent nanoseconds not yet consumed by logic updates. */
    private double acc;
    /** Running flag. */
    private boolean isRunning = true;

    /**
     * Create a hybrid loop.
     *
     * @param rateOriginal The native logic update rate (UPS), e.g. 60.
     * @param rateDesired The desired display/render rate (FPS), e.g. 144.
     */
    public LoopHybrid(int rateOriginal, int rateDesired)
    {
        super();

        logicStepNano = computeFrameTime(rateOriginal);
        renderFrameTimeNano = computeFrameTime(rateDesired);
    }

    // CHECKSTYLE IGNORE LINE: ExecutableStatementCount
    @Override
    public void start(Screen screen, Frame frame)
    {
        Check.notNull(screen);
        Check.notNull(frame);

        prepare(screen, frame);

        double extrp = Constant.EXTRP;
        long lastTimeNano = System.nanoTime() - Math.round(renderFrameTimeNano);
        acc = renderFrameTimeNano;

        final boolean windowed = screen.getConfig().isWindowed();
        final Runnable action = windowed ? this::pause : () ->
        {
            // Void
        };

        while (isRunning)
        {
            if (screen.isReady())
            {
                firstTimeNano = System.nanoTime();
                frame.computeFrameRate(lastTimeNano, firstTimeNano);

                final long elapsed = firstTimeNano - lastTimeNano;

                if (elapsed >= logicStepNano)
                {
                    extrp = Constant.EXTRP;
                }
                else
                {
                    extrp = elapsed / logicStepNano;
                    // CHECKSTYLE IGNORE LINE: NestedIfDepth
                    if (extrp > Constant.EXTRP)
                    {
                        extrp = Constant.EXTRP;
                    }
                }

                acc += elapsed;
                int updates = 0;
                do
                {
                    frame.update(extrp);
                    acc -= logicStepNano;
                    updates++;
                }
                while (acc > logicStepNano && updates < MAX_UPDATES_PER_FRAME);

                screen.preUpdate();
                frame.render();
                screen.update();

                action.run();

                lastTimeNano = firstTimeNano;
            }
            else
            {
                frame.check();
                UtilSequence.pause(Constant.DECADE);
            }
        }
    }

    /**
     * Park if enough time or spin wait for short remaining.
     */
    private void pause()
    {
        long remaining;
        while ((remaining = System.nanoTime() - firstTimeNano) < renderFrameTimeNano)
        {
            if (remaining > MINIMUM_REMAINING_TIME_FOR_SLEEP_NANO)
            {
                LockSupport.parkNanos(PARK_NANO);
            }
            else
            {
                Thread.onSpinWait();
            }
        }
    }

    @Override
    public void reset()
    {
        firstTimeNano = System.nanoTime();
        acc = renderFrameTimeNano;
    }

    @Override
    public void stop()
    {
        isRunning = false;
    }

    @Override
    public void notifyRateChanged(int rate)
    {
        renderFrameTimeNano = computeFrameTime(rate);
    }
}
