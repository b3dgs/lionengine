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
package com.b3dgs.lionengine.anim;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Animation implementation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
final class AnimationImpl implements Animation
{
    /** Animation name. */
    private final String name;
    /** First animation frame. */
    private final int firstFrame;
    /** Last animation frame. */
    private final int lastFrame;
    /** Animation speed. */
    private final double speed;
    /** Reverse flag. */
    private final boolean reverse;
    /** Repeat flag. */
    private final boolean repeat;

    /**
     * Internal constructor.
     * 
     * @param name The animation name.
     * @param firstFrame The first frame (included) index to play (superior or equal to {@link Animation#MINIMUM_FRAME}
     *            ).
     * @param lastFrame The last frame (included) index to play (superior or equal to firstFrame).
     * @param speed The animation playing speed (superior or equal to 0.0).
     * @param reverse <code>true</code> to reverse animation play (play it from first to last, and last to first).
     * @param repeat The repeat state (<code>true</code> will play in loop, <code>false</code> will play once only).
     * @throws LionEngineException If invalid animation.
     */
    AnimationImpl(String name, int firstFrame, int lastFrame, double speed, boolean reverse, boolean repeat)
    {
        Check.superiorOrEqual(firstFrame, Animation.MINIMUM_FRAME);
        Check.superiorOrEqual(lastFrame, firstFrame);
        Check.superiorOrEqual(speed, 0.0);

        this.name = name;
        this.firstFrame = firstFrame;
        this.lastFrame = lastFrame;
        this.speed = speed;
        this.reverse = reverse;
        this.repeat = repeat;
    }

    /*
     * Animation
     */

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int getFirst()
    {
        return firstFrame;
    }

    @Override
    public int getLast()
    {
        return lastFrame;
    }

    @Override
    public double getSpeed()
    {
        return speed;
    }

    @Override
    public boolean getReverse()
    {
        return reverse;
    }

    @Override
    public boolean getRepeat()
    {
        return repeat;
    }
}
