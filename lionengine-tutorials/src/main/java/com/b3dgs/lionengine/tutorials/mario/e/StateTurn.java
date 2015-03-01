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
package com.b3dgs.lionengine.tutorials.mario.e;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.State;
import com.b3dgs.lionengine.game.StateFactory;
import com.b3dgs.lionengine.game.trait.Mirrorable;

/**
 * Turn state implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class StateTurn
        implements State
{
    /** Mirrorable reference. */
    private final Mirrorable mirrorable;
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
     * @param entity The entity reference.
     * @param animation The associated animation.
     */
    public StateTurn(Entity entity, Animation animation)
    {
        this.animation = animation;
        mirrorable = entity.getTrait(Mirrorable.class);
        animator = entity.getSurface();
        movement = entity.getMovement();
    }

    @Override
    public State handleInput(StateFactory factory, InputDevice input)
    {
        if (input instanceof InputDeviceDirectional)
        {
            final InputDeviceDirectional device = (InputDeviceDirectional) input;
            if (device.getVerticalDirection() > 0)
            {
                return factory.getState(EntityState.JUMP);
            }
            side = device.getHorizontalDirection();
            if ((device.getHorizontalDirection() < 0 && movement.getDirectionHorizontal() < 0 || device
                    .getHorizontalDirection() > 0 && movement.getDirectionHorizontal() > 0)
                    && device.getVerticalDirection() == 0)
            {
                return factory.getState(EntityState.WALK);
            }
            else if (side == 0 && movement.getDirectionHorizontal() == 0.0)
            {
                mirrorable.mirror(mirrorable.getMirror() == Mirror.HORIZONTAL ? Mirror.NONE : Mirror.HORIZONTAL);
                return factory.getState(EntityState.IDLE);
            }
        }
        return null;
    }

    @Override
    public void enter()
    {
        animator.play(animation);
        movement.setVelocity(0.28);
        movement.setSensibility(0.005);
        side = 0;
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(side * 2, 0);
    }

    @Override
    public Enum<?> getState()
    {
        return EntityState.TURN;
    }
}
