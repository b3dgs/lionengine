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

/**
 * List of animation states.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Animator animator = Anim.createAnimator();
 * final Animation animation = Anim.createAnimation(1, 2, 1.0, false, false);
 * animator.getAnimState(); // returns STOPPED
 * animator.play(animation);
 * animator.updateAnimation(extrp);
 * animator.getAnimState(); // returns PLAYING
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Animation
 * @see Animator
 */
public enum AnimState
{
    /** Animation is stopped (default state). */
    STOPPED,
    /** Animation is currently animating. */
    PLAYING,
    /** Animation is currently playing in reverse. */
    REVERSING,
    /** Animation is finished (cannot exist in loop case). */
    FINISHED;
}
