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
package com.b3dgs.lionengine.example.game.gameplay.state;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.trait.Mirrorable;
import com.b3dgs.lionengine.game.trait.Movable;

/**
 * Walk state implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <E> The entity type used.
 */
public class StateWalk<E extends Movable & Mirrorable & Animator>
        extends EntityState<E>
{
    /** Animation reference. */
    private final Animation animation;
    /** Movement side. */
    private int side;

    /**
     * Create the walk state.
     * 
     * @param animation The associated animation.
     */
    public StateWalk(Animation animation)
    {
        this.animation = animation;
    }

    /*
     * EntityState
     */

    @Override
    public void clear()
    {
        side = 0;
    }

    @Override
    public void updateState(E entity)
    {
        entity.setMoveToReach(side * entity.getMoveSpeedMax(), 0);
        entity.setAnimSpeed(Math.abs(entity.getDirectionHorizontal()) / 12.0);
        if (side < 0)
        {
            if (entity.getDirectionHorizontal() < 0)
            {
                entity.mirror(Mirror.HORIZONTAL);
            }
        }
        else if (side > 0)
        {
            if (entity.getDirectionHorizontal() > 0)
            {
                entity.mirror(Mirror.NONE);
            }
        }
    }

    @Override
    public void enter(E entity)
    {
        entity.play(animation);
        entity.setMoveVelocity(0.5);
        entity.setMoveSensibility(0.1);
    }

    @Override
    protected EntityState<E> handleInput(E entity, EntityStateFactory<E> factory, InputDeviceDirectional input)
    {
        if (input.getVerticalDirection() > 0)
        {
            return factory.getState(EntityStateType.JUMP);
        }
        side = input.getHorizontalDirection();
        if (side == 0 && input.getVerticalDirection() == 0)
        {
            return factory.getState(EntityStateType.IDLE);
        }
        else if (side < 0 && entity.getDirectionHorizontal() > 0 || side > 0 && entity.getDirectionHorizontal() < 0)
        {
            return factory.getState(EntityStateType.TURN);
        }
        return null;
    }
}
