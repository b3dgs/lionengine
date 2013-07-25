package com.b3dgs.lionengine.anim;

/**
 * List of animation states.
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
