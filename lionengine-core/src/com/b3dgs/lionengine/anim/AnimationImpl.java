package com.b3dgs.lionengine.anim;

import com.b3dgs.lionengine.Check;

/**
 * Animation implementation.
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
    private int firstFrame;
    /** Last animation frame. */
    private int lastFrame;
    /** Animation speed. */
    private double speed;
    /** Reverse flag. */
    private boolean reverse;
    /** Repeat flag. */
    private boolean repeat;

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
    public void setFirst(int first)
    {
        Check.argument(firstFrame >= Animation.MINIMUM_FRAME, AnimationImpl.ERROR_FIRST_FRAME);
        firstFrame = first;
    }

    @Override
    public void setLast(int last)
    {
        Check.argument(lastFrame >= firstFrame, AnimationImpl.ERROR_LAST_FRAME);
        lastFrame = last;
    }

    @Override
    public void setSpeed(double speed)
    {
        Check.argument(speed >= 0.0, AnimationImpl.ERROR_SPEED);
        this.speed = speed;
    }

    @Override
    public void setReverse(boolean reverse)
    {
        this.reverse = reverse;
    }

    @Override
    public void setRepeat(boolean repeat)
    {
        this.repeat = repeat;
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
