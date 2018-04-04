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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
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

    /**
     * Get the maximum frame time in nano seconds.
     * 
     * @param screen The screen reference.
     * @return The maximum frame time in nano seconds.
     */
    private static double getMaxFrameTimeNano(Screen screen)
    {
        final Config config = screen.getConfig();
        final Resolution source = config.getSource();
        final double expectedRate = getExpectedRate(source);
        return Constant.ONE_SECOND_IN_MILLI / expectedRate * Constant.NANO_TO_MILLI;
    }

    /**
     * Get the expected frame rate.
     * 
     * @param source The resolution source.
     * @return The expected frame rate.
     */
    private static int getExpectedRate(Resolution source)
    {
        final int expectedRate;
        if (source.getRate() == 0)
        {
            expectedRate = MAX_FRAME_RATE;
        }
        else
        {
            expectedRate = source.getRate();
        }
        return expectedRate;
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
        Check.notNull(screen);
        Check.notNull(frame);

        final double maxFrameTimeNano = getMaxFrameTimeNano(screen);
        final boolean sync = hasSync(screen);
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
                    frame.update(Constant.EXTRP);
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
