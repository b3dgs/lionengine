/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Animation implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class AnimationImpl
        implements Animation
{
    /** First frame error. */
    private static final String ERROR_FIRST_FRAME = "Animation first frame is lower than the minimum frame !";
    /** Last frame error. */
    private static final String ERROR_LAST_FRAME = "Animation last frame is lower than the first frame !";
    /** Speed error. */
    private static final String ERROR_SPEED = "Animation speed must not be negative !";

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
     * Constructor.
     * 
     * @param firstFrame The first frame (included) index to play (>= {@link Animation#MINIMUM_FRAME}).
     * @param lastFrame The last frame (included) index to play (>= firstFrame).
     * @param speed The animation playing speed (>= 0.0).
     * @param reverse <code>true</code> to reverse animation play (play it from first to last, and last to first).
     * @param repeat The repeat state (<code>true</code> will play in loop, <code>false</code> will play once only).
     */
    AnimationImpl(int firstFrame, int lastFrame, double speed, boolean reverse, boolean repeat)
    {
        Check.argument(firstFrame >= Animation.MINIMUM_FRAME, AnimationImpl.ERROR_FIRST_FRAME);
        Check.argument(lastFrame >= firstFrame, AnimationImpl.ERROR_LAST_FRAME);
        Check.argument(speed >= 0.0, AnimationImpl.ERROR_SPEED);

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
