/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

/**
 * Animator can play an {@link Animation}.
 * <p>
 * To play correctly an animation, it needs the following steps:
 * </p>
 * <ul>
 * <li>Call only one time {@link #play(Animation)}</li>
 * <li>Call {@link #update(double)} in your main loop</li>
 * </ul>
 * 
 * @see Animation
 * @see AnimState
 */
public interface Animator extends Updatable
{
    /**
     * Play the animation. Should be called only one time to start, as {@link #update(double)} does the animation
     * update.
     * 
     * @param animation The animation to play (must not be <code>null</code>).
     * @throws LionEngineException If the animation is <code>null</code>.
     */
    void play(Animation animation);

    /**
     * Stop the current animation (animation state set to {@link AnimState#STOPPED}).
     */
    void stop();

    /**
     * Set the current animation speed. This function allows to change the current playing animation speed.
     * <p>
     * Can be used to synchronize the movement speed to the walking animation speed for example.
     * </p>
     * 
     * @param speed The new animation speed (superior or equal to {@link Animation#MINIMUM_SPEED}).
     * @throws LionEngineException If speed is negative.
     */
    void setAnimSpeed(double speed);

    /**
     * Set a fixed frame (it will overwrite the current animation frame).
     * 
     * @param frame The frame to set (superior or equal to {@link Animation#MINIMUM_FRAME} and inferior or equal to last
     *            frame).
     * @throws LionEngineException If frame is out of range.
     */
    void setFrame(int frame);

    /**
     * Get the current playing frame number.
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

    /**
     * Get current animation state.
     * 
     * @return animation The current animation state.
     */
    AnimState getAnimState();
}
