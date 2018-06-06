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
    /** Running flag. */
    private boolean isRunning;

    /**
     * Create loop.
     */
    public LoopUnlocked()
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

                frame.computeFrameRate(lastTime, Math.max(lastTime + 1, System.nanoTime()));
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
