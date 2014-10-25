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
import com.b3dgs.lionengine.LionEngineException;

/**
 * Animator implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class AnimatorImpl
        implements Animator
{
    /** Frame. */
    private static final double FRAME = 1.0;
    /** Half frame. */
    private static final double HALF_FRAME = 0.5;

    /** First frame. */
    private int first;
    /** Last frame. */
    private int last;
    /** Animation speed. */
    private double speed;
    /** Reverse flag. */
    private boolean reverse;
    /** Repeat flag. */
    private boolean repeat;
    /** Current playing frame. */
    private double current;
    /** Animation state. */
    private AnimState state;

    /**
     * Internal constructor.
     */
    AnimatorImpl()
    {
        first = Animation.MINIMUM_FRAME;
        state = AnimState.STOPPED;
        current = first;
        repeat = false;
        reverse = false;
    }

    /**
     * Update play mode routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePlaying(double extrp)
    {
        current += speed * extrp;

        // Last frame reached
        if (current >= last + FRAME)
        {
            // If not reversed, done, else, reverse
            current = last + HALF_FRAME;
            if (!reverse)
            {
                if (repeat)
                {
                    state = AnimState.PLAYING;
                    current = first;
                }
                else
                {
                    state = AnimState.FINISHED;
                }
            }
            else
            {
                state = AnimState.REVERSING;
            }
        }
    }

    /**
     * Update in reverse mode routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateReversing(double extrp)
    {
        current -= speed * extrp;

        // First frame reached, done
        if (current <= first)
        {
            current = first;

            if (repeat)
            {
                state = AnimState.PLAYING;
                current += 1.0;
            }
            else
            {
                state = AnimState.FINISHED;
            }
        }
    }

    /*
     * Animator
     */

    @Override
    public void play(Animation anim) throws LionEngineException
    {
        Check.notNull(anim);

        final int firstFrame = anim.getFirst();
        final int lastFrame = anim.getLast();
        final double animSpeed = anim.getSpeed();
        final boolean animReverse = anim.getReverse();
        final boolean animRepeat = anim.getRepeat();

        first = firstFrame;
        last = lastFrame;
        speed = animSpeed;
        reverse = animReverse;
        repeat = animRepeat;
        current = first;
        state = AnimState.PLAYING;
    }

    @Override
    public void stopAnimation()
    {
        state = AnimState.STOPPED;
    }

    @Override
    public void updateAnimation(double extrp)
    {
        if (state == AnimState.PLAYING)
        {
            updatePlaying(extrp);
        }
        if (state == AnimState.REVERSING)
        {
            updateReversing(extrp);
        }
    }

    @Override
    public void setAnimSpeed(double speed)
    {
        Check.superiorOrEqual(speed, 0.0);

        this.speed = speed;
    }

    @Override
    public void setFrame(int frame)
    {
        Check.superiorOrEqual(frame, Animation.MINIMUM_FRAME);

        current = frame;
    }

    @Override
    public AnimState getAnimState()
    {
        return state;
    }

    @Override
    public int getFrame()
    {
        return (int) Math.floor(current);
    }

    @Override
    public int getFrameAnim()
    {
        return getFrame() - first + 1;
    }
}
