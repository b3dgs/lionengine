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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.Constant;

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
    /** Maximum frame time in nano. */
    static final double MAX_FRAME_TIME_NANO = 250.0 * Constant.NANO_TO_MILLI;
    /** Maximum expected frame rate. */
    private static final int MAX_FRAME_RATE = 1000;

    /** Running flag. */
    private boolean isRunning;

    /**
     * Create loop.
     */
    public LoopFrameSkipping()
    {
        super();
    }

    /*
     * Loop
     */

    @Override
    public void start(Screen screen, Frame frame)
    {
        final Config config = screen.getConfig();
        final Resolution source = config.getSource();
        final Resolution output = config.getOutput();
        final double expectedRate;
        if (source.getRate() == 0)
        {
            expectedRate = MAX_FRAME_RATE;
        }
        else
        {
            expectedRate = source.getRate();
        }
        final double maxFrameTimeNano = Constant.ONE_SECOND_IN_MILLI / expectedRate * Constant.NANO_TO_MILLI;
        final boolean sync = config.isWindowed() && output.getRate() > 0;

        double currentTime = System.nanoTime();
        double acc = 0.0;
        isRunning = true;
        while (isRunning)
        {
            if (screen.isReady())
            {
                final double firstTime = System.nanoTime();
                double frameTime = firstTime - currentTime;
                if (frameTime > MAX_FRAME_TIME_NANO)
                {
                    frameTime = MAX_FRAME_TIME_NANO;
                }
                currentTime = firstTime;
                acc += frameTime;

                do
                {
                    frame.update(Constant.EXTRP);
                    acc -= maxFrameTimeNano;
                }
                while (acc > maxFrameTimeNano);

                screen.preUpdate();
                frame.render();
                screen.update();

                while (sync && System.nanoTime() - firstTime < maxFrameTimeNano)
                {
                    Thread.yield();
                }

                frame.computeFrameRate(firstTime, Math.max(firstTime + 1, System.nanoTime()));
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
