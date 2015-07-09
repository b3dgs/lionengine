/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Nameable;

/**
 * Animation data container for animation routine.
 * <p>
 * It contains the <code>first</code> and <code>last</code> animation frame number, the animation <code>speed</code>, a
 * <code>reverse</code> flag (for reversed animation), and a <code>repeat</code> flag (for looped animation).
 * </p>
 * <p>
 * <ul>
 * <li><code>first</code>: first frame of the animation that will be played (included).</li>
 * <li><code>last</code>: last frame of the animation that will be played (included).</li>
 * <li><code>speed</code>: animation speed.</li>
 * <li><code>reverse</code>: reverse flag (1 -> 2 -> 3 -> 2 -> 1).</li>
 * <li><code>repeat</code>: repeat flag (1 -> 2 -> 3 -> 1 -> 2 -> 3...).</li>
 * </ul>
 * </p>
 * <p>
 * Note: <code>reverse</code> and <code>repeat</code> can also be combined to play in loop an animation in reverse:
 * 1 -> 2 -> 3 -> 2 -> 1 -> 2 -> 3....
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Animation animation = Anim.createAnimation(4, 6, 0.125, false, true);
 * print(animation.getFirst()); // 4
 * print(animation.getLast()); // 6
 * print(animation.getSpeed()); // 0.125
 * print(animation.getReverse()); // false
 * print(animation.getRepeat()); // true
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Anim
 * @see Animator
 * @see AnimState
 */
public interface Animation extends Nameable
{
    /** Animation default name. */
    String DEFAULT_NAME = "default_anim";
    /** The minimum frame number. */
    int MINIMUM_FRAME = 1;

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
