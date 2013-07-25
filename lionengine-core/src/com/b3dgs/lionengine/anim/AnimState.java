package com.b3dgs.lionengine.anim;

/**
 * List of animation states.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Animator animator = Anim.createAnimator();
 * final AnimState state = animator.getAnimState();
 * </pre>
 * 
 * @see Animator
 */
public enum AnimState
{
    /** Animation is currently animating. */
    PLAYING,
    /** Animation is currently playing in reverse. */
    REVERSING,
    /** Animation is finished (cannot exist in loop case). */
    FINISHED,
    /** Animation is stopped. */
    STOPPED;
}
