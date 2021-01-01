/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

/**
 * Animator implementation.
 */
public final class AnimatorModel implements Animator
{
    /** Frame. */
    private static final double FRAME = 1.0;

    /** Animation listener. */
    private final ListenableModel<AnimatorListener> listenable = new ListenableModel<>();
    /** First frame. */
    private int first = Animation.MINIMUM_FRAME;
    /** Last frame. */
    private int last = Animation.MINIMUM_FRAME;
    /** Animation speed. */
    private double speed;
    /** Reverse flag. */
    private boolean reverse;
    /** Repeat flag. */
    private boolean repeat;
    /** Current playing frame. */
    private double current = first;
    /** Animation state. */
    private AnimState state = AnimState.STOPPED;
    /** Current animation (<code>null</code> if none). */
    private Animation anim;

    /**
     * Create animator.
     */
    public AnimatorModel()
    {
        super();
    }

    /**
     * Update play mode routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePlaying(double extrp)
    {
        final int old = getFrame();

        current += speed * extrp;

        // Last frame reached
        if (Double.compare(current, last + FRAME) >= 0)
        {
            // If not reversed, done, else, reverse
            current = last;
            checkStatePlaying();

            for (int i = 0; i < listenable.size(); i++)
            {
                listenable.get(i).notifyAnimState(state);
            }
        }
        else
        {
            final int cur = getFrame();
            if (cur != old)
            {
                for (int i = 0; i < listenable.size(); i++)
                {
                    listenable.get(i).notifyAnimFrame(cur);
                }
            }
        }
    }

    /**
     * Check state in playing case.
     */
    private void checkStatePlaying()
    {
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

    /**
     * Update in reverse mode routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateReversing(double extrp)
    {
        final int old = getFrame();

        current -= speed * extrp;

        // First frame reached, done
        if (Double.compare(current, first) <= 0)
        {
            current = first;

            if (repeat)
            {
                state = AnimState.PLAYING;
                current += FRAME - speed;
            }
            else
            {
                state = AnimState.FINISHED;
            }
            for (int i = 0; i < listenable.size(); i++)
            {
                listenable.get(i).notifyAnimState(state);
            }
        }
        else
        {
            final int cur = getFrame();
            if (cur != old)
            {
                for (int i = 0; i < listenable.size(); i++)
                {
                    listenable.get(i).notifyAnimFrame(cur);
                }
            }
        }
    }

    /*
     * Animator
     */

    @Override
    public void addListener(AnimatorListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(AnimatorListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public void play(Animation anim)
    {
        Check.notNull(anim);

        final int firstFrame = anim.getFirst();
        final int lastFrame = anim.getLast();
        final double animSpeed = anim.getSpeed();
        final boolean animReverse = anim.hasReverse();
        final boolean animRepeat = anim.hasRepeat();

        this.anim = anim;
        first = firstFrame;
        last = lastFrame;
        speed = animSpeed;
        reverse = animReverse;
        repeat = animRepeat;
        current = first;
        state = AnimState.PLAYING;

        for (int i = 0; i < listenable.size(); i++)
        {
            final AnimatorListener listener = listenable.get(i);
            listener.notifyAnimPlayed(anim);
            listener.notifyAnimState(state);
            listener.notifyAnimFrame(first);
        }
    }

    @Override
    public void stop()
    {
        state = AnimState.STOPPED;

        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyAnimState(state);
        }
    }

    @Override
    public void reset()
    {
        anim = null;
        first = Animation.MINIMUM_FRAME;
        current = Animation.MINIMUM_FRAME;
        last = Animation.MINIMUM_FRAME;
        speed = 0.0;
        reverse = false;
        repeat = false;
        state = AnimState.STOPPED;
    }

    @Override
    public void update(double extrp)
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

        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyAnimFrame(frame);
        }
    }

    @Override
    public Animation getAnim()
    {
        return anim;
    }

    @Override
    public int getFrames()
    {
        return last - first + 1;
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

    @Override
    public AnimState getAnimState()
    {
        return state;
    }
}
