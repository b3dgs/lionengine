/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Animator, which can play an {@link Animation} from animation container.
 * <p>
 * To play correctly an animation, it just needs the following steps:
 * <ul>
 * <li>Call only one time {@link #play(Animation)} or {@link #play(int, int, double, boolean, boolean)}</li>
 * <li>Call {@link #updateAnimation(double)} in your main loop</li>
 * </ul>
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Animator animator = Anim.createAnimator();
 * final Animation animation = Anim.createAnimation(4, 6, 0.125, false, true);
 * animator.play(animation);
 * 
 * // ... (loop)
 * animator.updateAnimation(extrp);
 * // (loop) ...
 * </pre>
 */
public interface Animator
{
    /**
     * Play the animation. Should be called only one time, as {@link #updateAnimation(double)} do the animation update.
     * 
     * @param animation The animation to play.
     */
    void play(Animation animation);

    /**
     * Play the animated sprite with a specific animation data. Should be called only one time, as
     * {@link #updateAnimation(double)} do the animation update.
     * 
     * @param firstFrame The first frame (included) index to play (>= {@link Animation#MINIMUM_FRAME}).
     * @param lastFrame The last frame (included) index to play (>= firstFrame).
     * @param speed The animation playing speed (>= 0.0).
     * @param reverse <code>true</code> to reverse animation play (play it from first to last, and last to first).
     * @param repeat The repeat state (<code>true</code> will play in loop, <code>false</code> will play once only).
     * @see Animation
     */
    void play(int firstFrame, int lastFrame, double speed, boolean reverse, boolean repeat);

    /**
     * Stop the current animation (animation state set to {@link AnimState#STOPPED}).
     */
    void stopAnimation();

    /**
     * Animation update routine. It will update the animation that have been defined with the last call of
     * {@link #play(Animation)} or {@link #play(int, int, double, boolean, boolean)}.
     * 
     * @param extrp The extrapolation value.
     */
    void updateAnimation(double extrp);

    /**
     * Set the current animation speed. This function allows to change the current playing animation speed.
     * <p>
     * Can be used to synchronize the player movement speed to the walking animation speed.
     * </p>
     * 
     * @param speed The new animation speed.
     */
    void setAnimSpeed(double speed);

    /**
     * Set a fixed frame (it will overwrite the current animation frame).
     * 
     * @param frame The frame to set.
     */
    void setFrame(int frame);

    /**
     * Get current animation state.
     * 
     * @return animation The current animation state.
     */
    AnimState getAnimState();

    /**
     * Get the current playing frame number.
     * 
     * @return The current playing frame number.
     */
    int getFrame();

    /**
     * Get the current playing animation frame number (value between first and last of the current animation).
     * 
     * @return The current playing frame number.
     */
    int getFrameAnim();
}
