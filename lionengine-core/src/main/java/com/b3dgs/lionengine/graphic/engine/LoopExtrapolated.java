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
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.Screen;

/**
 * Locked loop with extrapolation for computing, waiting for expected frame rate.
 * <p>
 * Max speed is capped, compensate slow speed with extrapolation value. Loss of precision during computation.
 * </p>
 */
public final class LoopExtrapolated implements Loop
{
    /** One second in nano. */
    private static final double ONE_SECOND_IN_NANO = 1_000_000_000.0;

    /** Running flag. */
    private boolean isRunning;
    /** Current rate. */
    private int rate = -1;
    /** Max frame time in nano. */
    private double maxFrameTimeNano;

    /**
     * Create loop.
     */
    public LoopExtrapolated()
    {
        super();
    }

    /**
     * Create loop.
     * 
     * @param rate The original rate.
     */
    public LoopExtrapolated(int rate)
    {
        super();

        notifyRateChanged(rate);
    }

    @Override
    public void start(Screen screen, Frame frame)
    {
        Check.notNull(screen);
        Check.notNull(frame);

        final Config config = screen.getConfig();
        final Resolution output = config.getOutput();
        final boolean sync = config.isWindowed() && output.getRate() > 0;

        notifyRateChanged(output.getRate());

        double extrp = 1.0;
        isRunning = true;
        while (isRunning)
        {
            if (screen.isReady())
            {
                final long lastTime = System.nanoTime();

                frame.update(extrp);
                screen.preUpdate();
                frame.render();
                screen.update();

                while (sync && System.nanoTime() - lastTime < maxFrameTimeNano)
                {
                    Thread.yield();
                }

                final long currentTime = Math.max(lastTime + 1L, System.nanoTime());
                extrp = rate / ONE_SECOND_IN_NANO * (currentTime - lastTime);
                frame.computeFrameRate(lastTime, currentTime);
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
        if (this.rate < 0)
        {
            this.rate = rate;
        }
        if (rate == 0)
        {
            maxFrameTimeNano = 0.0;
        }
        else
        {
            maxFrameTimeNano = Constant.ONE_SECOND_IN_MILLI / (double) rate * Constant.NANO_TO_MILLI;
        }
    }
}
