package com.b3dgs.lionengine.anim;

import com.b3dgs.lionengine.Check;

/**
 * Animator implementation.
 */
final class AnimatorImpl
        implements Animator
{
    /** Frame. */
    private static final double FRAME = 1.0;
    /** Half frame. */
    private static final double HALF_FRAME = 0.5;
    /** Current playing frame. */
    private double current;
    /** Animation speed. */
    private double speed;
    /** First frame. */
    private int first;
    /** Last frame. */
    private int last;
    /** Animation state. */
    private AnimState state;
    /** Reverse flag. */
    private boolean reverse;
    /** Repeat flag. */
    private boolean repeat;

    /**
     * Create an animator.
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
        if (current >= last + AnimatorImpl.FRAME)
        {
            // If not reversed, done, else, reverse
            current = last + AnimatorImpl.HALF_FRAME;
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
    public void play(Animation anim)
    {
        Check.notNull(anim, "Animation does not exist !");
        play(anim.getFirst(), anim.getLast(), anim.getSpeed(), anim.getReverse(), anim.getRepeat());
    }

    @Override
    public void play(int first, int last, double speed, boolean reverse, boolean repeat)
    {
        Check.argument(first >= Animation.MINIMUM_FRAME, "First frame must be >= Animation.MINIMUM_FRAME !");
        Check.argument(last >= first, "Last frame must be >= first !");
        Check.argument(speed >= 0.0, "Speed must be >= 0.0 !");

        this.first = first;
        this.last = last;
        this.speed = speed;
        this.reverse = reverse;
        this.repeat = repeat;
        current = this.first;
        state = AnimState.PLAYING;
    }

    @Override
    public void setAnimSpeed(double speed)
    {
        Check.argument(speed >= 0.0, "Speed must be >= 0.0 !");
        this.speed = speed;
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
    public AnimState getAnimState()
    {
        return state;
    }

    @Override
    public void setFrame(int frame)
    {
        Check.argument(frame >= Animation.MINIMUM_FRAME, "Frame must be >= Animation.MINIMUM_FRAME !");
        current = frame;
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
