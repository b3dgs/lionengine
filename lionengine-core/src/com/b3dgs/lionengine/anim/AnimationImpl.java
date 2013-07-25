package com.b3dgs.lionengine.anim;

import com.b3dgs.lionengine.Check;

/**
 * Animation implementation.
 */
final class AnimationImpl
        implements Animation
{
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
     * Create an animation, which can be played by an {@link Animator}.
     * 
     * @param firstFrame The first frame index to play (>= {@link Animation#MINIMUM_FRAME}).
     * @param lastFrame The last frame index to play (>= firstFrame).
     * @param speed The animation playing speed (>= 0.0).
     * @param reverse <code>true</code> to reverse animation play (play it from first to last, and last to first).
     * @param repeat The repeat state (<code>true</code> will play in loop, <code>false</code> will play once only).
     */
    AnimationImpl(int firstFrame, int lastFrame, double speed, boolean reverse, boolean repeat)
    {
        Check.argument(firstFrame >= Animation.MINIMUM_FRAME, "Animation first frame is lower than the minimum frame !");
        Check.argument(lastFrame >= firstFrame, "Animation last frame is lower than the first frame !");
        Check.argument(speed >= 0.0, "Animation speed must not be negative !");

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
        firstFrame = Math.max(Animation.MINIMUM_FRAME, Math.min(lastFrame, first));
    }

    @Override
    public void setLast(int last)
    {
        lastFrame = Math.max(firstFrame, last);
    }

    @Override
    public void setSpeed(double speed)
    {
        this.speed = Math.max(0.0, speed);
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
