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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.Screen;

/**
 * Locked loop. Update and render, waiting for expected frame rate.
 * <p>
 * Max speed is capped, but can be slower than expected depending of running machine.
 * </p>
 */
public final class LoopLocked implements Loop
{
    /** Running flag. */
    private boolean isRunning;
    /** Max frame time in nano. */
    private double maxFrameTimeNano = -1.0;

    /**
     * Create loop.
     */
    public LoopLocked()
    {
        super();
    }

    /*
     * Loop
     */

    @Override
    public void start(Screen screen, Frame frame)
    {
        Check.notNull(screen);
        Check.notNull(frame);

        final Config config = screen.getConfig();
        final Resolution output = config.getOutput();
        final boolean sync = config.isWindowed() && output.getRate() > 0;
        if (maxFrameTimeNano < 0)
        {
            notifyRateChanged(output.getRate());
        }

        isRunning = true;
        while (isRunning)
        {
            if (screen.isReady())
            {
                final long lastTime = System.nanoTime();

                frame.update(Constant.EXTRP);
                screen.preUpdate();
                frame.render();
                screen.update();

                while (sync && System.nanoTime() - lastTime < maxFrameTimeNano)
                {
                    Thread.yield();
                }

                frame.computeFrameRate(lastTime, Math.max(lastTime + 1L, System.nanoTime()));
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
