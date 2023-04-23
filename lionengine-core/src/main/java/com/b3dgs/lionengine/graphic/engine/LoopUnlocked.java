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
 * Unlocked loop. Update and render as fast as possible, does not sync to expected frame rate.
 * <p>
 * Completely machine dependent (both faster or slower).
 * </p>
 */
public final class LoopUnlocked implements Loop
{
    /** Extrapolation base. */
    private final double extrp;
    /** Running flag. */
    private boolean isRunning;

    /**
     * Create loop.
     */
    public LoopUnlocked()
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
    public LoopUnlocked(int rateOriginal, int rateDesired)
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
    }

    /*
     * Loop
     */

    @Override
    public void start(Screen screen, Frame frame)
    {
        Check.notNull(screen);
        Check.notNull(frame);

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

                frame.computeFrameRate(lastTime, System.nanoTime());
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
        // Nothing to do
    }
}
