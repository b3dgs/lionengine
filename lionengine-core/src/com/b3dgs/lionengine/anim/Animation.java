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
 * Animation data container for animation routine.
 * <p>
 * It contains the <code>first</code> and <code>last</code> animation frame number, the animation <code>speed</code>, a
 * <code>reverse</code> flag (for reversed animation), and a <code>repeat</code> flag (for looped animation).
 * </p>
 * <p>
 * <ul>
 * <li><code>first</code>: first frame of the animation that will be played.</li>
 * <li><code>last</code>: last frame of the animation that will be played.</li>
 * <li><code>speed</code>: animation speed.</li>
 * <li><code>reverse</code>: reverse flag.</li>
 * <li><code>repeat</code>: repeat flag.</li>
 * </ul>
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Animation animation = Anim.createAnimation(4, 6, 0.125, false, true);
 * </pre>
 */
public interface Animation
{
    /** The minimum frame number. */
    int MINIMUM_FRAME = 1;

    /**
     * Set first frame. This represents the first frame of the animation that will be played. The minimum value must be
     * {@value #MINIMUM_FRAME}.
     * 
     * @param first The first frame (>= {@value #MINIMUM_FRAME}).
     */
    void setFirst(int first);

    /**
     * Set last frame. This represents the last frame of the animation that will be played. The maximum value should not
     * exceed the total number of frames and must be >= first.
     * 
     * @param last The last frame (>= start).
     */
    void setLast(int last);

    /**
     * Set speed. The standard value is 1.0. A value of 2.0 will play it twice faster. A value of 0.5 will pay it twice
     * slower. The speed must be positive.
     * 
     * @param speed The speed (>= 0.0).
     */
    void setSpeed(double speed);

    /**
     * Set reverse state. If the value is set to <code>true</code>, it will make the animation be played in reverse when
     * the last frame is reached. In this case, the animation will be finished when the first frame is reached. A
     * <code>false</code> flag will play normally the animation, from first frame to last frame.
     * 
     * @param reverse The reverse state.
     */
    void setReverse(boolean reverse);

    /**
     * Set repeat state (loop). If the value is set to <code>true</code>, it will make the animation be played in loop.
     * In other word, when the last frame is reached, it will automatically play it again, starting at the first frame.
     * <p>
     * If it is combined with a reverse flag set to <code>true</code>, it will play from first frame to last frame, last
     * frame to first frame, and so on.
     * </p>
     * 
     * @param repeat The repeat state (loop).
     */
    void setRepeat(boolean repeat);

    /**
     * Get the first frame of the animation.
     * 
     * @return The first frame.
     */
    int getFirst();

    /**
     * Get the last frame if the animation.
     * 
     * @return The last frame.
     */
    int getLast();

    /**
     * Get the animation speed.
     * 
     * @return The animation speed.
     */
    double getSpeed();

    /**
     * Get the reverse state.
     * 
     * @return The reverse state.
     */
    boolean getReverse();

    /**
     * Get the repeat state.
     * 
     * @return The repeat state.
     */
    boolean getRepeat();
}
