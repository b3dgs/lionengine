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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.State;
import com.b3dgs.lionengine.game.StateFactory;

/**
 * Turn state implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class StateTurn
        extends State
{
    /** Animator reference. */
    private final Animator animator;
    /** Animation reference. */
    private final Animation animation;
    /** Movement force. */
    private final Force movement;
    /** Movement side. */
    private double side;

    /**
     * Create the walk state.
     * 
     * @param mario The mario reference.
     * @param animation The associated animation.
     */
    public StateTurn(Mario mario, Animation animation)
    {
        this.animation = animation;
        animator = mario.getSurface();
        movement = mario.getMovement();
    }

    @Override
    public void enter()
    {
        animator.play(animation);
        movement.setVelocity(0.28);
        movement.setSensibility(0.005);
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(side * 2, 0);
    }

    @Override
    public void clear()
    {
        side = 0;
    }

    @Override
    protected State handleInput(StateFactory factory, InputDeviceDirectional input)
    {
        if (input.getVerticalDirection() > 0)
        {
            return factory.getState(MarioState.JUMP);
        }
        side = input.getHorizontalDirection();
        if ((input.getHorizontalDirection() < 0 && movement.getDirectionHorizontal() < 0 || input
                .getHorizontalDirection() > 0 && movement.getDirectionHorizontal() > 0)
                && input.getVerticalDirection() == 0)
        {
            return factory.getState(MarioState.WALK);
        }
        return null;
    }
}
