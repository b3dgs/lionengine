/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.drawable.SpriteAnimated;

/**
 * Animator can play an {@link Animation}.
 * <p>
 * To play correctly an animation, it just needs the following steps:
 * <ul>
 * <li>Call only one time {@link #play(Animation)}</li>
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
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Anim
 * @see Animation
 * @see AnimState
 */
public interface Animator
{
    /**
     * Play the animation. Should be called only one time, as {@link #updateAnimation(double)} does the animation
     * update.
     * 
     * @param animation The animation to play (must not be <code>null</code>).
     * @throws LionEngineException If the animation is <code>null</code>.
     */
    void play(Animation animation) throws LionEngineException;

    /**
     * Stop the current animation (animation state set to {@link AnimState#STOPPED}).
     */
    void stopAnimation();

    /**
     * Animation update routine.
     * It will update the animation that have been defined with the last call of {@link #play(Animation)}.
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
     * @param speed The new animation speed (>= 0).
     * @throws LionEngineException If speed is negative.
     */
    void setAnimSpeed(double speed) throws LionEngineException;

    /**
     * Set a fixed frame (it will overwrite the current animation frame).
     * 
     * @param frame The frame to set (>= {@link Animation#MINIMUM_FRAME}, <= {@link SpriteAnimated#getFramesNumber()}).
     * @throws LionEngineException If frame is out of range.
     */
    void setFrame(int frame) throws LionEngineException;

    /**
     * Get current animation state.
     * 
     * @return animation The current animation state.
     */
    AnimState getAnimState();

    /**
     * Get the playing frame number.
     * 
     * @return The playing frame number.
     */
    int getFrame();

    /**
     * Get the current playing animation frame number (value between first and last of the current animation).
     * 
     * @return The current playing animation frame number.
     */
    int getFrameAnim();
}
