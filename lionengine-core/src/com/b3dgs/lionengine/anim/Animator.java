package com.b3dgs.lionengine.anim;

/**
 * Animator, which can play an {@link Animation} from animation container.
 * <p>
 * To play correctly an animation, it just needs the following steps:
 * <ul>
 * <li>Call only one time {@link #play(Animation)} or {@link #play(int, int, double, boolean, boolean)}</li>
 * <li>Call {@link #updateAnimation(double)} in your main loop</li>
 * </ul>
 * </p>
 */
public interface Animator
{
    /**
     * Play the animation, previously created. Should be called only one time, as {@link #updateAnimation(double)} do
     * the animation update.
     * 
     * @param animation The animation reference to play.
     */
    void play(Animation animation);

    /**
     * Play the animated sprite with a specific animation data. Should be called only one time, as
     * {@link #updateAnimation(double)} do the animation update.
     * 
     * @param first The first frame index to play (>= {@link Animation#MINIMUM_FRAME}).
     * @param last The last frame index to play (>= first).
     * @param speed The animation playing speed (>= 0.0).
     * @param reverse <code>true</code> to reverse animation play (play it from first to last, and last to first).
     * @param repeat The repeat state (<code>true</code> will play in loop, <code>false</code> will play once only).
     * @see Animation
     */
    void play(int first, int last, double speed, boolean reverse, boolean repeat);

    /**
     * Set the current animation speed. This function allows to change the current playing animation speed.
     * <p>
     * Example: it can be used to synchronize the player movement speed to the walking animation speed.
     * </p>
     * 
     * @param speed The new animation speed.
     */
    void setAnimSpeed(double speed);

    /**
     * Stop the current animation (animation state set to {@link AnimState#STOPPED}).
     */
    void stopAnimation();

    /**
     * Animation update routine. It will update the animation that have been define with the last call of
     * {@link #play(Animation)} or {@link #play(int, int, double, boolean, boolean)}.
     * 
     * @param extrp The extrapolation value.
     */
    void updateAnimation(double extrp);

    /**
     * Get current animation state.
     * 
     * @return animation The current animation state.
     */
    AnimState getAnimState();

    /**
     * Set a fixed frame (it will overwrite the current animation frame).
     * 
     * @param frame The frame to set.
     */
    void setFrame(int frame);

    /**
     * Get the current playing frame number. The returned value is the same as calling
     * <code>(int) {@link Math#floor(double)}</code> with {@link #getFrameReal()}.
     * 
     * @return The current playing frame number.
     */
    int getFrame();

    /**
     * Get the current playing frame with accurate precision. The integer part represents the current frame, while the
     * other part represents its progress.
     * <p>
     * Example: <code>1.75</code> means that the current frame is <code>1</code>, played at <code>75%</code>.
     * </p>
     * 
     * @return The real frame with double precision.
     */
    double getFrameReal();
}
