package com.b3dgs.lionengine.anim;

/**
 * List of animation states.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Animator animator = Anim.createAnimator();
 * animator.getAnimState(); // returns STOPPED
 * animator.play(1, 2, 1.0, false, false);
 * animator.updateAnimation(extrp);
 * animator.getAnimState(); // returns PLAYING
 * </pre>
 * 
 * @see Animator
 */
public enum AnimState
{
    /** Animation is stopped. */
    STOPPED,
    /** Animation is currently animating. */
    PLAYING,
    /** Animation is currently playing in reverse. */
    REVERSING,
    /** Animation is finished (cannot exist in loop case). */
    FINISHED;
}
