package com.b3dgs.lionengine.anim;

/**
 * Anim factory. Can create the following elements:
 * <ul>
 * <li>{@link Animation}</li>
 * <li>{@link Animator}</li>
 * </ul>
 */
public final class Anim
{
    /**
     * Create an animation, which can be played by an {@link Animator}.
     * 
     * @param firstFrame The first frame index to play (>= {@link Animation#MINIMUM_FRAME}).
     * @param lastFrame The last frame index to play (>= firstFrame).
     * @param speed The animation playing speed (>= 0.0).
     * @param reverse <code>true</code> to reverse animation play (play it from first to last, and last to first).
     * @param repeat The repeat state (<code>true</code> will play in loop, <code>false</code> will play once only).
     * @return The created animation.
     */
    public static Animation createAnimation(int firstFrame, int lastFrame, double speed, boolean reverse, boolean repeat)
    {
        return new AnimationImpl(firstFrame, lastFrame, speed, reverse, repeat);
    }

    /**
     * Create an animator, which we will be able to play {@link Animation}.
     * 
     * @return The created animator.
     */
    public static Animator createAnimator()
    {
        return new AnimatorImpl();
    }

    /**
     * Private constructor.
     */
    private Anim()
    {
        throw new RuntimeException();
    }
}
