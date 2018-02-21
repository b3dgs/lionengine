/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.sequence;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.graphic.Screen;

/**
 * Locked loop with extrapolation for computing, waiting for expected frame rate.
 * <p>
 * Max speed is capped, compensate slow speed with extrapolation value. Loss of precision during computation.
 * </p>
 */
public final class LoopExtrapolated implements Loop
{
    /** Running flag. */
    private boolean isRunning;

    /**
     * Create loop.
     */
    public LoopExtrapolated()
    {
        super();
    }

    /*
     * Loop
     */

    @Override
    public void start(Screen screen, Frame frame)
    {
        final double maxFrameTimeNano;
        final Config config = screen.getConfig();
        final Resolution source = config.getSource();
        final Resolution output = config.getOutput();
        if (output.getRate() == 0)
        {
            maxFrameTimeNano = 0.0;
        }
        else
        {
            maxFrameTimeNano = Constant.ONE_SECOND_IN_MILLI / (double) output.getRate() * Constant.NANO_TO_MILLI;
        }
        final boolean sync = config.isWindowed() && output.getRate() > 0;
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

                final long currentTime = Math.max(lastTime + 1, System.nanoTime());
                extrp = source.getRate() / (double) Constant.ONE_SECOND_IN_NANO * (currentTime - lastTime);
                frame.computeFrameRate(lastTime, currentTime);
            }
            else
            {
                frame.check();
                UtilSequence.pause(Constant.DECADE);
            }

            if (!Engine.isStarted())
            {
                isRunning = false;
            }
        }
    }

    @Override
    public void stop()
    {
        isRunning = false;
    }
}
